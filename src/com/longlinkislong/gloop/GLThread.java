/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.longlinkislong.gloop;

import java.util.Collection;
import java.util.Deque;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.Callable;
import java.util.concurrent.CancellationException;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Future;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;
import java.util.stream.Collectors;

/**
 * GLThread is a representation of an OpenGL thread.
 *
 * @author zmichaels
 * @since 15.07.01
 */
public class GLThread implements ExecutorService {

    private static final Map<Thread, GLThread> THREAD_MAP = new HashMap<>();
    final Deque<GLBlending> blendStack = new LinkedList<>();
    final Deque<GLClear> clearStack = new LinkedList<>();
    final Deque<GLDepthTest> depthTestStack = new LinkedList<>();
    final Deque<GLMask> maskStack = new LinkedList<>();
    final Deque<GLPolygonParameters> polygonParameterStack = new LinkedList<>();
    final Deque<GLViewport> viewportStack = new LinkedList<>();

    GLBlending currentBlend = new GLBlending(this);
    GLClear currentClear = new GLClear(this);
    GLDepthTest currentDepthTest = new GLDepthTest(this);
    GLPolygonParameters currentPolygonParameters = new GLPolygonParameters(this);
    GLMask currentMask = new GLMask(this);
    GLViewport currentViewport = null;

    /**
     * Retrieves the current blending mode.
     *
     * @return the current blending mode.
     * @since 15.07.01
     */
    public GLBlending currentBlend() {
        return this.currentBlend;
    }

    /**
     * Pushes the current blending mode onto a stack.
     *
     * @since 15.07.01
     */
    public void pushBlend() {
        this.blendStack.push(this.currentBlend);
    }

    /**
     * Restores the previous blending mode.
     *
     * @return the previous mode.
     * @since 15.07.01
     */
    public GLBlending popBlend() {
        final GLBlending blend = this.blendStack.pop();
        blend.applyBlending();
        return blend;
    }

    /**
     * Retrieves the current clear.
     *
     * @return the current clear.
     * @since 15.07.01
     */
    public GLClear currentClear() {
        return this.currentClear;
    }

    /**
     * Pushes the current clear object onto the stack.
     *
     * @since 15.07.16
     */
    public void pushClear() {
        this.clearStack.push(this.currentClear);
    }

    /**
     * Restores the previous clear mode.
     *
     * @return the previous clear.
     * @since 15.07.01
     */
    public GLClear popClear() {
        final GLClear clear = this.clearStack.pop();
        clear.clear();
        return clear;
    }

    /**
     * Retrieves the current depth test.
     *
     * @return the current depth test.
     * @since 15.07.01
     */
    public GLDepthTest currentDepthTest() {
        return this.currentDepthTest;
    }

    /**
     * Pushes the current depth test onto the stack.
     *
     * @since 15.07.01
     */
    public void pushDepthTest() {
        this.depthTestStack.push(this.currentDepthTest);
    }

    /**
     * Restores the previous depth test.
     *
     * @return the previous depth test.
     * @since 15.07.01
     */
    public GLDepthTest popDepthTest() {
        final GLDepthTest depthTest = this.depthTestStack.pop();
        depthTest.applyDepthFunc();
        return depthTest;
    }

    /**
     * Retrieves the current color mask.
     *
     * @return the current mask.
     * @since 15.07.01
     */
    public GLMask currentMask() {
        return this.currentMask;
    }

    /**
     * Pushes the current color mask onto the stack.
     *
     * @since 15.07.01
     */
    public void pushMask() {
        this.maskStack.push(this.currentMask);
    }

    /**
     * Restores the previous color mask.
     *
     * @return the previous mask.
     * @since 15.07.01
     */
    public GLMask popMask() {
        final GLMask mask = this.maskStack.pop();
        mask.applyMask();
        return mask;
    }

