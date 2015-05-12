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
public enum GLShaderType {
    GL_VERTEX_SHADER(0x8B31),
    GL_FRAGMENT_SHADER(0x8B30),
    GL_GEOMETRY_SHADER(0x8DD9),
    GL_TESS_CONTROL_SHADER(0x8E88),
    GL_TESS_EVALUATION_SHADER(0x8E87),
    GL_COMPUTE_SHADER(0x91B9);
    
    final int value;
    GLShaderType(final int value) {
        this.value = value;
    }
    
    public static GLShaderType valueOf(final int value) {
        for(GLShaderType sType : values()) {
            if(sType.value == value) {
                return sType;
            }
        }
        
        return null;
    }
}
