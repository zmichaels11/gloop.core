/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.longlinkislong.gloop;

import org.lwjgl.opengl.GL11;

/**
 *
 * @author zmichaels
 */
public enum GLTextureMinFilter {
    GL_NEAREST(GL11.GL_NEAREST),
    GL_LINEAR(GL11.GL_LINEAR),
    GL_NEAREST_MIPMAP_NEAREST(GL11.GL_NEAREST_MIPMAP_NEAREST),
    GL_LINEAR_MIPMAP_NEAREST(GL11.GL_LINEAR_MIPMAP_NEAREST),
    GL_NEAREST_MIPMAP_LINEAR(GL11.GL_NEAREST_MIPMAP_LINEAR),
    GL_LINEAR_MIPMAP_LINEAR(GL11.GL_LINEAR_MIPMAP_LINEAR);
    
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
