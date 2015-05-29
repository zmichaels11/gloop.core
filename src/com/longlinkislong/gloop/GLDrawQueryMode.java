/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.longlinkislong.gloop;

import org.lwjgl.opengl.GL30;

/**
 *
 * @author zmichaels
 */
public enum GLDrawQueryMode {
    GL_QUERY_WAIT(GL30.GL_QUERY_WAIT),
    GL_QUERY_NO_WAIT(GL30.GL_QUERY_NO_WAIT),
    GL_QUERY_BY_REGION_WAIT(GL30.GL_QUERY_BY_REGION_WAIT),
    GL_QUERY_BY_REGION_NO_WAIT(GL30.GL_QUERY_BY_REGION_NO_WAIT);
    
    final int value;
    
    GLDrawQueryMode(final int value) {
        this.value = value;
    }
    
    public static GLDrawQueryMode valueOf(final int value) {
        for(GLDrawQueryMode mode : values()) {
            if(mode.value == value) {
                return mode;
            }
        }
        
        return null;
    }
}
