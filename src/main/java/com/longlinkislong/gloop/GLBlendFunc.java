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
 * Types of pixel arithmetic scale operations to perform when blending. These
 * values are multiplied by the source and destination colors and then combined
 * with a GLBlendEquation.
 *
 * @author zmichaels
 * @see
 * <a href="https://www.opengl.org/wiki/GLAPI/glBlendFuncSeparate">glBlendFunc
 * (OpenGL Wiki)</a>
 * @see
 * <a href="https://www.opengl.org/sdk/docs/man/html/glBlendFunc.xhtml">glBlendFunc
 * (OpenGL SDK)</a>
 * @since 15.06.18
 */
public enum GLBlendFunc {

    /**
     * (fR, fG, fB, fA) = (0, 0, 0, 0)
     *
     * @since 15.06.18
     */
    GL_ZERO(0),
    /**
     * (fR, fG, fB, fA) = (1, 1, 1, 1)
     *
     * @since 15.06.18
     */
    GL_ONE(1),
    /**
     * (fR, fG, fB, fA) = (sR/kR, sG/kG, sB/kB, sA/kA)
     *
     * @since 15.06.18
     */
    GL_SRC_COLOR(768),
    /**
     * (fR, fG, fB, fA) = (1-sR/kR, 1-sG/kG, 1-sB/kB, 1-sA/kA)
     *
     * @since 15.06.18
     */
    GL_ONE_MINUS_SRC_COLOR(769),
    /**
     * (fR, fG, fB, fA) = (dR/kR, dG/kG, dB/kB, dA/kA)
     *
     * @since 15.06.18
     */
    GL_DST_COLOR(774),
    /**
     * (fR, fG, fB, fA) = (1-dR/kR, 1-dG/kG, 1-dB/kB, 1-dA/kA)
     *
     * @since 15.06.18
     */
    GL_ONE_MINUS_DST_COLOR(775),
    /**
     * (fR, fG, fB, fA) = (sA/kA, sA/kA, sA/kA, sA/kA)
     *
     * @since 15.06.18
     */
    GL_SRC_ALPHA(770),
    /**
     * (fR, fG, fB, fA) = (1-sA/kA, 1-sA/kA, 1-sA/kA, 1-sA/kA)
     *
     * @since 15.06.18
     */
    GL_ONE_MINUS_SRC_ALPHA(771),
    /**
     * (fR, fG, fB, fA) = (dA/kA, dA/kA, dA/kA, dA/kA)
     *
     * @since 15.06.18
     */
    GL_DST_ALPHA(772),
    /**
     * (fR, fG, fB, fA) = (1-dA/kA, 1-dA/kA, 1-dA,kA, 1-dA/kA)
     *
     * @since 15.06.18
     */
    GL_ONE_MINUS_DST_ALPHA(773),
    /**
     * (fR, fG, fB, fA) = (cR, cG, cB, cA)
     *
     * @since 15.06.18
     */
    GL_CONSTANT_COLOR(32769),
    /**
     * (fR, fG, fB, fA) = (1-cR, 1-cG, 1-cB, 1-cA)
     *
     * @since 15.06.18
     */
    GL_ONE_MINUS_CONSTANT_COLOR(32770),
    /**
     * (fR, fG, fB, fA) = (cA, cA, cA, cA)
     *
     * @since 15.06.18
     */
    GL_CONSTANT_ALPHA(32771),
    /**
     * (fR, fG, fB, fA) = (1-cA, 1-cA, 1-cA, 1-cA)
     *
     * @since 15.06.18
     */
    GL_ONE_MINUS_CONSTANT_ALPHA(32772),
    /**
     * (fR, fG, fB, fA) = (i, i, i, a)
     *
     * @since 15.06.18
     */
    GL_SRC_ALPHA_SATURATE(776);
    final int value;

    GLBlendFunc(final int value) {
        this.value = value;
    }

    /**
     * Translates a GLenum value to GLBlendFunc.
     *
     * @param glEnum the GLenum value.
     * @return the GLBlendFunc wrapped in an Optional.
     * @since 15.12.18
     */
    public static Optional<GLBlendFunc> of(final int glEnum) {
        for (GLBlendFunc func : values()) {
            if (func.value == glEnum) {
                return Optional.of(func);
            }
        }

        return Optional.empty();
    }
}
