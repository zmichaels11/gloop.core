/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.longlinkislong.gloop;

import org.lwjgl.opengl.GL15;
import org.lwjgl.opengl.GL30;
import org.lwjgl.opengl.GL33;
import org.lwjgl.opengl.GL43;

/**
 *
 * @author zmichaels
 */
public enum GLDrawQueryCondition {
    GL_ANY_SAMPLES_PASSED(GL33.GL_ANY_SAMPLES_PASSED),
    GL_ANY_SAMPLES_PASSED_CONSERVATIVE(GL43.GL_ANY_SAMPLES_PASSED_CONSERVATIVE),
    GL_PRIMITIVES_GENERATED(GL30.GL_PRIMITIVES_GENERATED),
    GL_SAMPLES_PASSED(GL15.GL_SAMPLES_PASSED),
    GL_TIME_ELAPSED(GL33.GL_TIME_ELAPSED),
    GL_TRANSFORM_FEEDBACK_PRIMITIVES_WRITTEN(GL30.GL_TRANSFORM_FEEDBACK_PRIMITIVES_WRITTEN);
        final int value;

    GLDrawQueryCondition(final int value) {
        this.value = value;
    }
    
    public static GLDrawQueryCondition valueOf(final int value) {
        for(GLDrawQueryCondition condition : values()) {
            if(condition.value == value) {
                return condition;
            }
        }
        
        return null;
    }
}
