/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.longlinkislong.gloop;

/**
 * The data type supported for index element buffer data.
 *
 * @author zmichaels
 * @since 15.05.27
 */
public enum GLIndexElementType {

    /**
     * Specifies that the data stored in the element array buffer is unsigned
     * bytes.
     *
     * @since 15.05.27
     */
    GL_UNSIGNED_BYTE(5121),
    /**
     * Specifies that the data stored in the element array buffer is unsigned
     * shorts; probably uint16.
     *
     * @since 15.05.27
     */
    GL_UNSIGNED_SHORT(5123),
    /**
     * Specifies that the data stored in the element array buffer is unsigned
     * ints; probably uint32.
     */
    GL_UNSIGNED_INT(5125);

    final int value;

    GLIndexElementType(final int value) {
        this.value = value;
    }

    /**
     * Converts an OpenGL GLenum value to a GLIndexElementType constant. If no
     * matching GLIndexElementType exists, null is then returned.
     *
     * @param value the GLenum value.
     * @return the GLIndexElementType or null.
     * @since 15.05.27
     */
    public static GLIndexElementType valueOf(final int value) {
        for (GLIndexElementType type : values()) {
            if (type.value == value) {
                return type;
            }
        }

        return null;
    }
}
