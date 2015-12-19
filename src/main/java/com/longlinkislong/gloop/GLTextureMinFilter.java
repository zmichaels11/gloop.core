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
 * The supported types of filters for texture minification.
 *
 * @author zmichaels
 * @since 15.12.18
 */
public enum GLTextureMinFilter {
    /**
     * Minifying a texture selects the nearest pixel.
     *
     * @since 15.12.18
     */
    GL_NEAREST(9728),
    /**
     * Minifying a texture selects the nearest pixels and calculates the
     * weighted average.
     *
     * @since 15.12.18
     */
    GL_LINEAR(9729),
    /**
     * Minifying a texture selects the nearest pixel from the nearest mipmap.
     *
     * @since 15.12.8
     */
    GL_NEAREST_MIPMAP_NEAREST(9984),
    /**
     * Minifying a texture selects the nearest pixel from the average of nearest
     * mipmaps.
     *
     * @since 15.12.18
     */
    GL_LINEAR_MIPMAP_NEAREST(9985),
    /**
     * Minifying a texture selects the average of nearest pixels from the
     * nearest mipmap.
     *
     * @since 15.12.18
     */
    GL_NEAREST_MIPMAP_LINEAR(9986),
    /**
     * Minifying a texture selects the average of nearest pixels fron the
     * average of nearest mipmaps.
     * @since 15.12.18
     */
    GL_LINEAR_MIPMAP_LINEAR(9987);

    final int value;

    GLTextureMinFilter(final int value) {
        this.value = value;
    }

    /**
     * Translates a GLenum into the corresponding GLTextureMinFilter.
     *
     * @param glEnum the GLenum.
     * @return the GLTextureMinFilter wrapped in an optional.
     * @since 15.12.18
     */
    public static Optional<GLTextureMinFilter> of(final int glEnum) {
        for (GLTextureMinFilter filter : values()) {
            if (filter.value == glEnum) {
                return Optional.of(filter);
            }
        }

        return Optional.empty();
    }
}
