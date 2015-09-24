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
public enum GLTextureTarget {
    GL_TEXTURE_1D(3552), 
    GL_TEXTURE_2D(3553), 
    GL_TEXTURE_1D_ARRAY(35864), 
    GL_TEXTURE_2D_ARRAY(35866), 
    GL_TEXTURE_3D(32879), 
    GL_TEXTURE_RECTANGLE(34037), 
    GL_TEXTURE_BUFFER(35882), 
    GL_TEXTURE_CUBE_MAP(34067), 
    GL_TEXTURE_CUBE_MAP_ARRAY(36873), 
    GL_TEXTURE_2D_MULTISAMPLE(37120), 
    GL_TEXTURE_2D_MULTISAMPLE_ARRAY(37122);
    
    final int value;
    GLTextureTarget(final int value) {
        this.value = value;
    }
    
    public static Optional<GLTextureTarget> of(int glEnum) {
        for(GLTextureTarget tgt : values()) {
            if(tgt.value == glEnum) {
                return Optional.of(tgt);
            }
        }
        
        return Optional.empty();
    }
}
