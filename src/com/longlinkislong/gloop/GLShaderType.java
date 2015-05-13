/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.longlinkislong.gloop;

import org.lwjgl.opengl.GL20;
import org.lwjgl.opengl.GL32;
import org.lwjgl.opengl.GL40;
import org.lwjgl.opengl.GL43;

/**
 *
 * @author zmichaels
 */
public enum GLShaderType {
    GL_VERTEX_SHADER(GL20.GL_VERTEX_SHADER),
    GL_FRAGMENT_SHADER(GL20.GL_FRAGMENT_SHADER),
    GL_GEOMETRY_SHADER(GL32.GL_GEOMETRY_SHADER),
    GL_TESS_CONTROL_SHADER(GL40.GL_TESS_CONTROL_SHADER),
    GL_TESS_EVALUATION_SHADER(GL40.GL_TESS_EVALUATION_SHADER),
    GL_COMPUTE_SHADER(GL43.GL_COMPUTE_SHADER);
    
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
