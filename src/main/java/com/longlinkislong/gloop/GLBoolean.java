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
 * The possible values of a GLenum that represents a boolean value.
 *
 * @author zmichaels
 * @since 15.05.27
 */
public enum GLBoolean {

    /**
     * True. Value is probably 1.
     *
     * @since 15.05.27
     */
    GL_TRUE(1),
    /**
     * False. Value is probably 0.
     *
     * @since 15.05.27
     */
    GL_FALSE(0);

    final int value;

    GLBoolean(final int value) {
        this.value = value;
    }

    /**
     * Translates a GLenum into the corresponding GLBoolean object.
     *
     * @param glEnum the GLenum value.
     * @return the GLBoolean object wrapped in an Optional.
     * @since 15.12.18
     */
    public static Optional<GLBoolean> of(final int glEnum) {
        if (glEnum == GL_TRUE.value) {
            return Optional.of(GL_TRUE);
        } else if (glEnum == GL_FALSE.value) {
            return Optional.of(GL_FALSE);
        } else {
            return Optional.empty();
        }
    }
}
