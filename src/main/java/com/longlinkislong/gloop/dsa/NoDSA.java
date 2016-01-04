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

import static com.longlinkislong.gloop.GLAsserts.checkGLError;
import static com.longlinkislong.gloop.GLAsserts.glErrorMsg;
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
import org.lwjgl.opengl.ARBCopyBuffer;
import org.lwjgl.opengl.ARBFramebufferObject;
import org.lwjgl.opengl.ARBGPUShaderFP64;
import org.lwjgl.opengl.EXTFramebufferBlit;
import org.lwjgl.opengl.EXTFramebufferObject;
import org.lwjgl.opengl.GL;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL12;
import org.lwjgl.opengl.GL13;
import org.lwjgl.opengl.GL15;
import org.lwjgl.opengl.GL21;
import org.lwjgl.opengl.GL30;
import org.lwjgl.opengl.GL31;
import org.lwjgl.opengl.GL40;
import org.lwjgl.opengl.GL44;
import org.lwjgl.opengl.GLCapabilities;
import static org.lwjgl.system.MemoryUtil.memAddress;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.Marker;
import org.slf4j.MarkerFactory;

/**
 *
 * @author zmichaels
 */
public final class NoDSA extends Common implements EXTDSADriver {

    private static final Marker GL_MARKER = MarkerFactory.getMarker("OPENGL");
    private static final Logger LOGGER = LoggerFactory.getLogger("OPENGL");

    @Override
    public void glNamedFramebufferTexture1D(int framebuffer, int attachment, int texTarget, int texture, int level) {
        final GLCapabilities cap = GL.getCapabilities();

        if (cap.OpenGL30) {
            LOGGER.trace(GL_MARKER, "glBindFramebuffer(GL_FRAMEBUFFER, {})", framebuffer);
            GL30.glBindFramebuffer(GL30.GL_FRAMEBUFFER, framebuffer);
            assert checkGLError() : glErrorMsg("glBindFramebuffer(II)", "GL_FRAMEBUFFER", framebuffer);

            LOGGER.trace(GL_MARKER, "glFramebufferTexture1D(GL_FRAMEBUFFER, {}, {}, {}, {})", attachment, texTarget, texture, level);
            GL30.glFramebufferTexture1D(GL30.GL_FRAMEBUFFER, attachment, texTarget, texture, level);
            assert checkGLError() : glErrorMsg("glFramebufferTexture1D(IIIII)", "GL_FRAMEBUFFER", attachment, GLTextureTarget.of(texTarget).get(), texture, level);
        } else if (cap.GL_ARB_framebuffer_object) {
            LOGGER.trace(GL_MARKER, "glBindFramebuffer(GL_FRAMEBUFFER, {}) (ARB)", framebuffer);
            ARBFramebufferObject.glBindFramebuffer(GL30.GL_FRAMEBUFFER, framebuffer);
            assert checkGLError() : glErrorMsg("glBindFramebufferARB(II)", "GL_FRAMEBUFFER", framebuffer);

            LOGGER.trace(GL_MARKER, "glFramebufferTexture1D(GL_FRAMEBUFFER, {}, {}, {}, {}) (ARB)", attachment, texTarget, texture, level);
            ARBFramebufferObject.glFramebufferTexture1D(GL30.GL_FRAMEBUFFER, attachment, texTarget, texture, level);
            assert checkGLError() : glErrorMsg("glFramebufferTexture1DARB(IIIII)", "GL_FRAMEBUFFER", attachment, GLTextureTarget.of(texTarget), texture, level);

        } else if (cap.GL_EXT_framebuffer_object) {
            LOGGER.trace(GL_MARKER, "glBindFramebufferEXT(GL_FRAMEBUFFER, {})", framebuffer);
            EXTFramebufferObject.glBindFramebufferEXT(GL30.GL_FRAMEBUFFER, framebuffer);
            assert checkGLError() : glErrorMsg("glBindFramebufferEXT(II)", "GL_FRAMEBUFFER", framebuffer);

            LOGGER.trace(GL_MARKER, "glFramebufferTexture1DEXT(GL_FRAMEBUFFER, {}, {}, {}, {})", attachment, texTarget, texture, level);
            EXTFramebufferObject.glFramebufferTexture1DEXT(GL30.GL_FRAMEBUFFER, attachment, texTarget, texture, level);
            assert checkGLError() : glErrorMsg("glFramebufferTExture1DEXT(IIIII)", "GL_FRAMEBUFFER", attachment, GLTextureTarget.of(texTarget).get(), texture, level);
        } else {
            throw unsupportedFramebufferObject();
        }
    }

    @Override
    public void glNamedFramebufferTexture2D(int framebuffer, int attachment, int texTarget, int texture, int level) {
        final GLCapabilities cap = GL.getCapabilities();

        if (cap.OpenGL30) {
            LOGGER.trace(GL_MARKER, "glBindFramebuffer(GL_FRAMEBUFFER, {})", framebuffer);
            GL30.glBindFramebuffer(GL30.GL_FRAMEBUFFER, framebuffer);
            assert checkGLError() : glErrorMsg("glBindFramebuffer(II)", "GL_FRAMEBUFFER", framebuffer);

            LOGGER.trace(GL_MARKER, "glFramebufferTexture2D(GL_FRAMEBUFFER, {}, {}, {}, {})", attachment, texTarget, texture, level);
            GL30.glFramebufferTexture2D(GL30.GL_FRAMEBUFFER, attachment, texTarget, texture, level);
            assert checkGLError() : glErrorMsg("glFramebufferTexture2D(IIIII)", "GL_FRAMEBUFFER", attachment, GLTextureTarget.of(texTarget).get(), texture, level);
        } else if (cap.GL_ARB_framebuffer_object) {
            LOGGER.trace(GL_MARKER, "glBindFramebuffer(GL_FRAMEBUFFER, {}) (ARB)", framebuffer);
            ARBFramebufferObject.glBindFramebuffer(GL30.GL_FRAMEBUFFER, framebuffer);
            assert checkGLError() : glErrorMsg("glBindFramebufferARB(II)", "GL_FRAMEBUFFER", framebuffer);

            LOGGER.trace(GL_MARKER, "glFramebufferTexture2D(GL_FRAMEBUFFER, {}, {}, {}, {}) (ARB)", attachment, texTarget, texture, level);
            ARBFramebufferObject.glFramebufferTexture2D(GL30.GL_FRAMEBUFFER, attachment, texTarget, texture, level);
            assert checkGLError() : glErrorMsg("glBindFramebufferTexture2DABR(IIIII)", "GL_FRAMEBUFFER", attachment, GLTextureTarget.of(texTarget).get(), texture, level);
        } else if (cap.GL_EXT_framebuffer_object) {
            LOGGER.trace(GL_MARKER, "glBindFramebufferEXT(GL_FRAMEBUFFER, {})", framebuffer);
            EXTFramebufferObject.glBindFramebufferEXT(GL30.GL_FRAMEBUFFER, framebuffer);
            assert checkGLError() : glErrorMsg("glBindFramebufferEXT(II)", "GL_FRAMEBUFFER", framebuffer);

            LOGGER.trace(GL_MARKER, "glFramebufferTexture2DEXT(GL_FRAMEBUFFER, {}, {}, {}, {})", attachment, texTarget, texture, level);
            EXTFramebufferObject.glFramebufferTexture2DEXT(GL30.GL_FRAMEBUFFER, attachment, texTarget, texture, level);
            assert checkGLError() : glErrorMsg("glFramebufferTexture2DEXT(IIIII)", "GL_FRAMEBUFFER", attachment, GLTextureTarget.of(texTarget).get(), texture, level);
        } else {
            throw unsupportedFramebufferObject();
        }
    }

