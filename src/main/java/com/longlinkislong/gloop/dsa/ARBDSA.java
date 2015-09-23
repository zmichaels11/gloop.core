/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.longlinkislong.gloop.dsa;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.DoubleBuffer;
import java.nio.FloatBuffer;
import org.lwjgl.opengl.ARBDirectStateAccess;
import org.lwjgl.opengl.ARBSeparateShaderObjects;
import org.lwjgl.opengl.ContextCapabilities;
import org.lwjgl.opengl.GL;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL30;
import org.lwjgl.opengl.GL41;

/**
 *
 * @author zmichaels
 */
public class ARBDSA implements DSADriver {    
    public static DSADriver getInstance() {
        return Holder.INSTANCE;
    }

    @Override
    public int glCreateFramebuffers() {
        final int out = ARBDirectStateAccess.glCreateFramebuffers();
        assert GL11.glGetError() == GL11.GL_NO_ERROR : String.format("glCreateFramebuffers() = %d failed!", out);
        return out;
    }

    @Override
    public void glTextureStorage1d(int textureId, int levels, int internalFormat, int width) {
        assert textureId > 0 : String.format("Invalid textureId [%d]! Must be at least 1.", textureId);
        assert levels > 0: String.format("Invalid mipmap levels [%d]! Must be at least 1.", levels);
        assert width > 0 : String.format("Invalid width [%d]! Must be at least 1.", width);
        
        ARBDirectStateAccess.glTextureStorage1D(textureId, levels, internalFormat, width);
        assert GL11.glGetError() == GL11.GL_NO_ERROR : String.format("glTextureStorage1d(%d, %d, %d, %d) failed!", textureId, levels, internalFormat, width);
    }

    @Override
    public void glTextureStorage2d(int textureId, int levels, int internalFormat, int width, int height) {
        assert textureId > 0 : String.format("Invalid textureId [%d]! Must be at least 1.", textureId);
        assert levels > 0: String.format("Invalid mipmap levels [%d]! Must be at least 1.", levels);
        assert width > 0 : String.format("Invalid width [%d]! Must be at least 1.", width);
        assert height > 0 : String.format("Invalid height [%d]! Must be at least 1.", height);
        
        ARBDirectStateAccess.glTextureStorage2D(textureId, levels, internalFormat, width, height);
        assert GL11.glGetError() == GL11.GL_NO_ERROR : String.format("glTextureStorage2d(%d, %d, %d, %d, %d) failed!", textureId, levels, internalFormat, width, height);
    }

    @Override
    public void glTextureStorage3d(int textureId, int levels, int internalFormat, int width, int height, int depth) {
        assert textureId > 0 : String.format("Invalid textureId [%d]! Must be at least 1.", textureId);
        assert levels > 0: String.format("Invalid mipmap levels [%d]! Must be at least 1.", levels);
        assert width > 0 : String.format("Invalid width [%d]! Must be at least 1.", width);
        assert height > 0 : String.format("Invalid height [%d]! Must be at least 1.", height);
        assert depth > 0 : String.format("Invalid depth [%d]! Must be at least 1.", depth);
        
        ARBDirectStateAccess.glTextureStorage3D(textureId, levels, internalFormat, width, height, depth);
        assert GL11.glGetError() == GL11.GL_NO_ERROR : String.format("glTextureStorage3d(%d, %d, %d, %d, %d, %d) failed!", textureId, levels, internalFormat, width, height, depth);
    }

    @Override
    public void glTextureParameteri(int textureId, int pName, int val) {
        assert textureId > 0 : String.format("Invalid textureId [%d]! Must be at least 1.", textureId);
        
        ARBDirectStateAccess.glTextureParameteri(textureId, pName, val);
        assert GL11.glGetError() == GL11.GL_NO_ERROR : String.format("glTextureParameteri(%d, %d, %d) failed!", textureId, pName, val);
    }

    @Override
    public void glTextureParameterf(int textureId, int pName, float val) {
        assert textureId > 0 : String.format("Invalid textureId [%d]! Must be at least 1.", textureId);
        
        ARBDirectStateAccess.glTextureParameterf(textureId, pName, val);
        assert GL11.glGetError() == GL11.GL_NO_ERROR : String.format("glTextureParameterf(%d, %d, %f) failed!", textureId, pName, val);
    }

    @Override
    public int glGetNamedBufferParameteri(int bufferId, int pName) {
        assert bufferId > 0 : String.format("Invalid bufferId [%d]! Must be at least 1.", bufferId);
        
        final int out = ARBDirectStateAccess.glGetNamedBufferParameteri(bufferId, pName);
        assert GL11.glGetError() == GL11.GL_NO_ERROR : String.format("glGetNamedBfuferParameteri(%d, %d) failed!", bufferId, pName);
        return out;
    }

    @Override
    public void glNamedFramebufferTexture(int framebuffer, int attachment, int texture, int level) {
        assert framebuffer > 0 : String.format("Invalid framebufferId [%d]! Must be at least 1.", framebuffer);
        assert attachment >= GL30.GL_COLOR_ATTACHMENT0 : String.format("Invalid attachment id [%d]! Attachment must be at least GL_COLOR_ATTACHMENT0 [%d]", attachment, GL30.GL_COLOR_ATTACHMENT0);
        assert texture > 0 : String.format("Invalid textureId [%d]! Must be at least 1.", texture);
        assert level >= 0 : String.format("Invalid mipmap level [%d]! Must be at least 0.", level);
        
        ARBDirectStateAccess.glNamedFramebufferTexture(framebuffer, attachment, texture, level);
        assert GL11.glGetError() == GL11.GL_NO_ERROR : String.format("glNamedFramebufferTexture(%d, %d, %d, %d) failed!", framebuffer, attachment, texture, level);
    }

