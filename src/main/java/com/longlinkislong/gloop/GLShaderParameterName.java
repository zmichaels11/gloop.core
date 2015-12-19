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
 * Supported parameters for querying the GLShader.
 *
 * @author zmichaels
 * @since 15.12.18
 */
public enum GLShaderParameterName {

    /**
     * Queries the type of GLShader.
     *
     * @since 15.12.18
     */
    GL_SHADER_TYPE(35663),
    /**
     * Queries the length of the GLShader info log.
     *
     * @since 15.12.18
     */
    GL_INFO_LOG_LENGTH(35716),
    /**
     * Queries if the GLShader has been deleted.
     *
     * @since 15.12.18
     */
    GL_DELETE_STATUS(35712),
    /**
     * Queries if the GLShader has been compiled.
     *
     * @since 15.12.18
     */
    GL_COMPILE_STATUS(35713),
    GL_COMPUTE_SHADER(37305),
    /**
     * Queries the length of the GLShader source.
     *
     * @since 15.12.18
     */
    GL_SHADER_SOURCE_LENGTH(35720);

    final int value;

    GLShaderParameterName(final int value) {
        this.value = value;
    }

    /**
     * Translates a GLenum into a GLShaderParameterName.
     *
     * @param glEnum the GLenum value.
     * @return the GLShaderParameterName wrapped in an Optional.
     * @since 15.12.18
     */
    public static Optional<GLShaderParameterName> of(final int glEnum) {
        for (GLShaderParameterName pName : values()) {
            if (pName.value == glEnum) {
                return Optional.of(pName);
            }
        }

        return Optional.empty();
    }
}
