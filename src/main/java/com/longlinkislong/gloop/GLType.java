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
public enum GLType {
    GL_UNSIGNED_BYTE(5121, false, false), 
    GL_BYTE(5120, true, false), 
    GL_UNSIGNED_SHORT(5123, false, false), 
    GL_SHORT(5122, true, false), 
    GL_UNSIGNED_INT(5125, false, false), 
    GL_INT(5124, true, false), 
    GL_FLOAT(5126, true, true), 
    GL_DOUBLE(5130, true, true), 
    GL_UNSIGNED_BYTE_3_3_2(32818, false, false), 
    GL_UNSIGNED_BYTE_2_3_3_REV(33634, false, false), 
    GL_UNSIGNED_SHORT_5_6_5(33635, false, false), 
    GL_UNSIGNED_SHORT_5_6_5_REV(33635, false, false), 
    GL_UNSIGNED_SHORT_4_4_4_4(32819, false, false), 
    GL_UNSIGNED_SHORT_4_4_4_4_REV(33637, false, false), 
    GL_UNSIGNED_SHORT_5_5_5_1(32820, false, false), 
    GL_UNSIGNED_SHORT_1_5_5_5_REV(33638, false, false), 
    GL_UNSIGNED_INT_8_8_8_8(32821, false, false), 
    GL_UNSIGNED_INT_8_8_8_8_REV(33639, false, false), 
    GL_UNSIGNED_INT_10_10_10_2(32822, false, false), 
    GL_UNSIGNED_INT_2_10_10_10_REV(33640, false, false);
    
    final int value;
    final boolean isSigned;
    final boolean isFloat;
    GLType(final int value, final boolean signed, final boolean isFloat) {
        this.value = value;
        this.isSigned = signed;
        this.isFloat = isFloat;
    }
}
