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
public enum GLVertexAttributeSize {

    BYTE(1),
    SHORT(1),
    INT(1),
    FLOAT(1),
    VEC2(2),
    VEC3(3),
    VEC4(4);
    
    final int value;

    GLVertexAttributeSize(final int value) {
        this.value = value;
    }
}
