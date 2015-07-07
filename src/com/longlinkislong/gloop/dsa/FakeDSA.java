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
import org.lwjgl.opengl.GL30;
import org.lwjgl.opengl.GL31;
import org.lwjgl.opengl.GL40;
import org.lwjgl.opengl.GL44;

/**
 *
 * @author zmichaels
 */
public class FakeDSA implements DirectStateAccess {    
    
    private int saveBuffer = 0;
    
    private void saveBuffer() {
        this.saveBuffer = GL11.glGetInteger(GL15.GL_ARRAY_BUFFER_BINDING);
    }
    
    private void restoreBuffer() {
        GL15.glBindBuffer(GL15.GL_ARRAY_BUFFER, this.saveBuffer);
    }
    
    private int saveTex1d = 0;
    private void saveTexture1d() {
        this.saveTex1d = GL11.glGetInteger(GL11.GL_TEXTURE_BINDING_1D);
    }
    
    private void restoreTexture1d() {
        GL11.glBindTexture(GL11.GL_TEXTURE_1D, this.saveTex1d);
    }
    
    private int saveTex2d = 0;
    private void saveTexture2d() {
        this.saveTex2d = GL11.glGetInteger(GL11.GL_TEXTURE_BINDING_2D);
    }
    private void restoreTexture2d() {
        GL11.glBindTexture(GL11.GL_TEXTURE_2D, this.saveTex2d);
    }
    
    private int saveTex3d = 0;
    private void saveTexture3d() {
        this.saveTex3d = GL11.glGetInteger(GL12.GL_TEXTURE_BINDING_3D);        
    }
    private void restoreTexture3d() {
        GL11.glBindTexture(GL12.GL_TEXTURE_3D, this.saveTex3d);
    }
    
    private void saveTexture(int target) {
        switch(target) {
            case GL11.GL_TEXTURE_1D:
                this.saveTexture1d();
                break;
            case GL11.GL_TEXTURE_2D:
                this.saveTexture2d();
                break;
            case GL12.GL_TEXTURE_3D:
                this.saveTexture3d();
                break;
        }
    }
    
    private void restoreTexture(int target) {
        switch(target) {
            case GL11.GL_TEXTURE_1D:
                this.restoreTexture1d();
                break;
            case GL11.GL_TEXTURE_2D:
                this.restoreTexture2d();
                break;
            case GL12.GL_TEXTURE_3D:
                this.restoreTexture3d();
                break;
        }
    }
    
    private int saveProgram = 0;
    private void saveProgram() {
        this.saveProgram = GL11.glGetInteger(GL20.GL_CURRENT_PROGRAM);
    }
    
    private void restoreProgram() {
        GL20.glUseProgram(this.saveProgram);
    }    
    
    @Override
    public String toString() {
        return "FakeDSA";
    }        
        
    @Override
    public int glCreateBuffers() {
        final int id = GL15.glGenBuffers();        
        
        this.saveBuffer();
        GL15.glBindBuffer(GL15.GL_ARRAY_BUFFER, id);
        this.restoreBuffer();

        return id;
    }

    @Override
    public int glCreateTextures(int target) {
        final int id = GL11.glGenTextures();
        
        this.saveTexture(target);
        GL11.glBindTexture(target, id);
        this.restoreTexture(target);

        return id;
    }       

    @Override
    public boolean isSupported() {
        return true;
    }

    @Override
    public void glGetNamedBufferSubData(int bufferId, long offset, ByteBuffer data) {
        this.saveBuffer();
        GL15.glBindBuffer(GL15.GL_ARRAY_BUFFER, bufferId);
        GL15.glGetBufferSubData(GL15.GL_ARRAY_BUFFER, offset, data);
        this.restoreBuffer();
    }
    
    @Override
    public int glGetNamedBufferParameteri(int bufferId, int pName) {
        this.saveBuffer();
        GL15.glBindBuffer(GL15.GL_ARRAY_BUFFER, bufferId);
        final int val = GL15.glGetBufferParameteri(GL15.GL_ARRAY_BUFFER, pName);
        this.restoreBuffer();
        
        return val;        
    }
    
    @Override
    public void glNamedBufferData(int bufferId, long size, int usage) {
        this.saveBuffer();
        GL15.glBindBuffer(GL15.GL_ARRAY_BUFFER, bufferId);
        GL15.glBufferData(GL15.GL_ARRAY_BUFFER, size, usage);
        this.restoreBuffer();
    }

