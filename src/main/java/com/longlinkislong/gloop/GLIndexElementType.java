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
     * Translates a GLenum into the corresponding GLIndexElementType.
     *
     * @param glEnum the GLenum value.
     * @return the GLIndexElementType wrapped in an Optional.
     * @since 15.12.18
     */
    public static Optional<GLIndexElementType> of(final int glEnum) {
        for (GLIndexElementType type : values()) {
            if (type.value == glEnum) {
                return Optional.of(type);
            }
        }

        return Optional.empty();
    }
}
