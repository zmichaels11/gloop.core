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
 * Types of supported conditions for GLDrawQuery.
 *
 * @author zmichaels
 * @since 15.12.18
 */
public enum GLDrawQueryCondition {
    GL_ANY_SAMPLES_PASSED(35887),
    GL_ANY_SAMPLES_PASSED_CONSERVATIVE(36202),
    GL_PRIMITIVES_GENERATED(35975),
    GL_SAMPLES_PASSED(35092),
    GL_TIME_ELAPSED(35007),
    GL_TRANSFORM_FEEDBACK_PRIMITIVES_WRITTEN(35976);
    final int value;

    GLDrawQueryCondition(final int value) {
        this.value = value;
    }

    /**
     * Translates a GLenum into the corresponding GLDrawQueryCondition.
     *
     * @param glEnum the GLenum value.
     * @return the GLDrawQueryCondition wrapped in an Optional.
     * @since 15.12.18
     */
    public static Optional<GLDrawQueryCondition> of(final int glEnum) {
        for (GLDrawQueryCondition condition : values()) {
            if (condition.value == glEnum) {
                return Optional.of(condition);
            }
        }

        return Optional.empty();
    }
}
