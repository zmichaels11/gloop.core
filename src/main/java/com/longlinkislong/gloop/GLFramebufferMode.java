/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.longlinkislong.gloop;

import java.util.Arrays;

/**
 * Different types of clear operations.
 * @author zmichaels
 * @since 15.06.18
 */
public enum GLFramebufferMode {
    /**
     * Clears the color buffer attachment of the current framebuffer.
     * @since 15.06.18
     */
    GL_COLOR_BUFFER_BIT(16384),
    /**
     * Clears the depth buffer attachment of the current framebuffer.
     * @since 15.06.18
     */
    GL_DEPTH_BUFFER_BIT(256),
    /**
     * Clears the stencil buffer attachment of the current framebuffer.
     * @since 15.06.18
     */
    GL_STENCIL_BUFFER_BIT(1024);
    
    final int value;    
    
    GLFramebufferMode(final int value) {
        this.value = value;
    }      
    
    public static GLFramebufferMode valueOf(final int value) {
        return Arrays.stream(values())
                .filter(f -> f.value == value)
                .findAny()
                .orElseThrow(()->{
                    return new GLException.InvalidGLEnumException("Invalid GLenum: " + value);
                });
    }
}
