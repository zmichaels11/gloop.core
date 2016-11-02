/* 
 * Copyright (c) 2015, longlinkislong.com
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 *
 * * Redistributions of source code must retain the above copyright notice, this
 *   list of conditions and the following disclaimer.
 * * Redistributions in binary form must reproduce the above copyright notice,
 *   this list of conditions and the following disclaimer in the documentation
 *   and/or other materials provided with the distribution.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS"
 * AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE
 * IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE
 * ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT HOLDER OR CONTRIBUTORS BE
 * LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR
 * CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF
 * SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS
 * INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN
 * CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE)
 * ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE
 * POSSIBILITY OF SUCH DAMAGE.
 */
package com.longlinkislong.gloop;

import java.util.concurrent.Callable;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Supplier;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.Marker;
import org.slf4j.MarkerFactory;

/**
 * A GLQuery is a GLTask that can return a value at end of execution.
 *
 * @author zmichaels
 * @param <ReturnType> the return type of the query
 * @since 15.05.27
 */
public abstract class GLQuery<ReturnType> implements Callable<ReturnType> {
    private static final Marker GLOOP_MARKER = MarkerFactory.getMarker("GLOOP");
    private static final Logger LOGGER = LoggerFactory.getLogger("GLQuery");
    /**
     * Performs the GLQuery on the specified GLThread and retrieves the result.
     * This method may cause a thread sync.
     *
     * @param thread the GLThread to run the GLQuery on.
     * @return the result of the GLQuery.
     * @throws GLException if any other form of exception occurred.
     * @since 15.05.27
     */
    public final ReturnType glCall(final GLThread thread) throws GLException {
        if (thread == null) {
            return this.glCall(GLThread.getDefaultInstance());
        }

        try {
            if (thread.isCurrent()) {
                return this.call();
            } else {
                return thread.submitGLQuery(this).get();
            }
        } catch (InterruptedException ex) {
            LOGGER.error(GLOOP_MARKER, "GLQuery interrupted! Handling interruption and restoring interrupt flag.");
            LOGGER.error(GLOOP_MARKER, ex.getMessage(), ex);
            
            Thread.currentThread().interrupt();
            return this.handleInterruption();
        } catch (Exception ex) {
            throw new GLException("Unable to call GLQuery!", ex);
        }
    }

    /**
     * Performs a GLCall on the default GLThread and retrieves the result. This
     * method may cause a thread sync.
     *
     * @return the result of the GLCall.
     * @throws GLException if any other form of exception occurred.
     * @since 15.05.27
     */
    public final ReturnType glCall() throws GLException {
        final GLThread thread = GLThread.getDefaultInstance();

        try {
            if (thread.isCurrent()) {
                return this.call();
            } else {
                return thread.submitGLQuery(this).get();
            }
        } catch (InterruptedException ex) {
            LOGGER.error(GLOOP_MARKER, "GLQuery interrupted! Handling interruption and restoring interrupt flag.");
            LOGGER.error(GLOOP_MARKER, ex.getMessage(), ex);
            
            Thread.currentThread().interrupt();
            return this.handleInterruption();
        } catch (Exception ex) {
            throw new GLException("Unable to call GLQuery!", ex);
        }
    }

    /**
     * Optional implementation for handling thread interruption.
     *
     * @return some safe value for when the query cannot fully process.
     * @since 15.07.20
     */
    protected ReturnType handleInterruption() {
        return null;
    }

    /**
     * Creates a GLTask that chain runs a task using the output of this query as
     * input.
     *
     * @param other the task to run
     * @return the GLTask
     * @throws GLException if any other exception occurred.
     * @since 15.06.13
     */
    public GLTask andThen(final Consumer<ReturnType> other) throws GLException {
        return new GLTask() {
            @Override
            public void run() {
                try {
                    other.accept(GLQuery.this.call());
                } catch (Exception ex) {
                    throw new GLException("Unable to call GLQuery!", ex);
                }
            }
        };
    }

    /**
     * Creates a GLTask that chain runs multiple tasks using the output of this
     * query as input.
     *
     * @param others series of other tasks to run.
     * @return the GLTask
     * @since 15.06.13
     */
    @SuppressWarnings("unchecked")
    public GLTask applyForEach(final Consumer<ReturnType>... others) {
        return new GLTask() {
            @Override
            public void run() {
                try {
                    final ReturnType value = GLQuery.this.call();

                    for (Consumer<ReturnType> other : others) {
                        other.accept(value);
                    }
                } catch (Exception ex) {
                    throw new GLException("Error processing GLQuery!", ex);
                }
            }
        };
    }

    /**
     * Converts a java.util.function into a GLQuery.
     *
     * @param <InputType> the type of input.
     * @param <ReturnType> the type of output.
     * @param function the function to apply.
     * @param input the input to the function.
     * @return the GLQuery that performs the operation.
     * @since 15.07.06
     */
    public static <InputType, ReturnType> GLQuery<ReturnType> create(final Function<InputType, ReturnType> function, InputType input) {
        return new GLQuery<ReturnType>() {
            @Override
            public ReturnType call() throws Exception {
                return function.apply(input);
            }
        };
    }

    /**
     * Creates a new GLQuery by wrapping a GLTask and a supplier for the result.
     *
     * @param <ReturnType> the return type
     * @param task the task to run
     * @param result the supplier to get the result after running the task.
     * @return the result
     * @since 15.05.27
     */
    public static <ReturnType> GLQuery<ReturnType> create(
            final Runnable task, final Supplier<ReturnType> result) {

        return new GLQuery<ReturnType>() {
            @Override
            public ReturnType call() throws Exception {
                task.run();
                return result.get();
            }
        };
    }

    /**
     * Creates a new GLQuery by wrapping a Callable.
     *
     * @param <ReturnType> the return type.
     * @param query the query to execute.
     * @return the result of the query.
     * @since 15.05.27
     */
    public static <ReturnType> GLQuery<ReturnType> create(
            final Callable<ReturnType> query) {

        return new GLQuery<ReturnType>() {

            @Override
            public ReturnType call() throws Exception {
                return query.call();
            }

        };
    }
}
