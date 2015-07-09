/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.longlinkislong.gloop.dsa;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.DoubleBuffer;
import java.nio.FloatBuffer;
import org.lwjgl.opengl.ARBBufferStorage;
import org.lwjgl.opengl.ARBFramebufferObject;
import org.lwjgl.opengl.ARBGPUShaderFP64;
import org.lwjgl.opengl.ContextCapabilities;
import org.lwjgl.opengl.EXTFramebufferObject;
import org.lwjgl.opengl.GL;
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
public class FakeDSA implements EXTDirectStateAccessPatch {

    private static final boolean CAN_CAST_DOUBLE_TO_FLOAT;
    private static final boolean IGNORE_FRAMEBUFFER_SUPPORT;
    private static final boolean IGNORE_BUFFER_STORAGE_SUPPORT;

    static {
        CAN_CAST_DOUBLE_TO_FLOAT = Boolean.parseBoolean(System.getProperty("gloop.dsa.can_cast_double_to_float", "true"));
        IGNORE_FRAMEBUFFER_SUPPORT = Boolean.parseBoolean(System.getProperty("gloop.dsa.ignore_framebuffer_support", "false"));
        IGNORE_BUFFER_STORAGE_SUPPORT = Boolean.parseBoolean(System.getProperty("gloop.dsa.ignore_buffer_storage_support", "true"));
    }

    private int saveFramebuffer = 0;

    private void saveFramebuffer() {
        this.saveFramebuffer = GL11.glGetInteger(GL30.GL_FRAMEBUFFER_BINDING);
    }

    private void restoreFramebuffer() {
        final ContextCapabilities cap = GL.getCapabilities();

        if (cap.OpenGL30) {
            GL30.glBindFramebuffer(GL30.GL_FRAMEBUFFER, this.saveFramebuffer);
        } else if (cap.GL_ARB_framebuffer_object) {
            ARBFramebufferObject.glBindFramebuffer(GL30.GL_FRAMEBUFFER, this.saveFramebuffer);
        } else if (cap.GL_EXT_framebuffer_object) {
            EXTFramebufferObject.glBindFramebufferEXT(GL30.GL_FRAMEBUFFER, this.saveFramebuffer);
        } else {
            throw new UnsupportedOperationException("glBindFramebuffer is not supported! glBindFramebuffer requires either: an OpenGL 3.0 context, ARB_framebuffer_object, or EXT_framebuffer_object.");
        }
    }

    private int saveBuffer = 0;

    private void saveBuffer() {
        this.saveBuffer = GL11.glGetInteger(GL15.GL_ARRAY_BUFFER_BINDING);
    }

    private void restoreBuffer() {
        GL15.glBindBuffer(GL15.GL_ARRAY_BUFFER, this.saveBuffer);
        assert GL11.glGetError() == GL11.GL_NO_ERROR : String.format("glBindBuffer(GL_ARRAY_BUFFER, %d) failed!", this.saveBuffer);
    }

    private int saveTex1d = 0;

    private void saveTexture1d() {
        this.saveTex1d = GL11.glGetInteger(GL11.GL_TEXTURE_BINDING_1D);
        assert GL11.glGetError() == GL11.GL_NO_ERROR : String.format("glGetInteger(GL_TEXTURE_BINDING_1D) = %d failed!", this.saveTex1d);
    }

    private void restoreTexture1d() {
        GL11.glBindTexture(GL11.GL_TEXTURE_1D, this.saveTex1d);
        assert GL11.glGetError() == GL11.GL_NO_ERROR : String.format("glBindTexture(GL_TEXTURE_1D, %d) failed!", this.saveTex1d);
    }

    private int saveTex2d = 0;

    private void saveTexture2d() {
        this.saveTex2d = GL11.glGetInteger(GL11.GL_TEXTURE_BINDING_2D);
        assert GL11.glGetError() == GL11.GL_NO_ERROR : String.format("glGetInteger(GL_TEXTURE_BINDING_2D) = %d failed!", this.saveTex2d);
    }

    private void restoreTexture2d() {
        GL11.glBindTexture(GL11.GL_TEXTURE_2D, this.saveTex2d);
        assert GL11.glGetError() == GL11.GL_NO_ERROR : String.format("glBindTexture(GL_TEXTURE_2D, %d) failed!", this.saveTex2d);
    }

    private int saveTex3d = 0;

    private void saveTexture3d() {
        this.saveTex3d = GL11.glGetInteger(GL12.GL_TEXTURE_BINDING_3D);
        assert GL11.glGetError() == GL11.GL_NO_ERROR : String.format("glGetInteger(GL_TEXTURE_BINDING_3D) = %d failed!", this.saveTex3d);
    }

    private void restoreTexture3d() {
        GL11.glBindTexture(GL12.GL_TEXTURE_3D, this.saveTex3d);
        assert GL11.glGetError() == GL11.GL_NO_ERROR : String.format("glBindTexture(GL_TEXTURE_3D, %d) failed!", this.saveTex3d);
    }

