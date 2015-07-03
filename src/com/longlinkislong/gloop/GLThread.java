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
 *
 * @author zmichaels
 */
public class GLThread implements ExecutorService {

    static final Map<Thread, GLThread> THREAD_MAP = new HashMap<>();
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
    
    public GLPolygonParameters currentPolygonParameters() {
        return this.currentPolygonParameters;
    }
    
    public void pushPolygonParameters() {
        this.polygonParameterStack.push(this.currentPolygonParameters);
    }
    
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
