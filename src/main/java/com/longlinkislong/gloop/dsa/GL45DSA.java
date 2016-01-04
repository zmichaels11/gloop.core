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
import static com.longlinkislong.gloop.GLAsserts.checkMipmapDefine;
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
import static com.longlinkislong.gloop.GLAsserts.invalidMipmapDefineMsg;
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
import java.nio.DoubleBuffer;
import java.nio.FloatBuffer;
import org.lwjgl.opengl.GL;
import org.lwjgl.opengl.GL41;
import org.lwjgl.opengl.GL43;
import org.lwjgl.opengl.GL45;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.Marker;
import org.slf4j.MarkerFactory;
import static org.lwjgl.system.MemoryUtil.memAddress;

/**
 * An implementation of ARB_direct_state_access that uses OpenGL4.5 calls.
 *
 * @author zmichaels
 * @since 15.07.14
 */
public final class GL45DSA extends Common implements DSADriver {

    private static final Marker GL_MARKER = MarkerFactory.getMarker("OPENGL");
    private static final Logger LOGGER = LoggerFactory.getLogger("OPENGL");

    @Override
    public String toString() {
        return "GL45DSA";
    }
    
    @Override
    public void glInvalidateBufferData(final int bufferId) {
        LOGGER.trace(GL_MARKER, "glInvalidateBufferData({})", bufferId);
        GL43.glInvalidateBufferData(bufferId);
        assert checkGLError() : glErrorMsg("glInvalidateBufferData(I) failed!", bufferId);
    }
    
    @Override
    public void glInvalidateBufferSubData(final int bufferId, final int offset, final int length) {
        LOGGER.trace(GL_MARKER, "glInvalidateBufferSubData({}, {}, {})", bufferId, offset, length);
        GL43.glInvalidateBufferSubData(bufferId, offset, length);
        assert checkGLError() : glErrorMsg("glInvalidateBufferSubData(III) failed!", bufferId, offset, length);
    }
    
    @Override
    public void glInvalidateTexSubImage(
            final int texImg, final int level, 
            final int xOffset, final int yOffset, final int zOffset, 
            final int width, final int height, final int depth) {
        
        LOGGER.trace(GL_MARKER, "glInvalidateTexSubImage({}, {}, {}, {}, {}, {}, {}, {})",
                texImg, level,
                xOffset, yOffset, zOffset,
                width, height, depth);
        GL43.glInvalidateTexSubImage(texImg, level, xOffset, yOffset, zOffset, width, height, depth);
        assert checkGLError() : glErrorMsg("glInvalidateTexSubImage(IIIIIIII) failed!",
                texImg, level,
                xOffset, yOffset, zOffset,
                width, height, depth);
    }

    @Override
    public void glInvalidateTexImage(final int texImg, final int level) {
        LOGGER.trace(GL_MARKER, "glInvalidateTexImage({}, {})", texImg, level);
        GL43.glInvalidateTexImage(texImg, level);
        assert checkGLError() : glErrorMsg("glInvalidateTexImage(II) failed!", texImg, level);
    }
    
    @Override
    public int glCreateFramebuffers() {
        LOGGER.trace(GL_MARKER, "glCreateFramebuffers()");
        final int out = GL45.glCreateFramebuffers();
        assert checkGLError() : glErrorMsg("glCreateFramebuffers(void)");
        return out;
    }

    @Override
    public int glCreateBuffers() {
        LOGGER.trace(GL_MARKER, "glCreateBuffers()");
        final int out = GL45.glCreateBuffers();
        assert checkGLError() : glErrorMsg("glCreateBuffers(void)");
        return out;
    }

    @Override
    public int glCreateTextures(int target) {
        assert checkGLenum(target, GLTextureTarget::of) : invalidGLenumMsg(target);
        LOGGER.trace(GL_MARKER, "glCreateTextures({})", target);
        final int out = GL45.glCreateTextures(target);
        assert checkGLError() : glErrorMsg("glCreateTextures(I)", GLTextureTarget.of(target).get());
        return out;
    }

    @Override
    public boolean isSupported() {
        return GL.getCapabilities().OpenGL45;
    }