    /**
     * Retrieves the current polygon parameters.
     *
     * @return the current polygon parameters.
     * @since 15.07.16
     */
    public GLPolygonParameters currentPolygonParameters() {
        return this.currentPolygonParameters;
    }

    /**
     * Pushes the current polygon parameters onto the stack.
     *
     * @since 15.07.16
     */
    public void pushPolygonParameters() {
        this.polygonParameterStack.push(this.currentPolygonParameters);
    }

    /**
     * Restores the previous polygon parameters.
     *
     * @return the previous polygon parameters.
     * @since 15.07.16
     */
    public GLPolygonParameters popPolygonParameters() {
        final GLPolygonParameters params = this.polygonParameterStack.pop();
        params.applyParameters();
        return params;
    }

    /**
     * Retrieves the current viewport.
     *
     * @return the current viewport.
     * @since 15.07.01
     */
    public GLViewport currentViewport() {
        return this.currentViewport;
    }

    /**
     * Pushes the current viewport onto the stack.
     *
     * @since 15.07.01
     */
    public void pushViewport() {
        this.viewportStack.push(this.currentViewport);
    }

    /**
     * Restores the previous viewport.
     *
     * @return the previous viewport.
     * @since 15.07.01
     */
    public GLViewport popViewport() {
        final GLViewport viewport = this.viewportStack.pop();
        viewport.applyViewport();
        return viewport;
    }

    private final ExecutorService internalExecutor = new ThreadPoolExecutor(
            1, 1, 0L, TimeUnit.MILLISECONDS, new LinkedBlockingQueue<>()) {

                @Override
                protected void afterExecute(final Runnable task, Throwable ex) {
                    super.afterExecute(task, ex);

                    if (task != null && task instanceof Future<?>) {
                        try {
                            final Future<?> future = (Future<?>) task;

                            if (future.isDone()) {
                                future.get();
                            }
                        } catch (CancellationException ce) {
                            ex = ce;
                        } catch (ExecutionException ee) {
                            ex = ee.getCause();
                        } catch (InterruptedException ie) {
                            Thread.currentThread().interrupt();
                        }
                    }

                    if (ex != null) {
                        ex.printStackTrace();
                    }
                }
            };
    private Thread internalThread = null;
    private boolean shouldHaltScheduledTasks = false;

    protected Thread getThread() {
        return this.internalThread;
    }

    @Override
    public void shutdown() {
        this.shouldHaltScheduledTasks = true;
        this.internalExecutor.shutdown();
    }

    /**
     * Checks if the OpenGL thread is the current thread.
     *
     * @return true if the GLThread is current.
     * @since 15.07.16
     */
    public boolean isCurrent() {
        return (Thread.currentThread() == this.internalThread);
    }

    /**
     * Submits a task to run on the OpenGL thread.
     *
     * @param task the task to run.
     * @since 15.07.16
     */
    public void submitGLTask(final GLTask task) {
        this.internalExecutor.execute(task);
    }

    /**
     * Schedules an OpenGL task to run at every iteration of the main loop.
     *
     * @param task the task to schedule.
     * @since 15.07.16
     */
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

    /**
     * Submits a GLTask to execute after the specified number of frames.
     *
     * @param task the task to execute.
     * @param delay the number of frames to wait.
     * @since 15.07.03
     */
    public void submitGLTask(final GLTask task, final long delay) {
        this.internalExecutor.execute(new GLTask() {
            long count = delay;

            @Override
            public void run() {
                if (count <= 0) {
                    task.run();
                } else {
                    count--;

                    if (!GLThread.this.shouldHaltScheduledTasks) {
                        GLThread.this.internalExecutor.execute(this);
                    }
                }
            }
        });
    }

    /**
     * Submits a GLQuery object to the OpenGL thread. A GLQuery is a task that
     * should be ran on the OpenGL thread and is expected to return some value.
     *
     * @param <ReturnType> the return type.
     * @param query the function to run on the OpenGL thread.
     * @return a Future object that will contain the result.
     * @since 15.07.16
     */
    public <ReturnType> GLFuture<ReturnType> submitGLQuery(
            final GLQuery<ReturnType> query) {

        final Future<ReturnType> raw = this.internalExecutor.submit(query);

        return new GLFuture<>(raw);

    }

