/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.longlinkislong.gloop.dsa;

import java.nio.ByteBuffer;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL12;

/**
 *
 * @author zmichaels
 */
public interface EXTDirectStateAccessPatch extends DirectStateAccess {
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
    
    void glVertexArrayVertexAttribOffset(int vaobj, int bufferId, int index, int size, int type, boolean normalized, int stride, long offset);
    
    @Override
    default void glVertexArrayElementBuffer(int vaobj, int index) {
        throw new UnsupportedOperationException("glVertexArrayElementBuffer is not supported in implementation: " + this.getClass().getName());
    }
    
    @Override
    default void glVertexArrayVertexBuffer(int vaobj, int bindingIndex, int buffer, long offset, int stride) {
        throw new UnsupportedOperationException("glVertexArrayVertexBuffer is not supported in implementation: " + this.getClass().getName());
    }
    
    @Override
    default void glVertexArrayAttribFormat(int vaobj, int attribIndex, int size, int type, boolean normalized, int relativeOffset) {
        throw new UnsupportedOperationException("glVertexArrayAttribFormat is not supported in implementation: " + this.getClass().getName());
    }
    
    @Override
    default void glVertexArrayAttribBinding(int vaobj, int attribIndex, int bindingIndex) {
        throw new UnsupportedOperationException("glVertexArrayAttribBinding is not supported in implementation: " + this.getClass().getName());
    }        
    
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
            this.glTextureImage1d(textureId, GL11.GL_TEXTURE_1D, i, internalFormat, width, 0, GL11.GL_RGBA, GL11.GL_UNSIGNED_BYTE, 0);
            width = Math.max(1, (width / 2));
        }
    }

    @Override
    default void glTextureStorage2d(int textureId, int levels, int internalFormat, int width, int height) {
        this.glTextureParameteri(textureId, GL11.GL_TEXTURE_2D, GL12.GL_TEXTURE_BASE_LEVEL, 0);
        this.glTextureParameteri(textureId, GL11.GL_TEXTURE_2D, GL12.GL_TEXTURE_MAX_LEVEL, levels);

        for (int i = 0; i < levels; i++) {
            this.glTextureImage2d(textureId, GL11.GL_TEXTURE_2D, i, internalFormat, width, height, 0, GL11.GL_RGBA, GL11.GL_UNSIGNED_BYTE, 0);            
            
            width = Math.max(1, (width / 2));
            height = Math.max(1, (height / 2));
        }
    }

    @Override
    default void glTextureStorage3d(int textureId, int levels, int internalFormat, int width, int height, int depth) {
        this.glTextureParameterf(textureId, GL12.GL_TEXTURE_3D, GL12.GL_TEXTURE_BASE_LEVEL, 0);
        this.glTextureParameteri(textureId, GL12.GL_TEXTURE_3D, GL12.GL_TEXTURE_MAX_LEVEL, levels);

        for (int i = 0; i < levels; i++) {
            this.glTextureImage3d(textureId, GL12.GL_TEXTURE_3D, i, internalFormat, width, height, depth, 0, GL11.GL_RGBA, GL11.GL_UNSIGNED_BYTE, 0);
            width = Math.max(1, (width / 2));
            height = Math.max(1, (height / 2));
            depth = Math.max(1, (depth / 2));
        }
    }
}
