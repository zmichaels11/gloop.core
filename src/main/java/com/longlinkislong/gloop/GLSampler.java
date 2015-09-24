/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.longlinkislong.gloop;

import static com.longlinkislong.gloop.GLAsserts.checkGLError;
import static com.longlinkislong.gloop.GLAsserts.glErrorMsg;
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
    private final boolean isLocked;

    private GLSampler() {
        this(GLThread.getDefaultInstance());
    }

    private GLSampler(final GLThread thread) {
        super(thread);
        this.samplerId = 0;
        this.parameters = null;
        this.isLocked = true;
    }

    /**
     * Constructs a new GLSampler object on the default OpenGL thread with the
     * specified GLTextureParameters.
     *
     * @param parameters the parameters for the sampler unit.
     * @since 15.07.06
     */
    public GLSampler(final GLTextureParameters parameters) {
        this(GLThread.getDefaultInstance(), parameters);
    }

    /**
     * Constructs a new GLSampler object on the specified OpenGL thread with the
     * specified GLTextureParameters.
     *
     * @param thread the OpenGL thread.
     * @param parameters the texture parameters.
     * @since 15.07.06
     */
    public GLSampler(final GLThread thread, final GLTextureParameters parameters) {
        super(thread);

        Objects.requireNonNull(this.parameters = parameters);
        this.isLocked = false;
    }

    private static final Map<GLThread, GLSampler> DEFAULT_SAMPLERS = new HashMap<>();

    /**
     * Retrieves the default sampler object associated with the default OpenGL
     * thread.
     *
     * @return the default sampler.
     * @since 15.07.06
     */
    public static GLSampler getDefaultSampler() {
        return getDefaultSampler(GLThread.getDefaultInstance());
    }

    /**
     * Retrieves the default sampler associated with the specified GLThread.
     *
     * @param thread the thread to check.
     * @return the sampler object.
     * @since 15.07.06
     */
    public static GLSampler getDefaultSampler(final GLThread thread) {
        if (!DEFAULT_SAMPLERS.containsKey(thread)) {
            final GLSampler sampler = new GLSampler(thread);

            DEFAULT_SAMPLERS.put(thread, sampler);
            return sampler;
        } else {
            return DEFAULT_SAMPLERS.get(thread);
        }
    }

    /**
     * Checks if the sampler object is currently valid.
     *
     * @return true if the GLSampler object has been initialized and has not yet
     * been deleted.
     * @since 15.07.06
     */
    public boolean isValid() {
        return this.samplerId != INVALID_SAMPLER_ID;
    }

    /**
     * Initializes the GLSampler unit on its associated GLThread.
     *
     * @since 15.07.06
     */
    public void init() {
        new InitTask().glRun(this.getThread());
    }

    /**
     * A GLTask that initialized the GLSampler object.
     *
     * @since 15.07.06
     */
    public class InitTask extends GLTask {

        @Override
        public void run() {
            if (GLSampler.this.isLocked) {
                throw new GLException("Cannot initialize the null instance of the GLSampler object!");
            } else if (GLSampler.this.isValid()) {
                throw new GLException("GLSampler is already initialized!");
            }

            GLSampler.this.samplerId = GL33.glGenSamplers();
            assert checkGLError() : glErrorMsg("glGenSamplers(void)");

            GL33.glSamplerParameteri(GL11.GL_TEXTURE_WRAP_S, GLSampler.this.samplerId, GLSampler.this.parameters.wrapS.value);
            assert checkGLError() : glErrorMsg("glSamplerParameteri(III)", "GL_TEXTURE_WRAP_S", GLSampler.this.samplerId, GLSampler.this.parameters.wrapS);

            GL33.glSamplerParameteri(GL11.GL_TEXTURE_WRAP_T, GLSampler.this.samplerId, GLSampler.this.parameters.wrapT.value);
            assert checkGLError() : glErrorMsg("glSamplerParameteri(III)", "GL_TEXTURE_WRAP_T", GLSampler.this.samplerId, GLSampler.this.parameters.wrapT);

            GL33.glSamplerParameteri(GL12.GL_TEXTURE_WRAP_R, GLSampler.this.samplerId, GLSampler.this.parameters.wrapR.value);
            assert checkGLError() : glErrorMsg("glSamplerParameteri(III)", "GL_TEXTURE_WRAP_R", GLSampler.this.samplerId, GLSampler.this.parameters.wrapR);

            GL33.glSamplerParameterf(GL12.GL_TEXTURE_MIN_LOD, GLSampler.this.samplerId, GLSampler.this.parameters.minLOD);
            assert checkGLError() : glErrorMsg("glSamplerParameterf(IIF)", "GL_TEXTURE_MIN_LOD", GLSampler.this.samplerId, GLSampler.this.parameters.minLOD);

            GL33.glSamplerParameterf(GL12.GL_TEXTURE_MAX_LOD, GLSampler.this.samplerId, GLSampler.this.parameters.maxLOD);
            assert checkGLError() : glErrorMsg("glSamplerParameterf(IIF)", "GL_TEXTURE_MAX_LOD", GLSampler.this.samplerId, GLSampler.this.parameters.maxLOD);

            GL33.glSamplerParameteri(GL11.GL_TEXTURE_MIN_FILTER, GLSampler.this.samplerId, GLSampler.this.parameters.minFilter.value);
            assert checkGLError() : glErrorMsg("glSamplerParameteri(III)", "GL_TEXTURE_MIN_FILTER", GLSampler.this.samplerId, GLSampler.this.parameters.minFilter);

            GL33.glSamplerParameteri(GL11.GL_TEXTURE_MAG_FILTER, GLSampler.this.samplerId, GLSampler.this.parameters.magFilter.value);
            assert checkGLError() : glErrorMsg("glSamplerParameteri(III)", "GL_TEXTURE_MAG_FILTER", GLSampler.this.samplerId, GLSampler.this.parameters.magFilter);

            GL33.glSamplerParameterf(EXTTextureFilterAnisotropic.GL_TEXTURE_MAX_ANISOTROPY_EXT, GLSampler.this.samplerId, GLSampler.this.parameters.anisotropicLevel);
            assert checkGLError() : glErrorMsg("glSamplerParameterf(IIF)", "GL_TEXTURE_MAX_ANISOTROPY_EXT", GLSampler.this.samplerId, GLSampler.this.parameters.anisotropicLevel);
        }
    }

    private BindTask bindTask = null;

    /**
     * Binds the sampler to the specified sampler unit.
     *
     * @param unit the sampler unit to bind to.
     * @since 15.07.06
     */
    public void bind(final int unit) {
        if (bindTask != null && this.bindTask.unit == unit) {
            this.bindTask.glRun(this.getThread());
        } else {
            this.bindTask = new BindTask(unit);

            this.bindTask.glRun(this.getThread());
        }
    }

    /**
     * Binds the GLSampler object.
     *
     * @since 15.07.06
     */
    public class BindTask extends GLTask {

        final int unit;

        /**
         * Constructs a new BindTask for binding the sampler object to the
         * specified sampler unit.
         *
         * @param unit the unit to bind the sampler to.
         * @since 15.07.06
         */
        public BindTask(final int unit) {
            if ((this.unit = unit) < 0) {
                throw new GLException("Invalid sampler unit!");
            }
        }

        @Override
        public void run() {
            if (!GLSampler.this.isValid()) {
                throw new GLException("Invalid GLSampler!");
            }

            GL33.glBindSampler(unit, GLSampler.this.samplerId);
            assert checkGLError() : glErrorMsg("glBindSampler(II)", unit, GLSampler.this.samplerId);
        }
    }

    /**
     * Deletes the sampler object.
     *
     * @throws GLException if the GLSampler object is not currently valid.
     * @since 15.07.06
     */
    public void delete() {
        new DeleteTask().glRun(this.getThread());
    }

    /**
     * A GLTask that deletes the sampler object.
     *
     * @since 15.07.06
     */
    public class DeleteTask extends GLTask {

        @Override
        public void run() {
            if (!GLSampler.this.isValid()) {
                throw new GLException("GLSampler is invalid!");
            } else if (GLSampler.this.isLocked) {
                throw new GLException("Cannot delete default GLSampler!");
            }

            GL33.glDeleteSamplers(GLSampler.this.samplerId);
            assert checkGLError() : glErrorMsg("glDeleteSamplers(I)", GLSampler.this.samplerId);

            GLSampler.this.samplerId = INVALID_SAMPLER_ID;
        }
    }

}
