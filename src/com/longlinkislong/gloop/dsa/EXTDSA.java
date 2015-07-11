/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.longlinkislong.gloop.dsa;

import com.longlinkislong.gloop.GLErrorType;
import java.nio.ByteBuffer;
import java.nio.DoubleBuffer;
import java.nio.FloatBuffer;
import org.lwjgl.opengl.ARBBufferStorage;
import org.lwjgl.opengl.ARBDirectStateAccess;
import org.lwjgl.opengl.ARBSeparateShaderObjects;
import org.lwjgl.opengl.ContextCapabilities;
import org.lwjgl.opengl.EXTDirectStateAccess;
import org.lwjgl.opengl.GL;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL12;
import org.lwjgl.opengl.GL41;
import org.lwjgl.opengl.GL42;

/**
 *
 * @author zmichaels
 */
public class EXTDSA implements EXTDSADriver {

    @Override
    public String toString() {
        return "EXTDSA";
    }

    @Override
    public boolean isSupported() {
        final ContextCapabilities cap = GL.getCapabilities();

        return cap.GL_EXT_direct_state_access && FakeDSA.getInstance().isSupported();
    }

    @Override
    public void glNamedBufferData(int bufferId, long size, int usage) {
        EXTDirectStateAccess.glNamedBufferDataEXT(bufferId, size, usage);
        assert GL11.glGetError() == GL11.GL_NO_ERROR : String.format("glNamedBufferDataEXT(%d, %d, %d) failed!", bufferId, size, usage);
    }

    @Override
    public void glNamedBufferData(int bufferId, ByteBuffer data, int usage) {
        EXTDirectStateAccess.glNamedBufferDataEXT(bufferId, data, usage);
        assert GL11.glGetError() == GL11.GL_NO_ERROR : String.format("glNamedBufferDataEXT(%d, [data], %d) failed!", bufferId, usage);
    }

    @Override
    public void glNamedBufferSubData(int buffer, long offset, ByteBuffer data) {
        EXTDirectStateAccess.glNamedBufferSubDataEXT(buffer, offset, data);
        assert GL11.glGetError() == GL11.GL_NO_ERROR : String.format("glNamedBufferSubDataEXT(%d, %d, [data]) failed!", buffer, offset);
    }

    @Override
    public void glGetNamedBufferSubData(int buffer, long offset, ByteBuffer out) {
        EXTDirectStateAccess.glGetNamedBufferSubDataEXT(buffer, offset, out);
        assert GL11.glGetError() == GL11.GL_NO_ERROR : String.format("glGetNamedBufferSubDataEXT(%d, %d, [data]) failed!", buffer, offset);
    }

    @Override
    public int glGetNamedBufferParameteri(int bufferId, int pName) {
        final int rVal = EXTDirectStateAccess.glGetNamedBufferParameteriEXT(bufferId, pName);
        assert GL11.glGetError() == GL11.GL_NO_ERROR : String.format("glGetNamedBufferParameteriEXT(%d, %d) = %d failed!", bufferId, pName, rVal);
        return rVal;
    }

    @Override
    public ByteBuffer glMapNamedBufferRange(int bufferId, long offset, long length, int access, ByteBuffer recycled) {
        final ByteBuffer out = EXTDirectStateAccess.glMapNamedBufferRangeEXT(bufferId, offset, length, access, recycled);

        assert GL11.glGetError() == GL11.GL_NO_ERROR : String.format("glMapNamedBufferRangeEXT(%d, %d, %d, %d, [recycled]) = [data] failed!", bufferId, offset, length, access);
        return out;
    }

    @Override
    public void glUnmapNamedBuffer(int bufferId) {
        EXTDirectStateAccess.glUnmapNamedBufferEXT(bufferId);
        assert GL11.glGetError() == GL11.GL_NO_ERROR : String.format("glUnmapNamedBufferEXT(%d) failed!", bufferId);
    }

    @Override
    public void glCopyNamedBufferSubData(int readBufferId, int writeBufferId, long readOffset, long writeOffset, long size) {
        EXTDirectStateAccess.glNamedCopyBufferSubDataEXT(readBufferId, writeBufferId, readOffset, writeOffset, size);
        assert GL11.glGetError() == GL11.GL_NO_ERROR : String.format("glCopyNamedBufferSubDataEXT(%d, %d, %d, %d, %d) failed!", readBufferId, writeBufferId, readOffset, writeOffset, size);
    }

    @Override
    public void glProgramUniform1f(int programId, int location, float value) {
        EXTDirectStateAccess.glProgramUniform1fEXT(programId, location, value);
        assert GL11.glGetError() == GL11.GL_NO_ERROR : String.format("glProgramUniform1fEXT(%d, %d, %f) failed!", programId, location, value);
    }

