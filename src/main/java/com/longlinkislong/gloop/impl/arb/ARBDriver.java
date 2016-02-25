/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.longlinkislong.gloop.impl.arb;

import com.longlinkislong.gloop.impl.Driver;
import java.nio.ByteBuffer;
import java.nio.DoubleBuffer;
import java.nio.FloatBuffer;
import java.nio.IntBuffer;
import org.lwjgl.opengl.GL11;

/**
 *
 * @author zmichaels
 */
public class ARBDriver implements Driver<
        ARBBuffer, ARBFramebuffer, ARBTexture, ARBShader, ARBProgram,
        ARBSampler, ARBVertexArray, ARBDrawQuery> {

    @Override
    public void blendingEnable(long rgbEq, long aEq, long rgbFuncSrc, long rgbFuncDst, long aFuncSrc, long aFuncDst) {
        GL11.glEnable(GL11.GL_BLEND);
    }

    @Override
    public void blendingDisable() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public ARBBuffer bufferCreate() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public long bufferGetParameter(ARBBuffer buffer, long paramId) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public void bufferDelete(ARBBuffer buffer) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public void bufferSetData(ARBBuffer buffer, ByteBuffer data, long usage) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public void bufferAllocateImmutable(ARBBuffer buffer, long size, long bitflags) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public void bufferAllocate(ARBBuffer buffer, long size, long usage) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public void bufferGetData(ARBBuffer buffer, long offset, ByteBuffer out) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public ByteBuffer bufferMapData(ARBBuffer buffer, long offset, long length, long accessFlags) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public void bufferUnmapData(ARBBuffer buffer) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public void bufferCopyData(ARBBuffer srcBuffer, long srcOffset, ARBBuffer dstBuffer, long dstOffset, long size) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public void bufferInvalidateRange(ARBBuffer buffer, long offset, long length) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public void bufferInvalidateData(ARBBuffer buffer) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public void clear(long bitfield, double red, double green, double blue, double alpha, double depth) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public void depthTestEnable(long depthTest) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public void depthTestDisable() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public boolean framebufferIsComplete(ARBFramebuffer framebuffer) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public ARBFramebuffer framebufferCreate() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public void framebufferDelete(ARBFramebuffer framebuffer) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public void framebufferBind(ARBFramebuffer framebuffer, IntBuffer attachments) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public void framebufferAddDepthStencilAttachment(ARBFramebuffer framebuffer, ARBTexture texId, long mipmapLevel) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public void framebufferAddDepthAttachment(ARBFramebuffer framebuffer, ARBTexture texId, long mipmapLevel) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public void framebufferAddAttachment(ARBFramebuffer framebuffer, long attachmentId, ARBTexture texId, long mipmapLevel) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public void framebufferBlit(ARBFramebuffer srcFb, long srcX0, long srcY0, long srcX1, long srcY1, ARBFramebuffer dstFb, long dstX0, long dstY0, long dstX1, long dstY1, long bitfield, long filter) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public void framebufferGetPixels(ARBFramebuffer framebuffer, long x, long y, long width, long height, long format, long type, ARBBuffer dstBuffer) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public void framebufferGetPixels(ARBFramebuffer framebuffer, long x, long y, long width, long height, long format, long type, ByteBuffer dstBuffer) {
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
    public void programUse(ARBProgram program) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public void programSetAttribLocation(ARBProgram program, long index, String name) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public void programSetFeedbackVaryings(ARBProgram program, String[] varyings) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public void programSetUniformMatD(ARBProgram program, long uLoc, DoubleBuffer mat) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public void programSetUniformMatF(ARBProgram program, long uLoc, FloatBuffer mat) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public void programSetUniformD(ARBProgram program, long uLoc, double[] value) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public void programSetUniformF(ARBProgram program, long uLoc, float[] value) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public void programSetUniformI(ARBProgram program, long uLoc, int[] value) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public void programLinkShaders(ARBProgram program, ARBShader[] shaders) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public ARBProgram programCreate() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public void programDelete(ARBProgram program) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public void programSetStorage(ARBProgram program, String storageName, ARBBuffer buffer, long bindingPoint) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public void programSetUniformBlock(ARBProgram program, String uniformName, ARBBuffer buffer, long bindingPoint) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public void programDispatchCompute(ARBProgram program, long numX, long numY, long numZ) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public void programSetFeedbackBuffer(ARBProgram program, long varyingLoc, ARBBuffer buffer) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public ARBSampler samplerCreate() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public void samplerSetParameter(ARBSampler sampler, long param, long value) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public void samplerDelete(ARBSampler sampler) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public void samplerBind(long unit, ARBSampler sampler) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public void scissorTestEnable(long left, long bottom, long width, long height) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public void scissorTestDisable() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public ARBShader shaderCompile(long type, String source) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public String shaderGetInfoLog(ARBShader shader) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public long shaderGetParameter(ARBShader shader, long pName) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public void shaderDelete(ARBShader shader) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public ARBTexture textureAllocate(long mipmaps, long internalFormat, long width, long height, long depth) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public void textureBind(ARBTexture texture, long unit) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public void textureDelete(ARBTexture texture) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public void textureSetData(ARBTexture texture, long level, long xOffset, long yOffset, long zOffset, long width, long height, long depth, long format, long type, ByteBuffer data) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public void textureGetData(ARBTexture texture, long level, long format, long type, ByteBuffer out) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public void textureInvalidateData(ARBTexture texture, long level) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public void textureInvalidateRange(ARBTexture texture, long level, long xOffset, long yOffset, long zOffset, long width, long height, long depth) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public void textureGenerateMipmap(ARBTexture texture) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public long textureGetMaxSize() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public long textureGetMaxBoundTextures() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public long textureGetPreferredFormat(long internalFormat) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public void textureSetParameter(ARBTexture texture, long param, long value) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public void textureSetParameter(ARBTexture texture, long param, double value) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public void textureAllocatePage(ARBTexture texture, long level, long xOffset, long yOffset, long zOffset, long width, long height, long depth) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public void textureDeallocatePage(ARBTexture texture, long level, long xOffset, long yOffset, long zOffset, long width, long height, long depth) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public void vertexArrayBind(ARBVertexArray vao) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public ARBVertexArray vertexArrayCreate() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public void vertexArrayDrawElementsIndirect(ARBVertexArray vao, ARBBuffer cmdBuffer, long drawMode, long indexType, long offset) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public void vertexArrayDrawArraysIndirect(ARBVertexArray vao, ARBBuffer cmdBuffer, long drawMode, long offset) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public void vertexArrayMultiDrawArrays(ARBVertexArray vao, long drawMode, IntBuffer first, IntBuffer count) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public void vertexArrayDrawElementsInstanced(ARBVertexArray vao, long drawMode, long count, long type, long offset, long instanceCount) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public void vertexArrayDrawArraysInstanced(ARBVertexArray vao, long drawMode, long first, long count, long instanceCount) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public void vertexArrayDrawElements(ARBVertexArray vao, long drawMode, long count, long type, long offset) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public void vertexArrayDrawArrays(ARBVertexArray vao, long drawMode, long start, long count) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public void vertexArrayDrawTransformFeedback(ARBVertexArray vao, long drawMode, long start, long count) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public void vertexArrayDelete(ARBVertexArray vao) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public void vertexArrayAttachIndexBuffer(ARBVertexArray vao, ARBBuffer buffer) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public void vertexArrayAttachBuffer(ARBVertexArray vao, long index, ARBBuffer buffer, long type, long size, long offset, long stride, long divisor) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public void viewportApply(long x, long y, long width, long height) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public void drawQueryEnable(long condition, ARBDrawQuery query) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public void drawQueryDisable(long condition) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public void drawQueryBeginConditionalRender(ARBDrawQuery query, long mode) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public void drawQueryEndConditionRender() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public ARBDrawQuery drawQueryCreate() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public void drawQueryDelete(ARBDrawQuery query) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }
    
}
