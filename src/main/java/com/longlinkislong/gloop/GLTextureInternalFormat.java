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
public enum GLTextureInternalFormat {    
    GL_COMPRESSED_RGB_S3TC_DXT1(33776), 
    GL_COMPRESSED_RGBA_S3TC_DXT1(33777), 
    GL_COMPRESSED_RGBA_S3TC_DXT3(33778), 
    GL_COMPRESSED_RGBA_S3TC_DXT5(33779), 
    GL_STENCIL_INDEX(6401), 
    GL_RED(6403), 
    GL_DEPTH_COMPONENT(6402), 
    GL_DEPTH_STENCIL(34041), 
    GL_RG(33319), 
    GL_RGB(6407), 
    GL_RGBA(6408), 
    GL_COMPRESSED_RED(33317), 
    GL_COMPRESSED_RG(33318), 
    GL_COMPRESSED_RGB(34029), 
    GL_COMPRESSED_RGBA(34030), 
    GL_COMPRESSED_SRGB(35912), 
    GL_COMPRESSED_SRGB_ALPHA(35913), 
    GL_R8(33321), 
    GL_R8_SNORM(36756), 
    GL_R16(33322), 
    GL_R16_SNORM(36760), 
    GL_RG8(33323), 
    GL_RG8_SNORM(36757), 
    GL_RG16(33324), 
    GL_RG16_SNORM(36761), 
    GL_R3_G3_B2(10768), 
    GL_RGB4(32847), 
    GL_RGB5(32848), 
    GL_RGB8(32849), 
    GL_RGB8_SNORM(36758), 
    GL_RGB10(32850), 
    GL_RGB12(32851), 
    GL_RGB16(32852), 
    GL_RGB16_SNORM(36762), 
    GL_RGBA2(32853), 
    GL_RGBA4(32854), 
    GL_RGBA16_SNORM(36763), 
    GL_RGB5_A1(32855), 
    GL_RGBA8(32856), 
    GL_RGBA8_SNORM(36759), 
    GL_RGB10_A2(32857), 
    GL_RGB10_A2UI(36975), 
    GL_RGBA12(32858), 
    GL_RGBA16(32859), 
    GL_SRGB8(35905), 
    GL_SRGB8_ALPHA8(35907), 
    GL_R16F(33325), 
    GL_RG16F(33327), 
    GL_RGB16F(34843), 
    GL_RGBA16F(34842), 
    GL_R32F(33326), 
    GL_RG32F(33328), 
    GL_RGB32F(34837), 
    GL_RGBA32F(34836), 
    GL_R11F_G11F_B10F(35898), 
    GL_RGB9_E5(35901), 
    GL_R8I(33329), 
    GL_R8UI(33330), 
    GL_R16I(33331), 
    GL_R16UI(33332), 
    GL_R32I(33333), 
    GL_R32UI(33334), 
    GL_RG8I(33335), 
    GL_RG8UI(33336), 
    GL_RG16I(33337), 
    GL_RG16UI(33338), 
    GL_RG32I(33339), 
    GL_RG32UI(33340), 
    GL_RGB8I(36239), 
    GL_RGB8UI(36221), 
    GL_RGB16I(36233), 
    GL_RGB16UI(36215), 
    GL_RGB32I(36227), 
    GL_RGB32UI(36209), 
    GL_RGBA8I(36238), 
    GL_RGBA8UI(36220), 
    GL_RGBA16I(36232), 
    GL_RGBA16UI(36214), 
    GL_RGBA32I(36226), 
    GL_RGBA32UI(36208), 
    GL_DEPTH_COMPONENT16(33189), 
    GL_DEPTH_COMPONENT24(33190), 
    GL_DEPTH_COMPONENT32(33191), 
    GL_DEPTH_COMPONENT32F(36012), 
    GL_DEPTH24_STENCIL8(35056), 
    GL_DEPTH32F_STENCIL8(36013);
    
    final int value;
    
    GLTextureInternalFormat(final int value) {
        this.value = value;        
    }
    
    @Deprecated
    public static GLTextureInternalFormat valueOf(int glEnum) {
        for(GLTextureInternalFormat fmt : values()) {
            if(fmt.value == glEnum) {
                return fmt;
            }
        }        
        return null;
    }
    
    public static Optional<GLTextureInternalFormat> of(int glEnum) {
        for(GLTextureInternalFormat fmt : values()) {
            if(fmt.value == glEnum) {
                return Optional.of(fmt);
            }
        }
        
        return Optional.empty();
    }
}
