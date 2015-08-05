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
public enum GLTextureWrap {
    GL_CLAMP_TO_EDGE(33071), 
    GL_MIRRORED_REPEAT(33648), 
    GL_REPEAT(10497), 
    GL_MIRROR_CLAMP_TO_EDGE(33648);
    
    final int value;
    
    GLTextureWrap(final int value) {
        this.value = value;
    }
    
    public static GLTextureWrap valueOf(final int value) {
        for(GLTextureWrap wrap : values()) {
            if(wrap.value== value) {
                return wrap;
            }
        }
        
        return null;
    }
}
