/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.longlinkislong.gloop;

import java.util.Deque;
import java.util.LinkedList;
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
    private final Deque<GLClear> clearStack = new LinkedList<>();
    private final Deque<GLBlending> blendStack = new LinkedList<>();
    private final Deque<GLScissorTest> scissorStack = new LinkedList<>();
    private final Deque<GLMask> maskStack = new LinkedList<>();
    private final Deque<GLViewport> viewportStack = new LinkedList<>();
    private final Deque<GLPolygonParameters> polygonStack = new LinkedList<>();
    private final Deque<GLDepthTest> depthTestStack = new LinkedList<>();
    
    public GLDepthTest currentDepthTest() {
        return this.depthTestStack.isEmpty()
                ? new GLDepthTest(this)
                : this.depthTestStack.peek();
    }
    
    public void pushDepthTest(final GLDepthTest test) {
        this.depthTestStack.push(test);
        test.applyDepthFunc();
    }
    
    public GLDepthTest popDepthTest() {
        final GLDepthTest top = this.depthTestStack.isEmpty()
                ? new GLDepthTest(this)
                : this.depthTestStack.pop();
        
        this.currentDepthTest().applyDepthFunc();
        
        return top;
    }
    
    public GLPolygonParameters currentPolygonParameters() {
        return this.polygonStack.isEmpty() 
                ? new GLPolygonParameters(this)
                : this.polygonStack.peek();
    }
    
    public void pushPolygonParameters(final GLPolygonParameters params) {
        params.applyParameters();
        this.polygonStack.push(params);
    }
    
    public GLPolygonParameters popPolygonParameters() {
        final GLPolygonParameters top = this.polygonStack.isEmpty()
                ? new GLPolygonParameters(this)
                : this.polygonStack.pop();
        
        this.currentPolygonParameters().applyParameters();
        
        return top;
    }

    public GLViewport currentViewport() {
        return this.viewportStack.peek();
    }

    public void pushViewport(final GLViewport viewport) {
        this.viewportStack.push(viewport);
    }

    public GLViewport popViewport() {
        final GLViewport top = this.viewportStack.pop();

        this.currentViewport().applyViewport();

        return top;
    }

    public GLMask currentMask() {
        return this.maskStack.isEmpty()
                ? new GLMask(this)
                : this.maskStack.peek();
    }

    public void pushMask(final GLMask mask) {
        this.maskStack.push(mask);
    }

    public GLMask popMask() {
        final GLMask top = this.maskStack.isEmpty()
                ? new GLMask(this)
                : this.maskStack.pop();

        this.currentMask().applyMask();

        return top;
    }

    public GLScissorTest currentScissorTest() {
        return this.scissorStack.peek();
    }

    public void pushScissorTest(final GLScissorTest test) {
        this.scissorStack.push(test);
    }

    public GLScissorTest popScissorTest() {
        this.currentScissorTest().end();

        if (this.scissorStack.isEmpty()) {
            return null;
        } else {
            final GLScissorTest top = this.scissorStack.pop();
            final GLScissorTest current = this.currentScissorTest();

            top.end();
            if (current != null) {
                current.begin();
            }

            return top;
        }
    }

    public GLBlending currentBlend() {
        return this.blendStack.isEmpty()
                ? new GLBlending(this)
                : this.blendStack.peek();
    }

    public void pushBlend(final GLBlending blend) {
        this.blendStack.push(blend);
    }

    public GLBlending popBlend() {
        final GLBlending top = this.blendStack.isEmpty()
                ? new GLBlending(this)
                : this.blendStack.pop();

        this.currentBlend().applyBlending();

        return top;
    }

    /**
     * Retrieves but does not remove the top of the clear stack. The GLClear
     * object will not be reinitialized.
     *
     * @return the GLClear object.
     * @since 15.05.27
     */
    public GLClear currentClear() {
        return this.clearStack.isEmpty()
                ? new GLClear(this)
                : this.clearStack.peek();
    }

    /**
     * Pushes the GLClear object onto the clear stack.
     *
     * @param clear the clear object to push
     * @since 15.05.27
     */
    public void pushClear(final GLClear clear) {
        this.clearStack.push(clear);
    }

    /**
     * Pops the last GLClear object from the stack. This will reinitialize the
     * GLClear object.
     *
     * @return the GLClear object.
     * @since 15.05.27
     */
    public GLClear popClear() {
        final GLClear top = this.clearStack.isEmpty()
                ? new GLClear(this)
                : this.clearStack.pop();

        this.currentClear().applyClear();

        return top;
    }

    protected Thread getThread() {
        return this.internalThread;
    }

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
