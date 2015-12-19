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

import java.nio.ByteBuffer;
import java.nio.IntBuffer;
import org.lwjgl.opengl.ARBComputeShader;
import org.lwjgl.opengl.ARBDrawIndirect;
import org.lwjgl.opengl.ARBDrawInstanced;
import org.lwjgl.opengl.ARBFramebufferObject;
import org.lwjgl.opengl.ARBInstancedArrays;
import org.lwjgl.opengl.ARBProgramInterfaceQuery;
import org.lwjgl.opengl.ARBSamplerObjects;
import org.lwjgl.opengl.ARBShaderStorageBufferObject;
import org.lwjgl.opengl.ARBVertexArrayObject;
import org.lwjgl.opengl.ContextCapabilities;
import org.lwjgl.opengl.EXTFramebufferObject;
import org.lwjgl.opengl.GL;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL14;
import org.lwjgl.opengl.GL15;
import org.lwjgl.opengl.GL20;
import org.lwjgl.opengl.GL30;
import org.lwjgl.opengl.GL31;
import org.lwjgl.opengl.GL33;
import org.lwjgl.opengl.GL40;
import org.lwjgl.opengl.GL41;
import org.lwjgl.opengl.GL43;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.Marker;
import org.slf4j.MarkerFactory;

/**
 *
 * @author zmichaels
 */
public abstract class Common {

    private static final Marker GL_MARKER = MarkerFactory.getMarker("OPENGL");
    private static final Logger LOGGER = LoggerFactory.getLogger("OPENGL");

    public final void glReadPixels(int x, int y, int w, int h, int format, int type, long ptr) {
        LOGGER.trace(GL_MARKER, "glReadPixels({}, {}, {}, {}, {}, {}, {})", x, y, w, h, format, type, ptr);
        GL11.glReadPixels(x, y, w, h, format, type, ptr);
        assert GL11.glGetError() == 0 : "glReadPixels(IIIIII*) failed!";
    }

    public final void glReadPixels(int x, int y, int w, int h, int format, int type, ByteBuffer buffer) {
        LOGGER.trace(GL_MARKER, "glReadPixels({}, {}, {}, {}, {}, {}, {})", x, y, w, h, format, type, buffer);
        GL11.glReadPixels(x, y, w, h, format, type, buffer);
        assert GL11.glGetError() == 0 : "glReadPixels(IIIIII*) failed!";
    }

    public final void glDrawBuffers(IntBuffer attachments) {
        LOGGER.trace(GL_MARKER, "glDrawBuffers({}), attachments");
        GL20.glDrawBuffers(attachments);
        assert GL11.glGetError() == 0 : "glDrawBuffers(I*) failed!";
    }

    public final void glDeleteFramebuffers(int fbId) {
        final ContextCapabilities cap = GL.getCapabilities();

        if (cap.OpenGL30) {
            LOGGER.trace(GL_MARKER, "glDeleteFramebuffers({})", fbId);
            GL30.glDeleteFramebuffers(fbId);
            assert GL11.glGetError() == 0 : "glDeleteFramebuffers(I) failed!";
        } else if (cap.GL_ARB_framebuffer_object) {
            LOGGER.trace(GL_MARKER, "glDeleteFramebuffers({}) (ARB)", fbId);
            ARBFramebufferObject.glDeleteFramebuffers(fbId);
            assert GL11.glGetError() == 0 : "glDeleteFramebuffers(I) (ARB) failed!";
        } else if (cap.GL_EXT_framebuffer_object) {
            LOGGER.trace(GL_MARKER, "glDeleteFramebuffersEXT({})", fbId);
            EXTFramebufferObject.glDeleteFramebuffersEXT(fbId);
            assert GL11.glGetError() == 0 : "glDeleteFramebuffersEXT(I) failed!";
        } else {
            throw new UnsupportedOperationException("glDeleteFramebuffers is not supported!");
        }
    }

    public final int glCheckFramebufferStatus(int fbId) {
        final ContextCapabilities cap = GL.getCapabilities();

        if (cap.OpenGL30) {
            LOGGER.trace(GL_MARKER, "glCheckFramebufferStatus({})", fbId);
            final int status = GL30.glCheckFramebufferStatus(fbId);
            assert GL11.glGetError() == 0 : "glCheckStatus(I) failed!";
            return status;
        } else if (cap.GL_ARB_framebuffer_object) {
            LOGGER.trace(GL_MARKER, "glCheckFramebufferStatus({}) (ARB)", fbId);
            final int status = ARBFramebufferObject.glCheckFramebufferStatus(fbId);
            assert GL11.glGetError() == 0 : "glCheckFramebufferStatus(I) (ARB) failed!";
            return status;
        } else if (cap.GL_EXT_framebuffer_object) {
            LOGGER.trace(GL_MARKER, "glCheckFramebufferStatusEXT({})", fbId);
            final int status = EXTFramebufferObject.glCheckFramebufferStatusEXT(fbId);
            assert GL11.glGetError() == 0 : "glCheckFramebufferStatusEXT(I) failed!";
            return status;
        } else {
            throw new UnsupportedOperationException("glDeleteFramebuffers is not supported!");
        }
    }

