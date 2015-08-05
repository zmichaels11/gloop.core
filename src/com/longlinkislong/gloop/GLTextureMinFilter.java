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
public enum GLTextureMinFilter {
    GL_NEAREST(9728), 
    GL_LINEAR(9729), 
    GL_NEAREST_MIPMAP_NEAREST(9984), 
    GL_LINEAR_MIPMAP_NEAREST(9985), 
    GL_NEAREST_MIPMAP_LINEAR(9986), 
    GL_LINEAR_MIPMAP_LINEAR(9987);
    
    final int value;
    
    GLTextureMinFilter(final int value) {
        this.value = value;
    }
    
    public static GLTextureMinFilter valueOf(final int value) {
        for(GLTextureMinFilter filter : values()) {
            if(filter.value == value) {
                return filter;
            }                        
        }
        
        return null;
    }
}
