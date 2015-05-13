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
public enum GLVertexAttributeType {    
    GL_FLOAT(GL11.GL_FLOAT, 4),
    GL_DOUBLE(GL11.GL_DOUBLE, 8),    
    GL_BYTE(GL11.GL_BYTE, 1),
    GL_UNSIGNED_BYTE(GL11.GL_UNSIGNED_BYTE, 1),
    GL_SHORT(GL11.GL_SHORT, 2),
    GL_UNSIGNED_SHORT(GL11.GL_UNSIGNED_SHORT, 2),
    GL_INT(GL11.GL_INT, 4),
    GL_UNSIGNED_INT(GL11.GL_UNSIGNED_INT, 4),
    GL_INT_2_10_10_10_REV(0x8D9F, 4),
    GL_UNSIGNED_INT_2_10_10_10_REV(0x8368, 4),
    GL_UNSIGNED_INT_10F_11F_11F_REV(0x8C3B, 4);
    
    final int value;
    final int width;
    
    GLVertexAttributeType(final int value, final int width) {
        this.value = value;
        this.width = width;
    }
}
