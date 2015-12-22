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

import static com.longlinkislong.gloop.GLAsserts.glErrorMsg;
import com.longlinkislong.gloop.GLBlendEquation;
import com.longlinkislong.gloop.GLBlendFunc;
import com.longlinkislong.gloop.GLDepthFunc;
import com.longlinkislong.gloop.GLDrawMode;
import com.longlinkislong.gloop.GLDrawQueryMode;
import com.longlinkislong.gloop.GLTextureFormat;
import com.longlinkislong.gloop.GLType;
import java.nio.ByteBuffer;
import java.nio.FloatBuffer;
import java.nio.IntBuffer;
import org.lwjgl.opengl.ARBComputeShader;
import org.lwjgl.opengl.ARBDrawBuffers;
import org.lwjgl.opengl.ARBDrawIndirect;
import org.lwjgl.opengl.ARBDrawInstanced;
import org.lwjgl.opengl.ARBFramebufferObject;
import org.lwjgl.opengl.ARBInstancedArrays;
import org.lwjgl.opengl.ARBProgramInterfaceQuery;
import org.lwjgl.opengl.ARBSamplerObjects;
import org.lwjgl.opengl.ARBShaderObjects;
import org.lwjgl.opengl.ARBShaderStorageBufferObject;
import org.lwjgl.opengl.ARBTextureBufferObject;
import org.lwjgl.opengl.ARBUniformBufferObject;
import org.lwjgl.opengl.ARBVertexArrayObject;
import org.lwjgl.opengl.ARBVertexAttrib64Bit;
import org.lwjgl.opengl.ARBVertexShader;
import org.lwjgl.opengl.ContextCapabilities;
import org.lwjgl.opengl.EXTBlendEquationSeparate;
import org.lwjgl.opengl.EXTFramebufferObject;
import org.lwjgl.opengl.EXTTransformFeedback;
import org.lwjgl.opengl.EXTVertexAttrib64bit;
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
import static org.lwjgl.system.MemoryUtil.memAddress;

/**
 *
 * @author zmichaels
 */
public abstract class Common {

    public static final boolean CAN_CAST_DOUBLE_TO_FLOAT = true;
    public static final boolean IGNORE_FRAMEBUFFER_SUPPORT;
    public static final boolean IGNORE_BUFFER_STORAGE_SUPPORT;
    
    private static final Marker GL_MARKER = MarkerFactory.getMarker("OPENGL");
    private static final Logger LOGGER = LoggerFactory.getLogger("OPENGL");

    static {                
        if (IGNORE_FRAMEBUFFER_SUPPORT = Boolean.getBoolean("com.longlinkislong.gloop.dsa.ignore_framebuffer_support")) {
            LOGGER.debug(GL_MARKER, "Removed framebuffer support from requirements!");
        }

        if (Boolean.getBoolean("com.longlinkislong.gloop.dsa.fakedsa.require_buffer_storage_support")) {
            LOGGER.debug(GL_MARKER, "Buffer storage support added to requirements!");
            IGNORE_BUFFER_STORAGE_SUPPORT = false;
        } else {
            IGNORE_BUFFER_STORAGE_SUPPORT = true;
        }
    }

    public final void glReadPixels(int x, int y, int w, int h, int format, int type, long ptr) {
        LOGGER.trace(GL_MARKER, "glReadPixels({}, {}, {}, {}, {}, {}, {})", x, y, w, h, format, type, ptr);
        GL11.glReadPixels(x, y, w, h, format, type, ptr);
        assert GL11.glGetError() == 0 : glErrorMsg("glReadPixels(IIIIIII)", x, y, w, h, GLTextureFormat.of(format).get(), GLType.of(type).get(), ptr);
    }

    public final void glReadPixels(int x, int y, int w, int h, int format, int type, ByteBuffer buffer) {
        LOGGER.trace(GL_MARKER, "glReadPixels({}, {}, {}, {}, {}, {}, {})", x, y, w, h, format, type, buffer);
        GL11.glReadPixels(x, y, w, h, format, type, buffer);
        assert GL11.glGetError() == 0 : glErrorMsg("glReadPixels(IIIIIII)", x, y, w, h, GLTextureFormat.of(format).get(), GLType.of(type).get(), memAddress(buffer));
    }

    public final void glDrawBuffers(IntBuffer attachments) {
        final ContextCapabilities cap = GL.getCapabilities();

        if (cap.OpenGL20) {
            LOGGER.trace(GL_MARKER, "glDrawBuffers({})", attachments);
            GL20.glDrawBuffers(attachments);
            assert GL11.glGetError() == 0 : glErrorMsg("glDrawBuffers(*)", memAddress(attachments));
        } else if (cap.GL_ARB_draw_buffers) {
            LOGGER.trace(GL_MARKER, "glDrawBuffersARB({})", attachments);
            ARBDrawBuffers.glDrawBuffersARB(attachments);
            assert GL11.glGetError() == 0 : glErrorMsg("glDrawBuffersARB(*)", memAddress(attachments));
        } else {
            throw new UnsupportedOperationException("glDrawBuffers[ARB] is not supported!");
        }
    }

    public final void glDeleteFramebuffers(int fbId) {
        final ContextCapabilities cap = GL.getCapabilities();

        if (cap.OpenGL30) {
            LOGGER.trace(GL_MARKER, "glDeleteFramebuffers({})", fbId);
            GL30.glDeleteFramebuffers(fbId);
            assert GL11.glGetError() == 0 : glErrorMsg("glDeleteFramebuffers(I)", fbId);
        } else if (cap.GL_ARB_framebuffer_object) {
            LOGGER.trace(GL_MARKER, "glDeleteFramebuffers({}) (ARB)", fbId);
            ARBFramebufferObject.glDeleteFramebuffers(fbId);
            assert GL11.glGetError() == 0 : glErrorMsg("glDeleteFramebuffersARB(I)", fbId);
        } else if (cap.GL_EXT_framebuffer_object) {
            LOGGER.trace(GL_MARKER, "glDeleteFramebuffersEXT({})", fbId);
            EXTFramebufferObject.glDeleteFramebuffersEXT(fbId);
            assert GL11.glGetError() == 0 : glErrorMsg("glDeleteFramebuffersEXT(I)", fbId);
        } else {
            throw new UnsupportedOperationException("glDeleteFramebuffers[ARB|EXT] is not supported!");
        }
    }

    public final int glCheckFramebufferStatus(int fbId) {
        final ContextCapabilities cap = GL.getCapabilities();

        if (cap.OpenGL30) {
            LOGGER.trace(GL_MARKER, "glCheckFramebufferStatus({})", fbId);
            final int status = GL30.glCheckFramebufferStatus(fbId);
            assert GL11.glGetError() == 0 : glErrorMsg("glCheckFramebufferStatus(I)", fbId);

            return status;
        } else if (cap.GL_ARB_framebuffer_object) {
            LOGGER.trace(GL_MARKER, "glCheckFramebufferStatus({}) (ARB)", fbId);
            final int status = ARBFramebufferObject.glCheckFramebufferStatus(fbId);
            assert GL11.glGetError() == 0 : glErrorMsg("glCheckFramebufferStatusARB(I)", fbId);

            return status;
        } else if (cap.GL_EXT_framebuffer_object) {
            LOGGER.trace(GL_MARKER, "glCheckFramebufferStatusEXT({})", fbId);
            final int status = EXTFramebufferObject.glCheckFramebufferStatusEXT(fbId);
            assert GL11.glGetError() == 0 : glErrorMsg("glCheckFramebufferStatusEXT(I)", fbId);
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
            assert GL11.glGetError() == 0 : glErrorMsg("glBindFramebuffer(II)", target, fb);
        } else if (cap.GL_ARB_framebuffer_object) {
            LOGGER.trace(GL_MARKER, "glBindFramebuffer({}, {}) (ARB)", target, fb);
            ARBFramebufferObject.glBindFramebuffer(target, fb);
            assert GL11.glGetError() == 0 : glErrorMsg("glBindFramebufferARB(II)", target, fb);
        } else if (cap.GL_EXT_framebuffer_object) {
            LOGGER.trace(GL_MARKER, "glBindFramebufferEXT({}, {})", target, fb);
            EXTFramebufferObject.glBindFramebufferEXT(target, fb);
            assert GL11.glGetError() == 0 : glErrorMsg("glBindFramebufferEXT(II)", target, fb);
        } else {
            throw new UnsupportedOperationException("glBindFramebuffer[ARB|EXT] is not supported!");
        }
    }

    public final void glColorMask(boolean red, boolean green, boolean blue, boolean alpha) {
        LOGGER.trace(GL_MARKER, "glColorMask({}, {}, {}, {})", red, green, blue, alpha);
        GL11.glColorMask(red, green, blue, alpha);
        assert GL11.glGetError() == 0 : glErrorMsg("glColorMask(BBBB)", red, green, blue, alpha);
    }

