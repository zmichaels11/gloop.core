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
public enum GLType {
    GL_UNSIGNED_BYTE(GL11.GL_UNSIGNED_BYTE, false, false),
    GL_BYTE(GL11.GL_BYTE, true, false),
    GL_UNSIGNED_SHORT(GL11.GL_UNSIGNED_SHORT, false, false),
    GL_SHORT(GL11.GL_SHORT, true, false),
    GL_UNSIGNED_INT(GL11.GL_UNSIGNED_INT, false, false),
    GL_INT(GL11.GL_INT, true, false),
    GL_FLOAT(GL11.GL_FLOAT, true, true),
    GL_DOUBLE(GL11.GL_DOUBLE, true, true);
    
    final int value;
    final boolean isSigned;
    final boolean isFloat;
    GLType(final int value, final boolean signed, final boolean isFloat) {
        this.value = value;
        this.isSigned = signed;
        this.isFloat = isFloat;
    }
}
