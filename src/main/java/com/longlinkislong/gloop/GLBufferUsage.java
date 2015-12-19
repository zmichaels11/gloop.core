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
 * Buffer usage refers to how the buffer will be used when allocated in memory.
 * These exist as hints for the video unit and are not guaranteed to do anything
 * different.
 *
 * <a href="https://www.opengl.org/wiki/GLAPI/glBufferData#Description">glBufferData,
 * Description</a>
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
    GL_STREAM_DRAW(35040),
    GL_STREAM_READ(35041),
    GL_STREAM_COPY(35042),
    /**
     * Static draw usage marks the buffer for infrequent write changes.
     *
     * @since 15.05.27
     */
    GL_STATIC_DRAW(35044),
    GL_STATIC_READ(35045),
    GL_STATIC_COPY(35046),
    /**
     * Dynamic draw usage marks the buffer for semi-frequent or partial write
     * changes.
     *
     * @since 15.05.27
     */
    GL_DYNAMIC_DRAW(35048),
    GL_DYNAMIC_READ(35049),
    GL_DYNAMIC_COPY(35050);

    final int value;

    GLBufferUsage(final int value) {
        this.value = value;
    }

    /**
     * Translates the GLenum into the corresponding GLBufferUsage object.
     *
     * @param glEnum the GLenum value.
     * @return the GLBufferUsage wrapped in an Optional.
     * @since 15.12.18
     */
    public static Optional<GLBufferUsage> of(final int glEnum) {
        for (GLBufferUsage usage : values()) {
            if (usage.value == glEnum) {
                return Optional.of(usage);
            }
        }

        return Optional.empty();
    }
}