    public final void glDepthMask(boolean depth) {
        LOGGER.trace(GL_MARKER, "glDepthMask({})", depth);
        GL11.glDepthMask(depth);
        assert GL11.glGetError() == 0 : glErrorMsg("glDepthMask(B)", depth);
    }

    public final void glStencilMask(int stencilMask) {
        LOGGER.trace(GL_MARKER, "glStencilMask({})", stencilMask);
        GL11.glStencilMask(stencilMask);
        assert GL11.glGetError() == 0 : glErrorMsg("glStencilMask(I)", stencilMask);
    }

    public final void glPointSize(float ps) {
        LOGGER.trace(GL_MARKER, "glPointSize({})", ps);
        GL11.glPointSize(ps);
        assert GL11.glGetError() == 0 : glErrorMsg("glPointSize(F)", ps);
    }

    public final void glLineWidth(float lw) {
        LOGGER.trace(GL_MARKER, "glLineWidth({})", lw);
        GL11.glLineWidth(lw);
        assert GL11.glGetError() == 0 : glErrorMsg("glLineWidth(F)", lw);
    }

    public final void glFrontFace(int ff) {
        LOGGER.trace(GL_MARKER, "glFrontFace({})", ff);
        GL11.glFrontFace(ff);
        assert GL11.glGetError() == 0 : glErrorMsg("glFrontFace(I)", ff);
    }

    public final void glCullFace(int cullMode) {
        LOGGER.trace(GL_MARKER, "glCullFace({})", cullMode);
        GL11.glCullFace(cullMode);
        assert GL11.glGetError() == 0 : glErrorMsg("glCullFace(I)", cullMode);
    }

    public final void glPolygonMode(int face, int mode) {
        LOGGER.trace(GL_MARKER, "glPolygonMode({}, {})", face, mode);
        GL11.glPolygonMode(face, mode);
        assert GL11.glGetError() == 0 : glErrorMsg("glPolygonMode(II)", face, mode);
    }

    public final void glPolygonOffset(float factor, float units) {
        LOGGER.trace(GL_MARKER, "glPolygonOffset({}, {})", factor, units);
        GL11.glPolygonOffset(factor, units);
        assert GL11.glGetError() == 0 : glErrorMsg("glPolygonOffset(FF)", factor, units);
    }

    public final void glDeleteProgram(int pId) {
        final ContextCapabilities cap = GL.getCapabilities();

        if (cap.OpenGL20) {
            LOGGER.trace(GL_MARKER, "glDeleteProgram({})", pId);
            GL20.glDeleteProgram(pId);
            assert GL11.glGetError() == 0 : glErrorMsg("glDeleteProgram(I)", pId);
        } else if (cap.GL_ARB_shader_objects) {
            LOGGER.trace(GL_MARKER, "glDeleteObjectARB({})", pId);
            ARBShaderObjects.glDeleteObjectARB(pId);
            assert GL11.glGetError() == 0 : glErrorMsg("glDeleteObjectARB(I)", pId);
        } else {
            throw new UnsupportedOperationException("[glDeleteProgram | glDeleteObjectARB] is not supported!");
        }
    }

    public final int glCreateProgram() {
        final ContextCapabilities cap = GL.getCapabilities();

        if (cap.OpenGL20) {
            LOGGER.trace(GL_MARKER, "glCreateProgram()");
            final int pId = GL20.glCreateProgram();
            assert GL11.glGetError() == 0 : glErrorMsg("glCreateProgram()");
            return pId;
        } else if (cap.GL_ARB_shader_objects) {
            LOGGER.trace(GL_MARKER, "glCreateProgramObjectARB()");
            final int pId = ARBShaderObjects.glCreateProgramObjectARB();
            assert GL11.glGetError() == 0 : glErrorMsg("glCreateProgramObjectARB()");

            return pId;
        } else {
            throw new UnsupportedOperationException("[glCreateProgram | glCreateProgramObjectARB] is not supported!");
        }
    }

    public final void glShaderStorageBlockBinding(int pId, int sbId, int sbb) {
        final ContextCapabilities cap = GL.getCapabilities();

        if (cap.OpenGL43) {
            LOGGER.trace(GL_MARKER, "glShaderStorageBlockBinding({}, {}, {})", pId, sbId, sbb);
            GL43.glShaderStorageBlockBinding(pId, sbId, sbb);
            assert GL11.glGetError() == 0 : glErrorMsg("glShaderstorageBlockBinding(III)", pId, sbId, sbb);
        } else if (cap.GL_ARB_shader_storage_buffer_object) {
            LOGGER.trace(GL_MARKER, "glShaderStorageBlockBinding({}, {}, {}) (ARB)", pId, sbId, sbb);
            ARBShaderStorageBufferObject.glShaderStorageBlockBinding(pId, sbId, sbId);
            assert GL11.glGetError() == 0 : glErrorMsg("glShaderStorageBlockBindingARB(III)", pId, sbId, sbb);
        } else {
            throw new UnsupportedOperationException("glShaderStorageBlockBinding is not supported!");
        }
    }

    public final int glGetUniformBlockIndex(int pId, CharSequence name) {
        final ContextCapabilities cap = GL.getCapabilities();

        if (cap.OpenGL31) {
            LOGGER.trace(GL_MARKER, "glGetUniformBlockIndex({}, {})", pId, name);
            final int loc = GL31.glGetUniformBlockIndex(pId, name);
            assert GL11.glGetError() == 0 : glErrorMsg("glGetUniformBlockIndex(I*)", pId, name);
            return loc;
        } else if (cap.GL_ARB_uniform_buffer_object) {
            LOGGER.trace(GL_MARKER, "glGetUniformBlockIndex({}, {}) (ARB)", pId, name);
            final int loc = ARBUniformBufferObject.glGetUniformBlockIndex(pId, name);
            assert GL11.glGetError() == 0 : glErrorMsg("glGetUniformBlockIndex(II)", pId, name);
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
            assert GL11.glGetError() == 0 : glErrorMsg("glUniformBlockBinding(III)", pId, ubId, ubb);
        } else if (cap.GL_ARB_uniform_buffer_object) {
            LOGGER.trace(GL_MARKER, "glUniformBlockBinding({}, {}, {}) (ARB)", pId, ubId, ubb);
            ARBUniformBufferObject.glUniformBlockBinding(pId, ubId, ubb);
            assert GL11.glGetError() == 0 : glErrorMsg("glUniformBlockBinding(III)", pId, ubId, ubb);
        } else {
            throw new UnsupportedOperationException("glUniformBlockBinding[ARB] is not supported!");
        }
    }

    public final int glGetProgramResourceLocation(int pId, int progInf, CharSequence name) {
        final ContextCapabilities cap = GL.getCapabilities();

        if (cap.OpenGL43) {
            LOGGER.trace(GL_MARKER, "glGetProgramResourceLocation({}, {}, {})", pId, progInf, name);
            final int loc = GL43.glGetProgramResourceLocation(pId, progInf, name);
            assert GL11.glGetError() == 0 : glErrorMsg("glGetProgramResourceLocation(II*)", pId, progInf, name);
            return loc;
        } else if (cap.GL_ARB_program_interface_query) {
            LOGGER.trace(GL_MARKER, "glGetProgramResourceLocation({}, {}, {}) (ARB)", pId, progInf, name);
            final int loc = ARBProgramInterfaceQuery.glGetProgramResourceLocation(pId, progInf, name);
            assert GL11.glGetError() == 0 : glErrorMsg("glGetProgramResourceLocationARB(II*)", pId, progInf, name);
            return loc;
        } else {
            throw new UnsupportedOperationException("glGetProgramResourceLocation[ARB] is not supported!");
        }
    }

    public final void glBindBufferBase(int target, int index, int bufferId) {
        if (GL.getCapabilities().OpenGL30) {
            LOGGER.trace(GL_MARKER, "glBindBufferBase({}, {}, {})", target, index, bufferId);
            GL30.glBindBufferBase(target, index, bufferId);
            assert GL11.glGetError() == 0 : glErrorMsg("glBindBufferBase(III)", target, index, bufferId);
        } else {
            throw new UnsupportedOperationException("glBindBufferBase is not supported!");
        }
    }

    public final void glDispatchCompute(int x, int y, int z) {
        final ContextCapabilities cap = GL.getCapabilities();

        if (cap.OpenGL43) {
            LOGGER.trace(GL_MARKER, "glDispatchCompute({}, {}, {})", x, y, z);
            GL43.glDispatchCompute(x, y, z);
            assert GL11.glGetError() == 0 : glErrorMsg("glDispatchCompute(III)", x, y, z);
        } else if (cap.GL_ARB_compute_shader) {
            LOGGER.trace(GL_MARKER, "glDispatchCompute({}, {}, {}) (ARB)", x, y, z);
            ARBComputeShader.glDispatchCompute(x, y, z);
            assert GL11.glGetError() == 0 : glErrorMsg("glDispatchComputeARB(III)", x, y, z);
        } else {
            throw new UnsupportedOperationException("glDispatchCompute[ARB] is not supported!");
        }
    }

