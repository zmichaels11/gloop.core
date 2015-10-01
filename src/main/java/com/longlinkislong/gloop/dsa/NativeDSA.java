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
 *
 * @author zmichaels
 */
public class NativeDSA implements EXTDSADriver {

    public static native void nglNamedFramebufferTexture1d(int framebuffer, int attachment, int texTarget, int texture, int level);

    public static native void nglNamedFramebufferTexture2d(int framebuffer, int attachment, int texTarget, int texture, int level);

    public static native void nglTextureParameteri(int textureId, int target, int pName, int val);

    public static native void nglTextureParameterf(int textureId, int target, int pName, float val);

    public static native void nglGenerateTextureMipmap(int textureId, int target);

    public static native void nglBindTextureUnit(int unit, int target, int textureId);

    public static native void nglTextureImage1d(int textureId, int target, int level, int internalFormat, int width, int border, int format, int type, long size);

    public static native void nglTextureImage1d(int texture, int target, int level, int internalFormat, int width, int border, int format, int type, ByteBuffer pixels);

    public static native void nglTextureImage2d(int texture, int target, int level, int internalFormat, int width, int height, int border, int format, int type, ByteBuffer pixels);

    public static native void nglTextureImage2d(int texture, int target, int level, int internalFormat, int width, int height, int border, int format, int type, long ptr);

    public static native void nglTextureImage3d(int texture, int target, int level, int internalFormat, int width, int height, int depth, int border, int format, int type, ByteBuffer pixels);

    public static native void nglTextureImage3d(int texture, int target, int level, int internalFormat, int width, int height, int depth, int border, int format, int type, long ptr);

    public static native void nglBlitNamedFramebuffer(int readFramebuffer, int drawFramebuffer, int srcX, int srcY, int srcX1, int srcY1, int dstX0, int dstY0, int dstX1, int dstY1, int mask, int filter);

    public static native int nglGetNamedBufferParameteri(int bufferId, int pName);

    public static native void nglBindTextureUnit(int unit, int textureId);

    public static native void nglGenerateTextureMipmap(int textureId);

    public static native void nglTextureStorage3d(int textureId, int levels, int internalFormat, int width, int height, int depth);

    public static native void nglTextureStorage2d(int textureId, int levels, int internalFormat, int width, int height);

    public static native void nglTextureStorage1d(int textureId, int levels, int internalFormat, int width);

    public static native void nglTextureSubImage3d(int textureId, int level, int xOffset, int yOffset, int zOffset, int width, int height, int depth, int format, int type, ByteBuffer pixels);

    public static native void nglTextureSubImage2d(int textureId, int level, int xOffset, int yOffset, int width, int height, int format, int type, ByteBuffer pixels);

    public static native void nglTextureSubImage1d(int textureId, int level, int xOffset, int width, int format, int type, ByteBuffer pixels);

    public static native void nglTextureParameterf(int textureId, int pName, float val);

    public static native void nglTextureParameteri(int textureId, int pName, int val);

    public static native void nglProgramUniformMatrix4d(int programId, int location, boolean needsTranspose, DoubleBuffer data);

    public static native void nglProgramUniformMatrix2d(int programId, int location, boolean needsTranspose, DoubleBuffer data);

    public static native void nglProgramUniformMatrix3d(int programId, int location, boolean needsTranspose, DoubleBuffer data);

    public static native void nglProgramUniformMatrix4f(int programId, int location, boolean needsTranspose, FloatBuffer data);

    public static native void nglProgramUniformMatrix3f(int programId, int location, boolean needsTranspose, FloatBuffer data);

    public static native void nglProgramUniformMatrix2f(int programId, int location, boolean needsTranspose, FloatBuffer data);

    public static native void nglProgramUniform4d(int programId, int location, double v0, double v1, double v2, double v3);

    public static native void nglProgramUniform3d(int programId, int location, double v0, double v1, double v2);

    public static native void nglProgramUniform2d(int programId, int location, double v0, double v1);

    public static native void nglProgramUniform4i(int programId, int location, int v0, int v1, int v2, int v3);

    public static native void nglProgramUniform3i(int programId, int location, int v0, int v1, int v2);

    public static native void nglProgramUniform1d(int programId, int location, double value);

    public static native void nglProgramUniform2i(int programId, int location, int v0, int v1);

