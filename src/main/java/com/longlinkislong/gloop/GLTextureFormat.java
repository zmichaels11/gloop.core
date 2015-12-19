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
 * A collection of texture storage formats.
 *
 * @author zmichaels
 * @since 15.07.01
 */
public enum GLTextureFormat {

    GL_DEPTH_COMPONENT(6402, 2),
    GL_DEPTH_STENCIL(34041, 1),
    GL_RED_INTEGER(36244, 1),
    GL_GREEN_INTEGER(36245, 1),
    GL_BLUE_INTEGER(36246, 1),
    GL_BGRA_INTEGER(36251, 4),
    GL_RG_INTEGER(33320, 2),
    GL_RGB_INTEGER(36248, 3),
    GL_RGBA_INTEGER(36249, 4),
    GL_BGR_INTEGER(36250, 3),
    GL_STENCIL_INDEX(6146, 1),
    GL_COLOR_INDEX(6400, 1), //TODO: confirm COLOR_INDEX is 1 component
    GL_RED(6403, 1),
    GL_GREEN(6404, 1),
    GL_BLUE(6405, 1),
    GL_ALPHA(6406, 1),
    GL_RGB(6407, 3),
    GL_BGR(32992, 3),
    GL_RGBA(6408, 4),
    GL_BGRA(32993, 4),
    GL_LUMINANCE(6409, 1),
    GL_LUMINANCE_ALPHA(6410, 2); //TODO: confirm LUMINANCE_ALPHA is 2-components

    final int value;
    final int size;

    GLTextureFormat(final int value, final int size) {
        this.value = value;
        this.size = size;
    }

    /**
     * Converts a GLenum value for Texture Format into an instance of
     * GLTextureFormat.
     *
     * @param glEnum the GLenum value.
     * @return an Optional that contains the equivalent GLTextureFormat.
     * @since 15.09.23
     */
    public static Optional<GLTextureFormat> of(final int glEnum) {
        for (GLTextureFormat format : values()) {
            if (format.value == glEnum) {
                return Optional.of(format);
            }
        }

        return Optional.empty();
    }
}
