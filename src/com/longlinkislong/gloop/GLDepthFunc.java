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
public enum GLDepthFunc {
    GL_NEVER(GL11.GL_NEVER),
    GL_ALWAYS(GL11.GL_ALWAYS),
    GL_LESS(GL11.GL_LESS),
    GL_GREATER(GL11.GL_GREATER),
    GL_EQUAL(GL11.GL_EQUAL),
    GL_LEQUAL(GL11.GL_LEQUAL),
    GL_GEQUAL(GL11.GL_GEQUAL),
    GL_NOTEQUAL(GL11.GL_NOTEQUAL);
        final int value;

    GLDepthFunc(final int value) {
        this.value = value;
    }
    
    public static GLDepthFunc valueOf(final int value) {
        for(GLDepthFunc func : values()) {
            if(func.value == value) {
                return func;
            }
        }
        
        return null;
    }
}