    @Override
    public void glProgramUniform2f(int programId, int location, float v0, float v1) {
        EXTDirectStateAccess.glProgramUniform2fEXT(programId, location, v0, v1);
        assert GL11.glGetError() == GL11.GL_NO_ERROR : String.format("glProgramuniform2fEXT(%d, %d, %f, %f) failed!", programId, location, v0, v1);
    }

    @Override
    public void glProgramUniform3f(int programId, int location, float v0, float v1, float v2) {
        EXTDirectStateAccess.glProgramUniform3fEXT(programId, location, v0, v1, v2);
        assert GL11.glGetError() == GL11.GL_NO_ERROR : String.format("glProgramuniform3fEXT(%d, %d, %f, %f, %f) failed!", programId, location, v0, v1, v2);
    }

    @Override
    public void glProgramUniform4f(int programId, int location, float v0, float v1, float v2, float v3) {
        EXTDirectStateAccess.glProgramUniform4fEXT(programId, location, v0, v1, v2, v3);
        assert GL11.glGetError() == GL11.GL_NO_ERROR : String.format("glProgramuniform3fEXT(%d, %d, %f, %f, %f, %f) failed!", programId, location, v0, v1, v2, v3);
    }

    @Override
    public void glProgramUniform1i(int programId, int location, int value) {
        EXTDirectStateAccess.glProgramUniform1iEXT(programId, location, value);
        assert GL11.glGetError() == GL11.GL_NO_ERROR : String.format("glProgramUniform1dEXT(%d, %d, %d) failed!", programId, location, value);
    }

    @Override
    public void glProgramUniform2i(int programId, int location, int v0, int v1) {
        EXTDirectStateAccess.glProgramUniform2iEXT(programId, location, v0, v1);
        assert GL11.glGetError() == GL11.GL_NO_ERROR : String.format("glProgramUniform2dEXT(%d, %d, %d, %d) failed!", programId, location, v0, v1);
    }

    @Override
    public void glProgramUniform3i(int programId, int location, int v0, int v1, int v2) {
        EXTDirectStateAccess.glProgramUniform3iEXT(programId, location, v0, v1, v2);
        assert GL11.glGetError() == GL11.GL_NO_ERROR : String.format("glProgramUniform3dEXT(%d, %d, %d, %d, %d) failed!", programId, location, v0, v1, v2);
    }

    @Override
    public void glProgramUniform4i(int programId, int location, int v0, int v1, int v2, int v3) {
        EXTDirectStateAccess.glProgramUniform4iEXT(programId, location, v0, v1, v2, v3);
        assert GL11.glGetError() == GL11.GL_NO_ERROR : String.format("glProgramUniform2dEXT(%d, %d, %d, %d, %d) failed!", programId, location, v0, v1, v2, v3);
    }

    @Override
    public void glProgramUniformMatrix2f(int programId, int location, boolean needsTranspose, FloatBuffer data) {
        EXTDirectStateAccess.glProgramUniformMatrix2fvEXT(programId, location, needsTranspose, data);
        assert GL11.glGetError() == GL11.GL_NO_ERROR : String.format("glProgramUniformMatrix2fvEXT(%d, %d, %s, [data]) failed!", programId, location, needsTranspose);
    }

    @Override
    public void glProgramUniformMatrix3f(int programId, int location, boolean needsTranspose, FloatBuffer data) {
        EXTDirectStateAccess.glProgramUniformMatrix3fvEXT(programId, location, needsTranspose, data);
        assert GL11.glGetError() == GL11.GL_NO_ERROR : String.format("glProgramUniformMatrix3fvEXT(%d, %d, %s, [data]) failed!", programId, location, needsTranspose);
    }

    @Override
    public void glProgramUniformMatrix4f(int programId, int location, boolean needsTranspose, FloatBuffer data) {
        EXTDirectStateAccess.glProgramUniformMatrix4fvEXT(programId, location, needsTranspose, data);
        assert GL11.glGetError() == GL11.GL_NO_ERROR : String.format("glProgramUniformMatrix4fvEXT(%d, %d, %s, [data]) failed!", programId, location, needsTranspose);
    }

    @Override
    public void glTextureParameteri(int textureId, int target, int pName, int val) {
        EXTDirectStateAccess.glTextureParameteriEXT(textureId, target, pName, val);
        assert GL11.glGetError() == GL11.GL_NO_ERROR : String.format("glTextureParameteriEXT(%d, %d, %d, %d) failed!", textureId, target, pName, val);
    }

    @Override
    public void glTextureParameterf(int textureId, int target, int pName, float val) {
        EXTDirectStateAccess.glTextureParameterfEXT(textureId, target, pName, val);

        assert GL11.glGetError() == GL11.GL_NO_ERROR : String.format("glTextureParameterfEXT(%d, %d, %d, %f) failed!", textureId, target, pName, val);
    }