    public final void glBindFramebuffer(int target, int fb) {
        final ContextCapabilities cap = GL.getCapabilities();

        if (cap.OpenGL30) {
            LOGGER.trace(GL_MARKER, "glBindFramebuffer({}, {})", target, fb);
            GL30.glBindFramebuffer(target, fb);
            assert GL11.glGetError() == 0 : "glBindFramebuffer(II) failed!";
        } else if (cap.GL_ARB_framebuffer_object) {
            LOGGER.trace(GL_MARKER, "glBindFramebuffer({}, {}) (ARB)", target, fb);
            ARBFramebufferObject.glBindFramebuffer(target, fb);
            assert GL11.glGetError() == 0 : "glBindFramebuffer(II) (ARB) failed!";
        } else if (cap.GL_EXT_framebuffer_object) {
            LOGGER.trace(GL_MARKER, "glBindFramebufferEXT({}, {})", target, fb);
            EXTFramebufferObject.glBindFramebufferEXT(target, fb);
            assert GL11.glGetError() == 0 : "glBindFramebufferEXT(II) failed!";
        } else {
            throw new UnsupportedOperationException("glBindFramebuffer is not supported!");
        }
    }

    public final void glColorMask(boolean red, boolean green, boolean blue, boolean alpha) {
        LOGGER.trace(GL_MARKER, "glColorMask({}, {}, {}, {})", red, green, blue, alpha);
        GL11.glColorMask(red, green, blue, alpha);
        assert GL11.glGetError() == 0 : "glColorMask(BBBB) failed!";
    }

    public final void glDepthMask(boolean depth) {
        LOGGER.trace(GL_MARKER, "glDepthMask({})", depth);
        GL11.glDepthMask(depth);
        assert GL11.glGetError() == 0 : "glDepthMask(D) failed!";
    }

    public final void glStencilMask(int stencilMask) {
        LOGGER.trace(GL_MARKER, "glStencilMask({})", stencilMask);
        GL11.glStencilMask(stencilMask);
        assert GL11.glGetError() == 0 : "glStencilMask(I) failed!";
    }

    public final void glPointSize(float ps) {
        LOGGER.trace(GL_MARKER, "glPointSize({})", ps);
        GL11.glPointSize(ps);
        assert GL11.glGetError() == 0 : "glPointSize(F) failed!";
    }

    public final void glLineWidth(float lw) {
        LOGGER.trace(GL_MARKER, "glLineWidth({})", lw);
        GL11.glLineWidth(lw);
        assert GL11.glGetError() == 0 : "glLineWidth(F) failed!";
    }

    public final void glFrontFace(int ff) {
        LOGGER.trace(GL_MARKER, "glFrontFace({})", ff);
        GL11.glFrontFace(ff);
        assert GL11.glGetError() == 0 : "glFrontFace(I) failed!";
    }

    public final void glCullFace(int cullMode) {
        LOGGER.trace(GL_MARKER, "glCullFace({})", cullMode);
        GL11.glCullFace(cullMode);
        assert GL11.glGetError() == 0 : "glCullFace(I) failed!";
    }

    public final void glPolygonMode(int face, int mode) {
        LOGGER.trace(GL_MARKER, "glPolygonMode({}, {})", face, mode);
        GL11.glPolygonMode(face, mode);
        assert GL11.glGetError() == 0 : "glPolygonMode(II) failed!";
    }

    public final void glPolygonOffset(float factor, float units) {
        LOGGER.trace(GL_MARKER, "glPolygonOffset({}, {})", factor, units);
        GL11.glPolygonOffset(factor, units);
        assert GL11.glGetError() == 0 : "glPolygonOffset(FF) failed!";
    }

    public final void glDeleteProgram(int pId) {
        LOGGER.trace(GL_MARKER, "glDeleteProgram({})", pId);
        GL20.glDeleteProgram(pId);
        assert GL11.glGetError() == 0 : "glDeleteProgram(I) failed!";
    }

    public final int glCreateProgram() {
        LOGGER.trace(GL_MARKER, "glCreateProgram()");
        final int pId = GL20.glCreateProgram();
        assert GL11.glGetError() == 0 : "glCreateProgram() failed!";
        return pId;
    }

    public final void glShaderStorageBlockBinding(int pId, int sbId, int sbb) {
        final ContextCapabilities cap = GL.getCapabilities();

        if (cap.OpenGL43) {
            LOGGER.trace(GL_MARKER, "glShaderStorageBlockBinding({}, {}, {})", pId, sbId, sbb);
            GL43.glShaderStorageBlockBinding(pId, sbId, sbb);
            assert GL11.glGetError() == 0 : "glShaderStorageBlockBinding(III) failed!";
        } else if (cap.GL_ARB_shader_storage_buffer_object) {
            LOGGER.trace(GL_MARKER, "glShaderStorageBlockBinding({}, {}, {}) (ARB)", pId, sbId, sbb);
            ARBShaderStorageBufferObject.glShaderStorageBlockBinding(pId, sbId, sbId);
            assert GL11.glGetError() == 0 : "glShaderStorageBlockBinding(III) (ARB) failed!";
        } else {
            throw new UnsupportedOperationException("glShaderStorageBlockBinding is not supported!");
        }
    }

