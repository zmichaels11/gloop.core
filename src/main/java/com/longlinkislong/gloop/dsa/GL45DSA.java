/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.longlinkislong.gloop.dsa;

import java.nio.ByteBuffer;
import java.nio.DoubleBuffer;
import java.nio.FloatBuffer;
import org.lwjgl.opengl.GL;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL41;
import org.lwjgl.opengl.GL45;

/**
 * An implementation of ARB_direct_state_access that uses OpenGL4.5 calls.
 *
 * @author zmichaels
 * @since 15.07.14
 */
public class GL45DSA implements DSADriver {

    @Override
    public String toString() {
        return "GL45DSA";
    }

    @Override
    public int glCreateFramebuffers() {
        final int out = GL45.glCreateFramebuffers();

        assert GL11.glGetError() == GL11.GL_NO_ERROR : String.format("glCreateFramebuffers() = %d failed!", out);
        return out;
    }

    @Override
    public int glCreateBuffers() {
        final int out = GL45.glCreateBuffers();

        assert GL11.glGetError() == GL11.GL_NO_ERROR : String.format("glCreateBuffers() = %d failed!", out);
        return out;
    }

    @Override
    public int glCreateTextures(int target) {
        final int out = GL45.glCreateTextures(target);

        assert GL11.glGetError() == GL11.GL_NO_ERROR : String.format("glCreateTextures(%d) = %d failed!", target, out);
        return out;
    }

    @Override
    public boolean isSupported() {
        return GL.getCurrent().getCapabilities().OpenGL45;
    }

    @Override
    public void glGetNamedBufferSubData(int bufferId, long offset, ByteBuffer out) {
        GL45.glGetNamedBufferSubData(bufferId, offset, out);
        assert GL11.glGetError() == GL11.GL_NO_ERROR : String.format("glGetNamedBufferSubData(%d, %d, [out]) failed!", bufferId, offset);
    }

    @Override
    public int glGetNamedBufferParameteri(int bufferId, int pName) {
        final int out = GL45.glGetNamedBufferParameteri(bufferId, pName);
        assert GL11.glGetError() == GL11.GL_NO_ERROR : String.format("glGetNamedBufferParameteri(%d, %d) = %d failed!", bufferId, pName, out);
        return out;
    }

    @Override
    public void glNamedBufferData(int bufferId, long size, int usage) {
        GL45.glNamedBufferData(bufferId, size, usage);
        assert GL11.glGetError() == GL11.GL_NO_ERROR : String.format("glNamedBufferData(%d, %d, %d) failed!", bufferId, size, usage);
    }

    @Override
    public void glNamedBufferData(int bufferId, ByteBuffer data, int usage) {
        GL45.glNamedBufferData(bufferId, data, usage);
        assert GL11.glGetError() == GL11.GL_NO_ERROR : String.format("glNamedBufferData(%d, [data], %d) failed!", bufferId, usage);
    }

    @Override
    public void glNamedBufferSubData(int buffer, long offset, ByteBuffer data) {
        GL45.glNamedBufferSubData(buffer, offset, data);
        assert GL11.glGetError() == GL11.GL_NO_ERROR : String.format("glNamedBufferSubData(%d, %d, [data]) failed!", buffer, offset);
    }

    @Override
    public void glNamedBufferStorage(int bufferId, ByteBuffer data, int flags) {
        GL45.glNamedBufferStorage(bufferId, data, flags);
        assert GL11.glGetError() == GL11.GL_NO_ERROR : String.format("glNamedBufferStorage(%d, [data], %d) failed!", bufferId, flags);
    }

    @Override
    public void glNamedBufferStorage(int bufferId, long size, int flags) {
        GL45.glNamedBufferStorage(bufferId, size, flags);
        assert GL11.glGetError() == GL11.GL_NO_ERROR : String.format("glNamedBufferStorage(%d, %d, %d) failed!", bufferId, size, flags);
    }

    @Override
    public ByteBuffer glMapNamedBufferRange(int bufferId, long offset, long length, int access, ByteBuffer recycled) {
        final ByteBuffer out = GL45.glMapNamedBufferRange(bufferId, offset, length, access, recycled);
        assert GL11.glGetError() == GL11.GL_NO_ERROR : String.format("glMapNamedBufferRange(%d, %d, %d, %d, [recycled]) = [data] failed!", bufferId, offset, length, access);
        return out;
    }

