/* 
 * Copyright (c) 2015, longlinkislong.com
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 *
 * * Redistributions of source code must retain the above copyright notice, this
 *   list of conditions and the following disclaimer.
 * * Redistributions in binary form must reproduce the above copyright notice,
 *   this list of conditions and the following disclaimer in the documentation
 *   and/or other materials provided with the distribution.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS"
 * AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE
 * IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE
 * ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT HOLDER OR CONTRIBUTORS BE
 * LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR
 * CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF
 * SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS
 * INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN
 * CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE)
 * ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE
 * POSSIBILITY OF SUCH DAMAGE.
 */
package com.longlinkislong.gloop;

import java.util.Optional;

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
    GL_MAP_READ(1),
    /**
     * Indicates that the returned pointer may be used to modify buffer object
     * data.
     *
     * @since 15.06.23
     */
    GL_MAP_WRITE(2),
    /**
     * Indicates that the previous contents of the specific range may be
     * discarded.
     *
     * @since 15.06.23
     */
    GL_MAP_INVALIDATE_RANGE(4),
    /**
     * Indicates that the previous contents of the entire buffer may be
     * discarded.
     *
     * @since 15.06.23
     */
    GL_MAP_INVALIDATE_BUFFER(8),
    /**
     * Indicates that one or more discrete subranges of the mapping may be
     * modified.
     *
     * @since 15.06.23
     */
    GL_MAP_FLUSH_EXPLICIT(16),
    /**
     * Indicates that OpenGL should not attempt to synchronize pending
     * operations on the buffer prior to returning from glMapBufferRange.
     *
     * @since 15.06.23
     */
    GL_MAP_UNSYNCHRONIZED(32),
    /**
     * Indicates that the mapping is to be made in a persistent fashion and that
     * the client intends to hold and use the returned pointer during subsequent
     * OpenGL operations.
     *
     * Requires OpenGL 4.4 support
     *
     * @since 15.06.23
     */
    GL_MAP_PERSISTENT(64),
    /**
     * Indicates that a persistent mapping is also to be coherent. Coherent maps
     * guarantee that the effects of writes to a buffer's data store by either
     * the client or server will eventually become visible to the other without
     * further intervention from the application.
     *
     * @since 15.06.23
     */
    GL_MAP_COHERENT(128);

    final int value;

    GLBufferAccess(final int value) {
        this.value = value;
    }

    /**
     * Translates the GLenum value into the corresponding GLBufferAccess.
     *
     * @param glEnum the GLenum value.
     * @return the GLBufferAccess wrapped in an Optional.
     * @since 15.12.18
     */
    public static Optional<GLBufferAccess> of(final int glEnum) {
        for (GLBufferAccess access : values()) {
            if (access.value == glEnum) {
                return Optional.of(access);
            }
        }

        return Optional.empty();
    }
}