    public static native void nglProgramUniform1i(int programId, int location, int value);

    public static native void nglProgramUniform4f(int programId, int location, float v0, float v1, float v2, float v3);

    public static native void nglProgramUniform3f(int programId, int location, float v0, float v1, float v2);

    public static native void nglProgramUniform2f(int programId, int location, float v0, float v1);

    public static native void nglProgramUniform1f(int programId, int location, float value);

    public static native void nglCopyNamedBufferSubData(int readBufferId, int writeBufferId, long readOffset, long writeOffset, long size);

    public static native void nglUnmapNamedBuffer(int bufferId);

    private static native ByteBuffer nglMapNamedBufferRange(int bufferId, long offset, long length, int access, ByteBuffer recycled);

    public static native void nglGetNamedBufferSubData(int bufferId, long offset, ByteBuffer out);

    public static native void nglNamedBufferStorage(int bufferId, long size, int flags);

    public static native void nglNamedBufferSubData(int buffer, long offset, ByteBuffer data);

    public static native void nglNamedBufferData(int bufferId, ByteBuffer data, int usage);

    public static native void nglNamedFramebufferTexture(int framebuffer, int attachment, int texture, int level);

    public static native void nglNamedBufferReadPixels(int bufferId, int x, int y, int width, int height, int format, int type, long ptr);

    public static native int nglCreateFramebuffers();

    public static native int nglCreateTextures(int target);

    public static native int nglCreateBuffers();

    public static native void nglGetTextureImage(int texture, int level, int format, int type, int bufferSize, ByteBuffer pixels);

    public static native void nglGetTextureImage(int texture, int target, int level, int format, int type, int bufferSize, ByteBuffer data);

    public static native void nglNamedBufferData(int bufferId, long size, int usage);

    public static native void nglNamedBufferStorage(int bufferId, ByteBuffer data, int flags);

    @Override
    public void glNamedFramebufferTexture1D(int framebuffer, int attachment, int texTarget, int texture, int level) {
        nglNamedFramebufferTexture1d(framebuffer, attachment, texTarget, texture, level);
    }

    @Override
    public void glNamedFramebufferTexture2D(int framebuffer, int attachment, int texTarget, int texture, int level) {
        nglNamedFramebufferTexture2d(framebuffer, attachment, texTarget, texture, level);
    }

    @Override
    public void glTextureParameteri(int textureId, int target, int pName, int val) {
        nglTextureParameteri(textureId, target, pName, val);
    }

    @Override
    public void glTextureParameterf(int textureId, int target, int pName, float val) {
        nglTextureParameterf(textureId, target, pName, val);
    }

    @Override
    public void glGenerateTextureMipmap(int textureId, int target) {
        nglGenerateTextureMipmap(textureId, target);
    }

    @Override
    public void glBindTextureUnit(int unit, int target, int textureId) {
        nglBindTextureUnit(unit, target, textureId);
    }

    @Override
    public void glTextureImage1d(int textureId, int target, int level, int internalFormat, int width, int border, int format, int type, long size) {
        nglTextureImage1d(textureId, target, level, internalFormat, width, border, format, type, size);
    }

    @Override
    public void glTextureImage1d(int texture, int target, int level, int internalFormat, int width, int border, int format, int type, ByteBuffer pixels) {
        nglTextureImage1d(texture, target, level, internalFormat, width, border, format, type, pixels);
    }

    @Override
    public void glTextureImage2d(int texture, int target, int level, int internalFormat, int width, int height, int border, int format, int type, ByteBuffer pixels) {
        nglTextureImage2d(texture, target, level, internalFormat, width, height, border, format, type, pixels);
    }

    @Override
    public void glTextureImage2d(int texture, int target, int level, int internalFormat, int width, int height, int border, int format, int type, long ptr) {
        nglTextureImage2d(texture, target, level, internalFormat, width, height, border, format, type, ptr);
    }

    @Override
    public void glTextureImage3d(int texture, int target, int level, int internalFormat, int width, int height, int depth, int border, int format, int type, ByteBuffer pixels) {
        nglTextureImage3d(texture, target, level, internalFormat, width, height, depth, border, format, type, pixels);
    }

    @Override
    public void glTextureImage3d(int texture, int target, int level, int internalFormat, int width, int height, int depth, int border, int format, int type, long ptr) {
        nglTextureImage3d(texture, target, level, internalFormat, width, height, depth, border, format, type, ptr);
    }

