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
public enum GLCullMode {

    GL_FRONT(GL11.GL_FRONT),
    GL_BACK(GL11.GL_BACK),
    GL_FRONT_AND_BACK(GL11.GL_FRONT_AND_BACK);
    final int value;

    GLCullMode(final int value) {
        this.value = value;
    }
    
    public static GLCullMode valueOf(final int value) {
        for(GLCullMode mode : values()) {
            if(mode.value == value) {
                return mode;
            }
        }
        
        return null;
    }
}
