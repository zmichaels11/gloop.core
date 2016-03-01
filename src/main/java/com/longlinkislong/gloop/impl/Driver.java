/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.longlinkislong.gloop.impl;

import com.longlinkislong.gloop.GLTextureInternalFormat;
import java.nio.ByteBuffer;
import java.nio.DoubleBuffer;
import java.nio.FloatBuffer;
import java.nio.IntBuffer;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL30;

/**
 *
 * @author zmichaels
 * @param <BufferT>
 * @param <FramebufferT>
 * @param <TextureT>
 * @param <ShaderT>
 * @param <ProgramT>
 * @param <SamplerT>
 * @param <VertexArrayT>
 * @param <QueryT>
 */
public interface Driver<BufferT extends Buffer, FramebufferT extends Framebuffer, TextureT extends Texture, ShaderT extends Shader, ProgramT extends Program, SamplerT extends Sampler, VertexArrayT extends VertexArray, QueryT extends DrawQuery> {

    boolean isBufferSupported();

    boolean isImmutableBufferSupported();

    boolean isDrawQuerySupported();

    boolean isFramebufferSupported();

    boolean isProgramSupported();

    boolean isSamplerSupported();

    boolean isComputeShaderSupported();

    boolean isSparseTextureSupported();

    boolean isDrawIndirectSupported();

    boolean isDrawInstancedSupported();

    boolean isInvalidateSubdataSupported();

    boolean isSeparateShaderObjectsSupported();

    boolean is64bitUniformsSupported();

    boolean isVertexArraySupported();

    boolean isSupported();

    default double getSupportRating() {
        double rating = 0.0;
        rating += isBufferSupported() ? 1.0 : 0.0;
        rating += isImmutableBufferSupported() ? 1.0 : 0.0;
        rating += isDrawQuerySupported() ? 1.0 : 0.0;
        rating += isFramebufferSupported() ? 1.0 : 0.0;
        rating += isProgramSupported() ? 1.0 : 0.0;
        rating += isSamplerSupported() ? 1.0 : 0.0;
        rating += isComputeShaderSupported() ? 1.0 : 0.0;
        rating += isSparseTextureSupported() ? 1.0 : 0.0;
        rating += isDrawIndirectSupported() ? 1.0 : 0.0;
        rating += isDrawInstancedSupported() ? 1.0 : 0.0;
        rating += isInvalidateSubdataSupported() ? 1.0 : 0.0;
        rating += isSeparateShaderObjectsSupported() ? 1.0 : 0.0;
        rating += is64bitUniformsSupported() ? 1.0 : 0.0;
        rating += isVertexArraySupported() ? 1.0 : 0.0;
        
        return rating / 14.0;
    }

    // blending
    void blendingEnable(long rgbEq, long aEq, long rgbFuncSrc, long rgbFuncDst, long aFuncSrc, long aFuncDst);

    void blendingDisable();

    // buffer
    BufferT bufferCreate();

    long bufferGetParameter(BufferT buffer, long paramId);

    void bufferDelete(BufferT buffer);

    void bufferSetData(BufferT buffer, ByteBuffer data, long usage);

    void bufferAllocateImmutable(BufferT buffer, long size, long bitflags);

    void bufferAllocate(BufferT buffer, long size, long usage);

    void bufferGetData(BufferT buffer, long offset, ByteBuffer out);

    ByteBuffer bufferMapData(BufferT buffer, long offset, long length, long accessFlags);

    void bufferUnmapData(BufferT buffer);

    void bufferCopyData(BufferT srcBuffer, long srcOffset, BufferT dstBuffer, long dstOffset, long size);

    void bufferInvalidateRange(BufferT buffer, long offset, long length);

    void bufferInvalidateData(BufferT buffer);

    // clear
    void clear(long bitfield, double red, double green, double blue, double alpha, double depth);

    // depth
    void depthTestEnable(long depthTest);

    void depthTestDisable();

    // framebuffer
    FramebufferT framebufferGetDefault();

    boolean framebufferIsComplete(FramebufferT framebuffer);

    FramebufferT framebufferCreate();

    void framebufferDelete(FramebufferT framebuffer);

    void framebufferBind(FramebufferT framebuffer, IntBuffer attachments);

    void framebufferAddDepthStencilAttachment(FramebufferT framebuffer, TextureT texId, long mipmapLevel);

    void framebufferAddDepthAttachment(FramebufferT framebuffer, TextureT texId, long mipmapLevel);

    void framebufferAddAttachment(FramebufferT framebuffer, long attachmentId, TextureT texId, long mipmapLevel);

