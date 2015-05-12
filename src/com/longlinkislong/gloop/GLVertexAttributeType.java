/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.longlinkislong.gloop;

/**
 *
 * @author zmichaels
 */
public enum GLVertexAttributeType {
    GL_HALF_FLOAT(0x140B, 2),
    GL_FLOAT(0x1406, 4),
    GL_DOUBLE(0x140A, 8),
    GL_FIXED(0x140C, 2),
    GL_BYTE(0x1400, 1),
    GL_UNSIGNED_BYTE(0x1401, 1),
    GL_SHORT(0x1402, 2),
    GL_UNSIGNED_SHORT(0x1403, 2),
    GL_INT(0x1404, 4),
    GL_UNSIGNED_INT(0x1405, 4),
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