    @Override
    public void glUnmapNamedBuffer(int bufferId) {
        GL45.glUnmapNamedBuffer(bufferId);
        assert GL11.glGetError() == GL11.GL_NO_ERROR : String.format("glUnmapNamedBuffer(%d) failed!", bufferId);
    }

    @Override
    public void glCopyNamedBufferSubData(int readBufferId, int writeBufferId, long readOffset, long writeOffset, long size) {
        GL45.glCopyNamedBufferSubData(readBufferId, writeBufferId, readOffset, writeOffset, size);
        assert GL11.glGetError() == GL11.GL_NO_ERROR : String.format("glCopyNamedBufferSubData(%d, %d, %d, %d, %d) failed!", readBufferId, writeBufferId, readOffset, writeOffset, size);
    }

    @Override
    public void glProgramUniform1f(int programId, int location, float value) {
        GL41.glProgramUniform1f(programId, location, value);
        assert GL11.glGetError() == GL11.GL_NO_ERROR : String.format("glProgramUniform1f(%d, %d, %f) failed!", programId, location, value);
    }

    @Override
    public void glProgramUniform2f(int programId, int location, float v0, float v1) {
        GL41.glProgramUniform2f(programId, location, v0, v1);
        assert GL11.glGetError() == GL11.GL_NO_ERROR : String.format("glProgramUniform2f(%d, %d, %f, %f) failed!", programId, location, v0, v1);
    }

    @Override
    public void glProgramUniform3f(int programId, int location, float v0, float v1, float v2) {
        GL41.glProgramUniform3f(programId, location, v0, v1, v2);
        assert GL11.glGetError() == GL11.GL_NO_ERROR : String.format("glProgramUniform3f(%d, %d, %f, %f, %f) failed!", programId, location, v0, v1, v2);
    }

    @Override
    public void glProgramUniform4f(int programId, int location, float v0, float v1, float v2, float v3) {
        GL41.glProgramUniform4f(programId, location, v0, v1, v2, v3);
        assert GL11.glGetError() == GL11.GL_NO_ERROR : String.format("glProgramUniform4f(%d, %d, %f, %f, %f, %f) failed!", programId, location, v0, v1, v2, v3);
    }

    @Override
    public void glProgramUniform1d(int programId, int location, double value) {
        GL41.glProgramUniform1d(programId, location, value);
        assert GL11.glGetError() == GL11.GL_NO_ERROR : String.format("glProgramUniform1d(%d, %d, %f) failed!", programId, location, value);
    }

    @Override
    public void glProgramUniform2d(int programId, int location, double v0, double v1) {
        GL41.glProgramUniform2d(programId, location, v0, v1);
        assert GL11.glGetError() == GL11.GL_NO_ERROR : String.format("glProgramUniform2d(%d, %d, %f, %f) failed!", programId, location, v0, v1);
    }

    @Override
    public void glProgramUniform3d(int programId, int location, double v0, double v1, double v2) {
        GL41.glProgramUniform3d(programId, location, v0, v1, v2);
        assert GL11.glGetError() == GL11.GL_NO_ERROR : String.format("glProgramUniform3d(%d, %d, %f, %f, %f) failed!", programId, location, v0, v1, v2);
    }

    @Override
    public void glProgramUniform4d(int programId, int location, double v0, double v1, double v2, double v3) {
        GL41.glProgramUniform4d(programId, location, v0, v1, v2, v3);
        assert GL11.glGetError() == GL11.GL_NO_ERROR : String.format("glProgramUniform4d(%d, %d, %f, %f, %f, %f) failed!", programId, location, v0, v1, v2, v3);
    }

    @Override
    public void glProgramUniformMatrix2f(int programId, int location, boolean needsTranspose, FloatBuffer data) {
        GL41.glProgramUniformMatrix2fv(programId, location, needsTranspose, data);
        assert GL11.glGetError() == GL11.GL_NO_ERROR : String.format("glProgramUniform Matrix2fv(%d, %d, %s, [data]) failed!", programId, location, needsTranspose);
    }

