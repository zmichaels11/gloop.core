/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.longlinkislong.gloop.dsa;

import java.nio.ByteBuffer;
import java.nio.DoubleBuffer;
import java.nio.FloatBuffer;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL12;
import org.lwjgl.opengl.GL13;
import org.lwjgl.opengl.GL15;
import org.lwjgl.opengl.GL20;
import org.lwjgl.opengl.GL21;
import org.lwjgl.opengl.GL30;
import org.lwjgl.opengl.GL31;
import org.lwjgl.opengl.GL40;
import org.lwjgl.opengl.GL44;

/**
 *
 * @author zmichaels
 */
public class NoDSA implements EXTDSADriver {

    @Override
    public void glNamedFramebufferTexture1D(int framebuffer, int attachment, int texTarget, int texture, int level) {
        GL30.glBindFramebuffer(GL30.GL_FRAMEBUFFER, framebuffer);
        GL30.glFramebufferTexture1D(GL30.GL_FRAMEBUFFER, attachment, texTarget, texture, level);
    }

    @Override
    public void glNamedFramebufferTexture2D(int framebuffer, int attachment, int texTarget, int texture, int level) {
        GL30.glBindFramebuffer(GL30.GL_FRAMEBUFFER, framebuffer);
        GL30.glFramebufferTexture2D(GL30.GL_FRAMEBUFFER, attachment, texTarget, texture, level);
    }

    @Override
    public void glTextureParameteri(int textureId, int target, int pName, int val) {
        GL11.glBindTexture(target, textureId);
        GL11.glTexParameteri(target, pName, val);
    }

    @Override
    public void glTextureParameterf(int textureId, int target, int pName, float val) {
        GL11.glBindTexture(target, textureId);
        GL11.glTexParameterf(target, pName, val);
    }

    @Override
    public void glGenerateTextureMipmap(int textureId, int target) {
        GL11.glBindTexture(target, textureId);
        GL30.glGenerateMipmap(target);
    }

    @Override
    public void glBindTextureUnit(int unit, int target, int textureId) {
        GL13.glActiveTexture(unit);
        GL11.glBindTexture(target, textureId);
    }

    @Override
    public void glTextureImage1d(int textureId, int target, int level, int internalFormat, int width, int border, int format, int type, long size) {
        GL11.glBindTexture(target, textureId);
        GL11.glTexImage1D(target, level, internalFormat, width, border, format, type, size);
    }

    @Override
    public void glTextureImage1d(int texture, int target, int level, int internalFormat, int width, int border, int format, int type, ByteBuffer pixels) {
        GL11.glBindTexture(target, texture);
        GL11.glTexImage1D(target, level, internalFormat, width, border, format, type, pixels);
    }

    @Override
    public void glTextureImage2d(int texture, int target, int level, int internalFormat, int width, int height, int border, int format, int type, ByteBuffer pixels) {
        GL11.glBindTexture(target, texture);
        GL11.glTexImage2D(target, level, internalFormat, width, height, border, format, type, pixels);
    }

    @Override
    public void glTextureImage2d(int texture, int target, int level, int internalFormat, int width, int height, int border, int format, int type, long ptr) {
        GL11.glBindTexture(target, texture);
        GL11.glTexImage2D(target, level, internalFormat, width, height, border, format, type, ptr);
    }

    @Override
    public void glTextureImage3d(int texture, int target, int level, int internalFormat, int width, int height, int depth, int border, int format, int type, ByteBuffer pixels) {
        GL11.glBindTexture(target, texture);
        GL12.glTexImage3D(target, level, internalFormat, width, height, depth, border, format, type, pixels);
    }

    @Override
    public void glTextureImage3d(int texture, int target, int level, int internalFormat, int width, int height, int depth, int border, int format, int type, long ptr) {
        GL11.glBindTexture(target, texture);
        GL12.glTexImage3D(target, level, internalFormat, width, height, depth, border, format, type, ptr);
    }

    @Override
    public boolean isSupported() {
        return true;
    }

    @Override
    public int glCreateBuffers() {
        final int out = GL15.glGenBuffers();
        GL15.glBindBuffer(GL15.GL_ARRAY_BUFFER, out);
        return out;
    }

    @Override
    public int glCreateTextures(int target) {
        final int out = GL11.glGenTextures();
        GL11.glBindTexture(target, out);
        return out;
    }