    public final int glGetUniformBlockIndex(int pId, CharSequence name) {
        final ContextCapabilities cap = GL.getCapabilities();

        if (cap.OpenGL31) {
            LOGGER.trace(GL_MARKER, "glGetUniformBlockIndex({}, {})", pId, name);
            final int loc = GL31.glGetUniformBlockIndex(pId, name);
            assert GL11.glGetError() == 0 : "glGetUniformBlockIndex(I*) failed!";
            return loc;
        } else {
            throw new UnsupportedOperationException("glGetUniformBlockIndex is not supported!");
        }
    }

    public final void glUniformBlockBinding(int pId, int ubId, int ubb) {
        final ContextCapabilities cap = GL.getCapabilities();

        if (cap.OpenGL31) {
            LOGGER.trace(GL_MARKER, "glUniformBlockBinding({}, {}, {})", pId, ubId, ubb);
            GL31.glUniformBlockBinding(pId, ubId, ubb);
            assert GL11.glGetError() == 0 : "glUniformBlockBinding(III) failed!";
        } else {
            throw new UnsupportedOperationException("glUniformBlockBinding is not supported!");
        }
    }

    public final int glGetProgramResourceLocation(int pId, int progInf, CharSequence name) {
        final ContextCapabilities cap = GL.getCapabilities();

        if (cap.OpenGL43) {
            LOGGER.trace(GL_MARKER, "glGetProgramResourceLocation({}, {}, {})", pId, progInf, name);
            final int loc = GL43.glGetProgramResourceLocation(pId, progInf, name);
            assert GL11.glGetError() == 0 : "glGetProgramResourceLocation(II*) failed!";
            return loc;
        } else if (cap.GL_ARB_program_interface_query) {
            LOGGER.trace(GL_MARKER, "glGetProgramResourceLocation({}, {}, {}) (ARB)", pId, progInf, name);
            final int loc = ARBProgramInterfaceQuery.glGetProgramResourceLocation(pId, progInf, name);
            assert GL11.glGetError() == 0 : "glGetProgramResourceLocation(II*) (ARB) failed!";
            return loc;
        } else {
            throw new UnsupportedOperationException("glGetProgramResourceLocation(II*) is not supported!");
        }
    }

    public final void glBindBufferBase(int target, int index, int bufferId) {
        LOGGER.trace(GL_MARKER, "glBindBufferBase({}, {}, {})", target, index, bufferId);
        GL30.glBindBufferBase(target, index, bufferId);
        assert GL11.glGetError() == 0 : "glBindBufferBase(III) failed!";
    }

    public final void glDispatchCompute(int x, int y, int z) {
        final ContextCapabilities cap = GL.getCapabilities();

        if (cap.OpenGL43) {
            LOGGER.trace(GL_MARKER, "glDispatchCompute({}, {}, {})", x, y, z);
            GL43.glDispatchCompute(x, y, z);
            assert GL11.glGetError() == 0 : "glDispatchCompute(III) failed!";
        } else if (cap.GL_ARB_compute_shader) {
            LOGGER.trace(GL_MARKER, "glDispatchCompute({}, {}, {}) (ARB)", x, y, z);
            ARBComputeShader.glDispatchCompute(x, y, z);
            assert GL11.glGetError() == 0 : "glDispatchCompute(III) (ARB) failed!";
        } else {
            throw new UnsupportedOperationException("glDispatchCompute is not supported!");
        }
    }

    public final void glDetachShader(int pId, int shId) {
        LOGGER.trace(GL_MARKER, "glDetachShader({}, {})", pId, shId);
        GL20.glDetachShader(pId, shId);
        assert GL11.glGetError() == 0 : "glDetachShader(II) failed!";
    }

    public final void glAttachShader(int pId, int shId) {
        LOGGER.trace(GL_MARKER, "glAttachShader({}, {})", pId, shId);
        GL20.glAttachShader(pId, shId);
        assert GL11.glGetError() == 0 : "glAttachShader(II) failed!";
    }

    public final void glLinkProgram(int pId) {
        LOGGER.trace(GL_MARKER, "glLinkProgram({})", pId);
        GL20.glLinkProgram(pId);
        assert GL11.glGetError() == 0 : "glLinkProgram(I) failed!";
    }

    public final int glGetProgrami(int pId, int param) {
        LOGGER.trace(GL_MARKER, "glGetProgrami({}, {})", pId, param);
        final int val = GL20.glGetProgrami(pId, param);
        assert GL11.glGetError() == 0 : "glGetProgrami(II) failed!";
        return val;
    }

    public final String glGetProgramInfoLog(int pId, int length) {
        LOGGER.trace(GL_MARKER, "glGetProgramInfoLog({}, {})", pId, length);
        final String res = GL20.glGetProgramInfoLog(pId, length);
        assert GL11.glGetError() == 0 : "glGetProgramInfoLog(II) failed!";
        return res;
    }

    public final void glTransformFeedbackVaryings(int pId, CharSequence[] varyings, int type) {
        LOGGER.trace(GL_MARKER, "glTransformFeedbackVaryings({}, {}, {})", pId, varyings, type);
        GL30.glTransformFeedbackVaryings(pId, varyings, type);
        assert GL11.glGetError() == 0 : "glTransformFeedbackVaryings(I*I) failed!";
    }

    public final void glBindAttribLocation(int pId, int index, CharSequence name) {
        LOGGER.trace(GL_MARKER, "glBindAttribLocation({}, {}, {})", pId, index, name);
        GL20.glBindAttribLocation(pId, index, name);
        assert GL11.glGetError() == 0 : "glBindAttribLocation(II*) failed!";
    }