    @Override
    public void glGetNamedBufferSubData(int bufferId, long offset, ByteBuffer out) {
        assert checkId(bufferId) : invalidBufferIdMsg(bufferId);
        assert checkOffset(offset) : invalidOffsetMsg(offset);
        assert checkBufferIsNative(out) : bufferIsNotNativeMsg(out);
        assert out.isDirect() : NON_DIRECT_BUFFER_MSG;

        LOGGER.trace(GL_MARKER, "glGetNamedBufferSubData({}, {}, {})", bufferId, offset, out);
        GL45.glGetNamedBufferSubData(bufferId, offset, out);
        assert checkGLError() : glErrorMsg("glGetNamedBufferSubData(IL*)", bufferId, offset, toHexString(memAddress(out)));
    }

    @Override
    public int glGetNamedBufferParameteri(int bufferId, int pName) {
        assert checkId(bufferId) : invalidBufferIdMsg(bufferId);
        assert checkGLenum(pName, GLBufferParameterName::of) : invalidGLenumMsg(pName);

        final int out = GL45.glGetNamedBufferParameteri(bufferId, pName);
        assert checkGLError() : glErrorMsg("glGetNamedBufferParameteri(II)", bufferId, GLBufferParameterName.of(pName).get());

        return out;
    }

    @Override
    public void glNamedBufferData(int bufferId, long size, int usage) {
        assert checkId(bufferId) : invalidBufferIdMsg(bufferId);
        assert checkSize(size) : invalidSizeMsg(size);
        assert checkGLenum(usage, GLBufferUsage::of) : invalidGLenumMsg(usage);

        LOGGER.trace(GL_MARKER, "glNamedBufferData({}, {}, {})", bufferId, size, usage);
        GL45.glNamedBufferData(bufferId, size, usage);
        assert checkGLError() : glErrorMsg("glNamedBufferData(ILI)", bufferId, size, GLBufferUsage.of(usage).get());
    }

    @Override
    public void glNamedBufferData(int bufferId, ByteBuffer data, int usage) {
        assert checkId(bufferId) : invalidBufferIdMsg(bufferId);
        assert checkBufferIsNative(data) : bufferIsNotNativeMsg(data);
        assert data.isDirect() : NON_DIRECT_BUFFER_MSG;
        assert checkGLenum(usage, GLBufferUsage::of) : invalidGLenumMsg(usage);

        LOGGER.trace(GL_MARKER, "glNamedBufferData({}, {}, {})", bufferId, data, usage);
        GL45.glNamedBufferData(bufferId, data, usage);
        assert checkGLError() : glErrorMsg("glNamedBufferData(I*I)", bufferId, toHexString(memAddress(data)), GLBufferUsage.of(usage).get());
    }

    @Override
    public void glNamedBufferSubData(int buffer, long offset, ByteBuffer data) {
        assert checkId(buffer) : invalidBufferIdMsg(buffer);
        assert checkOffset(offset) : invalidOffsetMsg(offset);
        assert checkBufferIsNative(data) : bufferIsNotNativeMsg(data);
        assert data.isDirect() : NON_DIRECT_BUFFER_MSG;

        LOGGER.trace(GL_MARKER, "glNamedBufferSubData({}, {}, {})", buffer, offset, data);
        GL45.glNamedBufferSubData(buffer, offset, data);
        assert checkGLError() : glErrorMsg("glNamedBufferSubData(IL*)", buffer, offset, toHexString(memAddress(data)));
    }

    @Override
    public void glNamedBufferStorage(int bufferId, ByteBuffer data, int flags) {
        assert checkId(bufferId) : invalidBufferIdMsg(bufferId);
        assert checkBufferIsNative(data) : bufferIsNotNativeMsg(data);
        assert data.isDirect() : NON_DIRECT_BUFFER_MSG;

        LOGGER.trace(GL_MARKER, "glNamedBufferStorage({}, {}, {})", bufferId, data, flags);
        GL45.glNamedBufferStorage(bufferId, data, flags);
        assert checkGLError() : glErrorMsg("glNamedBufferStorage(I*I)", bufferId, toHexString(memAddress(data)), flags);
    }

