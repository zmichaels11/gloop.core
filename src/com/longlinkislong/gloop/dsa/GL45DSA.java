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
public class GL45DSA implements DirectStateAccess {

    @Override
    public boolean isSupported() {
        return GL.getCurrent().getCapabilities().OpenGL45;
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
    public ByteBuffer glMapNammedBufferRange(int bufferId, long offset, long length, int access, ByteBuffer recycled) {
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
    public void glTextureParameteri(int textureId, int ignored, int pName, int val) {
        GL45.glTextureParameterIi(textureId, pName, val);
    }

    @Override
    public void glTextureParameterf(int textureId, int ignored, int pName, float val) {
        GL45.glTextureParameterf(textureId, pName, val);
    }

    @Override
    public void glBindTextureUnit(int unit, int ignored, int textureId) {
        GL45.glBindTextureUnit(unit, textureId);
    }

    public static DirectStateAccess getInstance() {
        return Holder.INSTANCE;
    }

    private static class Holder {

        private static final DirectStateAccess INSTANCE = new GL45DSA();
    }
}