    @Override
    public void glNamedBufferReadPixels(int bufferId, int x, int y, int width, int height, int format, int type, long ptr) {                
        FakeDSA.getInstance().glNamedBufferReadPixels(bufferId, x, y, width, height, format, type, ptr);
    }

    @Override
    public void glBlitNamedFramebuffer(int readFramebuffer, int drawFramebuffer, int srcX, int srcY, int srcX1, int srcY1, int dstX0, int dstY0, int dstX1, int dstY1, int mask, int filter) {
        assert readFramebuffer >= 0 : String.format("Invalid framebufferId [%d]! Must be at least 0.", readFramebuffer);
        assert drawFramebuffer >= 0 : String.format("Invalid framebufferId [%d]! Must be at least 0.", drawFramebuffer);
        assert srcX >= 0 && srcY >= 0 && srcX1 >= 0 && srcY1 >= 0 : String.format("Invalid blit rectangle [%d, %d, %d, %d]!", srcX, srcY, srcX1, srcY1);
        assert dstX0 >= 0 && dstY0 >= 0 && dstX1 >= 0 && dstY1 >= 0 : String.format("Invalid blit rectangle [%d, %d, %d, %d]!", dstX0, dstY0, dstX1, dstY1);
        
        ARBDirectStateAccess.glBlitNamedFramebuffer(readFramebuffer, drawFramebuffer, srcX1, srcY1, srcX1, srcY1, dstX0, dstY0, dstX1, dstY1, mask, filter);        
    }

    @Override
    public void glGetTextureImage(int texture, int level, int format, int type, int bufferSize, ByteBuffer pixels) {
        assert texture > 0 : String.format("Invalid textureId [%d]! Must be at least 1.", texture);
        assert level >= 0 : String.format("Invalid mipmap! Level must be at least 0.", level);
        assert pixels.limit() >= bufferSize : String.format("Not enough space in write buffer! Need: %d, have: %d", bufferSize, pixels.limit());
        
        ARBDirectStateAccess.glGetTextureImage(texture, level, format, type, bufferSize, pixels);
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
        final ContextCapabilities cap = GL.getCurrent().getCapabilities();

        return cap.GL_ARB_direct_state_access && FakeDSA.getInstance().isSupported();
    }

    @Override
    public int glCreateBuffers() {
        final int out = ARBDirectStateAccess.glCreateBuffers();
        assert GL11.glGetError() == GL11.GL_NO_ERROR : String.format("glCreateBuffers() = %d failed!", out);
        return out;
    }

    @Override
    public int glCreateTextures(int target) {
        final int out = ARBDirectStateAccess.glCreateTextures(target);
        assert GL11.glGetError() == GL11.GL_NO_ERROR : String.format("glCreateTextures() = %d failed!", out);
        return out;
    }

    @Override
    public void glNamedBufferData(int bufferId, long size, int usage) {
        assert bufferId > 0 : String.format("Invalid bufferId [%d]! Must be at least 1.", bufferId);
        assert size >= 0L : String.format("Invalid size [%d]! Must be at least 0.", size);
        
        ARBDirectStateAccess.glNamedBufferData(bufferId, size, usage);
        assert GL11.glGetError() == GL11.GL_NO_ERROR : String.format("glNamedBufferData(%d, %d, %d) failed!", bufferId, size, usage);
    }

    @Override
    public void glNamedBufferData(int bufferId, ByteBuffer data, int usage) {
        assert bufferId > 0 : String.format("Invalid bufferId [%d]! Must be at least 1.", bufferId);
        assert data.order() == ByteOrder.nativeOrder() : "ByteBuffer is not in native order!";
        assert data.isDirect() : "ByteBuffer is not direct!";
        
        ARBDirectStateAccess.glNamedBufferData(bufferId, data, usage);
        assert GL11.glGetError() == GL11.GL_NO_ERROR : String.format("glNamedBufferData(%d, [data], %d) failed!", bufferId, usage);
    }

    @Override
    public void glNamedBufferSubData(int buffer, long offset, ByteBuffer data) {
        assert buffer > 0 : String.format("Invalid bufferId [%d]! Must be at least 1.", buffer);
        assert offset >= 0L : String.format("Invalid offset [%d]! Must be at least 0.", offset);
        assert data.order() == ByteOrder.nativeOrder() : "ByteBuffer is not in native order!";
        assert data.isDirect() : "ByteBuffer is not direct!";
        
        ARBDirectStateAccess.glNamedBufferSubData(buffer, offset, data);
        assert GL11.glGetError() == GL11.GL_NO_ERROR : String.format("glNamedBufferSubData(%d, %d, [data]) failed!", buffer, offset);
    }

    @Override
    public void glNamedBufferStorage(int bufferId, ByteBuffer data, int flags) {
        assert bufferId > 0 : String.format("Invalid bufferId [%d]! Must be at least 1.", bufferId);
        assert data.order() == ByteOrder.nativeOrder() : "ByteBuffer is not in native order!";
        assert data.isDirect() : "ByteBuffer is not direct!";
        
        ARBDirectStateAccess.glNamedBufferStorage(bufferId, data, flags);
        assert GL11.glGetError() == GL11.GL_NO_ERROR : String.format("glNamedBufferStorage(%d, [data], %d) failed!", bufferId, flags);
    }

