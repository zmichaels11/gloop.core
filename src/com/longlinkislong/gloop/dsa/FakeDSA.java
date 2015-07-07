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
import org.lwjgl.opengl.GL13;
import org.lwjgl.opengl.GL15;
import org.lwjgl.opengl.GL20;
import org.lwjgl.opengl.GL30;
import org.lwjgl.opengl.GL31;
import org.lwjgl.opengl.GL40;
import org.lwjgl.opengl.GL44;

/**
 *
 * @author zmichaels
 */
public class FakeDSA implements DirectStateAccess {

    @Override
    public boolean isSupported() {
        return true;
    }
    
    @Override
    public void glNamedBufferData(int bufferId, long size, int usage) {
        GL15.glBindBuffer(GL15.GL_ARRAY_BUFFER, bufferId);
        GL15.glBufferData(GL15.GL_ARRAY_BUFFER, size, usage);
        GL15.glBindBuffer(GL15.GL_ARRAY_BUFFER, 0);
    }

    @Override
    public void glNamedBufferData(int bufferId, ByteBuffer data, int usage) {
        GL15.glBindBuffer(GL15.GL_ARRAY_BUFFER, bufferId);
        GL15.glBufferData(GL15.GL_ARRAY_BUFFER, data, usage);
        GL15.glBindBuffer(GL15.GL_ARRAY_BUFFER, 0);
    }

    @Override
    public void glNamedBufferSubData(int buffer, long offset, ByteBuffer data) {
        GL15.glBindBuffer(GL15.GL_ARRAY_BUFFER, buffer);
        GL15.glBufferSubData(GL15.GL_ARRAY_BUFFER, offset, data);
        GL15.glBindBuffer(GL15.GL_ARRAY_BUFFER, 0);
    }

    @Override
    public void glNamedBufferStorage(int bufferId, ByteBuffer data, int flags) {
        GL15.glBindBuffer(GL15.GL_ARRAY_BUFFER, bufferId);
        GL44.glBufferStorage(GL15.GL_ARRAY_BUFFER, data, flags);
        GL15.glBindBuffer(GL15.GL_ARRAY_BUFFER, 0);
    }

    @Override
    public void glNamedBufferStorage(int bufferId, long size, int flags) {
        GL15.glBindBuffer(GL15.GL_ARRAY_BUFFER, bufferId);
        GL44.glBufferStorage(GL15.GL_ARRAY_BUFFER, size, flags);
        GL15.glBindBuffer(GL15.GL_ARRAY_BUFFER, 0);
    }

    @Override
    public ByteBuffer glMapNammedBufferRange(int bufferId, long offset, long length, int access, ByteBuffer recycled) {
        GL15.glBindBuffer(GL15.GL_ARRAY_BUFFER, bufferId);
        final ByteBuffer out = GL30.glMapBufferRange(GL15.GL_ARRAY_BUFFER, offset, length, access, recycled);
        GL15.glBindBuffer(GL15.GL_ARRAY_BUFFER, 0);

        return out;
    }

    @Override
    public void glUnmapNamedBuffer(int bufferId) {
        GL15.glBindBuffer(GL15.GL_ARRAY_BUFFER, bufferId);
        GL15.glUnmapBuffer(GL15.GL_ARRAY_BUFFER);
        GL15.glBindBuffer(GL15.GL_ARRAY_BUFFER, 0);
    }

    @Override
    public void glCopyNamedBufferSubData(int readBufferId, int writeBufferId, long readOffset, long writeOffset, long size) {
        GL15.glBindBuffer(GL31.GL_COPY_READ_BUFFER, readBufferId);
        GL15.glBindBuffer(GL31.GL_COPY_WRITE_BUFFER, writeBufferId);

        GL31.glCopyBufferSubData(GL31.GL_COPY_READ_BUFFER, GL31.GL_COPY_WRITE_BUFFER, readOffset, writeOffset, size);

        GL15.glBindBuffer(GL31.GL_COPY_READ_BUFFER, 0);
        GL15.glBindBuffer(GL31.GL_COPY_WRITE_BUFFER, 0);
    }

    @Override
    public void glProgramUniform1f(int programId, int location, float value) {
        GL20.glUseProgram(programId);
        GL20.glUniform1f(location, value);
        GL20.glUseProgram(programId);
    }

