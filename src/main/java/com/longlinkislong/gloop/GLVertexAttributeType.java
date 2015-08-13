/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.longlinkislong.gloop;

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
    GL_FLOAT(5126, Float.BYTES),
    /**
     * 8 byte floating point type. Not all implementations of OpenGL support
     * doubles for vertex attributes.
     *
     * @since 15.06.24
     */
    GL_DOUBLE(5130, Double.BYTES),
    /**
     * 1 byte signed integer.
     *
     * @since 15.06.24
     */
    GL_BYTE(5120, Byte.BYTES),
    /**
     * 1 byte unsigned integer.
     *
     * @since 15.06.24
     */
    GL_UNSIGNED_BYTE(5121, Byte.BYTES),
    /**
     * 2 byte signed integer.
     *
     * @since 15.06.24
     */
    GL_SHORT(5122, Short.BYTES),
    /**
     * 2 byte unsigned integer.
     *
     * @since 15.06.24
     */
    GL_UNSIGNED_SHORT(5123, Short.BYTES),
    /**
     * 4 byte signed integer.
     *
     * @since 15.06.24
     */
    GL_INT(5124, Integer.BYTES),
    /**
     * 4 byte unsigned integer
     *
     * @since 15.06.24
     */
    GL_UNSIGNED_INT(5125, Integer.BYTES);

    final int value;
    final int width;

    GLVertexAttributeType(final int value, final int width) {
        this.value = value;
        this.width = width;
    }
}
