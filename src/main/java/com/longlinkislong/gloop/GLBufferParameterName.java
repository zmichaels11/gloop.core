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
    GL_BUFFER_SIZE(34660),
    /**
     * Query for requesting if the buffer is mapped. Returned value will be
     * either GL_TRUE or GL_FALSE.
     *
     * @since 15.05.27
     */
    GL_BUFFER_MAPPED(35004),
    /**
     * Query requesting the offset for the mapped buffer.
     *
     * @since 15.05.27
     */
    GL_BUFFER_MAP_OFFSET(37153),
    /**
     * Query requesting the usage of the mapped buffer.
     *
     * @since 15.05.27
     */
    GL_BUFFER_USAGE(34661),
    /**
     * Query requesting the length of the mapped segment in basic machine units;
     * probably bytes.
     *
     * @since 15.05.27
     */
    GL_BUFFER_MAP_LENGTH(37152);

    final int value;

    GLBufferParameterName(final int value) {
        this.value = value;
    }

    /**
     * Translates the GLenum into the corresponding GLBufferParameterName.
     *
     * @param glEnum the GLenum value.
     * @return the GLBufferParameterName wrapped in an Optional.
     * @since 15.12.18
     */
    public static Optional<GLBufferParameterName> of(final int glEnum) {
        for (GLBufferParameterName pName : values()) {
            if (pName.value == glEnum) {
                return Optional.of(pName);
            }
        }

        return Optional.empty();
    }
}