    private void saveTexture(int target) {
        switch (target) {
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
        switch (target) {
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
        assert GL11.glGetError() == GL11.GL_NO_ERROR : String.format("glGetInteger(GL_CURRENT_PROGRAM) = %d failed!", this.saveProgram);
    }

    private void restoreProgram() {
        GL20.glUseProgram(this.saveProgram);
        assert GL11.glGetError() == GL11.GL_NO_ERROR : String.format("glUseProgram(%d) failed!", this.saveProgram);
    }

    @Override
    public String toString() {
        return "FakeDSA";
    }

    @Override
    public int glCreateFramebuffers() {
        final ContextCapabilities cap = GL.getCapabilities();
        final int id;

        if (cap.OpenGL30) {
            id = GL30.glGenFramebuffers();
            assert GL11.glGetError() == GL11.GL_NO_ERROR : String.format("glGenFramebuffers() = %d failed!", id);
            this.saveFramebuffer();
            GL30.glBindFramebuffer(GL30.GL_FRAMEBUFFER, id);
            assert GL11.glGetError() == GL11.GL_NO_ERROR : String.format("glBindFramebuffer(GL_FRAMEBUFFER, %d) failed!", id);
            this.restoreFramebuffer();
        } else if (cap.GL_ARB_framebuffer_object) {
            id = ARBFramebufferObject.glGenFramebuffers();
            assert GL11.glGetError() == GL11.GL_NO_ERROR : String.format("glGenFramebuffersARB() = %d failed!", id);
            this.saveFramebuffer();
            ARBFramebufferObject.glBindFramebuffer(GL30.GL_FRAMEBUFFER, id);
            assert GL11.glGetError() == GL11.GL_NO_ERROR : String.format("glBindFramebufferARB(GL_FRAMEBUFFER, %d) failed!", id);
            this.restoreFramebuffer();
        } else if (cap.GL_EXT_framebuffer_object) {
            id = EXTFramebufferObject.glGenFramebuffersEXT();
            assert GL11.glGetError() == GL11.GL_NO_ERROR : String.format("glGenFramebuffersEXT() = %d failed!", id);
            this.saveFramebuffer();
            EXTFramebufferObject.glBindFramebufferEXT(GL30.GL_FRAMEBUFFER, id);
            assert GL11.glGetError() == GL11.GL_NO_ERROR : String.format("glBindFramebufferEXT(GL_FRAMEBUFFER, %d) failed!", id);
            this.restoreFramebuffer();
        } else {
            throw new UnsupportedOperationException("glGenFramebuffers is not supported! glGenFramebuffers requires an OpenGL 3.0 context, ARB_framebuffer_object, or EXT_framebuffer_object.");
        }

        return id;
    }

    @Override
    public int glCreateBuffers() {
        final int id = GL15.glGenBuffers();
        assert GL11.glGetError() == GL11.GL_NO_ERROR : String.format("glGenBuffers() = %d failed!", id);

        this.saveBuffer();
        GL15.glBindBuffer(GL15.GL_ARRAY_BUFFER, id);
        assert GL11.glGetError() == GL11.GL_NO_ERROR : String.format("glBindBuffer(GL_ARRAY_BUFFER, %d) failed!", id);

        this.restoreBuffer();

        return id;
    }

    @Override
    public int glCreateTextures(int target) {
        final int id = GL11.glGenTextures();
        assert GL11.glGetError() == GL11.GL_NO_ERROR : String.format("glGenTextures() = %d failed!", id);

        this.saveTexture(target);
        GL11.glBindTexture(target, id);
        assert GL11.glGetError() == GL11.GL_NO_ERROR : String.format("glBindTexture(%d, %d) failed!", target, id);

        this.restoreTexture(target);

        return id;
    }

    @Override
    public boolean isSupported() {
        final ContextCapabilities cap = GL.getCapabilities();

        final boolean hasDoubleSupport = cap.OpenGL40 || cap.GL_ARB_gpu_shader_fp64 || CAN_CAST_DOUBLE_TO_FLOAT;
        final boolean hasFramebufferSupport = cap.OpenGL30 || cap.GL_ARB_framebuffer_object || cap.GL_EXT_framebuffer_object || IGNORE_FRAMEBUFFER_SUPPORT;
        final boolean hasBufferStorageSupport = cap.OpenGL44 || cap.GL_ARB_buffer_storage || IGNORE_BUFFER_STORAGE_SUPPORT;

        return cap.OpenGL20 && hasDoubleSupport && hasFramebufferSupport && hasBufferStorageSupport;
    }

    @Override
    public void glGetNamedBufferSubData(int bufferId, long offset, ByteBuffer data) {
        this.saveBuffer();
        GL15.glBindBuffer(GL15.GL_ARRAY_BUFFER, bufferId);
        assert GL11.glGetError() == GL11.GL_NO_ERROR : String.format("glBindBuffer(GL_ARRAY_BUFFER, %d) failed!", bufferId);

        GL15.glGetBufferSubData(GL15.GL_ARRAY_BUFFER, offset, data);
        assert GL11.glGetError() == GL11.GL_NO_ERROR : String.format("glGetBufferSubData(GL_ARRAY_BUFFER, %d, [data]) failed!", offset);

        this.restoreBuffer();
    }

    @Override
    public int glGetNamedBufferParameteri(int bufferId, int pName) {
        this.saveBuffer();
        GL15.glBindBuffer(GL15.GL_ARRAY_BUFFER, bufferId);
        assert GL11.glGetError() == GL11.GL_NO_ERROR : String.format("glBindBuffer(GL_ARRAY_BUFFER, %d) failed!", bufferId);

        final int val = GL15.glGetBufferParameteri(GL15.GL_ARRAY_BUFFER, pName);
        assert GL11.glGetError() == GL11.GL_NO_ERROR : String.format("glGetBufferParameteri(GL_ARRAY_BUFFER, %d) = %d failed!", pName, val);

        this.restoreBuffer();

        return val;
    }

    @Override
    public void glNamedBufferData(int bufferId, long size, int usage) {
        this.saveBuffer();
        GL15.glBindBuffer(GL15.GL_ARRAY_BUFFER, bufferId);
        assert GL11.glGetError() == GL11.GL_NO_ERROR : String.format("glBindBuffer(GL_ARRAY_BUFFER, %d) failed!", bufferId);

        GL15.glBufferData(GL15.GL_ARRAY_BUFFER, size, usage);
        assert GL11.glGetError() == GL11.GL_NO_ERROR : String.format("glBufferData(GL_ARRAY_BUFFER, %d, %d) failed!", size, usage);

        this.restoreBuffer();
    }

    @Override
    public void glNamedBufferData(int bufferId, ByteBuffer data, int usage) {
        this.saveBuffer();
        GL15.glBindBuffer(GL15.GL_ARRAY_BUFFER, bufferId);
        assert GL11.glGetError() == GL11.GL_NO_ERROR : String.format("glBindBuffer(GL_ARRAY_BUFFER, %d) failed!", bufferId);

        GL15.glBufferData(GL15.GL_ARRAY_BUFFER, data, usage);
        assert GL11.glGetError() == GL11.GL_NO_ERROR : String.format("glBufferData(GL_ARRAY_BUFFER, [data], %d) failed!", usage);

        this.restoreBuffer();
    }

    @Override
    public void glNamedBufferSubData(int bufferId, long offset, ByteBuffer data) {
        this.saveBuffer();
        GL15.glBindBuffer(GL15.GL_ARRAY_BUFFER, bufferId);
        assert GL11.glGetError() == GL11.GL_NO_ERROR : String.format("glBindBuffer(GL_ARRAY_BUFFER, %d) failed!", bufferId);

        GL15.glBufferSubData(GL15.GL_ARRAY_BUFFER, offset, data);
        assert GL11.glGetError() == GL11.GL_NO_ERROR : String.format("glBufferSubData(GL_ARRAY_BUFFER, %d, [data]) failed!", offset);

        this.restoreBuffer();
    }

    @Override
    public void glNamedBufferStorage(int bufferId, ByteBuffer data, int flags) {
        final ContextCapabilities cap = GL.getCapabilities();

        this.saveBuffer();
        GL15.glBindBuffer(GL15.GL_ARRAY_BUFFER, bufferId);
        assert GL11.glGetError() == GL11.GL_NO_ERROR : String.format("glBindBuffer(GL_ARRAY_BUFFER, %d) failed!", bufferId);

        if (cap.OpenGL44) {
            GL44.glBufferStorage(GL15.GL_ARRAY_BUFFER, data, flags);
            assert GL11.glGetError() == GL11.GL_NO_ERROR : String.format("glBufferStorage(GL_ARRAY_BUFFER, [data], %d) failed!", flags);
        } else if (cap.GL_ARB_buffer_storage) {
            ARBBufferStorage.glBufferStorage(flags, data, flags);
            assert GL11.glGetError() == GL11.GL_NO_ERROR : String.format("glBufferStorageARB(GL_ARRAY_BUFFER, [data], %d) failed!", flags);
        } else {
            throw new UnsupportedOperationException("glBufferStorage(target, size, flags) is not supported!");
        }

        this.restoreBuffer();
    }

    @Override
    public void glNamedBufferStorage(int bufferId, long size, int flags) {
        final ContextCapabilities cap = GL.getCapabilities();

        this.saveBuffer();
        GL15.glBindBuffer(GL15.GL_ARRAY_BUFFER, bufferId);
        assert GL11.glGetError() == GL11.GL_NO_ERROR : String.format("glBindBuffer(GL_ARRAY_BUFFER, %d) failed!", bufferId);

        if (cap.OpenGL44) {
            GL44.glBufferStorage(GL15.GL_ARRAY_BUFFER, size, flags);
            assert GL11.glGetError() == GL11.GL_NO_ERROR : String.format("glBufferStorage(GL_ARRAY_BUFFER, %d, %d) failed!", size, flags);
        } else if (cap.GL_ARB_buffer_storage) {
            ARBBufferStorage.glBufferStorage(GL15.GL_ARRAY_BUFFER, size, flags);
            assert GL11.glGetError() == GL11.GL_NO_ERROR : String.format("glBufferStorageARB(GL_ARRAY_BUFFER, %d, %d) failed!", size, flags);
        } else {
            throw new UnsupportedOperationException("glBufferStorage(target, size, flags) is not supported!");
        }

        this.restoreBuffer();
    }

    @Override
    public ByteBuffer glMapNamedBufferRange(int bufferId, long offset, long length, int access, ByteBuffer recycled) {
        this.saveBuffer();
        GL15.glBindBuffer(GL15.GL_ARRAY_BUFFER, bufferId);
        assert GL11.glGetError() == GL11.GL_NO_ERROR : String.format("glBindBuffer(GL_ARRAY_BUFFER, %d) failed!", bufferId);

        final ByteBuffer out = GL30.glMapBufferRange(GL15.GL_ARRAY_BUFFER, offset, length, access, recycled);
        assert GL11.glGetError() == GL11.GL_NO_ERROR : String.format("glMapBufferRange(GL_ARRAY_BUFFER, %d, %d, %d, [recycled]) = [data] failed!", offset, length, access);

        this.restoreBuffer();

        return out;
    }

    @Override
    public void glUnmapNamedBuffer(int bufferId) {
        this.saveBuffer();
        GL15.glBindBuffer(GL15.GL_ARRAY_BUFFER, bufferId);
        assert GL11.glGetError() == GL11.GL_NO_ERROR : String.format("glBindBuffer(GL_ARRAY_BUFFER, %d) failed!", bufferId);

        GL15.glUnmapBuffer(GL15.GL_ARRAY_BUFFER);
        assert GL11.glGetError() == GL11.GL_NO_ERROR : "glUnmapBuffer(GL_ARRAY_BUFFER) failed!";
        this.restoreBuffer();
    }

    @Override
    public void glCopyNamedBufferSubData(int readBufferId, int writeBufferId, long readOffset, long writeOffset, long size) {
        GL15.glBindBuffer(GL31.GL_COPY_READ_BUFFER, readBufferId);
        assert GL11.glGetError() == GL11.GL_NO_ERROR : String.format("glBindBuffer(GL_COPY_READ_BUFFER, %d) failed!", readBufferId);

        GL15.glBindBuffer(GL31.GL_COPY_WRITE_BUFFER, writeBufferId);
        assert GL11.glGetError() == GL11.GL_NO_ERROR : String.format("glBindBuffer(GL_COPY_WRITE_BUFFER, %d) failed!", writeBufferId);

        GL31.glCopyBufferSubData(GL31.GL_COPY_READ_BUFFER, GL31.GL_COPY_WRITE_BUFFER, readOffset, writeOffset, size);
        assert GL11.glGetError() == GL11.GL_NO_ERROR : String.format("glCopyBufferSubData(GL_COPY_READ_BUFFER, GL_COPY_WRITE_BUFFER, %d, %d, %d) failed!", readOffset, writeOffset, size);

        GL15.glBindBuffer(GL31.GL_COPY_READ_BUFFER, 0);
        assert GL11.glGetError() == GL11.GL_NO_ERROR : "glBindBuffer(GL_COPY_READ_BUFFER, 0) failed!";

        GL15.glBindBuffer(GL31.GL_COPY_WRITE_BUFFER, 0);
        assert GL11.glGetError() == GL11.GL_NO_ERROR : "glBindBuffer(GL_COPY_WRITE_BUFFER, 0) failed!";
    }

    @Override
    public void glProgramUniform1f(int programId, int location, float value) {
        this.saveProgram();
        GL20.glUseProgram(programId);
        assert GL11.glGetError() == GL11.GL_NO_ERROR : String.format("glUseProgram(%d) failed!", programId);

        GL20.glUniform1f(location, value);
        assert GL11.glGetError() == GL11.GL_NO_ERROR : String.format("glUniform1f(%d, %f) failed!", location, value);

        this.restoreProgram();
    }

    @Override
    public void glProgramUniform2f(int programId, int location, float v0, float v1) {
        this.saveProgram();
        GL20.glUseProgram(programId);
        assert GL11.glGetError() == GL11.GL_NO_ERROR : String.format("glUseProgram(%d) failed!", programId);

        GL20.glUniform2f(location, v0, v1);
        assert GL11.glGetError() == GL11.GL_NO_ERROR : String.format("glUniform2f(%d, %f, %f) failed!", location, v0, v1);

        this.restoreProgram();
    }

    @Override
    public void glProgramUniform3f(int programId, int location, float v0, float v1, float v2) {
        this.saveProgram();
        GL20.glUseProgram(programId);
        assert GL11.glGetError() == GL11.GL_NO_ERROR : String.format("glUseProgram(%d) failed!", programId);

        GL20.glUniform3f(location, v0, v1, v2);
        assert GL11.glGetError() == GL11.GL_NO_ERROR : String.format("glUniform3f(%d, %f, %f) failed!", location, v0, v1, v2);

        this.restoreProgram();
    }

    @Override
    public void glProgramUniform4f(int programId, int location, float v0, float v1, float v2, float v3) {
        this.saveProgram();
        GL20.glUseProgram(programId);
        assert GL11.glGetError() == GL11.GL_NO_ERROR : String.format("glUseProgram(%d) failed!", programId);

        GL20.glUniform4f(location, v0, v1, v2, v3);
        assert GL11.glGetError() == GL11.GL_NO_ERROR : String.format("glUniform4f(%d, %f, %f, %f, %f) failed!", location, v0, v1, v2, v3);

        this.restoreProgram();
    }

    @Override
    public void glProgramUniform1d(int programId, int location, double value) {
        final ContextCapabilities cap = GL.getCapabilities();

        this.saveProgram();
        GL20.glUseProgram(programId);
        assert GL11.glGetError() == GL11.GL_NO_ERROR : String.format("glUseProgram(%d) failed!", programId);

        if (cap.OpenGL40) {
            GL40.glUniform1d(location, value);
        } else if (cap.GL_ARB_gpu_shader_fp64) {
            ARBGPUShaderFP64.glUniform1d(location, value);
        } else if (CAN_CAST_DOUBLE_TO_FLOAT) {
            GL20.glUniform1f(location, (float) value);
        } else {
            throw new UnsupportedOperationException("glUniform1d(location, double) is not supported!");
        }

        this.restoreProgram();
    }

    @Override
    public void glProgramUniform2d(int programId, int location, double v0, double v1) {
        final ContextCapabilities cap = GL.getCapabilities();

        this.saveProgram();
        GL20.glUseProgram(programId);
        assert GL11.glGetError() == GL11.GL_NO_ERROR : String.format("glUseProgram(%d) failed!", programId);

        if (cap.OpenGL40) {
            GL40.glUniform2d(location, v0, v1);
        } else if (cap.GL_ARB_gpu_shader_fp64) {
            ARBGPUShaderFP64.glUniform2d(location, v0, v1);
        } else if (CAN_CAST_DOUBLE_TO_FLOAT) {
            GL20.glUniform2f(location, (float) v0, (float) v1);
        } else {
            throw new UnsupportedOperationException("glUniform2d(location, double, double) is not supported!");
        }

        this.restoreProgram();
    }

    @Override
    public void glProgramUniform3d(int programId, int location, double v0, double v1, double v2) {
        final ContextCapabilities cap = GL.getCapabilities();

        this.saveProgram();
        GL20.glUseProgram(programId);
        assert GL11.glGetError() == GL11.GL_NO_ERROR : String.format("glUseProgram(%d) failed!", programId);

        if (cap.OpenGL40) {
            GL40.glUniform3d(location, v0, v1, v2);
        } else if (cap.GL_ARB_gpu_shader_fp64) {
            ARBGPUShaderFP64.glUniform3d(location, v0, v1, v2);
        } else if (CAN_CAST_DOUBLE_TO_FLOAT) {
            GL20.glUniform3f(location, (float) v0, (float) v1, (float) v2);
        } else {
            throw new UnsupportedOperationException("glUniform3d(location, double, double, double) is not supported!");
        }
        this.restoreProgram();
    }

    @Override
    public void glProgramUniform4d(int programId, int location, double v0, double v1, double v2, double v3) {
        final ContextCapabilities cap = GL.getCapabilities();

        this.saveProgram();
        GL20.glUseProgram(programId);
        assert GL11.glGetError() == GL11.GL_NO_ERROR : String.format("glUseProgram(%d) failed!", programId);

        if (cap.OpenGL40) {
            GL40.glUniform4d(location, v0, v1, v2, v3);
        } else if (cap.GL_ARB_gpu_shader_fp64) {
            ARBGPUShaderFP64.glUniform4d(location, v0, v1, v2, v3);
        } else if (CAN_CAST_DOUBLE_TO_FLOAT) {
            GL20.glUniform4f(location, (float) v0, (float) v1, (float) v2, (float) v3);
        } else {
            throw new UnsupportedOperationException("glUniform4f(location, double, double, double, double) is not supported!");
        }

        GL40.glUniform4d(location, v0, v1, v2, v3);
        this.restoreProgram();
    }

    @Override
    public void glProgramUniformMatrix2f(int programId, int location, boolean needsTranspose, FloatBuffer data) {
        this.saveProgram();
        GL20.glUseProgram(programId);
        assert GL11.glGetError() == GL11.GL_NO_ERROR : String.format("glUseProgram(%d) failed!", programId);

        GL20.glUniformMatrix2fv(location, needsTranspose, data);
        this.restoreProgram();
    }

    @Override
    public void glProgramUniformMatrix3f(int programId, int location, boolean needsTranspose, FloatBuffer data) {
        this.saveProgram();
        GL20.glUseProgram(programId);
        assert GL11.glGetError() == GL11.GL_NO_ERROR : String.format("glUseProgram(%d) failed!", programId);

        GL20.glUniformMatrix3fv(location, needsTranspose, data);
        this.restoreProgram();
    }

    @Override
    public void glProgramUniformMatrix4f(int programId, int location, boolean needsTranspose, FloatBuffer data) {
        this.saveProgram();
        GL20.glUseProgram(programId);
        assert GL11.glGetError() == GL11.GL_NO_ERROR : String.format("glUseProgram(%d) failed!", programId);

        GL20.glUniformMatrix4fv(location, needsTranspose, data);
        this.restoreProgram();
    }

    @Override
    public void glProgramUniformMatrix2d(int programId, int location, boolean needsTranspose, DoubleBuffer data) {
        final ContextCapabilities cap = GL.getCapabilities();

        this.saveProgram();
        GL20.glUseProgram(programId);
        assert GL11.glGetError() == GL11.GL_NO_ERROR : String.format("glUseProgram(%d) failed!", programId);

        if (cap.OpenGL40) {
            GL40.glUniformMatrix2dv(location, needsTranspose, data);
        } else if (cap.GL_ARB_gpu_shader_fp64) {
            ARBGPUShaderFP64.glUniformMatrix2dv(location, needsTranspose, data);
        } else if (CAN_CAST_DOUBLE_TO_FLOAT) {
            FloatBuffer casted = ByteBuffer
                    .allocateDirect(4 * Float.BYTES)
                    .order(ByteOrder.nativeOrder())
                    .asFloatBuffer();

            for (int i = 0; i < 4; i++) {
                casted.put((float) data.get());
            }

            casted.flip();

            GL20.glUniformMatrix2fv(location, needsTranspose, casted);
        } else {
            throw new UnsupportedOperationException("glUniformMatrix2dv(location, needsTranspose, data) is not supported!");
        }

        this.restoreProgram();
    }

    @Override
    public void glProgramUniformMatrix3d(int programId, int location, boolean needsTranspose, DoubleBuffer data) {
        final ContextCapabilities cap = GL.getCapabilities();

        this.saveProgram();
        GL20.glUseProgram(programId);
        assert GL11.glGetError() == GL11.GL_NO_ERROR : String.format("glUseProgram(%d) failed!", programId);

        if (cap.OpenGL40) {
            GL40.glUniformMatrix3dv(location, needsTranspose, data);
        } else if (cap.GL_ARB_gpu_shader_fp64) {
            ARBGPUShaderFP64.glUniformMatrix3dv(location, needsTranspose, data);
        } else if (CAN_CAST_DOUBLE_TO_FLOAT) {
            final FloatBuffer casted = ByteBuffer
                    .allocateDirect(9 * Float.BYTES)
                    .order(ByteOrder.nativeOrder())
                    .asFloatBuffer();

            for (int i = 0; i < 9; i++) {
                casted.put((float) data.get());
            }

            casted.flip();

            GL20.glUniformMatrix3fv(location, needsTranspose, casted);
        } else {
            throw new UnsupportedOperationException("glUniformMatrix3dv(location, needsTranspose, data) is not supported!");
        }

        this.restoreProgram();
    }

    @Override
    public void glProgramUniformMatrix4d(int programId, int location, boolean needsTranspose, DoubleBuffer data) {
        final ContextCapabilities cap = GL.getCapabilities();

        this.saveProgram();
        GL20.glUseProgram(programId);
        assert GL11.glGetError() == GL11.GL_NO_ERROR : String.format("glUseProgram(%d) failed!", programId);

        if (cap.OpenGL40) {
            GL40.glUniformMatrix4dv(location, needsTranspose, data);
        } else if (cap.GL_ARB_gpu_shader_fp64) {
            ARBGPUShaderFP64.glUniformMatrix4dv(location, needsTranspose, data);
        } else if (CAN_CAST_DOUBLE_TO_FLOAT) {
            final FloatBuffer casted = ByteBuffer
                    .allocateDirect(16 * Float.BYTES)
                    .order(ByteOrder.nativeOrder())
                    .asFloatBuffer();

            for (int i = 0; i < 16; i++) {
                casted.put((float) data.get());
            }

            casted.flip();

            GL20.glUniformMatrix4fv(location, needsTranspose, casted);
        } else {
            throw new UnsupportedOperationException("glUniformMatrix4dv(location, needsTranspose, data) is not supported!");
        }

        this.restoreProgram();
    }

    @Override
    public void glTextureParameteri(int textureId, int target, int pName, int val) {
        this.saveTexture(target);
        GL11.glBindTexture(target, textureId);
        assert GL11.glGetError() == GL11.GL_NO_ERROR : String.format("glBindTexture(%d, %d) failed!", target, textureId);

        GL11.glTexParameteri(target, pName, val);
        this.restoreTexture(target);
    }

    @Override
    public void glTextureParameterf(int textureId, int target, int pName, float val) {
        this.saveTexture(target);
        GL11.glBindTexture(target, textureId);
        assert GL11.glGetError() == GL11.GL_NO_ERROR : String.format("glBindTexture(%d, %d) failed!", target, textureId);

        GL11.glTexParameterf(target, pName, val);
        this.restoreTexture(target);
    }

    @Override
    public void glGenerateTextureMipmap(int textureId, int target) {
        final ContextCapabilities cap = GL.getCapabilities();

        this.saveTexture(target);

        GL11.glBindTexture(target, textureId);
        assert GL11.glGetError() == GL11.GL_NO_ERROR : String.format("glBindTexture(%d, %d) failed!", target, textureId);

        if (cap.OpenGL30) {
            GL30.glGenerateMipmap(target);
            assert GL11.glGetError() == GL11.GL_NO_ERROR : String.format("glGenerateMipmap(%d) failed!", target);
        } else if (cap.GL_ARB_framebuffer_object) {
            ARBFramebufferObject.glGenerateMipmap(target);
            assert GL11.glGetError() == GL11.GL_NO_ERROR : String.format("glGenerateMipmapARB(%d) failed!", target);
        } else if (cap.GL_EXT_framebuffer_object) {
            EXTFramebufferObject.glGenerateMipmapEXT(target);
            assert GL11.glGetError() == GL11.GL_NO_ERROR : String.format("glGenerateMipmapEXT(%d) failed!", target);
        } else {
            throw new UnsupportedOperationException("glGenerateMipmap(target) is not supported!");
        }

        this.restoreTexture(target);
    }

    @Override
    public void glBindTextureUnit(int unit, int target, int textureId) {
        GL13.glActiveTexture(unit);
        assert GL11.glGetError() == GL11.GL_NO_ERROR : String.format("glActiveTexture(%d) failed!", unit);

        GL11.glBindTexture(target, textureId);
        assert GL11.glGetError() == GL11.GL_NO_ERROR : String.format("glBindTexture(%d, %d) failed!", target, textureId);
    }

    public static DirectStateAccess getInstance() {
        return Holder.INSTANCE;
    }

    @Override
    public void glProgramUniform1i(int programId, int location, int value) {
        this.saveProgram();
        GL20.glUseProgram(programId);
        assert GL11.glGetError() == GL11.GL_NO_ERROR : String.format("glUseProgram(%d) failed!", programId);

        GL20.glUniform1i(location, value);
        this.restoreProgram();
    }

    @Override
    public void glProgramUniform2i(int programId, int location, int v0, int v1) {
        this.saveProgram();
        GL20.glUseProgram(programId);
        assert GL11.glGetError() == GL11.GL_NO_ERROR : String.format("glUseProgram(%d) failed!", programId);

        GL20.glUniform2i(location, v0, v1);
        this.restoreProgram();
    }

    @Override
    public void glProgramUniform3i(int programId, int location, int v0, int v1, int v2) {
        this.saveProgram();
        GL20.glUseProgram(programId);
        assert GL11.glGetError() == GL11.GL_NO_ERROR : String.format("glUseProgram(%d) failed!", programId);

        GL20.glUniform3i(location, v0, v1, v2);
        this.restoreProgram();
    }

    @Override
    public void glProgramUniform4i(int programId, int location, int v0, int v1, int v2, int v3) {
        this.saveProgram();
        GL20.glUseProgram(programId);
        assert GL11.glGetError() == GL11.GL_NO_ERROR : String.format("glUseProgram(%d) failed!", programId);

        GL20.glUniform4i(location, v0, v1, v2, v3);
        this.restoreProgram();
    }

    @Override
    public void glTextureSubImage1d(int textureId, int level, int xOffset, int width, int format, int type, ByteBuffer pixels) {
        this.saveTexture1d();

        GL11.glBindTexture(GL11.GL_TEXTURE_1D, textureId);
        assert GL11.glGetError() == GL11.GL_NO_ERROR : String.format("glBindTexture(GL_TEXTURE_1D, %d) failed!", textureId);

        GL11.glTexSubImage1D(GL11.GL_TEXTURE_1D, level, xOffset, width, format, type, pixels);
        assert GL11.glGetError() == GL11.GL_NO_ERROR : String.format("glTexSubImage1D(GL_TEXTURE_1D, %d, %d, %d, %d, %d, [data]) failed!", level, xOffset, width, format, type);

        this.restoreTexture1d();
    }

    @Override
    public void glTextureSubImage2d(int textureId, int level, int xOffset, int yOffset, int width, int height, int format, int type, ByteBuffer pixels) {
        this.saveTexture2d();

        GL11.glBindTexture(GL11.GL_TEXTURE_2D, textureId);
        assert GL11.glGetError() == GL11.GL_NO_ERROR : "glBindTexture(GL_TEXTURE_2D) failed!";

        GL11.glTexSubImage2D(GL11.GL_TEXTURE_2D, level, xOffset, yOffset, width, height, format, type, pixels);
        assert GL11.glGetError() == GL11.GL_NO_ERROR : String.format("glTexSubImage2D(GL_TEXTURE_2D, %d, %d, %d, %d, %d, %d, %d, [data]) failed!",
                level, xOffset, yOffset, width, height, format, type);

        this.restoreTexture2d();
    }

    @Override
    public void glTextureSubImage3d(int textureId, int level, int xOffset, int yOffset, int zOffset, int width, int height, int depth, int format, int type, ByteBuffer pixels) {
        this.saveTexture3d();
        GL11.glBindTexture(GL12.GL_TEXTURE_3D, textureId);
        assert GL11.glGetError() == GL11.GL_NO_ERROR : String.format("glBindTexture(GL_TEXTURE_3D, %d) failed!", textureId);

        GL12.glTexSubImage3D(GL12.GL_TEXTURE_3D, level, xOffset, yOffset, zOffset, width, height, depth, format, type, pixels);
        assert GL11.glGetError() == GL11.GL_NO_ERROR : String.format("glTexSubImage3D(GL_TEXTURE_3D, %d, %d, %d, %d, %d, %d, %d, %d, %d, [data]) failed!",
                level, xOffset, yOffset, zOffset, width, height, depth, format, type);

        this.restoreTexture3d();
    }

    @Override
    public void glTextureImage1d(int texture, int target, int level, int internalFormat, int width, int border, int format, int type, ByteBuffer pixels) {
        this.saveTexture1d();
        GL11.glBindTexture(target, texture);
        assert GL11.glGetError() == GL11.GL_NO_ERROR : String.format("glBindTexture(%d, %d) failed!", texture, target);

        GL11.glTexImage1D(target, level, internalFormat, width, border, format, type, pixels);
        assert GL11.glGetError() == GL11.GL_NO_ERROR : String.format("glTexImage1D(%d, %d, %d, %d, %d, %d, %d, [data]) failed!",
                target, level, internalFormat, width, border, format, type);

        this.restoreTexture1d();
    }

    @Override
    public void glTextureImage1d(int texture, int target, int level, int internalFormat, int width, int border, int format, int type, long ptr) {
        this.saveTexture1d();

        GL11.glBindTexture(target, texture);
        assert GL11.glGetError() == GL11.GL_NO_ERROR : String.format("glBindTexture(%d, %d) failed!", texture, target);

        GL11.glTexImage1D(target, level, internalFormat, width, border, format, type, ptr);
        assert GL11.glGetError() == GL11.GL_NO_ERROR : String.format("glTexImage2D(%d, %d, %d %d, %d, %d, %d, %d) failed!",
                target, level, internalFormat, width, border, format, type, ptr);

        this.restoreTexture1d();
    }

    @Override
    public void glTextureImage2d(int texture, int target, int level, int internalFormat, int width, int height, int border, int format, int type, ByteBuffer pixels) {
        this.saveTexture2d();
        GL11.glBindTexture(target, texture);
        assert GL11.glGetError() == GL11.GL_NO_ERROR : String.format("glBindTexture(%d, %d) failed!", texture, target);

        GL11.glTexImage2D(target, level, internalFormat, width, height, border, format, type, pixels);
        assert GL11.glGetError() == GL11.GL_NO_ERROR : String.format("glTexImage2D(%d, %d, %d, %d, %d, %d, %d, %d, [data]) failed!",
                target, level, internalFormat, width, height, border, format, type);

        this.restoreTexture2d();
    }

    @Override
    public void glTextureImage2d(int texture, int target, int level, int internalFormat, int width, int height, int border, int format, int type, long ptr) {
        this.saveTexture2d();
        GL11.glBindTexture(target, texture);
        assert GL11.glGetError() == GL11.GL_NO_ERROR : String.format("glBindTexture(%d, %d) failed!", texture, target);

        GL11.glTexImage2D(target, level, internalFormat, width, height, border, format, type, ptr);
        assert GL11.glGetError() == GL11.GL_NO_ERROR : String.format("glTexImage2D(%d, %d, %d, %d, %d, %d, %d, %d, %d) failed!",
                target, level, internalFormat, width, height, border, format, type, ptr);

        this.restoreTexture2d();
    }

    @Override
    public void glTextureImage3d(int texture, int target, int level, int internalFormat, int width, int height, int depth, int border, int format, int type, ByteBuffer pixels) {
        this.saveTexture3d();
        GL11.glBindTexture(target, texture);
        assert GL11.glGetError() == GL11.GL_NO_ERROR : String.format("glBindTexture(%d, %d) failed!", texture, target);

        GL12.glTexImage3D(target, level, internalFormat, width, height, depth, border, format, type, pixels);
        assert GL11.glGetError() == GL11.GL_NO_ERROR : String.format("glTexImage2D(%d, %d, %d, %d, %d, %d, %d, %d, %d, [data]) failed!",
                target, level, internalFormat, width, height, depth, border, format, type);

        this.restoreTexture3d();
    }

    @Override
    public void glTextureImage3d(int texture, int target, int level, int internalFormat, int width, int height, int depth, int border, int format, int type, long ptr) {
        this.saveTexture3d();
        GL11.glBindTexture(target, texture);
        assert GL11.glGetError() == GL11.GL_NO_ERROR : String.format("glBindTexture(%d, %d) failed!", texture, target);

        GL12.glTexImage3D(target, level, internalFormat, width, height, depth, border, format, type, ptr);
        assert GL11.glGetError() == GL11.GL_NO_ERROR : String.format("glTexImage2D(%d, %d, %d, %d, %d, %d, %d, %d, %d, %d) failed!",
                target, level, internalFormat, width, height, depth, border, format, type, ptr);

        this.restoreTexture3d();
    }

    @Override
    public void glNamedFramebufferTexture1D(int framebuffer, int attachment, int texTarget, int texture, int level) {
        final ContextCapabilities cap = GL.getCapabilities();

        if (cap.OpenGL30) {
            this.saveFramebuffer();
            GL30.glFramebufferTexture1D(framebuffer, attachment, texTarget, texture, level);
            assert GL11.glGetError() == GL11.GL_NO_ERROR : String.format("glFramebufferTexture1D(%d, %d, %d, %d, %d) failed!", framebuffer, attachment, texTarget, texture, level);
            this.restoreFramebuffer();
        } else if (cap.GL_ARB_framebuffer_object) {
            this.saveFramebuffer();
            ARBFramebufferObject.glFramebufferTexture1D(framebuffer, attachment, texTarget, texture, level);
            assert GL11.glGetError() == GL11.GL_NO_ERROR : String.format("glFramebufferTexture1DARB(%d, %d, %d, %d, %d) failed!", framebuffer, attachment, texTarget, texture, level);
            this.restoreFramebuffer();
        } else if (cap.GL_EXT_framebuffer_object) {
            this.saveFramebuffer();
            EXTFramebufferObject.glFramebufferTexture1DEXT(framebuffer, attachment, texTarget, texture, level);
            assert GL11.glGetError() == GL11.GL_NO_ERROR : String.format("glFramebufferTexture1DEXT(%d, %d, %d, %d, %d) failed!", framebuffer, attachment, texTarget, texture, level);
            this.restoreFramebuffer();
        } else {
            throw new UnsupportedOperationException("glFramebufferTexture1D is not supported. glFramebufferTexture1D requires either: an OpenGL 3.0 context, ARB_framebuffer_object, or EXT_framebuffer_object.");
        }

        this.restoreFramebuffer();
    }

    @Override
    public void glNamedFramebufferTexture2D(int framebuffer, int attachment, int texTarget, int texture, int level) {
        final ContextCapabilities cap = GL.getCapabilities();

        if (cap.OpenGL30) {
            this.saveFramebuffer();
            GL30.glFramebufferTexture2D(framebuffer, attachment, texTarget, texture, level);
            assert GL11.glGetError() == GL11.GL_NO_ERROR : String.format("glFramebufferTexture2D(%d, %d, %d, %d, %d) failed!", framebuffer, attachment, texTarget, texture, level);
            this.restoreFramebuffer();
        } else if (cap.GL_ARB_framebuffer_object) {
            this.saveFramebuffer();
            ARBFramebufferObject.glFramebufferTexture2D(framebuffer, attachment, texTarget, texture, level);
            assert GL11.glGetError() == GL11.GL_NO_ERROR : String.format("glFramebufferTexture2DARB(%d, %d, %d, %d, %d) failed!", framebuffer, attachment, texTarget, texture, level);
            this.restoreFramebuffer();
        } else if (cap.GL_EXT_framebuffer_object) {
            this.saveFramebuffer();
            EXTFramebufferObject.glFramebufferTexture2DEXT(framebuffer, attachment, texTarget, texture, level);
            assert GL11.glGetError() == GL11.GL_NO_ERROR : String.format("glFramebufferTexture2DEXT(%d, %d, %d, %d, %d) failed!", framebuffer, attachment, texTarget, texture, level);
            this.restoreFramebuffer();
        } else {
            throw new UnsupportedOperationException("glFramebufferTexture2D is not supported. glFramebufferTexture2D requires either: an OpenGL 3.0 context, ARB_framebuffer_object, or EXT_framebuffer_object.");
        }
    }

    private static class Holder {

        private static final DirectStateAccess INSTANCE = new FakeDSA();
    }
}
