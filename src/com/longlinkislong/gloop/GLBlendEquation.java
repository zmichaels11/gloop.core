/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.longlinkislong.gloop;

import org.lwjgl.opengl.GL14;

/**
 *
 * @author zmichaels
 */
public enum GLBlendEquation {
    GL_FUNC_ADD(GL14.GL_FUNC_ADD),
    GL_FUNC_SUBTRACT(GL14.GL_FUNC_SUBTRACT),
    GL_FUNC_REVERSE_SUBTRACT(GL14.GL_FUNC_REVERSE_SUBTRACT);
    
    final int value;
    GLBlendEquation(final int value) {
        this.value = value;
    }
}
