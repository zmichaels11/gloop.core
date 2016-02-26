/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.longlinkislong.gloop.impl;

import java.nio.ByteBuffer;
import java.nio.DoubleBuffer;
import java.nio.FloatBuffer;
import java.nio.IntBuffer;

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
public interface Driver <BufferT extends Buffer, 
        FramebufferT extends Framebuffer, 
        TextureT extends Texture,
        ShaderT extends Shader,
        ProgramT extends Program,
        SamplerT extends Sampler,
        VertexArrayT extends VertexArray,
        QueryT extends DrawQuery>{

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
}
