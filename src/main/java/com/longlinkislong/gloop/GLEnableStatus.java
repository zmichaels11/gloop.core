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
import org.lwjgl.opengl.GL11;

/**
 * The status of an OpenGL state. This mostly corresponds to GL_TRUE and
 * GL_FALSE, however it is exclusively in reference to OpenGL states that may be
 * enabled or disabled.
 *
 * @author zmichaels
 * @since 15.08.05
 */
public enum GLEnableStatus {

    /**
     * The state or feature is enabled.
     *
     * @since 15.08.05
     */
    GL_ENABLED(GL11.GL_TRUE),
    /**
     * The state or feature is disabled.
     *
     * @since 15.08.05
     */
    GL_DISABLED(GL11.GL_FALSE);

    final int value;

    GLEnableStatus(final int value) {
        this.value = value;
    }

    /**
     * Translates a GLenum value to the corresponding GLEnableStatus.
     *
     * @param glEnum the value.
     * @return the corresponding type. An empty Optional is returned when the
     * glEnum is an invalid value.
     * @since 16.06.10
     */
    public static Optional<GLEnableStatus> of(final int glEnum) {
        switch (glEnum) {
            case GL11.GL_TRUE:
                return Optional.of(GL_ENABLED);
            case GL11.GL_FALSE:
                return Optional.of(GL_DISABLED);
            default:
                return Optional.empty();
        }
    }
}
