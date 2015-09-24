/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.longlinkislong.gloop;

import static com.longlinkislong.gloop.GLAsserts.checkGLError;
import static com.longlinkislong.gloop.GLAsserts.glErrorMsg;
import java.util.Objects;
import org.lwjgl.opengl.GL11;

/**
 *
 * @author zmichaels
 */
public class GLPolygonParameters extends GLObject {

    public static final float DEFAULT_POINT_SIZE = 1f;
    public static final float DEFAULT_LINE_SIZE = 1f;
    public static final GLFrontFaceMode DEFAULT_FRONT_FACE = GLFrontFaceMode.GL_CCW;
    public static final GLPolygonMode DEFAULT_MODE = GLPolygonMode.GL_FILL;
    public static final float DEFAULT_POLYGON_OFFSET_FACTOR = 0f;
    public static final float DEFAULT_POLYGON_OFFSET_UNITS = 0f;
    public static final GLCullMode DEFAULT_CULL_MODE = GLCullMode.GL_BACK;

    public final GLEnableStatus cullEnabled;
    public final GLCullMode cullMode;
    public final float pointSize, lineSize;
    public final GLFrontFaceMode frontFace;
    public final GLPolygonMode mode;
    public final float polygonOffsetFactor, polygonOffsetUnits;

    public GLPolygonParameters() {
        this(GLThread.getDefaultInstance());
    }

    public GLPolygonParameters(final GLThread thread) {
        this(
                thread,
                DEFAULT_POINT_SIZE, DEFAULT_LINE_SIZE,
                DEFAULT_FRONT_FACE,
                DEFAULT_MODE,
                DEFAULT_POLYGON_OFFSET_FACTOR, DEFAULT_POLYGON_OFFSET_UNITS,
                GLEnableStatus.GL_ENABLED, DEFAULT_CULL_MODE);
    }

    public GLPolygonParameters(
            final GLThread thread,
            final float pointSize, final float lineSize,
            final GLFrontFaceMode frontFace,
            final GLPolygonMode mode,
            final float polygonOffsetFactor, final float polygonOffsetUnits,
            final GLEnableStatus cullEnabled, final GLCullMode cullMode) {

        super(thread);
        this.pointSize = pointSize;
        this.lineSize = lineSize;
        this.frontFace = Objects.requireNonNull(frontFace);
        this.mode = Objects.requireNonNull(mode);
        this.polygonOffsetFactor = polygonOffsetFactor;
        this.polygonOffsetUnits = polygonOffsetUnits;
        this.cullEnabled = cullEnabled;
        Objects.requireNonNull(this.cullMode = cullMode);
    }

    public GLPolygonParameters withGLThread(final GLThread thread) {
        return this.getThread() == thread
                ? this
                : new GLPolygonParameters(
                        thread,
                        this.pointSize, this.lineSize,
                        this.frontFace,
                        this.mode,
                        this.polygonOffsetFactor, this.polygonOffsetUnits,
                        this.cullEnabled, this.cullMode);
    }

    public GLPolygonParameters withPointSize(final float size) {
        return new GLPolygonParameters(
                this.getThread(),
                size, this.lineSize,
                this.frontFace,
                this.mode,
                this.polygonOffsetFactor, this.polygonOffsetUnits,
                this.cullEnabled, this.cullMode);
    }

    public GLPolygonParameters withLineWidth(final float size) {
        return new GLPolygonParameters(
                this.getThread(),
                this.pointSize, size,
                this.frontFace,
                this.mode,
                this.polygonOffsetFactor, this.polygonOffsetUnits,
                this.cullEnabled, this.cullMode);
    }

    public GLPolygonParameters withFrontFace(final GLFrontFaceMode frontFace) {
        return this.frontFace == frontFace
                ? this
                : new GLPolygonParameters(
                        this.getThread(),
                        this.pointSize, this.lineSize,
                        frontFace,
                        this.mode,
                        this.polygonOffsetFactor, this.polygonOffsetUnits,
                        this.cullEnabled, this.cullMode);
    }

    public GLPolygonParameters withPolygonMode(final GLPolygonMode mode) {

        return this.mode == mode ? this : new GLPolygonParameters(
                        this.getThread(),
                        this.pointSize, this.lineSize,
                        this.frontFace,
                        mode,
                        this.polygonOffsetFactor, this.polygonOffsetUnits,
                        this.cullEnabled, this.cullMode);
    }

    public GLPolygonParameters withPolygonOffset(
            final float factor, final float units) {

        return new GLPolygonParameters(
                this.getThread(),
                this.pointSize, this.lineSize,
                this.frontFace,
                this.mode,
                factor, units,
                this.cullEnabled, this.cullMode);
    }

    public GLPolygonParameters withCullMode(final GLEnableStatus enabled, final GLCullMode mode) {
        return this.cullEnabled == enabled && this.cullMode == mode
                ? this
                : new GLPolygonParameters(
                        this.getThread(),
                        this.pointSize, this.lineSize,
                        this.frontFace,
                        this.mode,
                        this.polygonOffsetFactor, this.polygonOffsetUnits,
                        enabled, mode);
    }

    private GLTask applyTask = null;

    public void applyParameters() {
        if (this.applyTask == null) {
            this.applyTask = new ApplyPolygonParametersTask();
        }

        this.applyTask.glRun(this.getThread());
    }

    public class ApplyPolygonParametersTask extends GLTask {

        @Override
        public void run() {
            final GLThread thread = GLThread.getCurrent().orElseThrow(GLException::new);

            thread.currentPolygonParameters = GLPolygonParameters.this.withGLThread(thread);

            GL11.glPointSize(GLPolygonParameters.this.pointSize);
            assert checkGLError() : glErrorMsg("glPointSize(F)", GLPolygonParameters.this.pointSize);
            
            GL11.glLineWidth(GLPolygonParameters.this.lineSize);
            assert checkGLError() : glErrorMsg("glLineWidth(F)", GLPolygonParameters.this.lineSize);

            GL11.glFrontFace(GLPolygonParameters.this.frontFace.value);
            assert checkGLError() : glErrorMsg("glFrontFace(I)", GLPolygonParameters.this.frontFace);

            switch (GLPolygonParameters.this.cullEnabled) {
                case GL_ENABLED:
                    GL11.glEnable(GL11.GL_CULL_FACE);
                    assert checkGLError() : glErrorMsg("glEnable(I)", "GL_CULL_FACE");

                    GL11.glCullFace(GLPolygonParameters.this.cullMode.value);
                    assert checkGLError() : glErrorMsg("glCullFace(I)", GLPolygonParameters.this.cullMode);
                    
                    break;
                case GL_DISABLED:
                    GL11.glDisable(GL11.GL_CULL_FACE);
                    assert checkGLError() : glErrorMsg("glDisable(I)", "GL_CULL_FACE");
            }

            GL11.glPolygonMode(GL11.GL_FRONT_AND_BACK, GLPolygonParameters.this.mode.value);
            assert checkGLError() : glErrorMsg("glPolygonMode(II)", "GL_FRONT_AND_BACK", GLPolygonParameters.this.mode);

            GL11.glPolygonOffset(GLPolygonParameters.this.polygonOffsetFactor, GLPolygonParameters.this.polygonOffsetUnits);
            assert checkGLError() : glErrorMsg("glPolygonOffset(FF)", GLPolygonParameters.this.polygonOffsetFactor, GLPolygonParameters.this.polygonOffsetUnits);
        }

    }
}