    void framebufferBlit(FramebufferT srcFb, long srcX0, long srcY0, long srcX1, long srcY1, FramebufferT dstFb, long dstX0, long dstY0, long dstX1, long dstY1, long bitfield, long filter);

    void framebufferGetPixels(FramebufferT framebuffer, long x, long y, long width, long height, long format, long type, BufferT dstBuffer);

    void framebufferGetPixels(FramebufferT framebuffer, long x, long y, long width, long height, long format, long type, ByteBuffer dstBuffer);

    // mask
    void maskApply(boolean red, boolean green, boolean blue, boolean alpha, boolean depth, long stencil);

    // polygon
    void polygonSetParameters(double pointSize, double lineWidth, long frontFace, long cullFrace, long polygonMode, double offsetFactor, double offsetUnits);

    // program
    void programUse(ProgramT program);

    void programSetAttribLocation(ProgramT program, long index, String name);

    void programSetFeedbackVaryings(ProgramT program, String[] varyings);

    void programSetUniformMatD(ProgramT program, long uLoc, DoubleBuffer mat);

    void programSetUniformMatF(ProgramT program, long uLoc, FloatBuffer mat);

    void programSetUniformD(ProgramT program, long uLoc, double[] value);

    void programSetUniformF(ProgramT program, long uLoc, float[] value);

    void programSetUniformI(ProgramT program, long uLoc, int[] value);

    void programLinkShaders(ProgramT program, Shader[] shaders);

    ProgramT programCreate();

    void programDelete(ProgramT program);

    void programSetStorage(ProgramT program, String storageName, BufferT buffer, long bindingPoint);

    void programSetUniformBlock(ProgramT program, String uniformName, BufferT buffer, long bindingPoint);

    void programDispatchCompute(ProgramT program, long numX, long numY, long numZ);

    void programSetFeedbackBuffer(ProgramT program, long varyingLoc, BufferT buffer);

    long programGetUniformLocation(ProgramT program, String name);

    //sampler
    SamplerT samplerCreate();

    void samplerSetParameter(SamplerT sampler, long param, long value);

    void samplerSetParameter(SamplerT sampler, long param, double value);

    void samplerDelete(SamplerT sampler);

    void samplerBind(long unit, SamplerT sampler);

    // scissor test
    void scissorTestEnable(long left, long bottom, long width, long height);

    void scissorTestDisable();

    //shader
    ShaderT shaderCompile(long type, String source);

    String shaderGetInfoLog(ShaderT shader);

    long shaderGetParameter(ShaderT shader, long pName);

    void shaderDelete(ShaderT shader);

    //texture
    TextureT textureAllocate(long mipmaps, long internalFormat, long width, long height, long depth);

    void textureBind(TextureT texture, long unit);

    void textureDelete(TextureT texture);

    void textureSetData(TextureT texture, long level, long xOffset, long yOffset, long zOffset, long width, long height, long depth, long format, long type, ByteBuffer data);

    void textureGetData(TextureT texture, long level, long format, long type, ByteBuffer out);

    void textureInvalidateData(TextureT texture, long level);

    void textureInvalidateRange(TextureT texture, long level, long xOffset, long yOffset, long zOffset, long width, long height, long depth);

    void textureGenerateMipmap(TextureT texture);

    long textureGetMaxSize();

    long textureGetMaxBoundTextures();

    long textureGetPageWidth(TextureT texture);

    long textureGetPageHeight(TextureT texture);

    long textureGetPageDepth(TextureT texture);

    long textureGetPreferredFormat(long internalFormat);

    void textureSetParameter(TextureT texture, long param, long value);

    void textureSetParameter(TextureT texture, long param, double value);

    void textureAllocatePage(TextureT texture, long level, long xOffset, long yOffset, long zOffset, long width, long height, long depth);

    void textureDeallocatePage(TextureT texture, long level, long xOffset, long yOffset, long zOffset, long width, long height, long depth);

    long textureGetMaxAnisotropy();

    // vertexArray
    VertexArrayT vertexArrayCreate();

    void vertexArrayDrawElementsIndirect(VertexArrayT vao, BufferT cmdBuffer, long drawMode, long indexType, long offset);

    void vertexArrayDrawArraysIndirect(VertexArrayT vao, BufferT cmdBuffer, long drawMode, long offset);

    void vertexArrayMultiDrawArrays(VertexArrayT vao, long drawMode, IntBuffer first, IntBuffer count);

    void vertexArrayDrawElementsInstanced(VertexArrayT vao, long drawMode, long count, long type, long offset, long instanceCount);

    void vertexArrayDrawArraysInstanced(VertexArrayT vao, long drawMode, long first, long count, long instanceCount);

