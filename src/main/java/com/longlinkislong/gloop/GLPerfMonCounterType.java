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
public enum GLPerfMonCounterType {
    GL_UNSIGNED_INT(GLType.GL_UNSIGNED_INT.value),
    GL_UNSIGNED_INT64_AMD(35778),
    GL_PERCENTAGE_AMD(35779),
    GL_FLOAT(GLType.GL_FLOAT.value);

    final int value;

    GLPerfMonCounterType(final int value) {
        this.value = value;
    }

    public static final Optional<GLPerfMonCounterType> of(final int glEnum) {
        for (GLPerfMonCounterType val : values()) {
            if (val.value == glEnum) {
                return Optional.of(val);
            }
        }
        
        return Optional.empty();
    }
}
