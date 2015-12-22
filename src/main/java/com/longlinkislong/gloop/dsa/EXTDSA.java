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
import java.nio.DoubleBuffer;
import java.nio.FloatBuffer;
import org.lwjgl.opengl.ARBBufferStorage;
import org.lwjgl.opengl.ARBSeparateShaderObjects;
import org.lwjgl.opengl.ContextCapabilities;
import org.lwjgl.opengl.EXTDirectStateAccess;
import org.lwjgl.opengl.GL;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL12;
import org.lwjgl.opengl.GL41;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.Marker;
import org.slf4j.MarkerFactory;
import static org.lwjgl.system.MemoryUtil.memAddress;

/**
 *
 * @author zmichaels
 */
public final class EXTDSA extends Common implements EXTDSADriver {
    private static final Marker GL_MARKER = MarkerFactory.getMarker("OPENGL");
    private static final Logger LOGGER = LoggerFactory.getLogger("OPENGL");

    @Override
    public void glGetTextureImage(int texture, int target, int level, int format, int type, int bufferSize, ByteBuffer pixels) {
        assert checkId(texture) : invalidTextureIdMsg(texture);
        assert checkGLenum(target, GLTextureTarget::of) : invalidGLenumMsg(target);
        assert checkMipmapLevel(level) : invalidMipmapLevelMsg(level);
        assert checkGLenum(format, GLTextureFormat::of) : invalidGLenumMsg(format);
        assert checkGLenum(type, GLType::of) : invalidGLenumMsg(type);
        assert checkBufferIsNative(pixels) : bufferIsNotNativeMsg(pixels);
        assert pixels.isDirect() : NON_DIRECT_BUFFER_MSG;
        assert checkBufferSize(bufferSize, 1, 1, format, type, pixels) : bufferTooSmallMsg(bufferSize, 1, 1, format, type, pixels);

        LOGGER.trace(GL_MARKER, "glGetTextureImageEXT({}, {}, {}, {}, {}, {})", texture, target, level, format, type, pixels);
        EXTDirectStateAccess.glGetTextureImageEXT(texture, target, level, format, type, pixels);
        
        assert checkGLError() : glErrorMsg("glGetTextureImageEXT(IIIII*)", texture, GLTextureTarget.of(target).get(), level, GLTextureFormat.of(format).get(), GLType.of(type).get(), toHexString(memAddress(pixels)));
    }

    @Override
    public String toString() {
        return "EXTDSA";
    }

    @Override
    public boolean isSupported() {
        final ContextCapabilities cap = GL.getCapabilities();

        return cap.GL_EXT_direct_state_access && FakeDSA.getInstance().isSupported();
    }

    @Override
    public void glNamedBufferData(int bufferId, long size, int usage) {
        assert checkId(bufferId) : invalidBufferIdMsg(bufferId);
        assert checkSize(size) : invalidSizeMsg(size);
        assert checkGLenum(usage, GLBufferUsage::of) : invalidGLenumMsg(usage);

        LOGGER.trace(GL_MARKER, "glNamedBufferDataEXT({}, {}, {})", bufferId, size, usage);
        
        EXTDirectStateAccess.glNamedBufferDataEXT(bufferId, size, usage);
        assert checkGLError() : glErrorMsg("glNamedBufferDataEXT(ILI)", bufferId, size, GLBufferUsage.of(usage).get());
    }

    @Override
    public void glNamedBufferData(int bufferId, ByteBuffer data, int usage) {
        assert checkId(bufferId) : invalidBufferIdMsg(bufferId);
        assert checkBufferIsNative(data) : bufferIsNotNativeMsg(data);
        assert data.isDirect() : NON_DIRECT_BUFFER_MSG;
        assert checkGLenum(usage, GLBufferUsage::of) : invalidGLenumMsg(usage);

        LOGGER.trace(GL_MARKER, "glNamedBufferDataEXT({}, {}, {})", bufferId, data, usage);
        EXTDirectStateAccess.glNamedBufferDataEXT(bufferId, data, usage);
        assert checkGLError() : glErrorMsg("glNamedBufferDataEXT(I*I)", bufferId, toHexString(memAddress(data)), GLBufferUsage.of(usage).get());
    }

    @Override
    public void glNamedBufferSubData(int buffer, long offset, ByteBuffer data) {
        assert checkId(buffer) : invalidBufferIdMsg(buffer);
        assert checkOffset(offset) : invalidOffsetMsg(offset);
        assert checkBufferIsNative(data) : bufferIsNotNativeMsg(data);
        assert data.isDirect() : NON_DIRECT_BUFFER_MSG;

        LOGGER.trace(GL_MARKER, "glNamedBufferSubDataEXT({}, {}, {})", buffer, offset, data);
        EXTDirectStateAccess.glNamedBufferSubDataEXT(buffer, offset, data);
        assert checkGLError() : glErrorMsg("glNamedBufferSubDataEXT(IL*)", buffer, offset, toHexString(memAddress(data)));
    }

    @Override
    public void glGetNamedBufferSubData(int buffer, long offset, ByteBuffer out) {
        assert checkId(buffer) : invalidBufferIdMsg(buffer);
        assert checkOffset(offset) : invalidOffsetMsg(offset);
        assert checkBufferIsNative(out) : bufferIsNotNativeMsg(out);
        assert out.isDirect() : NON_DIRECT_BUFFER_MSG;

        LOGGER.trace(GL_MARKER, "glGetNamedBufferSubDataEXT({}, {}, {})", buffer, offset, out);
        EXTDirectStateAccess.glGetNamedBufferSubDataEXT(buffer, offset, out);
        assert checkGLError() : glErrorMsg("glGetNamedBufferSubDataEXT(IL*)", buffer, offset, toHexString(memAddress(out)));
    }

