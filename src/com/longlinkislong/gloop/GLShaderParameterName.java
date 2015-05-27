/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.longlinkislong.gloop;

import org.lwjgl.opengl.GL20;
import org.lwjgl.opengl.GL43;

/**
 * 
 * @author zmichaels
 */
public enum GLShaderParameterName {

    GL_SHADER_TYPE(GL20.GL_SHADER_TYPE),
    GL_INFO_LOG_LENGTH(GL20.GL_INFO_LOG_LENGTH),
    GL_DELETE_STATUS(GL20.GL_DELETE_STATUS),
    GL_COMPILE_STATUS(GL20.GL_COMPILE_STATUS),
    GL_COMPUTE_SHADER(GL43.GL_COMPUTE_SHADER),
    GL_SHADER_SOURCE_LENGTH(GL20.GL_SHADER_SOURCE_LENGTH);

    final int value;

    GLShaderParameterName(final int value) {
        this.value = value;
    }
    
    public static GLShaderParameterName valueOf(final int value) {
        for(GLShaderParameterName pName : values()) {
            if(pName.value == value) {
                return pName;
            }
        }
        
        return null;
    }
}
