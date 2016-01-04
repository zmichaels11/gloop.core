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
import static com.longlinkislong.gloop.GLAsserts.invalidNullableBufferIdMsg;
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
import com.longlinkislong.gloop.GLBufferUsage;
import com.longlinkislong.gloop.GLTextureFormat;
import com.longlinkislong.gloop.GLTextureInternalFormat;
import com.longlinkislong.gloop.GLTextureTarget;
import com.longlinkislong.gloop.GLType;
import static java.lang.Long.toHexString;
import java.nio.ByteBuffer;
import java.nio.DoubleBuffer;
import java.nio.FloatBuffer;
import org.lwjgl.opengl.ARBDirectStateAccess;
import org.lwjgl.opengl.ARBSeparateShaderObjects;
import org.lwjgl.opengl.GL;
import org.lwjgl.opengl.GL41;
import org.lwjgl.opengl.GLCapabilities;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.Marker;
import org.slf4j.MarkerFactory;
import static org.lwjgl.system.MemoryUtil.memAddress;

/**
 *
 * @author zmichaels
 */
public final class ARBDSA extends Common implements DSADriver {

    private static final Marker GL_MARKER = MarkerFactory.getMarker("OPENGL");
    private static final Logger LOGGER = LoggerFactory.getLogger("OPENGL");

    public static DSADriver getInstance() {
        return Holder.INSTANCE;
    }

    @Override
    public int glCreateFramebuffers() {
        LOGGER.trace(GL_MARKER, "glCreateFramebuffers()");

        final int out = ARBDirectStateAccess.glCreateFramebuffers();
        assert checkGLError() : glErrorMsg("glCreateFramebuffersARB(void)");

        return out;
    }

    @Override
    public void glTextureStorage1d(int textureId, int levels, int internalFormat, int width) {
        assert checkId(textureId) : invalidTextureIdMsg(textureId);
        assert checkMipmapDefine(levels) : invalidMipmapDefineMsg(levels);
        assert checkGLenum(internalFormat, GLTextureInternalFormat::of) : invalidGLenumMsg(internalFormat);
        assert checkDimension(width) : invalidWidthMsg(width);

        LOGGER.trace(
                GL_MARKER,
                "glTextureStorage1D({}, {}, {}, {})",
                textureId, levels, internalFormat, width);

        ARBDirectStateAccess.glTextureStorage1D(textureId, levels, internalFormat, width);
        assert checkGLError() : glErrorMsg("glTextureStorage1dARB(IIII)", textureId, levels, GLTextureInternalFormat.of(internalFormat).get(), width);
    }

    @Override
    public void glTextureStorage2d(int textureId, int levels, int internalFormat, int width, int height) {
        assert checkId(textureId) : invalidTextureIdMsg(textureId);
        assert checkMipmapDefine(levels) : invalidMipmapDefineMsg(levels);
        assert checkGLenum(internalFormat, GLTextureInternalFormat::of) : invalidGLenumMsg(internalFormat);
        assert checkDimension(width) : invalidWidthMsg(width);
        assert checkDimension(height) : invalidHeightMsg(height);

        LOGGER.trace(
                GL_MARKER,
                "glTextureStorage2DARB({}, {}, {}, {}, {})",
                textureId, levels, internalFormat, width, height);

        ARBDirectStateAccess.glTextureStorage2D(textureId, levels, internalFormat, width, height);
        assert checkGLError() : glErrorMsg("glTextureStorage2dARB(IIIII)", textureId, levels, GLTextureInternalFormat.of(internalFormat).get(), width, height);
    }

    @Override
    public void glTextureStorage3d(int textureId, int levels, int internalFormat, int width, int height, int depth) {
        assert checkId(textureId) : invalidTextureIdMsg(textureId);
        assert checkMipmapDefine(levels) : invalidMipmapDefineMsg(levels);
        assert checkGLenum(internalFormat, GLTextureInternalFormat::of) : invalidGLenumMsg(internalFormat);
        assert checkDimension(width) : invalidWidthMsg(width);
        assert checkDimension(height) : invalidHeightMsg(height);
        assert checkDimension(depth) : invalidDepthMsg(depth);

        LOGGER.trace(
                GL_MARKER,
                "glTextureStorage3D({}, {}, {}, {}, {}, {}) (ARB)",
                textureId, levels, internalFormat, width, height, depth);

        ARBDirectStateAccess.glTextureStorage3D(textureId, levels, internalFormat, width, height, depth);
        assert checkGLError() : glErrorMsg("glTextureStorage3dARB(IIIIII)", textureId, levels, GLTextureInternalFormat.of(internalFormat).get(), width, height, depth);
    }

    @Override
    public void glTextureParameteri(int textureId, int pName, int val) {
        assert checkId(textureId) : invalidTextureIdMsg(textureId);

        LOGGER.trace(
                GL_MARKER,
                "glTextureParemeteri({}, {}, {}) (ARB)",
                textureId, pName, val);

        ARBDirectStateAccess.glTextureParameteri(textureId, pName, val);
        assert checkGLError() : glErrorMsg("glTextureParameteriARB(III)", textureId, pName, val);
    }