    @Override
    public int glGetNamedBufferParameteri(int bufferId, int pName) {
        assert checkId(bufferId) : invalidBufferIdMsg(bufferId);
        assert checkGLenum(pName, GLBufferParameterName::of) : invalidGLenumMsg(pName);

        LOGGER.trace(GL_MARKER, "glGetNamedBufferParameteriEXT({}, {})", bufferId, pName);
        
        final int rVal = EXTDirectStateAccess.glGetNamedBufferParameteriEXT(bufferId, pName);
        assert checkGLError() : glErrorMsg("glGetNamedBufferParameteriEXT(II)", bufferId, GLBufferParameterName.of(pName).get());

        return rVal;
    }

    @Override
    public ByteBuffer glMapNamedBufferRange(int bufferId, long offset, long length, int access, ByteBuffer recycled) {
        assert checkId(bufferId) : invalidBufferIdMsg(bufferId);
        assert checkOffset(offset) : invalidOffsetMsg(offset);
        assert checkSize(length) : invalidSizeMsg(length);

        LOGGER.trace(GL_MARKER, "glMapNamedBufferRangeEXT({}, {}, {}, {})", bufferId, offset, length, access);
        
        final ByteBuffer out = EXTDirectStateAccess.glMapNamedBufferRangeEXT(bufferId, offset, length, access, recycled);
        assert checkGLError() : glErrorMsg("glMapNamedBufferRangeEXT(ILLI)", bufferId, offset, length, access);

        return out;
    }

    @Override
    public void glUnmapNamedBuffer(int bufferId) {
        assert checkId(bufferId);

        LOGGER.trace(GL_MARKER, "glUnmapNamedBufferEXT({})", bufferId);
        EXTDirectStateAccess.glUnmapNamedBufferEXT(bufferId);
        assert checkGLError() : glErrorMsg("glUnmapNamedBufferEXT(I)", bufferId);
    }

    @Override
    public void glCopyNamedBufferSubData(int readBufferId, int writeBufferId, long readOffset, long writeOffset, long size) {
        assert checkId(readBufferId) : invalidBufferIdMsg(readBufferId);
        assert checkId(writeBufferId) : invalidBufferIdMsg(writeBufferId);
        assert checkOffset(readOffset) : invalidOffsetMsg(readOffset);
        assert checkOffset(writeOffset) : invalidOffsetMsg(writeOffset);
        assert checkSize(size) : invalidSizeMsg(size);

        LOGGER.trace(GL_MARKER, "glNamedCopyBufferSubDataEXT({}, {}, {}, {}, {})", readBufferId, writeBufferId, readOffset, writeOffset, size);
        EXTDirectStateAccess.glNamedCopyBufferSubDataEXT(readBufferId, writeBufferId, readOffset, writeOffset, size);
        assert checkGLError() : glErrorMsg("glNamedCopyBufferSubDataEXT(IILLL)", readBufferId, writeBufferId, readOffset, writeOffset, size);
    }

    @Override
    public void glProgramUniform1f(int programId, int location, float value) {
        assert checkId(programId) : invalidProgramIdMsg(programId);
        assert checkOffset(location) : invalidUniformLocationMsg(location);
        assert checkFloat(value) : invalidFloatMsg(value);

        LOGGER.trace(GL_MARKER, "glProgramUniform1fEXT({}, {}, {})", programId, location, value);
        EXTDirectStateAccess.glProgramUniform1fEXT(programId, location, value);
        assert checkGLError() : glErrorMsg("glProgramUniform1fEXT(IIF)", programId, location, value);
    }

    @Override
    public void glProgramUniform2f(int programId, int location, float v0, float v1) {
        assert checkId(programId) : invalidProgramIdMsg(programId);
        assert checkOffset(location) : invalidUniformLocationMsg(location);
        assert checkFloat(v0) : invalidFloatMsg(v0);
        assert checkFloat(v1) : invalidFloatMsg(v1);

        LOGGER.trace(GL_MARKER, "glProgramUniform2fEXT({}, {}, {}, {})", programId, location, v0, v1);
        EXTDirectStateAccess.glProgramUniform2fEXT(programId, location, v0, v1);
        assert checkGLError() : glErrorMsg("glProgramUniform2fEXT(IIFF)", programId, location, v0, v1);
    }

    @Override
    public void glProgramUniform3f(int programId, int location, float v0, float v1, float v2) {
        assert checkId(programId) : invalidProgramIdMsg(programId);
        assert checkOffset(location) : invalidUniformLocationMsg(location);
        assert checkFloat(v0) : invalidFloatMsg(v0);
        assert checkFloat(v1) : invalidFloatMsg(v1);
        assert checkFloat(v2) : invalidFloatMsg(v2);

        LOGGER.trace(GL_MARKER, "glProgramUniform3fEXT({}, {}, {}, {}, {})", programId, location, v0, v1, v2);
        EXTDirectStateAccess.glProgramUniform3fEXT(programId, location, v0, v1, v2);
        assert checkGLError() : glErrorMsg("glProgramUniform3fEXT(IIFFF)", programId, location, v0, v1, v2);
    }

    @Override
    public void glProgramUniform4f(int programId, int location, float v0, float v1, float v2, float v3) {
        assert checkId(programId) : invalidProgramIdMsg(programId);
        assert checkOffset(location) : invalidUniformLocationMsg(location);
        assert checkFloat(v0) : invalidFloatMsg(v0);
        assert checkFloat(v1) : invalidFloatMsg(v1);
        assert checkFloat(v2) : invalidFloatMsg(v2);
        assert checkFloat(v3) : invalidFloatMsg(v3);

        LOGGER.trace(GL_MARKER, "glProgramUniform4fEXT({}, {}, {}, {}, {}, {})", programId, location, v0, v1, v2, v3);        
        EXTDirectStateAccess.glProgramUniform4fEXT(programId, location, v0, v1, v2, v3);
        assert checkGLError() : glErrorMsg("glProgramUniform4fEXT(IIFFFF)", programId, location, v0, v1, v2, v3);
    }

