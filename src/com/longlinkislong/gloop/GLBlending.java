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
 * A representation of blend states to use.
 *
 * @author zmichaels
 * @since 15.06.18
 */
public class GLBlending extends GLObject {

    /**
     * Default RGB blend equation used by OpenGL.
     *
     * @since 15.06.18
     */
    public static final GLBlendEquation DEFAULT_RGB_BLEND = GLBlendEquation.GL_FUNC_ADD;
    /**
     * Default alpha blend equation used by OpenGL.
     *
     * @since 15.06.18
     */
    public static final GLBlendEquation DEFAULT_ALPHA_BLEND = GLBlendEquation.GL_FUNC_ADD;
    /**
     * Default RGB blend function used on source color for OpenGL.
     *
     * @since 15.06.18
     */
    public static final GLBlendFunc DEFAULT_RGB_FUNC_SRC = GLBlendFunc.GL_ONE;
    /**
     * Default RGB blend function used on destination color for OpenGL.
     *
     * @since 15.06.18
     */
    public static final GLBlendFunc DEFAULT_RGB_FUNC_DST = GLBlendFunc.GL_ZERO;
    /**
     * Default alpha blend function used on source color for OpenGL.
     *
     * @since 15.06.18
     */
    public static final GLBlendFunc DEFAULT_ALPHA_FUNC_SRC = GLBlendFunc.GL_ONE;
    /**
     * Default alpha blend function used on destination color for OpenGL
     *
     * @since 15.06.18
     */
    public static final GLBlendFunc DEFAULT_ALPHA_FUNC_DST = GLBlendFunc.GL_ZERO;

    /**
     * Enabled status for blending. Blending will occur when set if this is set
     * to GL_ENABLED.
     *
     * @since 15.06.18
     */
    public final GLEnableStatus enabled;
    /**
     * Blend equation used on RGB color elements
     *
     * @since 15.06.18
     */
    public final GLBlendEquation rgbBlend;
    /**
     * Blend equation used on alpha color elements
     *
     * @since 15.06.18
     */
    public final GLBlendEquation alphaBlend;
    /**
     * Blend function used on RGB source color elements.
     *
     * @since 15.06.18
     */
    public final GLBlendFunc rgbFuncSrc;
    /**
     * Blend function used on RGB destination color elements.
     *
     * @since 15.06.18
     */
    public final GLBlendFunc rgbFuncDst;
    /**
     * Blend function used on alpha source color elements.
     *
     * @since 15.06.18
     */
    public final GLBlendFunc alphaFuncSrc;
    /**
     * Blend function used on alpha destination color elements.
     *
     * @since 15.06.18
     */
    public final GLBlendFunc alphaFuncDst;

    /**
     * Constructs a new GLBlending object for the default OpenGL thread using
     * the default settings.
     *
     * @since 15.06.18
     */
    public GLBlending() {
        this(GLThread.getDefaultInstance());
    }

    /**
     * Constructs a new GLBlending object for the specified OpenGL thread using
     * the default settings.
     *
     * @param thread the thread to create the GLBlending object on.
     * @since 15.06.18
     */
    public GLBlending(final GLThread thread) {
        this(
                thread,
                GLEnableStatus.GL_DISABLED,
                DEFAULT_RGB_BLEND, DEFAULT_ALPHA_BLEND,
                DEFAULT_RGB_FUNC_SRC, DEFAULT_RGB_FUNC_DST,
                DEFAULT_ALPHA_FUNC_SRC, DEFAULT_ALPHA_FUNC_DST);
    }

    /**
     * Constructs a new GLBlending object for the specified OpenGL thread using
     * the specified parameters.
     *
     * @param thread the thread to create the GLBlending object on.
     * @param enabled specifies if blending is enabled.
     * @param rgbBlend the RGB blend equation.
     * @param alphaBlend the alpha blend equation.
     * @param rgbFuncSrc the RGB source blend function.
     * @param rgbFuncDst the RGB destination blend function.
     * @param alphaFuncSrc the alpha source blend function.
     * @param alphaFuncDst the alpha destination blend function.
     * @since 15.06.18
     */
    public GLBlending(
            final GLThread thread,
            final GLEnableStatus enabled,
            final GLBlendEquation rgbBlend,
            final GLBlendEquation alphaBlend,
            final GLBlendFunc rgbFuncSrc, final GLBlendFunc rgbFuncDst,
            final GLBlendFunc alphaFuncSrc, final GLBlendFunc alphaFuncDst) {

        super(thread);

        Objects.requireNonNull(this.enabled = enabled);
        Objects.requireNonNull(this.rgbBlend = rgbBlend);
        Objects.requireNonNull(this.alphaBlend = alphaBlend);
        Objects.requireNonNull(this.rgbFuncSrc = rgbFuncSrc);
        Objects.requireNonNull(this.rgbFuncDst = rgbFuncDst);
        Objects.requireNonNull(this.alphaFuncSrc = alphaFuncSrc);
        Objects.requireNonNull(this.alphaFuncDst = alphaFuncDst);
    }

