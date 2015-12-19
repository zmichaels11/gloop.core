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
 * The comparison function used for depth comparison.
 *
 * @author zmichaels
 * @since 15.06.18
 */
public enum GLDepthFunc {

    /**
     * The depth comparison always fails.
     *
     * @since 15.06.18
     */
    GL_NEVER(512),
    /**
     * The depth comparison always passes.
     *
     * @since 15.06.18
     */
    GL_ALWAYS(519),
    /**
     * The depth comparison only passes if the test is less.
     */
    GL_LESS(513),
    /**
     * The depth comparison only passes if the test is greater.
     *
     * @since 15.06.18
     */
    GL_GREATER(516),
    /**
     * The depth comparison only passes if the inputs are equal.
     *
     * @since 15.06.18
     */
    GL_EQUAL(514),
    /**
     * The depth comparison only passes when the test is less than or equal.
     *
     * @since 15.06.18
     */
    GL_LEQUAL(515),
    /**
     * The depth comparison only passes when the test is greater or equal.
     *
     * @since 15.06.18
     */
    GL_GEQUAL(518),
    /**
     * The depth comparison only passes when the inputs are not equal.
     *
     * @since 15.06.18
     */
    GL_NOTEQUAL(517);
    final int value;

    GLDepthFunc(final int value) {
        this.value = value;
    }

    /**
     * Translates a GLenum into the corresponding GLDepthFunc.
     *
     * @param glEnum the GLenum value.
     * @return the GLDepthFunc wrapped in an Optional.
     * @since 15.12.18
     */
    public static Optional<GLDepthFunc> of(final int glEnum) {
        for (GLDepthFunc func : values()) {
            if (func.value == glEnum) {
                return Optional.of(func);
            }
        }

        return Optional.empty();
    }
}
