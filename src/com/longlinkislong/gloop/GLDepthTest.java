/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.longlinkislong.gloop;

import java.util.Objects;
import org.lwjgl.opengl.GL11;

/**
 * GLDepthTest is a GLObject that controls depth test parameters.
 *
 * @author zmichaels
 * @since 15.06.18
 */
public class GLDepthTest extends GLObject {

    public static final GLDepthFunc DEFAULT_DEPTH_FUNC = GLDepthFunc.GL_LESS;
    public final GLEnableStatus depthTestEnabled;
    public final GLDepthFunc depthFunc;

    /**
     * Constructs a new GLDepthTest object on the default OpenGL thread.
     *
     * @since 15.06.18
     */
    public GLDepthTest() {
        this(GLThread.getDefaultInstance());
    }

    /**
     * Constructs a new GLDepthTest object on the specified OpenGL thread.
     *
     * @param thread the OpenGL thread to create the object on.
     * @since 15.06.18
     */
    public GLDepthTest(final GLThread thread) {
        this(thread, GLEnableStatus.GL_DISABLED, DEFAULT_DEPTH_FUNC);
    }

    /**
     * Constructs a new GLDepthTest object on the specified OpenGL thread with
     * the specified parameter values.
     *
     * @param thread the OpenGL thread to create the GLDepthTest object on.
     * @param enabled the enabled status.
     * @param depthFunc the depth function to perform for testing.
     * @since 15.06.18
     */
    public GLDepthTest(
            final GLThread thread,
            final GLEnableStatus enabled, final GLDepthFunc depthFunc) {

        super(thread);

        this.depthTestEnabled = Objects.requireNonNull(enabled);
        this.depthFunc = depthFunc;
    }

    /**
     * Copies the GLDepthTest object and overrides the enabled parameter.
     * @param isEnabled the new enabled parameter.
     * @return the GLDepthTest object.
     * @since 15.06.18
     */
    public GLDepthTest withEnabled(final GLEnableStatus isEnabled) {
        return new GLDepthTest(this.getThread(), isEnabled, this.depthFunc);
    }

    /**
     * Copies the GLDepthTest object and overrides the depth function parameter.
     * @param func the new depth function parameter.
     * @return the GLDepthTest object.
     * @since 15.06.18
     */
    public GLDepthTest withDepthFunc(final GLDepthFunc func) {
        return new GLDepthTest(this.getThread(), this.depthTestEnabled, func);
    }

    private final GLTask applyTask = new ApplyDepthFuncTask();

    /**
     * Applies the depth function to the associated OpenGL thread.
     *
     * @since 15.06.18
     */
    public void applyDepthFunc() {
        this.applyTask.glRun(this.getThread());
    }

    /**
     * A GLTask that applies the depth function.
     *
     * @since 15.06.18
     */
    public class ApplyDepthFuncTask extends GLTask {

        @Override
        public void run() {
            switch (GLDepthTest.this.depthTestEnabled) {
                case GL_ENABLED:
                    GL11.glEnable(GL11.GL_DEPTH_TEST);
                    GL11.glDepthFunc(GLDepthTest.this.depthFunc.value);
                    break;
                case GL_DISABLED:
                    GL11.glDisable(GL11.GL_DEPTH_TEST);
                    break;
            }
        }
    }
}
