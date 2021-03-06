/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.longlinkislong.gloop;

import java.io.IOException;
import org.junit.Test;

/**
 *
 * @author zmichaels
 */
public class NeHe02 {
    static {
        System.setProperty("org.slf4j.simpleLogger.defaultLogLevel", "debug");
    }    

    @Test
    public void test() {        
        new TestFramework("NeHe 02", this::init, this::redraw)
                .showWindow()
                .runFor(5000);
    }

    private final GLClear clear = new GLClear()
            .withClearBits(GLFramebufferMode.GL_COLOR_BUFFER_BIT, GLFramebufferMode.GL_DEPTH_BUFFER_BIT)
            .withClearColor(0, 0, 0, 1)
            .withClearDepth(1.0);

    private final GLDepthTest depthTest = new GLDepthTest()
                .withEnabled(GLEnableStatus.GL_ENABLED)
                .withDepthFunc(GLDepthFunc.GL_LEQUAL);

    private GLBuffer vTriangle;
    private GLBuffer vSquare;

    private GLVertexArray vaoTriangle;
    private GLVertexArray vaoSquare;

    private GLVertexAttributes attribs;

    private GLProgram simpleProgram;    

    private void init() {             
        final GLThread thread = GLThread.getCurrent()
                .orElseThrow(() -> new RuntimeException("No available OpenGL thread!"));

        attribs = new GLVertexAttributes();
        attribs.setAttribute("vPos", 0);

        vTriangle = new GLBuffer(thread);
        vSquare = new GLBuffer(thread);

        vaoTriangle = new GLVertexArray(thread);
        vaoSquare = new GLVertexArray(thread);

        simpleProgram = new GLProgram(thread);

        try {
            TestFramework.linkProgram(simpleProgram, "simple.vert", "simple.frag");
        } catch (IOException ex) {
            throw new RuntimeException("Unable to link shaders!", ex);
        }

        clear.clear();
        depthTest.applyDepthFunc();

        vTriangle.allocate(3 * GLVec3F.VECTOR_WIDTH, GLBufferUsage.GL_STATIC_DRAW)
                .upload(
                        0f, 1f, 0f,
                        -1f, -1f, 0f,
                        1f, -1f, 0f);

        vSquare.allocate(4 * GLVec3F.VECTOR_WIDTH, GLBufferUsage.GL_STATIC_DRAW)
                .upload(
                    -1f, 1f, 0f,
                    -1f, -1f, 0f,
                    1f, 1f, 0f,
                    1f, -1f, 0f);

        vaoTriangle.attachBuffer(0, vTriangle, GLVertexAttributeType.GL_FLOAT, GLVertexAttributeSize.VEC3);
        vaoSquare.attachBuffer(0, vSquare, GLVertexAttributeType.GL_FLOAT, GLVertexAttributeSize.VEC3);
    }
    
    private void redraw() {
        clear.clear();
        
        final GLMat4F p = GLMat4F.perspective(45.0f, 1.0f, 0.1f, 100.0f);

        simpleProgram.use();
        
        {
            final GLMat4F mv = GLMat4F.translation(-1.5f, 0f, -8f);

            simpleProgram.setUniformMatrixF("mvp", mv.multiply(p));
            vaoTriangle.drawArrays(GLDrawMode.GL_TRIANGLES, 0, 3);
        }

        {
            final GLMat4F mv = GLMat4F.translation(1.5f, 0f, -8f);

            simpleProgram.setUniformMatrixF("mvp", mv.multiply(p));
            vaoSquare.drawArrays(GLDrawMode.GL_TRIANGLE_STRIP, 0, 4);
        }

        TestFramework.assertNoGLError();
    }
}