    @Override
    public void glProgramUniform2f(int programId, int location, float v0, float v1) {
        GL20.glUseProgram(programId);
        GL20.glUniform2f(location, v0, v1);
        GL20.glUseProgram(0);
    }

    @Override
    public void glProgramUniform3f(int programId, int location, float v0, float v1, float v2) {
        GL20.glUseProgram(programId);
        GL20.glUniform3f(location, v0, v1, v2);
        GL20.glUseProgram(0);
    }

    @Override
    public void glProgramUniform4f(int programId, int location, float v0, float v1, float v2, float v3) {
        GL20.glUseProgram(programId);
        GL20.glUniform4f(location, v0, v1, v2, v3);
        GL20.glUseProgram(0);
    }

    @Override
    public void glProgramUniform1d(int programId, int location, double value) {
        GL20.glUseProgram(programId);
        GL40.glUniform1d(location, value);
        GL20.glUseProgram(0);
    }

    @Override
    public void glProgramUniform2d(int programId, int location, double v0, double v1) {
        GL20.glUseProgram(programId);
        GL40.glUniform2d(location, v0, v1);
        GL20.glUseProgram(programId);
    }

    @Override
    public void glProgramUniform3d(int programId, int location, double v0, double v1, double v2) {
        GL20.glUseProgram(programId);
        GL40.glUniform3d(location, v0, v1, v2);
        GL20.glUseProgram(0);
    }

    @Override
    public void glProgramUniform4d(int programId, int location, double v0, double v1, double v2, double v3) {
        GL20.glUseProgram(programId);
        GL40.glUniform4d(location, v0, v1, v2, v3);
        GL20.glUseProgram(0);
    }

    @Override
    public void glProgramUniformMatrix2f(int programId, int location, boolean needsTranspose, FloatBuffer data) {
        GL20.glUseProgram(programId);
        GL20.glUniformMatrix2fv(location, needsTranspose, data);
        GL20.glUseProgram(0);
    }

    @Override
    public void glProgramUniformMatrix3f(int programId, int location, boolean needsTranspose, FloatBuffer data) {
        GL20.glUseProgram(programId);
        GL20.glUniformMatrix3fv(location, needsTranspose, data);
        GL20.glUseProgram(0);
    }

    @Override
    public void glProgramUniformMatrix4f(int programId, int location, boolean needsTranspose, FloatBuffer data) {
        GL20.glUseProgram(programId);
        GL20.glUniformMatrix4fv(location, needsTranspose, data);
        GL20.glUseProgram(0);
    }

    @Override
    public void glProgramUniformMatrix2d(int programId, int location, boolean needsTranspose, DoubleBuffer data) {
        GL20.glUseProgram(programId);
        GL40.glUniformMatrix2dv(location, needsTranspose, data);
        GL20.glUseProgram(0);
    }

    @Override
    public void glProgramUniformMatrix3d(int programId, int location, boolean needsTranspose, DoubleBuffer data) {
        GL20.glUseProgram(programId);
        GL40.glUniformMatrix3dv(location, needsTranspose, data);
        GL20.glUseProgram(0);
    }

    @Override
    public void glProgramUniformMatrix4d(int programId, int location, boolean needsTranspose, DoubleBuffer data) {
        GL20.glUseProgram(programId);
        GL40.glUniformMatrix4dv(location, needsTranspose, data);
        GL20.glUseProgram(0);
    }

    @Override
    public void glTextureParameteri(int textureId, int target, int pName, int val) {
        GL11.glBindTexture(target, textureId);
        GL11.glTexParameteri(target, pName, val);
        GL11.glBindTexture(target, 0);
    }

    @Override
    public void glTextureParameterf(int textureId, int target, int pName, float val) {
        GL11.glBindTexture(target, textureId);
        GL11.glTexParameterf(target, pName, val);
        GL11.glBindTexture(target, 0);
    }

    @Override
    public void glBindTextureUnit(int unit, int target, int textureId) {
        GL13.glActiveTexture(unit);
        GL11.glBindTexture(target, textureId);
    }
    
    public static DirectStateAccess getInstance() {
        return Holder.INSTANCE;
    }
    
    private static class Holder {
        private static final DirectStateAccess INSTANCE = new FakeDSA();
    }
}