    @Override
    public void glTextureParameteri(int textureId, int target, int pName, int val) {
        LOGGER.trace(GL_MARKER, "glBindTexture({}, {})", target, textureId);
        GL11.glBindTexture(target, textureId);
        assert checkGLError() : glErrorMsg("glBindTexture(II)", GLTextureTarget.of(target).get(), textureId);

        LOGGER.trace(GL_MARKER, "glTexParameteri({}, {}, {})", target, pName, val);
        GL11.glTexParameteri(target, pName, val);
        assert checkGLError() : glErrorMsg("glTexParameteri(III)", GLTextureTarget.of(target).get(), pName, val);
    }

    @Override
    public void glTextureParameterf(int textureId, int target, int pName, float val) {
        LOGGER.trace(GL_MARKER, "glBindTexture({}, {})", target, textureId);
        GL11.glBindTexture(target, textureId);
        assert checkGLError() : glErrorMsg("glBindTexture(II)", GLTextureTarget.of(target).get(), textureId);

        LOGGER.trace(GL_MARKER, "glTexParameterf({}, {}, {})", target, pName, val);
        GL11.glTexParameterf(target, pName, val);
        assert checkGLError() : glErrorMsg("glTexParameterf(IIF)", GLTextureTarget.of(target).get(), pName, val);
    }

    @Override
    public void glGenerateTextureMipmap(int textureId, int target) {
        final GLCapabilities cap = GL.getCapabilities();

        if (cap.OpenGL30) {
            LOGGER.trace(GL_MARKER, "glBindTexture({}, {})", target, textureId);
            GL11.glBindTexture(target, textureId);
            assert checkGLError() : glErrorMsg("glBindTexture(II)", textureId, target);

            LOGGER.trace(GL_MARKER, "glGenerateMipmap({})", target);
            GL30.glGenerateMipmap(target);
            assert checkGLError() : glErrorMsg("glGenerateMipmap(I)", target);
        } else if (cap.GL_ARB_framebuffer_object) {
            LOGGER.trace(GL_MARKER, "glBindTexture({}, {})", target, textureId);
            GL11.glBindTexture(target, textureId);
            assert checkGLError() : glErrorMsg("glBindTexture(II)", textureId, target);

            LOGGER.trace(GL_MARKER, "glGenerateMipmap({})", target);
            ARBFramebufferObject.glGenerateMipmap(target);
            assert checkGLError() : glErrorMsg("glGenerateMipmapARB(I)", target);
        } else if (cap.GL_EXT_framebuffer_object) {
            LOGGER.trace(GL_MARKER, "glBindTexture({}, {})", target, textureId);
            GL11.glBindTexture(target, textureId);
            assert checkGLError() : glErrorMsg("glBindTexture(II)", textureId, target);

            LOGGER.trace(GL_MARKER, "glGenerateMipmap({})", target);
            EXTFramebufferObject.glGenerateMipmapEXT(target);
            assert checkGLError() : glErrorMsg("glGenerateMipmapEXT(I)", target);
        } else {
            throw unsupportedFramebufferObject();
        }
    }

    @Override
    public void glBindTextureUnit(int unit, int target, int textureId) {
        LOGGER.trace(GL_MARKER, "glActiveTexture({})", unit);
        GL13.glActiveTexture(unit);
        assert checkGLError() : glErrorMsg("glActiveTexture(I)", unit);

        LOGGER.trace(GL_MARKER, "glBindTexture({}, {})", target, textureId);
        GL11.glBindTexture(target, textureId);
        assert checkGLError() : glErrorMsg("glBindTexture(II)", GLTextureTarget.of(target).get(), textureId);
    }

    @Override
    public void glTextureImage1d(int texture, int target, int level, int internalFormat, int width, int border, int format, int type, long ptr) {
        LOGGER.trace(GL_MARKER, "glBindTexture({}, {})", target, texture);
        GL11.glBindTexture(target, texture);
        assert checkGLError() : glErrorMsg("glBindTexture(II)", GLTextureTarget.of(target).get(), texture);

        LOGGER.trace(GL_MARKER, "glTexImage1D({}, {}, {}, {}, {}, {}, {}, {})", target, level, internalFormat, width, border, format, type, ptr);
        GL11.glTexImage1D(target, level, internalFormat, width, border, format, type, ptr);
        assert checkGLError() : glErrorMsg("glTexImage1D(IIIIIIIL)", GLTextureTarget.of(target).get(), level, GLTextureInternalFormat.of(internalFormat).get(), width, border, GLTextureFormat.of(format).get(), GLType.of(type).get(), ptr);

    }

    @Override
    public void glTextureImage1d(int texture, int target, int level, int internalFormat, int width, int border, int format, int type, ByteBuffer pixels) {
        LOGGER.trace(GL_MARKER, "glBindTexture({}, {})", target, texture);
        GL11.glBindTexture(target, texture);
        assert checkGLError() : glErrorMsg("glBindTexture(II)", GLTextureTarget.of(target).get(), texture);

        LOGGER.trace(GL_MARKER, "glTexImage1D({}, {}, {}, {}, {}, {}, {}, {})", target, level, internalFormat, width, border, format, type, pixels);
        GL11.glTexImage1D(target, level, internalFormat, width, border, format, type, pixels);
        assert checkGLError() : glErrorMsg("glTexImage1D(IIIIIII*)", GLTextureTarget.of(target).get(), level, GLTextureInternalFormat.of(internalFormat).get(), width, border, GLTextureFormat.of(format).get(), GLType.of(type).get(), toHexString(memAddress(pixels)));
    }