    @Override
    public void glProgramUniformMatrix3f(int programId, int location, boolean needsTranspose, FloatBuffer data) {
        GL41.glProgramUniformMatrix3fv(programId, location, needsTranspose, data);
        assert GL11.glGetError() == GL11.GL_NO_ERROR : String.format("glProgramUniform Matrix3fv(%d, %d, %s, [data]) failed!", programId, location, needsTranspose);
    }

    @Override
    public void glProgramUniformMatrix4f(int programId, int location, boolean needsTranspose, FloatBuffer data) {
        GL41.glProgramUniformMatrix4fv(programId, location, needsTranspose, data);
        assert GL11.glGetError() == GL11.GL_NO_ERROR : String.format("glProgramUniform Matrix4fv(%d, %d, %s, [data]) failed!", programId, location, needsTranspose);
    }

    @Override
    public void glProgramUniformMatrix2d(int programId, int location, boolean needsTranspose, DoubleBuffer data) {
        GL41.glProgramUniformMatrix2dv(programId, location, needsTranspose, data);
        assert GL11.glGetError() == GL11.GL_NO_ERROR : String.format("glProgramUniform Matrix2dv(%d, %d, %s, [data]) failed!", programId, location, needsTranspose);
    }

    @Override
    public void glProgramUniformMatrix3d(int programId, int location, boolean needsTranspose, DoubleBuffer data) {
        GL41.glProgramUniformMatrix3dv(programId, location, needsTranspose, data);
        assert GL11.glGetError() == GL11.GL_NO_ERROR : String.format("glProgramUniform Matrix3dv(%d, %d, %s, [data]) failed!", programId, location, needsTranspose);
    }

    @Override
    public void glProgramUniformMatrix4d(int programId, int location, boolean needsTranspose, DoubleBuffer data) {
        GL41.glProgramUniformMatrix4dv(programId, location, needsTranspose, data);
        assert GL11.glGetError() == GL11.GL_NO_ERROR : String.format("glProgramUniform Matrix4dv(%d, %d, %s, [data]) failed!", programId, location, needsTranspose);
    }

    @Override
    public void glTextureParameteri(int textureId, int pName, int val) {
        GL45.glTextureParameteri(textureId, pName, val);
        assert GL11.glGetError() == GL11.GL_NO_ERROR : String.format("glTextureParameteri(%d, %d, %d) failed!", textureId, pName, val);
    }

    @Override
    public void glTextureParameterf(int textureId, int pName, float val) {
        GL45.glTextureParameterf(textureId, pName, val);
        assert GL11.glGetError() == GL11.GL_NO_ERROR : String.format("glTextureParameterf(%d, %d, %d) failed!", textureId, pName, val);
    }

    @Override
    public void glTextureSubImage1d(int textureId, int level, int xOffset, int width, int format, int type, ByteBuffer pixels) {
        GL45.glTextureSubImage1D(textureId, level, xOffset, width, format, type, pixels);
        assert GL11.glGetError() == GL11.GL_NO_ERROR : String.format("glTextureSubImage1D(%d, %d, %d, %d, %d, %d, [data]) failed!", textureId, level, xOffset, width, format, type);
    }

    @Override
    public void glTextureSubImage2d(int textureId, int level, int xOffset, int yOffset, int width, int height, int format, int type, ByteBuffer pixels) {
        GL45.glTextureSubImage2D(textureId, level, xOffset, yOffset, width, height, format, type, pixels);
        assert GL11.glGetError() == GL11.GL_NO_ERROR : String.format("glTextureSubImage2D(%d, %d, %d, %d, %d, %d, %d, %d, [data]) failed!", textureId, level, xOffset, yOffset, width, height, format, type);
    }

    @Override
    public void glTextureSubImage3d(int textureId, int level, int xOffset, int yOffset, int zOffset, int width, int height, int depth, int format, int type, ByteBuffer pixels) {
        GL45.glTextureSubImage3D(textureId, level, xOffset, yOffset, zOffset, width, height, depth, format, type, pixels);
        assert GL11.glGetError() == GL11.GL_NO_ERROR : String.format("glTextureSubImage3D(%d, %d, %d, %d, %d, %d, %d, %d, %d, %d, [data] failed!", textureId, level, xOffset, yOffset, zOffset, width, height, depth, format, type);
    }

    @Override
    public void glBindTextureUnit(int unit, int textureId) {
        GL45.glBindTextureUnit(unit, textureId);
        assert GL11.glGetError() == GL11.GL_NO_ERROR : String.format("glBindTextureUnit(%d, %d) failed!", unit, textureId);
    }

