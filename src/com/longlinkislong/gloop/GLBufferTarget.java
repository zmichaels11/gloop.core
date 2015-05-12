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
public enum GLBufferTarget {
    GL_ARRAY_BUFFER(0x8892),
    GL_UNIFORM_BUFFER(0xA11),
    GL_ATOMIC_COUNTER_BUFFER(0x92C0),    
    GL_QUERY_BUFFER(0x9192),
    GL_COPY_READ_BUFFER(0x8F36),
    GL_COPY_WRITE_BUFFER(0x8F37),
    GL_DISPATCH_INDIRECT_BUFFER(0x90EE),    
    GL_DRAW_INDIRECT_BUFFER(0x8F3F),
    GL_ELEMENT_ARRAY_BUFFER(0x8893),
    GL_TEXTURE_BUFFER(0x8C2A),
    GL_PIXEL_PACK_BUFFER(0x88EB),
    GL_PIXEL_UNPACK_BUFFER(0x88EC),
    GL_SHADER_STORAGE_BUFFER(0x90D2),
    GL_TRANSFORM_FEEDBACK_BUFFER(0x8C8E);
    
    final int value;
    GLBufferTarget(final int value) {
        this.value = value;
    }    
    
    public static GLBufferTarget valueOf(final int value) {
        for(GLBufferTarget tgt : values()) {
            if(tgt.value == value) {
                return tgt;
            }
        }
        
        return null;
    }
}