    @Override
    public void glTextureImage2d(int texture, int target, int level, int internalFormat, int width, int height, int border, int format, int type, ByteBuffer pixels) {
        LOGGER.trace(GL_MARKER, "glBindTexture({}, {})", target, texture);
        GL11.glBindTexture(target, texture);
        assert checkGLError() : glErrorMsg("glBindTexture(II)", GLTextureTarget.of(target).get(), texture);

        LOGGER.trace(GL_MARKER, "glTexImage2D({}, {}, {}, {}, {}, {}, {}, {}, {})", target, level, internalFormat, width, height, border, format, type, pixels);
        GL11.glTexImage2D(target, level, internalFormat, width, height, border, format, type, pixels);
        assert checkGLError() : glErrorMsg("glTexImage2D(IIIIIIII*)", GLTextureTarget.of(target).get(), level, GLTextureInternalFormat.of(internalFormat).get(), width, height, border, GLTextureFormat.of(format).get(), GLType.of(type).get(), toHexString(memAddress(pixels)));
    }

    @Override
    public void glTextureImage2d(int texture, int target, int level, int internalFormat, int width, int height, int border, int format, int type, long ptr) {
        LOGGER.trace(GL_MARKER, "glBindTexture({}, {})", target, texture);
        GL11.glBindTexture(target, texture);
        assert checkGLError() : glErrorMsg("glBindTexture(II)", GLTextureTarget.of(target).get(), texture);

        LOGGER.trace(GL_MARKER, "glTexImage2D({}, {}, {}, {}, {}, {}, {}, {}, {})", target, level, internalFormat, width, height, border, format, type, ptr);
        GL11.glTexImage2D(target, level, internalFormat, width, height, border, format, type, ptr);
        assert checkGLError() : glErrorMsg("glTexImage2D(IIIIIIIIL)", GLTextureTarget.of(target).get(), level, GLTextureInternalFormat.of(internalFormat).get(), width, height, border, GLTextureFormat.of(format).get(), GLType.of(type).get(), ptr);
    }

    @Override
    public void glTextureImage3d(int texture, int target, int level, int internalFormat, int width, int height, int depth, int border, int format, int type, ByteBuffer pixels) {
        LOGGER.trace(GL_MARKER, "glBindTexture({}, {})", target, texture);
        GL11.glBindTexture(target, texture);
        assert checkGLError() : glErrorMsg("glBindTexture(II)", GLTextureTarget.of(target).get(), texture);

        LOGGER.trace(GL_MARKER, "glTexImage3D({}, {}, {}, {}, {}, {}, {}, {}, {}, {})", target, level, internalFormat, width, height, depth, border, format, type, pixels);
        GL12.glTexImage3D(target, level, internalFormat, width, height, depth, border, format, type, pixels);
        assert checkGLError() : glErrorMsg("glTexImage3D(IIIIIIIII*)", GLTextureTarget.of(target).get(), level, GLTextureInternalFormat.of(internalFormat).get(), width, height, depth, border, GLTextureFormat.of(format).get(), GLType.of(type).get(), toHexString(memAddress(pixels)));
    }

    @Override
    public void glTextureImage3d(int texture, int target, int level, int internalFormat, int width, int height, int depth, int border, int format, int type, long ptr) {
        LOGGER.trace(GL_MARKER, "glBindTexture({}, {})", target, texture);
        GL11.glBindTexture(target, texture);
        assert checkGLError() : glErrorMsg("glBindTexture(II)", GLTextureTarget.of(target).get(), texture);

        LOGGER.trace(GL_MARKER, "glTexImage3D({}, {}, {}, {}, {}, {}, {}, {}, {}, {})", target, level, internalFormat, width, height, depth, border, format, type, ptr);
        GL12.glTexImage3D(target, level, internalFormat, width, height, depth, border, format, type, ptr);
        assert checkGLError() : glErrorMsg("glTexImage3D(IIIIIIIIIL)", GLTextureTarget.of(target).get(), level, GLTextureInternalFormat.of(internalFormat).get(), width, height, depth, border, GLTextureFormat.of(format).get(), GLType.of(type).get(), ptr);
    }

    @Override
    public boolean isSupported() {
        final GLCapabilities cap = GL.getCapabilities();

        final boolean hasDoubleSupport = cap.OpenGL40 || cap.GL_ARB_gpu_shader_fp64 || CAN_CAST_DOUBLE_TO_FLOAT;
        final boolean hasFramebufferSupport = cap.OpenGL30 || cap.GL_ARB_framebuffer_object || cap.GL_EXT_framebuffer_object || IGNORE_FRAMEBUFFER_SUPPORT;
        final boolean hasBufferStorageSupport = cap.OpenGL44 || cap.GL_ARB_buffer_storage || IGNORE_BUFFER_STORAGE_SUPPORT;
        final boolean hasShaderSupport = cap.OpenGL20 || cap.GL_ARB_shader_objects;

        return cap.OpenGL15 && hasDoubleSupport && hasFramebufferSupport && hasBufferStorageSupport && hasShaderSupport;
    }

    @Override
    public int glCreateBuffers() {
        LOGGER.trace(GL_MARKER, "glGenBuffers()");
        final int id = GL15.glGenBuffers();
        assert checkGLError() : glErrorMsg("glGenBuffers(void)");

        LOGGER.trace(GL_MARKER, "glBindBuffer(GL_ARRAY_BUFFER, {})", id);
        GL15.glBindBuffer(GL15.GL_ARRAY_BUFFER, id);
        assert checkGLError() : glErrorMsg("glBindBuffer(II)", "GL_ARRAY_BUFFER", id);

        return id;
    }

    @Override
    public int glCreateTextures(int target) {
        LOGGER.trace(GL_MARKER, "glGenTextures()");
        final int id = GL11.glGenTextures();
        assert checkGLError() : glErrorMsg("glGenTextures(void)");

        LOGGER.trace(GL_MARKER, "glBindTexture({}, {})", target, id);
        GL11.glBindTexture(target, id);
        assert checkGLError() : glErrorMsg("glBindTexture(II)", GLTextureTarget.of(target).get(), id);

        return id;
    }

    @Override
    public int glCreateFramebuffers() {
        final GLCapabilities cap = GL.getCapabilities();

        if (cap.OpenGL30) {
            LOGGER.trace(GL_MARKER, "glGenFramebuffers()");
            final int out = GL30.glGenFramebuffers();

            LOGGER.trace(GL_MARKER, "glBindFramebuffer(GL_FRAMEBUFFER, {})", out);
            GL30.glBindFramebuffer(GL30.GL_FRAMEBUFFER, out);
            return out;
        } else if (cap.GL_ARB_framebuffer_object) {
            LOGGER.trace(GL_MARKER, "glGenFramebuffers()");
            final int out = ARBFramebufferObject.glGenRenderbuffers();

            LOGGER.trace(GL_MARKER, "glBindFramebuffer(GL_FRAMEBUFFER, {})", out);
            ARBFramebufferObject.glBindFramebuffer(ARBFramebufferObject.GL_FRAMEBUFFER, out);
            return out;
        } else if (cap.GL_EXT_framebuffer_object) {
            LOGGER.trace(GL_MARKER, "glGenFramebuffersEXT()");
            final int out = EXTFramebufferObject.glGenFramebuffersEXT();

            LOGGER.trace(GL_MARKER, "glBindFramebufferEXT(GL_FRAMEBUFFER_EXT, {})", out);
            EXTFramebufferObject.glBindFramebufferEXT(EXTFramebufferObject.GL_FRAMEBUFFER_EXT, out);
            return out;
        } else {
            throw unsupportedFramebufferObject();
        }
    }

