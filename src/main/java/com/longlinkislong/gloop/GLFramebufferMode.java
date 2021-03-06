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
 * Different types of clear operations.
 *
 * @author zmichaels
 * @since 15.06.18
 */
public enum GLFramebufferMode {
    /**
     * Clears the color buffer attachment of the current framebuffer.
     *
     * @since 15.06.18
     */
    GL_COLOR_BUFFER_BIT(16384),
    /**
     * Clears the depth buffer attachment of the current framebuffer.
     *
     * @since 15.06.18
     */
    GL_DEPTH_BUFFER_BIT(256),
    /**
     * Clears the stencil buffer attachment of the current framebuffer.
     *
     * @since 15.06.18
     */
    GL_STENCIL_BUFFER_BIT(1024);

    final int value;

    GLFramebufferMode(final int value) {
        this.value = value;
    }

    /**
     * Translates a GLenum into the corresponding GLFramebufferMode.
     *
     * @param glEnum the GLenum value.
     * @return the GLFramebufferMode wrapped in an Optional.
     * @since 15.12.18
     */
    public static Optional<GLFramebufferMode> of(final int glEnum) {
        for (GLFramebufferMode mode : values()) {
            if (mode.value == glEnum) {
                return Optional.of(mode);
            }
        }

        return Optional.empty();
    }
}