    public final void glDetachShader(int pId, int shId) {
        final ContextCapabilities cap = GL.getCapabilities();

        if (cap.OpenGL20) {
            LOGGER.trace(GL_MARKER, "glDetachShader({}, {})", pId, shId);
            GL20.glDetachShader(pId, shId);
            assert GL11.glGetError() == 0 : glErrorMsg("glDetachShader(II)", pId, shId);
        } else if (cap.GL_ARB_shader_objects) {
            LOGGER.trace(GL_MARKER, "glDetachObjectARB({}, {})", pId, shId);
            ARBShaderObjects.glDetachObjectARB(pId, shId);
            assert GL11.glGetError() == 0 : glErrorMsg("glDetachObjectARB(II)", pId, shId);
        } else {
            throw new UnsupportedOperationException("[glDetachShader | glDetachObjectARB] is not supported!");
        }
    }

    public final void glAttachShader(int pId, int shId) {
        final ContextCapabilities cap = GL.getCapabilities();

        if (cap.OpenGL20) {
            LOGGER.trace(GL_MARKER, "glAttachShader({}, {})", pId, shId);
            GL20.glAttachShader(pId, shId);
            assert GL11.glGetError() == 0 : glErrorMsg("glAttachShader(II)", pId, shId);
        } else if (cap.GL_ARB_shader_objects) {
            LOGGER.trace(GL_MARKER, "glAttachObjectARB(II)", pId, shId);
            ARBShaderObjects.glAttachObjectARB(pId, shId);
            assert GL11.glGetError() == 0 : glErrorMsg("glAttachShader(II)", pId, shId);
        } else {
            throw new UnsupportedOperationException("[glAttachShader | glAttachObjectARB] is not supported!");
        }
    }

    public final void glLinkProgram(int pId) {
        final ContextCapabilities cap = GL.getCapabilities();

        if (cap.OpenGL20) {
            LOGGER.trace(GL_MARKER, "glLinkProgram({})", pId);
            GL20.glLinkProgram(pId);
            assert GL11.glGetError() == 0 : glErrorMsg("glLinkProgram(I)", pId);
        } else if (cap.GL_ARB_shader_objects) {
            LOGGER.trace(GL_MARKER, "glLinkProgramARB({})", pId);
            ARBShaderObjects.glLinkProgramARB(pId);
            assert GL11.glGetError() == 0 : glErrorMsg("glLinkProgramARB(I)", pId);
        } else {
            throw new UnsupportedOperationException("glLinkProgram is not supported!");
        }
    }

    public final int glGetProgrami(int pId, int param) {
        final ContextCapabilities cap = GL.getCapabilities();

        if (cap.OpenGL20) {
            LOGGER.trace(GL_MARKER, "glGetProgrami({}, {})", pId, param);
            final int val = GL20.glGetProgrami(pId, param);
            assert GL11.glGetError() == 0 : glErrorMsg("glGetProgrami(II)", pId, param);
            return val;
        } else if (cap.GL_ARB_shader_objects) {
            LOGGER.trace(GL_MARKER, "glGetObjectParameteriARB({}, {})", pId, param);
            final int val = ARBShaderObjects.glGetObjectParameteriARB(pId, param);
            assert GL11.glGetError() == 0 : glErrorMsg("glGetObjectParameteriARB(II)", pId, param);
            return val;
        } else {
            throw new UnsupportedOperationException("[glGetProgrami | glGetObjectParameteriARB] is not supported!");
        }
    }

    public final String glGetProgramInfoLog(int pId, int length) {
        final ContextCapabilities cap = GL.getCapabilities();

        if (cap.OpenGL20) {
            LOGGER.trace(GL_MARKER, "glGetProgramInfoLog({}, {})", pId, length);
            final String res = GL20.glGetProgramInfoLog(pId, length);
            assert GL11.glGetError() == 0 : glErrorMsg("glGetProgramInfoLog(II)", pId, length);
            return res;
        } else if (cap.GL_ARB_shader_objects) {
            LOGGER.trace(GL_MARKER, "glGetInfoLogARB({}, {})", pId, length);
            final String res = ARBShaderObjects.glGetInfoLogARB(pId, length);
            assert GL11.glGetError() == 0 : glErrorMsg("glGetInfoLogARB(II)", pId, length);
            return res;
        } else {
            throw new UnsupportedOperationException("[glGetProgramInfoLog | glGetInfoLogARB] is not supported!");
        }
    }

    public final void glTransformFeedbackVaryings(int pId, CharSequence[] varyings, int type) {
        final ContextCapabilities cap = GL.getCapabilities();

        if (cap.OpenGL30) {
            LOGGER.trace(GL_MARKER, "glTransformFeedbackVaryings({}, {}, {})", pId, varyings, type);
            GL30.glTransformFeedbackVaryings(pId, varyings, type);
            assert GL11.glGetError() == 0 : glErrorMsg("glTransformFeedbackVaryings(I*I)", pId, varyings, type);
        } else if (cap.GL_EXT_transform_feedback) {
            LOGGER.trace(GL_MARKER, "glTransformFeedbackVaryingsEXT({}, {}, {})", pId, varyings, type);
            EXTTransformFeedback.glTransformFeedbackVaryingsEXT(pId, varyings, type);
            assert GL11.glGetError() == 0 : glErrorMsg("glTransformFeedbackVaryingsEXT(I*I)", pId, varyings, type);
        } else {
            throw new UnsupportedOperationException("glTransformFeedbackVaryings[EXT] is not supported!");
        }
    }

    public final void glBindAttribLocation(int pId, int index, CharSequence name) {
        final ContextCapabilities cap = GL.getCapabilities();

        if (cap.OpenGL20) {
            LOGGER.trace(GL_MARKER, "glBindAttribLocation({}, {}, {})", pId, index, name);
            GL20.glBindAttribLocation(pId, index, name);
            assert GL11.glGetError() == 0 : glErrorMsg("glBindAttribLocation(II*)", pId, index, name);
        } else if (cap.GL_ARB_vertex_shader) {
            ARBVertexShader.glBindAttribLocationARB(pId, index, name);
            assert GL11.glGetError() == 0 : glErrorMsg("glBindAttribLocationARB(II*)", pId, index, name);
        } else {
            throw new UnsupportedOperationException("glBindAttribLocation[ARB] is not supported!");
        }
    }

    public final int glGetUniformLocation(int pId, CharSequence uName) {
        final ContextCapabilities cap = GL.getCapabilities();

        if (cap.OpenGL20) {
            LOGGER.trace(GL_MARKER, "glGetUniformLocation({}, {})", pId, uName);
            final int loc = GL20.glGetUniformLocation(pId, uName);
            assert GL11.glGetError() == 0 : glErrorMsg("glGetUniformLocation(I*)", pId, uName);
            return loc;
        } else if (cap.GL_ARB_shader_objects) {
            LOGGER.trace(GL_MARKER, "glGetUniformLocationARB({}, {})", pId, uName);
            final int loc = ARBShaderObjects.glGetUniformLocationARB(pId, uName);
            assert GL11.glGetError() == 0 : glErrorMsg("glGetUniformLocation(I*)", pId, uName);
            return loc;
        } else {
            throw new UnsupportedOperationException("glGetUniformLocation[ARB] is not supported!");
        }
    }

    public final void glUseProgram(int pId) {
        final ContextCapabilities cap = GL.getCapabilities();

        if (cap.OpenGL20) {
            LOGGER.trace(GL_MARKER, "glUseProgram({})", pId);
            GL20.glUseProgram(pId);
            assert GL11.glGetError() == 0 : glErrorMsg("glUseProgram(I)", pId);
        } else if (cap.GL_ARB_shader_objects) {
            LOGGER.trace(GL_MARKER, "glUseProgramObjectARB({})", pId);
            ARBShaderObjects.glUseProgramObjectARB(pId);
            assert GL11.glGetError() == 0 : glErrorMsg("glUseProgramObjectARB(I)", pId);
        } else {
            throw new UnsupportedOperationException("[glUseProgram | glUseProgramObjectARB] is not supported!");
        }
    }

    public final void glSamplerParameterf(int target, int sId, float value) {
        final ContextCapabilities cap = GL.getCapabilities();

        if (cap.OpenGL33) {
            LOGGER.trace(GL_MARKER, "glSamplerParameterf({}, {}, {})", target, sId, value);
            GL33.glSamplerParameterf(target, sId, value);
            assert GL11.glGetError() == 0 : glErrorMsg("glSamplerParameterf(IIF)", target, sId, value);
        } else if (cap.GL_ARB_sampler_objects) {
            LOGGER.trace(GL_MARKER, "glSamplerParameterf({}, {}, {}) (ARB)", target, sId, value);
            ARBSamplerObjects.glSamplerParameterf(target, sId, value);
            assert GL11.glGetError() == 0 : glErrorMsg("glSamplerParameterfARB(IIF)", target, sId, value);
        } else {
            throw new UnsupportedOperationException("glSamplerParameterf is not supported!");
        }
    }