    @Override
    public void glNamedFramebufferTexture(int framebuffer, int attachment, int texture, int level) {
        throw new UnsupportedOperationException();
    }

    @Override
    public void glNamedBufferData(int bufferId, long size, int usage) {
        LOGGER.trace(GL_MARKER, "glBindBuffer(GL_ARRAY_BUFFER, {})", bufferId);
        GL15.glBindBuffer(GL15.GL_ARRAY_BUFFER, bufferId);
        assert checkGLError() : glErrorMsg("glBindBuffer(II)", "GL_ARRAY_BUFFER", bufferId);

        LOGGER.trace(GL_MARKER, "glBufferData(GL_ARRAY_BUFFER, {}, {})", size, usage);
        GL15.glBufferData(GL15.GL_ARRAY_BUFFER, size, usage);
        assert checkGLError() : glErrorMsg("glBufferData(ILI)", "GL_ARRAY_BUFFER", size, GLBufferUsage.of(usage).get());
    }

    @Override
    public void glNamedBufferData(int bufferId, ByteBuffer data, int usage) {
        LOGGER.trace(GL_MARKER, "glBindBuffer(GL_ARRAY_BUFFER, {})", bufferId);
        GL15.glBindBuffer(GL15.GL_ARRAY_BUFFER, bufferId);
        assert checkGLError() : glErrorMsg("glBindBuffer(II)", "GL_ARRAY_BUFFER", bufferId);

        LOGGER.trace(GL_MARKER, "glBufferData(GL_ARRAY_BUFFER, {}, {})", data, usage);
        GL15.glBufferData(GL15.GL_ARRAY_BUFFER, data, usage);
        assert checkGLError() : glErrorMsg("glBufferData(I*I)", bufferId, toHexString(memAddress(data)), GLBufferUsage.of(usage).get());
    }

    @Override
    public void glNamedBufferSubData(int bufferId, long offset, ByteBuffer data) {
        LOGGER.trace(GL_MARKER, "glBindBuffer(GL_ARRAY_BUFFER, {})", bufferId);
        GL15.glBindBuffer(GL15.GL_ARRAY_BUFFER, bufferId);
        assert checkGLError() : glErrorMsg("glBindBuffer(II)", "GL_ARRAY_BUFFER", bufferId);

        LOGGER.trace(GL_MARKER, "glBufferSubData(GL_ARRAY_BUFFER, {}, {})", offset, data);
        GL15.glBufferSubData(GL15.GL_ARRAY_BUFFER, offset, data);
        assert checkGLError() : glErrorMsg("glBufferSubData(IL*)", "GL_ARRAY_BUFFER", offset, toHexString(memAddress(data)));
    }

    @Override
    public void glNamedBufferStorage(int bufferId, ByteBuffer data, int flags) {
        final GLCapabilities cap = GL.getCapabilities();

        LOGGER.trace(GL_MARKER, "glBindBuffer(GL_ARRAY_BUFFER, {})", bufferId);
        GL15.glBindBuffer(GL15.GL_ARRAY_BUFFER, bufferId);
        assert checkGLError() : glErrorMsg("glBindBuffer(II)", "GL_ARRAY_BUFFER", bufferId);

        if (cap.OpenGL44) {
            LOGGER.trace(GL_MARKER, "glBufferStorage(GL_ARRAY_BUFFER, {}, {})", data, flags);
            GL44.glBufferStorage(GL15.GL_ARRAY_BUFFER, data, flags);
            assert checkGLError() : glErrorMsg("glBufferStorage(I*I)", "GL_ARRAY_BUFFER", toHexString(memAddress(data)), flags);
        } else if (cap.GL_ARB_buffer_storage) {
            LOGGER.trace(GL_MARKER, "glBufferStorage(GL_ARRAY_BUFFER, {}, {}) (ARB)", data, flags);
            ARBBufferStorage.glBufferStorage(GL15.GL_ARRAY_BUFFER, data, flags);
            assert checkGLError() : glErrorMsg("glBufferStorageARB(I*I)", "GL_ARRAY_BUFFER", toHexString(memAddress(data)), flags);
        } else {
            throw unsupportedBufferStorage();
        }
    }

    @Override
    public void glNamedBufferStorage(int bufferId, long size, int flags) {
        final GLCapabilities cap = GL.getCapabilities();

        LOGGER.trace(GL_MARKER, "glBindBuffer(GL_ARRAY_BUFFER, {})", bufferId);
        GL15.glBindBuffer(GL15.GL_ARRAY_BUFFER, bufferId);
        assert checkGLError() : glErrorMsg("glBindBuffer(II)", "GL_ARRAY_BUFFER", bufferId);

        if (cap.OpenGL44) {
            LOGGER.trace(GL_MARKER, "glBufferStorage(GL_ARRAY_BUFFER, {}, {})", size, flags);
            GL44.glBufferStorage(GL15.GL_ARRAY_BUFFER, size, flags);
            assert checkGLError() : glErrorMsg("glBufferStorage(ILI)", "GL_ARRAY_BUFFER", size, flags);
        } else if (cap.GL_ARB_buffer_storage) {
            LOGGER.trace(GL_MARKER, "glBufferStorage(GL_ARRAY_BUFFER, {}, {}) (ARB)", size, flags);
            ARBBufferStorage.glBufferStorage(GL15.GL_ARRAY_BUFFER, size, flags);
            assert checkGLError() : glErrorMsg("glBufferStorageARB(ILI)", "GL_ARRAY_BUFFER", size, flags);
        } else {
            throw unsupportedBufferStorage();
        }
    }

    @Override
    public void glGetNamedBufferSubData(int bufferId, long offset, ByteBuffer data) {
        LOGGER.trace(GL_MARKER, "glBindBuffer(GL_ARRAY_BUFFER, {})", bufferId);
        GL15.glBindBuffer(GL15.GL_ARRAY_BUFFER, bufferId);
        assert checkGLError() : glErrorMsg("glBindBuffer(II)", "GL_ARRAY_BUFFER", bufferId);

        LOGGER.trace(GL_MARKER, "glGetBufferSubData(GL_ARRAY_BUFFER, {}, {})", offset, data);
        GL15.glGetBufferSubData(GL15.GL_ARRAY_BUFFER, offset, data);
        assert checkGLError() : glErrorMsg("glGetBufferSubData(IL*)", "GL_ARRAY_BUFFER", offset, toHexString(memAddress(data)));
    }

