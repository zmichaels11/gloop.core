/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.longlinkislong.gloop;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

/**
 *
 * @author zmichaels
 */
public class GLThread {

    private final ExecutorService internalExecutor = Executors.newSingleThreadExecutor();
    private Thread internalThread = null;
    private boolean shouldHaltScheduledTasks = false;

    public void shutdown() {
        this.shouldHaltScheduledTasks = true;
        this.internalExecutor.shutdown();
    }

    protected boolean isCurrent() {
        return (Thread.currentThread() == this.internalThread);
    }

    public void submitGLTask(final GLTask task) {
        this.internalExecutor.execute(task);
    }

    public void scheduleGLTask(final GLTask task) {
        this.internalExecutor.execute(new GLTask() {

            @Override
            public void run() {
                task.run();

                if (!GLThread.this.shouldHaltScheduledTasks) {
                    GLThread.this.internalExecutor.execute(this);
                }
            }
        });
    }

    public <ReturnType> GLFuture<ReturnType> submitGLQuery(
            final GLQuery<ReturnType> query) {

        final Future<ReturnType> raw = this.internalExecutor.submit(query);

        return new GLFuture<>(raw);

    }

    protected GLThread() {
        this.internalExecutor.execute(new InitTask());
    }

    private class InitTask implements Runnable {

        @Override
        public void run() {
            GLThread.this.internalThread = Thread.currentThread();
        }
    }

    /**
     * Creates a new GLThread. This should only be called by GLWindow. The first
     * GLWindow created owns the main GLThread. All subsequent instances of
     * GLWindow have a new GLThread leased to them.
     *
     * @return the GLThread
     * @since 15.05.14
     */
    protected static GLThread create() {
        if (!Holder.IS_ASSIGNED) {
            Holder.IS_ASSIGNED = true;
            return Holder.INSTANCE;
        } else {
            return new GLThread();
        }
    }

    private static final class Holder {

        private static boolean IS_ASSIGNED = false;
        private final static GLThread INSTANCE = new GLThread();
    }

    public static GLThread getDefaultInstance() {
        return Holder.INSTANCE;
    }
}
