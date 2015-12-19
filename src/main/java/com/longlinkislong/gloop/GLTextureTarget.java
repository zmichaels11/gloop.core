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
 * Supported targets for GLTexture.
 * @author zmichaels
 * @since 15.12.19
 */
public enum GLTextureTarget {
    GL_TEXTURE_1D(3552),
    GL_TEXTURE_2D(3553),
    GL_TEXTURE_1D_ARRAY(35864),
    GL_TEXTURE_2D_ARRAY(35866),
    GL_TEXTURE_3D(32879),
    GL_TEXTURE_RECTANGLE(34037),
    GL_TEXTURE_BUFFER(35882),
    GL_TEXTURE_CUBE_MAP(34067),
    GL_TEXTURE_CUBE_MAP_ARRAY(36873),
    GL_TEXTURE_2D_MULTISAMPLE(37120),
    GL_TEXTURE_2D_MULTISAMPLE_ARRAY(37122);

    final int value;

    GLTextureTarget(final int value) {
        this.value = value;
    }

    /**
     * Translates a GLenum into the corresponding GLTextureTarget.
     *
     * @param glEnum the GLenum value.
     * @return the GLTextureTarget wrapped in an Optional.
     * @since 15.12.17
     */
    public static Optional<GLTextureTarget> of(int glEnum) {
        for (GLTextureTarget tgt : values()) {
            if (tgt.value == glEnum) {
                return Optional.of(tgt);
            }
        }

        return Optional.empty();
    }
}
