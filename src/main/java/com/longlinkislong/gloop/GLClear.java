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

import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.Marker;
import org.slf4j.MarkerFactory;

/**
 * GLClear is a aGLObject that represents the parameters used for clearing the
 * backbuffer.
 *
 * @author zmichaels
 * @since 15.06.18
 */
public class GLClear extends GLObject {

    private static final Marker GLOOP_MARKER = MarkerFactory.getMarker("GLOOP");
    private static final Logger LOGGER = LoggerFactory.getLogger("GLClear");

    /**
     * The default value the red channel is set to on clear. By default it is
     * 0.0.
     *
     * @since 15.07.01
     */
    public static final float DEFAULT_CLEAR_RED = 0f;
    /**
     * The default value the green channel is set to on clear. By default it is
     * 0.0.
     *
     * @since 15.07.01
     */
    public static final float DEFAULT_CLEAR_GREEN = 0f;
    /**
     * The default value the blue channel is set to on clear. By default it is
     * 0.0.
     *
     * @since 15.07.01
     */
    public static final float DEFAULT_CLEAR_BLUE = 0f;
    /**
     * The default value the alpha channel is set to on clear. By default it is
     * 0.0.
     *
     * @since 15.07.01
     */
    public static final float DEFAULT_CLEAR_ALPHA = 0f;
    /**
     * The default value the depth buffer is set to on clear. By default it is
     * 1.0.
     *
     * @since 15.07.01
     */
    public static final double DEFAULT_CLEAR_DEPTH = 1d;
    /**
     * The default clear bits. By default it is GL_CLEAR_COLOR_BIT and
     * GL_CLEAR_DEPTH_BIT.
     *
     * @since 15.07.01
     */
    public static final Set<GLFramebufferMode> DEFAULT_CLEAR_BITS;

    static {
        final Set<GLFramebufferMode> bits = new HashSet<>();
        DEFAULT_CLEAR_BITS = Collections.unmodifiableSet(bits);

        bits.add(GLFramebufferMode.GL_COLOR_BUFFER_BIT);
        bits.add(GLFramebufferMode.GL_DEPTH_BUFFER_BIT);
    }

    private final int clearBitField;
    public final Set<GLFramebufferMode> clearBits;
    /**
     * The value to set each pixel's red component to.
     *
     * @since 15.06.18
     */
    public final float red;
    /**
     * The value to set each pixel's green component to.
     *
     * @since 15.06.18
     */
    public final float green;
    /**
     * The value to set each pixel's blue component to.
     *
     * @since 15.06.18
     */
    public final float blue;
    /**
     * The value to set each pixel's alpha component to.
     *
     * @since 15.06.18
     */
    public final float alpha;
    /**
     * The value to set each pixel's depth component to.
     *
     * @since 15.06.18
     */
    public final double depth;

    private volatile String name = "id=" + System.currentTimeMillis();

    /**
     * Assigns a human-readable name to the GLClear object.
     *
     * @param newName the name.
     * @since 15.12.17
     */
    public final void setName(final CharSequence newName) {
        GLTask.create(() -> {
            LOGGER.trace(
                    GLOOP_MARKER,
                    "Renamed GLClear[{}] to GLClear[{}]!",
                    this.name, newName);

            this.name = newName.toString();
        }).glRun(this.getThread());
    }

    /**
     * Retrieves the name of the GLClear object.
     *
     * @return the name.
     * @since 15.12.18
     */
    public String getName() {
        return this.name;
    }

    /**
     * Constructs a new GLClear object on the default OpenGL thread using the
     * default OpenGL clear color value and clear depth value.
     *
     * @since 15.06.18
     */
    public GLClear() {
        this(GLThread.getAny());
    }

    /**
     * Constructs a new GLClear object on the specified OpenGL thread using the
     * default OpenGL clear color value and clear depth value.
     *
     * @param thread the OpenGL thread.
     * @since 15.06.18
     */
    public GLClear(final GLThread thread) {
        this(thread,
                DEFAULT_CLEAR_RED, DEFAULT_CLEAR_GREEN, DEFAULT_CLEAR_BLUE,
                DEFAULT_CLEAR_ALPHA, DEFAULT_CLEAR_DEPTH,
                DEFAULT_CLEAR_BITS);
    }

