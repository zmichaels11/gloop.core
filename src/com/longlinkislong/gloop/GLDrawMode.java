/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.longlinkislong.gloop;

/**
 *
 * @author zmichaels
 */
public enum GLDrawMode {

    GL_POINTS(0x0000),
    GL_PATCHES(0xE),
    GL_LINE_STRIP(0x0003),
    GL_LINE_LOOP(0x0002),
    GL_TRIANGLE_STRIP(0x0005),
    GL_TRIANGLE_FAN(0x0006),
    GL_LINES(0x0001),
    GL_LINES_ADJACENCY(0x000A),
    GL_TRIANGLES(0x0004),
    GL_TRIANGLES_ADJACENCY(0x000C),
    GL_LINE_STRIP_ADJACENCY(0x000B),
    GL_TRIANGLE_STRIP_ADJACENCY(0x000D);
    
    final int value;
    
    GLDrawMode(final int value) {
        this.value = value;
    }

}
