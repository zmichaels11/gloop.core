/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.longlinkislong.gloop;

import org.lwjgl.opengl.GL11;

/**
 * The comparison function used for depth comparison.
 *
 * @author zmichaels
 * @since 15.06.18
 */
public enum GLDepthFunc {

    /**
     * The depth comparison always fails.
     *
     * @since 15.06.18
     */
    GL_NEVER(GL11.GL_NEVER),
    /**
     * The depth comparison always passes.
     *
     * @since 15.06.18
     */
    GL_ALWAYS(GL11.GL_ALWAYS),
    /**
     * The depth comparison only passes if the test is less.
     */
    GL_LESS(GL11.GL_LESS),
    /**
     * The depth comparison only passes if the test is greater.
     * @since 15.06.18
     */
    GL_GREATER(GL11.GL_GREATER),
    /**
     * The depth comparison only passes if the inputs are equal.
     * @since 15.06.18
     */
    GL_EQUAL(GL11.GL_EQUAL),
    /**
     * The depth comparison only passes when the test is less than or equal.
     * @since 15.06.18
     */
    GL_LEQUAL(GL11.GL_LEQUAL),
    /**
     * The depth comparison only passes when the test is greater or equal.
     * @since 15.06.18
     */
    GL_GEQUAL(GL11.GL_GEQUAL),
    /**
     * The depth comparison only passes when the inputs are not equal.
     *
     * @since 15.06.18
     */
    GL_NOTEQUAL(GL11.GL_NOTEQUAL);
    final int value;

    GLDepthFunc(final int value) {
        this.value = value;
    }

    /**
     * Converts a GLenum to the corresponding GLDepthFunc constant.
     *
     * @param value the GLenum value.
     * @return the GLDepthFunc constant or null if no matching GLDepthFunc
     * exists.
     * @since 15.06.18
     */
    public static GLDepthFunc valueOf(final int value) {
        for (GLDepthFunc func : values()) {
            if (func.value == value) {
                return func;
            }
        }

        return null;
    }
}