    public final void glBindSampler(int unit, int sId) {
        final ContextCapabilities cap = GL.getCapabilities();

        if (cap.OpenGL33) {
            LOGGER.trace(GL_MARKER, "glBindSampler({}, {})", unit, sId);
            GL33.glBindSampler(unit, sId);
            assert GL11.glGetError() == 0 : glErrorMsg("glBindSampler(II)", unit, sId);
        } else if (cap.GL_ARB_sampler_objects) {
            LOGGER.trace(GL_MARKER, "glBindSampler({}, {}) (ARB)", unit, sId);
            ARBSamplerObjects.glBindSampler(unit, sId);
            assert GL11.glGetError() == 0 : glErrorMsg("glBindSamplerARB(II)", unit, sId);
        } else {
            throw new UnsupportedOperationException("glBindSampler is not supported!");
        }
    }

    public final int glGenSamplers() {
        final ContextCapabilities cap = GL.getCapabilities();

        if (cap.OpenGL33) {
            LOGGER.trace(GL_MARKER, "glGenSamplers()");
            final int sId = GL33.glGenSamplers();
            assert GL11.glGetError() == 0 : glErrorMsg("glGenSamplers()");
            return sId;
        } else if (cap.GL_ARB_sampler_objects) {
            LOGGER.trace(GL_MARKER, "glGenSamplers() (ARB)");
            final int sId = ARBSamplerObjects.glGenSamplers();
            assert GL11.glGetError() == 0 : glErrorMsg("glGenSamplersARB()");
            return sId;
        } else {
            throw new UnsupportedOperationException("glGenSamplers is not supported!");
        }
    }

    public final void glDeleteSamplers(int sId) {
        final ContextCapabilities cap = GL.getCapabilities();

        if (cap.OpenGL33) {
            LOGGER.trace(GL_MARKER, "glDeleteSamplers({})", sId);
            GL33.glDeleteSamplers(sId);
            assert GL11.glGetError() == 0 : glErrorMsg("glDeleteSamplers(I)", sId);
        } else if (cap.GL_ARB_sampler_objects) {
            LOGGER.trace(GL_MARKER, "glDeleteSamplers({}) (ARB)", sId);
            ARBSamplerObjects.glDeleteSamplers(sId);
            assert GL11.glGetError() == 0 : glErrorMsg("glDeleteSamplersARB(I)", sId);
        } else {
            throw new UnsupportedOperationException("glDeleteSamplers is not supported!");
        }
    }

    public final void glSamplerParameteri(int pId, int sId, int value) {
        final ContextCapabilities cap = GL.getCapabilities();

        if (cap.OpenGL33) {
            LOGGER.trace(GL_MARKER, "glSamplerParameteri({}, {}, {})", pId, sId, value);
            GL33.glSamplerParameteri(pId, sId, value);
            assert GL11.glGetError() == 0 : glErrorMsg("glSamplerParameteri(III)", pId, sId, value);
        } else if (cap.GL_ARB_sampler_objects) {
            LOGGER.trace(GL_MARKER, "glSamplerParameteri({}, {}, {}) (ARB)", pId, sId, value);
            ARBSamplerObjects.glSamplerParameteri(pId, sId, value);
            assert GL11.glGetError() == 0 : glErrorMsg("glSamplerParameteri(III)", pId, sId, value);
        } else {
            throw new UnsupportedOperationException("glSamplerParameteri is not supported!");
        }
    }

    public final void glScissor(int left, int bottom, int width, int height) {
        LOGGER.trace(GL_MARKER, "glScissor({}, {}, {}, {})", left, bottom, width, height);
        GL11.glScissor(left, bottom, width, height);
        assert GL11.glGetError() == 0 : glErrorMsg("glScissor(IIII)", left, bottom, width, height);
    }

    public final String glGetShaderInfoLog(int shId, int length) {
        final ContextCapabilities cap = GL.getCapabilities();

        if (cap.OpenGL20) {
            LOGGER.trace(GL_MARKER, "glGetShaderInfoLog({}, {})", shId, length);
            final String res = GL20.glGetShaderInfoLog(shId, length);
            assert GL11.glGetError() == 0 : glErrorMsg("glGetShaderInfoLog(II)", shId, length);
            return res;
        } else if (cap.GL_ARB_shader_objects) {
            LOGGER.trace(GL_MARKER, "glGetInfoLogARB({}, {})", shId, length);
            final String res = ARBShaderObjects.glGetInfoLogARB(shId, length);
            assert GL11.glGetError() == 0 : glErrorMsg("glGetInfoLogARB(II)", shId, length);
            return res;
        } else {
            throw new UnsupportedOperationException("[glGetShaderInfoLog | glGetInfoLogARB] is not supported!");
        }
    }

    public final void glDeleteShader(int shId) {
        final ContextCapabilities cap = GL.getCapabilities();

        if (cap.OpenGL20) {
            LOGGER.trace(GL_MARKER, "glDeleteShader({})", shId);
            GL20.glDeleteShader(shId);
            assert GL11.glGetError() == 0 : glErrorMsg("glDeleteShader(I)", shId);
        } else if (cap.GL_ARB_shader_objects) {
            LOGGER.trace(GL_MARKER, "glDeleteObjectARB({})", shId);
            ARBShaderObjects.glDeleteObjectARB(shId);
            assert GL11.glGetError() == 0 : glErrorMsg("glDeleteObjectARB(I)", shId);
        } else {
            throw new UnsupportedOperationException("[glDeleteShader | glDeleteObjectARB] is not supported!");
        }
    }

    public final int glCreateShader(int type) {
        final ContextCapabilities cap = GL.getCapabilities();

        if (cap.OpenGL20) {
            LOGGER.trace(GL_MARKER, "glCreateShader({})", type);
            final int shId = GL20.glCreateShader(type);
            assert GL11.glGetError() == 0 : glErrorMsg("glCreateShader(I)", type);
            return shId;
        } else if (cap.GL_ARB_shader_objects) {
            LOGGER.trace(GL_MARKER, "glCreateShaderObjectARB({})", type);
            final int shId = ARBShaderObjects.glCreateShaderObjectARB(type);
            assert GL11.glGetError() == 0 : glErrorMsg("glCreateShaderObjectARB(I)", type);
            return shId;
        } else {
            throw new UnsupportedOperationException("glCreateShader[ObjectARB] is not supported!");
        }
    }

    public final void glShaderSource(int shId, CharSequence src) {
        final ContextCapabilities cap = GL.getCapabilities();

        if (cap.OpenGL20) {
            LOGGER.trace(GL_MARKER, "glShaderSource({}, {})", shId, src);
            GL20.glShaderSource(shId, src);
            assert GL11.glGetError() == 0 : glErrorMsg("glShaderSource(I*)", shId, src);
        } else if (cap.GL_ARB_shader_objects) {
            ARBShaderObjects.glShaderSourceARB(shId, src);
            assert GL11.glGetError() == 0 : glErrorMsg("glShaderSourceARB(I*)", shId, src);
        } else {
            throw new UnsupportedOperationException("glShaderSource[ARB] is not supported!");
        }
    }

    public final void glCompileShader(int shId) {
        final ContextCapabilities cap = GL.getCapabilities();

        if (cap.OpenGL20) {
            LOGGER.trace(GL_MARKER, "glCompileShader({})", shId);
            GL20.glCompileShader(shId);
            assert GL11.glGetError() == 0 : glErrorMsg("glCompileShader(I)", shId);
        } else if (cap.GL_ARB_shader_objects) {
            LOGGER.trace(GL_MARKER, "glCompileShaderARB({})", shId);
            ARBShaderObjects.glCompileShaderARB(shId);
            assert GL11.glGetError() == 0 : glErrorMsg("glCompileShaderARB(I)", shId);
        } else {
            throw new UnsupportedOperationException("glCompileShader[ARB] is not supported!");
        }
    }

    public final int glGetShaderi(int shId, int pId) {
        final ContextCapabilities cap = GL.getCapabilities();

        if (cap.OpenGL20) {
            LOGGER.trace(GL_MARKER, "glGetShaderi({}, {})", shId, pId);
            final int res = GL20.glGetShaderi(shId, pId);
            assert GL11.glGetError() == 0 : glErrorMsg("glGetShaderi(II)", shId, pId);
            return res;
        } else if (cap.GL_ARB_shader_objects) {
            final int res = ARBShaderObjects.glGetObjectParameteriARB(shId, pId);
            assert GL11.glGetError() == 0 : glErrorMsg("glGetObjectParameteriARB(II)", shId, pId);
            return res;
        } else {
            throw new UnsupportedOperationException("[glGetShaderi | glGetObjectParameteriARB] is not supported!");
        }
    }