    public final int glGetUniformLocation(int pId, CharSequence uName) {
        LOGGER.trace(GL_MARKER, "glGetUniformLocation({}, {})", pId, uName);
        final int loc = GL20.glGetUniformLocation(pId, uName);
        assert GL11.glGetError() == 0 : "glGetUniformLocation(I*) failed!";
        return loc;
    }

    public final void glUseProgram(int pId) {
        LOGGER.trace(GL_MARKER, "glUseProgram({})", pId);
        GL20.glUseProgram(pId);
        assert GL11.glGetError() == 0 : "glUseProgram(I) failed!";
    }

    public final void glSamplerParameterf(int target, int sId, float value) {
        final ContextCapabilities cap = GL.getCapabilities();

        if (cap.OpenGL33) {
            LOGGER.trace(GL_MARKER, "glSamplerParameterf({}, {}, {})", target, sId, value);
            GL33.glSamplerParameterf(target, sId, value);
            assert GL11.glGetError() == 0 : "glSamplerParameterf(IIF) failed!";
        } else if (cap.GL_ARB_sampler_objects) {
            LOGGER.trace(GL_MARKER, "glSamplerParameterf({}, {}, {}) (ARB)", target, sId, value);
            ARBSamplerObjects.glSamplerParameterf(target, sId, value);
            assert GL11.glGetError() == 0 : "glSamplerParameterf(IIF) (ARB) failed!";
        } else {
            throw new UnsupportedOperationException("glSamplerParameterf is not supported!");
        }
    }

    public final void glBindSampler(int unit, int sId) {
        final ContextCapabilities cap = GL.getCapabilities();

        if (cap.OpenGL33) {
            LOGGER.trace(GL_MARKER, "glBindSampler({}, {})", unit, sId);
            GL33.glBindSampler(unit, sId);
            assert GL11.glGetError() == 0 : "glBindSampler(II) failed!";
        } else if (cap.GL_ARB_sampler_objects) {
            LOGGER.trace(GL_MARKER, "glBindSampler({}, {}) (ARB)", unit, sId);
            ARBSamplerObjects.glBindSampler(unit, sId);
            assert GL11.glGetError() == 0 : "glBindSampler(II) failed!";
        } else {
            throw new UnsupportedOperationException("glBindSampler(II) is not supported!");
        }
    }

    public final int glGenSamplers() {
        final ContextCapabilities cap = GL.getCapabilities();

        if (cap.OpenGL33) {
            LOGGER.trace(GL_MARKER, "glGenSamplers()");
            final int sId = GL33.glGenSamplers();
            assert GL11.glGetError() == 0 : "glGenSamplers() failed!";
            return sId;
        } else if (cap.GL_ARB_sampler_objects) {
            LOGGER.trace(GL_MARKER, "glGenSamplers() (ARB)");
            final int sId = ARBSamplerObjects.glGenSamplers();
            assert GL11.glGetError() == 0 : "glGenSamplers() (ARB) failed!";
            return sId;
        } else {
            throw new UnsupportedOperationException("glGenSamplers() is not supported!");
        }
    }

    public final void glDeleteSamplers(int sId) {
        final ContextCapabilities cap = GL.getCapabilities();

        if (cap.OpenGL33) {
            LOGGER.trace(GL_MARKER, "glDeleteSamplers({})", sId);
            GL33.glDeleteSamplers(sId);
            assert GL11.glGetError() == 0 : "glDeleteSamplers(I) failed!";
        } else if (cap.GL_ARB_sampler_objects) {
            LOGGER.trace(GL_MARKER, "glDeleteSamplers({}) (ARB)", sId);
            ARBSamplerObjects.glDeleteSamplers(sId);
            assert GL11.glGetError() == 0 : "glDeleteSamplers(I) (ARB) failed!";
        } else {
            throw new UnsupportedOperationException("glDeleteSamplers(I) is not supported!");
        }
    }

    public final void glSamplerParameteri(int pId, int sId, int value) {
        final ContextCapabilities cap = GL.getCapabilities();

        if (cap.OpenGL33) {
            LOGGER.trace(GL_MARKER, "glSamplerParameteri({}, {}, {})", pId, sId, value);
            GL33.glSamplerParameteri(pId, sId, value);
            assert GL11.glGetError() == 0 : "glSamplerParameteri(III) failed!";
        } else if (cap.GL_ARB_sampler_objects) {
            LOGGER.trace(GL_MARKER, "glSamplerParameteri({}, {}, {}) (ARB)", pId, sId, value);
            ARBSamplerObjects.glSamplerParameteri(pId, sId, value);
            assert GL11.glGetError() == 0 : "glSamplerParameteri(III) (ARB) failed!";
        } else {
            throw new UnsupportedOperationException("glSamplerParameteri(III) is not supported!");
        }
    }

    public final void glScissor(int left, int bottom, int width, int height) {
        LOGGER.trace(GL_MARKER, "glScissor({}, {}, {}, {})", left, bottom, width, height);
        GL11.glScissor(left, bottom, width, height);
        assert GL11.glGetError() == 0 : "glScissor(IIII) failed!";
    }