    @Override
    public void glProgramUniform1i(int programId, int location, int value) {
        assert checkId(programId) : invalidProgramIdMsg(programId);
        assert checkOffset(location) : invalidUniformLocationMsg(location);

        LOGGER.trace(GL_MARKER, "glProgramUniform1iEXT({}, {}, {})", programId, location, value);
        EXTDirectStateAccess.glProgramUniform1iEXT(programId, location, value);
        assert checkGLError() : glErrorMsg("glProgramUniform1iEXT(III)", programId, location, value);
    }

    @Override
    public void glProgramUniform2i(int programId, int location, int v0, int v1) {
        assert checkId(programId) : invalidProgramIdMsg(programId);
        assert checkOffset(location) : invalidUniformLocationMsg(location);

        LOGGER.trace(GL_MARKER, "glProgramUniform2iEXT({}, {}, {}, {})", programId, location, v0, v1);
        EXTDirectStateAccess.glProgramUniform2iEXT(programId, location, v0, v1);
        assert checkGLError() : glErrorMsg("glProgramUniform2iEXT(IIII)", programId, location, v0, v1);
    }

    @Override
    public void glProgramUniform3i(int programId, int location, int v0, int v1, int v2) {
        assert checkId(programId) : invalidProgramIdMsg(programId);
        assert checkOffset(location) : invalidUniformLocationMsg(location);

        LOGGER.trace(GL_MARKER, "glProgramUniform3iEXT({}, {}, {}, {}, {})", programId, location, v0, v1, v2);
        EXTDirectStateAccess.glProgramUniform3iEXT(programId, location, v0, v1, v2);
        assert checkGLError() : glErrorMsg("glProgramUniform3iEXT(IIIII)", programId, location, v0, v1, v2);
    }

    @Override
    public void glProgramUniform4i(int programId, int location, int v0, int v1, int v2, int v3) {
        assert checkId(programId) : invalidProgramIdMsg(programId);
        assert checkOffset(location) : invalidUniformLocationMsg(location);

        LOGGER.trace(GL_MARKER, "glProgramUniform4iEXT({}, {}, {}, {}, {}, {})", programId, location, v0, v1, v2, v3);
        EXTDirectStateAccess.glProgramUniform4iEXT(programId, location, v0, v1, v2, v3);
        assert checkGLError() : glErrorMsg("glProgramUniform4iEXT(IIIIII)", programId, location, v0, v1, v2, v3);
    }

    @Override
    public void glProgramUniformMatrix2f(int programId, int location, boolean needsTranspose, FloatBuffer data) {
        assert checkId(programId) : invalidProgramIdMsg(programId);
        assert checkOffset(location) : invalidUniformLocationMsg(location);
        assert data.isDirect() : NON_DIRECT_BUFFER_MSG;

        LOGGER.trace(GL_MARKER, "glProgramUniformMatrix2fvEXT({}, {}, {}, {})", programId, location, needsTranspose, data);
        EXTDirectStateAccess.glProgramUniformMatrix2fvEXT(programId, location, needsTranspose, data);
        assert checkGLError() : glErrorMsg("glProgramUniformMatrix2fvEXT(IIB*)", programId, location, needsTranspose, toHexString(memAddress(data)));
    }

    @Override
    public void glProgramUniformMatrix3f(int programId, int location, boolean needsTranspose, FloatBuffer data) {
        assert checkId(programId) : invalidProgramIdMsg(programId);
        assert checkOffset(location) : invalidUniformLocationMsg(location);
        assert data.isDirect() : NON_DIRECT_BUFFER_MSG;

        LOGGER.trace(GL_MARKER, "glProgramUniformMatrix3fvEXT({}, {}, {}, {})", programId, location, needsTranspose, data);
        EXTDirectStateAccess.glProgramUniformMatrix3fvEXT(programId, location, needsTranspose, data);
        assert checkGLError() : glErrorMsg("glProgramUniformMatrix3fvEXT(IIB*)", programId, location, needsTranspose, toHexString(memAddress(data)));
    }

    @Override
    public void glProgramUniformMatrix4f(int programId, int location, boolean needsTranspose, FloatBuffer data) {
        assert checkId(programId) : invalidProgramIdMsg(programId);
        assert checkOffset(location) : invalidUniformLocationMsg(location);
        assert data.isDirect() : NON_DIRECT_BUFFER_MSG;

        LOGGER.trace(GL_MARKER, "glProgramUniformMatrix4fvEXT({}, {}, {}, {})", programId, location, needsTranspose, data);
        EXTDirectStateAccess.glProgramUniformMatrix4fvEXT(programId, location, needsTranspose, data);
        assert checkGLError() : glErrorMsg("glProgramUniformMatrix4fvEXT(IIB*)", programId, location, needsTranspose, toHexString(memAddress(data)));
    }

    @Override
    public void glTextureParameteri(int textureId, int target, int pName, int val) {
        assert checkId(textureId) : invalidTextureIdMsg(textureId);
        assert checkGLenum(target, GLTextureTarget::of) : invalidGLenumMsg(target);

        LOGGER.trace(GL_MARKER, "glTextureParameteri({}, {}, {}, {})", textureId, target, pName, val);
        EXTDirectStateAccess.glTextureParameteriEXT(textureId, target, pName, val);
        assert checkGLError() : glErrorMsg("glTextureParameteriEXT(IIII)", textureId, GLTextureTarget.of(target).get(), pName, val);
    }