    @Override
    public ByteBuffer glMapNamedBufferRange(int bufferId, long offset, long length, int access, ByteBuffer recycled) {
        LOGGER.trace(GL_MARKER, "glBindBuffer(GL_ARRAY_BUFFER, {})", bufferId);
        GL15.glBindBuffer(GL15.GL_ARRAY_BUFFER, bufferId);
        assert checkGLError() : glErrorMsg("glBindBuffer(II)", "GL_ARRAY_BUFFER", bufferId);

        LOGGER.trace(GL_MARKER, "glMapBufferRange(GL_ARRAY_BUFFER, {}, {}, {})", offset, length, access);
        final ByteBuffer out = GL30.glMapBufferRange(GL15.GL_ARRAY_BUFFER, offset, length, access, recycled);
        assert checkGLError() : glErrorMsg("glMapBufferRange(ILLI)", "GL_ARRAY_BUFFER", offset, length, access);

        return out;
    }

    @Override
    public void glUnmapNamedBuffer(int bufferId) {
        LOGGER.trace(GL_MARKER, "glBindBuffer(GL_ARRAY_BUFFER, {})", bufferId);
        GL15.glBindBuffer(GL15.GL_ARRAY_BUFFER, bufferId);
        assert checkGLError() : glErrorMsg("glBindBuffer(II)", "GL_ARRAY_BUFFER", bufferId);

        LOGGER.trace(GL_MARKER, "glUnmapBuffer(GL_ARRAY_BUFFER)");
        GL15.glUnmapBuffer(GL15.GL_ARRAY_BUFFER);
        assert checkGLError() : glErrorMsg("glUnmapBuffer(I)", "GL_ARRAY_BUFFER");
    }

    @Override
    public void glCopyNamedBufferSubData(int readBufferId, int writeBufferId, long readOffset, long writeOffset, long size) {
        final GLCapabilities cap = GL.getCapabilities();

        if (cap.OpenGL31) {
            LOGGER.trace(GL_MARKER, "glBindBuffer(GL_COPY_READ_BUFFER, {})", readBufferId);
            GL15.glBindBuffer(GL31.GL_COPY_READ_BUFFER, readBufferId);
            assert checkGLError() : glErrorMsg("glBindBuffer(II)", "GL_COPY_READ_BUFFER", readBufferId);

            LOGGER.trace(GL_MARKER, "glBindBuffer(GL_COPY_WRITE_BUFFER, {})", writeBufferId);
            GL15.glBindBuffer(GL31.GL_COPY_WRITE_BUFFER, writeBufferId);
            assert checkGLError() : glErrorMsg("glBindBuffer(II)", "GL_COPY_WRITE_BUFFER", writeBufferId);

            LOGGER.trace(GL_MARKER, "glCopyBufferSubData(GL_COPY_READ_BUFFER, GL_COPY_WRITE_BUFFER, {}, {}, {})", readOffset, writeOffset, size);
            GL31.glCopyBufferSubData(GL31.GL_COPY_READ_BUFFER, GL31.GL_COPY_WRITE_BUFFER, readOffset, writeOffset, size);
            assert checkGLError() : glErrorMsg("glCopyBufferSubData(IILLL)", "GL_COPY_READ_BUFFER", "GL_COPY_WRITE_BUFFER", readOffset, writeOffset, size);

            LOGGER.trace(GL_MARKER, "glBindBuffer(GL_COPY_READ_BUFFER, 0)");
            GL15.glBindBuffer(GL31.GL_COPY_READ_BUFFER, 0);
            assert checkGLError() : glErrorMsg("glBindBuffer(II)", "GL_COPY_READ_BUFFER", 0);

            LOGGER.trace(GL_MARKER, "glBindBuffer(GL_COPY_WRITE_BUFFER, 0)");
            GL15.glBindBuffer(GL31.GL_COPY_WRITE_BUFFER, 0);
            assert checkGLError() : glErrorMsg("glBindBuffer(II)", "GL_COPY_WRITE_BUFFER", 0);
        } else if (cap.GL_ARB_copy_buffer) {
            LOGGER.trace(GL_MARKER, "glBindBuffer(GL_COPY_READ_BUFFER, {})", readBufferId);
            GL15.glBindBuffer(GL31.GL_COPY_READ_BUFFER, readBufferId);
            assert checkGLError() : glErrorMsg("glBindBuffer(II)", "GL_COPY_READ_BUFFER", readBufferId);

            LOGGER.trace(GL_MARKER, "glBindBuffer(GL_COPY_WRITE_BUFFER, {})", writeBufferId);
            GL15.glBindBuffer(GL31.GL_COPY_WRITE_BUFFER, writeBufferId);
            assert checkGLError() : glErrorMsg("glBindBuffer(II)", "GL_COPY_WRITE_BUFFER", writeBufferId);

            LOGGER.trace(GL_MARKER, "glCopyBufferSubData(GL_COPY_READ_BUFFER, GL_COPY_WRITE_BUFFER, {}, {}, {}) (ARB)", readOffset, writeOffset, size);
            ARBCopyBuffer.glCopyBufferSubData(GL31.GL_COPY_READ_BUFFER, GL31.GL_COPY_WRITE_BUFFER, readOffset, writeOffset, size);
            assert checkGLError() : glErrorMsg("glCopyBufferSubData(IILLL)", "GL_COPY_READ_BUFFER", "GL_COPY_WRITE_BUFFER", readOffset, writeOffset, size);

            LOGGER.trace(GL_MARKER, "glBindBuffer(GL_COPY_READ_BUFFER, 0)");
            GL15.glBindBuffer(GL31.GL_COPY_READ_BUFFER, 0);
            assert checkGLError() : glErrorMsg("glBindBuffer(II)", "GL_COPY_READ_BUFFER", 0);

            LOGGER.trace(GL_MARKER, "glBindBuffer(GL_COPY_WRITE_BUFFER, 0)");
            GL15.glBindBuffer(GL31.GL_COPY_WRITE_BUFFER, 0);
            assert checkGLError() : glErrorMsg("glBindBuffer(II)", "GL_COPY_WRITE_BUFFER", 0);
        } else {
            throw new UnsupportedOperationException("Copy Buffer requires either an OpenGL 3.0 context or ARB_copy_buffer.");
        }
    }

    @Override
    public void glProgramUniform1f(int programId, int location, float value) {
        glUseProgram(programId);
        glUniform1f(location, value);
    }

    @Override
    public void glProgramUniform2f(int programId, int location, float v0, float v1) {
        glUseProgram(programId);
        glUniform2f(location, v0, v1);
    }

    @Override
    public void glProgramUniform3f(int programId, int location, float v0, float v1, float v2) {
        glUseProgram(programId);
        glUniform3f(location, v0, v1, v2);
    }

