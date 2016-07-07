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

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.Marker;
import org.slf4j.MarkerFactory;

/**
 * An OpenGL object that represents a ScissorTest. A ScissorTest is an OpenGL
 * operation used to select a segment of a larger scene to draw.
 *
 * @author zmichaels
 * @since 15.07.06
 */
public class GLScissorTest extends GLObject {

    private static final Marker GLOOP_MARKER = MarkerFactory.getMarker("GLOOP");
    private static final Logger LOGGER = LoggerFactory.getLogger("GLScissorTest");

    public final int left;
    public final int bottom;
    public final int width;
    public final int height;
    public final boolean enabled;

    private String name = "id=" + System.currentTimeMillis();

    /**
     * Assigns a human-readable name to the GLScissorTest.
     *
     * @param newName the human-readable name.
     * @since 15.12.18
     */
    public final void setName(final CharSequence newName) {
        GLTask.create(() -> {
            LOGGER.trace(
                    GLOOP_MARKER,
                    "Renamed GLScissorTest[{}] to GLScissorTest[{}]!",
                    this.name,
                    name);

            this.name = newName.toString();
        }).glRun(this.getThread());
    }

    /**
     * Retrieves the name of the GLScissorTest.
     *
     * @return the ScissorTest name.
     * @since 15.12.18
     */
    public final String getName() {
        return this.name;
    }

    /**
     * Constructs a new GLScissorTest on any OpenGL thread.
     *
     * @param enabled if the scissortest should be enabled.
     * @param left the leftmost value of the pixel test.
     * @param bottom the bottommost value of the pixel test.
     * @param width the width of the pixel test.
     * @param height the height of the pixel test.
     * @since 15.12.18
     */
    public GLScissorTest(
            final boolean enabled,
            final int left, final int bottom,
            final int width, final int height) {

        this(GLThread.getAny(), enabled, left, bottom, width, height);
    }

    /**
     * Constructs a new GLScissorTest on the specified thread.
     *
     * @param thread the OpenGL thread to associate the GLScissorTest with.
     * @param enabled if the scissor test should be enabled.
     * @param left the leftmost pixel of the test.
     * @param bottom the bottommost pixel of the test.
     * @param width the width of the test.
     * @param height the height of the test.
     * @since 15.12.18
     */
    public GLScissorTest(
            final GLThread thread,
            final boolean enabled,
            final int left, final int bottom,
            final int width, final int height) {

        super(thread);

        LOGGER.trace(
                GLOOP_MARKER,
                "Constructed GLScissorTest[{}] object on thread: {}",
                this.name,
                thread);

        this.left = left;
        this.bottom = bottom;
        this.width = width;
        this.height = height;
        this.enabled = enabled;
    }

    public GLScissorTest(final GLThread thread) {
        this(thread, false, 0, 0, 0, 0);
    }

    public GLScissorTest() {
        this(false, 0, 0, 0, 0);
    }

    public GLScissorTest withEnabled(final boolean enabled) {
        return new GLScissorTest(this.getThread(), enabled, this.left, this.bottom, this.width, this.height);
    }

    public GLScissorTest withRectangle(final int left, final int bottom, final int width, final int height) {
        return new GLScissorTest(this.getThread(), this.enabled, left, bottom, width, height);
    }

    public GLScissorTest withGLThread(final GLThread thread) {
        return new GLScissorTest(thread, enabled, left, bottom, width, height);
    }

    /**
     * Begins the GLScissorTest.
     *
     * @since 15.12.18
     */
    @Deprecated
    public void begin() {
        new BeginScissorTestTask().glRun(this.getThread());
    }

    public void apply() {
        new ApplyTask().glRun(this.getThread());
    }

    public class ApplyTask extends GLTask {
        @Override
        public void run() {
            final GLThread thread = GLThread.getCurrent().orElseThrow(GLException::new);

            thread.runOnScissorTestChangeCallback(thread.currentScissor, GLScissorTest.this);
            thread.currentScissor = GLScissorTest.this.withGLThread(thread);

            if(GLScissorTest.this.enabled) {
                GLTools.getDriverInstance().scissorTestEnable(left, bottom, width, height);
            } else {
                GLTools.getDriverInstance().scissorTestDisable();
            }
        }
    }

    /**
     * GLTask that begins the scissor test.
     *
     * @since 15.12.18
     */
    @Deprecated
    public class BeginScissorTestTask extends GLTask {

        @Override
        public void run() {
            LOGGER.trace(GLOOP_MARKER, "############### Start GLScissorTest Begin Scissor Test Task ###############");
            LOGGER.trace("\tAppying GLScissorTest[{}]", GLScissorTest.this.getName());
            
            GLTools.getDriverInstance().scissorTestEnable(left, bottom, width, height);
            LOGGER.trace(GLOOP_MARKER, "############### End GLScissorTest Begin Scissor Test Task ###############");
        }
    }

    /**
     * Ends the GLScissorTest.
     *
     * @since 15.12.18
     */
    @Deprecated
    public void end() {
        new EndScissorTestTask().glRun(this.getThread());
    }

    /**
     * GLTask that ends the scissor test.
     *
     * @since 15.12.18
     */
    @Deprecated
    public class EndScissorTestTask extends GLTask {

        @Override
        public void run() {
            LOGGER.trace(GLOOP_MARKER, "############### Start GLScissorTest End Scissor Test Task ###############");
            LOGGER.trace(GLOOP_MARKER, "\tApplying GLScissorTest[{}]", GLScissorTest.this.getName());
            
            GLTools.getDriverInstance().scissorTestDisable();
            LOGGER.trace(GLOOP_MARKER, "############### End GLScissorTest End Scissor Test Task ###############");
        }
    }
}