    @Override
    public void glNamedBufferStorage(int bufferId, long size, int flags) {
        assert checkId(bufferId) : invalidBufferIdMsg(bufferId);
        assert checkSize(size) : invalidSizeMsg(size);

        LOGGER.trace(GL_MARKER, "glNamedBufferStorage({}, {}, {})", bufferId, size, flags);
        GL45.glNamedBufferStorage(bufferId, size, flags);
        assert checkGLError() : glErrorMsg("glNamedBufferStorage(ILI)", bufferId, size, flags);
    }

    @Override
    public ByteBuffer glMapNamedBufferRange(int bufferId, long offset, long length, int access, ByteBuffer recycled) {
        assert checkId(bufferId) : invalidBufferIdMsg(bufferId);
        assert checkOffset(offset) : invalidOffsetMsg(offset);
        assert checkSize(length) : invalidSizeMsg(length);

        LOGGER.trace(GL_MARKER, "glMapNamedBufferRange({}, {}, {}, {})", bufferId, offset, length, access);
        final ByteBuffer out = GL45.glMapNamedBufferRange(bufferId, offset, length, access, recycled);
        assert checkGLError() : glErrorMsg("glMapNamedBufferRange(ILLI)", bufferId, offset, length, access);

        return out;
    }

    @Override
    public void glUnmapNamedBuffer(int bufferId) {
        assert checkId(bufferId) : invalidBufferIdMsg(bufferId);

        LOGGER.trace(GL_MARKER, "glUnmapNamedBuffer({})", bufferId);
        GL45.glUnmapNamedBuffer(bufferId);
        assert checkGLError() : glErrorMsg("glUnmapNamedBuffer(I)", bufferId);
    }

    @Override
    public void glCopyNamedBufferSubData(int readBufferId, int writeBufferId, long readOffset, long writeOffset, long size) {
        assert checkId(readBufferId) : invalidBufferIdMsg(readBufferId);
        assert checkId(writeBufferId) : invalidBufferIdMsg(writeBufferId);
        assert checkOffset(readOffset) : invalidOffsetMsg(readOffset);
        assert checkOffset(writeOffset) : invalidOffsetMsg(writeOffset);
        assert checkSize(size) : invalidSizeMsg(size);

        LOGGER.trace(GL_MARKER, "glCopyNamedBufferSubData({}, {}, {}, {}, {})", readBufferId, writeBufferId, readOffset, writeOffset, size);
        GL45.glCopyNamedBufferSubData(readBufferId, writeBufferId, readOffset, writeOffset, size);
        assert checkGLError() : glErrorMsg("glCopyNamedBufferSubData(IIIII)", readBufferId, writeBufferId, readOffset, writeOffset, size);
    }

    @Override
    public void glProgramUniform1f(int programId, int location, float value) {
        assert checkId(programId) : invalidProgramIdMsg(programId);
        assert checkOffset(location) : invalidUniformLocationMsg(location);
        assert checkFloat(value) : invalidFloatMsg(value);

        LOGGER.trace(GL_MARKER, "glProgramUniform1f({}, {}, {})", programId, location, value);
        GL41.glProgramUniform1f(programId, location, value);
        assert checkGLError() : glErrorMsg("glProgramUniform1f(IIF)", programId, location, value);
    }

    @Override
    public void glProgramUniform2f(int programId, int location, float v0, float v1) {
        assert checkId(programId) : invalidProgramIdMsg(programId);
        assert checkOffset(location) : invalidUniformLocationMsg(location);
        assert checkFloat(v0) : invalidFloatMsg(v0);
        assert checkFloat(v1) : invalidFloatMsg(v1);

        LOGGER.trace(GL_MARKER, "glProgramUniform2f({}, {}, {}, {})", programId, location, v0, v1);
        GL41.glProgramUniform2f(programId, location, v0, v1);
        assert checkGLError() : glErrorMsg("glProgramUniform2f(IIFF)", programId, location, v0, v1);
    }

    @Override
    public void glProgramUniform3f(int programId, int location, float v0, float v1, float v2) {
        assert checkId(programId) : invalidProgramIdMsg(programId);
        assert checkOffset(location) : invalidUniformLocationMsg(location);
        assert checkFloat(v0) : invalidFloatMsg(v0);
        assert checkFloat(v1) : invalidFloatMsg(v1);
        assert checkFloat(v2) : invalidFloatMsg(v2);

        LOGGER.trace(GL_MARKER, "glProgramUniform3f({}, {}, {}, {}, {})", programId, location, v0, v1, v2);
        GL41.glProgramUniform3f(programId, location, v0, v1, v2);
        assert checkGLError() : glErrorMsg("glProgramUniform3f(IIFFF)", programId, location, v0, v1, v2);
    }

