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
 * Supported rules for handling texture wrap.
 *
 * @author zmichaels
 * @since 15.12.18
 */
public enum GLTextureWrap {
    GL_CLAMP_TO_EDGE(33071),
    GL_MIRRORED_REPEAT(33648),
    GL_REPEAT(10497),
    GL_MIRROR_CLAMP_TO_EDGE(33648);

    final int value;

    GLTextureWrap(final int value) {
        this.value = value;
    }

    /**
     * Translates a GLenum into a GLTextureWrap.
     *
     * @param glEnum the GLenum value.
     * @return The corresponding GLTextureWrap wrapped in an Optional if it
     * exists.
     * @since 15.12.17
     */
    public static Optional<GLTextureWrap> of(final int glEnum) {
        for (GLTextureWrap wrap : values()) {
            if (wrap.value == glEnum) {
                return Optional.of(wrap);
            }
        }

        return Optional.empty();
    }
}