    void vertexArrayDrawElements(VertexArrayT vao, long drawMode, long count, long type, long offset);

    void vertexArrayDrawArrays(VertexArrayT vao, long drawMode, long start, long count);

    void vertexArrayDrawTransformFeedback(VertexArrayT vao, long drawMode, long start, long count);

    void vertexArrayDelete(VertexArrayT vao);

    void vertexArrayAttachIndexBuffer(VertexArrayT vao, BufferT buffer);

    void vertexArrayAttachBuffer(VertexArrayT vao, long index, BufferT buffer, long size, long type, long stride, long offset, long divisor);

    //viewport
    void viewportApply(long x, long y, long width, long height);

    // draw query
    void drawQueryEnable(long condition, QueryT query);

    void drawQueryDisable(long condition);

    void drawQueryBeginConditionalRender(QueryT query, long mode);

    void drawQueryEndConditionRender();

    QueryT drawQueryCreate();

    void drawQueryDelete(QueryT query);

    default int guessFormat(int internalFormat) {
        switch (GLTextureInternalFormat.of(internalFormat).orElseThrow(NullPointerException::new)) {
            case GL_COMPRESSED_RGB_S3TC_DXT1:
            case GL_RGB:
            case GL_COMPRESSED_RGB:
            case GL_COMPRESSED_SRGB:
            case GL_R3_G3_B2:
            case GL_RGB4:
            case GL_RGB5:
            case GL_RGB8:
            case GL_RGB8_SNORM:
            case GL_RGB10:
            case GL_RGB12:
            case GL_RGB16:
            case GL_RGB16_SNORM:
            case GL_SRGB8:
            case GL_RGB16F:
            case GL_RGB32F:
            case GL_RGB8I:
            case GL_RGB8UI:
            case GL_RGB16I:
            case GL_RGB16UI:
            case GL_RGB32I:
            case GL_RGB32UI:
                return GL11.GL_RGB;
            case GL_COMPRESSED_RGBA_S3TC_DXT1:
            case GL_COMPRESSED_RGBA_S3TC_DXT3:
            case GL_COMPRESSED_RGBA_S3TC_DXT5:
            case GL_RGBA:
            case GL_COMPRESSED_RGBA:
            case GL_COMPRESSED_SRGB_ALPHA:
            case GL_RGBA2:
            case GL_RGBA4:
            case GL_RGBA16_SNORM:
            case GL_RGB5_A1:
            case GL_RGBA8:
            case GL_RGBA8_SNORM:
            case GL_RGB10_A2:
            case GL_RGB10_A2UI:
            case GL_RGBA12:
            case GL_RGBA16:
            case GL_SRGB8_ALPHA8:
            case GL_RGBA16F:
            case GL_RGBA32F:
            case GL_R11F_G11F_B10F:
            case GL_RGBA8I:
            case GL_RGBA8UI:
            case GL_RGBA16I:
            case GL_RGBA16UI:
            case GL_RGBA32I:
            case GL_RGBA32UI:
                return GL11.GL_RGBA;
            case GL_STENCIL_INDEX:
                return GL11.GL_STENCIL;
            case GL_RED:
            case GL_R8:
            case GL_R8_SNORM:
            case GL_R16_SNORM:
            case GL_COMPRESSED_RED:
            case GL_R16:
            case GL_R16F:
            case GL_R32F:
            case GL_R8I:
            case GL_R8UI:
            case GL_R16I:
            case GL_R16UI:
            case GL_R32I:
            case GL_R32UI:
                return GL11.GL_RED;
            case GL_DEPTH_COMPONENT:
                return GL11.GL_DEPTH_COMPONENT;
            case GL_DEPTH_STENCIL:
            case GL_DEPTH24_STENCIL8:
            case GL_DEPTH32F_STENCIL8:
                return GL30.GL_DEPTH_STENCIL;
            case GL_RG:
            case GL_COMPRESSED_RG:
            case GL_RG8:
            case GL_RG8_SNORM:
            case GL_RG16:
            case GL_RG16_SNORM:
            case GL_RG16F:
            case GL_RG32F:
            case GL_RG8I:
            case GL_RG8UI:
            case GL_RG16I:
            case GL_RG16UI:
            case GL_RG32I:
            case GL_RG32UI:
                return GL30.GL_RG;
            case GL_DEPTH_COMPONENT16:
            case GL_DEPTH_COMPONENT24:
            case GL_DEPTH_COMPONENT32:
            case GL_DEPTH_COMPONENT32F:
                return GL11.GL_DEPTH_COMPONENT;
            default:
                return GL11.GL_RGBA;
        }
    }
}
