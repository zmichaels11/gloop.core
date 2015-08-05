/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.longlinkislong.gloop;

/**
 * The possible values of a GLenum that represents a boolean value.
 *
 * @author zmichaels
 * @since 15.05.27
 */
public enum GLBoolean {

    /**
     * True. Value is probably 1.
     *
     * @since 15.05.27
     */
    GL_TRUE(1),
    /**
     * False. Value is probably 0.
     *
     * @since 15.05.27
     */
    GL_FALSE(0);

    final int value;

    GLBoolean(final int value) {
        this.value = value;
    }

    /**
     * Converts an OpenGL GLenum value to a GLBoolean constant.
     *
     * @param value the value to convert.
     * @return GL_TRUE if the value is 1.
     * @since 15.05.27
     */
    public static GLBoolean valueOf(final int value) {
        return (GL_TRUE.value == value) ? GL_TRUE : GL_FALSE;
    }
}
