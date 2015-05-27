/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.longlinkislong.gloop;

import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL40;

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
    GL_POINTS(GL11.GL_POINTS),
    GL_PATCHES(GL40.GL_PATCHES),
    /**
     * Draw mode in which each vertex element is a point forming a continuous
     * line.
     *
     * @since 15.05.27
     */
    GL_LINE_STRIP(GL11.GL_LINE_STRIP),
    /**
     * Draw mode in which each vertex element is a point forming a continuous
     * line. The first vertex element is reused as the last element.
     *
     * @since 15.05.27
     */
    GL_LINE_LOOP(GL11.GL_LINE_LOOP),
    /**
     * Draw mode in which a series of triangles are defined by reusing the last
     * two vertex elements.
     *
     * @since 15.05.27
     */
    GL_TRIANGLE_STRIP(GL11.GL_TRIANGLE_STRIP),
    GL_TRIANGLE_FAN(GL11.GL_TRIANGLE_FAN),
    /**
     * Draw mode in which every two vertex elements make up a line.
     *
     * @since 15.05.27
     */
    GL_LINES(GL11.GL_LINES),
    /**
     * Draw mode in which every three vertex elements make up a triangle.
     *
     * @since 15.05.27
     */
    GL_TRIANGLES(GL11.GL_TRIANGLES),
    GL_LINES_ADJACENCY(0x000A),
    GL_TRIANGLES_ADJACENCY(0x000C),
    GL_LINE_STRIP_ADJACENCY(0x000B),
    GL_TRIANGLE_STRIP_ADJACENCY(0x000D);

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
    public static GLDrawMode valueOf(final int value) {
        for (GLDrawMode mode : values()) {
            if (mode.value == value) {
                return mode;
            }
        }

        return null;
    }
}
