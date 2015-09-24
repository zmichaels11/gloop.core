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
public enum GLDrawQueryCondition {
    GL_ANY_SAMPLES_PASSED(35887), 
    GL_ANY_SAMPLES_PASSED_CONSERVATIVE(36202), 
    GL_PRIMITIVES_GENERATED(35975), 
    GL_SAMPLES_PASSED(35092), 
    GL_TIME_ELAPSED(35007), 
    GL_TRANSFORM_FEEDBACK_PRIMITIVES_WRITTEN(35976);
        final int value;

    GLDrawQueryCondition(final int value) {
        this.value = value;
    }
    
    @Deprecated
    public static GLDrawQueryCondition valueOf(final int value) {
        return of(value).get();
    }
    
    public static Optional<GLDrawQueryCondition> of(final int glEnum) {
        for(GLDrawQueryCondition condition : values()) {
            if(condition.value == glEnum) {
                return Optional.of(condition);
            }
        }
        
        return Optional.empty();
    }
}