    public final String glGetShaderInfoLog(int shId, int length) {
        LOGGER.trace(GL_MARKER, "glGetShaderInfoLog({}, {})", shId, length);
        final String res = GL20.glGetShaderInfoLog(shId, length);
        assert GL11.glGetError() == 0 : "glGetShaderInfoLog(II) failed!";
        return res;
    }

    public final void glDeleteShader(int shId) {
        LOGGER.trace(GL_MARKER, "glDeleteShader({})", shId);
        GL20.glDeleteShader(shId);
        assert GL11.glGetError() == 0 : "glDeleteShader(I) failed!";
    }

    public final int glCreateShader(int type) {
        LOGGER.trace(GL_MARKER, "glCreateShader({})", type);
        final int shId = GL20.glCreateShader(type);
        assert GL11.glGetError() == 0 : "glCreateShader(I) failed!";
        return shId;
    }

    public final void glShaderSource(int shId, CharSequence src) {
        LOGGER.trace(GL_MARKER, "glShaderSource({}, {})", shId, src);
        GL20.glShaderSource(shId, src);
        assert GL11.glGetError() == 0 : "glShaderSource(I*) failed!";
    }

    public final void glCompileShader(int shId) {
        LOGGER.trace(GL_MARKER, "glCompileShader({})", shId);
        GL20.glCompileShader(shId);
        assert GL11.glGetError() == 0 : "glCompileShader(I) failed!";
    }

    public final int glGetShaderi(int shId, int pId) {
        LOGGER.trace(GL_MARKER, "glGetShaderi({}, {})", shId, pId);
        final int res = GL20.glGetShaderi(shId, pId);
        assert GL11.glGetError() == 0 : "glGetShaderi(II) failed!";
        return res;
    }

    public final int glGetTexLevelParameteri(int target, int i, int id) {
        LOGGER.trace(GL_MARKER, "glGetTexLevelParameteri({}, {}, {})", target, i, id);
        final int res = GL11.glGetTexLevelParameteri(target, i, id);
        assert GL11.glGetError() == 0 : "glGetTexLevelParameteri(III) failed!";
        return res;
    }

    public final void glTexBuffer(int target, int internalFormat, int bufferId) {
        if (GL.getCapabilities().OpenGL30) {
            LOGGER.trace(GL_MARKER, "glTexBuffer({}, {}, {})", target, internalFormat, bufferId);
            // this is listed as an OpenGL3.0 call?
            GL31.glTexBuffer(target, internalFormat, bufferId);
            assert GL11.glGetError() == 0 : "glTexBuffer(III) failed!";
        } else {
            throw new UnsupportedOperationException("glTexBuffer(III) is not supported!");
        }
    }

    public final void glBindTexture(int target, int texId) {
        LOGGER.trace(GL_MARKER, "glBindTexture({}, {})", target, texId);
        GL11.glBindTexture(target, texId);
        assert GL11.glGetError() == 0 : "glBindTexture(II) failed!";
    }

    public final void glTexSubImage2D(int target, int level, int xOffset, int yOffset, int width, int height, int format, int type, long ptr) {
        LOGGER.trace(GL_MARKER, "glTexSubImage2D({}, {}, {}, {}, {}, {}, {}, {}, {})", target, level, xOffset, yOffset, width, height, format, type, ptr);
        GL11.glTexSubImage2D(target, level, xOffset, yOffset, width, height, format, type, ptr);
        assert GL11.glGetError() == 0 : "glTexSubImage2D(IIIIIIII*) failed!";
    }

    public final void glDeleteTextures(int texId) {
        LOGGER.trace(GL_MARKER, "glDeleteTextures({})", texId);
        GL11.glDeleteTextures(texId);
        assert GL11.glGetError() == 0 : "glDeleteTextures(I) failed!";
    }

    public final float glGetFloat(int floatId) {
        LOGGER.trace(GL_MARKER, "glGetFloat({})", floatId);
        final float value = GL11.glGetFloat(floatId);
        assert GL11.glGetError() == 0 : "glGetFloat(I) failed!";
        return value;
    }

    public final int glGetInteger(int intId) {
        LOGGER.trace(GL_MARKER, "glGetInteger({})", intId);
        final int value = GL11.glGetInteger(intId);
        assert GL11.glGetError() == 0 : "glGetInteger(I) failed!";
        return value;
    }

    public final void glVertexAttribDivisor(int index, int divisor) {
        final ContextCapabilities cap = GL.getCapabilities();

        if (cap.OpenGL33) {
            LOGGER.trace(GL_MARKER, "glVertexAttribDivisor({}, {})", index, divisor);
            GL33.glVertexAttribDivisor(index, divisor);
            assert GL11.glGetError() == 0 : "glVertexAttribDivisor(II) failed!";
        } else if (cap.GL_ARB_instanced_arrays) {
            LOGGER.trace(GL_MARKER, "glVertexAttribDivisor({}, {}) (ARB)", index, divisor);
            ARBInstancedArrays.glVertexAttribDivisorARB(index, divisor);
            assert GL11.glGetError() == 0 : "glVertexAttribDivisor(II) failed!";
        } else {
            throw new UnsupportedOperationException("glVertexAttribDivisor(II) is not supported!");
        }
    }