    @Override
    public void glTextureParameterf(int textureId, int target, int pName, float val) {
        assert checkId(textureId) : invalidTextureIdMsg(textureId);
        assert checkGLenum(target, GLTextureTarget::of) : invalidGLenumMsg(target);
        assert checkFloat(val) : invalidFloatMsg(val);

        LOGGER.trace(GL_MARKER, "glTextureParameterfEXT({}, {}, {}, {})", textureId, target, pName, val);
        EXTDirectStateAccess.glTextureParameterfEXT(textureId, target, pName, val);
        assert checkGLError() : glErrorMsg("glTextureParameterfEXT(IIIF)", textureId, target, pName, val);
    }

    @Override
    public void glTextureSubImage1d(int textureId, int level, int xOffset, int width, int format, int type, ByteBuffer pixels) {
        assert checkId(textureId) : invalidTextureIdMsg(textureId);
        assert checkMipmapLevel(level) : invalidMipmapLevelMsg(level);
        assert checkOffset(xOffset) : invalidXOffsetMsg(xOffset);
        assert checkDimension(width) : invalidWidthMsg(width);
        assert checkGLenum(format, GLTextureFormat::of) : invalidGLenumMsg(format);
        assert checkGLenum(type, GLType::of) : invalidGLenumMsg(type);
        assert checkBufferSize(width, 1, 1, format, type, pixels) : bufferTooSmallMsg(width, 1, 1, format, type, pixels);

        LOGGER.trace(GL_MARKER, "glTextureSubImage1DEXT({}, GL_TEXTURE_1D, {}, {}, {}, {}, {}, {})", textureId, level, xOffset, width, format, type, pixels);
        EXTDirectStateAccess.glTextureSubImage1DEXT(textureId, GL11.GL_TEXTURE_1D, level, xOffset, width, format, type, pixels);
        assert checkGLError() : glErrorMsg("glTextureSubImage1DEXT(IIIIII*)", textureId, "GL_TEXTURE_1D", level, xOffset, width, GLTextureFormat.of(format).get(), GLType.of(type).get(), toHexString(memAddress(pixels)));
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
        assert checkBufferSize(width, height, 1, format, type, pixels) : bufferTooSmallMsg(width, height, 1, format, type, pixels);

        LOGGER.trace(GL_MARKER, "glTextureSubImage2DEXT({}, GL_TEXTURE_2D, {}, {}, {}, {}, {}, {}, {}, {})", textureId, level, xOffset, yOffset, width, height, format, type, pixels);
        EXTDirectStateAccess.glTextureSubImage2DEXT(textureId, GL11.GL_TEXTURE_2D, level, xOffset, yOffset, width, height, format, type, pixels);
        assert checkGLError() : glErrorMsg("glTextureSubImage2DEXT(IIIIIIII*)", textureId, "GL_TEXTURE_2D", level, xOffset, yOffset, width, height, GLTextureFormat.of(format).get(), GLType.of(type).get(), toHexString(memAddress(pixels)));
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
        assert checkBufferSize(width, height, depth, format, type, pixels) : bufferTooSmallMsg(width, height, depth, format, type, pixels);

        LOGGER.trace("glTextureSubImage3DEXT({}, GL_TEXTURE_3D, {}, {}, {}, {}, {}, {}, {}, {}, {}, {})", textureId, level, xOffset, yOffset, zOffset, width, height, depth, format, type, pixels);
        EXTDirectStateAccess.glTextureSubImage3DEXT(textureId, GL12.GL_TEXTURE_3D, level, xOffset, yOffset, zOffset, width, height, depth, format, type, pixels);
        assert checkGLError() : glErrorMsg("glTextureSubImage3DEXT(IIIIIIIIII*)", textureId, "GL_TEXTURE_3D", level, xOffset, yOffset, zOffset, width, height, depth, GLTextureFormat.of(format).get(), GLType.of(type).get(), toHexString(memAddress(pixels)));
    }

    @Override
    public void glBindTextureUnit(int unit, int target, int textureId) {
        assert checkNullableId(unit) : invalidTextureUnitMsg(unit);
        assert checkGLenum(target, GLTextureTarget::of) : invalidGLenumMsg(target);
        assert checkId(textureId) : invalidTextureIdMsg(textureId);

        LOGGER.trace("glBindMultiTextureEXT({}, {}, {})", unit, target, textureId);
        EXTDirectStateAccess.glBindMultiTextureEXT(unit, target, textureId);
        assert checkGLError() : glErrorMsg("glBindMultiTextureEXT(III)", unit, GLTextureTarget.of(target).get(), textureId);
    }

    @Override
    public void glTextureImage1d(int texture, int target, int level, int internalFormat, int width, int border, int format, int type, ByteBuffer pixels) {
        assert checkId(texture) : invalidTextureIdMsg(texture);
        assert checkGLenum(target, GLTextureTarget::of) : invalidGLenumMsg(target);
        assert checkMipmapLevel(level) : invalidMipmapLevelMsg(level);
        assert checkGLenum(internalFormat, GLTextureInternalFormat::of) : invalidGLenumMsg(internalFormat);
        assert checkDimension(width) : invalidWidthMsg(width);
        assert border == 0 : "Border must be set to 0.";
        assert checkGLenum(format, GLTextureFormat::of) : invalidGLenumMsg(format);
        assert checkGLenum(type, GLType::of) : invalidGLenumMsg(type);
        assert checkBufferSize(width, 1, 1, format, type, pixels) : bufferTooSmallMsg(width, 1, 1, format, type, pixels);

        LOGGER.trace(GL_MARKER, "glTextureImage1DEXT({}, {}, {}, {}, {}, {}, {}, {}, {})", texture, target, level, internalFormat, width, border, format, type, pixels);
        EXTDirectStateAccess.glTextureImage1DEXT(texture, target, level, internalFormat, width, border, format, type, pixels);
        assert checkGLError() : glErrorMsg("glTextureImage1DEXT(IIIIIIII*)", texture, target, level, GLTextureInternalFormat.of(internalFormat).get(), width, border, GLTextureFormat.of(format).get(), GLType.of(type).get(), toHexString(memAddress(pixels)));
    }

