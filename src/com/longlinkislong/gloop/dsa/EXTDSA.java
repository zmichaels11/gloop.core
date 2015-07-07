/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.longlinkislong.gloop.dsa;

import java.nio.ByteBuffer;
import java.nio.DoubleBuffer;
import java.nio.FloatBuffer;
import org.lwjgl.opengl.EXTDirectStateAccess;
import org.lwjgl.opengl.GL;
import org.lwjgl.opengl.GL15;
import org.lwjgl.opengl.GL20;
import org.lwjgl.opengl.GL40;
import org.lwjgl.opengl.GL44;

/**
 *
 * @author zmichaels
 */
public class EXTDSA implements DirectStateAccess {

    @Override
    public boolean isSupported() {
        return GL.getCurrent().getCapabilities().GL_EXT_direct_state_access;
    }
    
    @Override
    public void glNamedBufferData(int bufferId, long size, int usage) {
        EXTDirectStateAccess.glNamedBufferDataEXT(bufferId, size, usage);
    }

    @Override
    public void glNamedBufferData(int bufferId, ByteBuffer data, int usage) {
        EXTDirectStateAccess.glNamedBufferDataEXT(bufferId, data, usage);
    }

    @Override
    public void glNamedBufferSubData(int buffer, long offset, ByteBuffer data) {
        EXTDirectStateAccess.glNamedBufferSubDataEXT(buffer, offset, data);
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
        return EXTDirectStateAccess.glMapNamedBufferRangeEXT(bufferId, offset, length, access, recycled);
    }

    @Override
    public void glUnmapNamedBuffer(int bufferId) {
        EXTDirectStateAccess.glUnmapNamedBufferEXT(bufferId);
    }

    @Override
    public void glCopyNamedBufferSubData(int readBufferId, int writeBufferId, long readOffset, long writeOffset, long size) {
        EXTDirectStateAccess.glNamedCopyBufferSubDataEXT(readBufferId, writeBufferId, readOffset, writeOffset, size);
    }

    @Override
    public void glProgramUniform1f(int programId, int location, float value) {
        EXTDirectStateAccess.glProgramUniform1fEXT(programId, location, value);
    }

    @Override
    public void glProgramUniform2f(int programId, int location, float v0, float v1) {
        EXTDirectStateAccess.glProgramUniform2fEXT(programId, location, v0, v1);
    }

    @Override
    public void glProgramUniform3f(int programId, int location, float v0, float v1, float v2) {
        EXTDirectStateAccess.glProgramUniform3fEXT(programId, location, v0, v1, v2);
    }

    @Override
    public void glProgramUniform4f(int programId, int location, float v0, float v1, float v2, float v3) {
        EXTDirectStateAccess.glProgramUniform4fEXT(programId, location, v0, v1, v2, v3);
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
        GL20.glUseProgram(0);
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
        EXTDirectStateAccess.glProgramUniformMatrix2fvEXT(programId, location, needsTranspose, data);
    }

    @Override
    public void glProgramUniformMatrix3f(int programId, int location, boolean needsTranspose, FloatBuffer data) {
        EXTDirectStateAccess.glProgramUniformMatrix3fvEXT(programId, location, needsTranspose, data);
    }

    @Override
    public void glProgramUniformMatrix4f(int programId, int location, boolean needsTranspose, FloatBuffer data) {
        EXTDirectStateAccess.glProgramUniformMatrix4fvEXT(programId, location, needsTranspose, data);
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
        EXTDirectStateAccess.glTextureParameteriEXT(textureId, target, pName, val);
    }

    @Override
    public void glTextureParameterf(int textureId, int target, int pName, float val) {
        EXTDirectStateAccess.glTextureParameterfEXT(textureId, target, pName, val);
    }

    @Override
    public void glBindTextureUnit(int unit, int target, int textureId) {
        EXTDirectStateAccess.glBindMultiTextureEXT(unit, target, textureId);
    }

    public static DirectStateAccess getInstance() {
        return Holder.INSTANCE;
    }

    private static class Holder {

        private static final DirectStateAccess INSTANCE = new EXTDSA();
    }
}
