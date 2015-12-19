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
 * A GLObject that represents the state of viewport settings.
 *
 * @author zmichaels
 * @see
 * <a href="https://www.khronos.org/opengles/sdk/docs/man/xhtml/glViewport.xml">glViewport
 * (OpenGL SDK)</a>
 * @see <a href="https://www.opengl.org/wiki/GLAPI/glViewport">glViewport
 * (OpenGL Wiki)</a>
 * @since 15.06.24
 */
public class GLViewport extends GLObject {

    private static final Marker GLOOP_MARKER = MarkerFactory.getMarker("GLOOP");
    private static final Logger LOGGER = LoggerFactory.getLogger("GLViewport");
    /**
     * The lower-left corner of the viewport rectangle, in pixels.
     *
     * @since 15.06.24
     */
    public final int x;
    /**
     * The lower-left corner of the viewport rectangle, in pixels.
     */
    public final int y;
    /**
     * Specifies the width of the viewport.
     *
     * @since 15.06.24
     */
    public final int width;
    /**
     * Specifies the height of the viewport.
     *
     * @since 15.06.24
     */
    public final int height;

    /**
     * The aspect ratio of the viewport.
     *
     * @since 15.08.24
     */
    public final double aspect;

    private volatile String name = "id=" + System.currentTimeMillis();

    /**
     * Assigns a human-readable name to the GLViewport.
     *
     * @param newName the human-readable name.
     * @since 15.12.18
     */
    public void setName(final CharSequence newName) {
        GLTask.create(() -> {
            LOGGER.trace(
                    GLOOP_MARKER,
                    "Renamed GLViewport[{}] to GLViewport[{}]!",
                    GLViewport.this.name,
                    newName);

            GLViewport.this.name = newName.toString();
        }).glRun(this.getThread());
    }

    /**
     * Retrieves the name of the GLViewport.
     *
     * @return the name.
     * @since 15.12.18
     */
    public String getName() {
        return this.name;
    }

    /**
     * Constructs a new GLViewport object on the default OpenGL thread.
     *
     * @param x the x-element of the lower-left viewport rectangle.
     * @param y the y-element of the lower-left viewport rectangle.
     * @param w the width of the viewport.
     * @param h the height of the viewport.
     * @since 15.06.24
     */
    public GLViewport(final int x, final int y, final int w, final int h) {
        this(GLThread.getAny(), x, y, w, h);
    }

    /**
     * Constructs a new GLViewport object on the supplied OpenGL thread.
     *
     * @param thread the OpenGL thread to create the viewport object on.
     * @param x the x-element of the lower-left viewport rectangle.
     * @param y the y-element of the lower-left viewport rectangle.
     * @param w the width of the viewport.
     * @param h the height of the viewport.
     * @since 15.06.25
     */
    public GLViewport(
            final GLThread thread,
            final int x, final int y, final int w, final int h) {

        super(thread);

        LOGGER.trace(
                GLOOP_MARKER,
                "Constructed GLViewport on thread: {}",
                thread);

        this.x = x;
        this.y = y;
        this.width = w;
        this.height = h;
        this.aspect = (double) w / (double) h;
    }

    public GLViewport withGLThread(final GLThread thread) {
        return this.getThread() == thread
                ? this
                : new GLViewport(thread, this.x, this.y, this.width, this.height);
    }

    public GLViewport withViewRect(final int x, final int y, final int width, final int height) {
        return this.x == x && this.y == y && this.width == width && this.height == height
                ? this
                : new GLViewport(this.getThread(), x, y, width, height);
    }

    private final ApplyViewportTask applyTask = new ApplyViewportTask();

    /**
     * Applies the viewport to the associated OpenGL thread.
     *
     * @since 15.06.24
     */
    public void applyViewport() {
        this.applyTask.glRun(this.getThread());
    }

    /**
     * A GLTask that applies the viewport settings.
     *
     * @since 15.06.24
     */
    public class ApplyViewportTask extends GLTask {

        @Override
        public void run() {
            LOGGER.trace(GLOOP_MARKER, "############### Start GLViewport Apply Viewport Task ##############");
            LOGGER.trace(GLOOP_MARKER, "\tApplying GLViewport[{}]", getName());
            LOGGER.trace(GLOOP_MARKER, "\tX: {}, Y: {}, Width: {}, Height: {}", GLViewport.this.x, GLViewport.this.y, GLViewport.this.width, GLViewport.this.height);

            final GLThread thread = GLThread.getCurrent().orElseThrow(GLException::new);

            if (thread == GLViewport.this.getThread()) {
                thread.currentViewport = GLViewport.this;
            } else {
                thread.currentViewport = GLViewport.this.withGLThread(thread);
            }

            GLTools.getDSAInstance().glViewport(GLViewport.this.x, GLViewport.this.y, GLViewport.this.width, GLViewport.this.height);

            LOGGER.trace(
                    GLOOP_MARKER,
                    "############### End GLViewport Apply Viewport Task ##############");
        }
    }
}