    @Override
    public void glTextureImage2d(int texture, int target, int level, int internalFormat, int width, int height, int border, int format, int type, ByteBuffer pixels) {
        assert checkId(texture) : invalidTextureIdMsg(texture);
        assert checkGLenum(target, GLTextureTarget::of) : invalidGLenumMsg(target);
        assert checkMipmapLevel(level) : invalidMipmapLevelMsg(level);
        assert checkGLenum(internalFormat, GLTextureInternalFormat::of) : invalidGLenumMsg(internalFormat);
        assert checkDimension(width) : invalidWidthMsg(width);
        assert checkDimension(height) : invalidHeightMsg(height);
        assert border == 0 : " Border must be set to 0.";
        assert checkGLenum(format, GLTextureFormat::of) : invalidGLenumMsg(format);
        assert checkGLenum(type, GLType::of) : invalidGLenumMsg(type);
        assert checkBufferSize(width, height, 1, format, type, pixels) : bufferTooSmallMsg(width, height, 1, format, type, pixels);

        LOGGER.trace(GL_MARKER, "glTextureImage2DEXT({}, {}, {}, {}, {}, {}, {}, {}, {}, {})", texture, target, level, internalFormat, width, height, format, border, type, pixels);
        EXTDirectStateAccess.glTextureImage2DEXT(texture, target, level, internalFormat, width, height, format, border, type, pixels);
        assert checkGLError() : glErrorMsg("glTextureImage2DEXT(IIIIIIIII*)", texture, target, level, GLTextureInternalFormat.of(internalFormat).get(), width, height, GLTextureFormat.of(format).get(), border, GLType.of(type).get(), toHexString(memAddress(pixels)));
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
        assert border == 0 : "Border must be set to 0.";
        assert checkGLenum(format, GLTextureFormat::of) : invalidGLenumMsg(format);
        assert checkGLenum(type, GLType::of) : invalidGLenumMsg(type);
        assert checkBufferSize(width, height, depth, format, type, pixels) : bufferTooSmallMsg(width, height, depth, format, type, pixels);

        LOGGER.trace(GL_MARKER, "glTextureImage3DEXT({}, {}, {}, {}, {}, {}, {}, {}, {}, {}, {})", texture, target, level, internalFormat, width, height, depth, border, format, type, pixels);
        EXTDirectStateAccess.glTextureImage3DEXT(texture, target, level, internalFormat, width, height, depth, border, format, type, pixels);
        assert checkGLError() : glErrorMsg("glTextureImage3DEXT(IIIIIIIIII*)", texture, GLTextureTarget.of(target).get(), level, GLTextureInternalFormat.of(internalFormat).get(), width, height, depth, border, GLTextureFormat.of(format).get(), GLType.of(type).get(), toHexString(memAddress(pixels)));
    }

    @Override
    public void glTextureImage1d(int texture, int target, int level, int internalFormat, int width, int border, int format, int type, long ptr) {
        assert checkId(texture) : invalidTextureIdMsg(texture);
        assert checkGLenum(target, GLTextureTarget::of) : invalidGLenumMsg(target);
        assert checkMipmapLevel(level) : invalidMipmapLevelMsg(level);
        assert checkGLenum(internalFormat, GLTextureInternalFormat::of) : invalidGLenumMsg(internalFormat);
        assert checkDimension(width) : invalidWidthMsg(width);
        assert border == 0 : "Border must be set to 0.";

        LOGGER.trace(GL_MARKER, "glTextureImage1DEXT({}, {}, {}, {}, {}, {}, {}, {}, {})", texture, target, level, internalFormat, width, border, format, type, ptr);
        EXTDirectStateAccess.glTextureImage1DEXT(texture, target, level, internalFormat, width, border, format, type, ptr);
        assert checkGLError() : glErrorMsg("glTextureImage1DEXT(IIIIIIIIL)", texture, GLTextureTarget.of(target).get(), level, GLTextureInternalFormat.of(internalFormat).get(), width, border, GLTextureFormat.of(format).get(), GLType.of(type).get(), ptr);
    }

    @Override
    public void glTextureImage2d(int texture, int target, int level, int internalFormat, int width, int height, int border, int format, int type, long ptr) {
        assert checkId(texture) : invalidTextureIdMsg(texture);
        assert checkGLenum(target, GLTextureTarget::of) : invalidGLenumMsg(target);
        assert checkMipmapLevel(level) : invalidMipmapLevelMsg(level);
        assert checkGLenum(internalFormat, GLTextureInternalFormat::of) : invalidGLenumMsg(internalFormat);
        assert checkDimension(width) : invalidWidthMsg(width);
        assert checkDimension(height) : invalidHeightMsg(height);
        assert border == 0 : "Border must be set to 0.";
        assert checkGLenum(format, GLTextureFormat::of) : invalidGLenumMsg(format);
        assert checkGLenum(type, GLType::of) : invalidGLenumMsg(type);

        LOGGER.trace(GL_MARKER, "glTextureImage2DEXT({}, {}, {}, {}, {}, {}, {}, {}, {}, {})", texture, target, level, internalFormat, width, height, border, format, type, ptr);
        EXTDirectStateAccess.glTextureImage2DEXT(texture, target, level, internalFormat, width, height, border, format, type, ptr);
        assert checkGLError() : glErrorMsg("glTextureImage2DEXT(IIIIIIIIIL)", texture, GLTextureTarget.of(target).get(), GLTextureInternalFormat.of(internalFormat).get(), width, height, border, GLTextureFormat.of(format).get(), GLType.of(type).get(), ptr);
    }

