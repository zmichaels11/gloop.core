/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.longlinkislong.gloop.impl.vk10;

import com.longlinkislong.gloop.GLException;
import com.longlinkislong.gloop.impl.Driver;
import com.longlinkislong.gloop.impl.Shader;
import java.nio.ByteBuffer;
import java.nio.DoubleBuffer;
import java.nio.FloatBuffer;
import java.nio.IntBuffer;
import org.lwjgl.PointerBuffer;
import org.lwjgl.system.MemoryUtil;
import org.lwjgl.vulkan.VK10;
import org.lwjgl.vulkan.VkDevice;

/**
 *
 * @author zmichaels
 */
public final class VK10Driver implements Driver<
        VK10Buffer, VK10Framebuffer, VK10Texture, VK10Shader, VK10Program, VK10Sampler, VK10VertexArray, VK10DrawQuery> {

    public VkDevice device;
    
    @Override
    public void blendingDisable() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public void blendingEnable(long rgbEq, long aEq, long rgbFuncSrc, long rgbFuncDst, long aFuncSrc, long aFuncDst) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public void bufferAllocate(VK10Buffer buffer, long size, long usage) {
        bufferAllocateImmutable(buffer, size, 0);
    }

    @Override
    public void bufferAllocateImmutable(VK10Buffer buffer, long size, long bitflags) {
        buffer.size = size;
        buffer.state = VK10Buffer.State.CONFIGURED;
    }

    @Override
    public void bufferCopyData(VK10Buffer srcBuffer, long srcOffset, VK10Buffer dstBuffer, long dstOffset, long size) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public VK10Buffer bufferCreate() {
        return new VK10Buffer();
    }

    @Override
    public void bufferDelete(VK10Buffer buffer) {
        VK10.vkDestroyBuffer(device, buffer.pBuffer, null);        
        buffer.state = VK10Buffer.State.DEAD;
    }

    @Override
    public void bufferGetData(VK10Buffer buffer, long offset, ByteBuffer out) {
        final ByteBuffer mapBuffer = this.bufferMapData(buffer, offset, out.capacity(), 0);
        
        out.put(mapBuffer);
        
        this.bufferUnmapData(buffer);
    }

    @Override
    public long bufferGetParameter(VK10Buffer buffer, long paramId) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public void bufferInvalidateData(VK10Buffer buffer) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public void bufferInvalidateRange(VK10Buffer buffer, long offset, long length) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    private void bufferInitialize(VK10Buffer buffer) {
        if(buffer.state == VK10Buffer.State.CONFIGURED) {
            //TODO: initialize the buffer NOW
        }
    }
    
    @Override
    public ByteBuffer bufferMapData(VK10Buffer buffer, long offset, long length, long accessFlags) {
        bufferInitialize(buffer); // call deferred buffer initializer
        
        final PointerBuffer pp = MemoryUtil.memAllocPointer(1);
        
        final int err = VK10.vkMapMemory(device, buffer.pMemory, offset, length, 0, pp);
        
        if(err != VK10.VK_SUCCESS) {
            MemoryUtil.memFree(pp);
            throw new GLException();
        }
        
        final ByteBuffer mapBuffer = pp.getByteBuffer(0, (int) length);
        
        MemoryUtil.memFree(pp);
        
        return mapBuffer;
    }

    @Override
    public void bufferSetData(VK10Buffer buffer, ByteBuffer src, long usage) {
        bufferInitialize(buffer);
        
        final ByteBuffer dst = bufferMapData(buffer, 0, buffer.size, 0);
        
        dst.put(src);
        bufferUnmapData(buffer);
    }

    @Override
    public void bufferUnmapData(VK10Buffer buffer) {
        VK10.vkUnmapMemory(device, buffer.pMemory);
    }

    @Override
    public void clear(long bitfield, double red, double green, double blue, double alpha, double depth) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public void depthTestDisable() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public void depthTestEnable(long depthTest) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public void drawQueryBeginConditionalRender(VK10DrawQuery query, long mode) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public VK10DrawQuery drawQueryCreate() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public void drawQueryDelete(VK10DrawQuery query) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public void drawQueryDisable(long condition) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public void drawQueryEnable(long condition, VK10DrawQuery query) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public void drawQueryEndConditionRender() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public void framebufferAddAttachment(VK10Framebuffer framebuffer, long attachmentId, VK10Texture texId, long mipmapLevel) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public void framebufferAddDepthAttachment(VK10Framebuffer framebuffer, VK10Texture texId, long mipmapLevel) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public void framebufferAddDepthStencilAttachment(VK10Framebuffer framebuffer, VK10Texture texId, long mipmapLevel) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public void framebufferBind(VK10Framebuffer framebuffer, IntBuffer attachments) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public void framebufferBlit(VK10Framebuffer srcFb, long srcX0, long srcY0, long srcX1, long srcY1, VK10Framebuffer dstFb, long dstX0, long dstY0, long dstX1, long dstY1, long bitfield, long filter) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public VK10Framebuffer framebufferCreate() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public void framebufferDelete(VK10Framebuffer framebuffer) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public VK10Framebuffer framebufferGetDefault() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public void framebufferGetPixels(VK10Framebuffer framebuffer, long x, long y, long width, long height, long format, long type, VK10Buffer dstBuffer) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public void framebufferGetPixels(VK10Framebuffer framebuffer, long x, long y, long width, long height, long format, long type, ByteBuffer dstBuffer) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public boolean framebufferIsComplete(VK10Framebuffer framebuffer) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public boolean is64bitUniformsSupported() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public boolean isBufferSupported() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public boolean isComputeShaderSupported() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public boolean isDrawIndirectSupported() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public boolean isDrawInstancedSupported() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public boolean isDrawQuerySupported() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public boolean isFramebufferSupported() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public boolean isImmutableBufferSupported() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public boolean isInvalidateSubdataSupported() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public boolean isProgramSupported() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public boolean isSamplerSupported() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public boolean isSeparateShaderObjectsSupported() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public boolean isSparseTextureSupported() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public boolean isSupported() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public boolean isVertexArraySupported() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public void maskApply(boolean red, boolean green, boolean blue, boolean alpha, boolean depth, long stencil) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public void polygonSetParameters(double pointSize, double lineWidth, long frontFace, long cullFrace, long polygonMode, double offsetFactor, double offsetUnits) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public VK10Program programCreate() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public void programDelete(VK10Program program) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public void programDispatchCompute(VK10Program program, long numX, long numY, long numZ) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public long programGetUniformLocation(VK10Program program, String name) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public void programLinkShaders(VK10Program program, Shader[] shaders) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public void programSetAttribLocation(VK10Program program, long index, String name) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public void programSetFeedbackBuffer(VK10Program program, long varyingLoc, VK10Buffer buffer) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public void programSetFeedbackVaryings(VK10Program program, String[] varyings) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public void programSetStorage(VK10Program program, String storageName, VK10Buffer buffer, long bindingPoint) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public void programSetUniformBlock(VK10Program program, String uniformName, VK10Buffer buffer, long bindingPoint) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public void programSetUniformD(VK10Program program, long uLoc, double[] value) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public void programSetUniformF(VK10Program program, long uLoc, float[] value) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public void programSetUniformI(VK10Program program, long uLoc, int[] value) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public void programSetUniformMatD(VK10Program program, long uLoc, DoubleBuffer mat) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public void programSetUniformMatF(VK10Program program, long uLoc, FloatBuffer mat) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public void programUse(VK10Program program) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public void samplerBind(long unit, VK10Sampler sampler) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public VK10Sampler samplerCreate() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public void samplerDelete(VK10Sampler sampler) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public void samplerSetParameter(VK10Sampler sampler, long param, long value) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public void samplerSetParameter(VK10Sampler sampler, long param, double value) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public void scissorTestDisable() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public void scissorTestEnable(long left, long bottom, long width, long height) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public VK10Shader shaderCompile(long type, String source) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public void shaderDelete(VK10Shader shader) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public String shaderGetInfoLog(VK10Shader shader) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public long shaderGetParameter(VK10Shader shader, long pName) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public VK10Texture textureAllocate(long mipmaps, long internalFormat, long width, long height, long depth) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public void textureAllocatePage(VK10Texture texture, long level, long xOffset, long yOffset, long zOffset, long width, long height, long depth) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public void textureBind(VK10Texture texture, long unit) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public void textureDeallocatePage(VK10Texture texture, long level, long xOffset, long yOffset, long zOffset, long width, long height, long depth) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public void textureDelete(VK10Texture texture) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public void textureGenerateMipmap(VK10Texture texture) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public void textureGetData(VK10Texture texture, long level, long format, long type, ByteBuffer out) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public long textureGetMaxAnisotropy() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public long textureGetMaxBoundTextures() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public long textureGetMaxSize() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public long textureGetPageDepth(VK10Texture texture) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public long textureGetPageHeight(VK10Texture texture) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public long textureGetPageWidth(VK10Texture texture) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public long textureGetPreferredFormat(long internalFormat) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public void textureInvalidateData(VK10Texture texture, long level) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public void textureInvalidateRange(VK10Texture texture, long level, long xOffset, long yOffset, long zOffset, long width, long height, long depth) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public void textureSetData(VK10Texture texture, long level, long xOffset, long yOffset, long zOffset, long width, long height, long depth, long format, long type, ByteBuffer data) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public void textureSetParameter(VK10Texture texture, long param, long value) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public void textureSetParameter(VK10Texture texture, long param, double value) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public void vertexArrayAttachBuffer(VK10VertexArray vao, long index, VK10Buffer buffer, long size, long type, long stride, long offset, long divisor) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public void vertexArrayAttachIndexBuffer(VK10VertexArray vao, VK10Buffer buffer) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public VK10VertexArray vertexArrayCreate() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public void vertexArrayDelete(VK10VertexArray vao) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public void vertexArrayDrawArrays(VK10VertexArray vao, long drawMode, long start, long count) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public void vertexArrayDrawArraysIndirect(VK10VertexArray vao, VK10Buffer cmdBuffer, long drawMode, long offset) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public void vertexArrayDrawArraysInstanced(VK10VertexArray vao, long drawMode, long first, long count, long instanceCount) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public void vertexArrayDrawElements(VK10VertexArray vao, long drawMode, long count, long type, long offset) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public void vertexArrayDrawElementsIndirect(VK10VertexArray vao, VK10Buffer cmdBuffer, long drawMode, long indexType, long offset) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public void vertexArrayDrawElementsInstanced(VK10VertexArray vao, long drawMode, long count, long type, long offset, long instanceCount) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public void vertexArrayDrawTransformFeedback(VK10VertexArray vao, long drawMode, long start, long count) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public void vertexArrayMultiDrawArrays(VK10VertexArray vao, long drawMode, IntBuffer first, IntBuffer count) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public void viewportApply(long x, long y, long width, long height) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

}
