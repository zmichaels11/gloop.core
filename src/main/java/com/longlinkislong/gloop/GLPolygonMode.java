/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.longlinkislong.gloop;

import java.util.Optional;

/**
 * The settings for drawing polygons from vertices.
 *
 * @author zmichaels
 * @since 15.08.05
 */
public enum GLPolygonMode {

    /**
     * Specifies that polygons should be drawn by only drawing the vertices.
     *
     * @since 15.08.05
     */
    GL_POINT(6912),
    GL_LINE(6913),
    /**
     * Specifies that polygons should be drawn. This is the default operation.
     *
     * @since 15.08.05
     */
    GL_FILL(6914);
    final int value;

    GLPolygonMode(final int value) {
        this.value = value;
    }

    @Deprecated
    public static GLPolygonMode valueOf(final int value) {
        return of(value).get();
    }
    
    public static Optional<GLPolygonMode> of(final int glEnum) {
        for(GLPolygonMode mode : values()) {
            if(mode.value == glEnum) {
                return Optional.of(mode);
            }
        }
        
        return Optional.empty();
    }
}
