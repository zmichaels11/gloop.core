/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.longlinkislong.gloop;

import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

/**
 *
 * @author zmichaels
 * @param <ReturnType> the return type.
 * @since 15.08.06
 */
public class CLFuture<ReturnType> implements Future<ReturnType> {
    private final Future<ReturnType> internal;
    private final ReturnType result;
    
    protected CLFuture(final Future<ReturnType> internal) {
        this.internal = internal;
        this.result = null;
    }
    
    protected CLFuture(final ReturnType result) {
        this.internal = null;
        this.result = result;
    }
    
    @Override
    public boolean cancel(boolean mayInterruptIfRunning) {
        if(this.result == null) {
            return this.internal.cancel(mayInterruptIfRunning);
        } else {
            return false;
        }
    }

    @Override
    public boolean isCancelled() {
        if(this.result == null) {
            return this.internal.isCancelled();
        } else {
            return false;
        }
    }

    @Override
    public boolean isDone() {
        if(this.result == null) {
            return this.internal.isDone();
        } else {
            return true;
        }
    }

    @Override
    public ReturnType get() throws InterruptedException, ExecutionException {
        if(this.result == null) {
            return this.internal.get();
        } else {
            return this.result;
        }
    }

    @Override
    public ReturnType get(long timeout, TimeUnit unit) throws InterruptedException, ExecutionException, TimeoutException {
        if(this.result == null) {
            return this.internal.get(timeout, unit);
        } else {
            return this.result;
        }
    }
    
    @Override
    public final String toString() {
        try {
            return "ALFuture: [" + this.get() + "]";
        } catch (ExecutionException ex) {
            return "ALFuture: [NULL]: " + ex;
        } catch (InterruptedException ex) {
            Thread.currentThread().interrupt();
            return "ALFuture: [NULL]: " + ex;
        }
    }
}
