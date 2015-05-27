/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.longlinkislong.gloop;

import org.lwjgl.opengl.GL20;
import org.lwjgl.opengl.GL32;
import org.lwjgl.opengl.GL40;
import org.lwjgl.opengl.GL43;

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
    GL_VERTEX_SHADER(GL20.GL_VERTEX_SHADER),
    /**
     * Specifies the shader as a fragment shader.
     *
     * @since 15.05.27
     */
    GL_FRAGMENT_SHADER(GL20.GL_FRAGMENT_SHADER),
    /**
     * Specifies the shader as a geometry shader.
     *
     * @since 15.05.27
     */
    GL_GEOMETRY_SHADER(GL32.GL_GEOMETRY_SHADER),
    /**
     * Specifies the shader as a tessellation control shader.
     *
     * @since 15.05.27
     */
    GL_TESS_CONTROL_SHADER(GL40.GL_TESS_CONTROL_SHADER),
    /**
     * Specifies the shader as a tessellation evaluation shader.
     *
     * @since 15.05.27
     */
    GL_TESS_EVALUATION_SHADER(GL40.GL_TESS_EVALUATION_SHADER),
    /**
     * Specifies the shader as a compute shader.
     *
     * @since 15.05.27
     */
    GL_COMPUTE_SHADER(GL43.GL_COMPUTE_SHADER);

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
    public static GLShaderType valueOf(final int value) {
        for (GLShaderType sType : values()) {
            if (sType.value == value) {
                return sType;
            }
        }

        return null;
    }
}
