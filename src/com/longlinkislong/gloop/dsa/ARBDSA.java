/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.longlinkislong.gloop.dsa;

import java.nio.ByteBuffer;
import java.nio.DoubleBuffer;
import java.nio.FloatBuffer;
import org.lwjgl.opengl.ARBDirectStateAccess;
import org.lwjgl.opengl.ContextCapabilities;
import org.lwjgl.opengl.EXTDirectStateAccess;
import org.lwjgl.opengl.GL;
import org.lwjgl.opengl.GL41;

/**
 *
 * @author zmichaels
 */
public class ARBDSA implements DirectStateAccess {

    public static DirectStateAccess getInstance() {
        return Holder.INSTANCE;
    }
    
    private static class Holder {
        private static final DirectStateAccess INSTANCE = new ARBDSA();
    }
    
    @Override
    public String toString() {
        return "ARBDSA";
    }
    
    @Override
    public boolean isSupported() {
        final ContextCapabilities cap = GL.getCurrent().getCapabilities();
                
        return cap.GL_ARB_direct_state_access && cap.OpenGL41 && cap.GL_EXT_direct_state_access;
    }

    @Override
    public int glCreateBuffers() {
        return ARBDirectStateAccess.glCreateBuffers();
    }

    @Override
    public int glCreateTextures(int target) {
        return ARBDirectStateAccess.glCreateTextures(target);
    }

    @Override
    public void glNamedBufferData(int bufferId, long size, int usage) {
        ARBDirectStateAccess.glNamedBufferData(bufferId, size, usage);
    }

    @Override
    public void glNamedBufferData(int bufferId, ByteBuffer data, int usage) {
        ARBDirectStateAccess.glNamedBufferData(bufferId, data, usage);
    }

    @Override
    public void glNamedBufferSubData(int buffer, long offset, ByteBuffer data) {
        ARBDirectStateAccess.glNamedBufferSubData(buffer, offset, data);
    }

    @Override
    public void glNamedBufferStorage(int bufferId, ByteBuffer data, int flags) {
        ARBDirectStateAccess.glNamedBufferStorage(bufferId, data, flags);
    }

    @Override
    public void glNamedBufferStorage(int bufferId, long size, int flags) {
        ARBDirectStateAccess.glNamedBufferStorage(bufferId, size, flags);
    }

    @Override
    public void glGetNamedBufferSubData(int bufferId, long offset, ByteBuffer out) {
        ARBDirectStateAccess.glGetNamedBufferSubData(bufferId, offset, out);
    }

    @Override
    public ByteBuffer glMapNamedBufferRange(int bufferId, long offset, long length, int access, ByteBuffer recycled) {
        return ARBDirectStateAccess.glMapNamedBufferRange(bufferId, offset, length, access, recycled);
    }

    @Override
    public void glUnmapNamedBuffer(int bufferId) {
        ARBDirectStateAccess.glUnmapNamedBuffer(bufferId);
    }

    @Override
    public void glCopyNamedBufferSubData(int readBufferId, int writeBufferId, long readOffset, long writeOffset, long size) {
        ARBDirectStateAccess.glCopyNamedBufferSubData(readBufferId, writeBufferId, readOffset, writeOffset, size);
    }

    @Override
    public void glProgramUniform1f(int programId, int location, float value) {        
        GL41.glProgramUniform1f(location, location, value);
    }

    @Override
    public void glProgramUniform2f(int programId, int location, float v0, float v1) {
        GL41.glProgramUniform2f(location, location, v0, v1);
    }

    @Override
    public void glProgramUniform3f(int programId, int location, float v0, float v1, float v2) {
        GL41.glProgramUniform3f(location, location, v0, v1, v2);
    }

    @Override
    public void glProgramUniform4f(int programId, int location, float v0, float v1, float v2, float v3) {
        GL41.glProgramUniform4f(programId, location, v0, v1, v2, v3);
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
    public void glTextureParameteri(int textureId, int target, int pName, int val) {   
        EXTDirectStateAccess.glTextureParameteriEXT(textureId, target, pName, val);
        //ARBDirectStateAccess.glTextureParameteri(textureId, pName, val);
    }

    @Override
    public void glTextureParameterf(int textureId, int target, int pName, float val) {
        EXTDirectStateAccess.glTextureParameterfEXT(textureId, target, pName, val);        
        //ARBDirectStateAccess.glTextureParameterf(textureId, pName, val);
    }

    @Override
    public void glTextureSubImage1d(int textureId, int level, int xOffset, int width, int format, int type, ByteBuffer pixels) {
        ARBDirectStateAccess.glTextureSubImage1D(textureId, level, xOffset, width, format, type, pixels);
    }

    @Override
    public void glTextureSubImage2d(int textureId, int level, int xOffset, int yOffset, int width, int height, int format, int type, ByteBuffer pixels) {
        ARBDirectStateAccess.glTextureSubImage2D(textureId, level, xOffset, yOffset, width, height, format, type, pixels);
    }

    @Override
    public void glTextureSubImage3d(int textureId, int level, int xOffset, int yOffset, int zOffset, int width, int height, int depth, int format, int type, ByteBuffer pixels) {
        ARBDirectStateAccess.glTextureSubImage3D(textureId, level, xOffset, yOffset, zOffset, width, height, depth, format, type, pixels);
    }

    @Override
    public void glBindTextureUnit(int unit, int target, int textureId) {
        ARBDirectStateAccess.glBindTextureUnit(unit, textureId);
    }

    @Override
    public int glGetNamedBufferParameteri(int bufferId, int pName) {
        return ARBDirectStateAccess.glGetNamedBufferParameteri(bufferId, pName);
    }
    
}
