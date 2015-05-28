/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.longlinkislong.gloop;

import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL12;
import org.lwjgl.opengl.GL14;

/**
 *
 * @author zmichaels
 */
public enum GLTextureWrap {
    GL_CLAMP_TO_EDGE(GL12.GL_CLAMP_TO_EDGE),
    GL_MIRRORED_REPEAT(GL14.GL_MIRRORED_REPEAT),
    GL_REPEAT(GL11.GL_REPEAT),
    GL_MIRROR_CLAMP_TO_EDGE(GL14.GL_MIRRORED_REPEAT);
    
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
