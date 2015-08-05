/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.longlinkislong.gloop;

import java.util.concurrent.Callable;
import java.util.function.Consumer;
import java.util.function.Supplier;

/**
 * A request for information from the OpenAL device.
 *
 * @author zmichaels
 * @param <ReturnType> the return type.
 * @since 15.08.05
 */
public abstract class ALQuery<ReturnType> implements Callable<ReturnType> {

    @Override
    public abstract ReturnType call() throws Exception;

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
            Thread.currentThread().interrupt();
            return this.handleInterruption();
        } catch (Exception ex) {
            throw new ALException("Unable to call ALQuery!", ex);
        }
    }

    public final ReturnType alCall() {
        return this.alCall(null);
    }

    public ALTask andThen(final Consumer<ReturnType> other) {
        return new ALTask() {
            @Override
            public void run() {
                try {
                    other.accept(ALQuery.this.call());
                } catch (Exception ex) {
                    throw new ALException("Unable to call ALQuery!", ex);
                }
            }
        };
    }

    public static <ReturnType> ALQuery<ReturnType> create(final Runnable task, final Supplier<ReturnType> result) {
        return new ALQuery<ReturnType>() {
            @Override
            public ReturnType call() throws Exception {
                task.run();
                return result.get();
            }
        };
    }

    public static <ReturnType> ALQuery<ReturnType> create(final Callable<ReturnType> query) {
        return new ALQuery<ReturnType>() {
            @Override
            public ReturnType call() throws Exception {
                return query.call();
            }
        };
    }

    protected ReturnType handleInterruption() {
        return null;
    }
}