    @Override
    public void glTextureImage3d(int texture, int target, int level, int internalFormat, int width, int height, int depth, int border, int format, int type, long ptr) {
        assert checkId(texture) : invalidTextureIdMsg(texture);
        assert checkGLenum(target, GLTextureTarget::of) : invalidGLenumMsg(target);
        assert checkMipmapLevel(level) : invalidMipmapLevelMsg(level);
        assert checkGLenum(internalFormat, GLTextureInternalFormat::of) : invalidGLenumMsg(internalFormat);
        assert checkDimension(width) : invalidWidthMsg(width);
        assert checkDimension(height) : invalidHeightMsg(height);
        assert checkDimension(depth) : invalidDepthMsg(depth);
        assert border == 0 : "Border must be set to 0.";
        assert checkGLenum(format, GLTextureFormat::of) : invalidGLenumMsg(format);
        assert checkGLenum(type, GLType::of) : invalidGLenumMsg(type);

        LOGGER.trace(GL_MARKER, "glTextureImage2DEXT({}, {}, {}, {}, {}, {}, {}, {}, {}, {}, {})", texture, target, level, internalFormat, width, height, depth, border, format, type, ptr);
        EXTDirectStateAccess.glTextureImage3DEXT(texture, target, level, internalFormat, width, height, depth, border, format, type, ptr);
        assert checkGLError() : glErrorMsg("glTextureImage3DEXT(IIIIIIIIIIL)", texture, GLTextureTarget.of(target).get(), level, GLTextureInternalFormat.of(internalFormat).get(), width, height, depth, border, GLTextureFormat.of(format).get(), GLType.of(type).get(), ptr);
    }

    @Override
    public void glGenerateTextureMipmap(int texture, int target) {
        assert checkId(texture) : invalidTextureIdMsg(texture);
        assert checkGLenum(target, GLTextureTarget::of) : invalidGLenumMsg(target);

        LOGGER.trace(GL_MARKER, "glGenerateTextureMipmapEXT({}, {})", texture, target);
        EXTDirectStateAccess.glGenerateTextureMipmapEXT(texture, target);
        assert checkGLError() : glErrorMsg("glGenerateTextureMipmapEXT(II)", texture, GLTextureTarget.of(target).get());
    }

    public static DSADriver getInstance() {
        return Holder.INSTANCE;
    }

    @Override
    public int glCreateBuffers() {
        return FakeDSA.getInstance().glCreateBuffers();
    }

    @Override
    public int glCreateTextures(int target) {
        return FakeDSA.getInstance().glCreateTextures(target);
    }

    @Override
    public int glCreateFramebuffers() {
        return FakeDSA.getInstance().glCreateFramebuffers();
    }

    @Override
    public void glNamedBufferStorage(int bufferId, ByteBuffer data, int flags) {
        assert checkId(bufferId) : invalidBufferIdMsg(bufferId);
        assert checkBufferIsNative(data) : bufferIsNotNativeMsg(data);
        assert data.isDirect() : NON_DIRECT_BUFFER_MSG;

        final ContextCapabilities cap = GL.getCapabilities();

        if (cap.GL_ARB_buffer_storage) {
            LOGGER.trace(GL_MARKER, "glNamedBufferStorageEXT({}, {}, {})", bufferId, data, flags);
            ARBBufferStorage.glNamedBufferStorageEXT(bufferId, data, flags);
            assert checkGLError() : glErrorMsg("glNamedBufferStorageEXT(I*I)", bufferId, toHexString(memAddress(data)), flags);
        } else {            
            FakeDSA.getInstance().glNamedBufferStorage(bufferId, data, flags);
        }
    }

    @Override
    public void glNamedBufferStorage(int bufferId, long size, int flags) {
        assert checkId(bufferId) : invalidBufferIdMsg(bufferId);
        assert checkSize(size) : invalidSizeMsg(size);

        final ContextCapabilities cap = GL.getCapabilities();

        if (cap.GL_ARB_buffer_storage) {
            LOGGER.trace(GL_MARKER, "glNamedBufferStorageEXT({}, {}, {})", bufferId, size, flags);
            ARBBufferStorage.glNamedBufferStorageEXT(bufferId, size, flags);
            assert checkGLError() : glErrorMsg("glNamedBufferStorageEXT(ILI)", bufferId, size, flags);
        } else {
            FakeDSA.getInstance().glNamedBufferStorage(bufferId, size, flags);
        }
    }

    @Override
    public void glProgramUniform1d(int programId, int location, double value) {
        assert checkId(programId) : invalidProgramIdMsg(programId);
        assert checkOffset(location) : invalidUniformLocationMsg(location);
        assert checkDouble(value) : invalidDoubleMsg(value);

        final ContextCapabilities cap = GL.getCapabilities();

        if (cap.OpenGL41) {
            LOGGER.trace(GL_MARKER, "glProgramUniform1d({}, {}, {})", programId, location, value);
            GL41.glProgramUniform1d(programId, location, value);
            assert checkGLError() : glErrorMsg("glProgramUniform1d(IID)", programId, location, value);
        } else if (cap.GL_ARB_separate_shader_objects) {
            LOGGER.trace(GL_MARKER, "glProgramUniform1d({}, {}, {}) (ARB)", programId, location, value);
            ARBSeparateShaderObjects.glProgramUniform1d(programId, location, value);
            assert checkGLError() : glErrorMsg("glProgramUniform1dARB(IID)", programId, location, value);
        } else {
            FakeDSA.getInstance().glProgramUniform1d(programId, location, value);
        }
    }