    @Override
    public void glTextureParameterf(int textureId, int pName, float val) {
        assert checkId(textureId) : invalidTextureIdMsg(textureId);
        assert checkFloat(val) : invalidFloatMsg(val);

        LOGGER.trace(
                GL_MARKER,
                "glTextureParemeterf({}, {}, {}) (ARB)",
                textureId, pName, val);

        ARBDirectStateAccess.glTextureParameterf(textureId, pName, val);
        assert checkGLError() : glErrorMsg("glTextureParameterfARB(IIF)", textureId, pName, val);
    }

    @Override
    public int glGetNamedBufferParameteri(int bufferId, int pName) {
        assert checkNullableId(bufferId) : invalidNullableBufferIdMsg(bufferId);

        LOGGER.trace(
                GL_MARKER,
                "glGetNamedBufferParameteri({}, {}) (ARB)",
                bufferId, pName);

        final int out = ARBDirectStateAccess.glGetNamedBufferParameteri(bufferId, pName);
        assert checkGLError() : glErrorMsg("glGetNamedBufferParameteriARB(II)", bufferId, pName);

        return out;
    }

    @Override
    public void glNamedFramebufferTexture(int framebuffer, int attachment, int texture, int level) {
        assert checkId(framebuffer) : invalidFramebufferIdMsg(framebuffer);
        assert checkId(texture) : invalidTextureIdMsg(texture);
        assert checkMipmapLevel(level) : invalidMipmapLevelMsg(level);

        LOGGER.trace(
                GL_MARKER,
                "glGetNamedFramebufferTexture({}, {}, {}, {}) (ARB)",
                framebuffer, attachment, texture, level);

        ARBDirectStateAccess.glNamedFramebufferTexture(framebuffer, attachment, texture, level);
        assert checkGLError() : glErrorMsg("glNamedFramebufferTextureARB(IIII)", framebuffer, attachment, texture, level);
    }

    @Override
    public void glNamedBufferReadPixels(int bufferId, int x, int y, int width, int height, int format, int type, long ptr) {
        FakeDSA.getInstance().glNamedBufferReadPixels(bufferId, x, y, width, height, format, type, ptr);
    }

    @Override
    public void glBlitNamedFramebuffer(int readFramebuffer, int drawFramebuffer, int srcX, int srcY, int srcX1, int srcY1, int dstX0, int dstY0, int dstX1, int dstY1, int mask, int filter) {
        assert checkNullableId(readFramebuffer) : invalidFramebufferIdMsg(readFramebuffer);
        assert checkNullableId(drawFramebuffer) : invalidFramebufferIdMsg(drawFramebuffer);

        LOGGER.trace(
                GL_MARKER,
                "glBlitNamedFramebuffer({}, {}, {}, {}, {}, {}, {}, {}, {}, {}, {}, {}) (ARB)",
                readFramebuffer, drawFramebuffer,
                srcX, srcY, srcX1, srcY1,
                dstX0, dstY0, dstX1, dstY1,
                mask, filter);

        ARBDirectStateAccess.glBlitNamedFramebuffer(readFramebuffer, drawFramebuffer, srcX1, srcY1, srcX1, srcY1, dstX0, dstY0, dstX1, dstY1, mask, filter);
        assert checkGLError() : glErrorMsg("glBlitNamedFramebufferARB(IIIIIIIIIIII)", readFramebuffer, drawFramebuffer, srcX, srcY, srcX1, srcY1, dstX0, dstY0, dstX1, dstY1, mask, filter);
    }

    @Override
    public void glGetTextureImage(int texture, int level, int format, int type, int bufferSize, ByteBuffer pixels) {
        assert checkId(texture) : invalidTextureIdMsg(texture);
        assert checkMipmapLevel(level) : invalidMipmapLevelMsg(level);
        assert checkGLenum(format, GLTextureFormat::of) : invalidGLenumMsg(format);
        assert checkGLenum(type, GLType::of) : invalidGLenumMsg(type);
        assert checkBufferIsNative(pixels) : bufferIsNotNativeMsg(pixels);
        assert pixels.isDirect() : NON_DIRECT_BUFFER_MSG;
        assert checkBufferSize(bufferSize, 1, 1, format, type, pixels) : bufferTooSmallMsg(bufferSize, 1, 1, format, type, pixels);

        LOGGER.trace(
                GL_MARKER,
                "glGetTextureImage({}, {}, {}, {}, {}, {}) (ARB)",
                texture, level, format, type, bufferSize, pixels);

        ARBDirectStateAccess.glGetTextureImage(texture, level, format, type, bufferSize, pixels);
        assert checkGLError() : glErrorMsg("glGetTextureImageARB(IIIII*)", texture, level, GLTextureFormat.of(format).get(), GLType.of(type).get(), bufferSize, toHexString(memAddress(pixels)));
    }

    private static class Holder {

        private static final DSADriver INSTANCE = new ARBDSA();
    }

    @Override
    public String toString() {
        return "ARBDSA";
    }

    @Override
    public boolean isSupported() {
        final GLCapabilities cap = GL.getCapabilities();

        return cap.GL_ARB_direct_state_access && FakeDSA.getInstance().isSupported();
    }

    @Override
    public int glCreateBuffers() {
        LOGGER.trace(GL_MARKER, "glCreateBuffers() (ARB)");

        final int out = ARBDirectStateAccess.glCreateBuffers();
        assert checkGLError() : glErrorMsg("glCreateBuffersARB(void)");

        return out;
    }

