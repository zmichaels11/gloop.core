/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.longlinkislong.gloop.dsa;

import java.nio.ByteBuffer;
import java.nio.FloatBuffer;
import org.lwjgl.opengl.EXTDirectStateAccess;
import org.lwjgl.opengl.GL;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL12;

/**
 *
 * @author zmichaels
 */
public class EXTDSA extends FakeDSA {

    @Override
    public String toString() {
        return "EXTDSA";
    }

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
    public void glGetNamedBufferSubData(int buffer, long offset, ByteBuffer out) {
        EXTDirectStateAccess.glGetNamedBufferSubDataEXT(buffer, offset, out);
    }

    @Override
    public int glGetNamedBufferParameteri(int bufferId, int pName) {
        return EXTDirectStateAccess.glGetNamedBufferParameteriEXT(bufferId, pName);
    }

    @Override
    public ByteBuffer glMapNamedBufferRange(int bufferId, long offset, long length, int access, ByteBuffer recycled) {
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
    public void glProgramUniform1i(int programId, int location, int value) {
        EXTDirectStateAccess.glProgramUniform1iEXT(programId, location, value);
    }

    @Override
    public void glProgramUniform2i(int programId, int location, int v0, int v1) {
        EXTDirectStateAccess.glProgramUniform2iEXT(programId, location, v0, v1);
    }

    @Override
    public void glProgramUniform3i(int programId, int location, int v0, int v1, int v2) {
        EXTDirectStateAccess.glProgramUniform3iEXT(programId, location, v0, v1, v2);
    }

    @Override
    public void glProgramUniform4i(int programId, int location, int v0, int v1, int v2, int v3) {
        EXTDirectStateAccess.glProgramUniform4iEXT(programId, location, v0, v1, v2, v3);
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
    public void glTextureParameteri(int textureId, int target, int pName, int val) {
        EXTDirectStateAccess.glTextureParameteriEXT(textureId, target, pName, val);
    }

    @Override
    public void glTextureParameterf(int textureId, int target, int pName, float val) {
        EXTDirectStateAccess.glTextureParameterfEXT(textureId, target, pName, val);
    }

    @Override
    public void glTextureSubImage1d(int textureId, int level, int xOffset, int width, int format, int type, ByteBuffer pixels) {
        EXTDirectStateAccess.glTextureSubImage1DEXT(
                textureId, GL11.GL_TEXTURE_1D,
                level, xOffset, width,
                format, type, pixels);
    }

    @Override
    public void glTextureSubImage2d(int textureId, int level, int xOffset, int yOffset, int width, int height, int format, int type, ByteBuffer pixels) {
        EXTDirectStateAccess.glTextureSubImage2DEXT(
                textureId, GL11.GL_TEXTURE_2D,
                level, xOffset, yOffset, 
                width, height, format, type, pixels);
    }

    @Override
    public void glTextureSubImage3d(int textureId, int level, int xOffset, int yOffset, int zOffset, int width, int height, int depth, int format, int type, ByteBuffer pixels) {
        EXTDirectStateAccess.glTextureSubImage3DEXT(
                textureId, GL12.GL_TEXTURE_3D,
                level, xOffset, yOffset, zOffset,
                width, height, depth, format, type, pixels);
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
