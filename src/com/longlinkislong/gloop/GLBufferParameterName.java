/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.longlinkislong.gloop;

import org.lwjgl.opengl.GL15;
import org.lwjgl.opengl.GL30;

/**
 * Parameter queries that can be requested
 *
 * @author zmichaels
 * @since 15.05.27
 */
public enum GLBufferParameterName {

    /**
     * Query for requesting the size of the buffer in basic machine units;
     * probably bytes.
     *
     * @since 15.05.27
     */
    GL_BUFFER_SIZE(GL15.GL_BUFFER_SIZE),
    /**
     * Query for requesting if the buffer is mapped. Returned value will be
     * either GL_TRUE or GL_FALSE.
     *
     * @since 15.05.27
     */
    GL_BUFFER_MAPPED(GL15.GL_BUFFER_MAPPED),
    /**
     * Query requesting the offset for the mapped buffer.
     *
     * @since 15.05.27
     */
    GL_BUFFER_MAP_OFFSET(GL30.GL_BUFFER_MAP_OFFSET),
    /**
     * Query requesting the usage of the mapped buffer.
     *
     * @since 15.05.27
     */
    GL_BUFFER_USAGE(GL15.GL_BUFFER_USAGE),
    /**
     * Query requesting the length of the mapped segment in basic machine units;
     * probably bytes.
     *
     * @since 15.05.27
     */
    GL_BUFFER_MAP_LENGTH(GL30.GL_BUFFER_MAP_LENGTH);

    final int value;

    GLBufferParameterName(final int value) {
        this.value = value;
    }

    /**
     * Converts an OpenGL GLenum to a GLBufferParameterName constant. This
     * method will return null if the value does not match any of the
     * GLBufferParameterName constants.
     *
     * @param value the GLenum value.
     * @return the GLBufferParameterName constant or null.
     * @since 15.05.27
     */
    public static final GLBufferParameterName valueOf(final int value) {
        for (GLBufferParameterName pName : values()) {
            if (pName.value == value) {
                return pName;
            }
        }

        return null;
    }
}
