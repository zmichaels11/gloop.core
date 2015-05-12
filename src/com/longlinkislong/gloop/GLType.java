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
    GL_UNSIGNED_BYTE(0x1401, false, false),
    GL_BYTE(0x1400, true, false),
    GL_UNSIGNED_SHORT(0x1403, false, false),
    GL_SHORT(0x1402, true, false),
    GL_UNSIGNED_INT(0x1405, false, false),
    GL_INT(0x1404, true, false),
    GL_FLOAT(0x1406, true, true),
    GL_DOUBLE(0x140A, true, true);
    
    final int value;
    final boolean isSigned;
    final boolean isFloat;
    GLType(final int value, final boolean signed, final boolean isFloat) {
        this.value = value;
        this.isSigned = signed;
        this.isFloat = isFloat;
    }
}
