/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.longlinkislong.gloop;

import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL40;

/**
 *
 * @author zmichaels
 */
public enum GLDrawMode {

    GL_POINTS(GL11.GL_POINTS),
    GL_PATCHES(GL40.GL_PATCHES),
    GL_LINE_STRIP(GL11.GL_LINE_STRIP),
    GL_LINE_LOOP(GL11.GL_LINE_LOOP),
    GL_TRIANGLE_STRIP(GL11.GL_TRIANGLE_STRIP),
    GL_TRIANGLE_FAN(GL11.GL_TRIANGLE_FAN),
    GL_LINES(GL11.GL_LINES),
    GL_TRIANGLES(GL11.GL_TRIANGLES),
    GL_LINES_ADJACENCY(0x000A),    
    GL_TRIANGLES_ADJACENCY(0x000C),
    GL_LINE_STRIP_ADJACENCY(0x000B),
    GL_TRIANGLE_STRIP_ADJACENCY(0x000D);
    
    final int value;
    
    GLDrawMode(final int value) {
        this.value = value;
    }

}
