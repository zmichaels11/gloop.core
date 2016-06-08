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

import java.util.Objects;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.Marker;
import org.slf4j.MarkerFactory;

/**
 * GLDepthTest is a GLObject that controls depth test parameters.
 *
 * @author zmichaels
 * @since 15.06.18
 */
public class GLDepthTest extends GLObject {

    private static final Marker GLOOP_MARKER = MarkerFactory.getMarker("GLOOP");
    private static final Logger LOGGER = LoggerFactory.getLogger("GLDepthTest");

    public static final GLDepthFunc DEFAULT_DEPTH_FUNC = GLDepthFunc.GL_LESS;
    public final GLEnableStatus depthTestEnabled;
    public final GLDepthFunc depthFunc;

    private volatile String name = "id=" + System.currentTimeMillis();

    /**
     * Assigns a human-readable name to the GLDepthTest.
     *
     * @param newName the new name.
     * @since 15.12.18
     */
    public final void setName(final CharSequence newName) {
        GLTask.create(() -> {
            LOGGER.trace(GLOOP_MARKER,
                    "Renamed GLDepthTest[{}] to GLDepthTest[{}]!",
                    this.name,
                    newName);

            this.name = newName.toString();
        }).glRun(this.getThread());
    }

    /**
     * Retrieves the name of the GLDepthTest.
     *
     * @return the name.
     * @since 15.12.18
     */
    public final String getName() {
        return this.name;
    }

    /**
     * Constructs a new GLDepthTest object on the default OpenGL thread.
     *
     * @since 15.06.18
     */
    public GLDepthTest() {
        this(GLThread.getAny());
    }

    /**
     * Constructs a new GLDepthTest object on the specified OpenGL thread.
     *
     * @param thread the OpenGL thread to create the object on.
     * @since 15.06.18
     */
    public GLDepthTest(final GLThread thread) {
        this(thread, GLEnableStatus.GL_DISABLED, DEFAULT_DEPTH_FUNC);
    }

    /**
     * Constructs a new GLDepthTest object on the specified OpenGL thread with
     * the specified parameter values.
     *
     * @param thread the OpenGL thread to create the GLDepthTest object on.
     * @param enabled the enabled status.
     * @param depthFunc the depth function to perform for testing.
     * @since 15.06.18
     */
    public GLDepthTest(
            final GLThread thread,
            final GLEnableStatus enabled, final GLDepthFunc depthFunc) {

        super(thread);

        LOGGER.trace(
                GLOOP_MARKER,
                "Constructed GLDepthTest on thread: {}",
                thread);

        this.depthTestEnabled = Objects.requireNonNull(enabled);
        this.depthFunc = depthFunc;
    }

    /**
     * Copies the GLDepthTest object onto the specified OpenGL thread.
     *
     * @param thread the thread to copy the object to.
     * @return the GLDepthTest object.
     * @since 15.07.01
     */
    public GLDepthTest withGLThread(final GLThread thread) {
        return new GLDepthTest(thread, this.depthTestEnabled, this.depthFunc);
    }

    /**
     * Copies the GLDepthTest object and overrides the enabled parameter.
     *
     * @param isEnabled the new enabled parameter.
     * @return the GLDepthTest object.
     * @since 15.06.18
     */
    public GLDepthTest withEnabled(final GLEnableStatus isEnabled) {
        return new GLDepthTest(this.getThread(), isEnabled, this.depthFunc);
    }

    /**
     * Copies the GLDepthTest object and overrides the depth function parameter.
     *
     * @param func the new depth function parameter.
     * @return the GLDepthTest object.
     * @since 15.06.18
     */
    public GLDepthTest withDepthFunc(final GLDepthFunc func) {
        return new GLDepthTest(this.getThread(), this.depthTestEnabled, func);
    }

    /**
     * Applies the depth function to the associated OpenGL thread.
     *
     * @since 15.06.18
     */
    public void applyDepthFunc() {
        new ApplyDepthFuncTask().glRun(this.getThread());
    }

    /**
     * A GLTask that applies the depth function.
     *
     * @since 15.06.18
     */
    public class ApplyDepthFuncTask extends GLTask {

        @Override
        public void run() {
            LOGGER.trace(GLOOP_MARKER, "############### Start GLDepthTest Apply Depth Func Task ###############");
            LOGGER.trace(GLOOP_MARKER, "\tApplying GLDepthTest[{}]", GLDepthTest.this.getName());
            LOGGER.trace(GLOOP_MARKER, "\tEnabled: {} Function: {}", GLDepthTest.this.depthTestEnabled, GLDepthTest.this.depthFunc);                        

            final GLThread thread = GLThread.getCurrent().orElseThrow(GLException::new);            

            thread.runOnDepthTestChangeCallback(thread.currentDepthTest, GLDepthTest.this);
            thread.currentDepthTest = GLDepthTest.this.withGLThread(thread);

            switch (GLDepthTest.this.depthTestEnabled) {
                case GL_ENABLED:
                    GLTools.getDriverInstance().depthTestEnable(depthFunc.value);                    
                    break;
                case GL_DISABLED:
                    GLTools.getDriverInstance().depthTestDisable();                    
                    break;
            }

            LOGGER.trace(GLOOP_MARKER, "############### End GLDepthTest Apply Depth Func Task ###############");
        }
    }
}