    @Override
    public int glCreateFramebuffers() {
        final int out = GL30.glGenFramebuffers();
        GL30.glBindFramebuffer(GL30.GL_FRAMEBUFFER, out);
        return out;
    }

    @Override
    public void glNamedFramebufferTexture(int framebuffer, int attachment, int texture, int level) {
        throw new UnsupportedOperationException();
    }

    @Override
    public void glNamedBufferData(int bufferId, long size, int usage) {
        GL15.glBindBuffer(GL15.GL_ARRAY_BUFFER, bufferId);
        GL15.glBufferData(GL15.GL_ARRAY_BUFFER, size, usage);
    }

    @Override
    public void glNamedBufferData(int bufferId, ByteBuffer data, int usage) {
        GL15.glBindBuffer(GL15.GL_ARRAY_BUFFER, bufferId);
        GL15.glBufferData(GL15.GL_ARRAY_BUFFER, data, usage);
    }

    @Override
    public void glNamedBufferSubData(int buffer, long offset, ByteBuffer data) {
        GL15.glBindBuffer(GL15.GL_ARRAY_BUFFER, buffer);
        GL15.glBufferSubData(GL15.GL_ARRAY_BUFFER, offset, data);
    }

    @Override
    public void glNamedBufferStorage(int bufferId, ByteBuffer data, int flags) {
        GL15.glBindBuffer(GL15.GL_ARRAY_BUFFER, bufferId);
        GL44.glBufferStorage(GL15.GL_ARRAY_BUFFER, data, flags);
    }

    @Override
    public void glNamedBufferStorage(int bufferId, long size, int flags) {
        GL15.glBindBuffer(GL15.GL_ARRAY_BUFFER, bufferId);
        GL44.glBufferStorage(GL15.GL_ARRAY_BUFFER, size, flags);
    }

    @Override
    public void glGetNamedBufferSubData(int bufferId, long offset, ByteBuffer out) {
        GL15.glBindBuffer(GL15.GL_ARRAY_BUFFER, bufferId);
        GL15.glBufferSubData(GL15.GL_ARRAY_BUFFER, offset, out);
    }

    @Override
    public ByteBuffer glMapNamedBufferRange(int bufferId, long offset, long length, int access, ByteBuffer recycled) {
        GL15.glBindBuffer(GL15.GL_ARRAY_BUFFER, bufferId);
        return GL30.glMapBufferRange(GL15.GL_ARRAY_BUFFER, offset, length, access, recycled);
    }

    @Override
    public void glUnmapNamedBuffer(int bufferId) {
        GL15.glUnmapBuffer(bufferId);
    }

    @Override
    public void glCopyNamedBufferSubData(int readBufferId, int writeBufferId, long readOffset, long writeOffset, long size) {
        GL15.glBindBuffer(GL31.GL_COPY_READ_BUFFER, readBufferId);
        GL15.glBindBuffer(GL31.GL_COPY_WRITE_BUFFER, writeBufferId);
        GL31.glCopyBufferSubData(readBufferId, writeBufferId, readOffset, writeOffset, size);
    }

    @Override
    public void glProgramUniform1f(int programId, int location, float value) {
        GL20.glUseProgram(programId);
        GL20.glUniform1f(location, value);
    }

    @Override
    public void glProgramUniform2f(int programId, int location, float v0, float v1) {
        GL20.glUseProgram(programId);
        GL20.glUniform2f(location, v0, v1);
    }

    @Override
    public void glProgramUniform3f(int programId, int location, float v0, float v1, float v2) {
        GL20.glUseProgram(programId);
        GL20.glUniform3f(location, v0, v1, v2);
    }

    @Override
    public void glProgramUniform4f(int programId, int location, float v0, float v1, float v2, float v3) {
        GL20.glUseProgram(programId);
        GL20.glUniform4f(location, v0, v1, v2, v3);
    }

    @Override
    public void glProgramUniform1i(int programId, int location, int value) {
        GL20.glUseProgram(programId);
        GL20.glUniform1i(location, value);
    }

    @Override
    public void glProgramUniform2i(int programId, int location, int v0, int v1) {
        GL20.glUseProgram(programId);
        GL20.glUniform2i(location, v0, v1);
    }

    @Override
    public void glProgramUniform3i(int programId, int location, int v0, int v1, int v2) {
        GL20.glUseProgram(programId);
        GL20.glUniform3i(location, v0, v1, v2);
    }