    @Override
    public void glProgramUniform4f(int programId, int location, float v0, float v1, float v2, float v3) {
        assert checkId(programId) : invalidProgramIdMsg(programId);
        assert checkOffset(location) : invalidUniformLocationMsg(location);
        assert checkFloat(v0) : invalidFloatMsg(v0);
        assert checkFloat(v1) : invalidFloatMsg(v1);
        assert checkFloat(v2) : invalidFloatMsg(v2);
        assert checkFloat(v3) : invalidFloatMsg(v3);

        LOGGER.trace(GL_MARKER, "glProgramUniform4f({}, {}, {}, {}, {}, {})", programId, location, v0, v1, v2, v3);
        GL41.glProgramUniform4f(programId, location, v0, v1, v2, v3);
        assert checkGLError() : glErrorMsg("glProgramUniform4f(IIFFFF)", programId, location, v0, v1, v2, v3);
    }

    @Override
    public void glProgramUniform1d(int programId, int location, double value) {
        assert checkId(programId) : invalidProgramIdMsg(programId);
        assert checkOffset(location) : invalidUniformLocationMsg(location);
        assert checkDouble(value) : invalidDoubleMsg(value);

        LOGGER.trace(GL_MARKER, "glProgramUniform1d({}, {}, {})", programId, location, value);
        GL41.glProgramUniform1d(programId, location, value);
        assert checkGLError() : glErrorMsg("glProgramUniform1d(IID)", programId, location, value);
    }

    @Override
    public void glProgramUniform2d(int programId, int location, double v0, double v1) {
        assert checkId(programId) : invalidProgramIdMsg(programId);
        assert checkOffset(location) : invalidUniformLocationMsg(location);
        assert checkDouble(v0) : invalidDoubleMsg(v0);
        assert checkDouble(v1) : invalidDoubleMsg(v1);

        LOGGER.trace(GL_MARKER, "glProgramUniform2d({}, {}, {}, {})", programId, location, v0, v1);
        GL41.glProgramUniform2d(programId, location, v0, v1);
        assert checkGLError() : glErrorMsg("glProgramUniform2d(IIDD)", programId, location, v0, v1);
    }

    @Override
    public void glProgramUniform3d(int programId, int location, double v0, double v1, double v2) {
        assert checkId(programId) : invalidProgramIdMsg(programId);
        assert checkOffset(location) : invalidUniformLocationMsg(location);
        assert checkDouble(v0) : invalidDoubleMsg(v0);
        assert checkDouble(v1) : invalidDoubleMsg(v1);
        assert checkDouble(v2) : invalidDoubleMsg(v2);

        LOGGER.trace(GL_MARKER, "glProgramUniform3d({}, {}, {}, {}, {})", programId, location, v0, v1, v2);
        GL41.glProgramUniform3d(programId, location, v0, v1, v2);
        assert checkGLError() : glErrorMsg("glProgramUniform3d(IIDDD)", programId, location, v0, v1, v2);
    }

    @Override
    public void glProgramUniform4d(int programId, int location, double v0, double v1, double v2, double v3) {
        assert checkId(programId) : invalidProgramIdMsg(programId);
        assert checkOffset(location) : invalidUniformLocationMsg(location);
        assert checkDouble(v0) : invalidDoubleMsg(v0);
        assert checkDouble(v1) : invalidDoubleMsg(v1);
        assert checkDouble(v2) : invalidDoubleMsg(v2);
        assert checkDouble(v3) : invalidDoubleMsg(v3);

        LOGGER.trace(GL_MARKER, "glProgramUniform4d({}, {}, {}, {}, {}, {})", programId, location, v0, v1, v2, v3);
        GL41.glProgramUniform4d(programId, location, v0, v1, v2, v3);
        assert checkGLError() : glErrorMsg("glProgramUniform4d(IIDDDD)", programId, location, v0, v1, v2, v3);
    }

