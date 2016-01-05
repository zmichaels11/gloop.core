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
import java.util.concurrent.atomic.AtomicLong;
import java.util.stream.Collectors;
import org.lwjgl.glfw.GLFW;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.Marker;
import org.slf4j.MarkerFactory;

/**
 * GLThread is a representation of an OpenGL thread.
 *
 * @author zmichaels
 * @since 15.07.01
 */
public class GLThread implements ExecutorService {

    private static final Marker SYS_MARKER = MarkerFactory.getMarker("SYSTEM");
    private static final Marker GLOOP_MARKER = MarkerFactory.getMarker("GLOOP");
    private static final Logger LOGGER = LoggerFactory.getLogger("GLThread");

    private static final transient Map<Thread, GLThread> THREAD_MAP = new HashMap<>();
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
        LOGGER.trace(GLOOP_MARKER, "Pushing GLBlend stack!");
        this.blendStack.push(this.currentBlend);
    }

    /**
     * Restores the previous blending mode.
     *
     * @return the previous mode.
     * @since 15.07.01
     */
    public GLBlending popBlend() {
        LOGGER.trace(GLOOP_MARKER, "Popping GLBlend stack!");
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
        LOGGER.trace(GLOOP_MARKER, "Pushing GLClear stack!");
        this.clearStack.push(this.currentClear);
    }

    /**
     * Restores the previous clear mode.
     *
     * @return the previous clear.
     * @since 15.07.01
     */
    public GLClear popClear() {
        LOGGER.trace(GLOOP_MARKER, "Poping GLClear stack!");
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
        LOGGER.trace(GLOOP_MARKER, "Pushing GLDepthTest stack!");
        this.depthTestStack.push(this.currentDepthTest);
    }

    /**
     * Restores the previous depth test.
     *
     * @return the previous depth test.
     * @since 15.07.01
     */
    public GLDepthTest popDepthTest() {
        LOGGER.trace(GLOOP_MARKER, "Poping GLDepthTest stack!");
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
        LOGGER.trace(GLOOP_MARKER, "Pushing GLMask stack!");
        this.maskStack.push(this.currentMask);
    }

    /**
     * Restores the previous color mask.
     *
     * @return the previous mask.
     * @since 15.07.01
     */
    public GLMask popMask() {
        LOGGER.trace(GLOOP_MARKER, "Popping GLMask stack!");
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
        LOGGER.trace(GLOOP_MARKER, "Pushing GLPolygonParameters stack!");
        this.polygonParameterStack.push(this.currentPolygonParameters);
    }

    /**
     * Restores the previous polygon parameters.
     *
     * @return the previous polygon parameters.
     * @since 15.07.16
     */
    public GLPolygonParameters popPolygonParameters() {
        LOGGER.trace(GLOOP_MARKER, "Popping GLPolygonParameters stack!");
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
        LOGGER.trace(GLOOP_MARKER, "Pushing GLViewport stack!");
        this.viewportStack.push(this.currentViewport);
    }

    /**
     * Restores the previous viewport.
     *
     * @return the previous viewport.
     * @since 15.07.01
     */
    public GLViewport popViewport() {
        LOGGER.trace(GLOOP_MARKER, "Popping GLViewport stack!");
        final GLViewport viewport = this.viewportStack.pop();
        viewport.applyViewport();
        return viewport;
    }

    private transient final ExecutorService internalExecutor = new ThreadPoolExecutor(
            1, 1, 0L, TimeUnit.MILLISECONDS, new LinkedBlockingQueue<>()) {

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
                    LOGGER.error(SYS_MARKER, "GLTask was canceled.");
                    LOGGER.error(SYS_MARKER, ce.getMessage(), ce);
                } catch (ExecutionException ee) {
                    LOGGER.error(SYS_MARKER, "Error executing GLTask");
                    LOGGER.error(SYS_MARKER, ee.getMessage(), ee);
                } catch (InterruptedException ie) {
                    LOGGER.error(SYS_MARKER, "GLThread was interrupted! Resetting interrupt state.");
                    LOGGER.error(SYS_MARKER, ie.getMessage(), ie);
                    Thread.currentThread().interrupt();
                }
            }

            if (ex != null) {
                LOGGER.error(SYS_MARKER, "Error executing GLTask!");
                LOGGER.error(ex.getMessage(), ex);
            }
        }
    };
    private transient Thread internalThread = null;
    private boolean shouldHaltScheduledTasks = false;

    protected Thread getThread() {
        return this.internalThread;
    }

    @Override
    public void shutdown() {
        LOGGER.info(SYS_MARKER, "Shutting down thread: GLThread[{}]", this);
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

        LOGGER.trace(SYS_MARKER, "************** Inserting barrier **************");
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
            LOGGER.trace(SYS_MARKER, "*************** BARRIER *************");
            return null;
        }

    }

    private class InitTask implements Runnable {

        @Override
        public void run() {
            GLThread.this.internalThread = Thread.currentThread();
            final long id = THREAD_ID.getAndIncrement();
            final String name = id == 0 ? "OpenGL Thread: Primary" : "OpenGL Thread: " + id;

            LOGGER.debug(SYS_MARKER, "Renamed GLThread[{}] to GLThread[{}]", GLThread.this.internalThread.getName(), name);
            GLThread.this.internalThread.setName(name);
            THREAD_MAP.put(GLThread.this.internalThread, GLThread.this);
        }
    }

    private static transient final AtomicLong THREAD_ID = new AtomicLong();

    /**
     * A GLTask that limits the current framerate to the specified limit.
     *
     * @since 15.07.20
     */
    public static class FrameCapTask extends GLTask {

        private final double targetFrameTime;
        private double lastTime = 0.0;
        private double dTime;

        /**
         * Constructs a new FrameCapTask with the target fps.
         *
         * @param targetFPS the target fps to limit to.
         * @since 15.07.20
         */
        public FrameCapTask(final double targetFPS) {
            this.targetFrameTime = 1.0 / targetFPS;

            LOGGER.debug(SYS_MARKER, "Target thread time: {}", this.targetFrameTime);
        }

        /**
         * Retrieves the timestep since last frame in seconds.
         *
         * @return the timestep value.
         * @since 15.07.22
         */
        public double getTimestep() {
            return this.dTime;
        }

        @Override
        public void run() {
            final double now = GLFW.glfwGetTime();
            this.dTime = now - this.lastTime;

            if (dTime == now) {
                this.lastTime = now;
                return;
            }

            this.lastTime = now;

            if (dTime < this.targetFrameTime) {

                try {
                    final double sleepS = this.targetFrameTime - dTime;
                    final double sleepMS = sleepS * 1000.0;
                    final double sleepNS = sleepS * 1000000000.0 - sleepMS * 1000000.0;

                    Thread.sleep(GLTools.clamp((long) sleepMS, 0L, 100L), GLTools.clamp((int) sleepNS, 0, 999999));
                } catch (InterruptedException ex) {
                    LOGGER.error(SYS_MARKER, ex.getMessage(), ex);
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
        private double lastTime;
        private int warmup;
        private double timeStep;

        private final long updateInterval;

        /**
         * Constructs a new FrameStatsTask with a default warmup period of 300
         * frames.
         *
         * @since 15.07.20
         */
        public FrameStatsTask() {
            this(300, 1);
        }

        /**
         * Constructs a new FrameStatsTask that excludes all frames from profile
         * within the warmup period.
         *
         * @param warmupPeriod the number of frames to disregard on initialize.
         * @param updateInterval interval for printing out the display.
         * @since 15.07.20
         */
        public FrameStatsTask(final int warmupPeriod, final long updateInterval) {
            this.warmup = warmupPeriod;
            this.updateInterval = Math.max(updateInterval, 1);
            this.lastTime = GLFW.glfwGetTime();
        }

        /**
         * Retrieves the timestep between the last frame and this frame.
         *
         * @return the timestep value.
         * @since 15.07.22
         */
        public double getTimeStep() {
            return this.timeStep;
        }

        @Override
        public void run() {
            final double now = GLFW.glfwGetTime();

            this.timeStep = Math.max(now - this.lastTime, GLTools.ULTRAP);
            this.lastTime = now;

            if (warmup > 0) {
                this.warmup--;
                return;
            }

            if (timeStep > this.maxFrameTime) {
                this.maxFrameTime = timeStep;
            }

            if (timeStep < this.minFrameTime) {
                this.minFrameTime = timeStep;
            }

            this.totalFrameTime += timeStep;
            this.frameCount++;
            this.fps = 1.0 / timeStep;

            this.varFPS = this.getAverageFPS() - this.getFPS();
            this.totalVar += this.varFPS * this.varFPS;

            if (this.frameCount % this.updateInterval == 0) {
                LOGGER.debug("{}", this);
            }
        }

        @Override
        public String toString() {
            return String.format("Frame Stats: [fps: %.2f | avg fps: %.2f | min frame time: %.2fs | max frame time: %.2fs | std frame time: %.2f]", this.getFPS(), this.getAverageFPS(), this.getShortestFrameTime(), this.getLongestFrameTime(), this.getSTDFPS());
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
        return Optional.ofNullable(THREAD_MAP.get(Thread.currentThread()));
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

    static {
        NativeTools.getInstance().autoLoad();
    }

    @Override
    public String toString() {
        if (this.getThread() == null) {
            return "OpenGL Thread: [initializing]";
        } else {
            return this.getThread().getName();
        }
    }
}
