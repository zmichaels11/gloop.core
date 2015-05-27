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
 * A future object that is used with GLQuery objects.
 * @author zmichaels
 * @param <ReturnType> the return type that is set after the GLQuery executes.
 * @since 15.05.27
 */
public class GLFuture<ReturnType> implements Future<ReturnType> {
    private final Future<ReturnType> internal;
    private final ReturnType result;
    
    protected GLFuture(final Future<ReturnType> internal) {
        this.internal = internal;   
        this.result = null;
    }
    
    protected GLFuture(final ReturnType result) {
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
        try{
            return "GLFuture: [" + this.get() + "]";
        } catch (InterruptedException | ExecutionException ex) {
            return "GLFuture: [NULL]: " + ex;
        }
    }
    
}
