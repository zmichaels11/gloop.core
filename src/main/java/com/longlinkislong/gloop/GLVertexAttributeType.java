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
 * Valid types for vertex attributes.
 *
 * @author zmichaels
 * @see
 * <a href="https://www.opengl.org/wiki/GLAPI/glVertexAttribPointer#Function_Definition">glVertexAttribPointer,
 * Function Definition (OpenGL Wiki)</a>
 * <a href="https://www.khronos.org/opengles/sdk/docs/man3/docbook4/xhtml/glVertexAttribPointer.xml">glVertexAttribPointer
 * (OpenGL SDK)</a>
 * @since 15.06.24
 */
public enum GLVertexAttributeType {

    /**
     * 4 byte floating point type.
     *
     * @since 15.06.24
     */
    GL_FLOAT(5126, Float.BYTES),
    /**
     * 8 byte floating point type. Not all implementations of OpenGL support
     * doubles for vertex attributes.
     *
     * @since 15.06.24
     */
    GL_DOUBLE(5130, Double.BYTES),
    /**
     * 1 byte signed integer.
     *
     * @since 15.06.24
     */
    GL_BYTE(5120, Byte.BYTES),
    /**
     * 1 byte unsigned integer.
     *
     * @since 15.06.24
     */
    GL_UNSIGNED_BYTE(5121, Byte.BYTES),
    /**
     * 2 byte signed integer.
     *
     * @since 15.06.24
     */
    GL_SHORT(5122, Short.BYTES),
    /**
     * 2 byte unsigned integer.
     *
     * @since 15.06.24
     */
    GL_UNSIGNED_SHORT(5123, Short.BYTES),
    /**
     * 4 byte signed integer.
     *
     * @since 15.06.24
     */
    GL_INT(5124, Integer.BYTES),
    /**
     * 4 byte unsigned integer
     *
     * @since 15.06.24
     */
    GL_UNSIGNED_INT(5125, Integer.BYTES);

    final int value;
    final int width;

    GLVertexAttributeType(final int value, final int width) {
        this.value = value;
        this.width = width;
    }

    /**
     * Translates the GLenum value to the corresponding GLVertexAttributeType.
     *
     * @param glEnum the GLenum value.
     * @return the GLVertexAttributeType wrapped in an Optional.
     * @since 15.12.18
     */
    public static Optional<GLVertexAttributeType> of(final int glEnum) {
        for (GLVertexAttributeType type : values()) {
            if (type.value == glEnum) {
                return Optional.of(type);
            }
        }

        return Optional.empty();
    }
}
