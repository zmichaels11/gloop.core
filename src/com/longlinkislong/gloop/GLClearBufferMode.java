/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.longlinkislong.gloop;

import org.lwjgl.opengl.GL11;

/**
 * Different types of clear operations.
 * @author zmichaels
 * @since 15.06.18
 */
public enum GLClearBufferMode {
    /**
     * Clears the color buffer attachment of the current framebuffer.
     * @since 15.06.18
     */
    GL_COLOR_BUFFER_BIT(GL11.GL_COLOR_BUFFER_BIT),
    /**
     * Clears the depth buffer attachment of the current framebuffer.
     * @since 15.06.18
     */
    GL_DEPTH_BUFFER_BIT(GL11.GL_DEPTH_BUFFER_BIT),
    /**
     * Clears the stencil buffer attachment of the current framebuffer.
     * @since 15.06.18
     */
    GL_STENCIL_BUFFER_BIT(GL11.GL_STENCIL_BUFFER_BIT);
    
    final int value;
    
    GLClearBufferMode(final int value) {
        this.value = value;
    }
}
