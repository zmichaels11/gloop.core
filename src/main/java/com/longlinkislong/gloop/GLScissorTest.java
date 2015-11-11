/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.longlinkislong.gloop;

import static com.longlinkislong.gloop.GLAsserts.checkGLError;
import static com.longlinkislong.gloop.GLAsserts.glErrorMsg;
import org.lwjgl.opengl.GL11;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.Marker;
import org.slf4j.MarkerFactory;

/**
 * An OpenGL object that represents a ScissorTest. A ScissorTest is an OpenGL
 * operation used to select a segment of a larger scene to draw.
 *
 * @author zmichaels
 * @since 15.07.06
 */
public class GLScissorTest extends GLObject {
    private static final Marker GL_MARKER = MarkerFactory.getMarker("OPENGL");
    private static final Logger LOGGER = LoggerFactory.getLogger(GLScissorTest.class);
    
    {
        
    }
    
    public final int left;
    public final int bottom;
    public final int width;
    public final int height;        

    public GLScissorTest(
            final int left, final int bottom,
            final int width, final int height) {

        this(GLThread.getAny(), left, bottom, width, height);
    }

    public GLScissorTest(
            final GLThread thread,
            final int left, final int bottom,
            final int width, final int height) {

        super(thread);
        
        LOGGER.trace("Constructed GLScissorTest object on thread: {}", thread);
        
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
            assert checkGLError() : glErrorMsg("glEnable(I)", "GL_SCISSOR_TEST");
            
            GL11.glScissor(GLScissorTest.this.left, GLScissorTest.this.bottom, GLScissorTest.this.width, GLScissorTest.this.height);
            assert checkGLError() : glErrorMsg("glScissor(IIII)", GLScissorTest.this.left, GLScissorTest.this.bottom, GLScissorTest.this.width, GLScissorTest.this.height);                        
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
            assert checkGLError() : glErrorMsg("glDisable(I)", "GL_SCISSOR_TEST");
        }
    }
}
