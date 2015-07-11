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
import org.lwjgl.opengl.GL41;
import org.lwjgl.opengl.GL45;

/**
 *
 * @author zmichaels
 */
public class GL45DSA implements DSADriver {

    @Override
    public String toString() {
        return "GL45DSA";
    }

    @Override
    public int glCreateFramebuffers() {
        return GL45.glCreateFramebuffers();
    }

    @Override
    public int glCreateBuffers() {
        return GL45.glCreateBuffers();
    }

    @Override
    public int glCreateTextures(int target) {
        return GL45.glCreateTextures(target);
    }

    @Override
    public boolean isSupported() {
        return GL.getCurrent().getCapabilities().OpenGL45;
    }

    @Override
    public void glGetNamedBufferSubData(int bufferId, long offset, ByteBuffer out) {
        GL45.glGetNamedBufferSubData(bufferId, offset, out);
    }

    @Override
    public int glGetNamedBufferParameteri(int bufferId, int pName) {
        return GL45.glGetNamedBufferParameteri(bufferId, pName);
    }

    @Override
    public void glNamedBufferData(int bufferId, long size, int usage) {
        GL45.glNamedBufferData(bufferId, size, usage);
    }

    @Override
    public void glNamedBufferData(int bufferId, ByteBuffer data, int usage) {
        GL45.glNamedBufferData(bufferId, data, usage);
    }

    @Override
    public void glNamedBufferSubData(int buffer, long offset, ByteBuffer data) {
        GL45.glNamedBufferSubData(buffer, offset, data);
    }

    @Override
    public void glNamedBufferStorage(int bufferId, ByteBuffer data, int flags) {
        GL45.glNamedBufferStorage(bufferId, data, flags);
    }

    @Override
    public void glNamedBufferStorage(int bufferId, long size, int flags) {
        GL45.glNamedBufferStorage(bufferId, size, flags);
    }

    @Override
    public ByteBuffer glMapNamedBufferRange(int bufferId, long offset, long length, int access, ByteBuffer recycled) {
        return GL45.glMapNamedBufferRange(bufferId, offset, length, access, recycled);
    }

    @Override
    public void glUnmapNamedBuffer(int bufferId) {
        GL45.glUnmapNamedBuffer(bufferId);
    }

    @Override
    public void glCopyNamedBufferSubData(int readBufferId, int writeBufferId, long readOffset, long writeOffset, long size) {
        GL45.glCopyNamedBufferSubData(readBufferId, writeBufferId, readOffset, writeOffset, size);
    }

    @Override
    public void glProgramUniform1f(int programId, int location, float value) {
        GL41.glProgramUniform1f(programId, location, value);
    }

    @Override
    public void glProgramUniform2f(int programId, int location, float v0, float v1) {
        GL41.glProgramUniform2f(programId, location, v0, v1);
    }

    @Override
    public void glProgramUniform3f(int programId, int location, float v0, float v1, float v2) {
        GL41.glProgramUniform3f(programId, location, v0, v1, v2);
    }

    @Override
    public void glProgramUniform4f(int programId, int location, float v0, float v1, float v2, float v3) {
        GL41.glProgramUniform4f(programId, location, v0, v1, v2, v3);
    }

    @Override
    public void glProgramUniform1d(int programId, int location, double value) {
        GL41.glProgramUniform1d(programId, location, value);
    }

    @Override
    public void glProgramUniform2d(int programId, int location, double v0, double v1) {
        GL41.glProgramUniform2d(programId, location, v0, v1);
    }

    @Override
    public void glProgramUniform3d(int programId, int location, double v0, double v1, double v2) {
        GL41.glProgramUniform3d(programId, location, v0, v1, v2);
    }

    @Override
    public void glProgramUniform4d(int programId, int location, double v0, double v1, double v2, double v3) {
        GL41.glProgramUniform4d(programId, location, v0, v1, v2, v3);
    }

    @Override
    public void glProgramUniformMatrix2f(int programId, int location, boolean needsTranspose, FloatBuffer data) {
        GL41.glProgramUniformMatrix2fv(programId, location, needsTranspose, data);
    }

