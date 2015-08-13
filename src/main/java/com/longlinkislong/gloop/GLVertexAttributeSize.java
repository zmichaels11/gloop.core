/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.longlinkislong.gloop;

/**
 * The number of elements each attribute type occupies.
 * @author zmichaels
 * @since 15.06.24
 */
public enum GLVertexAttributeSize {

    /**
     * 1 byte signed or unsigned.
     * @since 15.06.24
     */
    BYTE(1),
    /**
     * 2 bytes signed or unsigned.
     * @since 15.06.24
     */
    SHORT(1),
    /**
     * 4 bytes signed or unsigned.
     * @since 15.06.24
     */
    INT(1),
    /**
     * Float or double.
     * @since 15.06.24
     */
    FLOAT(1),
    /**
     * 2-element vector.
     * @since 15.06.24
     */
    VEC2(2),
    /**
     * 3-element vector.
     * @since 15.06.24
     */
    VEC3(3),
    /**
     * 4-element vector.
     * @since 15.06.24
     */
    VEC4(4);
    
    final int value;

    GLVertexAttributeSize(final int value) {
        this.value = value;
    }
}
