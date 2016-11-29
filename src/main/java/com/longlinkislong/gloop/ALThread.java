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

import com.longlinkislong.gloop.alspi.Device;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.CancellationException;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Future;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;
import java.util.concurrent.atomic.AtomicBoolean;
import javax.swing.JOptionPane;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.Marker;
import org.slf4j.MarkerFactory;

/**
 * A representation of the thread that executes OpenAL calls. Currently only one
 * OpenAL thread with a single task queue is supported.
 *
 * @author zmichaels
 * @since 16.03.21
 */
public final class ALThread {

    private static final Marker SYS_MARKER = MarkerFactory.getMarker("SYSTEM");
    private static final Logger LOGGER = LoggerFactory.getLogger("ALThread");
    private static final transient Map<Thread, ALThread> THREAD_MAP = new HashMap<>(1);
    private transient Thread internalThread = null;
    private Device device;    
    private final AtomicBoolean isKill = new AtomicBoolean(false);

    private transient final ExecutorService internalExecutor = new ThreadPoolExecutor(1, 1, 0L, TimeUnit.MILLISECONDS, new LinkedBlockingQueue<>()) {
        @Override
        protected void afterExecute(final Runnable task, final Throwable ex) {
            super.afterExecute(task, ex);

            if (task != null && task instanceof Future<?>) {
                try {
                    final Future<?> future = (Future<?>) task;

                    if (future.isDone()) {
                        future.get();
                    }
                } catch (CancellationException ce) {
                    LOGGER.error(SYS_MARKER, "ALTask was canceled.");
                    LOGGER.debug(SYS_MARKER, ce.getMessage(), ce);
                } catch (ExecutionException ee) {
                    LOGGER.error(SYS_MARKER, "Error executing ALTask!");
                    LOGGER.debug(SYS_MARKER, ee.getMessage(), ee);
                } catch (InterruptedException ie) {
                    LOGGER.error(SYS_MARKER, "ALThread was interrupted! Resetting interrupt state.");
                    LOGGER.debug(SYS_MARKER, ie.getMessage(), ie);
                    Thread.currentThread().interrupt();
                }
            }

            if (ex != null) {
                LOGGER.error(SYS_MARKER, "Error executing ALTask!");
                LOGGER.debug(ex.getMessage(), ex);
            }
        }
    };

    private ALThread() {
        try {
            final Future<?> initTask = this.internalExecutor.submit(this::init);

            initTask.get(5, TimeUnit.SECONDS);
        } catch (InterruptedException ex) {
            throw new RuntimeException("OpenAL initialization was interrupted!", ex);
        } catch (ExecutionException ex) {
            LOGGER.error("Unable to initialize OpenAL!");
            LOGGER.debug(ex.getMessage(), ex);
            
            final int res = JOptionPane.showConfirmDialog(null, "Unable to initialize OpenAL! Would you like to terminate the application?", "OpenAL Error", JOptionPane.YES_NO_OPTION, JOptionPane.ERROR_MESSAGE);
            
            if (res == JOptionPane.YES_OPTION) {                
                System.exit(1);
            } else {
                isKill.set(true);
            }       
        } catch (TimeoutException ex) {
            LOGGER.error("Failed to initialize OpenAL in the timelimit. Sound will now be disabled.", ex);
            shutdown();
        }
    }

    private void init() {
        internalThread = Thread.currentThread();
        device = ALTools.getDriverInstance().deviceCreate();
        THREAD_MAP.put(internalThread, this);
    }

    /**
     * Sets the ALThread to shutdown. All resources will be freed. The task
     * queue will also be closed.
     *
     * @since 16.03.21
     */
    @SuppressWarnings("unchecked")
    public void shutdown() {
        if (isKill.get()) {
            LOGGER.error("Attempted to shutdown OpenAL thread, but OpenAL thread is already shutdown!");
            return;
        }

        try {
            internalExecutor.submit(() -> {                
                ALTools.getDriverInstance().deviceDelete(device);
                device = null;
                THREAD_MAP.remove(internalThread);
                internalThread = null;
                isKill.set(true);
            }).get();
        } catch (InterruptedException ex) {
            throw new RuntimeException("Synchronized task: [ALThread.destroy] was interrupted!", ex);
        } catch (ExecutionException ex) {
            LOGGER.error("An error occurred while attempting to shutdown the OpenAL thread!");
            LOGGER.debug(ex.getMessage(), ex);
        }

        internalExecutor.shutdown();
    }

    /**
     * Submits an ALTask to run on the OpenAL thread.
     *
     * @param task the OpenAL task to run.
     * @since 16.03.21
     */
    public void submitALTask(final ALTask task) {
        if (!this.isKill.get()) {
            this.internalExecutor.execute(Objects.requireNonNull(task));
        }
    }

    /**
     * Submits an ALQuery object and retrieves the result wrapped in a Future
     * object.
     *
     * @param <ReturnType> the return type to expect.
     * @param query the ALQuery object.
     * @return the result wrapped in an ALFuture object.
     * @since 16.03.21
     */
    public <ReturnType> ALFuture<ReturnType> submitALQuery(final ALQuery<ReturnType> query) {
        if (!this.isKill.get()) {
            final Future<ReturnType> raw = this.internalExecutor.submit(Objects.requireNonNull(query));

            return new ALFuture<>(raw);
        } else {
            return new ALFuture<>(null);
        }
    }

    /**
     * Checks if the current thread is this OpenAL thread.
     *
     * @return true if the current thread is this OpenAL thread.
     * @since 16.03.21
     */
    public boolean isCurrent() {
        return Thread.currentThread() == this.internalThread;
    }

    private static final class DefaultHolder {

        private static final ALThread INSTANCE = new ALThread();

        private DefaultHolder() {}
    }

    /**
     * Retrieves the default OpenAL thread.
     *
     * @return the OpenAL thread.
     * @since 16.03.21
     */
    public static ALThread getDefaultInstance() {
        return DefaultHolder.INSTANCE;
    }

    /**
     * Syncs the current thread with this OpenAL thread.
     *
     * @since 16.03.21
     */
    public void sync() {
        ALQuery.create(() -> null).alCall(this);
    }
}