    @Override
    public void glNamedBufferData(int bufferId, ByteBuffer data, int usage) {
        this.saveBuffer();
        GL15.glBindBuffer(GL15.GL_ARRAY_BUFFER, bufferId);
        GL15.glBufferData(GL15.GL_ARRAY_BUFFER, data, usage);
        this.restoreBuffer();
    }

    @Override
    public void glNamedBufferSubData(int buffer, long offset, ByteBuffer data) {
        this.saveBuffer();
        GL15.glBindBuffer(GL15.GL_ARRAY_BUFFER, buffer);
        GL15.glBufferSubData(GL15.GL_ARRAY_BUFFER, offset, data);
        this.restoreBuffer();
    }

    @Override
    public void glNamedBufferStorage(int bufferId, ByteBuffer data, int flags) {
        this.saveBuffer();
        GL15.glBindBuffer(GL15.GL_ARRAY_BUFFER, bufferId);
        GL44.glBufferStorage(GL15.GL_ARRAY_BUFFER, data, flags);
        this.restoreBuffer();
    }

    @Override
    public void glNamedBufferStorage(int bufferId, long size, int flags) {
        this.saveBuffer();
        GL15.glBindBuffer(GL15.GL_ARRAY_BUFFER, bufferId);
        GL44.glBufferStorage(GL15.GL_ARRAY_BUFFER, size, flags);
        this.restoreBuffer();
    }

    @Override
    public ByteBuffer glMapNamedBufferRange(int bufferId, long offset, long length, int access, ByteBuffer recycled) {
        this.saveBuffer();
        GL15.glBindBuffer(GL15.GL_ARRAY_BUFFER, bufferId);
        final ByteBuffer out = GL30.glMapBufferRange(GL15.GL_ARRAY_BUFFER, offset, length, access, recycled);
        this.restoreBuffer();

        return out;
    }

    @Override
    public void glUnmapNamedBuffer(int bufferId) {
        this.saveBuffer();
        GL15.glBindBuffer(GL15.GL_ARRAY_BUFFER, bufferId);
        GL15.glUnmapBuffer(GL15.GL_ARRAY_BUFFER);
        this.restoreBuffer();
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
        this.saveProgram();
        GL20.glUseProgram(programId);
        GL20.glUniform1f(location, value);
        this.restoreProgram();
    }

    @Override
    public void glProgramUniform2f(int programId, int location, float v0, float v1) {
        this.saveProgram();
        GL20.glUseProgram(programId);
        GL20.glUniform2f(location, v0, v1);
        this.restoreProgram();
    }

    @Override
    public void glProgramUniform3f(int programId, int location, float v0, float v1, float v2) {
        this.saveProgram();
        GL20.glUseProgram(programId);
        GL20.glUniform3f(location, v0, v1, v2);
        this.restoreProgram();
    }

    @Override
    public void glProgramUniform4f(int programId, int location, float v0, float v1, float v2, float v3) {
        this.saveProgram();
        GL20.glUseProgram(programId);
        GL20.glUniform4f(location, v0, v1, v2, v3);
        this.restoreProgram();
    }

    @Override
    public void glProgramUniform1d(int programId, int location, double value) {
        this.saveProgram();
        GL20.glUseProgram(programId);
        GL40.glUniform1d(location, value);
        this.restoreProgram();
    }

    @Override
    public void glProgramUniform2d(int programId, int location, double v0, double v1) {
        this.saveProgram();
        GL20.glUseProgram(programId);
        GL40.glUniform2d(location, v0, v1);
        this.restoreProgram();
    }

    @Override
    public void glProgramUniform3d(int programId, int location, double v0, double v1, double v2) {
        this.saveProgram();
        GL20.glUseProgram(programId);
        GL40.glUniform3d(location, v0, v1, v2);
        this.restoreProgram();
    }

    @Override
    public void glProgramUniform4d(int programId, int location, double v0, double v1, double v2, double v3) {
        this.saveProgram();
        GL20.glUseProgram(programId);
        GL40.glUniform4d(location, v0, v1, v2, v3);
        this.restoreProgram();
    }

    @Override
    public void glProgramUniformMatrix2f(int programId, int location, boolean needsTranspose, FloatBuffer data) {
        this.saveProgram();
        GL20.glUseProgram(programId);
        GL20.glUniformMatrix2fv(location, needsTranspose, data);
        this.restoreProgram();
    }

    @Override
    public void glProgramUniformMatrix3f(int programId, int location, boolean needsTranspose, FloatBuffer data) {
        this.saveProgram();
        GL20.glUseProgram(programId);
        GL20.glUniformMatrix3fv(location, needsTranspose, data);
        this.restoreProgram();
    }

