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
public enum GLShaderParameterName {

    GL_SHADER_TYPE(35663), 
    GL_INFO_LOG_LENGTH(35716), 
    GL_DELETE_STATUS(35712), 
    GL_COMPILE_STATUS(35713), 
    GL_COMPUTE_SHADER(37305), 
    GL_SHADER_SOURCE_LENGTH(35720);

    final int value;

    GLShaderParameterName(final int value) {
        this.value = value;
    }
    
    @Deprecated
    public static GLShaderParameterName valueOf(final int value) {
        return of(value).get();
    }
    
    public static Optional<GLShaderParameterName> of(final int glEnum) {
        for(GLShaderParameterName pName : values()) {
            if(pName.value == glEnum) {
                return Optional.of(pName);
            }
        }
        
        return Optional.empty();
    }
}
