/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.longlinkislong.gloop;

/**
 * A collection of texture storage formats.
 *
 * @author zmichaels
 * @since 15.07.01
 */
public enum GLTextureFormat {

    GL_DEPTH_COMPONENT(6402), 
    GL_DEPTH_STENCIL(34041), 
    GL_RED_INTEGER(36244), 
    GL_GREEN_INTEGER(36245), 
    GL_BLUE_INTEGER(36246), 
    GL_BGRA_INTEGER(36251), 
    GL_RG_INTEGER(33320), 
    GL_RGB_INTEGER(36248), 
    GL_RGBA_INTEGER(36249), 
    GL_BGR_INTEGER(36250), 
    GL_STENCIL_INDEX(6146), 
    GL_COLOR_INDEX(6400), 
    GL_RED(6403), 
    GL_GREEN(6404), 
    GL_BLUE(6405), 
    GL_ALPHA(6406), 
    GL_RGB(6407), 
    GL_BGR(32992), 
    GL_RGBA(6408), 
    GL_BGRA(32993), 
    GL_LUMINANCE(6409), 
    GL_LUMINANCE_ALPHA(6410);

    final int value;

    GLTextureFormat(final int value) {
        this.value = value;
    }

    /**
     * Converts a GLenum value for a Texture Format to an instance of a
     * GLTextureFormat constant.
     *
     * @param glEnum the GLenum value.
     * @return the corresponding GLTextureFormat if available or null.
     * @since 15.07.20
     */
    public static GLTextureFormat valueOf(final int glEnum) {
        for (GLTextureFormat format : values()) {
            if (format.value == glEnum) {
                return format;
            }
        }

        return null;
    }
}
