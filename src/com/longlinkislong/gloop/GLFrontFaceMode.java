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
public enum GLFrontFaceMode {

    GL_CW(GL11.GL_CW),
    GL_CCW(GL11.GL_CCW);
    final int value;

    GLFrontFaceMode(final int value) {
        this.value = value;
    }
    
    public static GLFrontFaceMode valueOf(final int value) {
        for(GLFrontFaceMode mode : values()) {
            if(mode.value == value) {
                return mode;
            }
        }
        
        return null;
    }
}
