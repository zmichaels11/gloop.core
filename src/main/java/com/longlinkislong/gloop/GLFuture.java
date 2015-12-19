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

import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

/**
 * A future object that is used with GLQuery objects.
 *
 * @author zmichaels
 * @param <ReturnType> the return type that is set after the GLQuery executes.
 * @since 15.05.27
 */
public class GLFuture<ReturnType> implements Future<ReturnType> {

    private final Future<ReturnType> internal;
    private final ReturnType result;

    /**
     * Constructs a new GLFuture object by wrapping a Future object.
     *
     * @param internal the Future object to wrap.
     * @since 15.12.18
     */
    protected GLFuture(final Future<ReturnType> internal) {
        this.internal = internal;
        this.result = null;
    }

    /**
     * Constructs a new GLFuture object that returns the specified value.
     *
     * @param result the return result.
     * @since 15.12.18
     */
    protected GLFuture(final ReturnType result) {
        this.internal = null;
        this.result = result;
    }

    @Override
    public boolean cancel(boolean mayInterruptIfRunning) {
        if (this.result == null) {
            return this.internal.cancel(mayInterruptIfRunning);
        } else {
            return false;
        }
    }

    @Override
    public boolean isCancelled() {
        if (this.result == null) {
            return this.internal.isCancelled();
        } else {
            return false;
        }
    }

    @Override
    public boolean isDone() {
        if (this.result == null) {
            return this.internal.isDone();
        } else {
            return true;
        }
    }

    @Override
    public ReturnType get() throws InterruptedException, ExecutionException {
        if (this.result == null) {
            return this.internal.get();
        } else {
            return this.result;
        }
    }

    @Override
    public ReturnType get(long timeout, TimeUnit unit) 
            throws InterruptedException, ExecutionException, TimeoutException {
        
        if (this.result == null) {
            return this.internal.get(timeout, unit);
        } else {
            return this.result;
        }
    }

    @Override
    public final String toString() {
        try {
            return "GLFuture: [" + this.get() + "]";
        } catch (ExecutionException ex) {
            return "GLFuture: [NULL]: " + ex;
        } catch (InterruptedException ex) {
            Thread.currentThread().interrupt();
            return "ALFuture: [NULL]: " + ex;
        }
    }

}
