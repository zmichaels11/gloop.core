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
 * Supported OpenGL data types.
 *
 * @author zmichaels
 * @since 15.12.18
 */
public enum GLType {

    /**
     * Unsigned 8-bit type.
     *
     * @since 15.12.18
     */
    GL_UNSIGNED_BYTE(5121, false, false, 1),
    /**
     * Signed 8-bit type.
     *
     * @since 15.12.18
     */
    GL_BYTE(5120, true, false, 1),
    /**
     * Unsigned 16-bit type.
     *
     * @since 15.12.18
     */
    GL_UNSIGNED_SHORT(5123, false, false, 2),
    /**
     * Signed 16-bit type.
     *
     * @since 15.12.18
     */
    GL_SHORT(5122, true, false, 2),
    /**
     * Unsigned 32-bit type.
     *
     * @since 15.12.18
     */
    GL_UNSIGNED_INT(5125, false, false, 4),
    /**
     * Signed 32-bit type.
     *
     * @since 15.12.18
     */
    GL_INT(5124, true, false, 4),
    /**
     * 32-bit IEEE-754 floating-point type.
     *
     * @since 15.12.18
     */
    GL_FLOAT(5126, true, true, 4),
    /**
     * 64-bit IEEE-754 floating-point type.
     *
     * @since 15.12.18
     */
    GL_DOUBLE(5130, true, true, 8),
    /**
     * 8-bit packed type. This indicates that a set of 3 values are packed into
     * 8-bits as (unsigned) {3-bits, 3-bits, 2-bits}.
     *
     * @since 15.12.18
     */
    GL_UNSIGNED_BYTE_3_3_2(32818, false, false, 1),
    /**
     * 8-bit packed type. This is the DirectX version of GL_UNSIGNED_BYTE_3_3_2.
     *
     * @since 15.12.18
     */
    GL_UNSIGNED_BYTE_2_3_3_REV(33634, false, false, 1),
    /**
     * 16-bit packed type. This indicates that a set of 3 values are packed into
     * 16-bits as (unsigned) {5-bits, 6-bits, 5-bits}.
     *
     * @since 15.12.18
     */
    GL_UNSIGNED_SHORT_5_6_5(33635, false, false, 2),
    /**
     * 16-bit packed type. This is the DirectX version of
     * GL_UNSIGNED_SHORT_5_6_5.
     *
     * @since 15.12.18
     */
    GL_UNSIGNED_SHORT_5_6_5_REV(33635, false, false, 2),
    /**
     * 16-bit packed type. This indicates that a set of 4 values are packed into
     * 16-bits as (unsigned) {4-bits, 4-bits, 4-bits, 4-bits}.
     *
     * @since 15.12.18
     */
    GL_UNSIGNED_SHORT_4_4_4_4(32819, false, false, 2),
    /**
     * 16-bit packed type. This is the DirectX version of
     * GL_UNSIGNED_SHORT_4_4_4_4_REV.
     *
     * @since 15.12.18
     */
    GL_UNSIGNED_SHORT_4_4_4_4_REV(33637, false, false, 2),
    /**
     * 16-bit packed type. This indicates that a set of 4 values are packed into
     * 16-bits as (unsigned) {5-bits, 5-bits, 5-bits, 1-bit}.
     *
     * @since 15.12.18
     */
    GL_UNSIGNED_SHORT_5_5_5_1(32820, false, false, 2),
    /**
     * 16-bit packed type. This is the DirectX version of
     * GL_UNSIGNED_SHORT_5_5_5_1.
     *
     * @since 15.12.18
     */
    GL_UNSIGNED_SHORT_1_5_5_5_REV(33638, false, false, 2),
    /**
     * 32-bit packed type. This indicates that a set of 4 values are packed into
     * 32-bits as (unsigned) {8-bits, 8-bits, 8-bits, 8-bits}. This is
     * effectively similar to using GL_UNSIGNED_BYTE in reference to texture
     * storage.
     *
     * @since 15.12.18
     */
    GL_UNSIGNED_INT_8_8_8_8(32821, false, false, 4),
    /**
     * 32-bit packed type. This is the DirectX version of
     * GL_UNSIGNED_INT_8_8_8_8_REV.
     *
     * @since 15.12.18
     */
    GL_UNSIGNED_INT_8_8_8_8_REV(33639, false, false, 4),
    /**
     * 32-bit packed type. This indicates that a set of 4-values are packed into
     * 32-bits as (unsigned) {10-bits, 10-bits, 10-bits, 2-bits}.
     *
     * @since 15.12.18
     */
    GL_UNSIGNED_INT_10_10_10_2(32822, false, false, 4),
    /**
     * 32-bit packed type. This is the DirectX version of
     * GL_UNSIGNED_INT_10_10_10_2.
     *
     * @since 15.12.18
     */
    GL_UNSIGNED_INT_2_10_10_10_REV(33640, false, false, 4);

    final int value;
    final boolean isSigned;
    final boolean isFloat;
    public final int width;

    GLType(final int value, final boolean signed, final boolean isFloat, final int bytes) {
        this.value = value;
        this.isSigned = signed;
        this.isFloat = isFloat;
        this.width = bytes;
    }

    /**
     * Converts a GLenum value for type into an instance of GLType.
     *
     * @param glEnum the GLenum value.
     * @return an Optional that contains the equivalent GLType.
     * @since 15.09.23
     */
    public static Optional<GLType> of(final int glEnum) {
        for (GLType type : values()) {
            if (type.value == glEnum) {
                return Optional.of(type);
            }
        }

        return Optional.empty();
    }
}
