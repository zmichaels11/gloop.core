/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.longlinkislong.gloop;

import org.lwjgl.opengl.GL11;

/**
 * A GLObject that represents the state of viewport settings.
 *
 * @author zmichaels
 * @see
 * <a href="https://www.khronos.org/opengles/sdk/docs/man/xhtml/glViewport.xml">glViewport
 * (OpenGL SDK)</a>
 * @see <a href="https://www.opengl.org/wiki/GLAPI/glViewport">glViewport
 * (OpenGL Wiki)</a>
 * @since 15.06.24
 */
public class GLViewport extends GLObject {

    /**
     * The lower-left corner of the viewport rectangle, in pixels.
     *
     * @since 15.06.24
     */
    public final int x;
    /**
     * The lower-left corner of the viewport rectangle, in pixels.
     */
    public final int y;
    /**
     * Specifies the width of the viewport.
     *
     * @since 15.06.24
     */
    public final int width;
    /**
     * Specifies the height of the viewport.
     *
     * @since 15.06.24
     */
    public final int height;

    /**
     * Constructs a new GLViewport object on the default OpenGL thread.
     *
     * @param x the x-element of the lower-left viewport rectangle.
     * @param y the y-element of the lower-left viewport rectangle.
     * @param w the width of the viewport.
     * @param h the height of the viewport.
     * @since 15.06.24
     */
    public GLViewport(final int x, final int y, final int w, final int h) {
        super();
        this.x = x;
        this.y = y;
        this.width = w;
        this.height = h;
    }

    /**
     * Constructs a new GLViewport object on the supplied OpenGL thread.
     *
     * @param thread the OpenGL thread to create the viewport object on.
     * @param x the x-element of the lower-left viewport rectangle.
     * @param y the y-element of the lower-left viewport rectangle.
     * @param w the width of the viewport.
     * @param h the height of the viewport.
     * @since 15.06.25
     */
    public GLViewport(
            final GLThread thread,
            final int x, final int y, final int w, final int h) {

        super(thread);
        this.x = x;
        this.y = y;
        this.width = w;
        this.height = h;
    }
    
    public GLViewport withGLThread(final GLThread thread) {
        return this.getThread() == thread
                ? this
                : new GLViewport(thread, this.x, this.y, this.width, this.height);
    }
    
    public GLViewport withViewRect(final int x, final int y, final int width, final int height) {
        return this.x == x && this.y == y && this.width == width && this.height == height
                ? this
                : new GLViewport(this.getThread(), x, y, width, height);
    }

    private final ApplyViewportTask applyTask = new ApplyViewportTask();

    /**
     * Applies the viewport to the associated OpenGL thread.
     *
     * @since 15.06.24
     */
    public void applyViewport() {
        this.applyTask.glRun(this.getThread());
    }

    /**
     * A GLTask that applies the viewport settings.
     *
     * @since 15.06.24
     */
    public class ApplyViewportTask extends GLTask {

        @Override
        public void run() {
            final GLThread thread = GLThread.getCurrent().orElseThrow(GLException::new);
            
            thread.currentViewport = GLViewport.this.withGLThread(thread);
            GL11.glViewport(
                    GLViewport.this.x, GLViewport.this.y,
                    GLViewport.this.width, GLViewport.this.height);
            
            assert(GL11.glGetError() == GL11.GL_NO_ERROR);
        }
    }
}