    @Override
    public int glCreateTextures(int target) {
        assert checkGLenum(target, GLTextureTarget::of) : invalidGLenumMsg(target);

        LOGGER.trace(GL_MARKER, "glCreateTextures({}) (ARB)", target);

        final int out = ARBDirectStateAccess.glCreateTextures(target);
        assert checkGLError() : glErrorMsg("glCreateTexturesARB(I)", GLTextureTarget.of(target).get());

        return out;
    }

    @Override
    public void glNamedBufferData(int bufferId, long size, int usage) {
        assert checkId(bufferId) : invalidBufferIdMsg(bufferId);
        assert checkSize(size) : invalidSizeMsg(size);
        assert checkGLenum(usage, GLBufferUsage::of) : invalidGLenumMsg(usage);

        LOGGER.trace(
                GL_MARKER,
                "glNamedBufferData({}, {}, {}) (ARB)",
                bufferId, size, usage);

        ARBDirectStateAccess.glNamedBufferData(bufferId, size, usage);
        assert checkGLError() : glErrorMsg("glNamedBufferDataARB(ILI)", bufferId, size, GLBufferUsage.of(usage).get());
    }

    @Override
    public void glNamedBufferData(int bufferId, ByteBuffer data, int usage) {
        assert checkId(bufferId) : invalidBufferIdMsg(bufferId);
        assert checkBufferIsNative(data) : bufferIsNotNativeMsg(data);
        assert data.isDirect() : NON_DIRECT_BUFFER_MSG;

        LOGGER.trace(
                GL_MARKER,
                "glNamedBufferData({}, {}, {}) (ARB)",
                bufferId, data, usage);

        ARBDirectStateAccess.glNamedBufferData(bufferId, data, usage);
        assert checkGLError() : glErrorMsg("glNamedBufferDataARB(I*I)", bufferId, toHexString(memAddress(data)), usage);
    }

    @Override
    public void glNamedBufferSubData(int buffer, long offset, ByteBuffer data) {
        assert checkId(buffer) : invalidBufferIdMsg(buffer);
        assert checkOffset(offset) : invalidOffsetMsg(offset);
        assert checkBufferIsNative(data) : bufferIsNotNativeMsg(data);
        assert data.isDirect() : NON_DIRECT_BUFFER_MSG;

        LOGGER.trace(
                GL_MARKER,
                "glNamedBufferSubData({}, {}, {}) (ARB)",
                buffer, offset, data);

        ARBDirectStateAccess.glNamedBufferSubData(buffer, offset, data);
        assert checkGLError() : glErrorMsg("glNamedBufferSubData(IL*)", buffer, offset, toHexString(memAddress(data)));
    }

    @Override
    public void glNamedBufferStorage(int bufferId, ByteBuffer data, int flags) {
        assert checkId(bufferId) : invalidBufferIdMsg(bufferId);
        assert checkBufferIsNative(data);
        assert data.isDirect() : NON_DIRECT_BUFFER_MSG;

        LOGGER.trace(GL_MARKER, "glNamedBufferStorage({}, {}, {}) (ARB)",
                bufferId, data, flags);

        ARBDirectStateAccess.glNamedBufferStorage(bufferId, data, flags);
        assert checkGLError() : glErrorMsg("glNamedBufferStorageARB(I*I)", bufferId, toHexString(memAddress(data)), flags);
    }

    @Override
    public void glNamedBufferStorage(int bufferId, long size, int flags) {
        assert checkId(bufferId) : invalidBufferIdMsg(bufferId);
        assert checkSize(size) : invalidSizeMsg(size);

        LOGGER.trace(
                GL_MARKER,
                "glNamedBufferStorage({}, {}, {}) (ARB)",
                bufferId, size, flags);

        ARBDirectStateAccess.glNamedBufferStorage(bufferId, size, flags);
        assert checkGLError() : glErrorMsg("glNamedBufferStorageARB(ILI)", bufferId, size, flags);
    }

    @Override
    public void glGetNamedBufferSubData(int bufferId, long offset, ByteBuffer out) {
        assert checkId(bufferId) : invalidBufferIdMsg(bufferId);
        assert checkOffset(offset) : invalidOffsetMsg(offset);
        assert checkBufferIsNative(out) : bufferIsNotNativeMsg(out);
        assert out.isDirect() : NON_DIRECT_BUFFER_MSG;

        LOGGER.trace(
                GL_MARKER,
                "glGetNamedBufferSubData({}, {}, {}) (ARB)",
                bufferId, offset, out);

        ARBDirectStateAccess.glGetNamedBufferSubData(bufferId, offset, out);
        assert checkGLError() : glErrorMsg("glGetNamedBufferSubDataARB(IL*)", bufferId, offset, toHexString(memAddress(out)));
    }

    @Override
    public ByteBuffer glMapNamedBufferRange(int bufferId, long offset, long length, int access, ByteBuffer recycled) {
        assert checkId(bufferId) : invalidBufferIdMsg(bufferId);
        assert checkOffset(offset) : invalidOffsetMsg(offset);
        assert checkSize(length) : invalidSizeMsg(length);

        LOGGER.trace(
                GL_MARKER,
                "glMapNamedBufferRange({}, {}, {}, {}) (ARB)",
                bufferId, offset, length, access);

        final ByteBuffer out = ARBDirectStateAccess.glMapNamedBufferRange(bufferId, offset, length, access, recycled);
        assert checkGLError() : glErrorMsg("glMapNamedBufferRangeARB(ILLI)", bufferId, offset, length, access);

        return out;
    }

