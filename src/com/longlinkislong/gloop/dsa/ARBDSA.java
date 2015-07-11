/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.longlinkislong.gloop.dsa;

import java.nio.ByteBuffer;
import java.nio.DoubleBuffer;
import java.nio.FloatBuffer;
import org.lwjgl.opengl.ARBDirectStateAccess;
import org.lwjgl.opengl.ARBSeparateShaderObjects;
import org.lwjgl.opengl.ContextCapabilities;
import org.lwjgl.opengl.GL;
import org.lwjgl.opengl.GL11;
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
        assert GL11.glGetError() == GL11.GL_NO_ERROR : "glCreateFramebuffers failed!";
        return out;
    }
    
    @Override
    public void glTextureStorage1d(int textureId, int levels, int internalFormat, int width) {
        ARBDirectStateAccess.glTextureStorage1D(textureId, levels, internalFormat, width);
        assert GL11.glGetError() == GL11.GL_NO_ERROR : "glTextureStorage1d failed!";
    }

    @Override
    public void glTextureStorage2d(int textureId, int levels, int internalFormat, int width, int height) {                
        ARBDirectStateAccess.glTextureStorage2D(textureId, levels, internalFormat, width, height);
        assert GL11.glGetError() == GL11.GL_NO_ERROR : "glTextureStorage2d failed!";
    }

    @Override
    public void glTextureStorage3d(int textureId, int levels, int internalFormat, int width, int height, int depth) {
        ARBDirectStateAccess.glTextureStorage3D(textureId, levels, internalFormat, width, height, depth);
        assert GL11.glGetError() == GL11.GL_NO_ERROR : "glTextureStorage3d failed!";
    }

    @Override
    public void glTextureParameteri(int textureId, int pName, int val) {
        ARBDirectStateAccess.glTextureParameteri(textureId, pName, val);
        assert GL11.glGetError() == GL11.GL_NO_ERROR : "glTextureParameteri failed!";
    }

    @Override
    public void glTextureParameterf(int textureId, int pName, float val) {
        ARBDirectStateAccess.glTextureParameterf(textureId, pName, val);
        assert GL11.glGetError() == GL11.GL_NO_ERROR : "glTextureParameterf failed!";
    }

    @Override
    public int glGetNamedBufferParameteri(int bufferId, int pName) {
        final int out = ARBDirectStateAccess.glGetNamedBufferParameteri(bufferId, pName);
        assert GL11.glGetError() == GL11.GL_NO_ERROR : "glGetNamedBfuferParameteri failed!";
        return out;
    }    

    @Override
    public void glNamedFramebufferTexture(int framebuffer, int attachment, int texture, int level) {
        ARBDirectStateAccess.glNamedFramebufferTexture(framebuffer, attachment, texture, level);    
        assert GL11.glGetError() == GL11.GL_NO_ERROR : "glNamedFramebufferTexture failed!";
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
        assert GL11.glGetError() == GL11.GL_NO_ERROR : "glCreateBuffers failed!";
        return out;
    }

    @Override
    public int glCreateTextures(int target) {
        final int out = ARBDirectStateAccess.glCreateTextures(target);
        assert GL11.glGetError() == GL11.GL_NO_ERROR : "glCreateTextures failed!";
        return out;
    }

    @Override
    public void glNamedBufferData(int bufferId, long size, int usage) {
        ARBDirectStateAccess.glNamedBufferData(bufferId, size, usage);
        assert GL11.glGetError() == GL11.GL_NO_ERROR : "glNamedBufferData failed!";
    }

    @Override
    public void glNamedBufferData(int bufferId, ByteBuffer data, int usage) {
        ARBDirectStateAccess.glNamedBufferData(bufferId, data, usage);
        assert GL11.glGetError() == GL11.GL_NO_ERROR : "glNamedBufferData failed!";
    }

    @Override
    public void glNamedBufferSubData(int buffer, long offset, ByteBuffer data) {
        ARBDirectStateAccess.glNamedBufferSubData(buffer, offset, data);
        assert GL11.glGetError() == GL11.GL_NO_ERROR : "glNamedBufferSubData failed!";
    }

    @Override
    public void glNamedBufferStorage(int bufferId, ByteBuffer data, int flags) {
        ARBDirectStateAccess.glNamedBufferStorage(bufferId, data, flags);
        assert GL11.glGetError() == GL11.GL_NO_ERROR : "glNamedBufferStorage failed!";
    }

    @Override
    public void glNamedBufferStorage(int bufferId, long size, int flags) {
        ARBDirectStateAccess.glNamedBufferStorage(bufferId, size, flags);
        assert GL11.glGetError() == GL11.GL_NO_ERROR : "glNamedBufferStorage failed!";
    }

    @Override
    public void glGetNamedBufferSubData(int bufferId, long offset, ByteBuffer out) {
        ARBDirectStateAccess.glGetNamedBufferSubData(bufferId, offset, out);
        assert GL11.glGetError() == GL11.GL_NO_ERROR : "glGetNamedBufferSubData failed!";
    }

    @Override
    public ByteBuffer glMapNamedBufferRange(int bufferId, long offset, long length, int access, ByteBuffer recycled) {
        final ByteBuffer out =  ARBDirectStateAccess.glMapNamedBufferRange(bufferId, offset, length, access, recycled);
        assert GL11.glGetError() == GL11.GL_NO_ERROR : "glMapNamedBufferRange failed!";
        return out;
    }

    @Override
    public void glUnmapNamedBuffer(int bufferId) {
        ARBDirectStateAccess.glUnmapNamedBuffer(bufferId);
        assert GL11.glGetError() == GL11.GL_NO_ERROR : "glUnmapNamedBuffer failed!";
    }

    @Override
    public void glCopyNamedBufferSubData(int readBufferId, int writeBufferId, long readOffset, long writeOffset, long size) {
        ARBDirectStateAccess.glCopyNamedBufferSubData(readBufferId, writeBufferId, readOffset, writeOffset, size);
        assert GL11.glGetError() == GL11.GL_NO_ERROR : "glCopyNamedBufferSubData failed!";
    }

    @Override
    public void glProgramUniform1f(int programId, int location, float value) {
        final ContextCapabilities cap = GL.getCapabilities();

        if (cap.OpenGL41) {
            GL41.glProgramUniform1f(location, location, value);
            assert GL11.glGetError() == GL11.GL_NO_ERROR : "glProgramUniform1f failed!";
        } else if (cap.GL_ARB_separate_shader_objects) {
            ARBSeparateShaderObjects.glProgramUniform1f(programId, location, value);
            assert GL11.glGetError() == GL11.GL_NO_ERROR : "glProgramUniform1fARB failed!";
        } else {
            FakeDSA.getInstance().glProgramUniform1f(programId, location, value);
        }
    }

    @Override
    public void glProgramUniform2f(int programId, int location, float v0, float v1) {
        final ContextCapabilities cap = GL.getCapabilities();

        if (cap.OpenGL41) {
            GL41.glProgramUniform2f(location, location, v0, v1);
            assert GL11.glGetError() == GL11.GL_NO_ERROR : "glProgramUniform2f failed!";
        } else if (cap.GL_ARB_separate_shader_objects) {
            ARBSeparateShaderObjects.glProgramUniform2f(programId, location, v0, v1);
            assert GL11.glGetError() == GL11.GL_NO_ERROR : "glProgramUniform2fARB failed!";
        } else {
            FakeDSA.getInstance().glProgramUniform2f(programId, location, v0, v1);
        }
    }

    @Override
    public void glProgramUniform3f(int programId, int location, float v0, float v1, float v2) {
        final ContextCapabilities cap = GL.getCapabilities();

        if (cap.OpenGL41) {
            GL41.glProgramUniform3f(location, location, v0, v1, v2);
            assert GL11.glGetError() == GL11.GL_NO_ERROR : "glProgramUniform3f failed!";
        } else if (cap.GL_ARB_separate_shader_objects) {
            ARBSeparateShaderObjects.glProgramUniform3f(programId, location, v0, v1, v2);
            assert GL11.glGetError() == GL11.GL_NO_ERROR : "glProgramUniform3fARB failed!";
        } else {
            FakeDSA.getInstance().glProgramUniform3f(programId, location, v0, v1, v2);
        }
    }

    @Override
    public void glProgramUniform4f(int programId, int location, float v0, float v1, float v2, float v3) {
        final ContextCapabilities cap = GL.getCapabilities();

        if (cap.OpenGL41) {
            GL41.glProgramUniform4f(location, location, v0, v1, v2, v3);
            assert GL11.glGetError() == GL11.GL_NO_ERROR : "glProgramUniform4f failed!";
        } else if (cap.GL_ARB_separate_shader_objects) {
            ARBSeparateShaderObjects.glProgramUniform4f(programId, location, v0, v1, v2, v3);
            assert GL11.glGetError() == GL11.GL_NO_ERROR : "glProgramUniform4fARB failed!";
        } else {
            FakeDSA.getInstance().glProgramUniform4f(programId, location, v0, v1, v2, v3);
        }
    }

    @Override
    public void glProgramUniform1i(int programId, int location, int value) {
        final ContextCapabilities cap = GL.getCapabilities();

        if (cap.OpenGL41) {
            GL41.glProgramUniform1i(programId, location, value);
            assert GL11.glGetError() == GL11.GL_NO_ERROR : "glProgramUniform1i failed!";
        } else if (cap.GL_ARB_separate_shader_objects) {
            ARBSeparateShaderObjects.glProgramUniform1i(programId, location, value);
            assert GL11.glGetError() == GL11.GL_NO_ERROR : "glProgramUniform1iARB failed!";
        } else {
            FakeDSA.getInstance().glProgramUniform1i(programId, location, value);
        }
    }

    @Override
    public void glProgramUniform2i(int programId, int location, int v0, int v1) {
        final ContextCapabilities cap = GL.getCapabilities();

        if (cap.OpenGL41) {
            GL41.glProgramUniform2i(programId, location, v0, v1);
            assert GL11.glGetError() == GL11.GL_NO_ERROR : "glProgramUniform2i failed!";
        } else if (cap.GL_ARB_separate_shader_objects) {
            ARBSeparateShaderObjects.glProgramUniform2i(programId, location, v0, v1);
            assert GL11.glGetError() == GL11.GL_NO_ERROR : "glProgramUniform2iARB failed!";
        } else {
            FakeDSA.getInstance().glProgramUniform2i(programId, location, v0, v1);
        }
    }

    @Override
    public void glProgramUniform3i(int programId, int location, int v0, int v1, int v2) {
        final ContextCapabilities cap = GL.getCapabilities();

        if (cap.OpenGL41) {
            GL41.glProgramUniform3i(programId, location, v0, v1, v2);
            assert GL11.glGetError() == GL11.GL_NO_ERROR : "glProgramUniform3i failed!";
        } else if (cap.GL_ARB_separate_shader_objects) {
            ARBSeparateShaderObjects.glProgramUniform3i(programId, location, v0, v1, v2);
            assert GL11.glGetError() == GL11.GL_NO_ERROR : "glProgramUniform3iARB failed!";
        } else {
            FakeDSA.getInstance().glProgramUniform3i(programId, location, v0, v1, v2);
        }
    }

    @Override
    public void glProgramUniform4i(int programId, int location, int v0, int v1, int v2, int v3) {
        final ContextCapabilities cap = GL.getCapabilities();

        if (cap.OpenGL41) {
            GL41.glProgramUniform4i(programId, location, v0, v1, v2, v3);
            assert GL11.glGetError() == GL11.GL_NO_ERROR : "glProgramUniform4i failed!";
        } else if (cap.GL_ARB_separate_shader_objects) {
            ARBSeparateShaderObjects.glProgramUniform4i(programId, location, v0, v1, v2, v3);
            assert GL11.glGetError() == GL11.GL_NO_ERROR : "glProgramUniform4iARB failed!";
        } else {
            FakeDSA.getInstance().glProgramUniform4i(programId, location, v0, v1, v2, v3);
        }
    }

    @Override
    public void glProgramUniform1d(int programId, int location, double value) {
        final ContextCapabilities cap = GL.getCapabilities();

        if (cap.OpenGL41) {
            GL41.glProgramUniform1d(programId, location, value);
            assert GL11.glGetError() == GL11.GL_NO_ERROR : "glProgramUniform1d failed!";
        } else if (cap.GL_ARB_separate_shader_objects) {
            ARBSeparateShaderObjects.glProgramUniform1d(programId, location, value);
            assert GL11.glGetError() == GL11.GL_NO_ERROR : "glProgramUniform1dARB failed!";
        } else {
            FakeDSA.getInstance().glProgramUniform1d(programId, location, value);
        }
    }

    @Override
    public void glProgramUniform2d(int programId, int location, double v0, double v1) {
        final ContextCapabilities cap = GL.getCapabilities();

        if (cap.OpenGL41) {
            GL41.glProgramUniform2d(programId, location, v0, v1);
            assert GL11.glGetError() == GL11.GL_NO_ERROR : "glProgramUniform2d failed!";
        } else if (cap.GL_ARB_separate_shader_objects) {
            ARBSeparateShaderObjects.glProgramUniform2d(programId, location, v0, v1);
            assert GL11.glGetError() == GL11.GL_NO_ERROR : "glProgramUniform2dARB failed!";
        } else {
            FakeDSA.getInstance().glProgramUniform2d(programId, location, v0, v1);
        }
    }

    @Override
    public void glProgramUniform3d(int programId, int location, double v0, double v1, double v2) {
        final ContextCapabilities cap = GL.getCapabilities();

        if (cap.OpenGL41) {
            GL41.glProgramUniform3d(programId, location, v0, v1, v2);
            assert GL11.glGetError() == GL11.GL_NO_ERROR : "glProgramUniform3d failed!";
        } else if (cap.GL_ARB_separate_shader_objects) {
            ARBSeparateShaderObjects.glProgramUniform3d(programId, location, v0, v1, v2);
            assert GL11.glGetError() == GL11.GL_NO_ERROR : "glProgramUniform3dARB failed!";
        } else {
            FakeDSA.getInstance().glProgramUniform3d(programId, location, v0, v1, v2);
        }
    }

    @Override
    public void glProgramUniform4d(int programId, int location, double v0, double v1, double v2, double v3) {
        final ContextCapabilities cap = GL.getCapabilities();

        if (cap.OpenGL41) {
            GL41.glProgramUniform4d(programId, location, v0, v1, v2, v3);
            assert GL11.glGetError() == GL11.GL_NO_ERROR : "glProgramUniform4d failed!";
        } else if (cap.GL_ARB_separate_shader_objects) {
            ARBSeparateShaderObjects.glProgramUniform4d(programId, location, v0, v1, v2, v3);
            assert GL11.glGetError() == GL11.GL_NO_ERROR : "glProgramUniform4dARB failed!";
        } else {
            FakeDSA.getInstance().glProgramUniform4d(programId, location, v0, v1, v2, v3);
        }
    }

    @Override
    public void glProgramUniformMatrix2f(int programId, int location, boolean needsTranspose, FloatBuffer data) {
        final ContextCapabilities cap = GL.getCapabilities();

        if (cap.OpenGL41) {
            GL41.glProgramUniformMatrix2fv(programId, location, needsTranspose, data);
            assert GL11.glGetError() == GL11.GL_NO_ERROR : "glProgramUniformMatrix2d failed!";
        } else if(cap.GL_ARB_separate_shader_objects) {
            ARBSeparateShaderObjects.glProgramUniformMatrix2fv(programId, location, needsTranspose, data);
            assert GL11.glGetError() == GL11.GL_NO_ERROR : "glProgramUniformMatrix2fARB failed!";
        } else {
            FakeDSA.getInstance().glProgramUniformMatrix2f(programId, location, needsTranspose, data);
        }
    }

    @Override
    public void glProgramUniformMatrix3f(int programId, int location, boolean needsTranspose, FloatBuffer data) {
        final ContextCapabilities cap = GL.getCapabilities();

        if (cap.OpenGL41) {
            GL41.glProgramUniformMatrix3fv(programId, location, needsTranspose, data);
            assert GL11.glGetError() == GL11.GL_NO_ERROR : "glProgramUniformMatrix3f failed!";
        } else if(cap.GL_ARB_separate_shader_objects) {
            ARBSeparateShaderObjects.glProgramUniformMatrix3fv(programId, location, needsTranspose, data);
            assert GL11.glGetError() == GL11.GL_NO_ERROR : "glProgramUniformMatrix3fARB failed!";
        } else {
            FakeDSA.getInstance().glProgramUniformMatrix3f(programId, location, needsTranspose, data);
        }
    }

    @Override
    public void glProgramUniformMatrix4f(int programId, int location, boolean needsTranspose, FloatBuffer data) {
        final ContextCapabilities cap = GL.getCapabilities();

        if (cap.OpenGL41) {
            GL41.glProgramUniformMatrix4fv(programId, location, needsTranspose, data);
            assert GL11.glGetError() == GL11.GL_NO_ERROR : "glProgramUniformMatrix4f failed!";
        } else if(cap.GL_ARB_separate_shader_objects) {
            ARBSeparateShaderObjects.glProgramUniformMatrix4fv(programId, location, needsTranspose, data);
            assert GL11.glGetError() == GL11.GL_NO_ERROR : "glProgramUniformMatrix4fARB failed!";
        } else {
            FakeDSA.getInstance().glProgramUniformMatrix4f(programId, location, needsTranspose, data);
        }
    }

    @Override
    public void glProgramUniformMatrix2d(int programId, int location, boolean needsTranspose, DoubleBuffer data) {
        final ContextCapabilities cap = GL.getCapabilities();

        if (cap.OpenGL41) {
            GL41.glProgramUniformMatrix2dv(programId, location, needsTranspose, data);
            assert GL11.glGetError() == GL11.GL_NO_ERROR : "glProgramUniformMatrix2d failed!";
        } else if(cap.GL_ARB_separate_shader_objects) {
            ARBSeparateShaderObjects.glProgramUniformMatrix2dv(programId, location, needsTranspose, data);
            assert GL11.glGetError() == GL11.GL_NO_ERROR : "glProgramUniformMatrix2dARB failed!";            
        } else {
            FakeDSA.getInstance().glProgramUniformMatrix2d(programId, location, needsTranspose, data);
        }
    }

    @Override
    public void glProgramUniformMatrix3d(int programId, int location, boolean needsTranspose, DoubleBuffer data) {
        final ContextCapabilities cap = GL.getCapabilities();

        if (cap.OpenGL41) {
            GL41.glProgramUniformMatrix3dv(programId, location, needsTranspose, data);
            assert GL11.glGetError() == GL11.GL_NO_ERROR : "glProgramUniformMatrix3d failed!";
        } else if(cap.GL_ARB_separate_shader_objects) {
            ARBSeparateShaderObjects.glProgramUniformMatrix3dv(programId, location, needsTranspose, data);
            assert GL11.glGetError() == GL11.GL_NO_ERROR : "glProgramUniformMatrix3dARB failed!";            
        } else {
            FakeDSA.getInstance().glProgramUniformMatrix3d(programId, location, needsTranspose, data);
        }
    }

    @Override
    public void glProgramUniformMatrix4d(int programId, int location, boolean needsTranspose, DoubleBuffer data) {
        final ContextCapabilities cap = GL.getCapabilities();

        if (cap.OpenGL41) {
            GL41.glProgramUniformMatrix4dv(programId, location, needsTranspose, data);
            assert GL11.glGetError() == GL11.GL_NO_ERROR : "glProgramUniformMatrix4d failed!";
        } else if(cap.GL_ARB_separate_shader_objects) {
            ARBSeparateShaderObjects.glProgramUniformMatrix4dv(programId, location, needsTranspose, data);
            assert GL11.glGetError() == GL11.GL_NO_ERROR : "glProgramUniformMatrix4dARB failed!";
        } else {
            FakeDSA.getInstance().glProgramUniformMatrix4d(programId, location, needsTranspose, data);
        }
    }

    @Override
    public void glTextureSubImage1d(int textureId, int level, int xOffset, int width, int format, int type, ByteBuffer pixels) {
        ARBDirectStateAccess.glTextureSubImage1D(textureId, level, xOffset, width, format, type, pixels);
        
        assert GL11.glGetError() == GL11.GL_NO_ERROR : "glTextureSubImage1d failed!";
    }

    @Override
    public void glTextureSubImage2d(int textureId, int level, int xOffset, int yOffset, int width, int height, int format, int type, ByteBuffer pixels) {
        ARBDirectStateAccess.glTextureSubImage2D(textureId, level, xOffset, yOffset, width, height, format, type, pixels);
        
        assert GL11.glGetError() == GL11.GL_NO_ERROR : "glTextureSubImage2d failed!";
    }

    @Override
    public void glTextureSubImage3d(int textureId, int level, int xOffset, int yOffset, int zOffset, int width, int height, int depth, int format, int type, ByteBuffer pixels) {
        ARBDirectStateAccess.glTextureSubImage3D(textureId, level, xOffset, yOffset, zOffset, width, height, depth, format, type, pixels);
        
        assert GL11.glGetError() == GL11.GL_NO_ERROR : "glTextureSubImage3d failed!";
    }

    @Override
    public void glBindTextureUnit(int unit, int textureId) {
        ARBDirectStateAccess.glBindTextureUnit(unit, textureId);

        assert GL11.glGetError() == GL11.GL_NO_ERROR : String.format("glBindTextureUnit(%d, %d) failed!", unit, textureId);
    }    

    @Override
    public void glGenerateTextureMipmap(int textureId) {
        ARBDirectStateAccess.glGenerateTextureMipmap(textureId);
        
        assert GL11.glGetError() == GL11.GL_NO_ERROR : String.format("glGenerateTextureMipmap(%d) failed!", textureId);
    }
}
