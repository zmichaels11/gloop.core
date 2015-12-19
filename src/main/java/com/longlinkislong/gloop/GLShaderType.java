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
 * Types of shader programs.
 *
 * @author zmichaels
 * @since 15.05.27
 */
public enum GLShaderType {

    /**
     * Specifies the shader as a vertex shader.
     *
     * @since 15.05.27
     */
    GL_VERTEX_SHADER(35633),
    /**
     * Specifies the shader as a fragment shader.
     *
     * @since 15.05.27
     */
    GL_FRAGMENT_SHADER(35632),
    /**
     * Specifies the shader as a geometry shader.
     *
     * @since 15.05.27
     */
    GL_GEOMETRY_SHADER(36313),
    /**
     * Specifies the shader as a tessellation control shader.
     *
     * @since 15.05.27
     */
    GL_TESS_CONTROL_SHADER(36488),
    /**
     * Specifies the shader as a tessellation evaluation shader.
     *
     * @since 15.05.27
     */
    GL_TESS_EVALUATION_SHADER(36487),
    /**
     * Specifies the shader as a compute shader.
     *
     * @since 15.05.27
     */
    GL_COMPUTE_SHADER(37305);

    final int value;

    GLShaderType(final int value) {
        this.value = value;
    }
    
    /**
     * Translates a GLenum to a GLShaderType.
     * @param glEnum The GLenum value.
     * @return the GLShaderType wrapped in an Optional.
     * @since 15.12.18
     */
    public static Optional<GLShaderType> of(final int glEnum) {
        for(GLShaderType type : values()) {
            if(type.value == glEnum) {
                return Optional.of(type);
            }
        }
        
        return Optional.empty();
    }
}