    @Override
    public void glNamedBufferStorage(int bufferId, long size, int flags) {
        assert bufferId > 0 : String.format("Invalid bufferId [%d]! Must be at least 1.", bufferId);
        assert size >= 0L : String.format("Invalid buffer size [%d]! Size must be at least 0.", size);
        
        ARBDirectStateAccess.glNamedBufferStorage(bufferId, size, flags);
        assert GL11.glGetError() == GL11.GL_NO_ERROR : String.format("glNamedBufferStorage(%d, %d, %d) failed!", bufferId, size, flags);
    }

    @Override
    public void glGetNamedBufferSubData(int bufferId, long offset, ByteBuffer out) {
        assert bufferId >= 0 : String.format("Invalid bufferId [%d]! Must be at least 0.", bufferId);
        assert offset >= 0 : String.format("Invalid offset [%d]! Must be at least 0.", offset);
        assert out.order() == ByteOrder.nativeOrder() : "ByteBuffer is not in native order!";
        assert out.isDirect() : "ByteBuffer is not direct!";
        
        ARBDirectStateAccess.glGetNamedBufferSubData(bufferId, offset, out);
        assert GL11.glGetError() == GL11.GL_NO_ERROR : String.format("glGetNamedBufferSubData(%d, %d, [data]) failed!", bufferId, offset);
    }

    @Override
    public ByteBuffer glMapNamedBufferRange(int bufferId, long offset, long length, int access, ByteBuffer recycled) {
        assert bufferId > 0 : String.format("Invalid bufferId [%d]! Must be at least 1.", bufferId);
        assert offset >= 0L : String.format("Invalid offset [%d]! Must be at least 0.", offset);
        assert length >= 0L : String.format("Invalid length [%d]! Must be at least 0.", length);
        
        final ByteBuffer out = ARBDirectStateAccess.glMapNamedBufferRange(bufferId, offset, length, access, recycled);
        assert GL11.glGetError() == GL11.GL_NO_ERROR : String.format("glMapNamedBufferRange(%d, %d, %d, %d, [data]) failed!", bufferId, offset, length, access);
        return out;
    }

    @Override
    public void glUnmapNamedBuffer(int bufferId) {
        assert bufferId > 0 : String.format("Invalid bufferId [%d]! Must be at least 1.", bufferId);
        ARBDirectStateAccess.glUnmapNamedBuffer(bufferId);
        assert GL11.glGetError() == GL11.GL_NO_ERROR : String.format("glUnmapNamedBuffer(%d) failed!", bufferId);
    }

    @Override
    public void glCopyNamedBufferSubData(int readBufferId, int writeBufferId, long readOffset, long writeOffset, long size) {
        assert readBufferId > 0 : String.format("Invalid read bufferId [%d]! Must be at least 1.", readBufferId);
        assert writeBufferId > 0 : String.format("Invalid write bufferId [%d]! Must be at least 1.", writeBufferId);
        assert readOffset >= 0L : String.format("Invalid read offset [%d]! Must be at least 0.", readOffset);
        assert writeOffset >= 0L : String.format("Invalid writeOffset [%d]! Must be at least 0.", writeOffset);
        assert size >= 0L : String.format("Invalid size [%d]! Must be at least 0.", size);
        
        ARBDirectStateAccess.glCopyNamedBufferSubData(readBufferId, writeBufferId, readOffset, writeOffset, size);
        assert GL11.glGetError() == GL11.GL_NO_ERROR : String.format("glCopyNamedBufferSubData(%d, %d, %d, %d, %d) failed!", readBufferId, writeBufferId, readOffset, writeOffset, size);
    }

    @Override
    public void glProgramUniform1f(int programId, int location, float value) {
        assert programId > 0 : String.format("Invalid programId [%d]! Must be at least 1.", programId);
        assert location >= 0 : String.format("Invalid location [%d]! Must be at least 0.", location);
        assert Float.isFinite(value) : String.format("Float value [%f] is not finite!", value);
        
        final ContextCapabilities cap = GL.getCapabilities();

        if (cap.OpenGL41) {
            GL41.glProgramUniform1f(programId, location, value);
            assert GL11.glGetError() == GL11.GL_NO_ERROR : String.format("glProgramUniform1f(%d, %d, %f) failed!", programId, location, value);
        } else if (cap.GL_ARB_separate_shader_objects) {
            ARBSeparateShaderObjects.glProgramUniform1f(programId, location, value);
            assert GL11.glGetError() == GL11.GL_NO_ERROR : String.format("glProgramUniform1fARB(%d, %d, %f) failed!", programId, location, value);
        } else {
            FakeDSA.getInstance().glProgramUniform1f(programId, location, value);
        }
    }

