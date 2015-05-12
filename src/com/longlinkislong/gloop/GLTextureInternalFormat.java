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
public enum GLTextureInternalFormat {
    STENCIL_INDEX(0x1901),
    RED(0x1903),
    DEPTH_COMPONENT(0x1902),
    DEPTH_STENCIL(0x84F9),
    RG(0x8227),
    RGB(0x1907),
    RGBA(0x1908),
    COMPRESSED_RED(0x8225),
    COMPRESSED_RG(0x8226),
    COMPRESSED_RGB(0x84ED),
    COMPRESSED_RGBA(0x84EE),
    COMPRESSED_SRGB(0x8C48),
    COMPRESSED_SRGB_ALPHA(0x8C49),    
    GL_R8(0x8229),
    GL_R8_SNORM(0x8F94),
    GL_R16(0x822A),
    GL_R16_SNORM(0x8F98),
    GL_RG8(0x822B),
    GL_RG8_SNORM(0x8F95),
    GL_RG16(0x822C),
    GL_RG16_SNORM(0x8F99),
    GL_R3_G3_B2(0x2A10),
    GL_RGB4(0x804F),
    GL_RGB5(0x8050),
    GL_RGB8(0x8051),
    GL_RGB8_SNORM(0x8F96),
    GL_RGB10(0x8052),
    GL_RGB12(0x8053),
    GL_RGB16(0x8054),
    GL_RGB16_SNORM(0x8F9A),
    GL_RGBA2(0x8055),
    GL_RGBA4(0x8056),
    GL_RGBA16_SNORM(0x8F9B),
    GL_RGB5_A1(0x8057),
    GL_RGBA8(0x8058),
    GL_RGBA8_SNORM(0x8F97),
    GL_RGB10_A2(0x8059),
    GL_RGB10_A2UI(0x906F),
    GL_RGBA12(0x805A),
    GL_RGBA16(0x805B),
    GL_SRGB8(0x8C41),    
    GL_SRGB8_ALPHA8(0x8C43),
    GL_R16F(0x822D),
    GL_RG16F(0x822F),
    GL_RGB16F(0x881B),
    GL_RGBA16F(0x881A),
    GL_R32F(0x822E),
    GL_RG32F(0x8230),
    GL_RGB32F(0x8815),    
    GL_RGBA32F(0x8814),
    GL_R11F_G11F_B10F(0x8C3A),
    GL_RGB9_E5(0x8C3D),
    GL_R8I(0x8231),
    GL_R8UI(0x8232),
    GL_R16I(0x8233),
    GL_R16UI(0x8234),
    GL_R32I(0x8235),
    GL_R32UI(0x8236),
    GL_RG8I(0x8237),
    GL_RG8UI(0x8238),
    GL_RG16I(0x8239),
    GL_RG16UI(0x823A),
    GL_RG32I(0x823B),
    GL_RG32UI(0x823C),
    GL_RGB8I(0x8D8F),
    GL_RGB8UI(0x8D7D),
    GL_RGB16I(0x8D89),
    GL_RGB16UI(0x8D77),
    GL_RGB32I(0x8D83),
    GL_RGB32UI(0x8D71),
    GL_RGBA8I(0x8D8E),
    GL_RGBA8UI(0x8D7C),
    GL_RGBA16I(0x8D88),
    GL_RGBA16UI(0x8D76),
    GL_RGBA32I(0x8D82),
    GL_RGBA32UI(0x8D70);
    
    final int value;
    
    GLTextureInternalFormat(final int value) {
        this.value = value;
    }
}