    @Override
    public void glProgramUniformMatrix2f(int programId, int location, boolean needsTranspose, FloatBuffer data) {
        assert checkId(programId) : invalidProgramIdMsg(programId);
        assert checkOffset(location) : invalidUniformLocationMsg(location);
        assert data.isDirect() : NON_DIRECT_BUFFER_MSG;

        LOGGER.trace(GL_MARKER, "glProgramUniformMatrix2fv({}, {}, {}, {})", programId, location, needsTranspose, data);
        GL41.glProgramUniformMatrix2fv(programId, location, needsTranspose, data);
        assert checkGLError() : glErrorMsg("glProgramUniformMatrix2fv(IIB*)", programId, location, needsTranspose, toHexString(memAddress(data)));
    }

    @Override
    public void glProgramUniformMatrix3f(int programId, int location, boolean needsTranspose, FloatBuffer data) {
        assert checkId(programId) : invalidProgramIdMsg(programId);
        assert checkOffset(location) : invalidUniformLocationMsg(location);
        assert data.isDirect() : NON_DIRECT_BUFFER_MSG;

        LOGGER.trace(GL_MARKER, "glProgramUniformMatrix3fv({}, {}, {}, {})", programId, location, needsTranspose, data);
        GL41.glProgramUniformMatrix3fv(programId, location, needsTranspose, data);
        assert checkGLError() : glErrorMsg("glProgramUniformMatrix3fv(IIB*)", programId, location, needsTranspose, toHexString(memAddress(data)));
    }

    @Override
    public void glProgramUniformMatrix4f(int programId, int location, boolean needsTranspose, FloatBuffer data) {
        assert checkId(programId) : invalidProgramIdMsg(programId);
        assert checkOffset(location) : invalidUniformLocationMsg(location);
        assert data.isDirect() : NON_DIRECT_BUFFER_MSG;

        LOGGER.trace(GL_MARKER, "glProgramUniformMatrix4fv({}, {}, {}, {})", programId, location, needsTranspose, data);
        GL41.glProgramUniformMatrix4fv(programId, location, needsTranspose, data);
        assert checkGLError() : glErrorMsg("glProgramUniformMatrix4fv(IIB*)", programId, location, needsTranspose, toHexString(memAddress(data)));
    }

    @Override
    public void glProgramUniformMatrix2d(int programId, int location, boolean needsTranspose, DoubleBuffer data) {
        assert checkId(programId) : invalidProgramIdMsg(programId);
        assert checkOffset(location) : invalidUniformLocationMsg(location);
        assert data.isDirect() : NON_DIRECT_BUFFER_MSG;

        LOGGER.trace(GL_MARKER, "glProgramUniformMatrix2dv({}, {}, {}, {})", programId, location, needsTranspose, data);
        GL41.glProgramUniformMatrix2dv(programId, location, needsTranspose, data);
        assert checkGLError() : glErrorMsg("glProgramUniformMatrix2dv(IIB*)", programId, location, needsTranspose, toHexString(memAddress(data)));
    }

    @Override
    public void glProgramUniformMatrix3d(int programId, int location, boolean needsTranspose, DoubleBuffer data) {
        assert checkId(programId) : invalidProgramIdMsg(programId);
        assert checkOffset(location) : invalidUniformLocationMsg(location);
        assert data.isDirect() : NON_DIRECT_BUFFER_MSG;

        LOGGER.trace(GL_MARKER, "glProgramUniformMatrix3dv({}, {}, {}, {})", programId, location, needsTranspose, data);
        GL41.glProgramUniformMatrix3dv(programId, location, needsTranspose, data);
        assert checkGLError() : glErrorMsg("glProgramUniformMatrix3dv(IIB*)", programId, location, needsTranspose, toHexString(memAddress(data)));
    }

    @Override
    public void glProgramUniformMatrix4d(int programId, int location, boolean needsTranspose, DoubleBuffer data) {
        assert checkId(programId) : invalidProgramIdMsg(programId);
        assert checkOffset(location) : invalidUniformLocationMsg(location);
        assert data.isDirect() : NON_DIRECT_BUFFER_MSG;

        LOGGER.trace(GL_MARKER, "glProgramUniformMatrix4dv({}, {}, {}, {})", programId, location, needsTranspose, data);
        GL41.glProgramUniformMatrix4dv(programId, location, needsTranspose, data);
        assert checkGLError() : glErrorMsg("glProgramUniformMatrix4dv(IIB*)", programId, location, needsTranspose, toHexString(memAddress(data)));
    }