    @Override
    public void glProgramUniform2f(int programId, int location, float v0, float v1) {
        assert programId > 0 : String.format("Invalid programId [%d]! Must be at least 1.", programId);
        assert location >= 0 : String.format("Invalid location [%d]! Must be at least 0.", location);
        assert Float.isFinite(v0) : String.format("Float value [%f] is not finite!", v0);
        assert Float.isFinite(v1) : String.format("Float value [%f] is not finite!", v1);
        
        final ContextCapabilities cap = GL.getCapabilities();

        if (cap.OpenGL41) {
            GL41.glProgramUniform2f(programId, location, v0, v1);
            assert GL11.glGetError() == GL11.GL_NO_ERROR : String.format("glProgramUniform2f(%d, %d, %f, %f) failed!", programId, location, v0, v1);
        } else if (cap.GL_ARB_separate_shader_objects) {
            ARBSeparateShaderObjects.glProgramUniform2f(programId, location, v0, v1);
            assert GL11.glGetError() == GL11.GL_NO_ERROR : String.format("glProgramUniform2fARB(%d, %d, %f, %f) failed!", programId, location, v0, v1);
        } else {
            FakeDSA.getInstance().glProgramUniform2f(programId, location, v0, v1);
        }
    }

    @Override
    public void glProgramUniform3f(int programId, int location, float v0, float v1, float v2) {
        assert programId > 0 : String.format("Invalid programId [%d]! Must be at least 1.", programId);
        assert location >= 0 : String.format("Invalid location [%d]! Must be at least 0.", location);
        assert Float.isFinite(v0) : String.format("Float value [%f] is not finite!", v0);
        assert Float.isFinite(v1) : String.format("Float value [%f] is not finite!", v1);
        assert Float.isFinite(v2) : String.format("Float value [%f] is not finite!", v2);
        
        final ContextCapabilities cap = GL.getCapabilities();

        if (cap.OpenGL41) {
            GL41.glProgramUniform3f(programId, location, v0, v1, v2);
            assert GL11.glGetError() == GL11.GL_NO_ERROR : String.format("glProgramUniform3f(%d, %d, %f, %f, %f) failed!", programId, location, v0, v1, v2);
        } else if (cap.GL_ARB_separate_shader_objects) {
            ARBSeparateShaderObjects.glProgramUniform3f(programId, location, v0, v1, v2);
            assert GL11.glGetError() == GL11.GL_NO_ERROR : String.format("glProgramUniform3fARB(%d, %d, %f, %f, %f) failed!", programId, location, v0, v1, v2);
        } else {
            FakeDSA.getInstance().glProgramUniform3f(programId, location, v0, v1, v2);
        }
    }

    @Override
    public void glProgramUniform4f(int programId, int location, float v0, float v1, float v2, float v3) {
        assert programId > 0 : String.format("Invalid programId [%d]! Must be at least 1.", programId);
        assert location >= 0 : String.format("Invalid location [%d]! Must be at least 0.", location);
        assert Float.isFinite(v0) : String.format("Float value [%f] is not finite!", v0);
        assert Float.isFinite(v1) : String.format("Float value [%f] is not finite!", v1);
        assert Float.isFinite(v2) : String.format("Float value [%f] is not finite!", v2);
        assert Float.isFinite(v3) : String.format("Float value [%f] is not finite!", v3);
        
        final ContextCapabilities cap = GL.getCapabilities();

        if (cap.OpenGL41) {
            GL41.glProgramUniform4f(programId, location, v0, v1, v2, v3);
            assert GL11.glGetError() == GL11.GL_NO_ERROR : String.format("glProgramUniform4f(%d, %d, %f, %f, %f, %f) failed!", programId, location, v0, v1, v2, v3);
        } else if (cap.GL_ARB_separate_shader_objects) {
            ARBSeparateShaderObjects.glProgramUniform4f(programId, location, v0, v1, v2, v3);
            assert GL11.glGetError() == GL11.GL_NO_ERROR : String.format("glProgramUniform4fARB(%d, %d, %f, %f, %f, %f) failed!", programId, location, v0, v1, v2, v3);
        } else {
            FakeDSA.getInstance().glProgramUniform4f(programId, location, v0, v1, v2, v3);
        }
    }

    @Override
    public void glProgramUniform1i(int programId, int location, int value) {
        assert programId > 0 : String.format("Invalid programId [%d]! Must be at least 1.", programId);
        assert location >= 0 : String.format("Invalid location [%d]! Must be at least 0.", location);
        
        final ContextCapabilities cap = GL.getCapabilities();

        if (cap.OpenGL41) {
            GL41.glProgramUniform1i(programId, location, value);
            assert GL11.glGetError() == GL11.GL_NO_ERROR : String.format("glProgramUniform1i(%d, %d, %d) failed!", programId, location, value);
        } else if (cap.GL_ARB_separate_shader_objects) {
            ARBSeparateShaderObjects.glProgramUniform1i(programId, location, value);
            assert GL11.glGetError() == GL11.GL_NO_ERROR : String.format("glProgramUniform1iARB(%d, %d, %d) failed!", programId, location, value);
        } else {
            FakeDSA.getInstance().glProgramUniform1i(programId, location, value);
        }
    }

    @Override
    public void glProgramUniform2i(int programId, int location, int v0, int v1) {
        assert programId > 0 : String.format("Invalid programId [%d]! Must be at least 1.", programId);
        assert location >= 0 : String.format("Invalid location [%d]! Must be at least 0.", location);
        
        final ContextCapabilities cap = GL.getCapabilities();

        if (cap.OpenGL41) {
            GL41.glProgramUniform2i(programId, location, v0, v1);
            assert GL11.glGetError() == GL11.GL_NO_ERROR : String.format("glProgramUniform2i(%d, %d, %d, %d) failed!", programId, location, v0, v1);
        } else if (cap.GL_ARB_separate_shader_objects) {
            ARBSeparateShaderObjects.glProgramUniform2i(programId, location, v0, v1);
            assert GL11.glGetError() == GL11.GL_NO_ERROR : String.format("glProgramUniform2iARB(%d, %d, %d, %d) failed!", programId, location, v0, v1);
        } else {
            FakeDSA.getInstance().glProgramUniform2i(programId, location, v0, v1);
        }
    }

