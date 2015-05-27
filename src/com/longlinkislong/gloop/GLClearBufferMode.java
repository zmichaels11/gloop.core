/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.longlinkislong.gloop;

import org.lwjgl.opengl.GL11;

/**
 *
 * @author zmichaels
 */
public enum GLClearBufferMode {
    GL_COLOR_BUFFER_BIT(GL11.GL_COLOR_BUFFER_BIT),
    GL_DEPTH_BUFFER_BIT(GL11.GL_DEPTH_BUFFER_BIT),
    GL_STENCIL_BUFFER_BIT(GL11.GL_STENCIL_BUFFER_BIT);
    
    final int value;
    
    GLClearBufferMode(final int value) {
        this.value = value;
    }
}