    @Override
    public void glTextureParameteri(int textureId, int pName, int val) {
        assert checkId(textureId) : invalidTextureIdMsg(textureId);

        LOGGER.trace(GL_MARKER, "glTextureParameteri({}, {}, {})", textureId, pName, val);
        GL45.glTextureParameteri(textureId, pName, val);
        assert checkGLError() : glErrorMsg("glTextureParameteri(III)", textureId, pName, val);
    }

    @Override
    public void glTextureParameterf(int textureId, int pName, float val) {
        assert checkId(textureId) : invalidTextureIdMsg(textureId);
        assert checkFloat(val) : invalidFloatMsg(val);

        LOGGER.trace(GL_MARKER, "glTextureParameterf({}, {}, {})", textureId, pName, val);
        GL45.glTextureParameterf(textureId, pName, val);
        assert checkGLError() : glErrorMsg("glTextureParameterf(IIF)", textureId, pName, val);
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

        LOGGER.trace(GL_MARKER, "glTextureSubImage1D({}, {}, {}, {}, {}, {})", textureId, level, xOffset, width, format, type, pixels);
        GL45.glTextureSubImage1D(textureId, level, xOffset, width, format, type, pixels);
        assert checkGLError() : glErrorMsg("glTextureSubImage1D(IIIIII*)", textureId, level, xOffset, width, GLTextureFormat.of(format).get(), GLType.of(type).get(), toHexString(memAddress(pixels)));
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

        LOGGER.trace(GL_MARKER, "glTextureSubImage2D({}, {}, {}, {}, {}, {}, {}, {}, {})", textureId, level, xOffset, yOffset, width, height, type, pixels);
        GL45.glTextureSubImage2D(textureId, level, xOffset, yOffset, width, height, format, type, pixels);
        assert checkGLError() : glErrorMsg("glTextureSubImage2D(IIIIIIII*)", textureId, level, xOffset, yOffset, width, height, GLTextureFormat.of(format).get(), GLType.of(type).get(), toHexString(memAddress(pixels)));
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

        LOGGER.trace(GL_MARKER, "glTextureSubImage3D({}, {}, {}, {, {}, {}, {}, {}, {}, {}, {})", textureId, level, xOffset, yOffset, zOffset, width, height, depth, format, type, pixels);
        GL45.glTextureSubImage3D(textureId, level, xOffset, yOffset, zOffset, width, height, depth, format, type, pixels);
        assert checkGLError() : glErrorMsg("glTextureSubImage3D(IIIIIIIIII*)", textureId, level, xOffset, yOffset, zOffset, width, height, depth, GLTextureFormat.of(format).get(), GLType.of(type).get(), toHexString(memAddress(pixels)));
    }

    @Override
    public void glBindTextureUnit(int unit, int textureId) {
        assert checkNullableId(unit) : invalidTextureUnitMsg(unit);
        assert checkId(textureId) : invalidTextureIdMsg(textureId);

        LOGGER.trace(GL_MARKER, "glBindTextureUnit({}, {})", unit, textureId);
        GL45.glBindTextureUnit(unit, textureId);
        assert checkGLError() : glErrorMsg("glBindTextureUnit(II)", unit, textureId);
    }

    public static DSADriver getInstance() {
        return Holder.INSTANCE;
    }

    @Override
    public void glProgramUniform1i(int programId, int location, int value) {
        assert checkId(programId) : invalidProgramIdMsg(programId);
        assert checkOffset(location) : invalidUniformLocationMsg(location);

        LOGGER.trace(GL_MARKER, "glProgramUniform1i({}, {}, {})", programId, location, value);
        GL41.glProgramUniform1i(programId, location, value);
        assert checkGLError() : glErrorMsg("glProgramUniform1i(III)", programId, location, value);
    }