    public final void glEnableVertexAttribArray(int index) {
        LOGGER.trace(GL_MARKER, "glEnableVertexAttribArray({})", index);
        GL20.glEnableVertexAttribArray(index);
        assert GL11.glGetError() == 0 : "glEnableVertexAttribArray(I) failed!";
    }

    public final void glVertexAttribLPointer(int index, int size, int type, int stride, long offset) {
        LOGGER.trace(GL_MARKER, "glVertexAttribLPointer({}, {}, {}, {}, {})", index, size, type, stride, offset);
        GL41.glVertexAttribLPointer(index, size, type, stride, offset);
        assert GL11.glGetError() == 0 : "glVertexAttribLPointer(IIIIL) failed!";
    }

    public final void glVertexAttribPointer(int index, int size, int type, boolean normalized, int stride, long offset) {
        LOGGER.trace(GL_MARKER, "glVertexAttribPointer({}, {}, {}, {}, {}, {})", index, size, type, normalized, stride, offset);
        GL20.glVertexAttribPointer(index, size, type, normalized, stride, offset);
        assert GL11.glGetError() == 0 : "glVertexAttribPointer(IIIBIL) failed!";
    }

    public final void glBeginTransformFeedback(int mode) {
        LOGGER.trace(GL_MARKER, "glBeginTransformFeedback({})", mode);
        GL30.glBeginTransformFeedback(mode);
        assert GL11.glGetError() == 0 : "glBeginTransformFeedback(I) failed!";
    }

    public final void glEndTransformFeedback() {
        LOGGER.trace(GL_MARKER, "glEndTransformFeedback()");
        GL30.glEndTransformFeedback();
        assert GL11.glGetError() == 0 : "glEndTransformFeedback() failed!";
    }

    public final void glBindBuffer(int target, int buffId) {
        LOGGER.trace(GL_MARKER, "glBindBuffer({}, {})", target, buffId);
        GL15.glBindBuffer(target, buffId);
        assert GL11.glGetError() == 0 : "glBindBuffer(II) failed!";
    }

    public final void glBindVertexArray(int vaoId) {
        LOGGER.trace(GL_MARKER, "glBindVertexArray({})", vaoId);
        GL30.glBindVertexArray(vaoId);
        assert GL11.glGetError() == 0 : "glBindVertexArray(I) failed!";
    }

    public final int glGenVertexArrays() {
        final ContextCapabilities cap = GL.getCapabilities();

        if (cap.OpenGL30) {
            LOGGER.trace(GL_MARKER, "glGenVertexArrays()");
            final int vao = GL30.glGenVertexArrays();
            assert GL11.glGetError() == 0 : "glGenVertexArrays() failed!";
            return vao;
        } else if (cap.GL_ARB_vertex_array_object) {
            LOGGER.trace(GL_MARKER, "glGenVertexArrays() (ARB)");
            final int vao = ARBVertexArrayObject.glGenVertexArrays();
            assert GL11.glGetError() == 0 : "glGenVertexArrays() (ARB) failed!";
            return vao;
        } else {
            throw new UnsupportedOperationException("glGenVertexArrays() is not supported!");
        }

    }

    public final void glDeleteVertexArrays(int vao) {
        final ContextCapabilities cap = GL.getCapabilities();
        
        if(cap.OpenGL30) {
            LOGGER.trace(GL_MARKER, "glDeleteVertexArrays({})", vao);
            GL30.glDeleteVertexArrays(vao);
            assert GL11.glGetError() == 0 : "glDeleteVertexArrays(I) failed!";
        } else if(cap.GL_ARB_vertex_array_object) {
            LOGGER.trace(GL_MARKER, "glDeleteVertexArrays({}) (ARB)", vao);
            ARBVertexArrayObject.glDeleteVertexArrays(vao);
            assert GL11.glGetError() == 0 : "glDeleteVertexArrays(I) (ARB) failed!";
        } else {
            throw new UnsupportedOperationException("glDeleteVertexArrays(I) is not supported!");
        }
    }

    public final int glGetError() {        
        return GL11.glGetError();
    }

    public final void glViewport(int x, int y, int width, int height) {
        LOGGER.trace(GL_MARKER, "glViewport({}, {}, {}, {})", x, y, width, height);
        GL11.glViewport(x, y, width, height);
        assert GL11.glGetError() == 0 : "glViewport(IIII) failed!";
    }   

    public final String glGetString(int strId) {
        LOGGER.trace(GL_MARKER, "glGetString({})", strId);
        final String result = GL11.glGetString(strId);
        assert GL11.glGetError() == 0 : "glGetString(I) failed!";
        return result;
    }

    public final int getOpenGLVersion() {
        final ContextCapabilities cap = GL.getCapabilities();

        if (cap.OpenGL40) {
            if (cap.OpenGL45) {
                return 45;
            } else if (cap.OpenGL44) {
                return 44;
            } else if (cap.OpenGL43) {
                return 43;
            } else if (cap.OpenGL42) {
                return 42;
            } else if (cap.OpenGL41) {
                return 41;
            } else {
                return 40;
            }
        } else if (cap.OpenGL30) {
            if (cap.OpenGL33) {
                return 33;
            } else if (cap.OpenGL32) {
                return 32;
            } else if (cap.OpenGL31) {
                return 31;
            } else {
                return 30;
            }
        } else if (cap.OpenGL20) {
            if (cap.OpenGL21) {
                return 21;
            } else {
                return 20;
            }
        } else if (cap.OpenGL15) {
            return 15;
        } else if (cap.OpenGL14) {
            return 14;
        } else if (cap.OpenGL13) {
            return 13;
        } else if (cap.OpenGL12) {
            return 12;
        } else {
            return 11;
        }
    }

