/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.longlinkislong.gloop.impl.gl45;

import com.longlinkislong.gloop.GLException;
import com.longlinkislong.gloop.impl.Driver;
import com.longlinkislong.gloop.impl.Shader;
import java.nio.ByteBuffer;
import java.nio.DoubleBuffer;
import java.nio.FloatBuffer;
import java.nio.IntBuffer;
import org.lwjgl.opengl.ARBSparseTexture;
import org.lwjgl.opengl.GL;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL12;
import org.lwjgl.opengl.GL14;
import org.lwjgl.opengl.GL15;
import org.lwjgl.opengl.GL20;
import org.lwjgl.opengl.GL21;
import org.lwjgl.opengl.GL30;
import org.lwjgl.opengl.GL31;
import org.lwjgl.opengl.GL33;
import org.lwjgl.opengl.GL40;
import org.lwjgl.opengl.GL41;
import org.lwjgl.opengl.GL42;
import org.lwjgl.opengl.GL43;
import org.lwjgl.opengl.GL45;
import org.lwjgl.opengl.GLCapabilities;

/**
 *
 * @author zmichaels
 */
public final class GL45Driver implements Driver<
        GL45Buffer, GL45Framebuffer, GL45Texture, GL45Shader, GL45Program, GL45Sampler, GL45VertexArray, GL45DrawQuery> {

    @Override
    public void blendingEnable(long rgbEq, long aEq, long rgbFuncSrc, long rgbFuncDst, long aFuncSrc, long aFuncDst) {
        GL11.glEnable(GL11.GL_BLEND);
        GL14.glBlendFuncSeparate((int) rgbFuncSrc, (int) rgbFuncDst, (int) aFuncSrc, (int) aFuncDst);
        GL20.glBlendEquationSeparate((int) rgbEq, (int) aEq);
    }

    @Override
    public void blendingDisable() {
        GL11.glDisable(GL11.GL_BLEND);
    }

    @Override
    public GL45Buffer bufferCreate() {
        final int id = GL45.glCreateBuffers();
        final GL45Buffer buffer = new GL45Buffer();

        buffer.bufferId = id;
        return buffer;
    }

    @Override
    public long bufferGetParameter(GL45Buffer buffer, long paramId) {
        return GL45.glGetNamedBufferParameteri(buffer.bufferId, (int) paramId);
    }

    @Override
    public void bufferDelete(GL45Buffer buffer) {
        GL15.glDeleteBuffers(buffer.bufferId);
        buffer.bufferId = -1;
    }

    @Override
    public void bufferSetData(GL45Buffer buffer, ByteBuffer data, long usage) {
        GL45.glNamedBufferData(buffer.bufferId, data, (int) usage);
    }

    @Override
    public void bufferAllocateImmutable(GL45Buffer buffer, long size, long bitflags) {
        GL45.glNamedBufferStorage(buffer.bufferId, size, (int) bitflags);
    }

    @Override
    public void bufferAllocate(GL45Buffer buffer, long size, long usage) {
        GL45.glNamedBufferData(buffer.bufferId, size, (int) usage);
    }

    @Override
    public void bufferGetData(GL45Buffer buffer, long offset, ByteBuffer out) {
        GL45.glGetNamedBufferSubData(buffer.bufferId, offset, out);
    }

    @Override
    public ByteBuffer bufferMapData(GL45Buffer buffer, long offset, long length, long accessFlags) {
        buffer.mapBuffer = GL45.glMapNamedBufferRange(buffer.bufferId, offset, length, (int) accessFlags, buffer.mapBuffer);
        return buffer.mapBuffer;
    }

    @Override
    public void bufferUnmapData(GL45Buffer buffer) {
        GL45.glUnmapNamedBuffer(buffer.bufferId);
    }

    @Override
    public void bufferCopyData(GL45Buffer srcBuffer, long srcOffset, GL45Buffer dstBuffer, long dstOffset, long size) {
        GL45.glCopyNamedBufferSubData(srcBuffer.bufferId, dstBuffer.bufferId, srcOffset, dstOffset, size);
    }

    @Override
    public void bufferInvalidateRange(GL45Buffer buffer, long offset, long length) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public void bufferInvalidateData(GL45Buffer buffer) {
        GL43.glInvalidateBufferData(buffer.bufferId);
    }

    @Override
    public void clear(long bitfield, double red, double green, double blue, double alpha, double depth) {
        GL11.glClearColor((float) red, (float) green, (float) blue, (float) alpha);
        GL11.glClearDepth(depth);
        GL11.glClear((int) bitfield);
    }

    @Override
    public void depthTestEnable(long depthTest) {
        GL11.glEnable(GL11.GL_DEPTH_TEST);
        GL11.glDepthFunc((int) depthTest);
    }

    @Override
    public void depthTestDisable() {
        GL11.glDisable(GL11.GL_DEPTH_TEST);
    }

    @Override
    public boolean framebufferIsComplete(GL45Framebuffer framebuffer) {
        final int currentFB = GL11.glGetInteger(GL30.GL_FRAMEBUFFER_BINDING);

        GL30.glBindFramebuffer(GL30.GL_FRAMEBUFFER, framebuffer.framebufferId);
        final int complete = GL30.glCheckFramebufferStatus(GL30.GL_FRAMEBUFFER);
        final boolean res = complete == GL30.GL_FRAMEBUFFER_COMPLETE;

        GL30.glBindFramebuffer(GL30.GL_FRAMEBUFFER, currentFB);

        return res;
    }

    @Override
    public GL45Framebuffer framebufferGetDefault() {
        final GL45Framebuffer fb = new GL45Framebuffer();
        fb.framebufferId = 0;
        return fb;
    }

    @Override
    public GL45Framebuffer framebufferCreate() {
        final int fbId = GL45.glCreateFramebuffers();
        final GL45Framebuffer framebuffer = new GL45Framebuffer();

        framebuffer.framebufferId = fbId;
        return framebuffer;
    }

    @Override
    public void framebufferDelete(GL45Framebuffer framebuffer) {
        GL30.glDeleteFramebuffers(framebuffer.framebufferId);
        framebuffer.framebufferId = -1;
    }

    @Override
    public void framebufferBind(GL45Framebuffer framebuffer, IntBuffer attachments) {

        GL30.glBindFramebuffer(GL30.GL_FRAMEBUFFER, framebuffer.framebufferId);

        if (attachments != null) {
            GL20.glDrawBuffers(attachments);
        }
    }

    @Override
    public void framebufferAddDepthStencilAttachment(GL45Framebuffer framebuffer, GL45Texture texture, long mipmapLevel) {
        GL45.glNamedFramebufferTexture(
                framebuffer.framebufferId,
                GL30.GL_DEPTH_STENCIL_ATTACHMENT,
                texture.textureId,
                (int) mipmapLevel);
    }

    @Override
    public void framebufferAddDepthAttachment(GL45Framebuffer framebuffer, GL45Texture texture, long mipmapLevel) {
        GL45.glNamedFramebufferTexture(
                framebuffer.framebufferId,
                GL30.GL_DEPTH_ATTACHMENT,
                texture.textureId,
                (int) mipmapLevel);
    }

    @Override
    public void framebufferAddAttachment(GL45Framebuffer framebuffer, long attachmentId, GL45Texture texture, long mipmapLevel) {
        GL45.glNamedFramebufferTexture(
                framebuffer.framebufferId,
                (int) attachmentId,
                texture.textureId,
                (int) mipmapLevel);
    }

    @Override
    public void framebufferBlit(GL45Framebuffer srcFb, long srcX0, long srcY0, long srcX1, long srcY1, GL45Framebuffer dstFb, long dstX0, long dstY0, long dstX1, long dstY1, long bitfield, long filter) {
        GL45.glBlitNamedFramebuffer(
                srcFb.framebufferId,
                dstFb.framebufferId,
                (int) srcX0, (int) srcY0, (int) srcX1, (int) srcY1,
                (int) dstX0, (int) dstY0, (int) dstX1, (int) dstY1,
                (int) bitfield, (int) filter);
    }

    @Override
    public void framebufferGetPixels(GL45Framebuffer framebuffer, long x, long y, long width, long height, long format, long type, GL45Buffer dstBuffer) {
        final int currentFB = GL11.glGetInteger(GL30.GL_FRAMEBUFFER);
        final int currentBuffer = GL11.glGetInteger(GL21.GL_PIXEL_PACK_BUFFER_BINDING);

        GL30.glBindFramebuffer(GL30.GL_FRAMEBUFFER, framebuffer.framebufferId);

        GL15.glBindBuffer(GL21.GL_PIXEL_PACK_BUFFER, dstBuffer.bufferId);
        GL11.glReadPixels(
                (int) x, (int) y, (int) width, (int) height,
                (int) format, (int) type,
                0L);
        GL15.glBindBuffer(GL21.GL_PIXEL_PACK_BUFFER, currentBuffer);

        GL30.glBindFramebuffer(GL30.GL_FRAMEBUFFER, currentFB);
    }

    @Override
    public void framebufferGetPixels(GL45Framebuffer framebuffer, long x, long y, long width, long height, long format, long type, ByteBuffer dstBuffer) {
        final int currentFB = GL11.glGetInteger(GL30.GL_FRAMEBUFFER);

        GL30.glBindFramebuffer(GL30.GL_FRAMEBUFFER, framebuffer.framebufferId);

        GL11.glReadPixels(
                (int) x, (int) y, (int) width, (int) height,
                (int) format, (int) type,
                dstBuffer);

        GL30.glBindFramebuffer(GL30.GL_FRAMEBUFFER, currentFB);
    }

    @Override
    public void polygonSetParameters(double pointSize, double lineWidth, long frontFace, long cullFace, long polygonMode, double offsetFactor, double offsetUnits) {
        GL11.glPointSize((float) pointSize);
        GL11.glLineWidth((float) lineWidth);
        GL11.glFrontFace((int) frontFace);

        if (cullFace == 0) {
            GL11.glDisable(GL11.GL_CULL_FACE);
        } else {
            GL11.glEnable(GL11.GL_CULL_FACE);
            GL11.glCullFace((int) cullFace);
        }

        GL11.glPolygonMode(GL11.GL_FRONT_AND_BACK, (int) polygonMode);
        GL11.glPolygonOffset((float) offsetFactor, (float) offsetUnits);
    }

    @Override
    public void maskApply(boolean red, boolean green, boolean blue, boolean alpha, boolean depth, long stencil) {
        GL11.glColorMask(red, green, blue, alpha);
        GL11.glDepthMask(depth);
        GL11.glStencilMask((int) stencil);
    }

    @Override
    public void programUse(GL45Program program) {
        GL20.glUseProgram(program.programId);
    }

    @Override
    public void programSetAttribLocation(GL45Program program, long index, String name) {
        GL20.glBindAttribLocation(program.programId, (int) index, name);
    }

    @Override
    public void programSetFeedbackVaryings(GL45Program program, String[] varyings) {
        GL30.glTransformFeedbackVaryings(program.programId, varyings, GL30.GL_SEPARATE_ATTRIBS);
    }

    @Override
    public void programSetUniformMatD(GL45Program program, long uLoc, DoubleBuffer mat) {
        switch (mat.limit()) {
            case 4:
                GL41.glProgramUniformMatrix2dv(program.programId, (int) uLoc, false, mat);
                break;
            case 9:
                GL41.glProgramUniformMatrix3dv(program.programId, (int) uLoc, false, mat);
                break;
            case 16:
                GL41.glProgramUniformMatrix4dv(program.programId, (int) uLoc, false, mat);
                break;
            default:
                throw new UnsupportedOperationException("Unsupported matrix size: " + mat.limit());
        }
    }

    @Override
    public void programSetUniformMatF(GL45Program program, long uLoc, FloatBuffer mat) {        
        switch (mat.limit()) {
            case 4:
                GL41.glProgramUniformMatrix2fv(program.programId, (int) uLoc, false, mat);
                break;
            case 9:
                GL41.glProgramUniformMatrix3fv(program.programId, (int) uLoc, false, mat);
                break;
            case 16:
                GL41.glProgramUniformMatrix4fv(program.programId, (int) uLoc, false, mat);
                break;
            default:
                throw new UnsupportedOperationException("Unsupported matrix size: " + mat.limit());
        }
    }

    @Override
    public void programSetUniformD(GL45Program program, long uLoc, double[] value) {
        switch (value.length) {
            case 1:
                GL41.glProgramUniform1d(program.programId, (int) uLoc, value[0]);
                break;
            case 2:
                GL41.glProgramUniform2d(program.programId, (int) uLoc, value[0], value[1]);
                break;
            case 3:
                GL41.glProgramUniform3d(program.programId, (int) uLoc, value[0], value[1], value[2]);
                break;
            case 4:
                GL41.glProgramUniform4d(program.programId, (int) uLoc, value[0], value[1], value[2], value[3]);
                break;
            default:
                throw new UnsupportedOperationException("Unsupported uniform vector size: " + value.length);
        }
    }

    @Override
    public void programSetUniformF(GL45Program program, long uLoc, float[] value) {
        switch (value.length) {
            case 1:
                GL41.glProgramUniform1f(program.programId, (int) uLoc, value[0]);
                break;
            case 2:
                GL41.glProgramUniform2f(program.programId, (int) uLoc, value[0], value[1]);
                break;
            case 3:
                GL41.glProgramUniform3f(program.programId, (int) uLoc, value[0], value[1], value[2]);
                break;
            case 4:
                GL41.glProgramUniform4f(program.programId, (int) uLoc, value[0], value[1], value[2], value[3]);
                break;
            default:
                throw new UnsupportedOperationException("Unsupported uniform vector size: " + value.length);
        }
    }

    @Override
    public void programSetUniformI(GL45Program program, long uLoc, int[] value) {
        switch (value.length) {
            case 1:
                GL41.glProgramUniform1i(program.programId, (int) uLoc, value[0]);
                break;
            case 2:
                GL41.glProgramUniform2i(program.programId, (int) uLoc, value[0], value[1]);
                break;
            case 3:
                GL41.glProgramUniform3i(program.programId, (int) uLoc, value[0], value[1], value[2]);
                break;
            case 4:
                GL41.glProgramUniform4i(program.programId, (int) uLoc, value[0], value[1], value[2], value[3]);
                break;
            default:
                throw new UnsupportedOperationException("Unsupported uniform vector size: " + value.length);
        }
    }

    @Override
    public void programLinkShaders(GL45Program program, Shader[] shaders) {
        for (Shader shader : shaders) {
            GL20.glAttachShader(program.programId, ((GL45Shader) shader).shaderId);
        }

        GL20.glLinkProgram(program.programId);

        for (Shader shader : shaders) {
            GL20.glDetachShader(program.programId, ((GL45Shader) shader).shaderId);
        }
    }

    @Override
    public GL45Program programCreate() {
        final GL45Program program = new GL45Program();

        program.programId = GL20.glCreateProgram();
        return program;
    }

    @Override
    public void programDelete(GL45Program program) {
        GL20.glDeleteProgram(program.programId);
        program.programId = -1;
    }

    @Override
    public void programSetStorage(GL45Program program, String storageName, GL45Buffer buffer, long bindingPoint) {
        final int sBlock = GL43.glGetProgramResourceLocation(program.programId, GL43.GL_SHADER_STORAGE_BLOCK, storageName);

        GL30.glBindBufferBase(GL43.GL_SHADER_STORAGE_BUFFER, (int) bindingPoint, buffer.bufferId);
        GL43.glShaderStorageBlockBinding(program.programId, sBlock, (int) bindingPoint);
    }

    @Override
    public void programSetUniformBlock(GL45Program program, String uniformName, GL45Buffer buffer, long bindingPoint) {
        final int uBlock = GL31.glGetUniformBlockIndex(program.programId, uniformName);

        GL30.glBindBufferBase(GL31.GL_UNIFORM_BUFFER, (int) bindingPoint, buffer.bufferId);
        GL31.glUniformBlockBinding(program.programId, uBlock, (int) bindingPoint);
    }

    @Override
    public void programDispatchCompute(GL45Program program, long numX, long numY, long numZ) {
        final int currentProgram = GL11.glGetInteger(GL20.GL_CURRENT_PROGRAM);

        GL20.glUseProgram(program.programId);
        GL43.glDispatchCompute((int) numX, (int) numY, (int) numZ);
        GL20.glUseProgram(currentProgram);
    }

    @Override
    public void programSetFeedbackBuffer(GL45Program program, long varyingLoc, GL45Buffer buffer) {
        GL30.glBindBufferBase(GL30.GL_TRANSFORM_FEEDBACK_BUFFER, (int) varyingLoc, buffer.bufferId);
    }

    @Override
    public GL45Sampler samplerCreate() {
        final GL45Sampler sampler = new GL45Sampler();
        sampler.samplerId = GL45.glCreateSamplers();
        return sampler;
    }

    @Override
    public void samplerSetParameter(GL45Sampler sampler, long param, long value) {
        GL33.glSamplerParameteri(sampler.samplerId, (int) param, (int) value);
    }

    @Override
    public void samplerDelete(GL45Sampler sampler) {
        GL33.glDeleteSamplers(sampler.samplerId);
        sampler.samplerId = -1;
    }

    @Override
    public void samplerBind(long unit, GL45Sampler sampler) {
        GL33.glBindSampler((int) unit, sampler.samplerId);
    }

    @Override
    public void scissorTestEnable(long left, long bottom, long width, long height) {
        GL11.glEnable(GL11.GL_SCISSOR_TEST);
        GL11.glScissor((int) left, (int) bottom, (int) width, (int) height);
    }

    @Override
    public void scissorTestDisable() {
        GL11.glDisable(GL11.GL_SCISSOR_TEST);
    }

    @Override
    public GL45Shader shaderCompile(long type, String source) {
        final GL45Shader shader = new GL45Shader();

        shader.shaderId = GL20.glCreateShader((int) type);
        GL20.glShaderSource(shader.shaderId, source);
        GL20.glCompileShader(shader.shaderId);

        return shader;
    }

    @Override
    public String shaderGetInfoLog(GL45Shader shader) {
        return GL20.glGetShaderInfoLog(shader.shaderId);
    }

    @Override
    public long shaderGetParameter(GL45Shader shader, long pName) {
        return GL20.glGetShaderi(shader.shaderId, (int) pName);
    }

    @Override
    public void shaderDelete(GL45Shader shader) {
        GL20.glDeleteShader(shader.shaderId);
        shader.shaderId = -1;
    }

    @Override
    public GL45Texture textureAllocate(long mipmaps, long internalFormat, long width, long height, long depth) {
        final int target;

        if (width < 1 || height < 1 || depth < 1) {
            throw new GLException("Invalid dimensions!");
        } else if (width >= 1 && height == 1 && depth == 1) {
            target = GL11.GL_TEXTURE_1D;
        } else if (width >= 1 && height > 1 && depth == 1) {
            target = GL11.GL_TEXTURE_2D;
        } else if (width >= 1 && height >= 1 && depth > 1) {
            target = GL12.GL_TEXTURE_3D;
        } else {
            throw new GLException("Invalid dimensions!");
        }

        final GL45Texture texture = new GL45Texture();

        texture.textureId = GL45.glCreateTextures(target);
        texture.target = target;
        texture.internalFormat = (int) internalFormat;

        switch (target) {
            case GL11.GL_TEXTURE_1D:
                GL45.glTextureStorage1D(texture.textureId, (int) mipmaps, (int) internalFormat, (int) width);
                break;
            case GL11.GL_TEXTURE_2D:
                GL45.glTextureStorage2D(texture.textureId, (int) mipmaps, (int) internalFormat, (int) width, (int) height);
                break;
            case GL12.GL_TEXTURE_3D:
                GL45.glTextureStorage3D(texture.textureId, (int) mipmaps, (int) internalFormat, (int) width, (int) height, (int) depth);
                break;
        }

        return texture;
    }

    @Override
    public void textureGenerateMipmap(GL45Texture texture) {
        GL45.glGenerateTextureMipmap(texture.textureId);
    }

    @Override
    public void textureBind(GL45Texture texture, long unit) {
        GL45.glBindTextureUnit((int) unit, texture.textureId);
    }

    @Override
    public void textureDelete(GL45Texture texture) {
        GL11.glDeleteTextures(texture.textureId);
        texture.textureId = -1;
        texture.target = -1;
    }

    @Override
    public void textureSetData(GL45Texture texture, long level, long xOffset, long yOffset, long zOffset, long width, long height, long depth, long format, long type, ByteBuffer data) {
        switch (texture.target) {
            case GL11.GL_TEXTURE_1D:
                GL45.glTextureSubImage1D(texture.textureId, (int) level, (int) xOffset, (int) width, (int) format, (int) type, data);
                break;
            case GL11.GL_TEXTURE_2D:
                GL45.glTextureSubImage2D(texture.textureId, (int) level, (int) xOffset, (int) yOffset, (int) width, (int) height, (int) format, (int) type, data);
                break;
            case GL12.GL_TEXTURE_3D:
                GL45.glTextureSubImage3D(texture.textureId, (int) level, (int) xOffset, (int) yOffset, (int) zOffset, (int) width, (int) height, (int) depth, (int) format, (int) type, data);
                break;
            default:
                throw new UnsupportedOperationException("Unsupported texture type: " + texture.target);
        }
    }

    @Override
    public void textureGetData(GL45Texture texture, long level, long format, long type, ByteBuffer out) {
        GL45.glGetTextureImage(texture.target, (int) level, (int) format, (int) type, out);
    }

    @Override
    public void textureInvalidateData(GL45Texture texture, long level) {
        GL43.glInvalidateTexImage(texture.textureId, (int) level);
    }

    @Override
    public void textureInvalidateRange(GL45Texture texture, long level, long xOffset, long yOffset, long zOffset, long width, long height, long depth) {
        GL43.glInvalidateTexSubImage(texture.textureId, (int) level, (int) xOffset, (int) yOffset, (int) zOffset, (int) width, (int) height, (int) depth);
    }

    @Override
    public long textureGetMaxSize() {
        return GL11.glGetInteger(GL11.GL_MAX_TEXTURE_SIZE);
    }

    @Override
    public long textureGetMaxBoundTextures() {
        return GL11.glGetInteger(GL20.GL_MAX_TEXTURE_IMAGE_UNITS);
    }

    @Override
    public long textureGetPreferredFormat(long internalFormat) {
        return GL11.GL_RGBA;
    }

    @Override
    public void textureSetParameter(GL45Texture texture, long param, long value) {
        GL45.glTextureParameteri(texture.textureId, (int) param, (int) value);
    }

    @Override
    public void textureSetParameter(GL45Texture texture, long param, double value) {
        GL45.glTextureParameterf(texture.textureId, (int) param, (float) value);
    }

    @Override
    public void textureAllocatePage(GL45Texture texture, long level, long xOffset, long yOffset, long zOffset, long width, long height, long depth) {
        final GLCapabilities cap = GL.getCapabilities();

        if (cap.GL_ARB_sparse_texture) {
            ARBSparseTexture.glTexPageCommitmentARB(
                    texture.textureId, (int) level,
                    (int) xOffset, (int) yOffset, (int) zOffset,
                    (int) width, (int) height, (int) depth,
                    true);
        } else {
            //TODO: support AMD extension
            throw new UnsupportedOperationException("ARB_sparse_texture is required!");
        }
    }

    @Override
    public void textureDeallocatePage(GL45Texture texture, long level, long xOffset, long yOffset, long zOffset, long width, long height, long depth) {
        final GLCapabilities cap = GL.getCapabilities();

        if (cap.GL_ARB_sparse_texture) {
            ARBSparseTexture.glTexPageCommitmentARB(
                    texture.textureId, (int) level,
                    (int) xOffset, (int) yOffset, (int) zOffset,
                    (int) width, (int) height, (int) depth,
                    false);
        } else {
            throw new UnsupportedOperationException("ARB_sparse_texture is required!");
        }
    }

    @Override
    public long textureGetPageWidth(GL45Texture texture) {
        return GL42.glGetInternalformati(texture.target, texture.internalFormat, ARBSparseTexture.GL_VIRTUAL_PAGE_SIZE_X_ARB);
    }

    @Override
    public long textureGetPageHeight(GL45Texture texture) {
        return GL42.glGetInternalformati(texture.target, texture.internalFormat, ARBSparseTexture.GL_VIRTUAL_PAGE_SIZE_Y_ARB);
    }

    @Override
    public long textureGetPageDepth(GL45Texture texture) {
        return GL42.glGetInternalformati(texture.target, texture.internalFormat, ARBSparseTexture.GL_VIRTUAL_PAGE_SIZE_Z_ARB);
    }

    @Override
    public GL45VertexArray vertexArrayCreate() {
        final GL45VertexArray vao = new GL45VertexArray();
        vao.vertexArrayId = GL30.glGenVertexArrays();
        return vao;
    }

    @Override
    public void vertexArrayDrawElementsIndirect(GL45VertexArray vao, GL45Buffer cmdBuffer, long drawMode, long indexType, long offset) {
        final int currentVao = GL11.glGetInteger(GL30.GL_VERTEX_ARRAY_BINDING);
        final int currentIndirect = GL11.glGetInteger(GL40.GL_DRAW_INDIRECT_BUFFER_BINDING);

        GL30.glBindVertexArray(vao.vertexArrayId);
        GL15.glBindBuffer(GL40.GL_DRAW_INDIRECT_BUFFER, cmdBuffer.bufferId);
        GL40.glDrawElementsIndirect((int) drawMode, (int) indexType, offset);
        GL15.glBindBuffer(GL40.GL_DRAW_INDIRECT_BUFFER, currentIndirect);
        GL30.glBindVertexArray(currentVao);
    }

    @Override
    public void vertexArrayDrawArraysIndirect(GL45VertexArray vao, GL45Buffer cmdBuffer, long drawMode, long offset) {
        final int currentVao = GL11.glGetInteger(GL30.GL_VERTEX_ARRAY_BINDING);
        final int currentIndirect = GL11.glGetInteger(GL40.GL_DRAW_INDIRECT_BUFFER);

        GL30.glBindVertexArray(vao.vertexArrayId);
        GL15.glBindBuffer(GL40.GL_DRAW_INDIRECT_BUFFER, cmdBuffer.bufferId);
        GL40.glDrawArraysIndirect((int) drawMode, offset);
        GL15.glBindBuffer(GL40.GL_DRAW_INDIRECT_BUFFER, currentIndirect);
        GL30.glBindVertexArray(currentVao);
    }

    @Override
    public void vertexArrayMultiDrawArrays(GL45VertexArray vao, long drawMode, IntBuffer first, IntBuffer count) {
        final int currentVao = GL11.glGetInteger(GL30.GL_VERTEX_ARRAY_BINDING);

        GL30.glBindVertexArray(vao.vertexArrayId);
        GL14.glMultiDrawArrays((int) drawMode, first, count);
        GL30.glBindVertexArray(currentVao);
    }

    @Override
    public void vertexArrayDrawElementsInstanced(GL45VertexArray vao, long drawMode, long count, long type, long offset, long instanceCount) {
        final int currentVao = GL11.glGetInteger(GL30.GL_VERTEX_ARRAY_BINDING);

        GL30.glBindVertexArray(vao.vertexArrayId);
        GL31.glDrawElementsInstanced((int) drawMode, (int) count, (int) type, offset, (int) instanceCount);
        GL30.glBindVertexArray(currentVao);
    }

    @Override
    public void vertexArrayDrawArraysInstanced(GL45VertexArray vao, long drawMode, long first, long count, long instanceCount) {
        final int currentVao = GL11.glGetInteger(GL30.GL_VERTEX_ARRAY_BINDING);

        GL30.glBindVertexArray(vao.vertexArrayId);
        GL31.glDrawArraysInstanced((int) drawMode, (int) first, (int) count, (int) instanceCount);
        GL30.glBindVertexArray(currentVao);
    }

    @Override
    public void vertexArrayDrawElements(GL45VertexArray vao, long drawMode, long count, long type, long offset) {
        final int currentVao = GL11.glGetInteger(GL30.GL_VERTEX_ARRAY_BINDING);

        GL30.glBindVertexArray(vao.vertexArrayId);
        GL11.glDrawElements((int) drawMode, (int) count, (int) type, offset);
        GL30.glBindVertexArray(currentVao);
    }

    @Override
    public void vertexArrayDrawArrays(GL45VertexArray vao, long drawMode, long start, long count) {
        final int currentVao = GL11.glGetInteger(GL30.GL_VERTEX_ARRAY_BINDING);
        GL30.glBindVertexArray(vao.vertexArrayId);
        GL11.glDrawArrays((int) drawMode, (int) start, (int) count);
        GL30.glBindVertexArray(currentVao);
    }

    @Override
    public void vertexArrayDrawTransformFeedback(GL45VertexArray vao, long drawMode, long start, long count) {
        final int currentVao = GL11.glGetInteger(GL30.GL_VERTEX_ARRAY_BINDING);

        GL30.glBindVertexArray(vao.vertexArrayId);
        GL11.glEnable(GL30.GL_RASTERIZER_DISCARD);
        GL30.glBeginTransformFeedback((int) drawMode);
        GL11.glDrawArrays((int) drawMode, (int) start, (int) count);
        GL30.glEndTransformFeedback();
        GL11.glDisable(GL30.GL_RASTERIZER_DISCARD);
        GL30.glBindVertexArray(currentVao);
    }

    @Override
    public void vertexArrayDelete(GL45VertexArray vao) {
        GL30.glDeleteVertexArrays(vao.vertexArrayId);
        vao.vertexArrayId = -1;
    }

    @Override
    public void vertexArrayAttachIndexBuffer(GL45VertexArray vao, GL45Buffer buffer) {
        final int currentVao = GL11.glGetInteger(GL30.GL_VERTEX_ARRAY_BINDING);

        if (currentVao != vao.vertexArrayId) {
            //TODO: can this be stateless?
            GL30.glBindVertexArray(vao.vertexArrayId);
        }

        GL15.glBindBuffer(GL15.GL_ELEMENT_ARRAY_BUFFER, buffer.bufferId);
    }

    @Override
    public void vertexArrayAttachBuffer(GL45VertexArray vao, long index, GL45Buffer buffer, long size, long type, long stride, long offset, long divisor) {

        GL30.glBindVertexArray(vao.vertexArrayId);

        GL15.glBindBuffer(GL15.GL_ARRAY_BUFFER, buffer.bufferId);
        GL20.glEnableVertexAttribArray((int) index);

        if (type == GL11.GL_DOUBLE) {
            GL41.glVertexAttribLPointer((int) index, (int) size, (int) type, (int) stride, offset);
        } else {
            GL20.glVertexAttribPointer((int) index, (int) size, (int) type, false, (int) stride, offset);
        }

        if (divisor > 0) {
            GL33.glVertexAttribDivisor((int) index, (int) divisor);
        }
    }

    @Override
    public void viewportApply(long x, long y, long width, long height) {
        GL11.glViewport((int) x, (int) y, (int) width, (int) height);
    }

    @Override
    public void drawQueryEnable(long condition, GL45DrawQuery query) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public void drawQueryDisable(long condition) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public void drawQueryBeginConditionalRender(GL45DrawQuery query, long mode) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public void drawQueryEndConditionRender() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public GL45DrawQuery drawQueryCreate() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public void drawQueryDelete(GL45DrawQuery query) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    private GL45Driver() {
    }

    @Override
    public long programGetUniformLocation(GL45Program program, String name) {
        final int currentProgram = GL11.glGetInteger(GL20.GL_CURRENT_PROGRAM);

        GL20.glUseProgram(program.programId);
        final int res = GL20.glGetUniformLocation(program.programId, name);
        GL20.glUseProgram(currentProgram);        
        return res;
    }

    @Override
    public void samplerSetParameter(GL45Sampler sampler, long param, double value) {
        GL33.glSamplerParameterf(sampler.samplerId, (int) param, (float) value);
    }

    @Override
    public long textureGetMaxAnisotropy() {
        return GL11.glGetInteger(org.lwjgl.opengl.EXTTextureFilterAnisotropic.GL_MAX_TEXTURE_MAX_ANISOTROPY_EXT);
    }

    private static final class Holder {

        private static final GL45Driver INSTANCE = new GL45Driver();
    }

    public static GL45Driver getInstance() {
        return Holder.INSTANCE;
    }
}
