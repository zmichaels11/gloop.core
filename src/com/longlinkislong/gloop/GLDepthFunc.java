/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.longlinkislong.gloop;

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
    GL_NEVER(512),
    /**
     * The depth comparison always passes.
     *
     * @since 15.06.18
     */
    GL_ALWAYS(519),
    /**
     * The depth comparison only passes if the test is less.
     */
    GL_LESS(513),
    /**
     * The depth comparison only passes if the test is greater.
     * @since 15.06.18
     */
    GL_GREATER(516),
    /**
     * The depth comparison only passes if the inputs are equal.
     * @since 15.06.18
     */
    GL_EQUAL(514),
    /**
     * The depth comparison only passes when the test is less than or equal.
     * @since 15.06.18
     */
    GL_LEQUAL(515),
    /**
     * The depth comparison only passes when the test is greater or equal.
     * @since 15.06.18
     */
    GL_GEQUAL(518),
    /**
     * The depth comparison only passes when the inputs are not equal.
     *
     * @since 15.06.18
     */
    GL_NOTEQUAL(517);
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
