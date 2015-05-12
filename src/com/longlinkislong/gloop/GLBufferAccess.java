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
public enum GLBufferAccess {
    GL_READ(0x0001),
    GL_WRITE(0x0002),    
    GL_READ_WRITE(0x0001 | 0x0002);
    
    final int value;
    
    GLBufferAccess (final int value) {
        this.value = value;
    }
    
    public static GLBufferAccess valueOf(final int value) {
        for(GLBufferAccess ba : values()) {
            if(ba.value == value) {
                return ba;
            }
        }
        
        return null;
    }
}