    @Override
    public void glProgramUniform4i(int programId, int location, int v0, int v1, int v2, int v3) {
        GL20.glUseProgram(programId);
        GL20.glUniform4i(location, v0, v1, v2, v3);
    }

    @Override
    public void glProgramUniform1d(int programId, int location, double value) {
        GL20.glUseProgram(programId);
        GL40.glUniform1d(location, value);
    }

    @Override
    public void glProgramUniform2d(int programId, int location, double v0, double v1) {
        GL20.glUseProgram(programId);
        GL40.glUniform2d(location, v0, v1);
    }

    @Override
    public void glProgramUniform3d(int programId, int location, double v0, double v1, double v2) {
        GL20.glUseProgram(programId);
        GL40.glUniform3d(location, v0, v1, v2);
    }

    @Override
    public void glProgramUniform4d(int programId, int location, double v0, double v1, double v2, double v3) {
        GL20.glUseProgram(programId);
        GL40.glUniform4d(location, v0, v1, v2, v3);
    }

    @Override
    public void glProgramUniformMatrix2f(int programId, int location, boolean needsTranspose, FloatBuffer data) {
        GL20.glUseProgram(programId);
        GL20.glUniformMatrix2fv(location, needsTranspose, data);
    }

    @Override
    public void glProgramUniformMatrix3f(int programId, int location, boolean needsTranspose, FloatBuffer data) {
        GL20.glUseProgram(programId);
        GL20.glUniformMatrix3fv(location, needsTranspose, data);
    }

    @Override
    public void glProgramUniformMatrix4f(int programId, int location, boolean needsTranspose, FloatBuffer data) {
        GL20.glUseProgram(programId);
        GL20.glUniformMatrix4fv(location, needsTranspose, data);
    }

    @Override
    public void glProgramUniformMatrix2d(int programId, int location, boolean needsTranspose, DoubleBuffer data) {
        GL20.glUseProgram(programId);
        GL40.glUniformMatrix2dv(location, needsTranspose, data);
    }

    @Override
    public void glProgramUniformMatrix3d(int programId, int location, boolean needsTranspose, DoubleBuffer data) {
        GL20.glUseProgram(programId);
        GL40.glUniformMatrix3dv(location, needsTranspose, data);
    }

    @Override
    public void glProgramUniformMatrix4d(int programId, int location, boolean needsTranspose, DoubleBuffer data) {
        GL20.glUseProgram(programId);
        GL40.glUniformMatrix4dv(location, needsTranspose, data);
    }

    @Override
    public void glTextureSubImage1d(int textureId, int level, int xOffset, int width, int format, int type, ByteBuffer pixels) {
        GL11.glBindTexture(GL11.GL_TEXTURE_1D, textureId);
        GL11.glTexSubImage1D(GL11.GL_TEXTURE_1D, level, xOffset, width, format, type, pixels);
    }

    @Override
    public void glTextureSubImage2d(int textureId, int level, int xOffset, int yOffset, int width, int height, int format, int type, ByteBuffer pixels) {
        GL11.glBindTexture(GL11.GL_TEXTURE_2D, textureId);
        GL11.glTexSubImage2D(GL11.GL_TEXTURE_2D, level, xOffset, yOffset, width, height, format, type, pixels);
    }

    @Override
    public void glTextureSubImage3d(int textureId, int level, int xOffset, int yOffset, int zOffset, int width, int height, int depth, int format, int type, ByteBuffer pixels) {
        GL11.glBindTexture(GL12.GL_TEXTURE_3D, textureId);
        GL12.glTexSubImage3D(GL12.GL_TEXTURE_3D, level, xOffset, yOffset, zOffset, width, height, depth, format, type, pixels);
    }

    @Override
    public int glGetNamedBufferParameteri(int bufferId, int pName) {
        GL15.glBindBuffer(GL15.GL_ARRAY_BUFFER, pName);
        return GL15.glGetBufferParameteri(GL15.GL_ARRAY_BUFFER, pName);
    }

    public static DSADriver getInstance() {
        return Holder.INSTANCE;
    }

    private NoDSA() {
    }

    @Override
    public void glNamedBufferReadPixels(int bufferId, int x, int y, int width, int height, int format, int type, long ptr) {
        GL15.glBindBuffer(GL21.GL_PIXEL_PACK_BUFFER, bufferId);
        GL11.glReadPixels(x, y, width, height, format, type, ptr);
    }    

    private static class Holder {

        private static final DSADriver INSTANCE = new NoDSA();
    }
}
