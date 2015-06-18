/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.longlinkislong.gloop;

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
        this(GLThread.getDefaultInstance(), 0f, 0f, 0f, 0f, 1d);
    }

    /**
     * Constructs a new GLClear object on the specified OpenGL thread using the
     * default OpenGL clear color value and clear depth value.
     *
     * @param thread the OpenGL thread.
     * @since 15.06.18
     */
    public GLClear(final GLThread thread) {
        this(thread, 0f, 0f, 0f, 0f, 1d);
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
     * @since 15.06.18
     */
    public GLClear(
            final GLThread thread,
            final float r, final float g, final float b, final float a,
            final double depth) {

        super(thread);

        this.red = r;
        this.green = g;
        this.blue = b;
        this.alpha = a;
        this.depth = depth;
    }

    private final GLTask init = new ApplyClearTask();

    /**
     * Applies the clear task to the OpenGL thread associated to the GLClear
     * object.
     *
     * @since 15.06.18
     */
    public final void applyClear() {
        this.init.glRun(this.getThread());
    }

    /**
     * A GLTask that sets both clear color and clear depth values.
     * @since 15.06.18
     */
    public class ApplyClearTask extends GLTask {

        @Override
        public void run() {
            GL11.glClearColor(
                    GLClear.this.red,
                    GLClear.this.green,
                    GLClear.this.blue,
                    GLClear.this.alpha);
            GL11.glClearDepth(GLClear.this.depth);
        }
    }

    private ClearTask lastClearTask = null;

    /**
     * Runs the clear command with the specified buffer clear bits.
     * @param bits the attachments of the backbuffer to clear.
     * @since 15.06.18
     */
    public void clear(final GLClearBufferMode... bits) {
        this.clear(bits, 0, bits.length);
    }

    /**
     * Runs the clear command with the specified clear buffer bits.
     * @param bits the clear bits
     * @param offset the offset to start reading from the clear bits array.
     * @param length the number of elements to read from the array.
     * @since 15.06.18
     */
    public void clear(
            final GLClearBufferMode[] bits, final int offset, final int length) {

        if (this.lastClearTask != null) {
            int bitValue = 0;

            for (GLClearBufferMode bit : bits) {
                bitValue |= bit.value;
            }

            if (this.lastClearTask.bits != bitValue) {
                this.lastClearTask = new ClearTask(bits, offset, length);
            }
        } else {
            this.lastClearTask = new ClearTask(bits, offset, length);
        }

        this.lastClearTask.glRun(this.getThread());
    }

    /**
     * A GLTask that clears the current framebuffer.
     * @since 15.06.18
     */
    public class ClearTask extends GLTask {

        final int bits;

        /**
         * The bitfield to clear
         * @param bits bitfield.
         * @since 15.06.18
         */
        public ClearTask(final GLClearBufferMode... bits) {
            this(bits, 0, bits.length);
        }

        /**
         * The bitfield to clear.
         * @param bits array containing bitfield parameters.
         * @param offset offset to start reading from the array.
         * @param length the number of elements to read from the array.
         * @since 15.06.18
         */
        public ClearTask(
                final GLClearBufferMode[] bits,
                final int offset,
                final int length) {

            int tBits = 0;

            for (GLClearBufferMode bit : bits) {
                tBits |= bit.value;
            }

            this.bits = tBits;
        }

        @Override
        public void run() {
            GL11.glClear(this.bits);
        }
    }
}
