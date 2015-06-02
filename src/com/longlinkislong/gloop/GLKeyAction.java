/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.longlinkislong.gloop;

import org.lwjgl.glfw.GLFW;

/**
 *
 * @author zmichaels
 */
public enum GLKeyAction {

    KEY_PRESSED(GLFW.GLFW_PRESS),
    KEY_RELEASE(GLFW.GLFW_RELEASE),
    KEY_REPEAT(GLFW.GLFW_REPEAT);
    final int value;

    GLKeyAction(final int value) {
        this.value = value;
    }
    
    public static GLKeyAction valueOf(final int value) {
        for(GLKeyAction action : values()) {
            if(action.value == value) {
                return action;
            }
        }
        
        return null;
    }
}
