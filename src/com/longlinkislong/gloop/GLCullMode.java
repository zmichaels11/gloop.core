/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.longlinkislong.gloop;

import org.lwjgl.opengl.GL11;

/**
 * Settings for OpenGL polygon cull mode.
 * @author zmichaels
 * @since 15.06.18
 */
public enum GLCullMode {
    /**
     * Cull front-facing polygons.
     * @since 15.06.18
     */
    GL_FRONT(GL11.GL_FRONT),
    /**
     * Cull back-facing polygons.
     * @since 15.06.18
     */
    GL_BACK(GL11.GL_BACK),
    /**
     * Cull front and back facing polygons.
     * @since 15.06.18
     */
    GL_FRONT_AND_BACK(GL11.GL_FRONT_AND_BACK);
    final int value;

    GLCullMode(final int value) {
        this.value = value;
    }
    
    /**
     * Converts a GLenum to the corresponding GLCullMode constant.
     * @param value the GLenum value.
     * @return the GLCullMode constant or null if no matching constant exists.
     * @since 15.06.18
     */
    public static GLCullMode valueOf(final int value) {
        for(GLCullMode mode : values()) {
            if(mode.value == value) {
                return mode;
            }
        }
        
        return null;
    }
}
