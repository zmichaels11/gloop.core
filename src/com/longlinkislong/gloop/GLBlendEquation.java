/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.longlinkislong.gloop;

import org.lwjgl.opengl.GL14;

/**
 * Types of blend equations.
 * @author zmichaels
 * @since 15.06.18
 */
public enum GLBlendEquation {
    /**
     * Uses additive blending
     * @since 15.06.18
     */
    GL_FUNC_ADD(GL14.GL_FUNC_ADD),
    /**
     * Uses subtractive blending
     * @since 15.06.18
     */
    GL_FUNC_SUBTRACT(GL14.GL_FUNC_SUBTRACT),
    GL_FUNC_REVERSE_SUBTRACT(GL14.GL_FUNC_REVERSE_SUBTRACT);
    
    final int value;
    GLBlendEquation(final int value) {
        this.value = value;                  
    }
}
