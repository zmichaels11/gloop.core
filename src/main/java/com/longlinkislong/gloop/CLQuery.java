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
 *
 * @author zmiichaels
 * @param <ReturnType> the return type.
 * @since 15.08.06
 */
public abstract class CLQuery<ReturnType> implements Callable<ReturnType> {

    @Override
    public abstract ReturnType call() throws Exception;

    public final ReturnType clCall(final CLThread thread) {
        if (thread == null) {
            return this.clCall(CLThread.getDefaultInstance());
        }

        try {
            if (thread.isCurrent()) {
                return this.call();
            } else {
                return thread.submitCLQuery(this).get();
            }
        } catch (InterruptedException ex) {
            Thread.currentThread().interrupt();
            return this.handleInterruption();
        } catch (Exception ex) {
            throw new CLException("Unable to call CLQuery!", ex);
        }
    }

    public final ReturnType clCall() {
        return this.clCall(null);
    }

    public CLTask andThen(final Consumer<ReturnType> other) {
        return new CLTask() {
            @Override
            public void run() {
                try {
                    other.accept(CLQuery.this.call());
                } catch (Exception ex) {
                    throw new CLException("Unable to call CLQuery!", ex);
                }
            }
        };
    }

    public static <ReturnType> CLQuery<ReturnType> create(final Runnable task, final Supplier<ReturnType> result) {
        return new CLQuery<ReturnType>() {
            @Override
            public ReturnType call() throws Exception {
                task.run();
                return result.get();
            }
        };
    }

    public static <ReturnType> CLQuery<ReturnType> create(final Callable<ReturnType> query) {
        return new CLQuery<ReturnType>() {
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
