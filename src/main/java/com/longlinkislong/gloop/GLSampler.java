/* 
 * Copyright (c) 2015, longlinkislong.com
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 *
 * * Redistributions of source code must retain the above copyright notice, this
 *   list of conditions and the following disclaimer.
 * * Redistributions in binary form must reproduce the above copyright notice,
 *   this list of conditions and the following disclaimer in the documentation
 *   and/or other materials provided with the distribution.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS"
 * AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE
 * IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE
 * ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT HOLDER OR CONTRIBUTORS BE
 * LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR
 * CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF
 * SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS
 * INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN
 * CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE)
 * ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE
 * POSSIBILITY OF SUCH DAMAGE.
 */
package com.longlinkislong.gloop;

import com.longlinkislong.gloop.spi.Driver;
import com.longlinkislong.gloop.spi.Sampler;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.Marker;
import org.slf4j.MarkerFactory;

/**
 * GLSampler object overrides the texture parameter settings of GLTexture when
 * bound.
 *
 * @author zmichaels
 * @since 15.12.18
 */
public class GLSampler extends GLObject {

    private static final Marker GLOOP_MARKER = MarkerFactory.getMarker("GLOOP");
    private static final Logger LOGGER = LoggerFactory.getLogger("GLSampler");

    private volatile transient Sampler sampler;
    private final GLTextureParameters parameters;
    private final boolean isLocked;

    private String name = "";

    /**
     * Assigns a human-readable name to the GLSampler.
     *
     * @param newName the new name of the sampler.
     * @since 15.12.18
     */
    public final void setName(final CharSequence newName) {
        GLTask.create(() -> {
            LOGGER.trace(
                    GLOOP_MARKER,
                    "Renamed GLSampler[{}] to GLSampler[{}]!",
                    this.name,
                    newName);

            this.name = newName.toString();
        }).glRun(this.getThread());
    }

    /**
     * Retrieves the name of the GLSampler.
     *
     * @return the name of the sampler object.
     * @since 15.12.18
     */
    public final String getName() {
        return this.name;
    }

    private GLSampler() {
        this(GLThread.getDefaultInstance());
    }

    private GLSampler(final GLThread thread) {
        super(thread);
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

        this.parameters = Objects.requireNonNull(parameters);
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

            sampler.setName(String.format("thread=%s DEFAULT", thread.getThread().getName()));

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
        return sampler != null && sampler.isValid();
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

        @SuppressWarnings({"rawtypes", "unchecked"})
        @Override
        public void run() {
            LOGGER.trace(GLOOP_MARKER, "############### Start GLSampler Init Task ###############");

            if (GLSampler.this.isLocked) {
                throw new GLException("Cannot initialize the null instance of the GLSampler object!");
            } else if (GLSampler.this.isValid()) {
                throw new GLException("GLSampler is already initialized!");
            }

            final Driver driver = GLTools.getDriverInstance();

            sampler = driver.samplerCreate();

            driver.samplerSetParameter(sampler, 10242 /* GL_TEXTURE_WRAP_S */, GLSampler.this.parameters.wrapS.value);
            driver.samplerSetParameter(sampler, 10243 /* GL_TEXTURE_WRAP_T */, GLSampler.this.parameters.wrapT.value);
            driver.samplerSetParameter(sampler, 32882 /* GL_TEXTURE_WRAP_R */, GLSampler.this.parameters.wrapR.value);
            driver.samplerSetParameter(sampler, 33082 /* GL_TEXTURE_MIN_LOD */, GLSampler.this.parameters.minLOD);
            driver.samplerSetParameter(sampler, 33083 /* GL_TEXTURE_MAX_LOD */, GLSampler.this.parameters.maxLOD);
            driver.samplerSetParameter(sampler, 10241 /* GL_TEXTURE_MIN_FILTER */, GLSampler.this.parameters.minFilter.value);
            driver.samplerSetParameter(sampler, 10240 /* GL_TEXTURE_MAG_FILTER */, GLSampler.this.parameters.magFilter.value);
            driver.samplerSetParameter(sampler, 34046 /* GL_TEXTURE_MAX_ANISOTROPY_EXT */, GLSampler.this.parameters.anisotropicLevel);

            LOGGER.trace(
                    GLOOP_MARKER,
                    "Initialized GLSampler[{}]!",
                    GLSampler.this.getName());

            LOGGER.trace(GLOOP_MARKER, "############### End GLSampler Init Task ###############");
        }
    }

    /**
     * Binds the sampler to the specified sampler unit.
     *
     * @param unit the sampler unit to bind to.
     * @since 15.07.06
     */
    public void bind(final int unit) {
        new BindTask(unit).glRun(this.getThread());
    }

    /**
     * Binds the GLSampler object.
     *
     * @since 15.07.06
     */
    public final class BindTask extends GLTask {

        private final int unit;

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

        @SuppressWarnings("unchecked")
        @Override
        public void run() {
            LOGGER.trace(GLOOP_MARKER, "############### Start GLSampler Bind Task ###############");
            LOGGER.trace(GLOOP_MARKER, "\tBinding GLSampler[{}]", getName());
            LOGGER.trace(GLOOP_MARKER, "\tTexture unit: {}", this.unit);

            if (!GLSampler.this.isValid()) {
                throw new GLException("Invalid GLSampler!");
            }

            GLTools.getDriverInstance().samplerBind(unit, sampler);
            LOGGER.trace(GLOOP_MARKER, "############### End GLSampler Bind Task ###############");
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

        @SuppressWarnings("unchecked")
        @Override
        public void run() {
            LOGGER.trace(GLOOP_MARKER, "############### Start GLSampler Delete Task ###############");
            LOGGER.trace(GLOOP_MARKER, "\tDeleting GLSampler[{}]", GLSampler.this.getName());

            if (!GLSampler.this.isValid()) {
                throw new GLException("GLSampler is invalid!");
            } else if (GLSampler.this.isLocked) {
                throw new GLException("Cannot delete default GLSampler!");
            }

            GLTools.getDriverInstance().samplerDelete(sampler);
            sampler = null;
            LOGGER.trace(GLOOP_MARKER, "############### End GLSampler Delete Task ###############");
        }
    }

}