    @Override
    public void glProgramUniform3i(int programId, int location, int v0, int v1, int v2) {
        assert programId > 0 : String.format("Invalid programId [%d]! Must be at least 1.", programId);
        assert location >= 0 : String.format("Invalid location [%d]! Must be at least 0.", location);
        
        final ContextCapabilities cap = GL.getCapabilities();

        if (cap.OpenGL41) {
            GL41.glProgramUniform3i(programId, location, v0, v1, v2);
            assert GL11.glGetError() == GL11.GL_NO_ERROR : String.format("glProgramUniform3i(%d, %d, %d, %d, %d) failed!", programId, location, v0, v1, v2);
        } else if (cap.GL_ARB_separate_shader_objects) {
            ARBSeparateShaderObjects.glProgramUniform3i(programId, location, v0, v1, v2);
            assert GL11.glGetError() == GL11.GL_NO_ERROR : String.format("glProgramUniform3iARB(%d, %d, %d, %d, %d) failed!", programId, location, v0, v1, v2);
        } else {
            FakeDSA.getInstance().glProgramUniform3i(programId, location, v0, v1, v2);
        }
    }

    @Override
    public void glProgramUniform4i(int programId, int location, int v0, int v1, int v2, int v3) {
        assert programId > 0 : String.format("Invalid programId [%d]! Must be at least 1.", programId);
        assert location >= 0 : String.format("Invalid location [%d]! Must be at least 0.", location);
        
        final ContextCapabilities cap = GL.getCapabilities();

        if (cap.OpenGL41) {
            GL41.glProgramUniform4i(programId, location, v0, v1, v2, v3);
            assert GL11.glGetError() == GL11.GL_NO_ERROR : String.format("glProgramUniform4i(%d, %d, %d, %d, %d, %d) failed!", programId, location, v0, v1, v2, v3);
        } else if (cap.GL_ARB_separate_shader_objects) {
            ARBSeparateShaderObjects.glProgramUniform4i(programId, location, v0, v1, v2, v3);
            assert GL11.glGetError() == GL11.GL_NO_ERROR : String.format("glProgramUniform4iARB(%d, %d, %d, %d, %d, %d) failed!", programId, location, v0, v1, v2, v3);
        } else {
            FakeDSA.getInstance().glProgramUniform4i(programId, location, v0, v1, v2, v3);
        }
    }

    @Override
    public void glProgramUniform1d(int programId, int location, double value) {
        assert programId > 0 : String.format("Invalid programId [%d]! Must be at least 1.", programId);
        assert location >= 0 : String.format("Invalid location [%d]! Must be at least 0.", location);
        assert Double.isFinite(value) : String.format("Double value [%f] is not finite!", value);
        
        final ContextCapabilities cap = GL.getCapabilities();

        if (cap.OpenGL41) {
            GL41.glProgramUniform1d(programId, location, value);
            assert GL11.glGetError() == GL11.GL_NO_ERROR : String.format("glProgramUniform1d(%d, %d, %f) failed!", programId, location, value);
        } else if (cap.GL_ARB_separate_shader_objects) {
            ARBSeparateShaderObjects.glProgramUniform1d(programId, location, value);
            assert GL11.glGetError() == GL11.GL_NO_ERROR : String.format("glProgramUniform1dARB(%d, %d, %f) failed!", programId, location, value);
        } else {
            FakeDSA.getInstance().glProgramUniform1d(programId, location, value);
        }
    }

    @Override
    public void glProgramUniform2d(int programId, int location, double v0, double v1) {
        assert programId > 0 : String.format("Invalid programId [%d]! Must be at least 1.", programId);
        assert location >= 0 : String.format("Invalid location [%d]! Must be at least 0.", location);
        assert Double.isFinite(v0) : String.format("Double value [%f] is not finite!", v0);
        assert Double.isFinite(v1) : String.format("Double value [%f] is not finite!", v1);
        
        final ContextCapabilities cap = GL.getCapabilities();

        if (cap.OpenGL41) {
            GL41.glProgramUniform2d(programId, location, v0, v1);
            assert GL11.glGetError() == GL11.GL_NO_ERROR : String.format("glProgramUniform2d(%d, %d, %f, %f) failed!", programId, location, v0, v1);
        } else if (cap.GL_ARB_separate_shader_objects) {
            ARBSeparateShaderObjects.glProgramUniform2d(programId, location, v0, v1);
            assert GL11.glGetError() == GL11.GL_NO_ERROR : String.format("glProgramUniform2dARB(%d, %d, %f, %f) failed!", programId, location, v0, v1);
        } else {
            FakeDSA.getInstance().glProgramUniform2d(programId, location, v0, v1);
        }
    }

