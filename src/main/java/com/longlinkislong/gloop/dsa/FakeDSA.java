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
import java.nio.ByteBuffer;
import java.nio.DoubleBuffer;
import java.nio.FloatBuffer;
import org.lwjgl.opengl.ARBFramebufferObject;
import org.lwjgl.opengl.ContextCapabilities;
import org.lwjgl.opengl.EXTFramebufferObject;
import org.lwjgl.opengl.GL;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL12;
import org.lwjgl.opengl.GL15;
import org.lwjgl.opengl.GL20;
import org.lwjgl.opengl.GL21;
import org.lwjgl.opengl.GL30;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.Marker;
import org.slf4j.MarkerFactory;

/**
 *
 * @author zmichaels
 */
public final class FakeDSA extends Common implements EXTDSADriver {

    private static final Marker GL_MARKER = MarkerFactory.getMarker("OPENGL");
    private static final Logger LOGGER = LoggerFactory.getLogger("OPENGL");    

    private int saveFramebuffer = 0;

    private void saveFramebuffer() {
        LOGGER.trace(GL_MARKER, "glGetInteger(GL_FRAMEBUFFER_BINDING)");
        this.saveFramebuffer = GL11.glGetInteger(GL30.GL_FRAMEBUFFER_BINDING);
    }

    private void restoreFramebuffer() {
        final ContextCapabilities cap = GL.getCapabilities();

        if (cap.OpenGL30) {
            LOGGER.trace(GL_MARKER, "glBindFramebuffer(GL_FRAMEBUFFER, {})", this.saveFramebuffer);
            GL30.glBindFramebuffer(GL30.GL_FRAMEBUFFER, this.saveFramebuffer);
            assert checkGLError() : glErrorMsg("glBindFramebuffer(II)", "GL_FRAMEBUFFER", this.saveFramebuffer);
        } else if (cap.GL_ARB_framebuffer_object) {
            LOGGER.trace(GL_MARKER, "glBindFramebuffer(GL_FRAMEBUFFER, {}) (ARB)", this.saveFramebuffer);
            ARBFramebufferObject.glBindFramebuffer(GL30.GL_FRAMEBUFFER, this.saveFramebuffer);
            assert checkGLError() : glErrorMsg("glBindFramebufferARB(II)", "GL_FRAMEBUFFER", this.saveFramebuffer);
        } else if (cap.GL_EXT_framebuffer_object) {
            LOGGER.trace(GL_MARKER, "glBindFramebufferEXT(GL_FRAMEBUFFER, {})", this.saveFramebuffer);
            EXTFramebufferObject.glBindFramebufferEXT(GL30.GL_FRAMEBUFFER, this.saveFramebuffer);
            assert checkGLError() : glErrorMsg("glBindFramebufferEXT(II)", "GL_FRAMEBUFFER", this.saveFramebuffer);
        } else {
            throw new UnsupportedOperationException("glBindFramebuffer is not supported! glBindFramebuffer requires either: an OpenGL 3.0 context, ARB_framebuffer_object, or EXT_framebuffer_object.");
        }
    }

    private int saveBuffer = 0;

    private void saveBuffer() {
        LOGGER.trace(GL_MARKER, "glGetInteger(GL_ARRAY_BUFFER_BINDING)");
        this.saveBuffer = GL11.glGetInteger(GL15.GL_ARRAY_BUFFER_BINDING);
    }

    private void restoreBuffer() {
        LOGGER.trace(GL_MARKER, "glBindBuffer(GL_ARRAY_BUFFER, {})", this.saveBuffer);
        GL15.glBindBuffer(GL15.GL_ARRAY_BUFFER, this.saveBuffer);
        assert checkGLError() : glErrorMsg("glBindBuffer(II)", "GL_ARRAY_BUFFER", this.saveBuffer);
    }

    private int saveTex1d = 0;

