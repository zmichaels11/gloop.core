/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.longlinkislong.gloop;

import java.util.Arrays;

/**
 *
 * @author zmichaels
 */
public enum GLDrawQueryMode {
    GL_QUERY_WAIT(36371), 
    GL_QUERY_NO_WAIT(36372), 
    GL_QUERY_BY_REGION_WAIT(36373), 
    GL_QUERY_BY_REGION_NO_WAIT(36374);
    
    final int value;
    
    GLDrawQueryMode(final int value) {
        this.value = value;
    }
    
    public static GLDrawQueryMode valueOf(final int value) {
        return Arrays.stream(values())
                .filter(f -> f.value == value)
                .findAny()
                .orElseThrow(()->{
                    return new GLException.InvalidGLEnumException("Invalid GLenum: " + value);
                });
    }
}
