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
 * The order that points are read for drawing a polygon.
 *
 * @author zmichaels
 * @since 15.08.05
 */
public enum GLFrontFaceMode {

    /**
     * Clock-Wise order
     *
     * @since 15.08.05
     */
    GL_CW(2304),
    /**
     * Counter-Clock-Wise order.
     *
     * @since 15.08.05
     */
    GL_CCW(2305);
    final int value;

    GLFrontFaceMode(final int value) {
        this.value = value;
    }

    /**
     * Translates the GLenum into the corresponding GLFrontFaceMode.
     *
     * @param glEnum the GLenum value.
     * @return the GLFrontFaceMode wrapped in an Optional.
     * @since 15.12.18
     */
    public static Optional<GLFrontFaceMode> of(final int glEnum) {
        for (GLFrontFaceMode mode : values()) {
            if (mode.value == glEnum) {
                return Optional.of(mode);
            }
        }

        return Optional.empty();
    }
}