    @Override
    public void glTextureSubImage1d(int textureId, int level, int xOffset, int width, int format, int type, ByteBuffer pixels) {
        EXTDirectStateAccess.glTextureSubImage1DEXT(
                textureId, GL11.GL_TEXTURE_1D,
                level, xOffset, width,
                format, type, pixels);

        assert GL11.glGetError() == GL11.GL_NO_ERROR : String.format("glTextureSubImage1dEXT(%d, GL_TEXTURE_1D, %d, %d, %d, %d, [data]) failed!",
                textureId, level, xOffset, width, format, type, pixels);
    }

    @Override
    public void glTextureSubImage2d(int textureId, int level, int xOffset, int yOffset, int width, int height, int format, int type, ByteBuffer pixels) {
        EXTDirectStateAccess.glTextureSubImage2DEXT(
                textureId, GL11.GL_TEXTURE_2D,
                level, xOffset, yOffset,
                width, height, format, type, pixels);

        assert GL11.glGetError() == GL11.GL_NO_ERROR : String.format("glTextureSubImage2dEXT(%d, GL_TEXTURE_2D, %d, %d, %d, %d, %d, %d, %d, %d, [data]) failed!",
                textureId, level, xOffset, yOffset, width, height, format, type, pixels);
    }

    @Override
    public void glTextureSubImage3d(int textureId, int level, int xOffset, int yOffset, int zOffset, int width, int height, int depth, int format, int type, ByteBuffer pixels) {
        EXTDirectStateAccess.glTextureSubImage3DEXT(
                textureId, GL12.GL_TEXTURE_3D,
                level, xOffset, yOffset, zOffset,
                width, height, depth, format, type, pixels);

        assert GL11.glGetError() == GL11.GL_NO_ERROR : String.format("glTextureSubImage3dEXT(%d, GL_TEXTURE_3D, %d, %d, %d, %d, %d, %d, %d, %d, %d, [data]) failed!",
                textureId, level, xOffset, yOffset, zOffset, width, height, depth, format, type);
    }

    @Override
    public void glBindTextureUnit(int unit, int target, int textureId) {
        EXTDirectStateAccess.glBindMultiTextureEXT(unit, target, textureId);

        assert GL11.glGetError() == GL11.GL_NO_ERROR : String.format("glBindMultiTextureEXT(%d, %d, %d) failed!", unit, target, textureId);
    }

    @Override
    public void glTextureImage1d(int texture, int target, int level, int internalFormat, int width, int border, int format, int type, ByteBuffer pixels) {
        EXTDirectStateAccess.glTextureImage1DEXT(texture, target, level, internalFormat, width, border, format, type, pixels);

        assert GL11.glGetError() == GL11.GL_NO_ERROR : String.format("glTextureImage1d(%d, %d, %d, %d, %d, %d, %d, %d, [data]) failed!",
                texture, target, level, internalFormat, width, border, format, type);
    }

    @Override
    public void glTextureImage2d(int texture, int target, int level, int internalFormat, int width, int height, int border, int format, int type, ByteBuffer pixels) {
        EXTDirectStateAccess.glTextureImage2DEXT(texture, target, level, internalFormat, width, height, format, border, type, pixels);

        assert GL11.glGetError() == GL11.GL_NO_ERROR : String.format("glTextureImage2d(%d, %d, %d, %d, %d, %d, %d, %d, %d, [data]) failed!",
                texture, target, level, internalFormat, width, height, border, format, type);
    }

    @Override
    public void glTextureImage3d(int texture, int target, int level, int internalFormat, int width, int height, int depth, int border, int format, int type, ByteBuffer pixels) {
        EXTDirectStateAccess.glTextureImage3DEXT(texture, target, level, internalFormat, width, height, depth, border, format, type, pixels);

        assert GL11.glGetError() == GL11.GL_NO_ERROR : String.format("glTextureImage3d(%d, %d, %d, %d, %d, %d, %d, %d, %d, %d, [data]) failed!",
                texture, target, level, internalFormat, width, height, depth, border, format, type);
    }

    @Override
    public void glTextureImage1d(int texture, int target, int level, int internalFormat, int width, int border, int format, int type, long ptr) {
        EXTDirectStateAccess.glTextureImage1DEXT(texture, target, level, internalFormat, width, border, format, type, ptr);

        assert GL11.glGetError() == GL11.GL_NO_ERROR : String.format("glTextureImage1d(%d, %d, %d, %d, %d, %d, %d, %d, %d) failed!",
                texture, target, level, internalFormat, width, border, format, type, ptr);
    }