    public static DSADriver getInstance() {
        return Holder.INSTANCE;
    }

    @Override
    public void glProgramUniform1i(int programId, int location, int value) {
        GL41.glProgramUniform1i(programId, location, value);
        assert GL11.glGetError() == GL11.GL_NO_ERROR : String.format("glProgramUniform1i(%d, %d, %d) failed!", programId, location, value);
    }

    @Override
    public void glProgramUniform2i(int programId, int location, int v0, int v1) {
        GL41.glProgramUniform2i(programId, location, v0, v1);
        assert GL11.glGetError() == GL11.GL_NO_ERROR : String.format("glProgramUniform2i(%d, %d, %d, %d) failed!", programId, location, v0, v1);
    }

    @Override
    public void glProgramUniform3i(int programId, int location, int v0, int v1, int v2) {
        GL41.glProgramUniform3i(programId, location, v0, v1, v2);
        assert GL11.glGetError() == GL11.GL_NO_ERROR : String.format("glProgramUniform3i(%d, %d, %d, %d, %d) failed!", programId, location, v0, v1, v2);
    }

    @Override
    public void glProgramUniform4i(int programId, int location, int v0, int v1, int v2, int v3) {
        GL41.glProgramUniform4i(programId, location, v0, v1, v2, v3);
        assert GL11.glGetError() == GL11.GL_NO_ERROR : String.format("glProgramUniform4i(%d, %d, %d, %d, %d, %d) failed!", programId, location, v0, v1, v2, v3);
    }

    @Override
    public void glTextureStorage1d(int textureId, int levels, int internalFormat, int width) {
        GL45.glTextureStorage1D(textureId, levels, internalFormat, width);
        assert GL11.glGetError() == GL11.GL_NO_ERROR : String.format("glTextureStorage1D(%d, %d, %d, %d) failed!", textureId, levels, internalFormat, width);
    }

    @Override
    public void glTextureStorage2d(int textureId, int levels, int internalFormat, int width, int height) {
        GL45.glTextureStorage2D(textureId, levels, internalFormat, width, height);
        assert GL11.glGetError() == GL11.GL_NO_ERROR : String.format("glTextureStorage2D(%d, %d, %d, %d, %d) failed!", textureId, levels, internalFormat, width, height);
    }

    @Override
    public void glTextureStorage3d(int textureId, int levels, int internalFormat, int width, int height, int depth) {
        GL45.glTextureStorage3D(textureId, levels, internalFormat, width, height, depth);
        assert GL11.glGetError() == GL11.GL_NO_ERROR : String.format("glTextureStorage3D(%d, %d, %d, %d, %d, %d) failed!", textureId, levels, internalFormat, width, height, depth);
    }

    @Override
    public void glGenerateTextureMipmap(int textureId) {
        GL45.glGenerateTextureMipmap(textureId);
        assert GL11.glGetError() == GL11.GL_NO_ERROR : String.format("glGenerateTextureMipmap(%d) failed!", textureId);
    }

    @Override
    public void glNamedFramebufferTexture(int framebuffer, int attachment, int texture, int level) {
        GL45.glNamedFramebufferTexture(framebuffer, attachment, texture, level);
        assert GL11.glGetError() == GL11.GL_NO_ERROR : String.format("glNamedFramebufferTexture(%d, %d, %d, %d) failed!", framebuffer, attachment, texture, level);
    }

    @Override
    public void glNamedBufferReadPixels(int bufferId, int x, int y, int width, int height, int format, int type, long ptr) {
        FakeDSA.getInstance().glNamedBufferReadPixels(bufferId, x, y, width, height, format, type, ptr);
    }

    @Override
    public void glBlitNamedFramebuffer(int readFramebuffer, int drawFramebuffer, int srcX0, int srcY0, int srcX1, int srcY1, int dstX0, int dstY0, int dstX1, int dstY1, int mask, int filter) {
        GL45.glBlitNamedFramebuffer(readFramebuffer, drawFramebuffer, srcX0, srcY0, srcX1, srcY1, dstX0, dstX1, dstY0, dstY1, mask, filter);
    }

    private static class Holder {

        private static final DSADriver INSTANCE = new GL45DSA();
    }
}