    @Override
    public void glUnmapNamedBuffer(int bufferId) {
        assert checkId(bufferId) : invalidBufferIdMsg(bufferId);

        LOGGER.trace(
                GL_MARKER,
                "glUnmapNamedBuffer({}) (ARB)",
                bufferId);

        ARBDirectStateAccess.glUnmapNamedBuffer(bufferId);
        assert checkGLError() : glErrorMsg("glUnmapNamedBufferARB(I)", bufferId);
    }

    @Override
    public void glCopyNamedBufferSubData(int readBufferId, int writeBufferId, long readOffset, long writeOffset, long size) {
        assert checkId(readBufferId) : invalidBufferIdMsg(readBufferId);
        assert checkId(writeBufferId) : invalidBufferIdMsg(writeBufferId);
        assert checkOffset(readOffset) : invalidOffsetMsg(readOffset);
        assert checkOffset(writeOffset) : invalidOffsetMsg(writeOffset);
        assert checkSize(size) : invalidSizeMsg(size);

        LOGGER.trace(
                GL_MARKER,
                "glCopyNamedBufferSubData({}, {}, {}, {}, {}) (ARB)",
                readBufferId, writeBufferId,
                readOffset, writeOffset,
                size);

        ARBDirectStateAccess.glCopyNamedBufferSubData(readBufferId, writeBufferId, readOffset, writeOffset, size);
        assert checkGLError() : glErrorMsg("glCopyNamedBufferSubDataARB(IILLL)", readBufferId, writeBufferId, readOffset, writeOffset, size);
    }

    @Override
    public void glProgramUniform1f(int programId, int location, float value) {
        assert checkId(programId) : invalidProgramIdMsg(programId);
        assert checkOffset(location) : invalidUniformLocationMsg(location);
        assert checkFloat(value) : invalidFloatMsg(value);

        final GLCapabilities cap = GL.getCapabilities();

        if (cap.OpenGL41) {
            LOGGER.trace(
                    GL_MARKER,
                    "glProgramUniform1f({}, {}, {})",
                    programId, location, value);

            GL41.glProgramUniform1f(programId, location, value);
            assert checkGLError() : glErrorMsg("glProgramUniform1f(IIF)", programId, location, value);
        } else if (cap.GL_ARB_separate_shader_objects) {
            LOGGER.trace(
                    GL_MARKER,
                    "glProgramUniform1f({}, {}, {}) (ARB)",
                    programId, location, value);

            ARBSeparateShaderObjects.glProgramUniform1f(programId, location, value);
            assert checkGLError() : glErrorMsg("glProgramUniform1fARB(IIF)", programId, location, value);
        } else {
            FakeDSA.getInstance().glProgramUniform1f(programId, location, value);
        }
    }

    @Override
    public void glProgramUniform2f(int programId, int location, float v0, float v1) {
        assert checkId(programId) : invalidProgramIdMsg(programId);
        assert checkOffset(location) : invalidUniformLocationMsg(location);
        assert checkFloat(v0) : invalidFloatMsg(v0);
        assert checkFloat(v1) : invalidFloatMsg(v1);

        final GLCapabilities cap = GL.getCapabilities();

        if (cap.OpenGL41) {
            LOGGER.trace(
                    GL_MARKER,
                    "glProgramUniform2f({}, {}, {}, {})",
                    programId, location, v0, v1);

            GL41.glProgramUniform2f(programId, location, v0, v1);
            assert checkGLError() : glErrorMsg("glProgramUniform2f(IIFF)", programId, location, v0, v1);
        } else if (cap.GL_ARB_separate_shader_objects) {
            LOGGER.trace(
                    GL_MARKER,
                    "glProgramUniform2f({}, {}, {}, {}) (ARB)",
                    programId, location, v0, v1);

            ARBSeparateShaderObjects.glProgramUniform2f(programId, location, v0, v1);
            assert checkGLError() : glErrorMsg("glProgramUniform2fARB(IIFF)", programId, location, v0, v1);
        } else {
            FakeDSA.getInstance().glProgramUniform2f(programId, location, v0, v1);
        }
    }

    @Override
    public void glProgramUniform3f(int programId, int location, float v0, float v1, float v2) {
        assert checkId(programId) : invalidProgramIdMsg(programId);
        assert checkOffset(location) : invalidUniformLocationMsg(location);
        assert checkFloat(v0) : invalidFloatMsg(v0);
        assert checkFloat(v1) : invalidFloatMsg(v1);
        assert checkFloat(v2) : invalidFloatMsg(v2);

        final GLCapabilities cap = GL.getCapabilities();

        if (cap.OpenGL41) {
            LOGGER.trace(
                    GL_MARKER,
                    "glProgramUniform3f({}, {}, {}, {}, {})",
                    programId, location, v0, v1, v2);

            GL41.glProgramUniform3f(programId, location, v0, v1, v2);
            assert checkGLError() : glErrorMsg("glProgramUniform3f(IIFFF)", programId, location, v0, v1, v2);
        } else if (cap.GL_ARB_separate_shader_objects) {
            LOGGER.trace(
                    GL_MARKER,
                    "glProgramUniform3f({}, {}, {}, {}, {}) (ARB)",
                    programId, location, v0, v1, v2);

            ARBSeparateShaderObjects.glProgramUniform3f(programId, location, v0, v1, v2);
            assert checkGLError() : glErrorMsg("glProgramUniform3fARB(IIFFF)", programId, location, v0, v1, v2);
        } else {
            FakeDSA.getInstance().glProgramUniform3f(programId, location, v0, v1, v2);
        }
    }