    @Override
    public void glGetTextureImage(int texture, int target, int level, int format, int type, int bufferSize, ByteBuffer data) {
        nglGetTextureImage(texture, target, level, format, type, bufferSize, data);
    }

    @Override
    public void glGetTextureImage(int texture, int level, int format, int type, int bufferSize, ByteBuffer pixels) {
        nglGetTextureImage(texture, level, format, type, bufferSize, pixels);
    }

    @Override
    public boolean isSupported() {
        return true;
    }

    @Override
    public int glCreateBuffers() {
        return nglCreateBuffers();
    }

    @Override
    public int glCreateTextures(int target) {
        return nglCreateTextures(target);
    }

    @Override
    public int glCreateFramebuffers() {
        return nglCreateFramebuffers();
    }

    @Override
    public void glNamedBufferReadPixels(int bufferId, int x, int y, int width, int height, int format, int type, long ptr) {
        nglNamedBufferReadPixels(bufferId, x, y, width, height, format, type, ptr);
    }

    @Override
    public void glNamedFramebufferTexture(int framebuffer, int attachment, int texture, int level) {
        nglNamedFramebufferTexture(framebuffer, attachment, texture, level);
    }

    @Override
    public void glNamedBufferData(int bufferId, long size, int usage) {
        nglNamedBufferData(bufferId, size, usage);
    }

    @Override
    public void glNamedBufferData(int bufferId, ByteBuffer data, int usage) {
        nglNamedBufferData(bufferId, data, usage);
    }

    @Override
    public void glNamedBufferSubData(int buffer, long offset, ByteBuffer data) {
        nglNamedBufferSubData(buffer, offset, data);
    }

    @Override
    public void glNamedBufferStorage(int bufferId, ByteBuffer data, int flags) {
        nglNamedBufferStorage(bufferId, data, flags);
    }

    @Override
    public void glNamedBufferStorage(int bufferId, long size, int flags) {
        nglNamedBufferStorage(bufferId, size, flags);
    }

    @Override
    public void glGetNamedBufferSubData(int bufferId, long offset, ByteBuffer out) {
        nglGetNamedBufferSubData(bufferId, offset, out);
    }

    @Override
    public ByteBuffer glMapNamedBufferRange(int bufferId, long offset, long length, int access, ByteBuffer recycled) {
        return nglMapNamedBufferRange(bufferId, offset, length, access, recycled);
    }

    @Override
    public void glUnmapNamedBuffer(int bufferId) {
        nglUnmapNamedBuffer(bufferId);
    }

    @Override
    public void glCopyNamedBufferSubData(int readBufferId, int writeBufferId, long readOffset, long writeOffset, long size) {
        nglCopyNamedBufferSubData(readBufferId, writeBufferId, readOffset, writeOffset, size);
    }

    @Override
    public void glProgramUniform1f(int programId, int location, float value) {
        nglProgramUniform1f(programId, location, value);
    }

    @Override
    public void glProgramUniform2f(int programId, int location, float v0, float v1) {
        nglProgramUniform2f(programId, location, v0, v1);
    }

    @Override
    public void glProgramUniform3f(int programId, int location, float v0, float v1, float v2) {
        nglProgramUniform3f(programId, location, v0, v1, v2);
    }

    @Override
    public void glProgramUniform4f(int programId, int location, float v0, float v1, float v2, float v3) {
        nglProgramUniform4f(programId, location, v0, v1, v2, v3);
    }

    @Override
    public void glProgramUniform1i(int programId, int location, int value) {
        nglProgramUniform1i(programId, location, value);
    }

    @Override
    public void glProgramUniform2i(int programId, int location, int v0, int v1) {
        nglProgramUniform2i(programId, location, v0, v1);
    }

    @Override
    public void glProgramUniform3i(int programId, int location, int v0, int v1, int v2) {
        nglProgramUniform3i(programId, location, v0, v1, v2);
    }

    @Override
    public void glProgramUniform4i(int programId, int location, int v0, int v1, int v2, int v3) {
        nglProgramUniform4i(programId, location, v0, v1, v2, v3);
    }

    @Override
    public void glProgramUniform1d(int programId, int location, double value) {
        nglProgramUniform1d(programId, location, value);
    }

