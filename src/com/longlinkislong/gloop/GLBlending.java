/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.longlinkislong.gloop;

import java.util.Objects;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL14;
import org.lwjgl.opengl.GL20;

/**
 *
 * @author zmichaels
 */
public class GLBlending extends GLObject {

    public static final GLBlendEquation DEFAULT_RGB_BLEND = GLBlendEquation.GL_FUNC_ADD;
    public static final GLBlendEquation DEFAULT_ALPHA_BLEND = GLBlendEquation.GL_FUNC_ADD;
    public static final GLBlendFunc DEFAULT_RGB_FUNC_SRC = GLBlendFunc.GL_ONE;
    public static final GLBlendFunc DEFAULT_RGB_FUNC_DST = GLBlendFunc.GL_ZERO;
    public static final GLBlendFunc DEFAULT_ALPHA_FUNC_SRC = GLBlendFunc.GL_ONE;
    public static final GLBlendFunc DEFAULT_ALPHA_FUNC_DST = GLBlendFunc.GL_ZERO;

    public final boolean enabled;
    public final GLBlendEquation rgbBlend;
    public final GLBlendEquation alphaBlend;
    public final GLBlendFunc rgbFuncSrc;
    public final GLBlendFunc rgbFuncDst;
    public final GLBlendFunc alphaFuncSrc;
    public final GLBlendFunc alphaFuncDst;

    public GLBlending() {
        this(GLThread.getDefaultInstance());
    }

    public GLBlending(final GLThread thread) {
        this(
                thread,
                false,
                DEFAULT_RGB_BLEND, DEFAULT_ALPHA_BLEND,
                DEFAULT_RGB_FUNC_SRC, DEFAULT_RGB_FUNC_DST,
                DEFAULT_ALPHA_FUNC_SRC, DEFAULT_ALPHA_FUNC_DST);
    }

    public GLBlending(
            final GLThread thread,
            final boolean enabled,
            final GLBlendEquation rgbBlend,
            final GLBlendEquation alphaBlend,
            final GLBlendFunc rgbFuncSrc, final GLBlendFunc rgbFuncDst,
            final GLBlendFunc alphaFuncSrc, final GLBlendFunc alphaFuncDst) {

        super(thread);
        this.enabled = enabled;
        Objects.requireNonNull(this.rgbBlend = rgbBlend);
        Objects.requireNonNull(this.alphaBlend = alphaBlend);
        Objects.requireNonNull(this.rgbFuncSrc = rgbFuncSrc);
        Objects.requireNonNull(this.rgbFuncDst = rgbFuncDst);
        Objects.requireNonNull(this.alphaFuncSrc = alphaFuncSrc);
        Objects.requireNonNull(this.alphaFuncDst = alphaFuncDst);
    }

    public GLBlending withBlendEquation(
            final GLBlendEquation rgb, final GLBlendEquation alpha) {

        return new GLBlending(
                this.getThread(),
                this.enabled,
                this.rgbBlend, this.alphaBlend,
                this.rgbFuncSrc, this.rgbFuncDst,
                this.alphaFuncSrc, this.alphaFuncDst);
    }

    public GLBlending withEnabled(final boolean isEnabled) {

        return new GLBlending(
                this.getThread(),
                isEnabled,
                this.rgbBlend, this.alphaBlend,
                this.rgbFuncSrc, this.rgbFuncDst,
                this.alphaFuncSrc, this.alphaFuncDst);
    }

    public GLBlending withBlendFunc(
            final GLBlendFunc rgbSrc, final GLBlendFunc rgbDst,
            final GLBlendFunc alphaSrc, final GLBlendFunc alphaDst) {

        return new GLBlending(
                this.getThread(),
                this.enabled,
                this.rgbBlend, this.alphaBlend,
                rgbSrc, rgbDst,
                alphaSrc, alphaDst);
    }

    private SetBlendingTask applyTask = null;

    public final void applyBlending() {
        if (this.applyTask != null) {
            this.applyTask.glRun(this.getThread());
        } else {
            this.applyTask = new SetBlendingTask();
        }
    }

    public class SetBlendingTask extends GLTask {

        @Override
        public void run() {
            if (GLBlending.this.enabled) {
                GL11.glEnable(GL11.GL_BLEND);
            } else {
                GL11.glDisable(GL11.GL_BLEND);
            }

            GL20.glBlendEquationSeparate(
                    GLBlending.this.rgbBlend.value,
                    GLBlending.this.alphaBlend.value);

            GL14.glBlendFuncSeparate(
                    GLBlending.this.rgbFuncSrc.value,
                    GLBlending.this.rgbFuncDst.value,
                    GLBlending.this.alphaFuncSrc.value,
                    GLBlending.this.alphaFuncDst.value);
        }
    }
}
