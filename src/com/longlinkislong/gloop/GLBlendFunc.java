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
public enum GLBlendFunc {
    GL_ZERO(0),
    GL_ONE(1),
    GL_SRC_COLOR(GL11.GL_SRC_COLOR),
    GL_ONE_MINUS_SRC_COLOR(GL11.GL_ONE_MINUS_SRC_COLOR),
    GL_DST_COLOR(GL11.GL_DST_COLOR),
    GL_ONE_MINUS_DST_COLOR(GL11.GL_ONE_MINUS_DST_COLOR),
    GL_SRC_ALPHA(GL11.GL_SRC_ALPHA),
    GL_ONE_MINUS_SRC_ALPHA(GL11.GL_ONE_MINUS_SRC_ALPHA),
    GL_DST_ALPHA(GL11.GL_DST_ALPHA),
    GL_ONE_MINUS_DST_ALPHA(GL11.GL_ONE_MINUS_DST_ALPHA),
    GL_CONSTANT_COLOR(GL11.GL_CONSTANT_COLOR),
    GL_ONE_MINUS_CONSTANT_COLOR(GL11.GL_ONE_MINUS_CONSTANT_COLOR),
    GL_CONSTANT_ALPHA(GL11.GL_CONSTANT_ALPHA),
    GL_ONE_MINUS_CONSTANT_ALPHA(GL11.GL_ONE_MINUS_CONSTANT_ALPHA),
    GL_SRC_ALPHA_SATURATE(GL11.GL_SRC_ALPHA_SATURATE);
        final int value;

    GLBlendFunc(final int value) {
        this.value = value;
    }
    
    public static GLBlendFunc valueOf(final int value) {
        for(GLBlendFunc func : values()) {
            if(func.value == value) {
                return func;
            }
        }
        
        return null;
    }
}
