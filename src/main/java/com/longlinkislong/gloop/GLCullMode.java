/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.longlinkislong.gloop;

import java.util.Optional;

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
    GL_FRONT(1028),
    /**
     * Cull back-facing polygons.
     * @since 15.06.18
     */
    GL_BACK(1029),
    /**
     * Cull front and back facing polygons.
     * @since 15.06.18
     */
    GL_FRONT_AND_BACK(1032);
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
    @Deprecated
    public static GLCullMode valueOf(final int value) {
        return of(value).get();
    }
    
    public static Optional<GLCullMode> of(final int glEnum) {
        for(GLCullMode mode : values()) {
            if(mode.value == glEnum) {
                return Optional.of(mode);
            }
        }
        
        return Optional.empty();
    }
}
