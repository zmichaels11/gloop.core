/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.longlinkislong.gloop;

import org.lwjgl.opengl.GL15;

/**
 * Targets that a GLBuffer may be bound to. Most buffer functions do not
 * actually care what target the buffer is bound to and buffers can be bound to
 * multiple targets. However there are some methods that are picky.
 * GL_ARRAY_BUFFER and GL_ELEMENT_BUFFER are accepted by most buffer
 * calls/queries.
 *
 * @author zmichaels
 * @since 15.05.27
 */
public enum GLBufferTarget {

    /**
     * An array buffer is used generally for vertex data. It can also be used as
     * a generic target.
     *
     * @since 15.05.27
     */
    GL_ARRAY_BUFFER(GL15.GL_ARRAY_BUFFER),
    /**
     * A uniform buffer allows buffers to be used to transport bulk uniform data
     * to a shader program.
     *
     * @since 15.05.27
     */
    GL_UNIFORM_BUFFER(0xA11),
    GL_ATOMIC_COUNTER_BUFFER(0x92C0),
    GL_QUERY_BUFFER(0x9192),
    /**
     * A copy read buffer is the buffer that is read from in a copy task.
     *
     * @since 15.05.27
     */
    GL_COPY_READ_BUFFER(0x8F36),
    /**
     * A copy write buffer is a buffer that is written to in a copy task.
     *
     * @since 15.05.27
     */
    GL_COPY_WRITE_BUFFER(0x8F37),
    GL_DISPATCH_INDIRECT_BUFFER(0x90EE),
    /**
     * A draw indirect buffer is a buffer that stores draw indirect commands.
     *
     * @since 15.05.27
     */
    GL_DRAW_INDIRECT_BUFFER(0x8F3F),
    /**
     * An element array buffer is a buffer that specifies the order of vertex
     * data.
     *
     * @since 15.05.27
     */
    GL_ELEMENT_ARRAY_BUFFER(0x8893),
    GL_TEXTURE_BUFFER(0x8C2A),
    GL_PIXEL_PACK_BUFFER(0x88EB),
    GL_PIXEL_UNPACK_BUFFER(0x88EC),
    /**
     * A shader storage buffer is a buffer that can be read directly from a
     * shader. This functions similarly to a uniform buffer, except with much
     * larger size.
     *
     * @since 15.05.27
     */
    GL_SHADER_STORAGE_BUFFER(0x90D2),
    /**
     * A transform feedback buffer is a buffer that records the output of a
     * transform feedback.
     *
     * @since 15.05.27
     */
    GL_TRANSFORM_FEEDBACK_BUFFER(0x8C8E);

    final int value;

    GLBufferTarget(final int value) {
        this.value = value;
    }

    /**
     * Converts an OpenGL GLenum value to a GLBufferTarget constant. This method
     * will return null if no GLBufferTarget constant matches the GLenum value.
     *
     * @param value the GLenum value to convert.
     * @return the GLBufferTarget constant or null.
     * @since 15.05.27
     */
    public static GLBufferTarget valueOf(final int value) {
        for (GLBufferTarget tgt : values()) {
            if (tgt.value == value) {
                return tgt;
            }
        }

        return null;
    }
}
