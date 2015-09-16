/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.longlinkislong.gloop.dsa;

import java.nio.ByteBuffer;
import java.nio.DoubleBuffer;
import java.nio.FloatBuffer;

/**
 * An implementation of the ARB_direct_state_access plugin to OpenGL.
 *
 * @author zmichaels
 * @since 15.07.10
 */
public interface DSADriver {

    void glGetTextureImage(int texture, int level, int format, int type, int bufferSize, ByteBuffer pixels);
    
    /**
     * Checks if the current context supports this driver.
     * @return true if all features of the driver are supported.
     * @since 15.07.10
     */
    boolean isSupported();

    int glCreateBuffers();

    int glCreateTextures(int target);

    int glCreateFramebuffers();
    
    void glNamedBufferReadPixels(int bufferId, int x, int y, int width, int height, int format, int type, long ptr);

    void glNamedFramebufferTexture(int framebuffer, int attachment, int texture, int level);

    void glNamedBufferData(int bufferId, long size, int usage);

    void glNamedBufferData(int bufferId, ByteBuffer data, int usage);

    void glNamedBufferSubData(int buffer, long offset, ByteBuffer data);

    void glNamedBufferStorage(int bufferId, ByteBuffer data, int flags);

    void glNamedBufferStorage(int bufferId, long size, int flags);

    void glGetNamedBufferSubData(int bufferId, long offset, ByteBuffer out);

    ByteBuffer glMapNamedBufferRange(int bufferId, long offset, long length, int access, ByteBuffer recycled);

    void glUnmapNamedBuffer(int bufferId);

    void glCopyNamedBufferSubData(final int readBufferId, final int writeBufferId, final long readOffset, final long writeOffset, final long size);

    void glProgramUniform1f(int programId, int location, float value);

    void glProgramUniform2f(int programId, int location, float v0, float v1);

    void glProgramUniform3f(int programId, int location, float v0, float v1, float v2);

    void glProgramUniform4f(int programId, int location, float v0, float v1, float v2, float v3);

    void glProgramUniform1i(int programId, int location, int value);

    void glProgramUniform2i(int programId, int location, int v0, int v1);

    void glProgramUniform3i(int programId, int location, int v0, int v1, int v2);

    void glProgramUniform4i(int programId, int location, int v0, int v1, int v2, int v3);

    void glProgramUniform1d(int programId, int location, double value);

    void glProgramUniform2d(int programId, int location, double v0, double v1);

    void glProgramUniform3d(int programId, int location, double v0, double v1, double v2);

    void glProgramUniform4d(int programId, int location, double v0, double v1, double v2, double v3);

    void glProgramUniformMatrix2f(int programId, int location, boolean needsTranspose, FloatBuffer data);

    void glProgramUniformMatrix3f(int programId, int location, boolean needsTranspose, FloatBuffer data);

    void glProgramUniformMatrix4f(int programId, int location, boolean needsTranspose, FloatBuffer data);

    void glProgramUniformMatrix2d(int programId, int location, boolean needsTranspose, DoubleBuffer data);

    void glProgramUniformMatrix3d(int programId, int location, boolean needsTranspose, DoubleBuffer data);

    void glProgramUniformMatrix4d(int programId, int location, boolean needsTranspose, DoubleBuffer data);        

    void glTextureParameteri(int textureId, int pName, int val);

    void glTextureParameterf(int textureId, int pName, float val);

    void glTextureSubImage1d(int textureId, int level, int xOffset, int width, int format, int type, ByteBuffer pixels);

    void glTextureSubImage2d(int textureId, int level, int xOffset, int yOffset, int width, int height, int format, int type, ByteBuffer pixels);

    void glTextureSubImage3d(int textureId, int level, int xOffset, int yOffset, int zOffset, int width, int height, int depth, int format, int type, ByteBuffer pixels);

    void glTextureStorage1d(int textureId, int levels, int internalFormat, int width);

    void glTextureStorage2d(int textureId, int levels, int internalFormat, int width, int height);

    void glTextureStorage3d(int textureId, int levels, int internalFormat, int width, int height, int depth);

    void glGenerateTextureMipmap(int textureId);

    void glBindTextureUnit(int unit, int textureId);

    int glGetNamedBufferParameteri(int bufferId, int pName);
    
    void glBlitNamedFramebuffer(int readFramebuffer, int drawFramebuffer, int srcX, int srcY, int srcX1, int srcY1, int dstX0, int dstY0, int dstX1, int dstY1, int mask, int filter);
}
