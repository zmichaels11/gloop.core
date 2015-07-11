/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.longlinkislong.gloop.dsa;

import com.longlinkislong.gloop.GLTextureInternalFormat;
import java.nio.ByteBuffer;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL12;
import org.lwjgl.opengl.GL30;

/**
 * An alternative version of DSADriver, matches the EXTDirectStateAccess
 *
 * @author zmichaels
 * @since 15.07.10
 */
public interface EXTDSADriver extends DSADriver {

    void glNamedFramebufferTexture1D(int framebuffer, int attachment, int texTarget, int texture, int level);

    void glNamedFramebufferTexture2D(int framebuffer, int attachment, int texTarget, int texture, int level);

    void glTextureParameteri(int textureId, int target, int pName, int val);

    void glTextureParameterf(int textureId, int target, int pName, float val);

    void glGenerateTextureMipmap(int textureId, int target);

    void glBindTextureUnit(int unit, int target, int textureId);

    void glTextureImage1d(int textureId, int target, int level, int internalFormat, int width, int border, int format, int type, long size);

    void glTextureImage1d(int texture, int target, int level, int internalFormat, int width, int border, int format, int type, ByteBuffer pixels);

    void glTextureImage2d(int texture, int target, int level, int internalFormat, int width, int height, int border, int format, int type, ByteBuffer pixels);

    void glTextureImage2d(int texture, int target, int level, int internalFormat, int width, int height, int border, int format, int type, long ptr);

    void glTextureImage3d(int texture, int target, int level, int internalFormat, int width, int height, int depth, int border, int format, int type, ByteBuffer pixels);

    void glTextureImage3d(int texture, int target, int level, int internalFormat, int width, int height, int depth, int border, int format, int type, long ptr);

    @Override
    default void glTextureParameteri(int textureId, int pName, int val) {
        throw new UnsupportedOperationException("glTextureParameteri requires target in implementation: " + this.getClass().getName());
    }

    @Override
    default void glTextureParameterf(int textureId, int pName, float val) {
        throw new UnsupportedOperationException("glTextureParameterf requires target in implementation: " + this.getClass().getName());
    }

    @Override
    default void glGenerateTextureMipmap(int textureId) {
        throw new UnsupportedOperationException("glGenerateTextureMipmap requires target in implementation: " + this.getClass().getName());
    }

    @Override
    default void glBindTextureUnit(int unit, int textureId) {
        throw new UnsupportedOperationException("glBindTextureUnit requires target in implementation: " + this.getClass().getName());
    }

    @Override
    default void glTextureStorage1d(int textureId, int levels, int internalFormat, int width) {
        this.glTextureParameterf(textureId, GL11.GL_TEXTURE_1D, GL12.GL_TEXTURE_BASE_LEVEL, 0);
        this.glTextureParameteri(textureId, GL11.GL_TEXTURE_1D, GL12.GL_TEXTURE_MAX_LEVEL, levels);

        for (int i = 0; i < levels; i++) {
            this.glTextureImage1d(textureId, GL11.GL_TEXTURE_1D, i, internalFormat, width, 0, guessFormat(internalFormat), GL11.GL_UNSIGNED_BYTE, 0);
            width = Math.max(1, (width / 2));
        }
    }

    @Override
    default void glTextureStorage2d(int textureId, int levels, int internalFormat, int width, int height) {
        this.glTextureParameteri(textureId, GL11.GL_TEXTURE_2D, GL12.GL_TEXTURE_BASE_LEVEL, 0);
        this.glTextureParameteri(textureId, GL11.GL_TEXTURE_2D, GL12.GL_TEXTURE_MAX_LEVEL, levels);

        for (int i = 0; i < levels; i++) {
            this.glTextureImage2d(textureId, GL11.GL_TEXTURE_2D, i, internalFormat, width, height, 0, guessFormat(internalFormat), GL11.GL_UNSIGNED_BYTE, 0);

            width = Math.max(1, (width / 2));
            height = Math.max(1, (height / 2));
        }
    }

    @Override
    default void glTextureStorage3d(int textureId, int levels, int internalFormat, int width, int height, int depth) {
        this.glTextureParameterf(textureId, GL12.GL_TEXTURE_3D, GL12.GL_TEXTURE_BASE_LEVEL, 0);
        this.glTextureParameteri(textureId, GL12.GL_TEXTURE_3D, GL12.GL_TEXTURE_MAX_LEVEL, levels);

        for (int i = 0; i < levels; i++) {
            this.glTextureImage3d(textureId, GL12.GL_TEXTURE_3D, i, internalFormat, width, height, depth, 0, guessFormat(internalFormat), GL11.GL_UNSIGNED_BYTE, 0);
            width = Math.max(1, (width / 2));
            height = Math.max(1, (height / 2));
            depth = Math.max(1, (depth / 2));
        }
    }