    @Override
    public void glProgramUniform3d(int programId, int location, double v0, double v1, double v2) {
        assert programId > 0 : String.format("Invalid programId [%d]! Must be at least 1.", programId);
        assert location >= 0 : String.format("Invalid location [%d]! Must be at least 0.", location);
        assert Double.isFinite(v0) : String.format("Double value [%f] is not finite!", v0);
        assert Double.isFinite(v1) : String.format("Double value [%f] is not finite!", v1);
        assert Double.isFinite(v2) : String.format("Double value [%f] is not finite!", v2);
        
        final ContextCapabilities cap = GL.getCapabilities();

        if (cap.OpenGL41) {
            GL41.glProgramUniform3d(programId, location, v0, v1, v2);
            assert GL11.glGetError() == GL11.GL_NO_ERROR : String.format("glProgramUniform3d(%d, %d, %f, %f, %f) failed!", programId, location, v0, v1, v2);
        } else if (cap.GL_ARB_separate_shader_objects) {
            ARBSeparateShaderObjects.glProgramUniform3d(programId, location, v0, v1, v2);
            assert GL11.glGetError() == GL11.GL_NO_ERROR : String.format("glProgramUniform3dARB(%d, %d, %f, %f, %f) failed!", programId, location, v0, v1, v2);
        } else {
            FakeDSA.getInstance().glProgramUniform3d(programId, location, v0, v1, v2);
        }
    }

    @Override
    public void glProgramUniform4d(int programId, int location, double v0, double v1, double v2, double v3) {
        assert programId > 0 : String.format("Invalid programId [%d]! Must be at least 1.", programId);
        assert location >= 0 : String.format("Invalid location [%d]! Must be at least 0.", location);
        assert Double.isFinite(v0) : String.format("Double value [%f] is not finite!", v0);
        assert Double.isFinite(v1) : String.format("Double value [%f] is not finite!", v1);
        assert Double.isFinite(v2) : String.format("Double value [%f] is not finite!", v2);
        assert Double.isFinite(v3) : String.format("Double value [%f] is not finite!", v3);
        
        final ContextCapabilities cap = GL.getCapabilities();

        if (cap.OpenGL41) {
            GL41.glProgramUniform4d(programId, location, v0, v1, v2, v3);
            assert GL11.glGetError() == GL11.GL_NO_ERROR : String.format("glProgramUniform4d(%d, %d, %f, %f, %f, %f) failed!", programId, location, v0, v1, v2, v3);
        } else if (cap.GL_ARB_separate_shader_objects) {
            ARBSeparateShaderObjects.glProgramUniform4d(programId, location, v0, v1, v2, v3);
            assert GL11.glGetError() == GL11.GL_NO_ERROR : String.format("glProgramUniform4dARB(%d, %d, %f, %f, %f, %f) failed!", programId, location, v0, v1, v2, v3);
        } else {
            FakeDSA.getInstance().glProgramUniform4d(programId, location, v0, v1, v2, v3);
        }
    }

    @Override
    public void glProgramUniformMatrix2f(int programId, int location, boolean needsTranspose, FloatBuffer data) {
        assert programId > 0 : String.format("Invalid programId [%d]! Must be at least 1.", programId);
        assert location >= 0 : String.format("Invalid location [%d]! Must be at least 0.", location);
        assert data.order() == ByteOrder.nativeOrder() : "FloatBuffer is not in native order!";
        assert data.isDirect() : "FloatBuffer is not direct!";
        assert data.limit() >= 4 : "glProgramUniformMatrix2f requires 4 floats!";
        
        final ContextCapabilities cap = GL.getCapabilities();

        if (cap.OpenGL41) {
            GL41.glProgramUniformMatrix2fv(programId, location, needsTranspose, data);
            assert GL11.glGetError() == GL11.GL_NO_ERROR : String.format("glProgramUniformMatrix2d(%d, %d, %s, [data]) failed!", programId, location, needsTranspose);
        } else if (cap.GL_ARB_separate_shader_objects) {
            ARBSeparateShaderObjects.glProgramUniformMatrix2fv(programId, location, needsTranspose, data);
            assert GL11.glGetError() == GL11.GL_NO_ERROR : String.format("glProgramUniformMatrix2fARB(%d, %d, %s, [data]) failed!", programId, location, needsTranspose);
        } else {
            FakeDSA.getInstance().glProgramUniformMatrix2f(programId, location, needsTranspose, data);
        }
    }

    @Override
    public void glProgramUniformMatrix3f(int programId, int location, boolean needsTranspose, FloatBuffer data) {
        assert programId > 0 : String.format("Invalid programId [%d]! Must be at least 1.", programId);
        assert location >= 0 : String.format("Invalid location [%d]! Must be at least 0.", location);
        assert data.order() == ByteOrder.nativeOrder() : "FloatBuffer is not in native order!";
        assert data.isDirect() : "FloatBuffer is not direct!";
        assert data.limit() >= 9 : "glProgramUniformMatrix3f requires 9 floats!";
        
        final ContextCapabilities cap = GL.getCapabilities();

        if (cap.OpenGL41) {
            GL41.glProgramUniformMatrix3fv(programId, location, needsTranspose, data);
            assert GL11.glGetError() == GL11.GL_NO_ERROR : String.format("glProgramUniformMatrix3f(%d, %d, %s, [data]) failed!", programId, location, needsTranspose);
        } else if (cap.GL_ARB_separate_shader_objects) {
            ARBSeparateShaderObjects.glProgramUniformMatrix3fv(programId, location, needsTranspose, data);
            assert GL11.glGetError() == GL11.GL_NO_ERROR : String.format("glProgramUniformMatrix3fARB(%d, %d, %s, [data]) failed!", programId, location, needsTranspose);
        } else {
            FakeDSA.getInstance().glProgramUniformMatrix3f(programId, location, needsTranspose, data);
        }
    }