    /**
     * Copies the GLBlending object and overrides the blend equations.
     * @param rgb the RGB blend equation.
     * @param alpha the alpha blend equation.
     * @return the GLBlending object.
     * @since 15.06.18
     */
    public GLBlending withBlendEquation(
            final GLBlendEquation rgb, final GLBlendEquation alpha) {

        return new GLBlending(
                this.getThread(),
                this.enabled,
                rgb, alpha,
                this.rgbFuncSrc, this.rgbFuncDst,
                this.alphaFuncSrc, this.alphaFuncDst);
    }

    /**
     * Copies the GLBlending object and overrides the enabled status.
     * @param isEnabled if blending should be enabled.
     * @return the GLBlending object
     * @since 15.06.18
     */
    public GLBlending withEnabled(final GLEnableStatus isEnabled) {

        return new GLBlending(
                this.getThread(),
                isEnabled,
                this.rgbBlend, this.alphaBlend,
                this.rgbFuncSrc, this.rgbFuncDst,
                this.alphaFuncSrc, this.alphaFuncDst);
    }

    /**
     * Copies the GLBlending object and overrides the source and destination blend functions.
     * @param rgbSrc the blend function for RGB source color.
     * @param rgbDst the blend function for RGB destination color.
     * @param alphaSrc the blend function for alpha source color.
     * @param alphaDst the blend function for alpha destination color.
     * @return the blend function
     * @since 15.06.18
     */
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

    /**
     * Applies all of the blend parameters on the GLBlending object's GLThread.
     * @since 15.06.18
     */
    public final void applyBlending() {
        if (this.applyTask == null) {            
            this.applyTask = new SetBlendingTask();
        }
        
        this.applyTask.glRun(this.getThread());
    }

    /**
     * GLTask that sets the OpenGL blending parameters.
     * @since 15.06.18
     */
    public class SetBlendingTask extends GLTask {

        @Override
        public void run() {
            switch (GLBlending.this.enabled) {
                case GL_ENABLED:
                    GL11.glEnable(GL11.GL_BLEND);

                    GL20.glBlendEquationSeparate(
                            GLBlending.this.rgbBlend.value,
                            GLBlending.this.alphaBlend.value);

                    GL14.glBlendFuncSeparate(
                            GLBlending.this.rgbFuncSrc.value,
                            GLBlending.this.rgbFuncDst.value,
                            GLBlending.this.alphaFuncSrc.value,
                            GLBlending.this.alphaFuncDst.value);
                    break;
                case GL_DISABLED:
                    GL11.glDisable(GL11.GL_BLEND);
            }
        }
    }

    @Override
    public boolean equals(final Object other) {
        if (this == other) {
            return true;
        } else if (other instanceof GLBlending) {
            final GLBlending oBlend = (GLBlending) other;

            return this.enabled == oBlend.enabled
                    && this.alphaBlend == oBlend.alphaBlend
                    && this.alphaFuncDst == oBlend.alphaFuncDst
                    && this.alphaFuncSrc == oBlend.alphaFuncSrc
                    && this.rgbBlend == oBlend.rgbBlend
                    && this.rgbFuncDst == oBlend.rgbFuncDst
                    && this.rgbFuncSrc == oBlend.rgbFuncSrc;
        } else {
            return false;
        }
    }

    @Override
    public int hashCode() {
        int hash = 3;
        hash = 59 * hash + Objects.hashCode(this.enabled);
        hash = 59 * hash + Objects.hashCode(this.rgbBlend);
        hash = 59 * hash + Objects.hashCode(this.alphaBlend);
        hash = 59 * hash + Objects.hashCode(this.rgbFuncSrc);
        hash = 59 * hash + Objects.hashCode(this.rgbFuncDst);
        hash = 59 * hash + Objects.hashCode(this.alphaFuncSrc);
        hash = 59 * hash + Objects.hashCode(this.alphaFuncDst);
        return hash;
    }
}
