/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.longlinkislong.gloop;

import java.util.Optional;

/**
 * Draw mode specifies how the vertex data is converted into polygons.
 *
 * @author zmichaels
 * @since 15.05.27
 */
public enum GLDrawMode {

    /**
     * Draw mode in which each vertex element is its own point.
     *
     * @since 15.05.27
     */
    GL_POINTS(0),
    GL_PATCHES(14),
    /**
     * Draw mode in which each vertex element is a point forming a continuous
     * line.
     *
     * @since 15.05.27
     */
    GL_LINE_STRIP(3),
    /**
     * Draw mode in which each vertex element is a point forming a continuous
     * line. The first vertex element is reused as the last element.
     *
     * @since 15.05.27
     */
    GL_LINE_LOOP(2),
    /**
     * Draw mode in which a series of triangles are defined by reusing the last
     * two vertex elements.
     *
     * @since 15.05.27
     */
    GL_TRIANGLE_STRIP(5),
    GL_TRIANGLE_FAN(6),
    /**
     * Draw mode in which every two vertex elements make up a line.
     *
     * @since 15.05.27
     */
    GL_LINES(1),
    /**
     * Draw mode in which every three vertex elements make up a triangle.
     *
     * @since 15.05.27
     */
    GL_TRIANGLES(4),
    GL_LINES_ADJACENCY(10),
    GL_TRIANGLES_ADJACENCY(12),
    GL_LINE_STRIP_ADJACENCY(11),
    GL_TRIANGLE_STRIP_ADJACENCY(13);

    final int value;

    GLDrawMode(final int value) {
        this.value = value;
    }

    /**
     * Converts an OpenGL GLenum to a GLDrawMode constant. If there is no
     * matching GLDrawMode constant, then null is returned.
     *
     * @param value the GLenum value.
     * @return the GLDrawMode constant or null.
     * @since 15.05.27
     */
    @Deprecated
    public static GLDrawMode valueOf(final int value) {
        return of(value).get();
    }
    
    public static Optional<GLDrawMode> of(final int glEnum) {
        for(GLDrawMode mode : values()) {
            if(mode.value == glEnum) {
                return Optional.of(mode);
            }
        }
        
        return Optional.empty();
    }
}
