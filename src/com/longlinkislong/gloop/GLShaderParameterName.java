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
public enum GLShaderParameterName {

    GL_SHADER_TYPE(0x8B4F),
    GL_INFO_LOG_LENGTH(0x8B84),
    GL_DELETE_STATUS(0x8B80),
    GL_COMPILE_STATUS(0x8B81),
    GL_COMPUTE_SHADER(0x91B9),
    GL_SHADER_SOURCE_LENGTH(0x8B88);

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