    @Override
    public void glTextureImage2d(int texture, int target, int level, int internalFormat, int width, int height, int border, int format, int type, long ptr) {
        EXTDirectStateAccess.glTextureImage2DEXT(texture, target, level, internalFormat, width, height, border, format, type, ptr);
        GLErrorType.getGLError().ifPresent(err -> {
            throw err.toGLException();
        });
        assert GL11.glGetError() == GL11.GL_NO_ERROR : String.format("glTextureImage2DEXT(%d, %d, %d, %d, %d, %d, %d, %d, %d, %d) failed!", texture, target, level, internalFormat, width, height, format, border, type, ptr);
    }

    @Override
    public void glTextureImage3d(int texture, int target, int level, int internalFormat, int width, int height, int depth, int border, int format, int type, long ptr) {
        EXTDirectStateAccess.glTextureImage3DEXT(texture, target, level, internalFormat, width, height, depth, border, format, type, ptr);
        assert GL11.glGetError() == GL11.GL_NO_ERROR : String.format("glTextureImage1d(%d, %d, %d, %d, %d, %d, %d, %d, %d, %d, %d) failed!",
                texture, target, level, internalFormat, width, height, depth, border, format, type, ptr);
    }

    @Override
    public void glGenerateTextureMipmap(int texture, int target) {
        EXTDirectStateAccess.glGenerateTextureMipmapEXT(texture, target);
        assert GL11.glGetError() == GL11.GL_NO_ERROR : String.format("glGenerateTextureMipmap(%d, %d) failed!", texture, target);
    }

    public static DSADriver getInstance() {
        return Holder.INSTANCE;
    }

    @Override
    public int glCreateBuffers() {
        return FakeDSA.getInstance().glCreateBuffers();
    }

    @Override
    public int glCreateTextures(int target) {
        return FakeDSA.getInstance().glCreateTextures(target);
    }

    @Override
    public int glCreateFramebuffers() {
        return FakeDSA.getInstance().glCreateFramebuffers();
    }

    @Override
    public void glNamedBufferStorage(int bufferId, ByteBuffer data, int flags) {
        final ContextCapabilities cap = GL.getCapabilities();

        if (cap.GL_ARB_buffer_storage) {
            ARBBufferStorage.glNamedBufferStorageEXT(bufferId, data, flags);
            assert GL11.glGetError() == GL11.GL_NO_ERROR : String.format("glNamedBufferStorageEXT(%d, [data], %d) failed!", bufferId, flags);
        } else {
            FakeDSA.getInstance().glNamedBufferStorage(bufferId, data, flags);
        }
    }

    @Override
    public void glNamedBufferStorage(int bufferId, long size, int flags) {
        final ContextCapabilities cap = GL.getCapabilities();

        if (cap.GL_ARB_buffer_storage) {
            ARBBufferStorage.glNamedBufferStorageEXT(bufferId, size, flags);
            assert GL11.glGetError() == GL11.GL_NO_ERROR : String.format("glNamedBufferStorageEXT(%d, %d, %d) failed!", bufferId, size, flags);
        } else {
            FakeDSA.getInstance().glNamedBufferStorage(bufferId, size, flags);
        }
    }

    @Override
    public void glProgramUniform1d(int programId, int location, double value) {
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
    public void glProgramUniformMatrix2d(int programId, int location, boolean needsTranspose, DoubleBuffer data) {
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
    public void glNamedFramebufferTexture1D(int framebuffer, int attachment, int texTarget, int texture, int level) {
        EXTDirectStateAccess.glNamedFramebufferTexture1DEXT(framebuffer, attachment, texTarget, texture, level);
        assert GL11.glGetError() == GL11.GL_NO_ERROR : String.format("glNamedFramebufferTexture1D(%d, %d, %d, %d, %d) failed!", framebuffer, attachment, texTarget, texture, level);
    }

    @Override
    public void glNamedFramebufferTexture2D(int framebuffer, int attachment, int texTarget, int texture, int level) {
        EXTDirectStateAccess.glNamedFramebufferTexture2DEXT(framebuffer, attachment, texTarget, texture, level);
        assert GL11.glGetError() == GL11.GL_NO_ERROR : String.format("glNamedFramebufferTexture2D(%d, %d, %d, %d, %d) failed!", framebuffer, attachment, texTarget, texture, level);
    }

    @Override
    public void glNamedFramebufferTexture(int framebuffer, int attachment, int texture, int level) {
        EXTDirectStateAccess.glNamedFramebufferTextureEXT(framebuffer, attachment, texture, level);
        assert GL11.glGetError() == GL11.GL_NO_ERROR : String.format("glNamedFramebufferTextureEXT(%d, %d, %d, %d) failed!", framebuffer, attachment, texture, level);
    }    

    private static class Holder {

        private static final DSADriver INSTANCE = new EXTDSA();
    }
}