    public final int glGetTexLevelParameteri(int target, int i, int id) {
        LOGGER.trace(GL_MARKER, "glGetTexLevelParameteri({}, {}, {})", target, i, id);
        final int res = GL11.glGetTexLevelParameteri(target, i, id);
        assert GL11.glGetError() == 0 : "glGetTexLevelParameteri(III) failed!";
        return res;
    }

    public final void glTexBuffer(int target, int internalFormat, int bufferId) {
        final ContextCapabilities cap = GL.getCapabilities();

        if (cap.OpenGL30) {
            LOGGER.trace(GL_MARKER, "glTexBuffer({}, {}, {})", target, internalFormat, bufferId);
            // this is listed as an OpenGL3.0 call?
            GL31.glTexBuffer(target, internalFormat, bufferId);
            assert GL11.glGetError() == 0 : glErrorMsg("glTexBuffer(III)", target, internalFormat, bufferId);
        } else if (cap.GL_ARB_texture_buffer_object) {
            LOGGER.trace(GL_MARKER, "glTexBufferARB({}, {}, {})", target, internalFormat, bufferId);
            ARBTextureBufferObject.glTexBufferARB(target, internalFormat, bufferId);
            assert GL11.glGetError() == 0 : glErrorMsg("glTexBufferARB(III)", target, internalFormat, bufferId);
        } else {
            throw new UnsupportedOperationException("glTexBuffer[ARB] is not supported!");
        }
    }

    public final void glBindTexture(int target, int texId) {
        LOGGER.trace(GL_MARKER, "glBindTexture({}, {})", target, texId);
        GL11.glBindTexture(target, texId);
        assert GL11.glGetError() == 0 : glErrorMsg("glBindTexture(II)", target, texId);
    }

    public final void glTexSubImage2D(int target, int level, int xOffset, int yOffset, int width, int height, int format, int type, long ptr) {
        LOGGER.trace(GL_MARKER, "glTexSubImage2D({}, {}, {}, {}, {}, {}, {}, {}, {})", target, level, xOffset, yOffset, width, height, format, type, ptr);
        GL11.glTexSubImage2D(target, level, xOffset, yOffset, width, height, format, type, ptr);
        assert GL11.glGetError() == 0 : glErrorMsg("glTexSubImage2D(IIIIIIIIL)", target, level, xOffset, yOffset, width, height, format, type, ptr);
    }

    public final void glDeleteTextures(int texId) {
        LOGGER.trace(GL_MARKER, "glDeleteTextures({})", texId);
        GL11.glDeleteTextures(texId);
        assert GL11.glGetError() == 0 : glErrorMsg("glDeleteTextures(I)", texId);
    }

    public final float glGetFloat(int floatId) {
        LOGGER.trace(GL_MARKER, "glGetFloat({})", floatId);
        final float value = GL11.glGetFloat(floatId);
        assert GL11.glGetError() == 0 : glErrorMsg("glGetFloat(I)", floatId);
        return value;
    }

    public final int glGetInteger(int intId) {
        LOGGER.trace(GL_MARKER, "glGetInteger({})", intId);
        final int value = GL11.glGetInteger(intId);
        assert GL11.glGetError() == 0 : glErrorMsg("glGetInteger(I)", intId);
        return value;
    }

    public final void glVertexAttribDivisor(int index, int divisor) {
        final ContextCapabilities cap = GL.getCapabilities();

        if (cap.OpenGL33) {
            LOGGER.trace(GL_MARKER, "glVertexAttribDivisor({}, {})", index, divisor);
            GL33.glVertexAttribDivisor(index, divisor);
            assert GL11.glGetError() == 0 : glErrorMsg("glVertexAttribDivisor(II)", index, divisor);
        } else if (cap.GL_ARB_instanced_arrays) {
            LOGGER.trace(GL_MARKER, "glVertexAttribDivisor({}, {}) (ARB)", index, divisor);
            ARBInstancedArrays.glVertexAttribDivisorARB(index, divisor);
            assert GL11.glGetError() == 0 : glErrorMsg("glVertexAttribDivisorARB(II)", index, divisor);
        } else {
            throw new UnsupportedOperationException("glVertexAttribDivisor[ARB] is not supported!");
        }
    }

    public final void glEnableVertexAttribArray(int index) {
        final ContextCapabilities cap = GL.getCapabilities();

        if (cap.OpenGL20) {
            LOGGER.trace(GL_MARKER, "glEnableVertexAttribArray({})", index);
            GL20.glEnableVertexAttribArray(index);
            assert GL11.glGetError() == 0 : glErrorMsg("glEnableVertexAttribArray(I)", index);
        } else if (cap.GL_ARB_vertex_shader) {
            LOGGER.trace(GL_MARKER, "glEnableVertexAttribArrayARB({})", index);
            ARBVertexShader.glEnableVertexAttribArrayARB(index);
            assert GL11.glGetError() == 0 : glErrorMsg("glEnableVertexAttribArrayARB(I)", index);
        } else {
            throw new UnsupportedOperationException("glEnableVertexAttribArray[ARB] is not supported!");
        }
    }

    public final void glVertexAttribLPointer(int index, int size, int type, int stride, long offset) {
        final ContextCapabilities cap = GL.getCapabilities();

        if (cap.OpenGL41) {
            LOGGER.trace(GL_MARKER, "glVertexAttribLPointer({}, {}, {}, {}, {})", index, size, type, stride, offset);
            GL41.glVertexAttribLPointer(index, size, type, stride, offset);
            assert GL11.glGetError() == 0 : glErrorMsg("glVertexAttribLPointer(IIIIL)", index, size, type, stride, offset);
        } else if (cap.GL_ARB_vertex_attrib_64bit) {
            LOGGER.trace(GL_MARKER, "glVertexAttribLPointer({}, {}, {}, {}, {}) (ARB)", index, size, type, stride, offset);
            ARBVertexAttrib64Bit.glVertexAttribLPointer(index, size, type, stride, offset);
            assert GL11.glGetError() == 0 : glErrorMsg("glVertexAttribLPointerARB(IIIIL)", index, size, type, stride, offset);
        } else if (cap.GL_EXT_vertex_attrib_64bit) {
            LOGGER.trace(GL_MARKER, "glVertexAttribLPointerEXT({}, {}, {}, {}, {})", index, size, type, stride, offset);
            EXTVertexAttrib64bit.glVertexAttribLPointerEXT(index, size, type, stride, offset);
            assert GL11.glGetError() == 0 : glErrorMsg("glVertexAttribLPointerEXT(IIIIL)", index, size, type, stride, offset);
        } else {
            throw new UnsupportedOperationException("glVertexAttribLPointer[EXT] is not supported!");
        }
    }

    public final void glVertexAttribPointer(int index, int size, int type, boolean normalized, int stride, long offset) {
        final ContextCapabilities cap = GL.getCapabilities();

        if (cap.OpenGL20) {
            LOGGER.trace(GL_MARKER, "glVertexAttribPointer({}, {}, {}, {}, {}, {})", index, size, type, normalized, stride, offset);
            GL20.glVertexAttribPointer(index, size, type, normalized, stride, offset);
            assert GL11.glGetError() == 0 : glErrorMsg("glVertexAttribPointer(IIIBIL)", index, size, type, normalized, stride, offset);
        } else if (cap.GL_ARB_vertex_shader) {
            LOGGER.trace(GL_MARKER, "glVertexAttribPointerARB({}, {}, {}, {}, {}, {})", index, size, type, normalized, stride, offset);
            ARBVertexShader.glVertexAttribPointerARB(index, size, type, normalized, stride, offset);
            assert GL11.glGetError() == 0 : glErrorMsg("glVertexAttribPointerARB(IIIBIL)", index, size, type, normalized, stride, offset);
        } else {
            throw new UnsupportedOperationException("glVertexAttribPointer[ARB] is not supported!");
        }
    }

    public final void glBeginTransformFeedback(int mode) {
        final ContextCapabilities cap = GL.getCapabilities();

        if (cap.OpenGL30) {
            LOGGER.trace(GL_MARKER, "glBeginTransformFeedback({})", mode);
            GL30.glBeginTransformFeedback(mode);
            assert GL11.glGetError() == 0 : glErrorMsg("glBeginTransformFeedback(I)", mode);
        } else if (cap.GL_EXT_transform_feedback) {
            LOGGER.trace(GL_MARKER, "glBeginTransformFeedbackEXT({})", mode);
            EXTTransformFeedback.glBeginTransformFeedbackEXT(mode);
            assert GL11.glGetError() == 0 : glErrorMsg("glBeginTransformFeedbackEXT(I)", mode);
        } else {
            throw new UnsupportedOperationException("glBeginTransformFeedback[EXT] is not supported!");
        }
    }

