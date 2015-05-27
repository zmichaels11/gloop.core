/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.longlinkislong.gloop;

import org.lwjgl.opengl.GL15;

/**
 * Access settings for a mapped GLBuffer
 *
 * @author zmichaels
 * @since 15.05.27
 */
public enum GLBufferAccess {

    /**
     * Marks the GLBuffer mapping for read only
     *
     * @since 15.05.27
     */
    GL_READ(GL15.GL_READ_ONLY),
    /**
     * Marks the GLBuffer mapping for read and write.
     *
     * @since 15.05.27
     */
    GL_READ_WRITE(GL15.GL_READ_WRITE);

    final int value;

    GLBufferAccess(final int value) {
        this.value = value;
    }

    /**
     * Translates an OpenGL GLenum value to a GLBufferAccess constant. This
     * method will return null if there is no associated GLBufferAccess
     * constant.
     *
     * @param value the GLenum value.
     * @return the GLBufferAccess constant or null.
     * @since 15.05.27
     */
    public static GLBufferAccess valueOf(final int value) {
        for (GLBufferAccess ba : values()) {
            if (ba.value == value) {
                return ba;
            }
        }

        return null;
    }
}
