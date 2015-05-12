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
public enum GLIndexElementType {
    GL_UNSIGNED_BYTE(0x1401),
    GL_UNSIGNED_SHORT(0x1403),
    GL_UNSIGNED_INT(0x1405);
    
    final int value;
    
    GLIndexElementType(final int value) {
        this.value = value;
    }
}
