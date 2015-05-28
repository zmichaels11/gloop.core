/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.longlinkislong.gloop;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import org.lwjgl.opengl.EXTTextureFilterAnisotropic;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL12;
import org.lwjgl.opengl.GL33;

/**
 *
 * @author zmichaels
 */
public class GLSampler extends GLObject {

    private static final int INVALID_SAMPLER_ID = -1;
    private int samplerId = INVALID_SAMPLER_ID;
    private final GLTextureParameters parameters;

    private GLSampler() {
        super();
        this.samplerId = 0;
        this.parameters = null;
    }

    private GLSampler(final GLThread thread) {
        super(thread);
        this.samplerId = 0;
        this.parameters = null;
    }

    public GLSampler(final GLTextureParameters parameters) {
        this.parameters = parameters;
    }

    public GLSampler(final GLThread thread, final GLTextureParameters parameters) {
        super(thread);

        Objects.requireNonNull(this.parameters = parameters);
    }

    private static final Map<GLThread, GLSampler> DEFAULT_SAMPLERS = new HashMap<>();

    public static GLSampler getDefaultSampler() {
        return getDefaultSampler(GLThread.getDefaultInstance());
    }

    public static GLSampler getDefaultSampler(final GLThread thread) {
        if (!DEFAULT_SAMPLERS.containsKey(thread)) {
            final GLSampler sampler = new GLSampler(thread);

            DEFAULT_SAMPLERS.put(thread, sampler);
            return sampler;
        } else {
            return DEFAULT_SAMPLERS.get(thread);
        }
    }

    public boolean isValid() {
        return this.samplerId != INVALID_SAMPLER_ID;
    }       
    
    public void init() {
        new InitTask().glRun(this.getThread());
    }

    public class InitTask extends GLTask {

        @Override
        public void run() {
            if (GLSampler.this.isValid()) {
                throw new GLException("GLSampler is already initialized!");
            }

            GLSampler.this.samplerId = GL33.glGenSamplers();
            GL33.glSamplerParameteri(
                    GL11.GL_TEXTURE_WRAP_S,
                    GLSampler.this.samplerId,
                    GLSampler.this.parameters.wrapS.value);
            GL33.glSamplerParameteri(
                    GL11.GL_TEXTURE_WRAP_T,
                    GLSampler.this.samplerId,
                    GLSampler.this.parameters.wrapT.value);
            GL33.glSamplerParameteri(
                    GL12.GL_TEXTURE_WRAP_R,
                    GLSampler.this.samplerId,
                    GLSampler.this.parameters.wrapR.value);
            GL33.glSamplerParameterf(
                    GL12.GL_TEXTURE_MIN_LOD,
                    GLSampler.this.samplerId,
                    GLSampler.this.parameters.minLOD);
            GL33.glSamplerParameterf(
                    GL12.GL_TEXTURE_MAX_LOD,
                    GLSampler.this.samplerId,
                    GLSampler.this.parameters.maxLOD);
            GL33.glSamplerParameteri(
                    GL11.GL_TEXTURE_MIN_FILTER,
                    GLSampler.this.samplerId,
                    GLSampler.this.parameters.minFilter.value);
            GL33.glSamplerParameteri(
                    GL11.GL_TEXTURE_MAG_FILTER,
                    GLSampler.this.samplerId,
                    GLSampler.this.parameters.magFilter.value);
            GL33.glSamplerParameterf(
                    EXTTextureFilterAnisotropic.GL_TEXTURE_MAX_ANISOTROPY_EXT,
                    GLSampler.this.samplerId,
                    GLSampler.this.parameters.anisotropicLevel);
        }
    }

    private BindTask bindTask = null;

    public void bind(final int unit) {
        if (bindTask != null && this.bindTask.unit == unit) {
            this.bindTask.glRun(this.getThread());
        } else {
            this.bindTask = new BindTask(unit);
            
            this.bindTask.glRun(this.getThread());
        }
    }

    public class BindTask extends GLTask {

        final int unit;

        public BindTask(final int unit) {
            this.unit = unit;
        }

        @Override
        public void run() {
            if (!GLSampler.this.isValid()) {
                throw new GLException("Invalid GLSampler!");
            }

            GL33.glBindSampler(unit, GLSampler.this.samplerId);
        }
    }

    public void delete() {
        new DeleteTask().glRun(this.getThread());
    }
    
    public class DeleteTask extends GLTask {

        @Override
        public void run() {
            if (!GLSampler.this.isValid()) {
                throw new GLException("GLSampler is invalid!");
            }

            if (GLSampler.this.samplerId == 0) {
                throw new GLException("Cannot delete default GLSampler!");
            }

            GL33.glDeleteSamplers(GLSampler.this.samplerId);
            GLSampler.this.samplerId = INVALID_SAMPLER_ID;
        }
    }

}
