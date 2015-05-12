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
public enum GLBufferUsage {

    GL_STREAM_DRAW(0x88E0),
    GL_STREAM_READ(0x88E1),
    GL_STREAM_COPY(0x88E2),
    GL_STATIC_DRAW(0x88E3),
    GL_STATIC_READ(0x88E4),
    GL_STATIC_COPY(0x88E6),
    GL_DYNAMIC_DRAW(0x88E8),
    GL_DYNAMIC_READ(0x88E9),
    GL_DYNAMIC_COPY(0x88EA);

    final int value;

    GLBufferUsage(final int value) {
        this.value = value;
    }
    
    public static GLBufferUsage valueOf(final int value) {
        for(GLBufferUsage bu : values()) {
            if(bu.value == value) {
                return bu;
            }
        }
        
        return null;
    }
}
