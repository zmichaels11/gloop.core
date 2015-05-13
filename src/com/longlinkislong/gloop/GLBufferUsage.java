/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.longlinkislong.gloop;

import org.lwjgl.opengl.GL15;

/**
 *
 * @author zmichaels
 */
public enum GLBufferUsage {

    GL_STREAM_DRAW(GL15.GL_STREAM_DRAW),
    GL_STREAM_READ(GL15.GL_STREAM_READ),
    GL_STREAM_COPY(GL15.GL_STREAM_COPY),
    GL_STATIC_DRAW(GL15.GL_STATIC_DRAW),
    GL_STATIC_READ(GL15.GL_STATIC_READ),
    GL_STATIC_COPY(GL15.GL_STATIC_COPY),
    GL_DYNAMIC_DRAW(GL15.GL_DYNAMIC_DRAW),
    GL_DYNAMIC_READ(GL15.GL_DYNAMIC_READ),
    GL_DYNAMIC_COPY(GL15.GL_DYNAMIC_COPY);

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