    @Override
    public void glProgramUniformMatrix3f(int programId, int location, boolean needsTranspose, FloatBuffer data) {
        GL41.glProgramUniformMatrix3fv(programId, location, needsTranspose, data);
    }

    @Override
    public void glProgramUniformMatrix4f(int programId, int location, boolean needsTranspose, FloatBuffer data) {
        GL41.glProgramUniformMatrix4fv(programId, location, needsTranspose, data);
    }

    @Override
    public void glProgramUniformMatrix2d(int programId, int location, boolean needsTranspose, DoubleBuffer data) {
        GL41.glProgramUniformMatrix2dv(programId, location, needsTranspose, data);
    }

    @Override
    public void glProgramUniformMatrix3d(int programId, int location, boolean needsTranspose, DoubleBuffer data) {
        GL41.glProgramUniformMatrix3dv(programId, location, needsTranspose, data);
    }

    @Override
    public void glProgramUniformMatrix4d(int programId, int location, boolean needsTranspose, DoubleBuffer data) {
        GL41.glProgramUniformMatrix4dv(programId, location, needsTranspose, data);
    }

    @Override
    public void glTextureParameteri(int textureId, int pName, int val) {
        GL45.glTextureParameterIi(textureId, pName, val);
    }

    @Override
    public void glTextureParameterf(int textureId, int pName, float val) {
        GL45.glTextureParameterf(textureId, pName, val);
    }

    @Override
    public void glTextureSubImage1d(int textureId, int level, int xOffset, int width, int format, int type, ByteBuffer pixels) {
        GL45.glTextureSubImage1D(textureId, level, xOffset, width, format, type, pixels);
    }

    @Override
    public void glTextureSubImage2d(int textureId, int level, int xOffset, int yOffset, int width, int height, int format, int type, ByteBuffer pixels) {
        GL45.glTextureSubImage2D(textureId, level, xOffset, yOffset, width, height, format, type, pixels);
    }

    @Override
    public void glTextureSubImage3d(int textureId, int level, int xOffset, int yOffset, int zOffset, int width, int height, int depth, int format, int type, ByteBuffer pixels) {
        GL45.glTextureSubImage3D(textureId, level, xOffset, yOffset, zOffset, width, height, depth, format, type, pixels);
    }

    @Override
    public void glBindTextureUnit(int unit, int textureId) {
        GL45.glBindTextureUnit(unit, textureId);
    }

    public static DSADriver getInstance() {
        return Holder.INSTANCE;
    }

    @Override
    public void glProgramUniform1i(int programId, int location, int value) {
        GL41.glProgramUniform1i(programId, location, value);
    }

    @Override
    public void glProgramUniform2i(int programId, int location, int v0, int v1) {
        GL41.glProgramUniform2i(programId, location, v0, v1);
    }

    @Override
    public void glProgramUniform3i(int programId, int location, int v0, int v1, int v2) {
        GL41.glProgramUniform3i(programId, location, v0, v1, v2);
    }

    @Override
    public void glProgramUniform4i(int programId, int location, int v0, int v1, int v2, int v3) {
        GL41.glProgramUniform4i(programId, location, v0, v1, v2, v3);
    }

    @Override
    public void glTextureStorage1d(int textureId, int levels, int internalFormat, int width) {
        GL45.glTextureStorage1D(textureId, levels, internalFormat, width);
    }

    @Override
    public void glTextureStorage2d(int textureId, int levels, int internalFormat, int width, int height) {
        GL45.glTextureStorage2D(textureId, levels, internalFormat, width, height);
    }

    @Override
    public void glTextureStorage3d(int textureId, int levels, int internalFormat, int width, int height, int depth) {
        GL45.glTextureStorage3D(textureId, levels, internalFormat, width, height, depth);
    }

    @Override
    public void glGenerateTextureMipmap(int textureId) {
        GL45.glGenerateTextureMipmap(textureId);
    }

    @Override
    public void glNamedFramebufferTexture(int framebuffer, int attachment, int texture, int level) {
        GL45.glNamedFramebufferTexture(framebuffer, attachment, texture, level);
    }

    private static class Holder {

        private static final DSADriver INSTANCE = new GL45DSA();
    }
}