    /**
     * Constructs a new GLClear object on the specified OpenGL thread using the
     * specified clear color and clear depth value.
     *
     * @param thread the OpenGL thread.
     * @param r the red component.
     * @param g the green component.
     * @param b the blue component.
     * @param a the alpha component.
     * @param depth the depth component.
     * @param clearBits the clear bits to use
     * @since 15.06.18
     */
    public GLClear(
            final GLThread thread,
            final float r, final float g, final float b, final float a,
            final double depth,
            final Set<GLFramebufferMode> clearBits) {

        super(thread);

        LOGGER.trace(
                GLOOP_MARKER,
                "Constructed GLClear object on thread: {}",
                thread);

        this.red = r;
        this.green = g;
        this.blue = b;
        this.alpha = a;
        this.depth = depth;

        final Set<GLFramebufferMode> bits = new HashSet<>();
        int bitField = 0;

        for (GLFramebufferMode bit : clearBits) {
            bitField |= bit.value;
            bits.add(bit);
        }

        this.clearBitField = bitField;
        this.clearBits = Collections.unmodifiableSet(bits);
    }

    /**
     * Copies the GLClear object onto the specified OpenGL thread.
     *
     * @param thread the thread to copy the OpenGL object to.
     * @return the GLClear object.
     * @since 15.07.01
     */
    public GLClear withGLThread(final GLThread thread) {
        return new GLClear(
                thread,
                this.red, this.green, this.blue, this.alpha,
                this.depth,
                this.clearBits);
    }

    /**
     * Copies the GLClear object and overrides the clear bits.
     *
     * @param modes a series of clear bits.
     * @return the GLClear object.
     * @since 15.07.01
     */
    public GLClear withClearBits(final GLFramebufferMode... modes) {
        return this.withClearBits(modes, 0, modes.length);
    }

    /**
     * Copies the GLClear object and overrides the clear bits.
     *
     * @param modes the array to read the clear bits from.
     * @param offset the offset to start reading the clear bits.
     * @param count the number of clear bits to read.
     * @return the GLClear object.
     * @since 15.07.01
     */
    public GLClear withClearBits(final GLFramebufferMode[] modes, final int offset, final int count) {
        final Set<GLFramebufferMode> bits = new HashSet<>();

        Arrays.stream(modes, offset, modes.length).forEach(bits::add);

        return new GLClear(this.getThread(), this.red, this.green, this.blue, this.alpha, this.depth, bits);
    }

    /**
     * Copies the GLClear object and overrides the clear color.
     *
     * @param r the new red value.
     * @param g the new green value.
     * @param b the new blue value.
     * @param a the new alpha value
     * @return the GLClear object
     * @since 15.06.18
     */
    public GLClear withClearColor(final float r, final float g, final float b, final float a) {
        return new GLClear(this.getThread(), r, g, b, a, this.depth, this.clearBits);
    }

    /**
     * Copies the GLClear object and overrides the clear depth.
     *
     * @param depth the new clear depth.
     * @return the GLClear object.
     * @since 15.06.18
     */
    public GLClear withClearDepth(final double depth) {
        return new GLClear(this.getThread(), this.red, this.green, this.blue, this.alpha, depth, this.clearBits);
    }

    /**
     * Performs the OpenGL clear operation.
     *
     * @since 15.07.01
     */
    public void clear() {
        new ClearTask().glRun(this.getThread());
    }

    /**
     * A GLTask that clears the select buffers.
     *
     * @since 15.07.01
     */
    public class ClearTask extends GLTask {

        @Override
        public void run() {
            LOGGER.trace(GLOOP_MARKER, "############### Start GLClear Clear Task ###############");
            LOGGER.trace(GLOOP_MARKER, "\tApplying GLClear[{}]", GLClear.this.getName());
            LOGGER.trace(GLOOP_MARKER, "\t<red={}, green={}, blue={}, alpha={}>", GLClear.this.red, GLClear.this.green, GLClear.this.blue, GLClear.this.alpha);
            LOGGER.trace(GLOOP_MARKER, "\tdepth={}", GLClear.this.depth);
            LOGGER.trace(GLOOP_MARKER, "\tClear bitfield: {}", GLClear.this.clearBitField);

            final GLThread thread = GLThread.getCurrent().orElseThrow(GLException::new);

            thread.runOnClearCallback(thread.currentClear, GLClear.this);
            thread.currentClear = GLClear.this.withGLThread(thread);
            GLTools.getDriverInstance().clear(clearBitField, red, green, blue, alpha, depth);
            
            LOGGER.trace(GLOOP_MARKER, "############### End GLClear Clear Task ###############");
        }

    }
}
