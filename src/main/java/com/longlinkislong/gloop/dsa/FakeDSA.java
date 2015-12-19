/* 
 * Copyright (c) 2015, longlinkislong.com
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 *
 * * Redistributions of source code must retain the above copyright notice, this
 *   list of conditions and the following disclaimer.
 * * Redistributions in binary form must reproduce the above copyright notice,
 *   this list of conditions and the following disclaimer in the documentation
 *   and/or other materials provided with the distribution.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS"
 * AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE
 * IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE
 * ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT HOLDER OR CONTRIBUTORS BE
 * LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR
 * CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF
 * SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS
 * INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN
 * CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE)
 * ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE
 * POSSIBILITY OF SUCH DAMAGE.
 */
package com.longlinkislong.gloop.dsa;

import static com.longlinkislong.gloop.GLAsserts.NON_DIRECT_BUFFER_MSG;
import static com.longlinkislong.gloop.GLAsserts.bufferIsNotNativeMsg;
import static com.longlinkislong.gloop.GLAsserts.bufferTooSmallMsg;
import static com.longlinkislong.gloop.GLAsserts.checkBufferIsNative;
import static com.longlinkislong.gloop.GLAsserts.checkBufferSize;
import static com.longlinkislong.gloop.GLAsserts.checkDimension;
import static com.longlinkislong.gloop.GLAsserts.checkDouble;
import static com.longlinkislong.gloop.GLAsserts.checkFloat;
import static com.longlinkislong.gloop.GLAsserts.checkGLError;
import static com.longlinkislong.gloop.GLAsserts.checkGLenum;
import static com.longlinkislong.gloop.GLAsserts.checkId;
import static com.longlinkislong.gloop.GLAsserts.checkMipmapLevel;
import static com.longlinkislong.gloop.GLAsserts.checkNullableId;
import static com.longlinkislong.gloop.GLAsserts.checkOffset;
import static com.longlinkislong.gloop.GLAsserts.checkSize;
import static com.longlinkislong.gloop.GLAsserts.glErrorMsg;
import static com.longlinkislong.gloop.GLAsserts.invalidBufferIdMsg;
import static com.longlinkislong.gloop.GLAsserts.invalidDepthMsg;
import static com.longlinkislong.gloop.GLAsserts.invalidDoubleMsg;
import static com.longlinkislong.gloop.GLAsserts.invalidFloatMsg;
import static com.longlinkislong.gloop.GLAsserts.invalidFramebufferIdMsg;
import static com.longlinkislong.gloop.GLAsserts.invalidGLenumMsg;
import static com.longlinkislong.gloop.GLAsserts.invalidHeightMsg;
import static com.longlinkislong.gloop.GLAsserts.invalidMipmapLevelMsg;
import static com.longlinkislong.gloop.GLAsserts.invalidOffsetMsg;
import static com.longlinkislong.gloop.GLAsserts.invalidProgramIdMsg;
import static com.longlinkislong.gloop.GLAsserts.invalidSizeMsg;
import static com.longlinkislong.gloop.GLAsserts.invalidTextureIdMsg;
import static com.longlinkislong.gloop.GLAsserts.invalidTextureUnitMsg;
import static com.longlinkislong.gloop.GLAsserts.invalidUniformLocationMsg;
import static com.longlinkislong.gloop.GLAsserts.invalidWidthMsg;
import static com.longlinkislong.gloop.GLAsserts.invalidXOffsetMsg;
import static com.longlinkislong.gloop.GLAsserts.invalidYOffsetMsg;
import static com.longlinkislong.gloop.GLAsserts.invalidZOffsetMsg;
import com.longlinkislong.gloop.GLBufferParameterName;
import com.longlinkislong.gloop.GLBufferUsage;
import com.longlinkislong.gloop.GLTextureFormat;
import com.longlinkislong.gloop.GLTextureInternalFormat;
import com.longlinkislong.gloop.GLTextureTarget;
import com.longlinkislong.gloop.GLType;
import static java.lang.Long.toHexString;
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
import org.lwjgl.opengl.GL14;
import org.lwjgl.opengl.GL15;
import org.lwjgl.opengl.GL20;
import org.lwjgl.opengl.GL21;
import org.lwjgl.opengl.GL30;
import org.lwjgl.opengl.GL31;
import org.lwjgl.opengl.GL40;
import org.lwjgl.opengl.GL44;
import static org.lwjgl.system.MemoryUtil.memAddress;

/**
 *
 * @author zmichaels
 */
