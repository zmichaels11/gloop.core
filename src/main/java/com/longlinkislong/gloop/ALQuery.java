/* 
 * Copyright (c) 2016, longlinkislong.com
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
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * A task with return value that gets ran on the OpenAL thread.
 *
 * @author zmichaels
 * @param <ReturnType> the expected type for the return value.
 * @since 16.03.21
 */
public abstract class ALQuery<ReturnType> implements Callable<ReturnType> {

    private static final Logger LOGGER = LoggerFactory.getLogger("ALQuery");

    /**
     * Executes the ALQuery on the specified thread. This will cause thread
     * synchronization
     *
     * @param thread the thread to run the ALQuery on. Uses the default OpenAL
     * thread if null is supplied.
     * @return the result of the ALQuery..
     * @since 16.03.21
     */
    public final ReturnType alCall(final ALThread thread) {
        if (thread == null) {
            return this.alCall(ALThread.getDefaultInstance());
        }

        try {
            if (thread.isCurrent()) {
                return this.call();
            } else {
                return thread.submitALQuery(this).get();
            }
        } catch (InterruptedException ex) {
            LOGGER.error("ALQuery interrupted! Handling interruption and restoring interruption flag!");
            LOGGER.debug(ex.getMessage(), ex);

            Thread.currentThread().interrupt();
            return this.handleInterruption();
        } catch (Exception ex) {
            throw new ALException("Unable to call ALQuery!", ex);
        }
    }

    /**
     * Executes the ALQuery on the default OpenAL thread. This will cause thread
     * synchronization.
     *
     * @return the return value.
     * @since 16.03.21
     */
    public final ReturnType alCall() {
        final ALThread thread = ALThread.getDefaultInstance();

        try {
            if (thread.isCurrent()) {
                return this.call();
            } else {
                return thread.submitALQuery(this).get();
            }
        } catch (InterruptedException ex) {
            LOGGER.error("ALQuery interrupted! Handling interruption and restoring interruption flag!");
            LOGGER.debug(ex.getMessage(), ex);
            return this.handleInterruption();
        } catch (Exception ex) {
            throw new ALException("Unable to call ALQuery!", ex);
        }
    }

    /**
     * Optional method for implementing handling of interrupted exception.
     * @return the value to return when the thread is interrupted.
     * @since 16.03.21
     */
    protected ReturnType handleInterruption() {
        return null;
    }

    /**
     * Constructs a new ALQuery.
     * @param <ReturnType> the expected return type.
     * @param query the method to execute.
     * @return the ALQuery object.
     * @since 16.03.21
     */
    public static <ReturnType> ALQuery<ReturnType> create(final Callable<ReturnType> query) {
        return new ALQuery<ReturnType>() {
            @Override
            public ReturnType call() throws Exception {
                return query.call();
            }
        };
    }
}
