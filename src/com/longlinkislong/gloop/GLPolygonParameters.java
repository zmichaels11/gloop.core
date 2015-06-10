/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.longlinkislong.gloop;

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
    public static final GLPolygonMode DEFAULT_FRONT_MODE = GLPolygonMode.GL_FILL;
    public static final GLPolygonMode DEFAULT_BACK_MODE = GLPolygonMode.GL_FILL;
    public static final float DEFAULT_POLYGON_OFFSET_FACTOR = 0f;
    public static final float DEFAULT_POLYGON_OFFSET_UNITS = 0f;
    public static final GLCullMode DEFAULT_CULL_MODE = GLCullMode.GL_BACK;

    public final GLEnableStatus cullEnabled;
    public final GLCullMode cullMode;
    public final float pointSize, lineSize;
    public final GLFrontFaceMode frontFace;
    public final GLPolygonMode frontMode, backMode;
    public final float polygonOffsetFactor, polygonOffsetUnits;

    public GLPolygonParameters() {
        this(GLThread.getDefaultInstance());
    }

    public GLPolygonParameters(final GLThread thread) {
        this(
                thread,
                DEFAULT_POINT_SIZE, DEFAULT_LINE_SIZE,
                DEFAULT_FRONT_FACE,
                DEFAULT_FRONT_MODE, DEFAULT_BACK_MODE,
                DEFAULT_POLYGON_OFFSET_FACTOR, DEFAULT_POLYGON_OFFSET_UNITS,
                GLEnableStatus.GL_ENABLED, DEFAULT_CULL_MODE);
    }

    public GLPolygonParameters(
            final float pointSize, final float lineSize,
            final GLFrontFaceMode frontFace,
            final GLPolygonMode frontMode, final GLPolygonMode backMode,
            final float polygonOffsetFactor, final float polygonOffsetUnits,
            final GLEnableStatus cullEnabled, final GLCullMode cullMode) {

        this(GLThread.getDefaultInstance(),
                pointSize, lineSize,
                frontFace,
                frontMode, backMode,
                polygonOffsetFactor, polygonOffsetUnits,
                cullEnabled, cullMode);
    }

    public GLPolygonParameters(
            final GLThread thread,
            final float pointSize, final float lineSize,
            final GLFrontFaceMode frontFace,
            final GLPolygonMode frontMode, final GLPolygonMode backMode,
            final float polygonOffsetFactor, final float polygonOffsetUnits,
            final GLEnableStatus cullEnabled, final GLCullMode cullMode) {

        super(thread);
        this.pointSize = pointSize;
        this.lineSize = lineSize;
        Objects.requireNonNull(this.frontFace = frontFace);
        Objects.requireNonNull(this.frontMode = frontMode);
        Objects.requireNonNull(this.backMode = backMode);
        this.polygonOffsetFactor = polygonOffsetFactor;
        this.polygonOffsetUnits = polygonOffsetUnits;
        this.cullEnabled = cullEnabled;
        Objects.requireNonNull(this.cullMode = cullMode);
    }

    public GLPolygonParameters withPointSize(final float size) {
        return new GLPolygonParameters(
                this.getThread(),
                size, this.lineSize,
                this.frontFace,
                this.frontMode, this.backMode,
                this.polygonOffsetFactor, this.polygonOffsetUnits,
                this.cullEnabled, this.cullMode);
    }

    public GLPolygonParameters withLineWidth(final float size) {
        return new GLPolygonParameters(
                this.getThread(),
                this.pointSize, size,
                this.frontFace,
                this.frontMode, this.backMode,
                this.polygonOffsetFactor, this.polygonOffsetUnits,
                this.cullEnabled, this.cullMode);
    }

    public GLPolygonParameters withFrontFace(final GLFrontFaceMode frontFace) {
        return new GLPolygonParameters(
                this.getThread(),
                this.pointSize, this.lineSize,
                frontFace,
                this.frontMode, this.backMode,
                this.polygonOffsetFactor, this.polygonOffsetUnits,
                this.cullEnabled, this.cullMode);
    }

    public GLPolygonParameters withPolygonMode(
            final GLPolygonMode frontMode, final GLPolygonMode backMode) {

        return new GLPolygonParameters(
                this.getThread(),
                this.pointSize, this.lineSize,
                this.frontFace,
                frontMode, backMode,
                this.polygonOffsetFactor, this.polygonOffsetUnits,
                this.cullEnabled, this.cullMode);
    }

    public GLPolygonParameters withPolygonOffset(
            final float factor, final float units) {

        return new GLPolygonParameters(
                this.getThread(),
                this.pointSize, this.lineSize,
                this.frontFace,
                this.frontMode, this.backMode,
                factor, units,
                this.cullEnabled, this.cullMode);
    }

    public GLPolygonParameters withCullMode(final GLEnableStatus enabled, final GLCullMode mode) {
        return new GLPolygonParameters(
                this.getThread(),
                this.pointSize, this.lineSize,
                this.frontFace,
                this.frontMode, this.backMode,
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
            GL11.glPointSize(GLPolygonParameters.this.pointSize);
            GL11.glLineWidth(GLPolygonParameters.this.lineSize);
            GL11.glFrontFace(GLPolygonParameters.this.frontFace.value);

            switch(GLPolygonParameters.this.cullEnabled) {
                case GL_ENABLED:
                    GL11.glEnable(GL11.GL_CULL_FACE);
                    GL11.glCullFace(GLPolygonParameters.this.cullMode.value);
                    break;
                case GL_DISABLED:
                    GL11.glDisable(GL11.GL_CULL_FACE);
            }            
            
            GL11.glPolygonMode(GL11.GL_FRONT, GLPolygonParameters.this.frontMode.value);
            GL11.glPolygonMode(GL11.GL_BACK, GLPolygonParameters.this.backMode.value);
            GL11.glPolygonOffset(
                    GLPolygonParameters.this.polygonOffsetFactor,
                    GLPolygonParameters.this.polygonOffsetUnits);
        }

    }
}