    @Override
    public void glProgramUniformMatrix4f(int programId, int location, boolean needsTranspose, FloatBuffer data) {
        assert programId > 0 : String.format("Invalid programId [%d]! Must be at least 1.", programId);
        assert location >= 0 : String.format("Invalid location [%d]! Must be at least 0.", location);
        assert data.order() == ByteOrder.nativeOrder() : "FloatBuffer is not in native order!";
        assert data.isDirect() : "FloatBuffer is not direct!";
        assert data.limit() >= 16 : "glProgramUniformMatrix4f requires 16 floats!";
        
        final ContextCapabilities cap = GL.getCapabilities();

        if (cap.OpenGL41) {
            GL41.glProgramUniformMatrix4fv(programId, location, needsTranspose, data);
            assert GL11.glGetError() == GL11.GL_NO_ERROR : String.format("glProgramUniformMatrix4f(%d, %d, %s, [data]) failed!", programId, location, needsTranspose);
        } else if (cap.GL_ARB_separate_shader_objects) {
            ARBSeparateShaderObjects.glProgramUniformMatrix4fv(programId, location, needsTranspose, data);
            assert GL11.glGetError() == GL11.GL_NO_ERROR : String.format("glProgramUniformMatrix4fARB(%d, %d, %s, [data]) failed!", programId, location, needsTranspose);
        } else {
            FakeDSA.getInstance().glProgramUniformMatrix4f(programId, location, needsTranspose, data);
        }
    }

    @Override
    public void glProgramUniformMatrix2d(int programId, int location, boolean needsTranspose, DoubleBuffer data) {
        assert programId > 0 : String.format("Invalid programId [%d]! Must be at least 1.", programId);
        assert location >= 0 : String.format("Invalid location [%d]! Must be at least 0.", location);
        assert data.order() == ByteOrder.nativeOrder() : "DoubleBuffer is not in native order!";
        assert data.isDirect() : "DoubleBuffer is not direct!";
        assert data.limit() >= 4 : "glProgramUniformMatrix2d requires 4 doubles!";
        
        final ContextCapabilities cap = GL.getCapabilities();

        if (cap.OpenGL41) {
            GL41.glProgramUniformMatrix2dv(programId, location, needsTranspose, data);
            assert GL11.glGetError() == GL11.GL_NO_ERROR : String.format("glProgramUniformMatrix2d(%d, %d, %s, [data]) failed!", programId, location, needsTranspose);
        } else if (cap.GL_ARB_separate_shader_objects) {
            ARBSeparateShaderObjects.glProgramUniformMatrix2dv(programId, location, needsTranspose, data);
            assert GL11.glGetError() == GL11.GL_NO_ERROR : String.format("glProgramUniformMatrix2dARB(%d, %d, %s, [data]) failed!", programId, location, needsTranspose);
        } else {
            FakeDSA.getInstance().glProgramUniformMatrix2d(programId, location, needsTranspose, data);
        }
    }

    @Override
    public void glProgramUniformMatrix3d(int programId, int location, boolean needsTranspose, DoubleBuffer data) {
        assert programId > 0 : String.format("Invalid programId [%d]! Must be at least 1.", programId);
        assert location >= 0 : String.format("Invalid location [%d]! Must be at least 0.", location);
        assert data.order() == ByteOrder.nativeOrder() : "DoubleBuffer is not in native order!";
        assert data.isDirect() : "DoubleBuffer is not direct!";
        assert data.limit() >= 9 : "glProgramUniformMatrix3d requires 9 doubles!";
        
        final ContextCapabilities cap = GL.getCapabilities();

        if (cap.OpenGL41) {
            GL41.glProgramUniformMatrix3dv(programId, location, needsTranspose, data);
            assert GL11.glGetError() == GL11.GL_NO_ERROR : String.format("glProgramUniformMatrix3d(%d, %d, %s, [data]) failed!", programId, location, needsTranspose);
        } else if (cap.GL_ARB_separate_shader_objects) {
            ARBSeparateShaderObjects.glProgramUniformMatrix3dv(programId, location, needsTranspose, data);
            assert GL11.glGetError() == GL11.GL_NO_ERROR : String.format("glProgramUniformMatrix3dARB(%d, %d, %s, [data]) failed!", programId, location, needsTranspose);
        } else {
            FakeDSA.getInstance().glProgramUniformMatrix3d(programId, location, needsTranspose, data);
        }
    }

    @Override
    public void glProgramUniformMatrix4d(int programId, int location, boolean needsTranspose, DoubleBuffer data) {
        assert programId > 0 : String.format("Invalid programId [%d]! Must be at least 1.", programId);
        assert location >= 0 : String.format("Invalid location [%d]! Must be at least 0.", location);
        assert data.order() == ByteOrder.nativeOrder() : "DoubleBuffer is not in native order!";
        assert data.isDirect() : "DoubleBuffer is not direct!";
        assert data.limit() >= 16 : "glProgramUniformMatrix4d requires 16 doubles!";
        
        final ContextCapabilities cap = GL.getCapabilities();

        if (cap.OpenGL41) {
            GL41.glProgramUniformMatrix4dv(programId, location, needsTranspose, data);
            assert GL11.glGetError() == GL11.GL_NO_ERROR : String.format("glProgramUniformMatrix4d(%d, %d, %s, [data]) failed!", programId, location, needsTranspose);
        } else if (cap.GL_ARB_separate_shader_objects) {
            ARBSeparateShaderObjects.glProgramUniformMatrix4dv(programId, location, needsTranspose, data);
            assert GL11.glGetError() == GL11.GL_NO_ERROR : String.format("glProgramUniformMatrix4dARB(%d, %d, %s, [data]) failed!", programId, location, needsTranspose);
        } else {
            FakeDSA.getInstance().glProgramUniformMatrix4d(programId, location, needsTranspose, data);
        }
    }

