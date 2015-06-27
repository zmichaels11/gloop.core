/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.longlinkislong.gloop;

import org.lwjgl.opengl.GL11;

/**
 * Valid types for vertex attributes.
 *
 * @author zmichaels
 * @see
 * <a href="https://www.opengl.org/wiki/GLAPI/glVertexAttribPointer#Function_Definition">glVertexAttribPointer,
 * Function Definition (OpenGL Wiki)</a>
 * <a href="https://www.khronos.org/opengles/sdk/docs/man3/docbook4/xhtml/glVertexAttribPointer.xml">glVertexAttribPointer
 * (OpenGL SDK)</a>
 * @since 15.06.24
 */
public enum GLVertexAttributeType {

    /**
     * 4 byte floating point type.
     *
     * @since 15.06.24
     */
    GL_FLOAT(GL11.GL_FLOAT, Float.BYTES),
    /**
     * 8 byte floating point type. Not all implementations of OpenGL support
     * doubles for vertex attributes.
     *
     * @since 15.06.24
     */
    GL_DOUBLE(GL11.GL_DOUBLE, Double.BYTES),
    /**
     * 1 byte signed integer.
     *
     * @since 15.06.24
     */
    GL_BYTE(GL11.GL_BYTE, Byte.BYTES),
    /**
     * 1 byte unsigned integer.
     *
     * @since 15.06.24
     */
    GL_UNSIGNED_BYTE(GL11.GL_UNSIGNED_BYTE, Byte.BYTES),
    /**
     * 2 byte signed integer.
     *
     * @since 15.06.24
     */
    GL_SHORT(GL11.GL_SHORT, Short.BYTES),
    /**
     * 2 byte unsigned integer.
     *
     * @since 15.06.24
     */
    GL_UNSIGNED_SHORT(GL11.GL_UNSIGNED_SHORT, Short.BYTES),
    /**
     * 4 byte signed integer.
     *
     * @since 15.06.24
     */
    GL_INT(GL11.GL_INT, Integer.BYTES),
    /**
     * 4 byte unsigned integer
     *
     * @since 15.06.24
     */
    GL_UNSIGNED_INT(GL11.GL_UNSIGNED_INT, Integer.BYTES);

    final int value;
    final int width;

    GLVertexAttributeType(final int value, final int width) {
        this.value = value;
        this.width = width;
    }
}