    @Override
    public void glProgramUniform4f(int programId, int location, float v0, float v1, float v2, float v3) {
        assert checkId(programId) : invalidProgramIdMsg(programId);
        assert checkOffset(location) : invalidUniformLocationMsg(location);
        assert checkFloat(v0) : invalidFloatMsg(v0);
        assert checkFloat(v1) : invalidFloatMsg(v1);
        assert checkFloat(v2) : invalidFloatMsg(v2);
        assert checkFloat(v3) : invalidFloatMsg(v3);

        final GLCapabilities cap = GL.getCapabilities();

        if (cap.OpenGL41) {
            LOGGER.trace(
                    GL_MARKER,
                    "glProgramUniform4f({}, {}, {}, {}, {}, {})",
                    programId, location, v0, v1, v2, v3);

            GL41.glProgramUniform4f(programId, location, v0, v1, v2, v3);
            assert checkGLError() : glErrorMsg("glProgramUniform4f(IIFFFF)", programId, location, v0, v1, v2, v3);
        } else if (cap.GL_ARB_separate_shader_objects) {
            LOGGER.trace(
                    GL_MARKER,
                    "glProgramUniform4f({}, {}, {}, {}, {}, {}) (ARB)",
                    programId, location, v0, v1, v2, v3);

            ARBSeparateShaderObjects.glProgramUniform4f(programId, location, v0, v1, v2, v3);
            assert checkGLError() : glErrorMsg("glProgramUniform4fARB(IIFFFF)", programId, location, v0, v1, v2, v3);
        } else {
            FakeDSA.getInstance().glProgramUniform4f(programId, location, v0, v1, v2, v3);
        }
    }

    @Override
    public void glProgramUniform1i(int programId, int location, int value) {
        assert checkId(programId) : invalidProgramIdMsg(programId);
        assert checkOffset(location) : invalidUniformLocationMsg(location);

        final GLCapabilities cap = GL.getCapabilities();

        if (cap.OpenGL41) {
            LOGGER.trace(
                    GL_MARKER,
                    "glProgramUniform1i({}, {}, {})",
                    programId, location, value);

            GL41.glProgramUniform1i(programId, location, value);
            assert checkGLError() : glErrorMsg("glProgramUniform1i(III)", programId, location, value);
        } else if (cap.GL_ARB_separate_shader_objects) {
            LOGGER.trace(
                    GL_MARKER,
                    "glProgramUniform1i({}, {}, {}) (ARB)",
                    programId, location, value);

            ARBSeparateShaderObjects.glProgramUniform1i(programId, location, value);
            assert checkGLError() : glErrorMsg("glProgramUniform1iARB(III)", programId, location, value);
        } else {
            FakeDSA.getInstance().glProgramUniform1i(programId, location, value);
        }
    }

    @Override
    public void glProgramUniform2i(int programId, int location, int v0, int v1) {
        assert checkId(programId) : invalidProgramIdMsg(programId);
        assert checkOffset(location) : invalidUniformLocationMsg(location);

        final GLCapabilities cap = GL.getCapabilities();

        if (cap.OpenGL41) {
            LOGGER.trace(
                    GL_MARKER,
                    "glProgramUniform2i({}, {}, {}, {})",
                    programId, location, v0, v1);

            GL41.glProgramUniform2i(programId, location, v0, v1);
            assert checkGLError() : glErrorMsg("glProgramUniform2i(IIII)", programId, location, v0, v1);
        } else if (cap.GL_ARB_separate_shader_objects) {
            LOGGER.trace(
                    GL_MARKER,
                    "glProgramUniform2i({}, {}, {}, {}) (ARB)",
                    programId, location, v0, v1);

            ARBSeparateShaderObjects.glProgramUniform2i(programId, location, v0, v1);
            assert checkGLError() : glErrorMsg("glProgramUniform2iARB(IIII)", programId, location, v0, v1);
        } else {
            FakeDSA.getInstance().glProgramUniform2i(programId, location, v0, v1);
        }
    }

    @Override
    public void glProgramUniform3i(int programId, int location, int v0, int v1, int v2) {
        assert checkId(programId) : invalidProgramIdMsg(programId);
        assert checkOffset(location) : invalidUniformLocationMsg(location);

        final GLCapabilities cap = GL.getCapabilities();

        if (cap.OpenGL41) {
            LOGGER.trace(
                    GL_MARKER,
                    "glProgramUniform3i({}, {}, {}, {}, {})",
                    programId, location, v0, v1, v2);

            GL41.glProgramUniform3i(programId, location, v0, v1, v2);
            assert checkGLError() : glErrorMsg("glProgramUniform3i(IIIII)", programId, location, v0, v1, v2);
        } else if (cap.GL_ARB_separate_shader_objects) {
            LOGGER.trace(
                    GL_MARKER,
                    "glProgramUniform3i({}, {}, {}, {}, {}) (ARB)",
                    programId, location, v0, v1, v2);

            ARBSeparateShaderObjects.glProgramUniform3i(programId, location, v0, v1, v2);
            assert checkGLError() : glErrorMsg("glProgramUniform3iARB(IIIII)", programId, location, v0, v1, v2);
        } else {
            FakeDSA.getInstance().glProgramUniform3i(programId, location, v0, v1, v2);
        }
    }