    protected GLThread() {
        this.internalExecutor.execute(new InitTask());
    }

    /**
     * Waits until all queries/tasks submitted to the GLThread prior to the
     * barrier are executed.
     *
     * @return null.
     * @since 15.06.01
     */
    public Void insertBarrier() {
        if (Thread.currentThread() == this.internalThread) {
            throw new RuntimeException("Attempted barrier insertion on OpenGL thread!");
        }
        return new BarrierQuery().glCall(this);
    }

    @Override
    public List<Runnable> shutdownNow() {
        return this.internalExecutor.shutdownNow();
    }

    @Override
    public boolean isShutdown() {
        return this.internalExecutor.isShutdown();
    }

    @Override
    public boolean isTerminated() {
        return this.internalExecutor.isTerminated();
    }

    @Override
    public boolean awaitTermination(long timeout, TimeUnit unit) throws InterruptedException {
        return this.internalExecutor.awaitTermination(timeout, unit);
    }

    @Override
    public <T> Future<T> submit(Callable<T> task) {
        return this.submitGLQuery(GLQuery.create(task));
    }

    @Override
    public <T> Future<T> submit(Runnable task, T result) {
        return this.submitGLQuery(GLQuery.create(task, () -> {
            return result;
        }));
    }

    @Override
    public Future<?> submit(Runnable task) {
        return this.submitGLQuery(GLQuery.create(task, () -> {
            return Boolean.TRUE;
        }));
    }

    @Override
    public <T> List<Future<T>> invokeAll(Collection<? extends Callable<T>> tasks) throws InterruptedException {
        return tasks.stream().map(this::submit).collect(Collectors.toList());
    }