    @Override
    public void glProgramUniform4f(int programId, int location, float v0, float v1, float v2, float v3) {
        glUseProgram(programId);
        glUniform4f(location, v0, v1, v2, v3);
    }

    @Override
    public void glProgramUniform1i(int programId, int location, int value) {
        glUseProgram(programId);
        glUniform1i(location, value);
    }

    @Override
    public void glProgramUniform2i(int programId, int location, int v0, int v1) {
        glUseProgram(programId);
        glUniform2i(location, v0, v1);
    }

    @Override
    public void glProgramUniform3i(int programId, int location, int v0, int v1, int v2) {
        glUseProgram(programId);
        glUniform3i(location, v0, v1, v2);
    }

    @Override
    public void glProgramUniform4i(int programId, int location, int v0, int v1, int v2, int v3) {
        glUseProgram(programId);
        glUniform4i(location, v0, v1, v2, v3);
    }

    @Override
    public void glProgramUniform1d(int programId, int location, double value) {
        final GLCapabilities cap = GL.getCapabilities();

        glUseProgram(programId);

        if (cap.OpenGL40) {
            LOGGER.trace(GL_MARKER, "glUniform1d({}, {})", location, value);
            GL40.glUniform1d(location, value);
            assert checkGLError() : glErrorMsg("glUniform1d(ID)", location, value);
        } else if (cap.GL_ARB_gpu_shader_fp64) {
            LOGGER.trace(GL_MARKER, "glUniform1d({}, {}) (ARB)", location, value);
            ARBGPUShaderFP64.glUniform1d(location, value);
            assert checkGLError() : glErrorMsg("glUniform1dARB(ID)", location, value);
        } else if (CAN_CAST_DOUBLE_TO_FLOAT) {
            glUniform1f(location, (float) value);
        } else {
            throw unsupportedGPUShaderFP64();
        }
    }

    @Override
    public void glProgramUniform2d(int programId, int location, double v0, double v1) {
        final GLCapabilities cap = GL.getCapabilities();

        glUseProgram(programId);

        if (cap.OpenGL40) {
            LOGGER.trace(GL_MARKER, "glUniform2d({}, {}, {})", location, v0, v1);
            GL40.glUniform2d(location, v0, v1);
            assert checkGLError() : glErrorMsg("glUniform2d(IDD)", location, v0, v1);
        } else if (cap.GL_ARB_gpu_shader_fp64) {
            LOGGER.trace(GL_MARKER, "glUniform2d({}, {}, {}) (ARB)", location, v0, v1);
            ARBGPUShaderFP64.glUniform2d(location, v0, v1);
            assert checkGLError() : glErrorMsg("glUniform2dARB(IDD)", location, v0, v1);
        } else if (CAN_CAST_DOUBLE_TO_FLOAT) {
            glUniform2f(location, (float) v0, (float) v1);
        } else {
            throw unsupportedGPUShaderFP64();
        }
    }

    @Override
    public void glProgramUniform3d(int programId, int location, double v0, double v1, double v2) {
        final GLCapabilities cap = GL.getCapabilities();

        glUseProgram(programId);

        if (cap.OpenGL40) {
            LOGGER.trace(GL_MARKER, "glUniform3d({}, {}, {}, {})", location, v0, v1, v2);
            GL40.glUniform3d(location, v0, v1, v2);
            assert checkGLError() : glErrorMsg("glUniform3d(IDDD)", location, v0, v1, v2);
        } else if (cap.GL_ARB_gpu_shader_fp64) {
            LOGGER.trace(GL_MARKER, "glUniform3d({}, {}, {}, {}) (ARB)", location, v0, v1, v2);
            ARBGPUShaderFP64.glUniform3d(location, v0, v1, v2);
            assert checkGLError() : glErrorMsg("glUniform3dARB(IDDD)", location, v0, v1, v2);
        } else if (CAN_CAST_DOUBLE_TO_FLOAT) {
            glUniform3f(location, (float) v0, (float) v1, (float) v2);
        } else {
            throw unsupportedGPUShaderFP64();
        }
    }

    @Override
    public void glProgramUniform4d(int programId, int location, double v0, double v1, double v2, double v3) {
        final GLCapabilities cap = GL.getCapabilities();

        glUseProgram(programId);

        if (cap.OpenGL40) {
            LOGGER.trace(GL_MARKER, "glUniform4d({}, {}, {}, {}, {})", location, v0, v1, v2, v3);
            GL40.glUniform4d(location, v0, v1, v2, v3);
            assert checkGLError() : glErrorMsg("glUniform4d(IDDDD)", location, v0, v1, v2, v3);
        } else if (cap.GL_ARB_gpu_shader_fp64) {
            LOGGER.trace(GL_MARKER, "glUniform4d({}, {}, {}, {}, {}) (ARB)", location, v0, v1, v2, v3);
            ARBGPUShaderFP64.glUniform4d(location, v0, v1, v2, v3);
            assert checkGLError() : glErrorMsg("glUniform4dARB(IDDDD)", location, v0, v1, v2, v3);
        } else if (CAN_CAST_DOUBLE_TO_FLOAT) {
            glUniform4f(location, (float) v0, (float) v1, (float) v2, (float) v3);
        } else {
            throw unsupportedGPUShaderFP64();
        }
    }

    @Override
    public void glProgramUniformMatrix2f(int programId, int location, boolean needsTranspose, FloatBuffer data) {
        glUseProgram(programId);
        glUniformMatrix2fv(location, needsTranspose, data);
    }

    @Override
    public void glProgramUniformMatrix3f(int programId, int location, boolean needsTranspose, FloatBuffer data) {
        glUseProgram(programId);
        glUniformMatrix3fv(location, needsTranspose, data);
    }

    @Override
    public void glProgramUniformMatrix4f(int programId, int location, boolean needsTranspose, FloatBuffer data) {
        glUseProgram(programId);
        glUniformMatrix4fv(location, needsTranspose, data);
    }

    @Override
    public void glProgramUniformMatrix2d(int programId, int location, boolean needsTranspose, DoubleBuffer data) {
        final GLCapabilities cap = GL.getCapabilities();

        glUseProgram(programId);
        if (cap.OpenGL40) {
            LOGGER.trace(GL_MARKER, "glUniformMatrix2dv({}, {}, {})", location, needsTranspose, data);
            GL40.glUniformMatrix2dv(location, needsTranspose, data);
            assert checkGLError() : glErrorMsg("glUniformMatrix2dv(IB*)", location, needsTranspose, toHexString(memAddress(data)));
        } else if (cap.GL_ARB_gpu_shader_fp64) {
            LOGGER.trace(GL_MARKER, "glUniformMatrix2dv({}, {}, {}) (ARB)", location, needsTranspose, data);
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

            glUniformMatrix2fv(location, needsTranspose, casted);
        } else {
            throw unsupportedGPUShaderFP64();
        }
    }

