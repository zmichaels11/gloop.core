/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.longlinkislong.gloop;

import org.lwjgl.opengl.GL30;
import org.lwjgl.opengl.GL44;

/**
 * Access settings for a mapped GLBuffer.
 *
 * @see
 * <a href="https://www.opengl.org/wiki/GLAPI/glMapBufferRange#Description">glMapBufferRange</a>
 * @author zmichaels
 * @since 15.05.27
 */
public enum GLBufferAccess {

    /**
     * Indicates that the returned pointer may be used to read buffer object
     * data.
     *
     * @since 15.06.23
     */
    GL_MAP_READ(GL30.GL_MAP_READ_BIT),
    /**
     * Indicates that the returned pointer may be used to modify buffer object
     * data.
     *
     * @since 15.06.23
     */
    GL_MAP_WRITE(GL30.GL_MAP_WRITE_BIT),
    /**
     * Indicates that the previous contents of the specific range may be
     * discarded.
     *
     * @since 15.06.23
     */
    GL_MAP_INVALIDATE_RANGE(GL30.GL_MAP_INVALIDATE_RANGE_BIT),
    /**
     * Indicates that the previous contents of the entire buffer may be
     * discarded.
     *
     * @since 15.06.23
     */
    GL_MAP_INVALIDATE_BUFFER(GL30.GL_MAP_INVALIDATE_BUFFER_BIT),
    /**
     * Indicates that one or more discrete subranges of the mapping may be
     * modified.
     *
     * @since 15.06.23
     */
    GL_MAP_FLUSH_EXPLICIT(GL30.GL_MAP_FLUSH_EXPLICIT_BIT),
    /**
     * Indicates that OpenGL should not attempt to synchronize pending
     * operations on the buffer prior to returning from glMapBufferRange.
     *
     * @since 15.06.23
     */
    GL_MAP_UNSYNCHRONIZED(GL30.GL_MAP_UNSYNCHRONIZED_BIT),
    /**
     * Indicates that the mapping is to be made in a persistent fashion and that
     * the client intends to hold and use the returned pointer during subsequent
     * OpenGL operations.
     *
     * Requires OpenGL 4.4 support
     *
     * @since 15.06.23
     */
    GL_MAP_PERSISTENT(GL44.GL_MAP_PERSISTENT_BIT),
    /**
     * Indicates that a persistent mapping is also to be coherent. Coherent maps
     * guarantee that the effects of writes to a buffer's data store by either
     * the client or server will eventually become visible to the other without
     * further intervention from the application.
     * @since 15.06.23
     */
    GL_MAP_COHERENT(GL44.GL_MAP_COHERENT_BIT);

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
