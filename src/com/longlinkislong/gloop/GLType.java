/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.longlinkislong.gloop;

import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL12;

/**
 *
 * @author zmichaels
 */
public enum GLType {
    GL_UNSIGNED_BYTE(GL11.GL_UNSIGNED_BYTE, false, false),
    GL_BYTE(GL11.GL_BYTE, true, false),
    GL_UNSIGNED_SHORT(GL11.GL_UNSIGNED_SHORT, false, false),
    GL_SHORT(GL11.GL_SHORT, true, false),
    GL_UNSIGNED_INT(GL11.GL_UNSIGNED_INT, false, false),
    GL_INT(GL11.GL_INT, true, false),
    GL_FLOAT(GL11.GL_FLOAT, true, true),
    GL_DOUBLE(GL11.GL_DOUBLE, true, true),
    GL_UNSIGNED_BYTE_3_3_2(GL12.GL_UNSIGNED_BYTE_3_3_2, false, false),
    GL_UNSIGNED_BYTE_2_3_3(GL12.GL_UNSIGNED_BYTE_2_3_3_REV, false, false),
    GL_UNSIGNED_SHORT_5_6_5(GL12.GL_UNSIGNED_SHORT_5_6_5, false, false),
    GL_UNSIGNED_SHORT_5_6_5_REV(GL12.GL_UNSIGNED_SHORT_5_6_5, false, false),
    GL_UNSIGNED_SHORT_4_4_4_4(GL12.GL_UNSIGNED_SHORT_4_4_4_4, false, false),
    GL_UNSIGNED_SHORT_4_4_4_4_REV(GL12.GL_UNSIGNED_SHORT_4_4_4_4_REV, false, false),
    GL_UNSIGNED_SHORT_5_5_5_1(GL12.GL_UNSIGNED_SHORT_5_5_5_1, false, false),
    GL_UNSIGNED_SHORT_1_5_5_5_REV(GL12.GL_UNSIGNED_SHORT_1_5_5_5_REV, false, false),
    GL_UNSIGNED_INT_8_8_8_8(GL12.GL_UNSIGNED_INT_8_8_8_8, false, false),
    GL_UNSIGNED_INT_8_8_8_8_REV(GL12.GL_UNSIGNED_INT_8_8_8_8_REV, false, false),
    GL_UNSIGNED_INT_10_10_10_2(GL12.GL_UNSIGNED_INT_10_10_10_2, false, false),
    GL_UNSIGNED_INT_2_10_10_10(GL12.GL_UNSIGNED_INT_2_10_10_10_REV, false, false);
    
    final int value;
    final boolean isSigned;
    final boolean isFloat;
    GLType(final int value, final boolean signed, final boolean isFloat) {
        this.value = value;
        this.isSigned = signed;
        this.isFloat = isFloat;
    }
}