    @Override
    public void glProgramUniformMatrix3d(int programId, int location, boolean needsTranspose, DoubleBuffer data) {
        final GLCapabilities cap = GL.getCapabilities();

        glUseProgram(programId);

        if (cap.OpenGL40) {
            LOGGER.trace(GL_MARKER, "glUniformMatrix3dv({}, {}, {})", location, needsTranspose, data);
            GL40.glUniformMatrix3dv(location, needsTranspose, data);
            assert checkGLError() : glErrorMsg("glUniformMatrix3dv(IB*)", location, needsTranspose, toHexString(memAddress(data)));
        } else if (cap.GL_ARB_gpu_shader_fp64) {
            LOGGER.trace(GL_MARKER, "glUniformMatrix3dv({}, {}, {})", location, needsTranspose, data);
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

            glUniformMatrix3fv(location, needsTranspose, casted);
        } else {
            throw unsupportedGPUShaderFP64();
        }
    }

    @Override
    public void glProgramUniformMatrix4d(int programId, int location, boolean needsTranspose, DoubleBuffer data) {
        final GLCapabilities cap = GL.getCapabilities();

        glUseProgram(programId);

        if (cap.OpenGL40) {
            LOGGER.trace(GL_MARKER, "glUniformMatrix4dv({}, {}, {})", location, needsTranspose, data);
            GL40.glUniformMatrix4dv(location, needsTranspose, data);
            assert checkGLError() : glErrorMsg("glUniformMatrix4dv(IB*)", location, needsTranspose, toHexString(memAddress(data)));
        } else if (cap.GL_ARB_gpu_shader_fp64) {
            LOGGER.trace(GL_MARKER, "glUniformMatrix4dv({}, {}, {}) (ARB)", location, needsTranspose, data);
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

            glUniformMatrix4fv(location, needsTranspose, casted);
        } else {
            throw new UnsupportedOperationException("glUniformMatrix4dv(location, needsTranspose, data) is not supported!");
        }
    }

    @Override
    public void glTextureSubImage1d(int textureId, int level, int xOffset, int width, int format, int type, ByteBuffer pixels) {
        LOGGER.trace(GL_MARKER, "glBindTexture(GL_TEXTURE_1D, {})", textureId);
        GL11.glBindTexture(GL11.GL_TEXTURE_1D, textureId);
        assert checkGLError() : glErrorMsg("glBindTexture(II)", "GL_TEXTURE_1D", textureId);

        LOGGER.trace(GL_MARKER, "glTexSubImage1D(GL_TEXTURE_1D, {}, {}, {}, {}, {}, {})", level, xOffset, width, format, type, pixels);
        GL11.glTexSubImage1D(GL11.GL_TEXTURE_1D, level, xOffset, width, format, type, pixels);
        assert checkGLError() : glErrorMsg("glTexSubImage1D(IIIIII*)", "GL_TEXTURE_1D", level, xOffset, width, GLTextureFormat.of(format).get(), GLType.of(type).get(), toHexString(memAddress(pixels)));
    }

    @Override
    public void glTextureSubImage2d(int textureId, int level, int xOffset, int yOffset, int width, int height, int format, int type, ByteBuffer pixels) {
        LOGGER.trace(GL_MARKER, "glBindTexture(GL_TEXTURE_2D, {})", textureId);
        GL11.glBindTexture(GL11.GL_TEXTURE_2D, textureId);
        assert checkGLError() : glErrorMsg("glBindTexture(II)", "GL_TEXTURE_2D", textureId);

        LOGGER.trace(GL_MARKER, "glTexSubImage2D(GL_TEXTURE_2D, {}, {}, {}, {}, {}, {}, {}, {})", level, xOffset, yOffset, width, height, format, type, pixels);
        GL11.glTexSubImage2D(GL11.GL_TEXTURE_2D, level, xOffset, yOffset, width, height, format, type, pixels);
        assert checkGLError() : glErrorMsg("glTexSubImage2D(IIIIIIII*)", "GL_TEXTURE_2D", level, xOffset, yOffset, width, height, GLTextureFormat.of(format).get(), GLType.of(type).get(), toHexString(memAddress(pixels)));
    }

    @Override
    public void glTextureSubImage3d(int textureId, int level, int xOffset, int yOffset, int zOffset, int width, int height, int depth, int format, int type, ByteBuffer pixels) {
        LOGGER.trace(GL_MARKER, "glBindTexture(GL_TEXTURE_3D, {})", textureId);
        GL11.glBindTexture(GL12.GL_TEXTURE_3D, textureId);
        assert checkGLError() : glErrorMsg("glBindTexture(II)", "GL_TEXTURE_3D", textureId);

        LOGGER.trace(GL_MARKER, "glTexSubImage3D(GL_TEXTURE_3D, {}, {}, {}, {}, {}, {}, {}, {}, {})", level, xOffset, yOffset, zOffset, width, height, depth, format, type, pixels);
        GL12.glTexSubImage3D(GL12.GL_TEXTURE_3D, level, xOffset, yOffset, zOffset, width, height, depth, format, type, pixels);
        assert checkGLError() : glErrorMsg("glTexSubImage3D(IIIIIIIIII*)", "GL_TEXTURE_3D", level, xOffset, yOffset, zOffset, width, height, depth, GLTextureFormat.of(format).get(), GLType.of(type).get(), toHexString(memAddress(pixels)));
    }

    @Override
    public int glGetNamedBufferParameteri(int bufferId, int pName) {
        LOGGER.trace(GL_MARKER, "glBindBuffer(GL_ARRAY_BUFFER, {})", bufferId);
        GL15.glBindBuffer(GL15.GL_ARRAY_BUFFER, bufferId);
        assert checkGLError() : glErrorMsg("glBindBuffer(II)", "GL_ARRAY_BUFFER", bufferId);

        LOGGER.trace(GL_MARKER, "glGetBufferParameteri(GL_ARRAY_BUFFER, {})", pName);
        final int val = GL15.glGetBufferParameteri(GL15.GL_ARRAY_BUFFER, pName);
        assert checkGLError() : glErrorMsg("glGetBufferParameteri(II)", "GL_ARRAY_BUFFER", GLBufferParameterName.of(pName).get());

        return val;
    }