    @Override
    public void glProgramUniform2d(int programId, int location, double v0, double v1) {
        assert checkId(programId) : invalidProgramIdMsg(programId);
        assert checkOffset(location) : invalidUniformLocationMsg(location);
        assert checkDouble(v0) : invalidDoubleMsg(v0);
        assert checkDouble(v1) : invalidDoubleMsg(v1);

        final ContextCapabilities cap = GL.getCapabilities();

        if (cap.OpenGL41) {
            LOGGER.trace(GL_MARKER, "glProgramUniform2d({}, {}, {}, {})", programId, location, v0, v1);
            GL41.glProgramUniform2d(programId, location, v0, v1);
            assert checkGLError() : glErrorMsg("glProgramUniform2d(IIDD)", programId, location, v0, v1);
        } else if (cap.GL_ARB_separate_shader_objects) {
            LOGGER.trace(GL_MARKER, "glProgramUniform2d({}, {}, {}, {}) (ARB)", programId, location, v0, v1);
            ARBSeparateShaderObjects.glProgramUniform2d(programId, location, v0, v1);
            assert checkGLError() : glErrorMsg("glProgramUniform2dARB(IIDD)", programId, location, v0, v1);
        } else {
            FakeDSA.getInstance().glProgramUniform2d(programId, location, v0, v1);
        }
    }

    @Override
    public void glProgramUniform3d(int programId, int location, double v0, double v1, double v2) {
        assert checkId(programId) : invalidProgramIdMsg(programId);
        assert checkOffset(location) : invalidUniformLocationMsg(location);
        assert checkDouble(v0) : invalidDoubleMsg(v0);
        assert checkDouble(v1) : invalidDoubleMsg(v1);
        assert checkDouble(v2) : invalidDoubleMsg(v2);

        final ContextCapabilities cap = GL.getCapabilities();

        if (cap.OpenGL41) {
            LOGGER.trace(GL_MARKER, "glProgramUniform3d({}, {}, {}, {}, {})", programId, location, v0, v1, v2);
            GL41.glProgramUniform3d(programId, location, v0, v1, v2);
            assert checkGLError() : glErrorMsg("glProgramUniform3d(IIDDD)", programId, location, v0, v1, v2);
        } else if (cap.GL_ARB_separate_shader_objects) {
            LOGGER.trace(GL_MARKER, "glProgramUniform3d({}, {}, {}, {}, {}) (ARB)", programId, location, v0, v1, v2);
            ARBSeparateShaderObjects.glProgramUniform3d(programId, location, v0, v1, v2);
            assert checkGLError() : glErrorMsg("glProgramUniform3dARB(IIDDD)", programId, location, v0, v1, v2);
        } else {
            FakeDSA.getInstance().glProgramUniform3d(programId, location, v0, v1, v2);
        }
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

        if (cap.OpenGL41) {
            LOGGER.trace(GL_MARKER, "glProgramUniform4d({}, {}, {}, {}, {}, {})", programId, location, v0, v1, v2, v3);
            GL41.glProgramUniform4d(programId, location, v0, v1, v2, v3);
            assert checkGLError() : glErrorMsg("glProgramUniform4d(IIDDDD)", programId, location, v0, v1, v2, v3);
        } else if (cap.GL_ARB_separate_shader_objects) {
            LOGGER.trace(GL_MARKER, "glProgramUniform4d({}, {}, {}, {}, {}, {}) (ARB)", programId, location, v0, v1, v2, v3);
            ARBSeparateShaderObjects.glProgramUniform4d(programId, location, v0, v1, v2, v3);
            assert checkGLError() : glErrorMsg("glProgramUniform4dARB(IIDDDD)", programId, location, v0, v1, v2, v3);
        } else {
            FakeDSA.getInstance().glProgramUniform4d(programId, location, v0, v1, v2, v3);
        }
    }

    @Override
    public void glProgramUniformMatrix2d(int programId, int location, boolean needsTranspose, DoubleBuffer data) {
        assert checkId(programId) : invalidProgramIdMsg(programId);
        assert checkOffset(location) : invalidUniformLocationMsg(location);
        assert data.isDirect() : NON_DIRECT_BUFFER_MSG;

        final ContextCapabilities cap = GL.getCapabilities();

        if (cap.OpenGL41) {
            LOGGER.trace(GL_MARKER, "glProgramUniform2dv({}, {}, {}, {})", programId, location, needsTranspose, data);
            GL41.glProgramUniformMatrix2dv(programId, location, needsTranspose, data);
            assert checkGLError() : glErrorMsg("glProgramUniformMatrix2dv(IIB*)", programId, location, needsTranspose, toHexString(memAddress(data)));
        } else if (cap.GL_ARB_separate_shader_objects) {
            LOGGER.trace(GL_MARKER, "glProgramUniformMatrix2dv({}, {}, {}, {})", programId, location, needsTranspose, data);
            ARBSeparateShaderObjects.glProgramUniformMatrix2dv(programId, location, needsTranspose, data);
            assert checkGLError() : glErrorMsg("glProgramUniformMatrix2dvARB(IIB*)", programId, location, needsTranspose, toHexString(memAddress(data)));
        } else {
            FakeDSA.getInstance().glProgramUniformMatrix2d(programId, location, needsTranspose, data);
        }
    }

