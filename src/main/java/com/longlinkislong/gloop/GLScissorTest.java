/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.longlinkislong.gloop;

import org.lwjgl.opengl.GL11;

/**
 * An OpenGL object that represents a ScissorTest. A ScissorTest is an OpenGL
 * operation used to select a segment of a larger scene to draw.
 *
 * @author zmichaels
 * @since 15.07.06
 */
public class GLScissorTest extends GLObject {
    
    public final int left;
    public final int bottom;
    public final int width;
    public final int height;

    public GLScissorTest(
            final int left, final int bottom,
            final int width, final int height) {

        super();
        this.left = left;
        this.bottom = bottom;
        this.width = width;
        this.height = height;
    }

    public GLScissorTest(
            final GLThread thread,
            final int left, final int bottom,
            final int width, final int height) {

        super(thread);
        this.left = left;
        this.bottom = bottom;
        this.width = width;
        this.height = height;
    }

    private final BeginScissorTestTask beginTask = new BeginScissorTestTask();

    public void begin() {
        this.beginTask.glRun(this.getThread());
    }

    public class BeginScissorTestTask extends GLTask {

        @Override
        public void run() {
            GL11.glEnable(GL11.GL_SCISSOR_TEST);
            
            assert GL11.glGetError() == GL11.GL_NO_ERROR : "glEnable(GL_SCISSOR_TEST) failed!";
            
            GL11.glScissor(GLScissorTest.this.left, GLScissorTest.this.bottom,
                    GLScissorTest.this.width, GLScissorTest.this.height);
            
            assert GL11.glGetError() == GL11.GL_NO_ERROR : String.format("glScissor(%d, %d, %d, %d) failed!",
                    GLScissorTest.this.left, GLScissorTest.this.bottom, GLScissorTest.this.width, GLScissorTest.this.height);
        }
    }

    private final EndScissorTestTask endTask = new EndScissorTestTask();

    public void end() {
        this.endTask.glRun(this.getThread());
    }

    public class EndScissorTestTask extends GLTask {

        @Override
        public void run() {
            GL11.glDisable(GL11.GL_SCISSOR_TEST);
            
            assert GL11.glGetError() == GL11.GL_NO_ERROR : "glDisable(GL_SCISSOR_TEST)";
        }
    }
}
