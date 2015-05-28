/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.longlinkislong.gloop;

import org.lwjgl.opengl.GL30;

/**
 *
 * @author zmichaels
 */
public enum GLTextureFormat {
    GL_DEPTH_COMPONENT(0x1902),
    GL_DEPTH_STENCIL(GL30.GL_DEPTH_STENCIL),
    GL_RED_INTEGER(0x8D94),
    GL_GREEN_INTEGER(0x8D95),
    GL_BLUE_INTEGER(0x8D96),
    GL_BGRA_INTEGER(0x8D9B),
    GL_RG_INTEGER(0x8228),
    GL_RGB_INTEGER(0x8D98),
    GL_RGBA_INTEGER(0x8D99),
    GL_BGR_INTEGER(0x8D9A),
    GL_STENCIL_INDEX(0x1802),
    GL_COLOR_INDEX(0x1900),
    GL_RED(0x1903),
    GL_GREEN(0x1904),
    GL_BLUE(0x1905),
    GL_ALPHA(0x1906),
    GL_RGB(0x1907),
    GL_BGR(0x80E0),
    GL_RGBA(0x1908),
    GL_BGRA(0x80E1),
    GL_LUMINANCE(0x1909),
    GL_LUMINANCE_ALPHA(0x190A);
    
    final int value;
    GLTextureFormat(final int value) {
        this.value = value;
    }
}
