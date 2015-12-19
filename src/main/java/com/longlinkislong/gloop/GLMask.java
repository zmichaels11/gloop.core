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

import com.longlinkislong.gloop.dsa.DSADriver;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.Marker;
import org.slf4j.MarkerFactory;

/**
 * An immutable GLObject that contains mask settings.
 *
 * @author zmichaels
 * @since 15.12.18
 */
public class GLMask extends GLObject {

    private static final Marker GLOOP_MARKER = MarkerFactory.getMarker("GLOOP");
    private static final Logger LOGGER = LoggerFactory.getLogger("GLMask");

    public static final boolean DEFAULT_RED_MASK = true;
    public static final boolean DEFAULT_GREEN_MASK = true;
    public static final boolean DEFAULT_BLUE_MASK = true;
    public static final boolean DEFAULT_ALPHA_MASK = true;
    public static final boolean DEFAULT_DEPTH_MASK = true;
    public static final int DEFAULT_STENCIL_MASK = 0xFFFFFFFF;

    public final boolean red, green, blue, alpha;
    public final boolean depth;
    public final int stencil;

    private volatile String name = "id=" + System.currentTimeMillis();

    /**
     * Assigns a human-readable name to the GLMask object.
     *
     * @param newName the new name to assign.
     * @since 15.12.18
     */
    public final void setName(final CharSequence newName) {
        GLTask.create(() -> {
            LOGGER.trace(
                    GLOOP_MARKER,
                    "Renamed GLMask[{}] to GLMask[{}]!",
                    this.name,
                    newName);

            this.name = newName.toString();
        }).glRun(this.getThread());
    }

    /**
     * Retrieves the name of the GLMask object.
     *
     * @return the name.
     * @since 15.12.18
     */
    public final String getName() {
        return this.name;
    }

    /**
     * Constructs a new GLMask on any OpenGL thread with the default mask
     * settings.
     *
     * @since 15.12.18
     */
    public GLMask() {
        this(GLThread.getAny());
    }

    /**
     * Constructs a new GLMask on the specified OpenGL thread with the default
     * mask settings.
     *
     * @param thread the GLThread to construct the mask on.
     * @since 15.12.18
     */
    public GLMask(final GLThread thread) {
        this(
                thread,
                DEFAULT_RED_MASK, DEFAULT_GREEN_MASK, DEFAULT_BLUE_MASK, DEFAULT_ALPHA_MASK,
                DEFAULT_DEPTH_MASK,
                DEFAULT_STENCIL_MASK);
    }

    /**
     * Constructs a new GLMask with the specified settings.
     *
     * @param thread the OpenGL thread to associate with the GLMask.
     * @param red the red mask.
     * @param green the green mask.
     * @param blue the blue mask.
     * @param alpha the alpha mask.
     * @param depth the depth mask.
     * @param stencil the stencil mask.
     * @since 15.12.18
     */
    public GLMask(
            final GLThread thread,
            final boolean red,
            final boolean green,
            final boolean blue,
            final boolean alpha,
            final boolean depth,
            final int stencil) {

        super(thread);

        this.red = red;
        this.green = green;
        this.blue = blue;
        this.alpha = alpha;
        this.depth = depth;
        this.stencil = stencil;
    }

    /**
     * Constructs a new instance of GLMask by copying all of the values and
     * overriding the color mask settings.
     *
     * @param red the red mask of the new GLMask object.
     * @param green the green mask of the new GLMask object.
     * @param blue the blue mask of the new GLMask object.
     * @param alpha the alpha mask of the new GLMask object.
     * @return the new GLMask object.
     * @since 15.12.18
     */
    public GLMask withColorMask(
            final boolean red,
            final boolean green,
            final boolean blue,
            final boolean alpha) {

        return new GLMask(
                this.getThread(),
                red, green, blue, alpha,
                this.depth,
                this.stencil);
    }

    /**
     * Constructs a new instance of GLMask by copying all of the values and
     * overriding the depth mask.
     *
     * @param depth the depth mask of the new GLMask object.
     * @return the new GLMask.
     * @since 15.12.18
     */
    public GLMask withDepthMask(final boolean depth) {
        return new GLMask(
                this.getThread(),
                this.red, this.green, this.blue, this.alpha,
                depth,
                this.stencil);
    }

    /**
     * Constructs a new instance of GLMask by copying all of the values and
     * overriding the stencil mask.
     *
     * @param stencilMask the stencil mask of the new GLMask object.
     * @return the new GLMask.
     * @since 15.12.18
     */
    public GLMask withStencilMask(final int stencilMask) {
        return new GLMask(
                this.getThread(),
                this.red, this.green, this.blue, this.alpha,
                this.depth,
                stencilMask);
    }

    /**
     * Constructs a new instance of GLMask by copying all of the values and
     * overriding the thread.
     *
     * @param thread the OpenGL thread to associate with the new GLMask object.
     * @return the new GLMask object.
     * @since 15.12.18
     */
    public GLMask withGLThread(final GLThread thread) {
        return new GLMask(
                thread,
                this.red, this.green, this.blue, this.alpha,
                this.depth, this.stencil);
    }

    /**
     * Applies the GLMask.
     *
     * @since 15.12.18
     */
    public void applyMask() {
        new ApplyTask().glRun(this.getThread());
    }

    /**
     * A GLTask that applies the GLMask settings.
     *
     * @since 15.12.18
     */
    public class ApplyTask extends GLTask {

        @Override
        public void run() {
            LOGGER.trace(GLOOP_MARKER, "############### Start GLMask Apply Task ###############");
            LOGGER.trace(GLOOP_MARKER, "\tApplying GLMask[{}]", GLMask.this.getName());
            LOGGER.trace(GLOOP_MARKER, "\tRed: {} Green: {}, Blue: {}", red, green, blue);
            LOGGER.trace(GLOOP_MARKER, "\tAlpha: {}", alpha);
            LOGGER.trace(GLOOP_MARKER, "\tDepth: {}", depth);
            LOGGER.trace("\tStencil bitfield: {}", stencil);            

            final GLThread thread = GLThread.getCurrent().orElseThrow(GLException::new);

            if (thread == GLMask.this.getThread()) {
                thread.currentMask = GLMask.this;
            } else {
                thread.currentMask = GLMask.this.withGLThread(thread);
            }

            final DSADriver dsa = GLTools.getDSAInstance();

            dsa.glColorMask(GLMask.this.red, GLMask.this.green, GLMask.this.blue, GLMask.this.alpha);
            dsa.glDepthMask(GLMask.this.depth);
            dsa.glStencilMask(GLMask.this.stencil);

            LOGGER.trace(GLOOP_MARKER,"############### End GLMask Apply Task ###############");
        }
    }
}