    @Override
    public void glBlitNamedFramebuffer(int readFramebuffer, int drawFramebuffer, int srcX0, int srcY0, int srcX1, int srcY1, int dstX0, int dstY0, int dstX1, int dstY1, int mask, int filter) {
        final GLCapabilities cap = GL.getCapabilities();

        if (cap.OpenGL30) {
            LOGGER.trace(GL_MARKER, "glBindFramebuffer(GL_READ_FRAMEBUFFER, {})", readFramebuffer);
            GL30.glBindFramebuffer(GL30.GL_READ_FRAMEBUFFER, readFramebuffer);
            assert checkGLError() : glErrorMsg("glBindFramebuffer(II)", "GL_READ_FRAMEBUFFER", readFramebuffer);

            LOGGER.trace(GL_MARKER, "glBindFramebuffer(GL_DRAW_FRAMEBUFFER, {})", drawFramebuffer);
            GL30.glBindFramebuffer(GL30.GL_DRAW_FRAMEBUFFER, drawFramebuffer);
            assert checkGLError() : glErrorMsg("glBindFramebuffer(II)", "GL_DRAW_FRAMEBUFFER", drawFramebuffer);

            LOGGER.trace(GL_MARKER, "glBlitFramebuffer({}, {}, {}, {}, {}, {}, {}, {}, {}, {})", srcX0, srcY0, srcX1, srcY1, dstX0, dstY0, dstX1, dstY1, mask, filter);
            GL30.glBlitFramebuffer(srcX0, srcY0, srcX1, srcY1, dstX0, dstY0, dstX1, dstY1, mask, filter);
            assert checkGLError() : glErrorMsg("glBlitFramebuffer(IIIIIIIIII)", srcX0, srcY0, srcX1, srcY1, dstX0, dstY0, dstX1, dstY1, mask, filter);
        } else if (cap.GL_ARB_framebuffer_object) {
            LOGGER.trace(GL_MARKER, "glBindFramebuffer(GL_DRAW_FRAMEBUFFER, {}) (ARB)", drawFramebuffer);
            ARBFramebufferObject.glBindFramebuffer(ARBFramebufferObject.GL_DRAW_FRAMEBUFFER, drawFramebuffer);
            assert checkGLError() : glErrorMsg("glBindFramebufferARB(II)", "GL_DRAW_FRAMEBUFFER", drawFramebuffer);

            LOGGER.trace(GL_MARKER, "glBindFramebuffer(GL_READ_FRAMEBUFFER, {}) (ARB)", readFramebuffer);
            ARBFramebufferObject.glBindFramebuffer(ARBFramebufferObject.GL_READ_FRAMEBUFFER, readFramebuffer);
            assert checkGLError() : glErrorMsg("glBindFramebufferARB(II)", "GL_READ_FRAMEBUFFER", readFramebuffer);

            LOGGER.trace(GL_MARKER, "glBlitFramebuffer({}, {}, {}, {}, {}, {}, {}, {}, {}, {}) (ARB)", srcX0, srcY0, srcX1, srcY1, dstX0, dstY0, dstX1, dstY1, mask, filter);
            ARBFramebufferObject.glBlitFramebuffer(srcX0, srcY0, srcX1, srcY1, dstX0, dstY0, dstX1, dstY1, mask, filter);
            assert checkGLError() : glErrorMsg("glBlitFramebufferARB(IIIIIIIIII)", srcX0, srcY0, srcX1, srcY1, dstX0, dstY0, dstX1, dstY1, mask, filter);
        } else if (cap.GL_EXT_framebuffer_object && cap.GL_EXT_framebuffer_blit) {
            LOGGER.trace(GL_MARKER, "glBindFramebufferEXT(GL_READ_FRAMEBUFFER_EXT, {})", readFramebuffer);
            EXTFramebufferObject.glBindFramebufferEXT(EXTFramebufferBlit.GL_READ_FRAMEBUFFER_EXT, readFramebuffer);
            assert checkGLError() : glErrorMsg("glBindFramebuffer(II)", "GL_READ_FRAMEBUFFER_EXT", readFramebuffer);

            LOGGER.trace(GL_MARKER, "glBindFramebufferEXT(GL_DRAW_FRAMEBUFFER, {})", drawFramebuffer);
            EXTFramebufferObject.glBindFramebufferEXT(EXTFramebufferBlit.GL_DRAW_FRAMEBUFFER_EXT, drawFramebuffer);
            assert checkGLError() : glErrorMsg("glBindFramebufferEXT(II)", "GL_DRAW_FRAMEBUFFER_EXT", drawFramebuffer);

            LOGGER.trace(GL_MARKER, "glBlitFramebufferEXT({}, {}, {}, {}, {}, {}, {}, {}, {}, {})", srcX0, srcY0, srcX1, srcY1, dstX0, dstY0, dstX1, dstY1, mask, filter);
            EXTFramebufferBlit.glBlitFramebufferEXT(srcX0, srcY0, srcX1, srcY1, dstX0, dstY0, dstX1, dstY1, mask, filter);
            assert checkGLError() : glErrorMsg("glBlitFramebufferEXT(IIIIIIIIII)", srcX0, srcY0, srcX1, srcY1, dstX0, dstY0, dstX1, dstY1, mask, filter);
        } else {
            throw new UnsupportedOperationException("Framebuffer blit requires either: an OpenGL 3.0 context, ARB_framebuffer_object, or (EXT_framebuffer_object and EXT_framebuffer_blit).");
        }
    }

    @Override
    public void glGetTextureImage(int texture, int target, int level, int format, int type, int bufferSize, ByteBuffer pixels) {
        LOGGER.trace(GL_MARKER, "glBindTexture({}, {})", target, texture);
        GL11.glBindTexture(target, texture);
        assert checkGLError() : glErrorMsg("glBindTexture(II)", GLTextureTarget.of(target).get(), texture);

        LOGGER.trace(GL_MARKER, "glGetTexImage({}, {}, {}, {}, {})", target, level, format, type, pixels);
        GL11.glGetTexImage(target, level, format, type, pixels);
        assert checkGLError() : glErrorMsg("glGetTexImage(IIIII)", GLTextureTarget.of(target).get(), level, GLTextureFormat.of(format).get(), GLType.of(type).get(), toHexString(memAddress(pixels)));
    }

    public static NoDSA getInstance() {
        return Holder.INSTANCE;
    }

    private NoDSA() {
    }

    @Override
    public void glNamedBufferReadPixels(int bufferId, int x, int y, int width, int height, int format, int type, long ptr) {
        LOGGER.trace(GL_MARKER, "glBindBuffer(GL_PIXEL_BUFFER, {})", bufferId);
        GL15.glBindBuffer(GL21.GL_PIXEL_PACK_BUFFER, bufferId);
        assert checkGLError() : glErrorMsg("glBindBuffer(II)", "GL_PIXEL_PACK_BUFFER", bufferId);

        LOGGER.trace(GL_MARKER, "glReadPixels({}, {}, {}, {}, {}, {}, {})", x, y, width, height, format, type, ptr);
        GL11.glReadPixels(x, y, width, height, format, type, ptr);
        assert checkGLError() : glErrorMsg("glReadPixels(IIIIIIL)", x, y, width, height, GLTextureFormat.of(format).get(), GLType.of(type).get(), ptr);
    }

    private static class Holder {

        private static final NoDSA INSTANCE = new NoDSA();
    }
}