    private void saveTexture1d() {
        LOGGER.trace(GL_MARKER, "glGetInteger(GL_TEXTURE_BINDING_1D)");
        this.saveTex1d = GL11.glGetInteger(GL11.GL_TEXTURE_BINDING_1D);
        assert checkGLError() : glErrorMsg("glGetInteger(I)", "GL_TEXTURE_BINDING_1D");
    }

    private void restoreTexture1d() {
        LOGGER.trace(GL_MARKER, "glBindTexture(GL_TEXTURE_1D, {})", this.saveTex1d);
        GL11.glBindTexture(GL11.GL_TEXTURE_1D, this.saveTex1d);
        assert checkGLError() : glErrorMsg("glBindTexture(II)", "GL_TEXTURE_1D", this.saveTex1d);
    }

    private int saveTex2d = 0;

    private void saveTexture2d() {
        LOGGER.trace(GL_MARKER, "glGetInteger(GL_TEXTURE_BINDING_2D)");
        this.saveTex2d = GL11.glGetInteger(GL11.GL_TEXTURE_BINDING_2D);
        assert checkGLError() : glErrorMsg("glGetInteger(I)", "GL_TEXTURE_BINDING_2D");
    }

    private void restoreTexture2d() {
        LOGGER.trace(GL_MARKER, "glBindTexture(GL_TEXTURE_2D, {})", this.saveTex2d);
        GL11.glBindTexture(GL11.GL_TEXTURE_2D, this.saveTex2d);
        assert checkGLError() : glErrorMsg("glBindTexture(II)", "GL_TEXTURE_2D", this.saveTex2d);
    }

    private int saveTex3d = 0;

    private void saveTexture3d() {
        LOGGER.trace(GL_MARKER, "glGetInteger(GL_TEXTURE_BINDING_3D)");
        this.saveTex3d = GL11.glGetInteger(GL12.GL_TEXTURE_BINDING_3D);
        assert checkGLError() : glErrorMsg("glGetInteger(I)", "GL_TEXTURE_BINDING_3D");
    }

    private void restoreTexture3d() {
        LOGGER.trace(GL_MARKER, "glBindTexture(GL_TEXTURE_3D, {})", this.saveTex3d);
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
        LOGGER.trace(GL_MARKER, "glGetInteger(GL_CURRENT_PROGRAM)");
        this.saveProgram = GL11.glGetInteger(GL20.GL_CURRENT_PROGRAM);
        assert checkGLError() : glErrorMsg("glGetInteger(I)", "GL_CURRENT_PROGRAM");
    }

    private void restoreProgram() {
        LOGGER.trace(GL_MARKER, "glUseProgram({})", this.saveProgram);
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
            LOGGER.trace(GL_MARKER, "glGenFramebuffers()");
            id = GL30.glGenFramebuffers();
            assert checkGLError() : glErrorMsg("glGenFramebuffers(void)");

            this.saveFramebuffer();

            LOGGER.trace(GL_MARKER, "glBindFramebuffer(GL_FRAMEBUFFER, {})", id);
            GL30.glBindFramebuffer(GL30.GL_FRAMEBUFFER, id);
            assert checkGLError() : glErrorMsg("glBindFramebuffer(II)", "GL_FRAMEBUFFER", id);

            this.restoreFramebuffer();
        } else if (cap.GL_ARB_framebuffer_object) {
            LOGGER.trace(GL_MARKER, "glGenFramebuffers() (ARB)");
            id = ARBFramebufferObject.glGenFramebuffers();
            assert checkGLError() : glErrorMsg("glGenFramebuffersARB(void)");

            this.saveFramebuffer();

            LOGGER.trace(GL_MARKER, "glBindFramebuffer(GL_FRAMEBUFFER, {}) (ARB)", id);
            ARBFramebufferObject.glBindFramebuffer(GL30.GL_FRAMEBUFFER, id);
            assert checkGLError() : glErrorMsg("glBindFramebufferARB(II)", "GL_FRAMEBUFFER", id);

            this.restoreFramebuffer();
        } else if (cap.GL_EXT_framebuffer_object) {
            LOGGER.trace(GL_MARKER, "glGenFramebuffersEXT()");
            id = EXTFramebufferObject.glGenFramebuffersEXT();
            assert checkGLError() : glErrorMsg("glGenFramebuffersEXT(void)");

            this.saveFramebuffer();

            LOGGER.trace(GL_MARKER, "glBindFramebufferEXT(GL_FRAMEBUFFER, {})", id);
            EXTFramebufferObject.glBindFramebufferEXT(GL30.GL_FRAMEBUFFER, id);
            assert checkGLError() : glErrorMsg("glBindFramebufferEXT(II)", "GL_FRAMEBUFFER", id);

            this.restoreFramebuffer();
        } else {
            throw unsupportedFramebufferObject();
        }