    @Override
    public void glProgramUniform2d(int programId, int location, double v0, double v1) {
        nglProgramUniform2d(programId, location, v0, v1);
    }

    @Override
    public void glProgramUniform3d(int programId, int location, double v0, double v1, double v2) {
        nglProgramUniform3d(programId, location, v0, v1, v2);
    }

    @Override
    public void glProgramUniform4d(int programId, int location, double v0, double v1, double v2, double v3) {
        nglProgramUniform4d(programId, location, v0, v1, v2, v3);
    }

    @Override
    public void glProgramUniformMatrix2f(int programId, int location, boolean needsTranspose, FloatBuffer data) {
        nglProgramUniformMatrix2f(programId, location, needsTranspose, data);
    }

    @Override
    public void glProgramUniformMatrix3f(int programId, int location, boolean needsTranspose, FloatBuffer data) {
        nglProgramUniformMatrix3f(programId, location, needsTranspose, data);
    }

    @Override
    public void glProgramUniformMatrix4f(int programId, int location, boolean needsTranspose, FloatBuffer data) {
        nglProgramUniformMatrix4f(programId, location, needsTranspose, data);
    }

    @Override
    public void glProgramUniformMatrix2d(int programId, int location, boolean needsTranspose, DoubleBuffer data) {
        nglProgramUniformMatrix2d(programId, location, needsTranspose, data);
    }

    @Override
    public void glProgramUniformMatrix3d(int programId, int location, boolean needsTranspose, DoubleBuffer data) {
        nglProgramUniformMatrix3d(programId, location, needsTranspose, data);
    }

    @Override
    public void glProgramUniformMatrix4d(int programId, int location, boolean needsTranspose, DoubleBuffer data) {
        nglProgramUniformMatrix4d(programId, location, needsTranspose, data);
    }

    @Override
    public void glTextureParameteri(int textureId, int pName, int val) {
        nglTextureParameteri(textureId, pName, val);
    }

    @Override
    public void glTextureParameterf(int textureId, int pName, float val) {
        nglTextureParameterf(textureId, pName, val);
    }

    @Override
    public void glTextureSubImage1d(int textureId, int level, int xOffset, int width, int format, int type, ByteBuffer pixels) {
        nglTextureSubImage1d(textureId, level, xOffset, width, format, type, pixels);
    }

    @Override
    public void glTextureSubImage2d(int textureId, int level, int xOffset, int yOffset, int width, int height, int format, int type, ByteBuffer pixels) {
        nglTextureSubImage2d(textureId, level, xOffset, yOffset, width, height, format, type, pixels);
    }

    @Override
    public void glTextureSubImage3d(int textureId, int level, int xOffset, int yOffset, int zOffset, int width, int height, int depth, int format, int type, ByteBuffer pixels) {
        nglTextureSubImage3d(textureId, level, xOffset, yOffset, zOffset, width, height, depth, format, type, pixels);
    }

    @Override
    public void glTextureStorage1d(int textureId, int levels, int internalFormat, int width) {
        nglTextureStorage1d(textureId, levels, internalFormat, width);
    }

    @Override
    public void glTextureStorage2d(int textureId, int levels, int internalFormat, int width, int height) {
        nglTextureStorage2d(textureId, levels, internalFormat, width, height);
    }

    @Override
    public void glTextureStorage3d(int textureId, int levels, int internalFormat, int width, int height, int depth) {
        nglTextureStorage3d(textureId, levels, internalFormat, width, height, depth);
    }

    @Override
    public void glGenerateTextureMipmap(int textureId) {
        nglGenerateTextureMipmap(textureId);
    }

    @Override
    public void glBindTextureUnit(int unit, int textureId) {
        nglBindTextureUnit(unit, textureId);
    }

    @Override
    public int glGetNamedBufferParameteri(int bufferId, int pName) {
        return nglGetNamedBufferParameteri(bufferId, pName);
    }

    @Override
    public void glBlitNamedFramebuffer(int readFramebuffer, int drawFramebuffer, int srcX, int srcY, int srcX1, int srcY1, int dstX0, int dstY0, int dstX1, int dstY1, int mask, int filter) {
        nglBlitNamedFramebuffer(readFramebuffer, drawFramebuffer, srcX, srcY, srcX1, srcY1, dstX0, dstY0, dstX1, dstY1, mask, filter);
    }
}
