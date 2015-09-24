/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
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
     * Converts an OpenGL GLenum value to a GLShaderType constant. If there is
     * no associated GLShaderType to the GLenum value, null is returned.
     *
     * @param value the value to convert.
     * @return the GLShaderType.
     * @since 15.05.27
     */    
    @Deprecated
    public static GLShaderType valueOf(final int value) {
        return of(value).get();
    }
    
    public static Optional<GLShaderType> of(final int glEnum) {
        for(GLShaderType type : values()) {
            if(type.value == glEnum) {
                return Optional.of(type);
            }
        }
        
        return Optional.empty();
    }
}
