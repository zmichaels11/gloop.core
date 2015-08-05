/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.longlinkislong.gloop;

/**
 * Types of blend equations.
 * @author zmichaels 
 * @see <a href="https://www.opengl.org/wiki/GLAPI/glBlendEquationSeparate">glBlendEquation (OpenGL Wiki)</a>
 * @see <a href="https://www.opengl.org/sdk/docs/man/html/glBlendEquation.xhtml">glBlendEquation (OpenGL SDK)</a>
 * @since 15.06.18
 */
public enum GLBlendEquation {
    /**
     * Uses additive blending
     * @since 15.06.18
     */
    GL_FUNC_ADD(32774),
    /**
     * Uses subtractive blending
     * @since 15.06.18
     */
    GL_FUNC_SUBTRACT(32778),
    GL_FUNC_REVERSE_SUBTRACT(32779),
    GL_MIN(32775),
    GL_MAX(32776);
    
    final int value;
    GLBlendEquation(final int value) {
        this.value = value;                  
    }
}
