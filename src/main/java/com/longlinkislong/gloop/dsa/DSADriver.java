/* 
 * Copyright (c) 2015, longlinkislong.com
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 *
 * * Redistributions of source code must retain the above copyright notice, this
 *   list of conditions and the following disclaimer.
 * * Redistributions in binary form must reproduce the above copyright notice,
 *   this list of conditions and the following disclaimer in the documentation
 *   and/or other materials provided with the distribution.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS"
 * AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE
 * IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE
 * ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT HOLDER OR CONTRIBUTORS BE
 * LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR
 * CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF
 * SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS
 * INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN
 * CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE)
 * ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE
 * POSSIBILITY OF SUCH DAMAGE.
 */
package com.longlinkislong.gloop.dsa;

import java.nio.ByteBuffer;
import java.nio.DoubleBuffer;
import java.nio.FloatBuffer;
import java.nio.IntBuffer;

/**
 * An implementation of the ARB_direct_state_access plugin to OpenGL.
 *
 * @author zmichaels
 * @since 15.07.10
 */
public interface DSADriver {

    void glInvalidateBufferSubData(int bufferId, int offset, int length);
    
    void glInvalidateBufferData(int bufferId);
    
    void glInvalidateTexSubImage(int texId, int level, int xOffset, int yOffset, int zOffset, int width, int height, int depth);
    
    void glInvalidateTexImage(int texId, int level);
    
    void glReadPixels(int x, int y, int w, int h, int format, int type, long ptr);
    
    void glReadPixels(int x, int y, int w, int h, int format, int type, ByteBuffer buffer);
    
    void glDrawBuffers(IntBuffer attachments);
    
    void glDeleteFramebuffers(int fbId);
    
    int glCheckFramebufferStatus(int fbId);
    
    void glBindFramebuffer(int target, int fb);
    
    void glColorMask(boolean red, boolean green, boolean blue, boolean alpha);
    
    void glDepthMask(boolean depth);
    
    void glStencilMask(int stencilMask);
    
    void glPointSize(float ps);
    
    void glLineWidth(float lw);
    
    void glFrontFace(int ff);
    
    void glCullFace(int cullMode);
    
    void glPolygonMode(int face, int mode);
    
    void glPolygonOffset(float factor, float units);
    
    void glDeleteProgram(int pId);
    
    int glCreateProgram();
    
    void glShaderStorageBlockBinding(int pId, int sbId, int sbb);        
    
    int glGetUniformBlockIndex(int pId, CharSequence name);
    
    void glUniformBlockBinding(int pId, int ubId, int ubb);
    
    int glGetProgramResourceLocation(int pId, int progInf, CharSequence name);
    
    void glBindBufferBase(int target, int index, int bufferId);
    
    void glDispatchCompute(int x, int y, int z);
    
    void glDetachShader(int pId, int shId);
    
    void glAttachShader(int pId, int shId);
    
    void glLinkProgram(int pId);
    
    int glGetProgrami(int pId, int param);
    
    String glGetProgramInfoLog(int pId, int length);
    
    void glTransformFeedbackVaryings(int pId, CharSequence[] varyings, int type);
    
    void glBindAttribLocation(int pId, int index, CharSequence name);
    
    int glGetUniformLocation(int pId, CharSequence uName);
    
    void glUseProgram(int pId);
    
    void glSamplerParameterf(int target, int sId, float value);
    
    void glBindSampler(int unit, int sId);

    int glGenSamplers();

    void glDeleteSamplers(int sId);

    void glSamplerParameteri(int pId, int sId, int value);

    void glScissor(int left, int bottom, int width, int height);

    String glGetShaderInfoLog(int shId, int length);

    void glDeleteShader(int shId);

    int glCreateShader(int type);

    void glShaderSource(int shId, CharSequence src);

    void glCompileShader(int shId);

    int glGetShaderi(int shId, int pId);

    int glGetTexLevelParameteri(int target, int i, int id);

    void glTexBuffer(int target, int internalFormat, int bufferId);

    void glBindTexture(int target, int texId);

    void glTexSubImage2D(int target, int level, int xOffset, int yOffset, int width, int height, int format, int type, long ptr);

    void glDeleteTextures(int texId);

    int getOpenGLVersion();

    String glGetString(int strId);

    float glGetFloat(int floatId);

    int glGetInteger(int intId);

    void glVertexAttribDivisor(int index, int divisor);

    void glEnableVertexAttribArray(int index);

    void glVertexAttribLPointer(int index, int size, int type, int stride, long offset);

    void glVertexAttribPointer(int index, int size, int type, boolean normalized, int stride, long offset);

    void glBeginTransformFeedback(int mode);

    void glEndTransformFeedback();

    void glDrawArrays(int mode, int start, int count);

    void glDrawElements(int mode, int count, int type, long offset);

    void glDrawArraysInstanced(int mode, int first, int count, int instanceCount);

    void glDrawElementsInstanced(int mode, int count, int type, long offset, int instanceCount);

    void glMultiDrawArrays(int mode, IntBuffer first, IntBuffer count);

    void glDrawArraysIndirect(int mode, long offset);

    void glDrawElementsIndirect(int mode, int index, long offset);

    void glBindBuffer(int target, int buffId);

    void glBindVertexArray(int vaoId);

    int glGenVertexArrays();

    void glDeleteVertexArrays(int vao);

    int glGetError();

    void glViewport(int x, int y, int width, int height);

    void glGetTextureImage(int texture, int level, int format, int type, int bufferSize, ByteBuffer pixels);

    /**
     * Checks if the current context supports this driver.
     *
     * @return true if all features of the driver are supported.
     * @since 15.07.10
     */
    boolean isSupported();

    void glBeginConditionalRender(int queryId, int mode);

    void glEndConditionalRender();

    int glGenQueries();

    void glDeleteQueries(int queryId);

    void glBeginQuery(int condition, int queryId);

    void glEndQuery(int condition);

    void glDepthFunc(int depthFunc);

    void glClearColor(float red, float green, float blue, float alpha);

    void glClearDepth(double depth);

    void glClear(int bitfield);

    void glDeleteBuffers(int bufferId);

    void glEnable(int capability);

    void glDisable(int capability);

    void glBlendEquationSeparate(int rgb, int alpha);

    void glBlendFuncSeparate(int rgbSrc, int rgbDst, int aSrc, int aDst);

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
