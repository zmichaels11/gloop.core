/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.longlinkislong.gloop;

import java.util.Optional;

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
    
    @Deprecated
    public static GLBlendEquation valueOf(final int val) {
        return of(val).get();
    }
    
    public static Optional<GLBlendEquation> of(final int glEnum) {
        for(GLBlendEquation eq : values()) {
            if(eq.value == glEnum) {
                return Optional.of(eq);
            }
        }
        
        return Optional.empty();
    }
}