public final class FakeDSA extends Common implements EXTDSADriver {

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
            assert checkGLError() : glErrorMsg("glBindFramebuffer(II)", "GL_FRAMEBUFFER", this.saveFramebuffer);
        } else if (cap.GL_ARB_framebuffer_object) {
            ARBFramebufferObject.glBindFramebuffer(GL30.GL_FRAMEBUFFER, this.saveFramebuffer);
            assert checkGLError() : glErrorMsg("glBindFramebufferARB(II)", "GL_FRAMEBUFFER", this.saveFramebuffer);
        } else if (cap.GL_EXT_framebuffer_object) {
            EXTFramebufferObject.glBindFramebufferEXT(GL30.GL_FRAMEBUFFER, this.saveFramebuffer);
            assert checkGLError() : glErrorMsg("glBindFramebufferEXT(II)", "GL_FRAMEBUFFER", this.saveFramebuffer);
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
        assert checkGLError() : glErrorMsg("glBindBuffer(II)", "GL_ARRAY_BUFFER", this.saveBuffer);
    }

    private int saveTex1d = 0;

    private void saveTexture1d() {
        this.saveTex1d = GL11.glGetInteger(GL11.GL_TEXTURE_BINDING_1D);
        assert checkGLError() : glErrorMsg("glGetInteger(I)", "GL_TEXTURE_BINDING_1D");
    }

    private void restoreTexture1d() {
        GL11.glBindTexture(GL11.GL_TEXTURE_1D, this.saveTex1d);
        assert checkGLError() : glErrorMsg("glBindTexture(II)", "GL_TEXTURE_1D", this.saveTex1d);
    }

    private int saveTex2d = 0;

    private void saveTexture2d() {
        this.saveTex2d = GL11.glGetInteger(GL11.GL_TEXTURE_BINDING_2D);
        assert checkGLError() : glErrorMsg("glGetInteger(I)", "GL_TEXTURE_BINDING_2D");
    }

    private void restoreTexture2d() {
        GL11.glBindTexture(GL11.GL_TEXTURE_2D, this.saveTex2d);
        assert checkGLError() : glErrorMsg("glBindTexture(II)", "GL_TEXTURE_2D", this.saveTex2d);
    }

    private int saveTex3d = 0;

    private void saveTexture3d() {
        this.saveTex3d = GL11.glGetInteger(GL12.GL_TEXTURE_BINDING_3D);
        assert checkGLError() : glErrorMsg("glGetInteger(I)", "GL_TEXTURE_BINDING_3D");
    }

    private void restoreTexture3d() {
        GL11.glBindTexture(GL12.GL_TEXTURE_3D, this.saveTex3d);
        assert checkGLError() : glErrorMsg("glBindTexture(II)", "GL_TEXTURE_3D", this.saveTex3d);
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
        assert checkGLError() : glErrorMsg("glGetInteger(I)", "GL_CURRENT_PROGRAM");
    }

    private void restoreProgram() {
        GL20.glUseProgram(this.saveProgram);
        assert checkGLError() : glErrorMsg("glUseProgram(I)", this.saveProgram);
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
            assert checkGLError() : glErrorMsg("glGenFramebuffers(void)");

            this.saveFramebuffer();

            GL30.glBindFramebuffer(GL30.GL_FRAMEBUFFER, id);
            assert checkGLError() : glErrorMsg("glBindFramebuffer(II)", "GL_FRAMEBUFFER", id);

            this.restoreFramebuffer();
        } else if (cap.GL_ARB_framebuffer_object) {
            id = ARBFramebufferObject.glGenFramebuffers();
            assert checkGLError() : glErrorMsg("glGenFramebuffersARB(void)");

            this.saveFramebuffer();

            ARBFramebufferObject.glBindFramebuffer(GL30.GL_FRAMEBUFFER, id);
            assert checkGLError() : glErrorMsg("glBindFramebufferARB(II)", "GL_FRAMEBUFFER", id);

            this.restoreFramebuffer();
        } else if (cap.GL_EXT_framebuffer_object) {
            id = EXTFramebufferObject.glGenFramebuffersEXT();
            assert checkGLError() : glErrorMsg("glGenFramebuffersEXT(void)");

            this.saveFramebuffer();

            EXTFramebufferObject.glBindFramebufferEXT(GL30.GL_FRAMEBUFFER, id);
            assert checkGLError() : glErrorMsg("glBindFramebufferEXT(II)", "GL_FRAMEBUFFER", id);

            this.restoreFramebuffer();
        } else {
            throw new UnsupportedOperationException("glGenFramebuffers is not supported! glGenFramebuffers requires an OpenGL 3.0 context, ARB_framebuffer_object, or EXT_framebuffer_object.");
        }

        return id;
    }

    @Override
    public int glCreateBuffers() {
        final int id = GL15.glGenBuffers();
        assert checkGLError() : glErrorMsg("glGenBuffers(void)");

        this.saveBuffer();

        GL15.glBindBuffer(GL15.GL_ARRAY_BUFFER, id);
        assert checkGLError() : glErrorMsg("glBindBuffer(II)", "GL_ARRAY_BUFFER", id);

        this.restoreBuffer();

        return id;
    }

    @Override
    public int glCreateTextures(int target) {
        assert checkGLenum(target, GLTextureTarget::of) : invalidGLenumMsg(target);

        final int id = GL11.glGenTextures();
        assert checkGLError() : glErrorMsg("glGenTextures(void)");

        this.saveTexture(target);

        GL11.glBindTexture(target, id);
        assert checkGLError() : glErrorMsg("glBindTexture(II)", GLTextureTarget.of(target).get(), id);

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
        assert checkId(bufferId) : invalidBufferIdMsg(bufferId);
        assert checkOffset(offset) : invalidOffsetMsg(offset);
        assert checkBufferIsNative(data) : bufferIsNotNativeMsg(data);
        assert data.isDirect() : NON_DIRECT_BUFFER_MSG;

        this.saveBuffer();
        GL15.glBindBuffer(GL15.GL_ARRAY_BUFFER, bufferId);
        assert checkGLError() : glErrorMsg("glBindBuffer(II)", "GL_ARRAY_BUFFER", bufferId);

        GL15.glGetBufferSubData(GL15.GL_ARRAY_BUFFER, offset, data);
        assert checkGLError() : glErrorMsg("glGetBufferSubData(IL*)", "GL_ARRAY_BUFFER", offset, toHexString(memAddress(data)));

        this.restoreBuffer();
    }

    @Override
    public int glGetNamedBufferParameteri(int bufferId, int pName) {
        assert checkId(bufferId) : invalidBufferIdMsg(bufferId);
        assert checkGLenum(pName, GLBufferParameterName::of) : invalidGLenumMsg(pName);

        this.saveBuffer();

        GL15.glBindBuffer(GL15.GL_ARRAY_BUFFER, bufferId);
        assert checkGLError() : glErrorMsg("glBindBuffer(II)", "GL_ARRAY_BUFFER", bufferId);

        final int val = GL15.glGetBufferParameteri(GL15.GL_ARRAY_BUFFER, pName);
        assert checkGLError() : glErrorMsg("glGetBufferParameteri(II)", "GL_ARRAY_BUFFER", GLBufferParameterName.of(pName).get());

        this.restoreBuffer();

        return val;
    }

    @Override
    public void glNamedBufferData(int bufferId, long size, int usage) {
        assert checkId(bufferId) : invalidBufferIdMsg(bufferId);
        assert checkSize(size) : invalidSizeMsg(size);
        assert checkGLenum(usage, GLBufferUsage::of) : invalidGLenumMsg(usage);

        this.saveBuffer();

        GL15.glBindBuffer(GL15.GL_ARRAY_BUFFER, bufferId);
        assert checkGLError() : glErrorMsg("glBindBuffer(II)", "GL_ARRAY_BUFFER", bufferId);

        GL15.glBufferData(GL15.GL_ARRAY_BUFFER, size, usage);
        assert checkGLError() : glErrorMsg("glBufferData(ILI)", "GL_ARRAY_BUFFER", size, GLBufferUsage.of(usage).get());

        this.restoreBuffer();
    }

    @Override
    public void glNamedBufferData(int bufferId, ByteBuffer data, int usage) {
        assert checkId(bufferId) : invalidBufferIdMsg(bufferId);
        assert checkBufferIsNative(data) : bufferIsNotNativeMsg(data);
        assert data.isDirect() : NON_DIRECT_BUFFER_MSG;
        assert checkGLenum(usage, GLBufferUsage::of) : invalidGLenumMsg(usage);

        this.saveBuffer();

        GL15.glBindBuffer(GL15.GL_ARRAY_BUFFER, bufferId);
        assert checkGLError() : glErrorMsg("glBindBuffer(II)", "GL_ARRAY_BUFFER", bufferId);

        GL15.glBufferData(GL15.GL_ARRAY_BUFFER, data, usage);
        assert checkGLError() : glErrorMsg("glBufferData(I*I)", bufferId, toHexString(memAddress(data)), GLBufferUsage.of(usage).get());

        this.restoreBuffer();
    }

    @Override
    public void glNamedBufferSubData(int bufferId, long offset, ByteBuffer data) {
        assert checkId(bufferId) : invalidBufferIdMsg(bufferId);
        assert checkOffset(offset) : invalidOffsetMsg(offset);
        assert checkBufferIsNative(data) : bufferIsNotNativeMsg(data);
        assert data.isDirect() : NON_DIRECT_BUFFER_MSG;

        this.saveBuffer();

        GL15.glBindBuffer(GL15.GL_ARRAY_BUFFER, bufferId);
        assert checkGLError() : glErrorMsg("glBindBuffer(II)", "GL_ARRAY_BUFFER", bufferId);

        GL15.glBufferSubData(GL15.GL_ARRAY_BUFFER, offset, data);
        assert checkGLError() : glErrorMsg("glBufferSubData(IL*)", "GL_ARRAY_BUFFER", offset, toHexString(memAddress(data)));

        this.restoreBuffer();
    }

    @Override
    public void glNamedBufferStorage(int bufferId, ByteBuffer data, int flags) {
        assert checkId(bufferId) : invalidBufferIdMsg(bufferId);
        assert checkBufferIsNative(data) : bufferIsNotNativeMsg(data);
        assert data.isDirect() : NON_DIRECT_BUFFER_MSG;

        final ContextCapabilities cap = GL.getCapabilities();

        this.saveBuffer();
        GL15.glBindBuffer(GL15.GL_ARRAY_BUFFER, bufferId);
        assert checkGLError() : glErrorMsg("glBindBuffer(II)", "GL_ARRAY_BUFFER", bufferId);

        if (cap.OpenGL44) {
            GL44.glBufferStorage(GL15.GL_ARRAY_BUFFER, data, flags);
            assert checkGLError() : glErrorMsg("glBufferStorage(I*I)", "GL_ARRAY_BUFFER", toHexString(memAddress(data)), flags);
        } else if (cap.GL_ARB_buffer_storage) {
            ARBBufferStorage.glBufferStorage(GL15.GL_ARRAY_BUFFER, data, flags);
            assert checkGLError() : glErrorMsg("glBufferStorageARB(I*I)", "GL_ARRAY_BUFFER", toHexString(memAddress(data)), flags);
        } else {
            throw new UnsupportedOperationException("glBufferStorage(target, size, flags) is not supported!");
        }

        this.restoreBuffer();
    }

    @Override
    public void glNamedBufferStorage(int bufferId, long size, int flags) {
        assert checkId(bufferId) : invalidBufferIdMsg(bufferId);
        assert checkSize(size) : invalidSizeMsg(size);

        final ContextCapabilities cap = GL.getCapabilities();

        this.saveBuffer();
        GL15.glBindBuffer(GL15.GL_ARRAY_BUFFER, bufferId);
        assert checkGLError() : glErrorMsg("glBindBuffer(II)", "GL_ARRAY_BUFFER", bufferId);

        if (cap.OpenGL44) {
            GL44.glBufferStorage(GL15.GL_ARRAY_BUFFER, size, flags);
            assert checkGLError() : glErrorMsg("glBufferStorage(ILI)", "GL_ARRAY_BUFFER", size, flags);
        } else if (cap.GL_ARB_buffer_storage) {
            ARBBufferStorage.glBufferStorage(GL15.GL_ARRAY_BUFFER, size, flags);
            assert checkGLError() : glErrorMsg("glBufferStorageARB(ILI)", "GL_ARRAY_BUFFER", size, flags);
        } else {
            throw new UnsupportedOperationException("glBufferStorage(target, size, flags) is not supported!");
        }

        this.restoreBuffer();
    }

    @Override
    public ByteBuffer glMapNamedBufferRange(int bufferId, long offset, long length, int access, ByteBuffer recycled) {
        assert checkId(bufferId) : invalidBufferIdMsg(bufferId);
        assert checkOffset(offset) : invalidOffsetMsg(offset);
        assert checkSize(length) : invalidSizeMsg(length);

        this.saveBuffer();
        GL15.glBindBuffer(GL15.GL_ARRAY_BUFFER, bufferId);
        assert checkGLError() : glErrorMsg("glBindBuffer(II)", "GL_ARRAY_BUFFER", bufferId);

        final ByteBuffer out = GL30.glMapBufferRange(GL15.GL_ARRAY_BUFFER, offset, length, access, recycled);
        assert checkGLError() : glErrorMsg("glMapBufferRange(ILLI)", "GL_ARRAY_BUFFER", offset, length, access);

        this.restoreBuffer();

        return out;
    }

    @Override
    public void glUnmapNamedBuffer(int bufferId) {
        assert checkId(bufferId);

        this.saveBuffer();
        GL15.glBindBuffer(GL15.GL_ARRAY_BUFFER, bufferId);
        assert checkGLError() : glErrorMsg("glBindBuffer(II)", "GL_ARRAY_BUFFER", bufferId);

        GL15.glUnmapBuffer(GL15.GL_ARRAY_BUFFER);
        assert checkGLError() : glErrorMsg("glUnmapBuffer(I)", "GL_ARRAY_BUFFER");

        this.restoreBuffer();
    }

    @Override
    public void glCopyNamedBufferSubData(int readBufferId, int writeBufferId, long readOffset, long writeOffset, long size) {
        assert checkId(readBufferId) : invalidBufferIdMsg(readBufferId);
        assert checkId(writeBufferId) : invalidBufferIdMsg(writeBufferId);
        assert checkOffset(readOffset) : invalidOffsetMsg(readOffset);
        assert checkOffset(writeOffset) : invalidOffsetMsg(writeOffset);
        assert checkSize(size) : invalidSizeMsg(size);

        GL15.glBindBuffer(GL31.GL_COPY_READ_BUFFER, readBufferId);
        assert checkGLError() : glErrorMsg("glBindBuffer(II)", "GL_COPY_READ_BUFFER", readBufferId);

        GL15.glBindBuffer(GL31.GL_COPY_WRITE_BUFFER, writeBufferId);
        assert checkGLError() : glErrorMsg("glBindBuffer(II)", "GL_COPY_WRITE_BUFFER", writeBufferId);

        GL31.glCopyBufferSubData(GL31.GL_COPY_READ_BUFFER, GL31.GL_COPY_WRITE_BUFFER, readOffset, writeOffset, size);
        assert checkGLError() : glErrorMsg("glCopyBufferSubData(IILLL)", "GL_COPY_READ_BUFFER", "GL_COPY_WRITE_BUFFER", readOffset, writeOffset, size);

        GL15.glBindBuffer(GL31.GL_COPY_READ_BUFFER, 0);
        assert checkGLError() : glErrorMsg("glBindBuffer(II)", "GL_COPY_READ_BUFFER", 0);

        GL15.glBindBuffer(GL31.GL_COPY_WRITE_BUFFER, 0);
        assert checkGLError() : glErrorMsg("glBindBuffer(II)", "GL_COPY_WRITE_BUFFER", 0);
    }

    @Override
    public void glProgramUniform1f(int programId, int location, float value) {
        assert checkId(programId) : invalidProgramIdMsg(programId);
        assert checkOffset(location) : invalidUniformLocationMsg(location);
        assert checkFloat(value) : invalidFloatMsg(value);

        this.saveProgram();
        GL20.glUseProgram(programId);
        assert checkGLError() : glErrorMsg("glUseProgram(I)", programId);

        GL20.glUniform1f(location, value);
        assert checkGLError() : glErrorMsg("glUniform1f(II)", location, value);

        this.restoreProgram();
    }

    @Override
    public void glProgramUniform2f(int programId, int location, float v0, float v1) {
        assert checkId(programId) : invalidProgramIdMsg(programId);
        assert checkOffset(location) : invalidUniformLocationMsg(location);
        assert checkFloat(v0) : invalidFloatMsg(v0);
        assert checkFloat(v1) : invalidFloatMsg(v1);

        this.saveProgram();
        GL20.glUseProgram(programId);
        assert checkGLError() : glErrorMsg("glUseProgram(I)", programId);

        GL20.glUniform2f(location, v0, v1);
        assert checkGLError() : glErrorMsg("glUniform2f(IFF)", location, v0, v1);

        this.restoreProgram();
    }

    @Override
    public void glProgramUniform3f(int programId, int location, float v0, float v1, float v2) {
        assert checkId(programId) : invalidProgramIdMsg(programId);
        assert checkOffset(location) : invalidUniformLocationMsg(location);
        assert checkFloat(v0) : invalidFloatMsg(v0);
        assert checkFloat(v1) : invalidFloatMsg(v1);
        assert checkFloat(v2) : invalidFloatMsg(v2);

        this.saveProgram();
        GL20.glUseProgram(programId);
        assert checkGLError() : glErrorMsg("glUseProgram(I)", programId);

        GL20.glUniform3f(location, v0, v1, v2);
        assert checkGLError() : glErrorMsg("glUniform3f(IFFF)", location, v0, v1, v2);

        this.restoreProgram();
    }

    @Override
    public void glProgramUniform4f(int programId, int location, float v0, float v1, float v2, float v3) {
        assert checkId(programId) : invalidProgramIdMsg(programId);
        assert checkOffset(location) : invalidUniformLocationMsg(location);
        assert checkFloat(v0) : invalidFloatMsg(v0);
        assert checkFloat(v1) : invalidFloatMsg(v1);
        assert checkFloat(v2) : invalidFloatMsg(v2);
        assert checkFloat(v3) : invalidFloatMsg(v3);

        this.saveProgram();
        GL20.glUseProgram(programId);
        assert checkGLError() : glErrorMsg("glUseProgram(I)", programId);

        GL20.glUniform4f(location, v0, v1, v2, v3);
        assert checkGLError() : glErrorMsg("glUniform4f(IFFFF)", location, v0, v1, v2, v3);

        this.restoreProgram();
    }

    @Override
    public void glProgramUniform1d(int programId, int location, double value) {
        assert checkId(programId) : invalidProgramIdMsg(programId);
        assert checkOffset(location) : invalidUniformLocationMsg(location);
        assert checkDouble(value) : invalidDoubleMsg(value);

        final ContextCapabilities cap = GL.getCapabilities();

        this.saveProgram();

        GL20.glUseProgram(programId);
        assert checkGLError() : glErrorMsg("glUseProgram(I)", programId);

        if (cap.OpenGL40) {
            GL40.glUniform1d(location, value);
            assert checkGLError() : glErrorMsg("glUniform1d(ID)", location, value);
        } else if (cap.GL_ARB_gpu_shader_fp64) {
            ARBGPUShaderFP64.glUniform1d(location, value);
            assert checkGLError() : glErrorMsg("glUniform1dARB(ID)", location, value);
        } else if (CAN_CAST_DOUBLE_TO_FLOAT) {
            GL20.glUniform1f(location, (float) value);
            assert checkGLError() : glErrorMsg("glUniform1f(IF)", location, value);
        } else {
            throw new UnsupportedOperationException("glUniform1d(location, double) is not supported!");
        }

        this.restoreProgram();
    }

    @Override
    public void glProgramUniform2d(int programId, int location, double v0, double v1) {
        assert checkId(programId) : invalidProgramIdMsg(programId);
        assert checkOffset(location) : invalidUniformLocationMsg(location);
        assert checkDouble(v0) : invalidDoubleMsg(v0);
        assert checkDouble(v1) : invalidDoubleMsg(v1);

        final ContextCapabilities cap = GL.getCapabilities();

        this.saveProgram();
        GL20.glUseProgram(programId);
        assert checkGLError() : glErrorMsg("glUseProgram(I)", programId);

        if (cap.OpenGL40) {
            GL40.glUniform2d(location, v0, v1);
            assert checkGLError() : glErrorMsg("glUniform2d(IDD)", location, v0, v1);
        } else if (cap.GL_ARB_gpu_shader_fp64) {
            ARBGPUShaderFP64.glUniform2d(location, v0, v1);
            assert checkGLError() : glErrorMsg("glUniform2dARB(IDD)", location, v0, v1);
        } else if (CAN_CAST_DOUBLE_TO_FLOAT) {
            GL20.glUniform2f(location, (float) v0, (float) v1);
            assert checkGLError() : glErrorMsg("glUniform2f(IFF)", location, v0, v1);
        } else {
            throw new UnsupportedOperationException("glUniform2d(location, double, double) is not supported!");
        }

        this.restoreProgram();
    }

    @Override
    public void glProgramUniform3d(int programId, int location, double v0, double v1, double v2) {
        assert checkId(programId) : invalidProgramIdMsg(programId);
        assert checkOffset(location) : invalidUniformLocationMsg(location);
        assert checkDouble(v0) : invalidDoubleMsg(v0);
        assert checkDouble(v1) : invalidDoubleMsg(v1);
        assert checkDouble(v2) : invalidDoubleMsg(v2);

        final ContextCapabilities cap = GL.getCapabilities();

        this.saveProgram();
        GL20.glUseProgram(programId);
        assert checkGLError() : glErrorMsg("glUseProgram(I)", programId);

        if (cap.OpenGL40) {
            GL40.glUniform3d(location, v0, v1, v2);
            assert checkGLError() : glErrorMsg("glUniform3d(IDDD)", location, v0, v1, v2);
        } else if (cap.GL_ARB_gpu_shader_fp64) {
            ARBGPUShaderFP64.glUniform3d(location, v0, v1, v2);
            assert checkGLError() : glErrorMsg("glUniform3dARB(IDDD)", location, v0, v1, v2);
        } else if (CAN_CAST_DOUBLE_TO_FLOAT) {
            GL20.glUniform3f(location, (float) v0, (float) v1, (float) v2);
            assert checkGLError() : glErrorMsg("glUniform3f(IFFF)", location, v0, v1, v2);
        } else {
            throw new UnsupportedOperationException("glUniform3d(location, double, double, double) is not supported!");
        }
        this.restoreProgram();
    }

    @Override
    public void glProgramUniform4d(int programId, int location, double v0, double v1, double v2, double v3) {
        assert checkId(programId) : invalidProgramIdMsg(programId);
        assert checkOffset(location) : invalidUniformLocationMsg(location);
        assert checkDouble(v0) : invalidDoubleMsg(v0);
        assert checkDouble(v1) : invalidDoubleMsg(v1);
        assert checkDouble(v2) : invalidDoubleMsg(v2);
        assert checkDouble(v3) : invalidDoubleMsg(v3);

        final ContextCapabilities cap = GL.getCapabilities();

        this.saveProgram();
        GL20.glUseProgram(programId);
        assert checkGLError() : glErrorMsg("glUseProgram(I)", programId);

        if (cap.OpenGL40) {
            GL40.glUniform4d(location, v0, v1, v2, v3);
            assert checkGLError() : glErrorMsg("glUniform4d(IDDDD)", location, v0, v1, v2, v3);
        } else if (cap.GL_ARB_gpu_shader_fp64) {
            ARBGPUShaderFP64.glUniform4d(location, v0, v1, v2, v3);
            assert checkGLError() : glErrorMsg("glUniform4dARB(IDDDD)", location, v0, v1, v2, v3);
        } else if (CAN_CAST_DOUBLE_TO_FLOAT) {
            GL20.glUniform4f(location, (float) v0, (float) v1, (float) v2, (float) v3);
            assert checkGLError() : glErrorMsg("glUniform4f(IFFFF)", location, v0, v1, v2, v3);
        } else {
            throw new UnsupportedOperationException("glUniform4f(location, double, double, double, double) is not supported!");
        }

        GL40.glUniform4d(location, v0, v1, v2, v3);
        assert checkGLError() : glErrorMsg("glUniform4d(IDDDD)", location, v0, v1, v2, v3);
        this.restoreProgram();
    }

    @Override
    public void glProgramUniformMatrix2f(int programId, int location, boolean needsTranspose, FloatBuffer data) {
        assert checkId(programId) : invalidProgramIdMsg(programId);
        assert checkOffset(location) : invalidUniformLocationMsg(location);
        assert data.isDirect() : NON_DIRECT_BUFFER_MSG;

        this.saveProgram();
        GL20.glUseProgram(programId);
        assert checkGLError() : glErrorMsg("glUseProgram(I)", programId);

        GL20.glUniformMatrix2fv(location, needsTranspose, data);
        assert checkGLError() : glErrorMsg("glUniformMatrix2fv(IB*)", location, needsTranspose, toHexString(memAddress(data)));

        this.restoreProgram();
    }

    @Override
    public void glProgramUniformMatrix3f(int programId, int location, boolean needsTranspose, FloatBuffer data) {
        assert checkId(programId) : invalidProgramIdMsg(programId);
        assert checkOffset(location) : invalidUniformLocationMsg(location);
        assert data.isDirect() : NON_DIRECT_BUFFER_MSG;

        this.saveProgram();
        GL20.glUseProgram(programId);
        assert checkGLError() : glErrorMsg("glUseProgram(I)", programId);

        GL20.glUniformMatrix3fv(location, needsTranspose, data);
        assert checkGLError() : glErrorMsg("glUniformMatrix3fv(IB*)", location, needsTranspose, toHexString(memAddress(data)));
        this.restoreProgram();
    }

    @Override
    public void glProgramUniformMatrix4f(int programId, int location, boolean needsTranspose, FloatBuffer data) {
        assert checkId(programId) : invalidProgramIdMsg(programId);
        assert checkOffset(location) : invalidUniformLocationMsg(location);
        assert data.isDirect() : NON_DIRECT_BUFFER_MSG;

        this.saveProgram();
        GL20.glUseProgram(programId);
        assert checkGLError() : glErrorMsg("glUseProgram(I)", programId);

        GL20.glUniformMatrix4fv(location, needsTranspose, data);
        assert checkGLError() : glErrorMsg("glUniformMatrix4fv(IB*)", location, needsTranspose, toHexString(memAddress(data)));
        this.restoreProgram();
    }

    @Override
    public void glProgramUniformMatrix2d(int programId, int location, boolean needsTranspose, DoubleBuffer data) {
        assert checkId(programId) : invalidProgramIdMsg(programId);
        assert checkOffset(location) : invalidUniformLocationMsg(location);
        assert data.isDirect() : NON_DIRECT_BUFFER_MSG;

        final ContextCapabilities cap = GL.getCapabilities();

        this.saveProgram();
        GL20.glUseProgram(programId);
        assert checkGLError() : glErrorMsg("glUseProgram(I)", programId);

        if (cap.OpenGL40) {
            GL40.glUniformMatrix2dv(location, needsTranspose, data);
            assert checkGLError() : glErrorMsg("glUniformMatrix2dv(IB*)", location, needsTranspose, toHexString(memAddress(data)));
        } else if (cap.GL_ARB_gpu_shader_fp64) {
            ARBGPUShaderFP64.glUniformMatrix2dv(location, needsTranspose, data);
            assert checkGLError() : glErrorMsg("glUniformMatrix2dvARB(IB*)", location, needsTranspose, toHexString(memAddress(data)));
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
            assert checkGLError() : glErrorMsg("glUniformMatrix2fv(IB*)", location, needsTranspose, toHexString(memAddress(casted)));
        } else {
            throw new UnsupportedOperationException("glUniformMatrix2dv(location, needsTranspose, data) is not supported!");
        }

        this.restoreProgram();
    }

    @Override
    public void glProgramUniformMatrix3d(int programId, int location, boolean needsTranspose, DoubleBuffer data) {
        assert checkId(programId) : invalidProgramIdMsg(programId);
        assert checkOffset(location) : invalidUniformLocationMsg(location);
        assert data.isDirect() : NON_DIRECT_BUFFER_MSG;

        final ContextCapabilities cap = GL.getCapabilities();

        this.saveProgram();
        GL20.glUseProgram(programId);
        assert checkGLError() : glErrorMsg("glUseProgram(I)", programId);

        if (cap.OpenGL40) {
            GL40.glUniformMatrix3dv(location, needsTranspose, data);
            assert checkGLError() : glErrorMsg("glUniformMatrix3dv(IB*)", location, needsTranspose, toHexString(memAddress(data)));
        } else if (cap.GL_ARB_gpu_shader_fp64) {
            ARBGPUShaderFP64.glUniformMatrix3dv(location, needsTranspose, data);
            assert checkGLError() : glErrorMsg("glUniformMatrix3dvARB(IB*)", location, needsTranspose, toHexString(memAddress(data)));
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
            assert checkGLError() : glErrorMsg("glUniformMatrix3fv(IB*)", location, needsTranspose, toHexString(memAddress(casted)));
        } else {
            throw new UnsupportedOperationException("glUniformMatrix3dv(location, needsTranspose, data) is not supported!");
        }

        this.restoreProgram();
    }

    @Override
    public void glProgramUniformMatrix4d(int programId, int location, boolean needsTranspose, DoubleBuffer data) {
        assert checkId(programId) : invalidProgramIdMsg(programId);
        assert checkOffset(location) : invalidUniformLocationMsg(location);
        assert data.isDirect() : NON_DIRECT_BUFFER_MSG;

        final ContextCapabilities cap = GL.getCapabilities();

        this.saveProgram();
        GL20.glUseProgram(programId);
        assert checkGLError() : glErrorMsg("glUseProgram(I)", programId);

        if (cap.OpenGL40) {
            GL40.glUniformMatrix4dv(location, needsTranspose, data);
            assert checkGLError() : glErrorMsg("glUniformMatrix4dv(IB*)", location, needsTranspose, toHexString(memAddress(data)));
        } else if (cap.GL_ARB_gpu_shader_fp64) {
            ARBGPUShaderFP64.glUniformMatrix4dv(location, needsTranspose, data);
            assert checkGLError() : glErrorMsg("glUniformMatrix4dvARB(IB*)", location, needsTranspose, toHexString(memAddress(data)));
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
            assert checkGLError() : glErrorMsg("glUniformMatrix4fv(IB*)", location, needsTranspose, toHexString(memAddress(casted)));
        } else {
            throw new UnsupportedOperationException("glUniformMatrix4dv(location, needsTranspose, data) is not supported!");
        }

        this.restoreProgram();
    }

    @Override
    public void glTextureParameteri(int textureId, int target, int pName, int val) {
        assert checkId(textureId) : invalidTextureIdMsg(textureId);
        assert checkGLenum(target, GLTextureTarget::of) : invalidGLenumMsg(target);

        this.saveTexture(target);
        GL11.glBindTexture(target, textureId);
        assert checkGLError() : glErrorMsg("glBindTexture(II)", GLTextureTarget.of(target).get(), textureId);

        GL11.glTexParameteri(target, pName, val);
        assert checkGLError() : glErrorMsg("glTexParameteri(III)", GLTextureTarget.of(target).get(), pName, val);

        this.restoreTexture(target);
    }

    @Override
    public void glTextureParameterf(int textureId, int target, int pName, float val) {
        assert checkId(textureId) : invalidTextureIdMsg(textureId);
        assert checkGLenum(target, GLTextureTarget::of) : invalidGLenumMsg(target);
        assert checkFloat(val) : invalidFloatMsg(val);

        this.saveTexture(target);
        GL11.glBindTexture(target, textureId);
        assert checkGLError() : glErrorMsg("glBindTexture(II)", GLTextureTarget.of(target).get(), textureId);

        GL11.glTexParameterf(target, pName, val);
        assert checkGLError() : glErrorMsg("glTexParameterf(IIF)", GLTextureTarget.of(target).get(), pName, val);

        this.restoreTexture(target);
    }

    @Override
    public void glGenerateTextureMipmap(int textureId, int target) {
        assert checkId(textureId) : invalidTextureIdMsg(textureId);
        assert checkGLenum(target, GLTextureTarget::of) : invalidGLenumMsg(target);

        final ContextCapabilities cap = GL.getCapabilities();

        this.saveTexture(target);

        GL11.glBindTexture(target, textureId);
        assert checkGLError() : glErrorMsg("glBindTexture(II)", GLTextureTarget.of(target).get(), textureId);

        if (cap.OpenGL30) {
            GL30.glGenerateMipmap(target);
            assert checkGLError() : glErrorMsg("glGenerateMipmap(I)", GLTextureTarget.of(target).get());
        } else if (cap.GL_ARB_framebuffer_object) {
            ARBFramebufferObject.glGenerateMipmap(target);
            assert checkGLError() : glErrorMsg("glGenerateMipmapARB(I)", GLTextureTarget.of(target).get());
        } else if (cap.GL_EXT_framebuffer_object) {
            EXTFramebufferObject.glGenerateMipmapEXT(target);
            assert checkGLError() : glErrorMsg("glGenerateMipmapEXT(I)", GLTextureTarget.of(target).get());
        } else {
            throw new UnsupportedOperationException("glGenerateMipmap(target) is not supported!");
        }

        this.restoreTexture(target);
    }

    @Override
    public void glBindTextureUnit(int unit, int target, int textureId) {
        assert checkNullableId(unit) : invalidTextureUnitMsg(unit);
        assert checkGLenum(target, GLTextureTarget::of) : invalidGLenumMsg(target);

        GL13.glActiveTexture(unit);
        assert checkGLError() : glErrorMsg("glActiveTexture(I)", unit);

        GL11.glBindTexture(target, textureId);
        assert checkGLError() : glErrorMsg("glBindTexture(II)", GLTextureTarget.of(target).get(), textureId);
    }

    public static DSADriver getInstance() {
        return Holder.INSTANCE;
    }

    @Override
    public void glProgramUniform1i(int programId, int location, int value) {
        assert checkId(programId) : invalidProgramIdMsg(programId);
        assert checkOffset(location) : invalidUniformLocationMsg(location);

        this.saveProgram();
        GL20.glUseProgram(programId);
        assert checkGLError() : glErrorMsg("glUseProgram(I)", programId);

        GL20.glUniform1i(location, value);
        assert checkGLError() : glErrorMsg("glUniform1i(II)", location, value);

        this.restoreProgram();
    }

    @Override
    public void glProgramUniform2i(int programId, int location, int v0, int v1) {
        assert checkId(programId) : invalidProgramIdMsg(programId);
        assert checkOffset(location) : invalidUniformLocationMsg(location);

        this.saveProgram();
        GL20.glUseProgram(programId);
        assert checkGLError() : glErrorMsg("glUseProgram(I)", programId);

        GL20.glUniform2i(location, v0, v1);
        assert checkGLError() : glErrorMsg("glUniform2i(III)", location, v0, v1);
        this.restoreProgram();
    }

    @Override
    public void glProgramUniform3i(int programId, int location, int v0, int v1, int v2) {
        assert checkId(programId) : invalidProgramIdMsg(programId);
        assert checkOffset(location) : invalidUniformLocationMsg(location);

        this.saveProgram();
        GL20.glUseProgram(programId);
        assert checkGLError() : glErrorMsg("glUseProgram(I)", programId);

        GL20.glUniform3i(location, v0, v1, v2);
        assert checkGLError() : glErrorMsg("glUniform3i(IIII)", location, v0, v1, v2);
        this.restoreProgram();
    }

    @Override
    public void glProgramUniform4i(int programId, int location, int v0, int v1, int v2, int v3) {
        assert checkId(programId) : invalidProgramIdMsg(programId);
        assert checkOffset(location) : invalidUniformLocationMsg(location);

        this.saveProgram();
        GL20.glUseProgram(programId);
        assert checkGLError() : glErrorMsg("glUseProgram(I)", programId);

        GL20.glUniform4i(location, v0, v1, v2, v3);
        assert checkGLError() : glErrorMsg("glUniform4i(IIIII)", location, v0, v1, v2, v3);
        this.restoreProgram();
    }

    @Override
    public void glTextureSubImage1d(int textureId, int level, int xOffset, int width, int format, int type, ByteBuffer pixels) {
        assert checkId(textureId) : invalidTextureIdMsg(textureId);
        assert checkMipmapLevel(level) : invalidMipmapLevelMsg(level);
        assert checkOffset(xOffset) : invalidXOffsetMsg(xOffset);
        assert checkDimension(width) : invalidWidthMsg(width);
        assert checkGLenum(format, GLTextureFormat::of) : invalidGLenumMsg(format);
        assert checkGLenum(type, GLType::of) : invalidGLenumMsg(type);
        assert checkBufferIsNative(pixels) : bufferIsNotNativeMsg(pixels);
        assert pixels.isDirect() : NON_DIRECT_BUFFER_MSG;
        assert checkBufferSize(width, 1, 1, format, type, pixels) : bufferTooSmallMsg(width, 1, 1, format, type, pixels);

        this.saveTexture1d();

        GL11.glBindTexture(GL11.GL_TEXTURE_1D, textureId);
        assert checkGLError() : glErrorMsg("glBindTexture(II)", "GL_TEXTURE_1D", textureId);

        GL11.glTexSubImage1D(GL11.GL_TEXTURE_1D, level, xOffset, width, format, type, pixels);
        assert checkGLError() : glErrorMsg("glTexSubImage1D(IIIIII*)", "GL_TEXTURE_1D", level, xOffset, width, GLTextureFormat.of(format).get(), GLType.of(type).get(), toHexString(memAddress(pixels)));

        this.restoreTexture1d();
    }

    @Override
    public void glTextureSubImage2d(int textureId, int level, int xOffset, int yOffset, int width, int height, int format, int type, ByteBuffer pixels) {
        assert checkId(textureId) : invalidTextureIdMsg(textureId);
        assert checkMipmapLevel(level) : invalidMipmapLevelMsg(level);
        assert checkOffset(xOffset) : invalidXOffsetMsg(xOffset);
        assert checkOffset(yOffset) : invalidYOffsetMsg(yOffset);
        assert checkDimension(width) : invalidWidthMsg(width);
        assert checkDimension(height) : invalidHeightMsg(height);
        assert checkGLenum(format, GLTextureFormat::of) : invalidGLenumMsg(format);
        assert checkGLenum(type, GLType::of) : invalidGLenumMsg(type);
        assert checkBufferIsNative(pixels) : bufferIsNotNativeMsg(pixels);
        assert pixels.isDirect() : NON_DIRECT_BUFFER_MSG;
        assert checkBufferSize(width, height, 1, format, type, pixels) : bufferTooSmallMsg(width, height, 1, format, type, pixels);

        this.saveTexture2d();

        GL11.glBindTexture(GL11.GL_TEXTURE_2D, textureId);
        assert checkGLError() : glErrorMsg("glBindTexture(II)", "GL_TEXTURE_2D", textureId);

        GL11.glTexSubImage2D(GL11.GL_TEXTURE_2D, level, xOffset, yOffset, width, height, format, type, pixels);
        assert checkGLError() : glErrorMsg("glTexSubImage2D(IIIIIIII*)", "GL_TEXTURE_2D", level, xOffset, yOffset, width, height, GLTextureFormat.of(format).get(), GLType.of(type).get(), toHexString(memAddress(pixels)));

        this.restoreTexture2d();
    }

    @Override
    public void glTextureSubImage3d(int textureId, int level, int xOffset, int yOffset, int zOffset, int width, int height, int depth, int format, int type, ByteBuffer pixels) {
        assert checkId(textureId) : invalidTextureIdMsg(textureId);
        assert checkMipmapLevel(level) : invalidMipmapLevelMsg(level);
        assert checkOffset(xOffset) : invalidXOffsetMsg(xOffset);
        assert checkOffset(yOffset) : invalidYOffsetMsg(yOffset);
        assert checkOffset(zOffset) : invalidZOffsetMsg(zOffset);
        assert checkDimension(width) : invalidWidthMsg(width);
        assert checkDimension(height) : invalidHeightMsg(height);
        assert checkDimension(depth) : invalidDepthMsg(depth);
        assert checkGLenum(format, GLTextureFormat::of) : invalidGLenumMsg(format);
        assert checkGLenum(type, GLType::of) : invalidGLenumMsg(type);
        assert checkBufferIsNative(pixels) : bufferIsNotNativeMsg(pixels);
        assert pixels.isDirect() : NON_DIRECT_BUFFER_MSG;
        assert checkBufferSize(width, height, depth, format, type, pixels) : bufferTooSmallMsg(width, height, depth, format, type, pixels);

        this.saveTexture3d();
        GL11.glBindTexture(GL12.GL_TEXTURE_3D, textureId);
        assert checkGLError() : glErrorMsg("glBindTexture(II)", "GL_TEXTURE_3D", textureId);

        GL12.glTexSubImage3D(GL12.GL_TEXTURE_3D, level, xOffset, yOffset, zOffset, width, height, depth, format, type, pixels);
        assert checkGLError() : glErrorMsg("glTexSubImage3D(IIIIIIIIII*)", "GL_TEXTURE_3D", level, xOffset, yOffset, zOffset, width, height, depth, GLTextureFormat.of(format).get(), GLType.of(type).get(), toHexString(memAddress(pixels)));

        this.restoreTexture3d();
    }

    @Override
    public void glTextureImage1d(int texture, int target, int level, int internalFormat, int width, int border, int format, int type, ByteBuffer pixels) {
        assert checkId(texture) : invalidTextureIdMsg(texture);
        assert checkGLenum(target, GLTextureTarget::of) : invalidGLenumMsg(target);
        assert checkMipmapLevel(level) : invalidMipmapLevelMsg(level);
        assert checkGLenum(internalFormat, GLTextureInternalFormat::of) : invalidGLenumMsg(internalFormat);
        assert checkDimension(width) : invalidWidthMsg(width);
        assert border == 0 : "Border must be 0.";
        assert checkGLenum(format, GLTextureFormat::of) : invalidGLenumMsg(format);
        assert checkGLenum(type, GLType::of) : invalidGLenumMsg(type);
        assert checkBufferIsNative(pixels) : bufferIsNotNativeMsg(pixels);
        assert pixels.isDirect() : NON_DIRECT_BUFFER_MSG;
        assert checkBufferSize(width, 1, 1, format, type, pixels) : bufferTooSmallMsg(width, 1, 1, format, type, pixels);

        this.saveTexture1d();
        GL11.glBindTexture(target, texture);
        assert checkGLError() : glErrorMsg("glBindTexture(II)", GLTextureTarget.of(target).get(), texture);

        GL11.glTexImage1D(target, level, internalFormat, width, border, format, type, pixels);
        assert checkGLError() : glErrorMsg("glTexImage1D(IIIIIII*)", GLTextureTarget.of(target).get(), level, GLTextureInternalFormat.of(internalFormat).get(), width, border, GLTextureFormat.of(format).get(), GLType.of(type).get(), toHexString(memAddress(pixels)));

        this.restoreTexture1d();
    }

    @Override
    public void glTextureImage1d(int texture, int target, int level, int internalFormat, int width, int border, int format, int type, long ptr) {
        assert checkId(texture) : invalidTextureIdMsg(texture);
        assert checkGLenum(target, GLTextureTarget::of) : invalidGLenumMsg(target);
        assert checkMipmapLevel(level) : invalidMipmapLevelMsg(level);
        assert checkGLenum(internalFormat, GLTextureInternalFormat::of) : invalidGLenumMsg(internalFormat);
        assert checkDimension(width) : invalidWidthMsg(width);
        assert border == 0 : "Border must be 0.";
        assert checkGLenum(type, GLType::of) : invalidGLenumMsg(type);

        this.saveTexture1d();

        GL11.glBindTexture(target, texture);
        assert checkGLError() : glErrorMsg("glBindTexture(II)", GLTextureTarget.of(target).get(), texture);

        GL11.glTexImage1D(target, level, internalFormat, width, border, format, type, ptr);
        assert checkGLError() : glErrorMsg("glTexImage1D(IIIIIIIL)", GLTextureTarget.of(target).get(), level, GLTextureInternalFormat.of(internalFormat).get(), width, border, GLTextureFormat.of(format).get(), GLType.of(type).get(), ptr);

        this.restoreTexture1d();
    }

    @Override
    public void glTextureImage2d(int texture, int target, int level, int internalFormat, int width, int height, int border, int format, int type, ByteBuffer pixels) {
        assert checkId(texture) : invalidTextureIdMsg(texture);
        assert checkGLenum(target, GLTextureTarget::of) : invalidGLenumMsg(target);
        assert checkMipmapLevel(level) : invalidMipmapLevelMsg(level);
        assert checkGLenum(internalFormat, GLTextureInternalFormat::of);
        assert checkDimension(width) : invalidWidthMsg(width);
        assert checkDimension(height) : invalidHeightMsg(height);
        assert border == 0 : "Border must be 0.";
        assert checkGLenum(format, GLTextureFormat::of) : invalidGLenumMsg(format);
        assert checkGLenum(type, GLType::of) : invalidGLenumMsg(type);
        assert checkBufferIsNative(pixels) : bufferIsNotNativeMsg(pixels);
        assert pixels.isDirect() : NON_DIRECT_BUFFER_MSG;
        assert checkBufferSize(width, height, 1, format, type, pixels) : bufferTooSmallMsg(width, height, 1, format, type, pixels);

        this.saveTexture2d();
        GL11.glBindTexture(target, texture);
        assert checkGLError() : glErrorMsg("glBindTexture(II)", GLTextureTarget.of(target).get(), texture);

        GL11.glTexImage2D(target, level, internalFormat, width, height, border, format, type, pixels);
        assert checkGLError() : glErrorMsg("glTexImage2D(IIIIIIII*)", GLTextureTarget.of(target).get(), level, GLTextureInternalFormat.of(internalFormat).get(), width, height, border, GLTextureFormat.of(format).get(), GLType.of(type).get(), toHexString(memAddress(pixels)));

        this.restoreTexture2d();
    }

    @Override
    public void glTextureImage2d(int texture, int target, int level, int internalFormat, int width, int height, int border, int format, int type, long ptr) {
        assert checkId(texture) : invalidTextureIdMsg(texture);
        assert checkGLenum(target, GLTextureTarget::of) : invalidGLenumMsg(target);
        assert checkMipmapLevel(level) : invalidMipmapLevelMsg(level);
        assert checkGLenum(internalFormat, GLTextureInternalFormat::of) : invalidGLenumMsg(internalFormat);
        assert checkDimension(width) : invalidWidthMsg(width);
        assert checkDimension(height) : invalidHeightMsg(height);
        assert border == 0 : "Border must be 0.";
        assert checkGLenum(format, GLTextureFormat::of) : invalidGLenumMsg(format);
        assert checkGLenum(type, GLType::of) : invalidGLenumMsg(type);

        this.saveTexture2d();
        GL11.glBindTexture(target, texture);
        assert checkGLError() : glErrorMsg("glBindTexture(II)", GLTextureTarget.of(target).get(), texture);

        GL11.glTexImage2D(target, level, internalFormat, width, height, border, format, type, ptr);
        assert checkGLError() : glErrorMsg("glTexImage2D(IIIIIIIIL)", GLTextureTarget.of(target).get(), level, GLTextureInternalFormat.of(internalFormat).get(), width, height, border, GLTextureFormat.of(format).get(), GLType.of(type).get(), ptr);

        this.restoreTexture2d();
    }

    @Override
    public void glTextureImage3d(int texture, int target, int level, int internalFormat, int width, int height, int depth, int border, int format, int type, ByteBuffer pixels) {
        assert checkId(texture) : invalidTextureIdMsg(texture);
        assert checkGLenum(target, GLTextureTarget::of) : invalidGLenumMsg(target);
        assert checkMipmapLevel(level) : invalidMipmapLevelMsg(level);
        assert checkGLenum(internalFormat, GLTextureInternalFormat::of) : invalidGLenumMsg(internalFormat);
        assert checkDimension(width) : invalidWidthMsg(width);
        assert checkDimension(height) : invalidHeightMsg(height);
        assert checkDimension(depth) : invalidDepthMsg(depth);
        assert border == 0 : "Border must be 0.";
        assert checkGLenum(format, GLTextureFormat::of) : invalidGLenumMsg(format);
        assert checkGLenum(type, GLType::of) : invalidGLenumMsg(type);
        assert checkBufferIsNative(pixels) : bufferIsNotNativeMsg(pixels);
        assert pixels.isDirect() : NON_DIRECT_BUFFER_MSG;
        assert checkBufferSize(width, height, depth, format, type, pixels) : bufferTooSmallMsg(width, height, depth, format, type, pixels);

        this.saveTexture3d();
        GL11.glBindTexture(target, texture);
        assert checkGLError() : glErrorMsg("glBindTexture(II)", GLTextureTarget.of(target).get(), texture);

        GL12.glTexImage3D(target, level, internalFormat, width, height, depth, border, format, type, pixels);
        assert checkGLError() : glErrorMsg("glTexImage3D(IIIIIIIII*)", GLTextureTarget.of(target).get(), level, GLTextureInternalFormat.of(internalFormat).get(), width, height, depth, border, GLTextureFormat.of(format).get(), GLType.of(type).get(), toHexString(memAddress(pixels)));

        this.restoreTexture3d();
    }

    @Override
    public void glTextureImage3d(int texture, int target, int level, int internalFormat, int width, int height, int depth, int border, int format, int type, long ptr) {
        assert checkId(texture) : invalidTextureIdMsg(texture);
        assert checkGLenum(target, GLTextureTarget::of) : invalidGLenumMsg(target);
        assert checkMipmapLevel(level) : invalidMipmapLevelMsg(level);
        assert checkGLenum(target, GLTextureInternalFormat::of) : invalidGLenumMsg(internalFormat);
        assert checkDimension(width) : invalidWidthMsg(width);
        assert checkDimension(height) : invalidHeightMsg(height);
        assert checkDimension(depth) : invalidDepthMsg(depth);
        assert border == 0 : "Border must be 0.";
        assert checkGLenum(format, GLTextureFormat::of) : invalidGLenumMsg(format);
        assert checkGLenum(type, GLType::of) : invalidGLenumMsg(type);

        this.saveTexture3d();
        GL11.glBindTexture(target, texture);
        assert checkGLError() : glErrorMsg("glBindTexture(II)", GLTextureTarget.of(target).get(), texture);

        GL12.glTexImage3D(target, level, internalFormat, width, height, depth, border, format, type, ptr);
        assert checkGLError() : glErrorMsg("glTexImage3D(IIIIIIIIIL)", GLTextureTarget.of(target).get(), level, GLTextureInternalFormat.of(internalFormat).get(), width, height, depth, border, GLTextureFormat.of(format).get(), GLType.of(type).get(), ptr);

        this.restoreTexture3d();
    }

    @Override
    public void glNamedFramebufferTexture1D(int framebuffer, int attachment, int texTarget, int texture, int level) {
        assert checkId(framebuffer) : invalidFramebufferIdMsg(framebuffer);
        assert checkGLenum(texTarget, GLTextureTarget::of) : invalidGLenumMsg(texTarget);
        assert checkId(texture) : invalidTextureIdMsg(texture);
        assert checkMipmapLevel(level) : invalidMipmapLevelMsg(level);

        final ContextCapabilities cap = GL.getCapabilities();

        if (cap.OpenGL30) {
            this.saveFramebuffer();
            GL30.glBindFramebuffer(GL30.GL_FRAMEBUFFER, framebuffer);
            assert checkGLError() : glErrorMsg("glBindFramebuffer(II)", "GL_FRAMEBUFFER", framebuffer);

            GL30.glFramebufferTexture1D(GL30.GL_FRAMEBUFFER, attachment, texTarget, texture, level);
            assert checkGLError() : glErrorMsg("glFramebufferTexture1D(IIIII)", "GL_FRAMEBUFFER", attachment, GLTextureTarget.of(texTarget).get(), texture, level);

            this.restoreFramebuffer();
        } else if (cap.GL_ARB_framebuffer_object) {
            this.saveFramebuffer();
            ARBFramebufferObject.glBindFramebuffer(GL30.GL_FRAMEBUFFER, framebuffer);
            assert checkGLError() : glErrorMsg("glBindFramebufferARB(II)", "GL_FRAMEBUFFER", framebuffer);

            ARBFramebufferObject.glFramebufferTexture1D(GL30.GL_FRAMEBUFFER, attachment, texTarget, texture, level);
            assert checkGLError() : glErrorMsg("glFramebufferTexture1DARB(IIIII)", "GL_FRAMEBUFFER", attachment, GLTextureTarget.of(texTarget), texture, level);

            this.restoreFramebuffer();
        } else if (cap.GL_EXT_framebuffer_object) {
            this.saveFramebuffer();
            EXTFramebufferObject.glBindFramebufferEXT(GL30.GL_FRAMEBUFFER, framebuffer);
            assert checkGLError() : glErrorMsg("glBindFramebufferEXT(II)", "GL_FRAMEBUFFER", framebuffer);

            EXTFramebufferObject.glFramebufferTexture1DEXT(GL30.GL_FRAMEBUFFER, attachment, texTarget, texture, level);
            assert checkGLError() : glErrorMsg("glFramebufferTExture1DEXT(IIIII)", "GL_FRAMEBUFFER", attachment, GLTextureTarget.of(texTarget).get(), texture, level);
            this.restoreFramebuffer();
        } else {
            throw new UnsupportedOperationException("glFramebufferTexture1D is not supported. glFramebufferTexture1D requires either: an OpenGL 3.0 context, ARB_framebuffer_object, or EXT_framebuffer_object.");
        }

        this.restoreFramebuffer();
    }

    @Override
    public void glNamedFramebufferTexture2D(int framebuffer, int attachment, int texTarget, int texture, int level) {
        assert checkId(framebuffer) : invalidFramebufferIdMsg(framebuffer);
        assert checkGLenum(texTarget, GLTextureTarget::of) : invalidGLenumMsg(texTarget);
        assert checkId(texture) : invalidTextureIdMsg(texture);
        assert checkMipmapLevel(level) : invalidMipmapLevelMsg(level);

        final ContextCapabilities cap = GL.getCapabilities();

        if (cap.OpenGL30) {
            this.saveFramebuffer();
            GL30.glBindFramebuffer(GL30.GL_FRAMEBUFFER, framebuffer);
            assert checkGLError() : glErrorMsg("glBindFramebuffer(II)", "GL_FRAMEBUFFER", framebuffer);

            GL30.glFramebufferTexture2D(GL30.GL_FRAMEBUFFER, attachment, texTarget, texture, level);
            assert checkGLError() : glErrorMsg("glFramebufferTexture2D(IIIII)", "GL_FRAMEBUFFER", attachment, GLTextureTarget.of(texTarget).get(), texture, level);
            this.restoreFramebuffer();
        } else if (cap.GL_ARB_framebuffer_object) {
            this.saveFramebuffer();
            ARBFramebufferObject.glBindFramebuffer(GL30.GL_FRAMEBUFFER, framebuffer);
            assert checkGLError() : glErrorMsg("glBindFramebufferARB(II)", "GL_FRAMEBUFFER", framebuffer);

            ARBFramebufferObject.glFramebufferTexture2D(GL30.GL_FRAMEBUFFER, attachment, texTarget, texture, level);
            assert checkGLError() : glErrorMsg("glBindFramebufferTexture2DABR(IIIII)", "GL_FRAMEBUFFER", attachment, GLTextureTarget.of(texTarget).get(), texture, level);
            this.restoreFramebuffer();
        } else if (cap.GL_EXT_framebuffer_object) {
            this.saveFramebuffer();
            EXTFramebufferObject.glBindFramebufferEXT(GL30.GL_FRAMEBUFFER, framebuffer);
            assert checkGLError() : glErrorMsg("glBindFramebufferEXT(II)", "GL_FRAMEBUFFER", framebuffer);

            EXTFramebufferObject.glFramebufferTexture2DEXT(GL30.GL_FRAMEBUFFER, attachment, texTarget, texture, level);
            assert checkGLError() : glErrorMsg("glFramebufferTexture2DEXT(IIIII)", "GL_FRAMEBUFFER", attachment, GLTextureTarget.of(texTarget).get(), texture, level);
            this.restoreFramebuffer();
        } else {
            throw new UnsupportedOperationException("glFramebufferTexture2D is not supported. glFramebufferTexture2D requires either: an OpenGL 3.0 context, ARB_framebuffer_object, or EXT_framebuffer_object.");
        }
    }

    @Override
    public void glBlitNamedFramebuffer(int readFramebuffer, int drawFramebuffer, int srcX0, int srcY0, int srcX1, int srcY1, int dstX0, int dstY0, int dstX1, int dstY1, int mask, int filter) {
        assert checkNullableId(readFramebuffer) : invalidFramebufferIdMsg(readFramebuffer);
        assert checkNullableId(drawFramebuffer) : invalidFramebufferIdMsg(drawFramebuffer);

        final ContextCapabilities cap = GL.getCapabilities();

        if (cap.OpenGL30) {
            final int prevReadFB = GL11.glGetInteger(GL30.GL_READ_FRAMEBUFFER_BINDING);
            assert checkGLError() : glErrorMsg("glGetInteger(I)", "GL_READ_FRAMEBUFFER_BINDING");

            final int prevDrawFB = GL11.glGetInteger(GL30.GL_DRAW_FRAMEBUFFER_BINDING);
            assert checkGLError() : glErrorMsg("glGetInteger(I)", "GL_DRAW_FRAMEBUFFER_BINDING");

            GL30.glBindFramebuffer(GL30.GL_READ_FRAMEBUFFER, readFramebuffer);
            assert checkGLError() : glErrorMsg("glBindFramebuffer(II)", "GL_READ_FRAMEBUFFER", readFramebuffer);

            GL30.glBindFramebuffer(GL30.GL_DRAW_FRAMEBUFFER, drawFramebuffer);
            assert checkGLError() : glErrorMsg("glBindFramebuffer(II)", "GL_DRAW_FRAMEBUFFER", drawFramebuffer);

            GL30.glBlitFramebuffer(srcX0, srcY0, srcX1, srcY1, dstX0, dstY0, dstX1, dstY1, mask, filter);
            assert checkGLError() : glErrorMsg("glBlitFramebuffer(IIIIIIIIII)", srcX0, srcY0, srcX1, srcY1, dstX0, dstY0, dstX1, dstY1, mask, filter);

            GL30.glBindFramebuffer(GL30.GL_DRAW_FRAMEBUFFER, prevDrawFB);
            assert checkGLError() : glErrorMsg("glBindFramebuffer(II)", "GL_DRAW_FRAMEBUFFER", prevDrawFB);

            GL30.glBindFramebuffer(GL30.GL_READ_FRAMEBUFFER, prevReadFB);
            assert checkGLError() : glErrorMsg("glBindFramebuffer(II)", "GL_READ_FRAMEBUFFER", prevReadFB);
        } else if (cap.GL_ARB_framebuffer_object) {
            final int prevReadFB = GL11.glGetInteger(ARBFramebufferObject.GL_READ_FRAMEBUFFER_BINDING);
            assert checkGLError() : glErrorMsg("glGetInteger(I)", "GL_READ_FRAMEBUFFER_BINDING");

            final int prevDrawFB = GL11.glGetInteger(ARBFramebufferObject.GL_DRAW_FRAMEBUFFER_BINDING);
            assert checkGLError() : glErrorMsg("glGetInteger(I)", "GL_DRAW_FRAMEBUFFER_BINDING");

            ARBFramebufferObject.glBindFramebuffer(ARBFramebufferObject.GL_DRAW_FRAMEBUFFER, drawFramebuffer);
            assert checkGLError() : glErrorMsg("glBindFramebufferARB(II)", "GL_DRAW_FRAMEBUFFER", drawFramebuffer);

            ARBFramebufferObject.glBindFramebuffer(ARBFramebufferObject.GL_READ_FRAMEBUFFER, readFramebuffer);
            assert checkGLError() : glErrorMsg("glBindFramebufferARB(II)", "GL_READ_FRAMEBUFFER", readFramebuffer);

            ARBFramebufferObject.glBlitFramebuffer(srcX0, srcY0, srcX1, srcY1, dstX0, dstY0, dstX1, dstY1, mask, filter);
            assert checkGLError() : glErrorMsg("glBlitFramebufferARB(IIIIIIIIII)", srcX0, srcY0, srcX1, srcY1, dstX0, dstY0, dstX1, dstY1, mask, filter);

            ARBFramebufferObject.glBindFramebuffer(ARBFramebufferObject.GL_DRAW_FRAMEBUFFER, prevDrawFB);
            assert checkGLError() : glErrorMsg("glBindFramebuffer(II)", "GL_DRAW_FRAMEBUFFER", prevDrawFB);

            ARBFramebufferObject.glBindFramebuffer(ARBFramebufferObject.GL_READ_FRAMEBUFFER, prevReadFB);
            assert checkGLError() : glErrorMsg("glBindFramebuffer(II)", "GL_READ_FRAMEBUFFER", prevReadFB);
        } else {
            throw new UnsupportedOperationException("glBlitFramebuffer requires either an OpenGL3.0 context or ARB_framebuffer_object!");
        }
    }

    @Override
    public void glNamedBufferReadPixels(int bufferId, int x, int y, int width, int height, int format, int type, long ptr) {
        assert checkId(bufferId) : invalidBufferIdMsg(bufferId);
        assert checkGLenum(format, GLTextureFormat::of) : invalidGLenumMsg(format);
        assert checkGLenum(type, GLType::of) : invalidGLenumMsg(type);

        final int prevPixBuffer = GL11.glGetInteger(GL21.GL_PIXEL_PACK_BUFFER_BINDING);
        assert checkGLError() : glErrorMsg("glGetInteger(I)", "GL_PIXEL_PACK_BUFFER_BINDING");

        GL15.glBindBuffer(GL21.GL_PIXEL_PACK_BUFFER, bufferId);
        assert checkGLError() : glErrorMsg("glBindBuffer(II)", "GL_PIXEL_PACK_BUFFER", bufferId);

        GL11.glReadPixels(x, y, width, height, format, type, ptr);
        assert checkGLError() : glErrorMsg("glReadPixels(IIIIIIL)", x, y, width, height, GLTextureFormat.of(format).get(), GLType.of(type).get(), ptr);

        GL15.glBindBuffer(GL21.GL_PIXEL_PACK_BUFFER, prevPixBuffer);
        assert checkGLError() : glErrorMsg("glBindBuffer(II)", "GL_PIXEL_PACK_BUFFER", prevPixBuffer);
    }

    @Override
    public void glGetTextureImage(int texture, int target, int level, int format, int type, int bufferSize, ByteBuffer pixels) {
        assert checkId(texture) : invalidTextureIdMsg(texture);
        assert checkGLenum(target, GLTextureTarget::of) : invalidGLenumMsg(target);
        assert checkMipmapLevel(level) : invalidMipmapLevelMsg(level);
        assert checkGLenum(format, GLTextureFormat::of) : invalidGLenumMsg(format);
        assert checkGLenum(type, GLType::of) : invalidGLenumMsg(type);
        assert checkSize(bufferSize) : invalidSizeMsg(bufferSize);
        assert checkBufferIsNative(pixels) : bufferIsNotNativeMsg(pixels);
        assert pixels.isDirect() : NON_DIRECT_BUFFER_MSG;
        assert checkBufferSize(bufferSize, 1, 1, format, type, pixels) : bufferTooSmallMsg(bufferSize, 1, 1, format, type, pixels);

        this.saveTexture(target);

        GL11.glBindTexture(target, texture);
        assert checkGLError() : glErrorMsg("glBindTexture(II)", GLTextureTarget.of(target).get(), texture);

        GL11.glGetTexImage(target, level, format, type, pixels);
        assert checkGLError() : glErrorMsg("glGetTexImage(IIIII)", GLTextureTarget.of(target).get(), level, GLTextureFormat.of(format).get(), GLType.of(type).get(), toHexString(memAddress(pixels)));

        this.restoreTexture(target);
    }

    private static class Holder {

        private static final DSADriver INSTANCE = new FakeDSA();
    }
}