    @Override
    public void glProgramUniformMatrix4f(int programId, int location, boolean needsTranspose, FloatBuffer data) {
        this.saveProgram();
        GL20.glUseProgram(programId);
        GL20.glUniformMatrix4fv(location, needsTranspose, data);
        this.restoreProgram();
    }

    @Override
    public void glProgramUniformMatrix2d(int programId, int location, boolean needsTranspose, DoubleBuffer data) {
        this.saveProgram();
        GL20.glUseProgram(programId);
        GL40.glUniformMatrix2dv(location, needsTranspose, data);
        this.restoreProgram();
    }

    @Override
    public void glProgramUniformMatrix3d(int programId, int location, boolean needsTranspose, DoubleBuffer data) {
        this.saveProgram();
        GL20.glUseProgram(programId);
        GL40.glUniformMatrix3dv(location, needsTranspose, data);
        this.restoreProgram();
    }

    @Override
    public void glProgramUniformMatrix4d(int programId, int location, boolean needsTranspose, DoubleBuffer data) {
        this.saveProgram();
        GL20.glUseProgram(programId);
        GL40.glUniformMatrix4dv(location, needsTranspose, data);
        this.restoreProgram();
    }

    @Override
    public void glTextureParameteri(int textureId, int target, int pName, int val) {
        this.saveTexture(target);
        GL11.glBindTexture(target, textureId);
        GL11.glTexParameteri(target, pName, val);
        this.restoreTexture(target);
    }

    @Override
    public void glTextureParameterf(int textureId, int target, int pName, float val) {
        this.saveTexture(target);
        GL11.glBindTexture(target, textureId);
        GL11.glTexParameterf(target, pName, val);
        this.restoreTexture(target);
    }

    @Override
    public void glBindTextureUnit(int unit, int target, int textureId) {        
        GL13.glActiveTexture(unit);
        this.saveTexture(target);
        GL11.glBindTexture(target, textureId);
        this.restoreTexture(target);
    }

    public static DirectStateAccess getInstance() {
        return Holder.INSTANCE;
    }

    @Override
    public void glProgramUniform1i(int programId, int location, int value) {
        this.saveProgram();
        GL20.glUseProgram(programId);
        GL20.glUniform1i(location, value);
        this.restoreProgram();
    }

    @Override
    public void glProgramUniform2i(int programId, int location, int v0, int v1) {
        this.saveProgram();
        GL20.glUseProgram(programId);
        GL20.glUniform2i(location, v0, v1);
        this.restoreProgram();
    }

    @Override
    public void glProgramUniform3i(int programId, int location, int v0, int v1, int v2) {
        this.saveProgram();
        GL20.glUseProgram(programId);
        GL20.glUniform3i(location, v0, v1, v2);
        this.restoreProgram();
    }

    @Override
    public void glProgramUniform4i(int programId, int location, int v0, int v1, int v2, int v3) {
        this.saveProgram();
        GL20.glUseProgram(programId);
        GL20.glUniform4i(location, v0, v1, v2, v3);
        this.restoreProgram();
    }

    @Override
    public void glTextureSubImage1d(int textureId, int level, int xOffset, int width, int format, int type, ByteBuffer pixels) {
        this.saveTexture1d();
        GL11.glBindTexture(GL11.GL_TEXTURE_1D, textureId);
        GL11.glTexSubImage1D(GL11.GL_TEXTURE_1D, level, xOffset, width, format, type, pixels);
        this.restoreTexture1d();
    }

    @Override
    public void glTextureSubImage2d(int textureId, int level, int xOffset, int yOffset, int width, int height, int format, int type, ByteBuffer pixels) {
        this.saveTexture2d();
        GL11.glBindTexture(GL11.GL_TEXTURE_2D, textureId);
        GL11.glTexSubImage2D(GL11.GL_TEXTURE_2D, level, xOffset, yOffset, width, height, format, type, pixels);
        this.restoreTexture2d();
    }

    @Override
    public void glTextureSubImage3d(int textureId, int level, int xOffset, int yOffset, int zOffset, int width, int height, int depth, int format, int type, ByteBuffer pixels) {
        this.saveTexture3d();
        GL11.glBindTexture(GL12.GL_TEXTURE_3D, textureId);        
        GL12.glTexSubImage3D(GL12.GL_TEXTURE_3D, level, xOffset, yOffset, zOffset, width, height, depth, format, type, pixels);
        this.restoreTexture3d();
    }

    private static class Holder {

        private static final DirectStateAccess INSTANCE = new FakeDSA();
    }
}
