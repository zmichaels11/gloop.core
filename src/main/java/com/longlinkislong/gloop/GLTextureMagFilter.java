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
public enum GLTextureMagFilter {

    GL_NEAREST(9728),
    GL_LINEAR(9729);

    final int value;

    GLTextureMagFilter(final int value) {
        this.value = value;
    }

    public static GLTextureMagFilter valueOf(final int value) {
        for (GLTextureMagFilter filter : values()) {
            if (filter.value == value) {
                return filter;
            }
        }

        return null;
    }
}
