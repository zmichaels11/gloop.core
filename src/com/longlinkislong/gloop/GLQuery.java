/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.longlinkislong.gloop;

import java.util.concurrent.Callable;
import java.util.function.Supplier;

/**
 * A GLQuery is a GLTask that can return a value at end of execution.
 *
 * @author zmichaels
 * @param <ReturnType> the return type of the query
 * @since 15.05.27
 */
public abstract class GLQuery<ReturnType> implements Callable<ReturnType> {

    /**
     * Performs the GLQuery on the specified GLThread and retrieves the result.
     * This method may cause a thread sync.
     *
     * @param thread the GLThread to run the GLQuery on.
     * @return the result of the GLQuery.
     * @since 15.05.27
     */
    public final ReturnType glCall(final GLThread thread) {
        if (thread == null) {
            return this.glCall(GLThread.getDefaultInstance());
        }

        try {
            if (thread.isCurrent()) {
                return this.call();
            } else {
                return thread.submitGLQuery(this).get();
            }
        } catch (final Exception ex) {
            throw new GLException("Unable to call GLQuery!", ex);
        }
    }

    /**
     * Performs a GLCall on the default GLThread and retrieves the result. This
     * method may cause a thread sync.
     *
     * @return the result of the GLCall.
     * @since 15.05.27
     */
    public final ReturnType glCall() {
        final GLThread thread = GLThread.getDefaultInstance();

        try {
            if (thread.isCurrent()) {
                return this.call();
            } else {
                return thread.submitGLQuery(this).get();
            }
        } catch (final Exception ex) {
            throw new GLException("Unable to call GLQuery!", ex);
        }
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
