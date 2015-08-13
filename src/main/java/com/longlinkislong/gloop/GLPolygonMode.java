/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.longlinkislong.gloop;

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

    public static GLPolygonMode valueOf(final int value) {
        for (GLPolygonMode mode : values()) {
            if (mode.value == value) {
                return mode;
            }
        }

        return null;
    }
}
