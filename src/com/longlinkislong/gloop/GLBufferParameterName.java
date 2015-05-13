/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.longlinkislong.gloop;

import org.lwjgl.opengl.GL15;
import org.lwjgl.opengl.GL30;

/**
 *
 * @author zmichaels
 */
public enum GLBufferParameterName {
    GL_BUFFER_SIZE(GL15.GL_BUFFER_SIZE),
    GL_BUFFER_USAGE(GL15.GL_BUFFER_USAGE),
    GL_BUFFER_MAPPED(GL15.GL_BUFFER_MAPPED),
    GL_BUFFER_MAP_OFFSET(GL30.GL_BUFFER_MAP_OFFSET),
    GL_BUFFER_MAP_LENGTH(GL30.GL_BUFFER_MAP_LENGTH);
    
    final int value;
    GLBufferParameterName(final int value) {
        this.value = value;
    }
    
    public static final GLBufferParameterName valueOf(final int value) {
        for(GLBufferParameterName pName : values()) {
            if(pName.value == value) {
                return pName;
            }
        }
        
        return null;
    }
}
