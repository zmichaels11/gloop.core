/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.longlinkislong.gloop;

import org.lwjgl.opengl.GL11;

/**
 *
 * @author zmichaels
 */
public enum GLPolygonMode {

    GL_POINT(GL11.GL_POINT),
    GL_LINE(GL11.GL_LINE),
    GL_FILL(GL11.GL_FILL);
    final int value;

    GLPolygonMode(final int value) {
        this.value = value;
    }

    public static GLPolygonMode valueOf(final int value) {
        for (GLPolygonMode mode : values()) {
            if (mode.value == value) {
                return mode;
            }
        }

        return null;
    }
}