        return id;
    }

    @Override
    public int glCreateBuffers() {
        this.saveBuffer();
        final int id = NoDSA.getInstance().glCreateBuffers();
        this.restoreBuffer();

        return id;
    }

    @Override
    public int glCreateTextures(int target) {
        assert checkGLenum(target, GLTextureTarget::of) : invalidGLenumMsg(target);

        this.saveTexture(target);

        final int id = NoDSA.getInstance().glCreateTextures(target);

        this.restoreTexture(target);

        return id;
    }

    @Override
    public boolean isSupported() {
        return NoDSA.getInstance().isSupported();
    }

    @Override
    public void glGetNamedBufferSubData(int bufferId, long offset, ByteBuffer data) {
        assert checkId(bufferId) : invalidBufferIdMsg(bufferId);
        assert checkOffset(offset) : invalidOffsetMsg(offset);
        assert checkBufferIsNative(data) : bufferIsNotNativeMsg(data);
        assert data.isDirect() : NON_DIRECT_BUFFER_MSG;

        this.saveBuffer();
        NoDSA.getInstance().glGetNamedBufferSubData(bufferId, offset, data);
        this.restoreBuffer();
    }

    @Override
    public int glGetNamedBufferParameteri(int bufferId, int pName) {
        assert checkId(bufferId) : invalidBufferIdMsg(bufferId);
        assert checkGLenum(pName, GLBufferParameterName::of) : invalidGLenumMsg(pName);

        this.saveBuffer();
        final int val = NoDSA.getInstance().glGetNamedBufferParameteri(bufferId, pName);
        this.restoreBuffer();

        return val;
    }

    @Override
    public void glNamedBufferData(int bufferId, long size, int usage) {
        assert checkId(bufferId) : invalidBufferIdMsg(bufferId);
        assert checkSize(size) : invalidSizeMsg(size);
        assert checkGLenum(usage, GLBufferUsage::of) : invalidGLenumMsg(usage);

        this.saveBuffer();
        NoDSA.getInstance().glNamedBufferData(bufferId, size, usage);
        this.restoreBuffer();
    }

    @Override
    public void glNamedBufferData(int bufferId, ByteBuffer data, int usage) {
        assert checkId(bufferId) : invalidBufferIdMsg(bufferId);
        assert checkBufferIsNative(data) : bufferIsNotNativeMsg(data);
        assert data.isDirect() : NON_DIRECT_BUFFER_MSG;
        assert checkGLenum(usage, GLBufferUsage::of) : invalidGLenumMsg(usage);

        this.saveBuffer();
        NoDSA.getInstance().glNamedBufferData(bufferId, data, usage);
        this.restoreBuffer();
    }

    @Override
    public void glNamedBufferSubData(int bufferId, long offset, ByteBuffer data) {
        assert checkId(bufferId) : invalidBufferIdMsg(bufferId);
        assert checkOffset(offset) : invalidOffsetMsg(offset);
        assert checkBufferIsNative(data) : bufferIsNotNativeMsg(data);
        assert data.isDirect() : NON_DIRECT_BUFFER_MSG;

        this.saveBuffer();
        NoDSA.getInstance().glNamedBufferSubData(bufferId, offset, data);
        this.restoreBuffer();
    }

    @Override
    public void glNamedBufferStorage(int bufferId, ByteBuffer data, int flags) {
        assert checkId(bufferId) : invalidBufferIdMsg(bufferId);
        assert checkBufferIsNative(data) : bufferIsNotNativeMsg(data);
        assert data.isDirect() : NON_DIRECT_BUFFER_MSG;

        this.saveBuffer();
        NoDSA.getInstance().glNamedBufferStorage(bufferId, data, flags);
        this.restoreBuffer();
    }

    @Override
    public void glNamedBufferStorage(int bufferId, long size, int flags) {
        assert checkId(bufferId) : invalidBufferIdMsg(bufferId);
        assert checkSize(size) : invalidSizeMsg(size);

        this.saveBuffer();
        NoDSA.getInstance().glNamedBufferStorage(bufferId, size, flags);
        this.restoreBuffer();
    }

    @Override
    public ByteBuffer glMapNamedBufferRange(int bufferId, long offset, long length, int access, ByteBuffer recycled) {
        assert checkId(bufferId) : invalidBufferIdMsg(bufferId);
        assert checkOffset(offset) : invalidOffsetMsg(offset);
        assert checkSize(length) : invalidSizeMsg(length);

        this.saveBuffer();

        final ByteBuffer out = NoDSA.getInstance().glMapNamedBufferRange(bufferId, offset, length, access, recycled);

        this.restoreBuffer();

        return out;
    }

    @Override
    public void glUnmapNamedBuffer(int bufferId) {
        assert checkId(bufferId);

        this.saveBuffer();
        NoDSA.getInstance().glUnmapNamedBuffer(bufferId);
        this.restoreBuffer();
    }

    @Override
    public void glCopyNamedBufferSubData(int readBufferId, int writeBufferId, long readOffset, long writeOffset, long size) {
        assert checkId(readBufferId) : invalidBufferIdMsg(readBufferId);
        assert checkId(writeBufferId) : invalidBufferIdMsg(writeBufferId);
        assert checkOffset(readOffset) : invalidOffsetMsg(readOffset);
        assert checkOffset(writeOffset) : invalidOffsetMsg(writeOffset);
        assert checkSize(size) : invalidSizeMsg(size);

        NoDSA.getInstance().glCopyNamedBufferSubData(readBufferId, writeBufferId, readOffset, writeOffset, size);
    }

    @Override
    public void glProgramUniform1f(int programId, int location, float value) {
        assert checkId(programId) : invalidProgramIdMsg(programId);
        assert checkOffset(location) : invalidUniformLocationMsg(location);
        assert checkFloat(value) : invalidFloatMsg(value);

        this.saveProgram();
        NoDSA.getInstance().glProgramUniform1f(programId, location, value);
        this.restoreProgram();
    }

    @Override
    public void glProgramUniform2f(int programId, int location, float v0, float v1) {
        assert checkId(programId) : invalidProgramIdMsg(programId);
        assert checkOffset(location) : invalidUniformLocationMsg(location);
        assert checkFloat(v0) : invalidFloatMsg(v0);
        assert checkFloat(v1) : invalidFloatMsg(v1);

        this.saveProgram();
        NoDSA.getInstance().glProgramUniform2f(programId, location, v0, v1);
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
        NoDSA.getInstance().glProgramUniform3f(programId, location, v0, v1, v2);
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
        NoDSA.getInstance().glProgramUniform4f(programId, location, v0, v1, v2, v3);
        this.restoreProgram();
    }

    @Override
    public void glProgramUniform1d(int programId, int location, double value) {
        assert checkId(programId) : invalidProgramIdMsg(programId);
        assert checkOffset(location) : invalidUniformLocationMsg(location);
        assert checkDouble(value) : invalidDoubleMsg(value);

        this.saveProgram();
        NoDSA.getInstance().glProgramUniform1d(programId, location, value);
        this.restoreProgram();
    }

    @Override
    public void glProgramUniform2d(int programId, int location, double v0, double v1) {
        assert checkId(programId) : invalidProgramIdMsg(programId);
        assert checkOffset(location) : invalidUniformLocationMsg(location);
        assert checkDouble(v0) : invalidDoubleMsg(v0);
        assert checkDouble(v1) : invalidDoubleMsg(v1);

        this.saveProgram();
        NoDSA.getInstance().glProgramUniform2d(programId, location, v0, v1);
        this.restoreProgram();
    }

    @Override
    public void glProgramUniform3d(int programId, int location, double v0, double v1, double v2) {
        assert checkId(programId) : invalidProgramIdMsg(programId);
        assert checkOffset(location) : invalidUniformLocationMsg(location);
        assert checkDouble(v0) : invalidDoubleMsg(v0);
        assert checkDouble(v1) : invalidDoubleMsg(v1);
        assert checkDouble(v2) : invalidDoubleMsg(v2);

        this.saveProgram();
        NoDSA.getInstance().glProgramUniform3d(programId, location, v0, v1, v2);
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

        this.saveProgram();
        NoDSA.getInstance().glProgramUniform4d(programId, location, v0, v1, v2, v3);
        this.restoreProgram();
    }

    @Override
    public void glProgramUniformMatrix2f(int programId, int location, boolean needsTranspose, FloatBuffer data) {
        assert checkId(programId) : invalidProgramIdMsg(programId);
        assert checkOffset(location) : invalidUniformLocationMsg(location);
        assert data.isDirect() : NON_DIRECT_BUFFER_MSG;

        this.saveProgram();
        NoDSA.getInstance().glProgramUniformMatrix2f(programId, location, needsTranspose, data);
        this.restoreProgram();
    }

    @Override
    public void glProgramUniformMatrix3f(int programId, int location, boolean needsTranspose, FloatBuffer data) {
        assert checkId(programId) : invalidProgramIdMsg(programId);
        assert checkOffset(location) : invalidUniformLocationMsg(location);
        assert data.isDirect() : NON_DIRECT_BUFFER_MSG;

        this.saveProgram();
        NoDSA.getInstance().glProgramUniformMatrix3f(programId, location, needsTranspose, data);
        this.restoreProgram();
    }

    @Override
    public void glProgramUniformMatrix4f(int programId, int location, boolean needsTranspose, FloatBuffer data) {
        assert checkId(programId) : invalidProgramIdMsg(programId);
        assert checkOffset(location) : invalidUniformLocationMsg(location);
        assert data.isDirect() : NON_DIRECT_BUFFER_MSG;

        this.saveProgram();
        NoDSA.getInstance().glProgramUniformMatrix4f(programId, location, needsTranspose, data);
        this.restoreProgram();
    }

    @Override
    public void glProgramUniformMatrix2d(int programId, int location, boolean needsTranspose, DoubleBuffer data) {
        assert checkId(programId) : invalidProgramIdMsg(programId);
        assert checkOffset(location) : invalidUniformLocationMsg(location);
        assert data.isDirect() : NON_DIRECT_BUFFER_MSG;

        this.saveProgram();
        NoDSA.getInstance().glProgramUniformMatrix2d(programId, location, needsTranspose, data);
        this.restoreProgram();
    }

    @Override
    public void glProgramUniformMatrix3d(int programId, int location, boolean needsTranspose, DoubleBuffer data) {
        assert checkId(programId) : invalidProgramIdMsg(programId);
        assert checkOffset(location) : invalidUniformLocationMsg(location);
        assert data.isDirect() : NON_DIRECT_BUFFER_MSG;

        this.saveProgram();
        NoDSA.getInstance().glProgramUniformMatrix3d(programId, location, needsTranspose, data);
        this.restoreProgram();
    }

    @Override
    public void glProgramUniformMatrix4d(int programId, int location, boolean needsTranspose, DoubleBuffer data) {
        assert checkId(programId) : invalidProgramIdMsg(programId);
        assert checkOffset(location) : invalidUniformLocationMsg(location);
        assert data.isDirect() : NON_DIRECT_BUFFER_MSG;

        this.saveProgram();
        NoDSA.getInstance().glProgramUniformMatrix4d(programId, location, needsTranspose, data);
        this.restoreProgram();
    }

    @Override
    public void glTextureParameteri(int textureId, int target, int pName, int val) {
        assert checkId(textureId) : invalidTextureIdMsg(textureId);
        assert checkGLenum(target, GLTextureTarget::of) : invalidGLenumMsg(target);

        this.saveTexture(target);
        NoDSA.getInstance().glTextureParameteri(textureId, target, pName, val);
        this.restoreTexture(target);
    }

    @Override
    public void glTextureParameterf(int textureId, int target, int pName, float val) {
        assert checkId(textureId) : invalidTextureIdMsg(textureId);
        assert checkGLenum(target, GLTextureTarget::of) : invalidGLenumMsg(target);
        assert checkFloat(val) : invalidFloatMsg(val);

        this.saveTexture(target);
        NoDSA.getInstance().glTextureParameterf(textureId, target, pName, val);
        this.restoreTexture(target);
    }

    @Override
    public void glGenerateTextureMipmap(int textureId, int target) {
        assert checkId(textureId) : invalidTextureIdMsg(textureId);
        assert checkGLenum(target, GLTextureTarget::of) : invalidGLenumMsg(target);

        this.saveTexture(target);
        NoDSA.getInstance().glGenerateTextureMipmap(textureId, target);
        this.restoreTexture(target);
    }

    @Override
    public void glBindTextureUnit(int unit, int target, int textureId) {
        assert checkNullableId(unit) : invalidTextureUnitMsg(unit);
        assert checkGLenum(target, GLTextureTarget::of) : invalidGLenumMsg(target);

        NoDSA.getInstance().glBindTextureUnit(unit, target, textureId);
    }

    public static DSADriver getInstance() {
        return Holder.INSTANCE;
    }

    @Override
    public void glProgramUniform1i(int programId, int location, int value) {
        assert checkId(programId) : invalidProgramIdMsg(programId);
        assert checkOffset(location) : invalidUniformLocationMsg(location);

        this.saveProgram();
        NoDSA.getInstance().glProgramUniform1i(programId, location, value);
        this.restoreProgram();
    }

    @Override
    public void glProgramUniform2i(int programId, int location, int v0, int v1) {
        assert checkId(programId) : invalidProgramIdMsg(programId);
        assert checkOffset(location) : invalidUniformLocationMsg(location);

        this.saveProgram();
        NoDSA.getInstance().glProgramUniform2i(programId, location, v0, v1);
        this.restoreProgram();
    }

    @Override
    public void glProgramUniform3i(int programId, int location, int v0, int v1, int v2) {
        assert checkId(programId) : invalidProgramIdMsg(programId);
        assert checkOffset(location) : invalidUniformLocationMsg(location);

        this.saveProgram();
        NoDSA.getInstance().glProgramUniform3i(programId, location, v0, v1, v2);
        this.restoreProgram();
    }

    @Override
    public void glProgramUniform4i(int programId, int location, int v0, int v1, int v2, int v3) {
        assert checkId(programId) : invalidProgramIdMsg(programId);
        assert checkOffset(location) : invalidUniformLocationMsg(location);

        this.saveProgram();
        NoDSA.getInstance().glProgramUniform4i(programId, location, v0, v1, v2, v3);
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
        NoDSA.getInstance().glTextureSubImage1d(textureId, level, xOffset, width, format, type, pixels);
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
        NoDSA.getInstance().glTextureSubImage2d(textureId, level, xOffset, yOffset, width, height, format, type, pixels);
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
        NoDSA.getInstance().glTextureSubImage3d(textureId, level, xOffset, yOffset, zOffset, width, height, depth, format, type, pixels);
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
        NoDSA.getInstance().glTextureImage1d(texture, target, level, internalFormat, width, border, format, type, pixels);
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
        NoDSA.getInstance().glTextureImage1d(texture, target, level, internalFormat, width, border, format, type, ptr);
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
        NoDSA.getInstance().glTextureImage2d(texture, target, level, internalFormat, width, height, border, format, type, pixels);
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
        NoDSA.getInstance().glTextureImage2d(texture, target, level, internalFormat, width, height, border, format, type, ptr);
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
        NoDSA.getInstance().glTextureImage3d(texture, target, level, internalFormat, width, height, depth, border, format, type, pixels);
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
        NoDSA.getInstance().glTextureImage3d(texture, target, level, internalFormat, width, height, depth, border, format, type, ptr);
        this.restoreTexture3d();
    }

    @Override
    public void glNamedFramebufferTexture1D(int framebuffer, int attachment, int texTarget, int texture, int level) {
        assert checkId(framebuffer) : invalidFramebufferIdMsg(framebuffer);
        assert checkGLenum(texTarget, GLTextureTarget::of) : invalidGLenumMsg(texTarget);
        assert checkId(texture) : invalidTextureIdMsg(texture);
        assert checkMipmapLevel(level) : invalidMipmapLevelMsg(level);

        this.saveFramebuffer();
        NoDSA.getInstance().glNamedFramebufferTexture1D(framebuffer, attachment, texTarget, texture, level);
        this.restoreFramebuffer();
    }

    @Override
    public void glNamedFramebufferTexture2D(int framebuffer, int attachment, int texTarget, int texture, int level) {
        assert checkId(framebuffer) : invalidFramebufferIdMsg(framebuffer);
        assert checkGLenum(texTarget, GLTextureTarget::of) : invalidGLenumMsg(texTarget);
        assert checkId(texture) : invalidTextureIdMsg(texture);
        assert checkMipmapLevel(level) : invalidMipmapLevelMsg(level);

        this.saveFramebuffer();
        NoDSA.getInstance().glNamedFramebufferTexture2D(framebuffer, attachment, texTarget, texture, level);
        this.restoreFramebuffer();
    }

    @Override
    public void glBlitNamedFramebuffer(int readFramebuffer, int drawFramebuffer, int srcX0, int srcY0, int srcX1, int srcY1, int dstX0, int dstY0, int dstX1, int dstY1, int mask, int filter) {
        assert checkNullableId(readFramebuffer) : invalidFramebufferIdMsg(readFramebuffer);
        assert checkNullableId(drawFramebuffer) : invalidFramebufferIdMsg(drawFramebuffer);

        final ContextCapabilities cap = GL.getCapabilities();

        LOGGER.trace(GL_MARKER, "glGetInteger(GL_READ_FRAMEBUFFER_BINDING)");
        final int prevReadFB = GL11.glGetInteger(GL30.GL_READ_FRAMEBUFFER_BINDING);
        assert checkGLError() : glErrorMsg("glGetInteger(I)", "GL_READ_FRAMEBUFFER_BINDING");

        LOGGER.trace(GL_MARKER, "glGetInteger(GL_DRAW_FRAMEBUFFER_BINDING)");
        final int prevDrawFB = GL11.glGetInteger(GL30.GL_DRAW_FRAMEBUFFER_BINDING);
        assert checkGLError() : glErrorMsg("glGetInteger(I)", "GL_DRAW_FRAMEBUFFER_BINDING");

        NoDSA.getInstance().glBlitNamedFramebuffer(readFramebuffer, drawFramebuffer, srcX0, srcY0, srcX1, srcY1, dstX0, dstY0, dstX1, dstY1, mask, filter);

        if (cap.OpenGL30) {
            LOGGER.trace(GL_MARKER, "glBindFramebuffer(GL_DRAW_FRAMEBUFFER, {})", prevDrawFB);
            GL30.glBindFramebuffer(GL30.GL_DRAW_FRAMEBUFFER, prevDrawFB);
            assert checkGLError() : glErrorMsg("glBindFramebuffer(II)", "GL_DRAW_FRAMEBUFFER", prevDrawFB);

            LOGGER.trace(GL_MARKER, "glBindFramebuffer(GL_READ_FRAMEBUFFER, {})", prevReadFB);
            GL30.glBindFramebuffer(GL30.GL_READ_FRAMEBUFFER, prevReadFB);
            assert checkGLError() : glErrorMsg("glBindFramebuffer(II)", "GL_READ_FRAMEBUFFER", prevReadFB);
        } else if (cap.GL_ARB_framebuffer_object) {
            LOGGER.trace(GL_MARKER, "glBindFramebuffer(GL_DRAW_FRAMEBUFFER, {}) (ARB)", prevDrawFB);
            ARBFramebufferObject.glBindFramebuffer(ARBFramebufferObject.GL_DRAW_FRAMEBUFFER, prevDrawFB);
            assert checkGLError() : glErrorMsg("glBindFramebufferARB(II)", "GL_DRAW_FRAMEBUFFER", prevDrawFB);

            LOGGER.trace(GL_MARKER, "glBindFramebuffer(GL_READ_FRAMEBUFFER, {}) (ARB)", prevReadFB);
            ARBFramebufferObject.glBindFramebuffer(ARBFramebufferObject.GL_READ_FRAMEBUFFER, prevReadFB);
            assert checkGLError() : glErrorMsg("glBindFramebufferARB(II)", "GL_READ_FRAMEBUFFER", prevReadFB);
        } else if (cap.GL_EXT_framebuffer_object) {
            LOGGER.trace(GL_MARKER, "glBindFramebufferEXT(GL_DRAW_FRAMEBUFFER, {})", prevDrawFB);
            EXTFramebufferObject.glBindFramebufferEXT(ARBFramebufferObject.GL_DRAW_FRAMEBUFFER, prevDrawFB);
            assert checkGLError() : glErrorMsg("glBindFramebufferEXT(II)", "GL_DRAW_FRAMEBUFFER", prevDrawFB);

            LOGGER.trace(GL_MARKER, "glBindFramebufferEXT(GL_READ_FRAMEBUFFER, {})", prevReadFB);
            EXTFramebufferObject.glBindFramebufferEXT(ARBFramebufferObject.GL_READ_FRAMEBUFFER, prevReadFB);
            assert checkGLError() : glErrorMsg("glBindFramebufferEXT(II)", "GL_READ_FRAMEBUFFER", prevReadFB);
        } else {
            throw unsupportedFramebufferObject();
        }
    }

    @Override
    public void glNamedBufferReadPixels(int bufferId, int x, int y, int width, int height, int format, int type, long ptr) {
        assert checkId(bufferId) : invalidBufferIdMsg(bufferId);
        assert checkGLenum(format, GLTextureFormat::of) : invalidGLenumMsg(format);
        assert checkGLenum(type, GLType::of) : invalidGLenumMsg(type);

        LOGGER.trace(GL_MARKER, "glGetInteger(GL_PIXEL_PACK_BUFFER_BINDING)");
        final int prevPixBuffer = GL11.glGetInteger(GL21.GL_PIXEL_PACK_BUFFER_BINDING);
        assert checkGLError() : glErrorMsg("glGetInteger(I)", "GL_PIXEL_PACK_BUFFER_BINDING");

        NoDSA.getInstance().glNamedBufferReadPixels(bufferId, x, y, width, height, format, type, ptr);

        LOGGER.trace(GL_MARKER, "glBindBuffer(GL_PIXEL_PACK_BUFFER, {})", prevPixBuffer);
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
        NoDSA.getInstance().glGetTextureImage(texture, target, level, format, type, bufferSize, pixels);
        this.restoreTexture(target);
    }

    private static class Holder {

        private static final DSADriver INSTANCE = new FakeDSA();
    }
}
