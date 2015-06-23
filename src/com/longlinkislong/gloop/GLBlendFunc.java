/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.longlinkislong.gloop;

import org.lwjgl.opengl.GL11;

/**
 * Types of pixel arithmetic scale operations to perform when blending. These
 * values are multiplied by the source and destination colors and then combined
 * with a GLBlendEquation.
 *
 * @author zmichaels
 * @see <a href="https://www.opengl.org/wiki/GLAPI/glBlendFuncSeparate">glBlendFunc (OpenGL Wiki)</a>
 * @see <a href="https://www.opengl.org/sdk/docs/man/html/glBlendFunc.xhtml">glBlendFunc (OpenGL SDK)</a>
 * @since 15.06.18
 */
public enum GLBlendFunc {

    /**
     * (fR, fG, fB, fA) = (0, 0, 0, 0)
     *
     * @since 15.06.18
     */
    GL_ZERO(0),
    /**
     * (fR, fG, fB, fA) = (1, 1, 1, 1)
     *
     * @since 15.06.18
     */
    GL_ONE(1),
    /**
     * (fR, fG, fB, fA) = (sR/kR, sG/kG, sB/kB, sA/kA)
     * @since 15.06.18
     */
    GL_SRC_COLOR(GL11.GL_SRC_COLOR),
    /**
     * (fR, fG, fB, fA) = (1-sR/kR, 1-sG/kG, 1-sB/kB, 1-sA/kA)
     * @since 15.06.18
     */
    GL_ONE_MINUS_SRC_COLOR(GL11.GL_ONE_MINUS_SRC_COLOR),
    /**
     * (fR, fG, fB, fA) = (dR/kR, dG/kG, dB/kB, dA/kA)
     * @since 15.06.18
     */
    GL_DST_COLOR(GL11.GL_DST_COLOR),
    /**
     * (fR, fG, fB, fA) = (1-dR/kR, 1-dG/kG, 1-dB/kB, 1-dA/kA)
     * @since 15.06.18
     */
    GL_ONE_MINUS_DST_COLOR(GL11.GL_ONE_MINUS_DST_COLOR),
    /**
     * (fR, fG, fB, fA) = (sA/kA, sA/kA, sA/kA, sA/kA)
     * @since 15.06.18
     */
    GL_SRC_ALPHA(GL11.GL_SRC_ALPHA),
    /**
     * (fR, fG, fB, fA) = (1-sA/kA, 1-sA/kA, 1-sA/kA, 1-sA/kA)
     * @since 15.06.18
     */
    GL_ONE_MINUS_SRC_ALPHA(GL11.GL_ONE_MINUS_SRC_ALPHA),
    /**
     * (fR, fG, fB, fA) = (dA/kA, dA/kA, dA/kA, dA/kA)
     * @since 15.06.18
     */
    GL_DST_ALPHA(GL11.GL_DST_ALPHA),
    /**
     * (fR, fG, fB, fA) = (1-dA/kA, 1-dA/kA, 1-dA,kA, 1-dA/kA)
     * @since 15.06.18
     */
    GL_ONE_MINUS_DST_ALPHA(GL11.GL_ONE_MINUS_DST_ALPHA),
    /**
     * (fR, fG, fB, fA) = (cR, cG, cB, cA)
     * @since 15.06.18
     */
    GL_CONSTANT_COLOR(GL11.GL_CONSTANT_COLOR),
    /**
     * (fR, fG, fB, fA) = (1-cR, 1-cG, 1-cB, 1-cA)
     * @since 15.06.18
     */
    GL_ONE_MINUS_CONSTANT_COLOR(GL11.GL_ONE_MINUS_CONSTANT_COLOR),
    /**
     * (fR, fG, fB, fA) = (cA, cA, cA, cA)
     * @since 15.06.18
     */
    GL_CONSTANT_ALPHA(GL11.GL_CONSTANT_ALPHA),
    /**
     * (fR, fG, fB, fA) = (1-cA, 1-cA, 1-cA, 1-cA)
     * @since 15.06.18
     */
    GL_ONE_MINUS_CONSTANT_ALPHA(GL11.GL_ONE_MINUS_CONSTANT_ALPHA),
    /**
     * (fR, fG, fB, fA) = (i, i, i, a)
     * @since 15.06.18
     */
    GL_SRC_ALPHA_SATURATE(GL11.GL_SRC_ALPHA_SATURATE);
    final int value;

    GLBlendFunc(final int value) {
        this.value = value;
    }

    public static GLBlendFunc valueOf(final int value) {
        for (GLBlendFunc func : values()) {
            if (func.value == value) {
                return func;
            }
        }

        return null;
    }
}