    @Override
    public <T> List<Future<T>> invokeAll(Collection<? extends Callable<T>> tasks, long timeout, TimeUnit unit) throws InterruptedException {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public <T> T invokeAny(Collection<? extends Callable<T>> tasks) throws InterruptedException, ExecutionException {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public <T> T invokeAny(Collection<? extends Callable<T>> tasks, long timeout, TimeUnit unit) throws InterruptedException, ExecutionException, TimeoutException {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public void execute(Runnable command) {
        this.submitGLTask(GLTask.create(command));
    }
    
    /**
     * Inserts a query that returns when it has been processed by the OpenGL
     * thread.
     *
     * @since 15.07.16
     */
    public class BarrierQuery extends GLQuery<Void> {

        @Override
        public Void call() throws Exception {
            return null;
        }

    }

    private class InitTask implements Runnable {

        @Override
        public void run() {
            GLThread.this.internalThread = Thread.currentThread();
            THREAD_MAP.put(GLThread.this.internalThread, GLThread.this);
        }
    }

    /**
     * A GLTask that limits the current framerate to the specified limit.
     *
     * @since 15.07.20
     * @deprecated experimental code.
     */
    @Deprecated
    public static class FrameCapTask extends GLTask {

        private final long targetFrameTime;
        long lastTime = System.nanoTime();

        /**
         * Constructs a new FrameCapTask with the target fps.
         *
         * @param targetFPS the target fps to limit to.
         * @since 15.07.20
         */
        public FrameCapTask(final double targetFPS) {
            final double spf = 1.0 / targetFPS;
            final double nspf = 1000000000.0 * spf;

            this.targetFrameTime = (long) nspf;
        }

        @Override
        public void run() {
            final long now = System.nanoTime();
            final long dTime = now - this.lastTime;

            this.lastTime = now;

            if (dTime < this.targetFrameTime) {
                final long sleepTime = this.targetFrameTime - dTime;
                final long msSleepTime = sleepTime / 1000000;
                final int nsSleepTime = (int) (sleepTime - msSleepTime * 1000000);

                try {
                    Thread.sleep(msSleepTime, nsSleepTime);
                } catch (InterruptedException ex) {
                    // -\_0_0_/-
                    Thread.currentThread().interrupt();
                }
            }
        }
    }

    /**
     * A GLTask that profiles the current thread.
     *
     * @since 15.07.20
     */
    public static class FrameStatsTask extends GLTask {

        private double varFPS = 0.0;
        private double totalVar = 0.0;
        private double totalFrameTime = 0.0;
        private double maxFrameTime = Double.NEGATIVE_INFINITY;
        private double minFrameTime = Double.POSITIVE_INFINITY;
        private double fps;
        private long frameCount = 0L;
        private long lastTime;
        private int warmup;

        /**
         * Constructs a new FrameStatsTask with a default warmup period of 300
         * frames.
         *
         * @since 15.07.20
         */
        public FrameStatsTask() {
            this(300);
        }

        /**
         * Constructs a new FrameStatsTask that excludes all frames from profile
         * within the warmup period.
         *
         * @param warmupPeriod the number of frames to disregard on initialize.
         * @since 15.07.20
         */
        public FrameStatsTask(final int warmupPeriod) {
            this.warmup = warmupPeriod;
        }

        @Override
        public void run() {
            final long now = System.currentTimeMillis();

            if (warmup > 0) {
                this.lastTime = now;
                this.warmup--;
                return;
            }

            final long frameTime = now - this.lastTime;
            this.lastTime = now;

            if (frameTime > this.maxFrameTime) {
                this.maxFrameTime = frameTime / 1000.0;
            }

            if (frameTime < this.minFrameTime) {
                this.minFrameTime = frameTime / 1000.0;
            }

            this.totalFrameTime += (frameTime / 1000.0);
            this.frameCount++;
            this.fps = 1000.0 / frameTime;

            this.varFPS = this.getAverageFPS() - this.getFPS();
            this.totalVar += this.varFPS * this.varFPS;

            if (DEBUG) {
                System.out.println(this);
            }
        }

        @Override
        public String toString() {
            return String.format("Frame Stats: [fps: %.2f | avg fps: %.2f | min frame time: %.3f | max frame time: %.3f | std frame time: %.2f]", this.getFPS(), this.getAverageFPS(), this.getShortestFrameTime(), this.getLongestFrameTime(), this.getSTDFPS());
        }

        public double getFPS() {
            return this.fps;
        }

        public double getSTDFPS() {
            return Math.sqrt(this.totalVar / this.frameCount);
        }

        /**
         * Retrieves the average fps
         *
         * @return the FPS.
         * @since 15.07.20
         */
        public double getAverageFPS() {
            return (this.frameCount / this.totalFrameTime);
        }

        public double getShortestFrameTime() {
            return this.minFrameTime;
        }

        public double getLongestFrameTime() {
            return this.maxFrameTime;
        }

        public long getFrameCount() {
            return this.frameCount;
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

    /**
     * Retrieves the default GLThread instance.
     *
     * @return the default OpenGL thread.
     * @since 15.07.16
     */
    public static GLThread getDefaultInstance() {
        return Holder.INSTANCE;
    }

    /**
     * Retrieves the current GLThread.
     *
     * @return the current OpenGL thread.
     * @since 15.07.16
     */
    public static Optional<GLThread> getCurrent() {
        return Optional.of(THREAD_MAP.get(Thread.currentThread()));
    }

    /**
     * Retrieves either the current OpenGL thread or the default OpenGL thread.
     *
     * @return an OpenGL thread.
     * @since 15.07.20
     */
    public static GLThread getAny() {
        return GLThread.getCurrent().orElseGet(GLThread::getDefaultInstance);
    }

    private static final boolean DEBUG;

    static {
        DEBUG = Boolean.getBoolean("debug");
    }
}