    public final void glEndTransformFeedback() {
        final ContextCapabilities cap = GL.getCapabilities();

        if (cap.OpenGL30) {
            LOGGER.trace(GL_MARKER, "glEndTransformFeedback()");
            GL30.glEndTransformFeedback();
            assert GL11.glGetError() == 0 : "glEndTransformFeedback() failed!";
        } else if (cap.GL_EXT_transform_feedback) {
            LOGGER.trace(GL_MARKER, "glEndTransformFeedbackEXT()");
            EXTTransformFeedback.glEndTransformFeedbackEXT();
            assert GL11.glGetError() == 0 : glErrorMsg("glEndTransformFeedback[EXT] is not supported!");
        }
    }

    public final void glBindBuffer(int target, int buffId) {
        LOGGER.trace(GL_MARKER, "glBindBuffer({}, {})", target, buffId);
        GL15.glBindBuffer(target, buffId);
        assert GL11.glGetError() == 0 : glErrorMsg("glBindBuffer(II)", target, buffId);
    }

    public final void glBindVertexArray(int vaoId) {
        final ContextCapabilities cap = GL.getCapabilities();

        if (cap.OpenGL30) {
            LOGGER.trace(GL_MARKER, "glBindVertexArray({})", vaoId);
            GL30.glBindVertexArray(vaoId);
            assert GL11.glGetError() == 0 : glErrorMsg("glBindVertexArray(I)", vaoId);
        } else if (cap.GL_ARB_vertex_array_object) {
            ARBVertexArrayObject.glBindVertexArray(vaoId);
            assert GL11.glGetError() == 0 : glErrorMsg("glBindVertexArrayARB(I)", vaoId);
        } else {
            throw new UnsupportedOperationException("glBindVertexArray is not supported!");
        }
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

        if (cap.OpenGL30) {
            LOGGER.trace(GL_MARKER, "glDeleteVertexArrays({})", vao);
            GL30.glDeleteVertexArrays(vao);
            assert GL11.glGetError() == 0 : "glDeleteVertexArrays(I) failed!";
        } else if (cap.GL_ARB_vertex_array_object) {
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
        assert GL11.glGetError() == 0 : glErrorMsg("glMultiDrawArrays(I**)", mode, first, count);
    }

    public final void glDrawArraysInstanced(int drawMode, int first, int count, int instanceCount) {
        final ContextCapabilities cap = GL.getCapabilities();

        if (cap.OpenGL31) {
            LOGGER.trace(GL_MARKER, "glDrawArraysInstanced({}, {}, {}, {})", drawMode, first, count, instanceCount);
            GL31.glDrawArraysInstanced(drawMode, first, count, instanceCount);
            assert GL11.glGetError() == 0 : glErrorMsg("glDrawArraysInstanced(IIII)", GLDrawMode.of(drawMode).get(), first, count, instanceCount);
        } else if (cap.GL_ARB_draw_instanced) {
            LOGGER.trace(GL_MARKER, "glDrawArraysInstancedARB({}, {}, {}, {})", drawMode, first, count, instanceCount);
            ARBDrawInstanced.glDrawArraysInstancedARB(drawMode, first, count, instanceCount);
            assert GL11.glGetError() == 0 : glErrorMsg("glDrawArraysInstancedARB(IIII)", GLDrawMode.of(drawMode).get(), first, count, instanceCount);
        } else {
            final int currentProgram = glGetInteger(GL20.GL_CURRENT_PROGRAM);
            final int uLoc = glGetUniformLocation(currentProgram, "gl_InstanceID");

            for (int i = 0; i < instanceCount; i++) {
                if (cap.OpenGL20) {
                    LOGGER.trace(GL_MARKER, "glUniform1i({}, {})", uLoc, i);
                    GL20.glUniform1i(uLoc, i);
                    assert GL11.glGetError() == 0 : glErrorMsg("glUniform1i(II)", uLoc, i);
                } else if (cap.GL_ARB_shader_objects) {
                    LOGGER.trace(GL_MARKER, "glUniform1iARB({}, {})", uLoc, i);
                    ARBShaderObjects.glUniform1iARB(uLoc, i);
                    assert GL11.glGetError() == 0 : glErrorMsg("glUniform1iARB(II)", uLoc, i);
                } else {
                    throw new UnsupportedOperationException("[glDrawArraysInstanced[ARB] | glUniform1i[ARB] is not supported!");
                }

                glDrawArrays(drawMode, first, count);
            }
        }
    }

    public final void glDrawElementsInstanced(int drawMode, int count, int type, long offset, int instanceCount) {
        final ContextCapabilities cap = GL.getCapabilities();

        if (cap.OpenGL31) {
            LOGGER.trace(GL_MARKER, "glDrawElementsInstanced({}, {}, {}, {}, {})", drawMode, count, type, offset, instanceCount);
            GL31.glDrawElementsInstanced(drawMode, count, type, offset, instanceCount);
            assert GL11.glGetError() == 0 : glErrorMsg("glDrawElementsInstanced(IIILI)", GLDrawMode.of(drawMode).get(), count, GLType.of(type).get(), offset, instanceCount);
        } else if (cap.GL_ARB_draw_instanced) {
            LOGGER.trace(GL_MARKER, "glDrawElementsInstancedARB({}, {}, {}, {}, {})", drawMode, count, type, offset, instanceCount);
            ARBDrawInstanced.glDrawElementsInstancedARB(drawMode, count, type, offset, instanceCount);
            assert GL11.glGetError() == 0 : glErrorMsg("glDrawElementsInstancedARB(IIILI)", GLDrawMode.of(drawMode).get(), count, GLType.of(type).get(), offset, instanceCount);
        } else {
            final int currentProgram = glGetInteger(GL20.GL_CURRENT_PROGRAM);
            final int uLoc = glGetUniformLocation(currentProgram, "gl_InstanceID");

            for (int i = 0; i < instanceCount; i++) {
                if (cap.OpenGL20) {
                    LOGGER.trace(GL_MARKER, "glUniform1i({}, {})", uLoc, i);
                    GL20.glUniform1i(uLoc, i);
                    assert GL11.glGetError() == 0 : glErrorMsg("glUniform1i(II)", uLoc, i);
                } else if (cap.GL_ARB_shader_objects) {
                    LOGGER.trace(GL_MARKER, "glUniform1iARB({}, {})", uLoc, i);
                    ARBShaderObjects.glUniform1iARB(uLoc, i);
                    assert GL11.glGetError() == 0 : glErrorMsg("glUniform1iARB(II)", uLoc, i);
                } else {
                    throw new UnsupportedOperationException("[glDrawElementsInstanced[ARB] | glUniform1i[ARB]] is not supported!");
                }

                glDrawElements(drawMode, count, type, offset);
            }
        }
    }

    public final void glDrawElements(int mode, int count, int type, long offset) {
        LOGGER.trace(GL_MARKER, "glDrawElements({}, {}, {}, {})", mode, count, type, offset);
        GL11.glDrawElements(mode, count, type, offset);
        assert GL11.glGetError() == 0 : glErrorMsg("glDrawElements(IIIL)", GLDrawMode.of(mode).get(), count, GLType.of(type).get(), offset);
    }

    public final void glDrawArrays(int mode, int first, int count) {
        LOGGER.trace(GL_MARKER, "glDrawArrays({}, {}, {})", mode, first, count);
        GL11.glDrawArrays(mode, first, count);
        assert GL11.glGetError() == 0 : glErrorMsg("glDrawArrays(III)", GLDrawMode.of(mode).get(), first, count);
    }

    public final void glDrawArraysIndirect(int mode, long offset) {
        final ContextCapabilities cap = GL.getCapabilities();

        if (cap.OpenGL40) {
            LOGGER.trace(GL_MARKER, "glDrawArraysIndirect({}, {})", mode, offset);
            GL40.glDrawArraysIndirect(mode, offset);
            assert GL11.glGetError() == 0 : glErrorMsg("glDrawArraysIndirect(IL)", GLDrawMode.of(mode).get(), offset);
        } else if (cap.GL_ARB_draw_indirect) {
            LOGGER.trace(GL_MARKER, "glDrawArraysIndirect({}, {})", mode, offset);
            ARBDrawIndirect.glDrawArraysIndirect(mode, offset);
            assert GL11.glGetError() == 0 : glErrorMsg("glDrawArraysIndirectARB(IL)", GLDrawMode.of(mode).get(), offset);
        } else {
            throw new UnsupportedOperationException("glDrawArraysIndirect is not supported!");
        }
    }

    public final void glDrawElementsIndirect(int mode, int type, long offset) {
        final ContextCapabilities cap = GL.getCapabilities();

        if (cap.OpenGL40) {
            LOGGER.trace(GL_MARKER, "glDrawElementsIndirect({}, {}, {})", mode, type, offset);
            GL40.glDrawElementsIndirect(mode, type, offset);
            assert GL11.glGetError() == 0 : glErrorMsg("glDrawElementsIndirect(IIL)", GLDrawMode.of(mode).get(), GLType.of(type).get(), offset);
        } else if (cap.GL_ARB_draw_indirect) {
            LOGGER.trace(GL_MARKER, "glDrawElementsIndirect({}, {}, {}) (ARB)", mode, type, offset);
            ARBDrawIndirect.glDrawElementsIndirect(mode, type, offset);
            assert GL11.glGetError() == 0 : glErrorMsg("glDrawElementsIndirectARB(IIL)", GLDrawMode.of(mode).get(), GLType.of(type).get(), offset);
        } else {
            throw new UnsupportedOperationException("glDrawElementsIndirect is not supported!");
        }
    }

    public final void glBeginConditionalRender(int query, int mode) {
        final ContextCapabilities cap = GL.getCapabilities();

        if (cap.OpenGL30) {
            LOGGER.trace(GL_MARKER, "glBeginConditionalRender({}, {})", query, mode);
            GL30.glBeginConditionalRender(query, mode);
            assert GL11.glGetError() == 0 : glErrorMsg("glBeginConditionalRender(II)", GLDrawQueryMode.of(query).get(), GLDrawMode.of(mode).get());
        } else {
            throw new UnsupportedOperationException("glBeginConditionalRender is not supported!");
        }
    }

    public final void glEndConditionalRender() {
        final ContextCapabilities cap = GL.getCapabilities();

        if (cap.OpenGL30) {
            LOGGER.trace(GL_MARKER, "glEndConditionalRender()");
            GL30.glEndConditionalRender();
            assert GL11.glGetError() == 0 : glErrorMsg("glEndConditionalRender()");
        } else {
            throw new UnsupportedOperationException("glEndConditionalRender is not supported!");
        }
    }

    public final int glGenQueries() {
        LOGGER.trace(GL_MARKER, "glGenQueries()");
        final int query = GL15.glGenQueries();
        assert GL11.glGetError() == 0 : glErrorMsg("glGenQueries()");
        return query;
    }

    public final void glDeleteQueries(int query) {
        LOGGER.trace(GL_MARKER, "glDeleteQueries({})", query);
        GL15.glDeleteQueries(query);
        assert GL11.glGetError() == 0 : glErrorMsg("glDeleteQueries(I)", query);
    }

    public final void glBeginQuery(int condition, int query) {
        LOGGER.trace(GL_MARKER, "glBeginQuery({}, {})", condition, query);
        GL15.glBeginQuery(condition, query);
        assert GL11.glGetError() == 0 : glErrorMsg("glBeginQuery(II)", condition, query);
    }

    public final void glEndQuery(int condition) {
        LOGGER.trace(GL_MARKER, "glEndQuery({})", condition);
        GL15.glEndQuery(condition);
        assert GL11.glGetError() == 0 : glErrorMsg("glEndQuery(I)", condition);
    }

    public final void glDepthFunc(int depthFunc) {
        LOGGER.trace(GL_MARKER, "glDepthFunc({})", depthFunc);
        GL11.glDepthFunc(depthFunc);
        assert GL11.glGetError() == 0 : glErrorMsg("glDepthFunc(I)", GLDepthFunc.of(depthFunc).get());
    }

    public final void glClearColor(float red, float green, float blue, float alpha) {
        LOGGER.trace(GL_MARKER, "glClearColor({}, {}, {}, {})", red, green, blue, alpha);
        GL11.glClearColor(red, green, blue, alpha);
        assert GL11.glGetError() == 0 : glErrorMsg("glClearColor(FFFF)", red, green, blue, alpha);
    }

    public final void glClearDepth(double depth) {
        LOGGER.trace(GL_MARKER, "glClearDepth({})", depth);
        GL11.glClearDepth(depth);
        assert GL11.glGetError() == 0 : glErrorMsg("glClearDepth(D)", depth);
    }

    public final void glClear(int bitfield) {
        LOGGER.trace(GL_MARKER, "glClear({})", bitfield);
        GL11.glClear(bitfield);
        assert GL11.glGetError() == 0 : glErrorMsg("glClear(I)", bitfield);
    }

    public final void glDeleteBuffers(int bufferId) {
        LOGGER.trace(GL_MARKER, "glDeleteBuffers({})", bufferId);
        GL15.glDeleteBuffers(bufferId);
        assert GL11.glGetError() == 0 : glErrorMsg("glDeleteBuffers(I)", bufferId);
    }

    public final void glEnable(int capability) {
        LOGGER.trace(GL_MARKER, "glEnable({})", capability);
        GL11.glEnable(capability);
        assert GL11.glGetError() == 0 : glErrorMsg("glEnable(I)", capability);
    }

    public final void glDisable(int capability) {
        LOGGER.trace(GL_MARKER, "glDisable({})", capability);
        GL11.glDisable(capability);
        assert GL11.glGetError() == 0 : glErrorMsg("glDisable(I)", capability);
    }

    public final void glBlendEquationSeparate(int rgb, int alpha) {
        final ContextCapabilities cap = GL.getCapabilities();

        if (cap.OpenGL20) {
            LOGGER.trace(GL_MARKER, "glBlendEquationSeparate({}, {})", rgb, alpha);
            GL20.glBlendEquationSeparate(rgb, alpha);
            assert GL11.glGetError() == 0 : glErrorMsg("glBlendEquationSeparate(II)", GLBlendEquation.of(rgb).get(), GLBlendEquation.of(alpha).get());
        } else if (cap.GL_EXT_blend_equation_separate) {
            LOGGER.trace(GL_MARKER, "glBlendEquationSeparateEXT(II)", rgb, alpha);
            EXTBlendEquationSeparate.glBlendEquationSeparateEXT(rgb, alpha);
            assert GL11.glGetError() == 0 : glErrorMsg("glBlendEquationSeparateEXT(II)", GLBlendEquation.of(rgb).get(), GLBlendEquation.of(alpha).get());
        } else {
            throw new UnsupportedOperationException("glBlendEquationSeparate[EXT] is not supported!");
        }
    }

    public final void glBlendFuncSeparate(int rgbSrc, int rgbDst, int aSrc, int aDst) {
        LOGGER.trace(GL_MARKER, "glBlendFuncSeparate({}, {}, {}, {})", rgbSrc, rgbDst, aSrc, aDst);
        GL14.glBlendFuncSeparate(rgbSrc, rgbDst, aSrc, aDst);
        assert GL11.glGetError() == 0 : glErrorMsg("glBlendFuncSeparate(IIII)", GLBlendFunc.of(rgbSrc).get(), GLBlendFunc.of(rgbDst).get(), GLBlendFunc.of(aSrc).get(), GLBlendFunc.of(aDst).get());
    }

    public final void glUniform1f(int loc, float value) {
        final ContextCapabilities cap = GL.getCapabilities();

        if (cap.OpenGL20) {
            LOGGER.trace(GL_MARKER, "glUniform1f({}, {})", loc, value);
            GL20.glUniform1f(loc, value);
            assert GL11.glGetError() == 0 : glErrorMsg("glUniform1f(IF)", loc, value);
        } else if (cap.GL_ARB_shader_objects) {
            LOGGER.trace(GL_MARKER, "glUniform1fARB({}, {})", loc, value);
            ARBShaderObjects.glUniform1fARB(loc, value);
            assert GL11.glGetError() == 0 : glErrorMsg("glUniform1fARB(IF)", loc, value);
        } else {
            throw unsupportedShaderObjects();
        }
    }

    public final void glUniform2f(int loc, float v0, float v1) {
        final ContextCapabilities cap = GL.getCapabilities();

        if (cap.OpenGL20) {
            LOGGER.trace(GL_MARKER, "glUniform2f({}, {}, {})", loc, v0, v1);
            GL20.glUniform2f(loc, v0, v1);
            assert GL11.glGetError() == 0 : glErrorMsg("glUniform2f(IFF)", loc, v0, v1);
        } else if (cap.GL_ARB_shader_objects) {
            LOGGER.trace(GL_MARKER, "glUniform2fARB({}, {}, {})", loc, v0, v1);
            ARBShaderObjects.glUniform2fARB(loc, v0, v1);
            assert GL11.glGetError() == 0 : glErrorMsg("glUniform2fARB(IFF)", loc, v0, v1);
        } else {
            throw unsupportedShaderObjects();
        }
    }

    public final void glUniform3f(int loc, float v0, float v1, float v2) {
        final ContextCapabilities cap = GL.getCapabilities();

        if (cap.OpenGL20) {
            LOGGER.trace(GL_MARKER, "glUniform3f({}, {}, {}, {})", loc, v0, v1, v2);
            GL20.glUniform3f(loc, v0, v1, v2);
            assert GL11.glGetError() == 0 : glErrorMsg("glUniform3f(IFFF)", loc, v0, v1, v2);
        } else if (cap.GL_ARB_shader_objects) {
            LOGGER.trace(GL_MARKER, "glUniform3fARB({}, {}, {}, {})", loc, v0, v1, v2);
            ARBShaderObjects.glUniform3fARB(loc, v0, v1, v2);
            assert GL11.glGetError() == 0 : glErrorMsg("glUniform3fARB(IFFF)", loc, v0, v1, v2);
        } else {
            throw unsupportedShaderObjects();
        }
    }

    public final void glUniform4f(int loc, float v0, float v1, float v2, float v3) {
        final ContextCapabilities cap = GL.getCapabilities();

        if (cap.OpenGL20) {
            LOGGER.trace(GL_MARKER, "glUniform4f({}, {}, {}, {}, {})", loc, v0, v1, v2, v3);
            GL20.glUniform4f(loc, v0, v1, v2, v3);
            assert GL11.glGetError() == 0 : glErrorMsg("glUniform4f(IFFFF)", loc, v0, v1, v2, v3);
        } else if (cap.GL_ARB_shader_objects) {
            LOGGER.trace(GL_MARKER, "glUniform4fARB({}, {}, {}, [}, {})", loc, v0, v1, v2, v3);
            ARBShaderObjects.glUniform4fARB(loc, v0, v1, v2, v3);
            assert GL11.glGetError() == 0 : glErrorMsg("glUniform4fARB(IFFFF)", loc, v0, v1, v2, v3);
        } else {
            throw unsupportedShaderObjects();
        }
    }

    public final void glUniform1i(int loc, int value) {
        final ContextCapabilities cap = GL.getCapabilities();

        if (cap.OpenGL20) {
            LOGGER.trace(GL_MARKER, "glUniform1i({}, {})", loc, value);
            GL20.glUniform1i(loc, value);
            assert GL11.glGetError() == 0 : glErrorMsg("glUniform1i(II)", loc, value);
        } else if (cap.GL_ARB_shader_objects) {
            LOGGER.trace(GL_MARKER, "glUniform1iARB({}, {})", loc, value);
            ARBShaderObjects.glUniform1iARB(loc, value);
            assert GL11.glGetError() == 0 : glErrorMsg("glUniform1fARB(IF)", loc, value);
        } else {
            throw unsupportedShaderObjects();
        }
    }

    public final void glUniform2i(int loc, int v0, int v1) {
        final ContextCapabilities cap = GL.getCapabilities();

        if (cap.OpenGL20) {
            LOGGER.trace(GL_MARKER, "glUniform2i({}, {}, {})", loc, v0, v1);
            GL20.glUniform2i(loc, v0, v1);
            assert GL11.glGetError() == 0 : glErrorMsg("glUniform2i(IFF)", loc, v0, v1);
        } else if (cap.GL_ARB_shader_objects) {
            LOGGER.trace(GL_MARKER, "glUniform2iARB({}, {}, {})", loc, v0, v1);
            ARBShaderObjects.glUniform2iARB(loc, v0, v1);
            assert GL11.glGetError() == 0 : glErrorMsg("glUniform2iARB(IFF)", loc, v0, v1);
        } else {
            throw unsupportedShaderObjects();
        }
    }

    public final void glUniform3i(int loc, int v0, int v1, int v2) {
        final ContextCapabilities cap = GL.getCapabilities();

        if (cap.OpenGL20) {
            LOGGER.trace(GL_MARKER, "glUniform3i({}, {}, {}, {})", loc, v0, v1, v2);
            GL20.glUniform3i(loc, v0, v1, v2);
            assert GL11.glGetError() == 0 : glErrorMsg("glUniform3i(IIII)", loc, v0, v1, v2);
        } else if (cap.GL_ARB_shader_objects) {
            LOGGER.trace(GL_MARKER, "glUniform3iARB({}, {}, {}, {})", loc, v0, v1, v2);
            ARBShaderObjects.glUniform3iARB(loc, v0, v1, v2);
            assert GL11.glGetError() == 0 : glErrorMsg("glUniform3iARB(IIII)", loc, v0, v1, v2);
        } else {
            throw unsupportedShaderObjects();
        }
    }

    public final void glUniform4i(int loc, int v0, int v1, int v2, int v3) {
        final ContextCapabilities cap = GL.getCapabilities();

        if (cap.OpenGL20) {
            LOGGER.trace(GL_MARKER, "glUniform4i({}, {}, {}, {}, {})", loc, v0, v1, v2, v3);
            GL20.glUniform4i(loc, v0, v1, v2, v3);
            assert GL11.glGetError() == 0 : glErrorMsg("glUniform4i(IIIII)", loc, v0, v1, v2, v3);
        } else if (cap.GL_ARB_shader_objects) {
            LOGGER.trace(GL_MARKER, "glUniform4iARB({}, {}, {}, [}, {})", loc, v0, v1, v2, v3);
            ARBShaderObjects.glUniform4iARB(loc, v0, v1, v2, v3);
            assert GL11.glGetError() == 0 : glErrorMsg("glUniform4iARB(IIIII)", loc, v0, v1, v2, v3);
        } else {
            throw unsupportedShaderObjects();
        }
    }

    public final void glUniformMatrix2fv(int location, boolean needsTranspose, FloatBuffer data) {
        final ContextCapabilities cap = GL.getCapabilities();

        if (cap.OpenGL20) {
            LOGGER.trace(GL_MARKER, "glUniformMatrix2fv({}, {}, {})", location, needsTranspose, data);
            GL20.glUniformMatrix2fv(location, needsTranspose, data);
            assert GL11.glGetError() == 0 : glErrorMsg("glUniformMatrix2fv(IB*)", location, needsTranspose, data);
        } else if (cap.GL_ARB_shader_objects) {
            LOGGER.trace(GL_MARKER, "glUniformMatrix2fvARB({}, {}, {})", location, needsTranspose, data);
            ARBShaderObjects.glUniformMatrix2fvARB(location, needsTranspose, data);
            assert GL11.glGetError() == 0 : glErrorMsg("glUniformMatrix2fvARB(IB*)", location, needsTranspose, data);
        } else {
            throw unsupportedShaderObjects();
        }
    }

    public final void glUniformMatrix3fv(int location, boolean needsTranspose, FloatBuffer data) {
        final ContextCapabilities cap = GL.getCapabilities();

        if (cap.OpenGL20) {
            LOGGER.trace(GL_MARKER, "glUniformMatrix3fv({}, {}, {})", location, needsTranspose, data);
            GL20.glUniformMatrix3fv(location, needsTranspose, data);
            assert GL11.glGetError() == 0 : glErrorMsg("glUniformMatrix3fv(IB*)", location, needsTranspose, data);
        } else if (cap.GL_ARB_shader_objects) {
            LOGGER.trace(GL_MARKER, "glUniformMatrix3fvARB({}, {}, {})", location, needsTranspose, data);
            ARBShaderObjects.glUniformMatrix3fvARB(location, needsTranspose, data);
            assert GL11.glGetError() == 0 : glErrorMsg("glUniformMatrix3fvARB(IB*)", location, needsTranspose, data);
        } else {
            throw unsupportedShaderObjects();
        }
    }

    public final void glUniformMatrix4fv(int location, boolean needsTranspose, FloatBuffer data) {
        final ContextCapabilities cap = GL.getCapabilities();

        if (cap.OpenGL20) {
            LOGGER.trace(GL_MARKER, "glUniformMatrix4fv({}, {}, {})", location, needsTranspose, data);
            GL20.glUniformMatrix4fv(location, needsTranspose, data);
            assert GL11.glGetError() == 0 : glErrorMsg("glUniformMatrix4fv(IB*)", location, needsTranspose, data);
        } else if (cap.GL_ARB_shader_objects) {
            LOGGER.trace(GL_MARKER, "glUniformMatrix4fvARB({}, {}, {})", location, needsTranspose, data);
            ARBShaderObjects.glUniformMatrix4fvARB(location, needsTranspose, data);
            assert GL11.glGetError() == 0 : glErrorMsg("glUniformMatrix4fvARB(IB*)", location, needsTranspose, data);
        } else {
            throw unsupportedShaderObjects();
        }
    }

    protected final UnsupportedOperationException unsupportedShaderObjects() {
        return new UnsupportedOperationException("Shader objects require either an OpenGL 2.0 context or ARB_shader_objects.");
    }

    protected final UnsupportedOperationException unsupportedFramebufferObject() {
        return new UnsupportedOperationException("Framebuffer objects require either: an OpenGL 3.0 constet, ARB_framebuffer_object, or EXT_framebuffer_object.");
    }

    protected final UnsupportedOperationException unsupportedBufferStorage() {
        return new UnsupportedOperationException("Buffer storage requires either an OpenGL 4.4 context or ARB_buffer_storage.");
    }
    
    protected final UnsupportedOperationException unsupportedGPUShaderFP64() {
        return new UnsupportedOperationException("64bit uniforms require either an OpenGL 4.0 context or ARB_gpu_shader_fp64.");
    }
}
