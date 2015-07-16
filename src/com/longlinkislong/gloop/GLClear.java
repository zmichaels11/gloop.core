/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.longlinkislong.gloop;

import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;
import org.lwjgl.opengl.GL11;

/**
 * GLClear is a aGLObject that represents the parameters used for clearing the
 * backbuffer.
 *
 * @author zmichaels
 * @since 15.06.18
 */
public class GLClear extends GLObject {

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
    public static final Set<GLClearBufferMode> DEFAULT_CLEAR_BITS;

    static {
        final Set<GLClearBufferMode> bits = new HashSet<>();
        DEFAULT_CLEAR_BITS = Collections.unmodifiableSet(bits);

        bits.add(GLClearBufferMode.GL_COLOR_BUFFER_BIT);
        bits.add(GLClearBufferMode.GL_DEPTH_BUFFER_BIT);
    }

    private final int clearBitField;
    public final Set<GLClearBufferMode> clearBits;
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

    /**
     * Constructs a new GLClear object on the default OpenGL thread using the
     * default OpenGL clear color value and clear depth value.
     *
     * @since 15.06.18
     */
    public GLClear() {
        this(GLThread.getDefaultInstance());
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
            final Set<GLClearBufferMode> clearBits) {

        super(thread);

        this.red = r;
        this.green = g;
        this.blue = b;
        this.alpha = a;
        this.depth = depth;

        final Set<GLClearBufferMode> bits = new HashSet<>();
        int bitField = 0;

        for (GLClearBufferMode bit : clearBits) {
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
        return this.getThread() == thread
                ? this
                : new GLClear(
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
    public GLClear withClearBits(final GLClearBufferMode... modes) {
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
    public GLClear withClearBits(final GLClearBufferMode[] modes, final int offset, final int count) {
        final Set<GLClearBufferMode> bits = new HashSet<>();

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

    private final GLTask clearTask = new ClearTask();

    /**
     * Performs the OpenGL clear operation.
     *
     * @since 15.07.01
     */
    public void clear() {
        this.clearTask.glRun(this.getThread());
    }

    /**
     * A GLTask that clears the select buffers.
     *
     * @since 15.07.01
     */
    public class ClearTask extends GLTask {

        @Override
        public void run() {
            final GLThread thread = GLThread.getCurrent();
            
            thread.currentClear = GLClear.this.withGLThread(thread);            
            GL11.glClearColor(GLClear.this.red, GLClear.this.green, GLClear.this.blue, GLClear.this.alpha);
            assert GL11.glGetError() == GL11.GL_NO_ERROR : String.format("glClear(%f, %f, %f, %f) failed!",
                    GLClear.this.red, GLClear.this.green, GLClear.this.blue, GLClear.this.alpha);
                        
            GL11.glClearDepth(GLClear.this.depth);
            
            assert GL11.glGetError() == GL11.GL_NO_ERROR : String.format("glClear(%f) failed!", GLClear.this.depth);
            
            GL11.glClear(GLClear.this.clearBitField);
            assert GL11.glGetError() == GL11.GL_NO_ERROR : String.format("glClear(%s) failed!", GLClear.this.clearBits);
        }

    }
}