    @Override
    public void glProgramUniformMatrix3d(int programId, int location, boolean needsTranspose, DoubleBuffer data) {
        assert checkId(programId) : invalidProgramIdMsg(programId);
        assert checkOffset(location) : invalidUniformLocationMsg(location);
        assert data.isDirect() : NON_DIRECT_BUFFER_MSG;

        final ContextCapabilities cap = GL.getCapabilities();

        if (cap.OpenGL41) {
            LOGGER.trace(GL_MARKER, "glProgramUniformMatrix3dv({}, {}, {}, {})", programId, location, needsTranspose, data);
            GL41.glProgramUniformMatrix3dv(programId, location, needsTranspose, data);
            assert checkGLError() : glErrorMsg("glProgramUniformMatrix3dv(IIB*)", programId, location, needsTranspose, toHexString(memAddress(data)));
        } else if (cap.GL_ARB_separate_shader_objects) {
            LOGGER.trace(GL_MARKER, "glProgramUniformMatrix3dv({}, {}, {}, {})", programId, location, needsTranspose, data);
            ARBSeparateShaderObjects.glProgramUniformMatrix3dv(programId, location, needsTranspose, data);
            assert checkGLError() : glErrorMsg("glProgramUniformMatrix3dvARB(IIB*)", programId, location, needsTranspose, toHexString(memAddress(data)));
        } else {
            FakeDSA.getInstance().glProgramUniformMatrix3d(programId, location, needsTranspose, data);
        }
    }

    @Override
    public void glProgramUniformMatrix4d(int programId, int location, boolean needsTranspose, DoubleBuffer data) {
        assert checkId(programId) : invalidProgramIdMsg(programId);
        assert checkOffset(location) : invalidUniformLocationMsg(location);
        assert data.isDirect() : NON_DIRECT_BUFFER_MSG;

        final ContextCapabilities cap = GL.getCapabilities();

        if (cap.OpenGL41) {
            LOGGER.trace(GL_MARKER, "glProgramUniformMatrix4dv({}, {}, {}, {})", programId, location, needsTranspose, data);
            GL41.glProgramUniformMatrix4dv(programId, location, needsTranspose, data);
            assert checkGLError() : glErrorMsg("glProgramUniformMatrix4dv(IIB*)", programId, location, needsTranspose, toHexString(memAddress(data)));
        } else if (cap.GL_ARB_separate_shader_objects) {
            LOGGER.trace(GL_MARKER, "glProgramUniformMatrix4dv({}, {}, {}, {}) (ARB)", programId, location, needsTranspose, data);
            ARBSeparateShaderObjects.glProgramUniformMatrix4dv(programId, location, needsTranspose, data);
            assert checkGLError() : glErrorMsg("glProgramUniformMatrix4dvARB(IIB*)", programId, location, needsTranspose, toHexString(memAddress(data)));
        } else {
            FakeDSA.getInstance().glProgramUniformMatrix4d(programId, location, needsTranspose, data);
        }
    }

    @Override
    public void glNamedFramebufferTexture1D(int framebuffer, int attachment, int texTarget, int texture, int level) {
        assert checkId(framebuffer) : invalidFramebufferIdMsg(framebuffer);
        assert checkGLenum(texTarget, GLTextureTarget::of) : invalidGLenumMsg(texTarget);
        assert checkId(texture) : invalidTextureIdMsg(texture);
        assert checkMipmapLevel(level) : invalidMipmapLevelMsg(level);

        LOGGER.trace(GL_MARKER, "glNamedFramebufferTexture1DEXT({}, {}, {}, {}, {})", framebuffer, attachment, texTarget, texture, level);
        EXTDirectStateAccess.glNamedFramebufferTexture1DEXT(framebuffer, attachment, texTarget, texture, level);
        assert checkGLError() : glErrorMsg("glNamedFramebufferTexture1DEXT(IIIII)", framebuffer, attachment, GLTextureTarget.of(texTarget).get(), texture, level);
    }

    @Override
    public void glNamedFramebufferTexture2D(int framebuffer, int attachment, int texTarget, int texture, int level) {
        assert checkId(framebuffer) : invalidFramebufferIdMsg(framebuffer);
        assert checkGLenum(texTarget, GLTextureTarget::of) : invalidGLenumMsg(texTarget);
        assert checkId(texture) : invalidTextureIdMsg(texture);
        assert checkMipmapLevel(level) : invalidMipmapLevelMsg(level);

        LOGGER.trace(GL_MARKER, "glNamedFramebufferTexture2DEXT({}, {}, {}, {}, {})", framebuffer, attachment, texTarget, texture, level);
        EXTDirectStateAccess.glNamedFramebufferTexture2DEXT(framebuffer, attachment, texTarget, texture, level);
        assert checkGLError() : glErrorMsg("glNamedFramebufferTexture2DEXT(IIIII)", framebuffer, attachment, GLTextureTarget.of(texTarget).get(), texture, level);
    }

    @Override
    public void glNamedFramebufferTexture(int framebuffer, int attachment, int texture, int level) {
        assert checkId(framebuffer) : invalidFramebufferIdMsg(framebuffer);
        assert checkId(texture) : invalidTextureIdMsg(texture);
        assert checkMipmapLevel(level) : invalidMipmapLevelMsg(level);

        LOGGER.trace(GL_MARKER, "glNamedFramebufferTextureEXT({}, {}, {}, {})", framebuffer, attachment, texture, level);
        EXTDirectStateAccess.glNamedFramebufferTextureEXT(framebuffer, attachment, texture, level);
        assert checkGLError() : glErrorMsg("glNamedFramebufferTextureEXT(IIII)", framebuffer, attachment, texture, level);
    }

    @Override
    public void glNamedBufferReadPixels(int bufferId, int x, int y, int width, int height, int format, int type, long ptr) {
        FakeDSA.getInstance().glNamedBufferReadPixels(bufferId, x, y, width, height, format, type, ptr);
    }

    @Override
    public void glBlitNamedFramebuffer(int readFramebuffer, int drawFramebuffer, int srcX0, int srcY0, int srcX1, int srcY1, int dstX0, int dstY0, int dstX1, int dstY1, int mask, int filter) {
        FakeDSA.getInstance().glBlitNamedFramebuffer(readFramebuffer, drawFramebuffer, srcX0, srcY0, srcX1, srcY1, dstX0, dstY0, dstX1, dstY1, mask, filter);
    }

    private static class Holder {

        private static final DSADriver INSTANCE = new EXTDSA();
    }
}
