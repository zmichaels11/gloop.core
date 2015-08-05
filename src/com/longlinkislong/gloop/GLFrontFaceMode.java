/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.longlinkislong.gloop;

import org.lwjgl.opengl.GL11;

/**
 * The order that points are read for drawing a polygon.
 *
 * @author zmichaels
 * @since 15.08.05
 */
public enum GLFrontFaceMode {

    /**
     * Clock-Wise order
     *
     * @since 15.08.05
     */
    GL_CW(GL11.GL_CW),
    /**
     * Counter-Clock-Wise order.
     *
     * @since 15.08.05
     */
    GL_CCW(GL11.GL_CCW);
    final int value;

    GLFrontFaceMode(final int value) {
        this.value = value;
    }

    public static GLFrontFaceMode valueOf(final int value) {
        for (GLFrontFaceMode mode : values()) {
            if (mode.value == value) {
                return mode;
            }
        }

        return null;
    }
}