    @Override
    public void glProgramUniform4i(int programId, int location, int v0, int v1, int v2, int v3) {
        assert checkId(programId) : invalidProgramIdMsg(programId);
        assert checkOffset(location) : invalidUniformLocationMsg(location);

        final GLCapabilities cap = GL.getCapabilities();

        if (cap.OpenGL41) {
            LOGGER.trace(
                    GL_MARKER,
                    "glProgramUniform4i({}, {}, {}, {}, {}, {})",
                    programId, location, v0, v1, v2, v3);

            GL41.glProgramUniform4i(programId, location, v0, v1, v2, v3);
            assert checkGLError() : glErrorMsg("glProgramUnfirom4i(IIIIII)", programId, location, v0, v1, v2, v3);
        } else if (cap.GL_ARB_separate_shader_objects) {
            LOGGER.trace(
                    GL_MARKER,
                    "glProgramUniform4i({}, {}, {}, {}, {}, {}) (ARB)",
                    programId, location, v0, v1, v2, v3);
            ARBSeparateShaderObjects.glProgramUniform4i(programId, location, v0, v1, v2, v3);
            assert checkGLError() : glErrorMsg("glProgramUniform4iARB(IIIIII)", programId, location, v0, v1, v2, v3);
        } else {
            FakeDSA.getInstance().glProgramUniform4i(programId, location, v0, v1, v2, v3);
        }
    }