    @Override
    public void glTextureSubImage1d(int textureId, int level, int xOffset, int width, int format, int type, ByteBuffer pixels) {
        assert textureId > 0 : String.format("Invalid textureId [%d]! Must be at least 1.", textureId);
        assert level >= 0 : String.format("Invalid mipmap level [%d]! Level must be at least 0.", level);
        assert xOffset >= 0 : String.format("Invalid xOffset [%d]! Must be at least 0.", xOffset);
        assert width > 0 : String.format("Invalid width [%d]! Must be at least 1.", width);
        assert pixels.order() == ByteOrder.nativeOrder() : "ByteBuffer is not in native order!";
        assert pixels.isDirect() : "ByteBuffer is not direct!";
        
        ARBDirectStateAccess.glTextureSubImage1D(textureId, level, xOffset, width, format, type, pixels);

        assert GL11.glGetError() == GL11.GL_NO_ERROR : String.format("glTextureSubImage1d(%d, %d, %d, %d, %d, %d, [data]) failed!", textureId, level, xOffset, width, format, type);
    }

    @Override
    public void glTextureSubImage2d(int textureId, int level, int xOffset, int yOffset, int width, int height, int format, int type, ByteBuffer pixels) {
        assert textureId > 0 : String.format("Invalid textureId [%d]! Must be at least 1.", textureId);
        assert level >= 0 : String.format("Invalid mipmap level [%d]! Level must be at least 0.", level);
        assert xOffset >= 0 : String.format("Invalid xOffset [%d]! Must be at least 0.", xOffset);
        assert width > 0 : String.format("Invalid width [%d]! Must be at least 1.", width);
        assert yOffset >= 0 : String.format("Invalid yOffset [%d]! Must be at least 0.", yOffset);
        assert height > 0 : String.format("Invalid height [%d]! Must be at least 1.", height);
        assert pixels.order() == ByteOrder.nativeOrder() : "ByteBuffer is not in native order!";
        assert pixels.isDirect() : "ByteBuffer is not direct!";
        
        ARBDirectStateAccess.glTextureSubImage2D(textureId, level, xOffset, yOffset, width, height, format, type, pixels);

        assert GL11.glGetError() == GL11.GL_NO_ERROR : String.format("glTextureSubImage2d(%d, %d, %d, %d, %d, %d, %d, %d, [data]) failed!", textureId, level, xOffset, yOffset, width, height, format, type);
    }

    @Override
    public void glTextureSubImage3d(int textureId, int level, int xOffset, int yOffset, int zOffset, int width, int height, int depth, int format, int type, ByteBuffer pixels) {
        assert textureId > 0 : String.format("Invalid textureId [%d]! Must be at least 1.", textureId);
        assert level >= 0 : String.format("Invalid mipmap level [%d]! Level must be at least 0.", level);
        assert xOffset >= 0 : String.format("Invalid xOffset [%d]! Must be at least 0.", xOffset);
        assert width > 0 : String.format("Invalid width [%d]! Must be at least 1.", width);
        assert yOffset >= 0 : String.format("Invalid yOffset [%d]! Must be at least 0.", yOffset);
        assert height > 0 : String.format("Invalid height [%d]! Must be at least 1.", height);
        assert zOffset >= 0 : String.format("Invalid zOffset [%d]! Must be at least 0.", zOffset);
        assert depth > 0 : String.format("Invalid depth [%d]! Must be at least 1.", depth);
        assert pixels.order() == ByteOrder.nativeOrder() : "ByteBuffer is not in native order!";
        assert pixels.isDirect() : "ByteBuffer is not direct!";
        
        ARBDirectStateAccess.glTextureSubImage3D(textureId, level, xOffset, yOffset, zOffset, width, height, depth, format, type, pixels);

        assert GL11.glGetError() == GL11.GL_NO_ERROR : String.format("glTextureSubImage3d(%d, %d, %d, %d, %d, %d, %d, %d, %d, %d, [data] failed!", textureId, level, xOffset, yOffset, zOffset, width, height, depth, format, type);
    }

    @Override
    public void glBindTextureUnit(int unit, int textureId) {
        assert unit >= 0 : String.format("Invalid texture unit [%d]! Must be at least 0.", unit);
        assert textureId >= 0 : String.format("Invalid textureId [%d]! Must be at least 0.", textureId);
        
        ARBDirectStateAccess.glBindTextureUnit(unit, textureId);

        assert GL11.glGetError() == GL11.GL_NO_ERROR : String.format("glBindTextureUnit(%d, %d) failed!", unit, textureId);
    }      

    @Override
    public void glGenerateTextureMipmap(int textureId) {
        assert textureId > 0 : String.format("Invalid textureId [%d]! Must be at least 1.", textureId);
        
        ARBDirectStateAccess.glGenerateTextureMipmap(textureId);

        assert GL11.glGetError() == GL11.GL_NO_ERROR : String.format("glGenerateTextureMipmap(%d) failed!", textureId);
    }
}
