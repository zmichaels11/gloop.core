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
public enum GLTextureMagFilter {
    GL_NEAREST(GL11.GL_NEAREST),
    GL_LINEAR(GL11.GL_LINEAR);
    
    final int value;
    
    GLTextureMagFilter(final int value) {
        this.value = value;
    }
    
    public static GLTextureMagFilter valueOf(final int value) {
        for(GLTextureMagFilter filter : values()) {
            if(filter.value == value) {
                return filter;
            }
        }
        
        return null;
    }
}