    @Override
    public void glProgramUniform2i(int programId, int location, int v0, int v1) {
        assert checkId(programId) : invalidProgramIdMsg(programId);
        assert checkOffset(location) : invalidUniformLocationMsg(location);

        LOGGER.trace(GL_MARKER, "glProgramUniform2i({}, {}, {}, {})", programId, location, v0, v1);
        GL41.glProgramUniform2i(programId, location, v0, v1);
        assert checkGLError() : glErrorMsg("glProgramUniform2i(IIII)", programId, location, v0, v1);
    }

    @Override
    public void glProgramUniform3i(int programId, int location, int v0, int v1, int v2) {
        assert checkId(programId) : invalidProgramIdMsg(programId);
        assert checkOffset(location) : invalidUniformLocationMsg(location);

        LOGGER.trace(GL_MARKER, "glProgramUniform3i({}, {}, {}, {}, {})", programId, location, v0,  v1, v2);
        GL41.glProgramUniform3i(programId, location, v0, v1, v2);
        assert checkGLError() : glErrorMsg("glProgramUniform3i(IIIII)", programId, location, v0, v1, v2);
    }

    @Override
    public void glProgramUniform4i(int programId, int location, int v0, int v1, int v2, int v3) {
        assert checkId(programId) : invalidProgramIdMsg(programId);
        assert checkOffset(location) : invalidUniformLocationMsg(location);

        LOGGER.trace(GL_MARKER, "glProgramUniform4i({}, {}, {}, {}, {}, {})", programId, location, v0, v1, v2, v3);
        GL41.glProgramUniform4i(programId, location, v0, v1, v2, v3);
        assert checkGLError() : glErrorMsg("glProgramUniform4i(IIIIIII)", programId, location, v0, v1, v2, v3);
    }

    @Override
    public void glTextureStorage1d(int textureId, int levels, int internalFormat, int width) {
        assert checkId(textureId) : invalidTextureIdMsg(textureId);
        assert checkMipmapDefine(levels) : invalidMipmapDefineMsg(levels);
        assert checkGLenum(internalFormat, GLTextureInternalFormat::of) : invalidGLenumMsg(internalFormat);
        assert checkDimension(width) : invalidWidthMsg(width);

        LOGGER.trace(GL_MARKER, "glTextureStorage1D({}, {}, {}, {})", textureId, levels, internalFormat, width);
        GL45.glTextureStorage1D(textureId, levels, internalFormat, width);
        assert checkGLError() : glErrorMsg("glTextureStorage1d(IIII)", textureId, levels, GLTextureInternalFormat.of(internalFormat).get(), width);
    }

    @Override
    public void glTextureStorage2d(int textureId, int levels, int internalFormat, int width, int height) {
        assert checkId(textureId) : invalidTextureIdMsg(textureId);
        assert checkMipmapDefine(levels) : invalidMipmapDefineMsg(levels);
        assert checkGLenum(internalFormat, GLTextureInternalFormat::of) : invalidGLenumMsg(internalFormat);
        assert checkDimension(width) : invalidWidthMsg(width);
        assert checkDimension(height) : invalidHeightMsg(height);

        LOGGER.trace(GL_MARKER, "glTextureStorage2D({}, {}, {}, {}, {})", textureId, levels, internalFormat, width, height);
        GL45.glTextureStorage2D(textureId, levels, internalFormat, width, height);
        assert checkGLError() : glErrorMsg("glTextureStorage2D(IIIII)", textureId, levels, GLTextureInternalFormat.of(internalFormat).get(), width, height);
    }

    @Override
    public void glTextureStorage3d(int textureId, int levels, int internalFormat, int width, int height, int depth) {
        assert checkId(textureId) : invalidTextureIdMsg(textureId);
        assert checkMipmapDefine(levels) : invalidMipmapDefineMsg(levels);
        assert checkGLenum(internalFormat, GLTextureInternalFormat::of) : invalidGLenumMsg(internalFormat);
        assert checkDimension(width) : invalidWidthMsg(width);
        assert checkDimension(height) : invalidHeightMsg(height);
        assert checkDimension(depth) : invalidDepthMsg(depth);

        LOGGER.trace(GL_MARKER, "glTextureStorage3D({}, {}, {}, {}, {}, {})", textureId, levels, internalFormat, width, height, depth);
        GL45.glTextureStorage3D(textureId, levels, internalFormat, width, height, depth);
        assert checkGLError() : glErrorMsg("glTextureStorage3D(IIIIII)", textureId, levels, GLTextureInternalFormat.of(internalFormat).get(), width, height, depth);
    }