    public final void glMultiDrawArrays(final int mode, final IntBuffer first, final IntBuffer count) {
        LOGGER.trace(GL_MARKER, "glMultiDrawArrays({}, {}, {})", mode, first, count);
        GL14.glMultiDrawArrays(mode, first, count);
        assert GL11.glGetError() == 0 : "glMultiDrawArrays(I**) failed!";
    }    
    
    public final void glDrawArraysInstanced(int drawMode, int first, int count, int instanceCount) {
        final ContextCapabilities cap = GL.getCapabilities();

        if (cap.OpenGL31) {
            LOGGER.trace(GL_MARKER, "glDrawArraysInstanced({}, {}, {}, {})", drawMode, first, count, instanceCount);
            GL31.glDrawArraysInstanced(drawMode, first, count, instanceCount);
            assert GL11.glGetError() == 0 : "glDrawArraysInstanced(IIII) failed!";
        } else if (cap.GL_ARB_draw_instanced) {
            LOGGER.trace(GL_MARKER, "glDrawArraysInstancedARB({}, {}, {}, {})", drawMode, first, count, instanceCount);
            ARBDrawInstanced.glDrawArraysInstancedARB(drawMode, first, count, instanceCount);
            assert GL11.glGetError() == 0 : "glDrawArraysInstancedARB(IIII) failed!";
        } else {
            final int currentProgram = glGetInteger(GL20.GL_CURRENT_PROGRAM);
            final int uLoc = glGetUniformLocation(currentProgram, "gl_InstanceID");

            for (int i = 0; i < instanceCount; i++) {
                LOGGER.trace(GL_MARKER, "glUniform1i({}, {})", uLoc, i);
                GL20.glUniform1i(uLoc, i);
                assert GL11.glGetError() == 0 : "glUniform1i(II) failed!";
                
                glDrawArrays(drawMode, first, count);
            }
        }
    }

    public final void glDrawElementsInstanced(int drawMode, int count, int type, long offset, int instanceCount) {
        final ContextCapabilities cap = GL.getCapabilities();

        if (cap.OpenGL31) {
            LOGGER.trace(GL_MARKER, "glDrawElementsInstanced({}, {}, {}, {}, {})", drawMode, count, type, offset, instanceCount);
            GL31.glDrawElementsInstanced(drawMode, count, type, offset, instanceCount);
            assert GL11.glGetError() == 0 : "glDrawElementsInstanced(IIILI) failed!";
        } else if (cap.GL_ARB_draw_instanced) {
            LOGGER.trace(GL_MARKER, "glDrawElementsInstancedARB({}, {}, {}, {}, {})", drawMode, count, type, offset, instanceCount);
            ARBDrawInstanced.glDrawElementsInstancedARB(drawMode, count, type, offset, instanceCount);
            assert GL11.glGetError() == 0 : "glDrawElementsInstancedARB(IIILI) failed!";
        } else {
            final int currentProgram = glGetInteger(GL20.GL_CURRENT_PROGRAM);
            final int uLoc = glGetUniformLocation(currentProgram, "gl_InstanceID");

            for (int i = 0; i < instanceCount; i++) {
                LOGGER.trace(GL_MARKER, "glUniform1i({}, {})", uLoc, i);
                GL20.glUniform1i(uLoc, i);
                assert GL11.glGetError() == 0 : "glUniform1i(II) failed!";
                
                glDrawElements(drawMode, count, type, offset);
            }
        }
    }        
    
    public final void glDrawElements(int mode, int count, int type, long offset) {
        LOGGER.trace(GL_MARKER, "glDrawElements({}, {}, {}, {})", mode, count, type, offset);
        GL11.glDrawElements(mode, count, type, offset);
        assert GL11.glGetError() == 0 : "glDrawElements(IIIL) failed!";
    }
    
    public final void glDrawArrays(int mode, int first, int count) {
        LOGGER.trace(GL_MARKER, "glDrawArrays({}, {}, {})", mode, first, count);
        GL11.glDrawArrays(mode, first, count);
        assert GL11.glGetError() == 0 : "glDrawArrays(III) failed!";
    }

    public final void glDrawArraysIndirect(int mode, long offset) {
        final ContextCapabilities cap = GL.getCapabilities();

        if (cap.OpenGL40) {
            LOGGER.trace(GL_MARKER, "glDrawArraysIndirect({}, {})", mode, offset);
            GL40.glDrawArraysIndirect(mode, offset);
            assert GL11.glGetError() == 0 : "glDrawArraysIndirect(II) failed!";
        } else if (cap.GL_ARB_draw_indirect) {
            LOGGER.trace(GL_MARKER, "glDrawArraysIndirect({}, {})", mode, offset);
            ARBDrawIndirect.glDrawArraysIndirect(mode, offset);
            assert GL11.glGetError() == 0 : "glDrawArraysIndirect(II) (ARB) failed!";
        } else {
            throw new UnsupportedOperationException("glDrawArraysIndirect(II) is unsupported!");
        }
    }

