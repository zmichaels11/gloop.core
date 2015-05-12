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
public enum GLTextureTarget {
    GL_TEXTURE_1D(0x0DE0),
    GL_TEXTURE_2D(0x0DE1),
    GL_TEXTURE_1D_ARRAY(0x8C18),
    GL_TEXTURE_2D_ARRAY(0x8C1A),
    GL_TEXTURE_3D(0x806F),
    GL_TEXTURE_RECTANGLE(0x84F5),
    GL_TEXTURE_BUFFER(0x8C2A),
    GL_TEXTURE_CUBE_MAP(0x8513),
    GL_TEXTURE_CUBE_MAP_ARRAY(0x9009),
    GL_TEXTURE_2D_MULTISAMPLE(0x9100),
    GL_TEXTURE_2D_MULTISAMPLE_ARRAY(0x9102);
    
    final int value;
    GLTextureTarget(final int value) {
        this.value = value;
    }
}