    @Override
    public void glGenerateTextureMipmap(int textureId) {
        assert checkId(textureId) : invalidTextureIdMsg(textureId);

        LOGGER.trace(GL_MARKER, "glGenerateTextureMipmap({})", textureId);
        GL45.glGenerateTextureMipmap(textureId);
        assert checkGLError() : glErrorMsg("glGenerateTextureMipmap(I)", textureId);
    }

    @Override
    public void glNamedFramebufferTexture(int framebuffer, int attachment, int texture, int level) {
        assert checkId(framebuffer) : invalidFramebufferIdMsg(framebuffer);
        assert checkId(texture) : invalidTextureIdMsg(texture);
        assert checkMipmapLevel(level) : invalidMipmapLevelMsg(level);

        LOGGER.trace(GL_MARKER, "glNamedFramebufferTexture({}, {}, {}, {})", framebuffer, attachment, texture, level);
        GL45.glNamedFramebufferTexture(framebuffer, attachment, texture, level);
        assert checkGLError() : glErrorMsg("glNamedFramebufferTexture(IIII)", framebuffer, attachment, texture, level);
    }

    @Override
    public void glNamedBufferReadPixels(int bufferId, int x, int y, int width, int height, int format, int type, long ptr) {
        FakeDSA.getInstance().glNamedBufferReadPixels(bufferId, x, y, width, height, format, type, ptr);
    }

    @Override
    public void glBlitNamedFramebuffer(int readFramebuffer, int drawFramebuffer, int srcX0, int srcY0, int srcX1, int srcY1, int dstX0, int dstY0, int dstX1, int dstY1, int mask, int filter) {
        assert checkNullableId(readFramebuffer) : invalidFramebufferIdMsg(readFramebuffer);
        assert checkNullableId(drawFramebuffer) : invalidFramebufferIdMsg(drawFramebuffer);

        LOGGER.trace(GL_MARKER, "glBlitNamedFramebuffer({}, {}, {}, {}, {}, {}, {}, {}, {}, {}, {}, {})", readFramebuffer, drawFramebuffer, srcX0, srcY0, srcX1, srcY1, dstX0, dstY0, dstX1, dstY1, mask, filter);
        GL45.glBlitNamedFramebuffer(readFramebuffer, drawFramebuffer, srcX0, srcY0, srcX1, srcY1, dstX0, dstY0, dstX1, dstY1, mask, filter);
        assert checkGLError() : glErrorMsg("glBlitNamedFramebuffer(IIIIIIIIIIII)", readFramebuffer, drawFramebuffer, srcX0, srcY0, srcX1, srcY1, dstX0, dstX1, dstY0, dstY1, mask, filter);
    }

    @Override
    public void glGetTextureImage(int texture, int level, int format, int type, int bufferSize, ByteBuffer pixels) {
        assert checkId(texture) : invalidTextureIdMsg(texture);
        assert checkMipmapLevel(level) : invalidMipmapLevelMsg(level);
        assert checkGLenum(format, GLTextureFormat::of) : invalidGLenumMsg(format);
        assert checkGLenum(type, GLType::of) : invalidGLenumMsg(type);
        assert checkSize(bufferSize) : invalidSizeMsg(bufferSize);
        assert checkBufferIsNative(pixels) : bufferIsNotNativeMsg(pixels);
        assert pixels.isDirect() : NON_DIRECT_BUFFER_MSG;
        assert checkBufferSize(bufferSize, 1, 1, format, type, pixels) : bufferTooSmallMsg(bufferSize, 1, 1, format, type, pixels);

        LOGGER.trace(GL_MARKER, "glGetTextureImage({}, {}, {}, {}, {}, {})", texture, level, format, type, bufferSize, pixels);
        GL45.glGetTextureImage(texture, level, format, type, bufferSize, pixels);
        assert checkGLError() : glErrorMsg("glGetTextureImage(IIIII*)", texture, level, GLTextureFormat.of(format).get(), GLType.of(type).get(), bufferSize, toHexString(memAddress(pixels)));
    }

    private static class Holder {

        private static final DSADriver INSTANCE = new GL45DSA();
    }
}
