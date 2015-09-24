/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.longlinkislong.gloop;

import java.util.Optional;

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

    @Deprecated
    public static GLTextureMagFilter valueOf(final int value) {
        return of(value).get();
    }
    
    public static Optional<GLTextureMagFilter> of(final int glEnum) {
        for (GLTextureMagFilter filter : values()) {
            if (filter.value == glEnum) {
                return Optional.of(filter);
            }
        }
        
        return Optional.empty();
    }
}
