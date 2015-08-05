/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.longlinkislong.gloop;

import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
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
import org.lwjgl.glfw.GLFW;

/**
 *
 * @author zmichaels
 */
public final class ALThread implements ExecutorService {

    private static final Map<Thread, ALThread> THREAD_MAP = new HashMap<>();
    private static final AtomicLong THREAD_ID = new AtomicLong();
    private static final boolean DEBUG;

    static {
        DEBUG = Boolean.getBoolean("debug") && !System.getProperty("debug.exclude", "").contains("althread");
    }

    private static final class Holder {

        private static final ALThread INSTANCE = new ALThread();
    }

    public ALThread() {
        this(new ThreadPoolExecutor(
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
                });
    }

    public ALThread(ExecutorService service) {
        this.internalExecutor = Objects.requireNonNull(service);
        this.submitALTask(new InitTask());
    }

    public static ALThread getDefaultInstance() {
        return Holder.INSTANCE;
    }

    public static Optional<ALThread> getCurrent() {
        return Optional.ofNullable(THREAD_MAP.get(Thread.currentThread()));
    }

    public static ALThread getAny() {
        return ALThread.getCurrent().orElseGet(ALThread::getDefaultInstance);
    }

    private boolean shouldHaltScheduledTasks = false;
    private Thread internalThread = null;

    private final ExecutorService internalExecutor;

    @Override
    public void shutdown() {
        this.shouldHaltScheduledTasks = true;
        this.internalExecutor.shutdown();
    }

    public void submitALTask(final ALTask task) {
        this.internalExecutor.execute(task);
    }

    public <ReturnType> ALFuture<ReturnType> submitALQuery(final ALQuery<ReturnType> query) {
        return new ALFuture<>(this.internalExecutor.submit(query));
    }

    public void scheduleALTask(final ALTask task) {
        this.internalExecutor.execute(new ALTask() {
            @Override
            public void run() {
                task.run();

                if (!ALThread.this.shouldHaltScheduledTasks) {
                    ALThread.this.internalExecutor.execute(this);
                }
            }
        });
    }

    public void submitALTask(final ALTask task, final long delay) {
        this.internalExecutor.execute(new ALTask() {
            long count = delay;

            @Override
            public void run() {
                if (count <= 0) {
                    task.run();
                } else {
                    count--;

                    if (!ALThread.this.shouldHaltScheduledTasks) {
                        ALThread.this.internalExecutor.execute(this);
                    }
                }
            }
        });
    }

    public Void insertBarrier() {
        if (Thread.currentThread() == this.internalThread) {
            throw new ALException("Attempted barrier insertion on OpenAL thread!");
        }
        return new BarrierQuery().alCall(this);
    }

    public boolean isCurrent() {
        return (Thread.currentThread() == this.internalThread);
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
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public <T> Future<T> submit(Runnable task, T result) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public Future<?> submit(Runnable task) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public <T> List<Future<T>> invokeAll(Collection<? extends Callable<T>> tasks) throws InterruptedException {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public <T> List<Future<T>> invokeAll(Collection<? extends Callable<T>> tasks, long timeout, TimeUnit unit) throws InterruptedException {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public <T> T invokeAny(Collection<? extends Callable<T>> tasks) throws InterruptedException, ExecutionException {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public <T> T invokeAny(Collection<? extends Callable<T>> tasks, long timeout, TimeUnit unit) throws InterruptedException, ExecutionException, TimeoutException {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public void execute(final Runnable command) {
        this.submitALTask(ALTask.create(command));
    }

    public class BarrierQuery extends ALQuery<Void> {

        @Override
        public Void call() throws Exception {
            return null;
        }
    }

    private class InitTask extends ALTask {

        @Override
        public void run() {
            ALThread.this.internalThread = Thread.currentThread();

            final long id = THREAD_ID.getAndIncrement();
            final String name = id == 0 ? "OpenAL Thread: Primary" : "OpenAL Thread: " + id;

            ALThread.this.internalThread.setName(name);
            THREAD_MAP.put(ALThread.this.internalThread, ALThread.this);
        }
    }

    public static class FrameCapTask extends ALTask {

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

            if (DEBUG) {
                System.out.printf("Target thread time: %.2fs\n", this.targetFrameTime);
            }
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
                    // -\_0_0_/-
                    Thread.currentThread().interrupt();
                }
            }
        }
    }

    public static class FrameStatsTask extends ALTask {

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

            if (DEBUG && this.frameCount % this.updateInterval == 0) {
                System.out.println(this);
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

}