    /**
     * Retrieves an OpenGL texture format that is a version of the guessed
     * format. The purpose for this is in emulating glTextureStorageXD in
     * glTextureImageXD.
     *
     * @param internalFormat the sized pixel format
     * @return the format.
     * @since 15.07.10
     */
    default int guessFormat(int internalFormat) {
        switch (GLTextureInternalFormat.valueOf(internalFormat)) {
            case GL_COMPRESSED_RGB_S3TC_DXT1:
            case GL_RGB:
            case GL_COMPRESSED_RGB:
            case GL_COMPRESSED_SRGB:
            case GL_R3_G3_B2:
            case GL_RGB4:
            case GL_RGB5:
            case GL_RGB8:
            case GL_RGB8_SNORM:
            case GL_RGB10:
            case GL_RGB12:
            case GL_RGB16:
            case GL_RGB16_SNORM:
            case GL_SRGB8:
            case GL_RGB16F:
            case GL_RGB32F:
            case GL_RGB8I:
            case GL_RGB8UI:
            case GL_RGB16I:
            case GL_RGB16UI:
            case GL_RGB32I:
            case GL_RGB32UI:
                return GL11.GL_RGB;
            case GL_COMPRESSED_RGBA_S3TC_DXT1:
            case GL_COMPRESSED_RGBA_S3TC_DXT3:
            case GL_COMPRESSED_RGBA_S3TC_DXT5:
            case GL_RGBA:
            case GL_COMPRESSED_RGBA:
            case GL_COMPRESSED_SRGB_ALPHA:
            case GL_RGBA2:
            case GL_RGBA4:
            case GL_RGBA16_SNORM:
            case GL_RGB5_A1:
            case GL_RGBA8:
            case GL_RGBA8_SNORM:
            case GL_RGB10_A2:
            case GL_RGB10_A2UI:
            case GL_RGBA12:
            case GL_RGBA16:
            case GL_SRGB8_ALPHA8:
            case GL_RGBA16F:
            case GL_RGBA32F:
            case GL_R11F_G11F_B10F:
            case GL_RGBA8I:
            case GL_RGBA8UI:
            case GL_RGBA16I:
            case GL_RGBA16UI:
            case GL_RGBA32I:
            case GL_RGBA32UI:
                return GL11.GL_RGBA;
            case GL_STENCIL_INDEX:
                return GL11.GL_STENCIL;
            case GL_RED:
            case GL_R8:
            case GL_R8_SNORM:
            case GL_R16_SNORM:
            case GL_COMPRESSED_RED:
            case GL_R16:
            case GL_R16F:
            case GL_R32F:
            case GL_R8I:
            case GL_R8UI:
            case GL_R16I:
            case GL_R16UI:
            case GL_R32I:
            case GL_R32UI:
                return GL11.GL_RED;
            case GL_DEPTH_COMPONENT:
                return GL11.GL_DEPTH_COMPONENT;
            case GL_DEPTH_STENCIL:
            case GL_DEPTH24_STENCIL8:
            case GL_DEPTH32F_STENCIL8:
                return GL30.GL_DEPTH_STENCIL;
            case GL_RG:
            case GL_COMPRESSED_RG:
            case GL_RG8:
            case GL_RG8_SNORM:
            case GL_RG16:
            case GL_RG16_SNORM:
            case GL_RG16F:
            case GL_RG32F:
            case GL_RG8I:
            case GL_RG8UI:
            case GL_RG16I:
            case GL_RG16UI:
            case GL_RG32I:
            case GL_RG32UI:
                return GL30.GL_RG;
            case GL_DEPTH_COMPONENT16:
            case GL_DEPTH_COMPONENT24:
            case GL_DEPTH_COMPONENT32:
            case GL_DEPTH_COMPONENT32F:
                return GL11.GL_DEPTH_COMPONENT;
            default:
                return GL11.GL_RGBA;
        }
    }

    @Override
    default void glNamedFramebufferTexture(int framebuffer, int attachment, int texture, int level
    ) {
        throw new UnsupportedOperationException("glNamedFramebufferTexture requires target for implementation: " + this.getClass().getName());
    }
}
