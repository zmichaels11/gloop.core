/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.longlinkislong.gloop;

import org.lwjgl.opengl.GL15;

/**
 * Buffer usage refers to how the buffer will be used when allocated in memory.
 * These exist as hints for the video unit and are not guaranteed to do anything
 * different.
 *
 * @author zmichaels
 * @since 15.05.27
 */
public enum GLBufferUsage {

    /**
     * Stream draw usage marks the buffer for frequent write changes.
     *
     * @since 15.05.27
     */
    GL_STREAM_DRAW(GL15.GL_STREAM_DRAW),
    GL_STREAM_READ(GL15.GL_STREAM_READ),
    GL_STREAM_COPY(GL15.GL_STREAM_COPY),
    /**
     * Static draw usage marks the buffer for infrequent write changes.
     *
     * @since 15.05.27
     */
    GL_STATIC_DRAW(GL15.GL_STATIC_DRAW),
    GL_STATIC_READ(GL15.GL_STATIC_READ),
    GL_STATIC_COPY(GL15.GL_STATIC_COPY),
    /**
     * Dynamic draw usage marks the buffer for semi-frequent or partial write
     * changes.
     *
     * @since 15.05.27
     */
    GL_DYNAMIC_DRAW(GL15.GL_DYNAMIC_DRAW),
    GL_DYNAMIC_READ(GL15.GL_DYNAMIC_READ),
    GL_DYNAMIC_COPY(GL15.GL_DYNAMIC_COPY);

    final int value;

    GLBufferUsage(final int value) {
        this.value = value;
    }

    /**
     * Converts an OpenGL GLenum value to a GLBufferUsage constant. If no
     * GLBufferUsage constant matches the supplied GLenum value, then null will
     * be returned.
     *
     * @param value the GLenum value.
     * @return the GLBufferUsage constant or null.
     * @since 15.05.27
     */
    public static GLBufferUsage valueOf(final int value) {
        for (GLBufferUsage bu : values()) {
            if (bu.value == value) {
                return bu;
            }
        }

        return null;
    }
}
