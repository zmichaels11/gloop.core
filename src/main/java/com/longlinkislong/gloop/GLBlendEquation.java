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
 * Types of blend equations.
 *
 * @author zmichaels
 * @see
 * <a href="https://www.opengl.org/wiki/GLAPI/glBlendEquationSeparate">glBlendEquation
 * (OpenGL Wiki)</a>
 * @see
 * <a href="https://www.opengl.org/sdk/docs/man/html/glBlendEquation.xhtml">glBlendEquation
 * (OpenGL SDK)</a>
 * @since 15.06.18
 */
public enum GLBlendEquation {
    /**
     * Uses additive blending
     *
     * @since 15.06.18
     */
    GL_FUNC_ADD(32774),
    /**
     * Uses subtractive blending
     *
     * @since 15.06.18
     */
    GL_FUNC_SUBTRACT(32778),
    GL_FUNC_REVERSE_SUBTRACT(32779),
    GL_MIN(32775),
    GL_MAX(32776);

    final int value;

    GLBlendEquation(final int value) {
        this.value = value;
    }

    /**
     * Translates a GLenum to a GLBlendEquation object.
     *
     * @param glEnum the GLenum value.
     * @return the GLBlendEquation wrapped in an Optional.
     * @since 15.12.17
     */
    public static Optional<GLBlendEquation> of(final int glEnum) {
        for (GLBlendEquation eq : values()) {
            if (eq.value == glEnum) {
                return Optional.of(eq);
            }
        }

        return Optional.empty();
    }
}
