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
public enum GLBufferParameterName {
    GL_BUFFER_SIZE(0x8764),
    GL_BUFFER_USAGE(0x8765),
    GL_BUFFER_MAPPED(0x88BC),
    GL_BUFFER_MAP_OFFSET(0x9120),
    GL_BUFFER_MAP_LENGTH(0x9121);
    
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
