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
public enum GLIndexElementType {
    GL_UNSIGNED_BYTE(GL11.GL_UNSIGNED_BYTE),
    GL_UNSIGNED_SHORT(GL11.GL_UNSIGNED_SHORT),
    GL_UNSIGNED_INT(GL11.GL_UNSIGNED_INT);
    
    final int value;
    
    GLIndexElementType(final int value) {
        this.value = value;
    }
}