    @Override
    public void glProgramUniform1d(int programId, int location, double value) {
        assert checkId(programId) : invalidProgramIdMsg(programId);
        assert checkOffset(location) : invalidUniformLocationMsg(location);
        assert checkDouble(value) : invalidDoubleMsg(value);

        final GLCapabilities cap = GL.getCapabilities();

        if (cap.OpenGL41) {
            LOGGER.trace(
                    GL_MARKER,
                    "glProgramUniform1d({}, {}, {})",
                    programId, location, value);

            GL41.glProgramUniform1d(programId, location, value);
            assert checkGLError() : glErrorMsg("glProgramUniform1d(IID)", programId, location, value);
        } else if (cap.GL_ARB_separate_shader_objects) {
            LOGGER.trace(
                    GL_MARKER,
                    "glProgramUniform1d({}, {}, {}) (ARB)",
                    programId, location, value);

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

        final GLCapabilities cap = GL.getCapabilities();

        if (cap.OpenGL41) {
            LOGGER.trace(
                    GL_MARKER,
                    "glProgramUniform2d({}, {}, {}, {})",
                    programId, location, v0, v1);

            GL41.glProgramUniform2d(programId, location, v0, v1);
            assert checkGLError() : glErrorMsg("glProgramUniform2d(IIDD)", programId, location, v0, v1);
        } else if (cap.GL_ARB_separate_shader_objects) {
            LOGGER.trace(
                    GL_MARKER,
                    "glProgramUniform2d({}, {}, {}, {}) (ARB)",
                    programId, location, v0, v1);

            ARBSeparateShaderObjects.glProgramUniform2d(programId, location, v0, v1);
            assert checkGLError() : glErrorMsg("glProgramUnifrom2dARB(IIDD)", programId, location, v0, v1);
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

        final GLCapabilities cap = GL.getCapabilities();

        if (cap.OpenGL41) {
            LOGGER.trace(
                    GL_MARKER,
                    "glProgramUniform3d({}, {}, {}, {}, {})",
                    programId, location, v0, v1, v2);

            GL41.glProgramUniform3d(programId, location, v0, v1, v2);
            assert checkGLError() : glErrorMsg("glProgramUniform3d(IIDDD)", programId, location, v0, v1, v2);
        } else if (cap.GL_ARB_separate_shader_objects) {
            LOGGER.trace(
                    GL_MARKER,
                    "glProgramUniform3d({}, {}, {}, {}, {}) (ARB)",
                    programId, location, v0, v1, v2);

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

        final GLCapabilities cap = GL.getCapabilities();

        if (cap.OpenGL41) {
            LOGGER.trace(
                    GL_MARKER,
                    "glProgramUniform4d({}, {}, {}, {}, {}, {})",
                    programId, location, v0, v1, v2, v3);

            GL41.glProgramUniform4d(programId, location, v0, v1, v2, v3);
            assert checkGLError() : glErrorMsg("glProgramUniform4d(IIDDDD)", programId, location, v0, v1, v2, v3);
        } else if (cap.GL_ARB_separate_shader_objects) {
            LOGGER.trace(
                    GL_MARKER,
                    "glProgramUniform4d({}, {}, {}, {}, {}, {}) (ARB)",
                    programId, location, v0, v1, v2, v3);

            ARBSeparateShaderObjects.glProgramUniform4d(programId, location, v0, v1, v2, v3);
            assert checkGLError() : glErrorMsg("glProgramUniform4dARB(IIDDDD)", programId, location, v0, v1, v2, v3);
        } else {
            FakeDSA.getInstance().glProgramUniform4d(programId, location, v0, v1, v2, v3);
        }
    }

    @Override
    public void glProgramUniformMatrix2f(int programId, int location, boolean needsTranspose, FloatBuffer data) {
        assert checkId(programId) : invalidProgramIdMsg(programId);
        assert checkOffset(location) : invalidUniformLocationMsg(location);
        assert data.isDirect() : NON_DIRECT_BUFFER_MSG;

        final GLCapabilities cap = GL.getCapabilities();

        if (cap.OpenGL41) {
            LOGGER.trace(
                    GL_MARKER,
                    "glProgramUniformMatrix2fv({}, {}, {}, {})",
                    programId, location, needsTranspose, data);

            GL41.glProgramUniformMatrix2fv(programId, location, needsTranspose, data);
            assert checkGLError() : glErrorMsg("glProgramUniformMatrix2fv(IIB*)", programId, location, needsTranspose, toHexString(memAddress(data)));
        } else if (cap.GL_ARB_separate_shader_objects) {
            LOGGER.trace(
                    GL_MARKER,
                    "glProgramUniformMatrix2fv({}, {}, {}, {}) (ARB)",
                    programId, location, needsTranspose, data);
            ARBSeparateShaderObjects.glProgramUniformMatrix2fv(programId, location, needsTranspose, data);
            assert checkGLError() : glErrorMsg("glProgramUniformMatrix2fvARB(IIB*)", programId, location, needsTranspose, toHexString(memAddress(data)));
        } else {
            FakeDSA.getInstance().glProgramUniformMatrix2f(programId, location, needsTranspose, data);
        }
    }

    @Override
    public void glProgramUniformMatrix3f(int programId, int location, boolean needsTranspose, FloatBuffer data) {
        assert checkId(programId) : invalidProgramIdMsg(programId);
        assert checkOffset(location) : invalidUniformLocationMsg(location);
        assert data.isDirect() : NON_DIRECT_BUFFER_MSG;

        final GLCapabilities cap = GL.getCapabilities();

        if (cap.OpenGL41) {
            LOGGER.trace(
                    GL_MARKER,
                    "glProgramUniformMatrix3fv({}, {}, {}, {})",
                    programId, location, needsTranspose, data);

            GL41.glProgramUniformMatrix3fv(programId, location, needsTranspose, data);
            assert checkGLError() : glErrorMsg("glProgramUniformMatrix3fv(IIB*)", programId, location, needsTranspose, toHexString(memAddress(data)));
        } else if (cap.GL_ARB_separate_shader_objects) {
            LOGGER.trace(
                    GL_MARKER,
                    "glProgramUniformMatrix3fv({}, {}, {}, {}) (ARB)",
                    programId, location, needsTranspose, data);

            ARBSeparateShaderObjects.glProgramUniformMatrix3fv(programId, location, needsTranspose, data);
            assert checkGLError() : glErrorMsg("glProgramUniformMatrix3fvARB(IIB*)", programId, location, needsTranspose, toHexString(memAddress(data)));
        } else {
            FakeDSA.getInstance().glProgramUniformMatrix3f(programId, location, needsTranspose, data);
        }
    }

    @Override
    public void glProgramUniformMatrix4f(int programId, int location, boolean needsTranspose, FloatBuffer data) {
        assert checkId(programId) : invalidProgramIdMsg(programId);
        assert checkOffset(location) : invalidUniformLocationMsg(location);
        assert data.isDirect() : NON_DIRECT_BUFFER_MSG;

        final GLCapabilities cap = GL.getCapabilities();

        if (cap.OpenGL41) {
            LOGGER.trace(
                    GL_MARKER,
                    "glProgramUniformMatrix4fv({}, {}, {}, {})",
                    programId, location, needsTranspose, data);

            GL41.glProgramUniformMatrix4fv(programId, location, needsTranspose, data);
            assert checkGLError() : glErrorMsg("glProgramUniformMatrix4fv(IIB*)", programId, location, needsTranspose, toHexString(memAddress(data)));
        } else if (cap.GL_ARB_separate_shader_objects) {
            LOGGER.trace(
                    GL_MARKER,
                    "glProgramUniformMatrix4fv({}, {}, {}, {}) (ARB)",
                    programId, location, needsTranspose, data);

            ARBSeparateShaderObjects.glProgramUniformMatrix4fv(programId, location, needsTranspose, data);
            assert checkGLError() : glErrorMsg("glProgramUniformMatrix4fvARB(IIB*)", programId, location, needsTranspose, toHexString(memAddress(data)));
        } else {
            FakeDSA.getInstance().glProgramUniformMatrix4f(programId, location, needsTranspose, data);
        }
    }

    @Override
    public void glProgramUniformMatrix2d(int programId, int location, boolean needsTranspose, DoubleBuffer data) {
        assert checkId(programId) : invalidProgramIdMsg(programId);
        assert checkOffset(location) : invalidUniformLocationMsg(location);
        assert data.isDirect() : NON_DIRECT_BUFFER_MSG;

        final GLCapabilities cap = GL.getCapabilities();

        if (cap.OpenGL41) {
            LOGGER.trace(
                    GL_MARKER,
                    "glProgramUniformMatrix2dv({}, {}, {}, {})",
                    programId, location, needsTranspose, data);

            GL41.glProgramUniformMatrix2dv(programId, location, needsTranspose, data);
            assert checkGLError() : glErrorMsg("glProgramUniformMatrix2dv(IIB*)", programId, location, needsTranspose, toHexString(memAddress(data)));
        } else if (cap.GL_ARB_separate_shader_objects) {
            LOGGER.trace(
                    GL_MARKER,
                    "glProgramUniformMatrix2dv({}, {}, {}, {}) (ARB)",
                    programId, location, needsTranspose, data);

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

        final GLCapabilities cap = GL.getCapabilities();

        if (cap.OpenGL41) {
            LOGGER.trace(
                    GL_MARKER,
                    "glProgramUniformMatrix3dv({}, {}, {}, {})",
                    programId, location, needsTranspose, data);

            GL41.glProgramUniformMatrix3dv(programId, location, needsTranspose, data);
            assert checkGLError() : glErrorMsg("glProgramUniformMatrix3dv(IIB*)", programId, location, needsTranspose, toHexString(memAddress(data)));
        } else if (cap.GL_ARB_separate_shader_objects) {
            LOGGER.trace(
                    GL_MARKER,
                    "glProgramUniformMatrix3dv({}, {}, {}, {}) (ARB)",
                    programId, location, needsTranspose, data);

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

        final GLCapabilities cap = GL.getCapabilities();

        if (cap.OpenGL41) {
            LOGGER.trace(
                    GL_MARKER,
                    "glProgramUniformMatrix4dv({}, {}, {}, {})",
                    programId, location, needsTranspose, data);

            GL41.glProgramUniformMatrix4dv(programId, location, needsTranspose, data);
            assert checkGLError() : glErrorMsg("glProgramUniformMatrix4dv(IIB*)", programId, location, needsTranspose, toHexString(memAddress(data)));
        } else if (cap.GL_ARB_separate_shader_objects) {
            LOGGER.trace(
                    GL_MARKER,
                    "glProgramUniformMatrix4dv({}, {}, {}, {}) (ARB)",
                    programId, location, needsTranspose, data);

            ARBSeparateShaderObjects.glProgramUniformMatrix4dv(programId, location, needsTranspose, data);
            assert checkGLError() : glErrorMsg("glProgramUniformMatrix4dvARB(IIB*)", programId, location, needsTranspose, toHexString(memAddress(data)));
        } else {
            FakeDSA.getInstance().glProgramUniformMatrix4d(programId, location, needsTranspose, data);
        }
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

        LOGGER.trace(
                GL_MARKER,
                "glTextureSubImage1d({}, {}, {}, {}, {}, {}, {})",
                textureId, level, xOffset, width, format, type, pixels);

        ARBDirectStateAccess.glTextureSubImage1D(textureId, level, xOffset, width, format, type, pixels);
        assert checkGLError() : glErrorMsg("glTextureSubImage1DARB(IIIIII*)", textureId, level, xOffset, width, GLTextureFormat.of(format).get(), GLType.of(type).get(), toHexString(memAddress(pixels)));
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

        LOGGER.trace(
                GL_MARKER,
                "glTextureSubImage2D({}, {}, {}, {}, {}, {}, {}, {}, {})",
                textureId,
                level,
                xOffset, yOffset,
                width, height,
                format, type, pixels);

        ARBDirectStateAccess.glTextureSubImage2D(textureId, level, xOffset, yOffset, width, height, format, type, pixels);
        assert checkGLError() : glErrorMsg("glTextureSubImage2dARB(IIIIIIII*)", textureId, level, xOffset, yOffset, width, height, GLTextureFormat.of(format).get(), GLType.of(type).get(), toHexString(memAddress(pixels)));
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
        assert checkBufferSize(width, height, depth, format, type, pixels) : bufferTooSmallMsg(width, height, depth, format, type, pixels);

        LOGGER.trace(
                GL_MARKER,
                "glTextureSubImage3D({}, {}, {}, {}, {}, {}, {}, {}, {}, {}, {})",
                textureId,
                level,
                xOffset, yOffset, zOffset,
                width, height, depth,
                format, type, pixels);

        ARBDirectStateAccess.glTextureSubImage3D(textureId, level, xOffset, yOffset, zOffset, width, height, depth, format, type, pixels);
        assert checkGLError() : glErrorMsg("glTextureSubImage3dARB(IIIIIIIIIII*)", textureId, level, xOffset, yOffset, zOffset, width, height, depth, GLTextureFormat.of(format).get(), GLType.of(type).get(), toHexString(memAddress(pixels)));
    }

    @Override
    public void glBindTextureUnit(int unit, int textureId) {
        assert checkNullableId(unit) : invalidTextureUnitMsg(unit);
        assert checkId(textureId) : invalidTextureIdMsg(textureId);

        LOGGER.trace(GL_MARKER, "glBindTexutreUnit({}, {})", unit, textureId);
        ARBDirectStateAccess.glBindTextureUnit(unit, textureId);
        assert checkGLError() : glErrorMsg("glBindTextureUnitARB(II)", unit, textureId);
    }

    @Override
    public void glGenerateTextureMipmap(int textureId) {
        assert checkId(textureId) : invalidTextureIdMsg(textureId);

        LOGGER.trace(GL_MARKER, "glGenerateTextureMipmap({})", textureId);
        ARBDirectStateAccess.glGenerateTextureMipmap(textureId);
        assert checkGLError() : glErrorMsg("glGenerateTextureMipmapARB(I)", textureId);
    }
}