    public final void glDrawElementsIndirect(int mode, int type, long offset) {
        final ContextCapabilities cap = GL.getCapabilities();

        if (cap.OpenGL40) {
            LOGGER.trace(GL_MARKER, "glDrawElementsIndirect({}, {}, {})", mode, type, offset);
            GL40.glDrawElementsIndirect(mode, type, offset);
            assert GL11.glGetError() == 0 : "glDrawElementsIndirect(III) failed!";
        } else if (cap.GL_ARB_draw_indirect) {
            LOGGER.trace(GL_MARKER, "glDrawElementsIndirect({}, {}, {}) (ARB)", mode, type, offset);
            ARBDrawIndirect.glDrawElementsIndirect(mode, type, offset);
            assert GL11.glGetError() == 0 : "glDrawElementsIndirect(III) failed!";
        } else {
            throw new UnsupportedOperationException("glDrawElementsIndirect(III) is unsupported!");
        }
    }

    public final void glBeginConditionalRender(int query, int mode) {
        LOGGER.trace(GL_MARKER, "glBeginConditionalRender({}, {})", query, mode);
        GL30.glBeginConditionalRender(query, mode);
        assert GL11.glGetError() == 0 : "glBeginConditionalRender(II) failed!";
    }

    public final void glEndConditionalRender() {
        LOGGER.trace(GL_MARKER, "glEndConditionalRender()");
        GL30.glEndConditionalRender();
        assert GL11.glGetError() == 0 : "glEndConditionalRender() failed!";
    }

    public final int glGenQueries() {
        LOGGER.trace(GL_MARKER, "glGenQueries()");
        final int query = GL15.glGenQueries();
        assert GL11.glGetError() == 0 : "glGenQueries() failed!";
        return query;
    }

    public final void glDeleteQueries(int query) {
        LOGGER.trace(GL_MARKER, "glDeleteQueries({})", query);
        GL15.glDeleteQueries(query);
        assert GL11.glGetError() == 0 : "glDeleteQueries(I) failed!";
    }

    public final void glBeginQuery(int condition, int query) {
        LOGGER.trace(GL_MARKER, "glBeginQuery({}, {})", condition, query);
        GL15.glBeginQuery(condition, query);
        assert GL11.glGetError() == 0 : "glBeginQuery(II) failed!";
    }

    public final void glEndQuery(int condition) {
        LOGGER.trace(GL_MARKER, "glEndQuery({})", condition);
        GL15.glEndQuery(condition);
        assert GL11.glGetError() == 0 : "glEndQuery(I) failed!";
    }

    public final void glDepthFunc(int depthFunc) {
        LOGGER.trace(GL_MARKER, "glDepthFunc({})", depthFunc);
        GL11.glDepthFunc(depthFunc);
        assert GL11.glGetError() == 0 : "glDepthFunc(I) failed!";
    }

    public final void glClearColor(float red, float green, float blue, float alpha) {
        LOGGER.trace(GL_MARKER, "glClearColor({}, {}, {}, {})", red, green, blue, alpha);
        GL11.glClearColor(red, green, blue, alpha);
        assert GL11.glGetError() == 0 : "glClearColor(FFFF) failed!";
    }

    public final void glClearDepth(double depth) {
        LOGGER.trace(GL_MARKER, "glClearDepth({})", depth);
        GL11.glClearDepth(depth);
        assert GL11.glGetError() == 0 : "glClearDepth(D) failed!";
    }

    public final void glClear(int bitfield) {
        LOGGER.trace(GL_MARKER, "glClear({})", bitfield);
        GL11.glClear(bitfield);
        assert GL11.glGetError() == 0 : "glClear(I) failed!";
    }

    public final void glDeleteBuffers(int bufferId) {
        LOGGER.trace(GL_MARKER, "glDeleteBuffers({})", bufferId);
        GL15.glDeleteBuffers(bufferId);
        assert GL11.glGetError() == 0 : "glDeleteBuffers(I) failed!";
    }

    public final void glEnable(int capability) {
        LOGGER.trace(GL_MARKER, "glEnable({})", capability);
        GL11.glEnable(capability);
        assert GL11.glGetError() == 0 : "glEnable(I) failed!";
    }

    public final void glDisable(int capability) {
        LOGGER.trace(GL_MARKER, "glDisable({})", capability);
        GL11.glDisable(capability);
        assert GL11.glGetError() == 0 : "glDisable(I) failed!";
    }

    public final void glBlendEquationSeparate(int rgb, int alpha) {
        LOGGER.trace(GL_MARKER, "glBlendEquationSeparate({}, {})", rgb, alpha);
        GL20.glBlendEquationSeparate(rgb, alpha);
        assert GL11.glGetError() == 0 : "glBlendEquationSeparate(II) failed!";
    }

    public final void glBlendFuncSeparate(int rgbSrc, int rgbDst, int aSrc, int aDst) {
        LOGGER.trace(GL_MARKER, "glBlendFuncSeparate({}, {}, {}, {})", rgbSrc, rgbDst, aSrc, aDst);
        GL14.glBlendFuncSeparate(rgbSrc, rgbDst, aSrc, aDst);
        assert GL11.glGetError() == 0 : "glBlendFuncSeparate(IIII) failed!";
    }
}
