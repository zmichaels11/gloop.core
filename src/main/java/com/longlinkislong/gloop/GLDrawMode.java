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

import java.util.Optional;

/**
 * Draw mode specifies how the vertex data is converted into polygons.
 *
 * @author zmichaels
 * @since 15.05.27
 */
public enum GLDrawMode {

    /**
     * Draw mode in which each vertex element is its own point.
     *
     * @since 15.05.27
     */
    GL_POINTS(0),
    GL_PATCHES(14),
    /**
     * Draw mode in which each vertex element is a point forming a continuous
     * line.
     *
     * @since 15.05.27
     */
    GL_LINE_STRIP(3),
    /**
     * Draw mode in which each vertex element is a point forming a continuous
     * line. The first vertex element is reused as the last element.
     *
     * @since 15.05.27
     */
    GL_LINE_LOOP(2),
    /**
     * Draw mode in which a series of triangles are defined by reusing the last
     * two vertex elements.
     *
     * @since 15.05.27
     */
    GL_TRIANGLE_STRIP(5),
    GL_TRIANGLE_FAN(6),
    /**
     * Draw mode in which every two vertex elements make up a line.
     *
     * @since 15.05.27
     */
    GL_LINES(1),
    /**
     * Draw mode in which every three vertex elements make up a triangle.
     *
     * @since 15.05.27
     */
    GL_TRIANGLES(4),
    GL_LINES_ADJACENCY(10),
    GL_TRIANGLES_ADJACENCY(12),
    GL_LINE_STRIP_ADJACENCY(11),
    GL_TRIANGLE_STRIP_ADJACENCY(13);

    final int value;

    GLDrawMode(final int value) {
        this.value = value;
    }

    /**
     * Translates the GLenum to the corresponding GLDrawMode.
     *
     * @param glEnum the GLenum value.
     * @return the GLDrawMode wrapped in an Optional.
     * @since 15.12.18
     */
    public static Optional<GLDrawMode> of(final int glEnum) {
        for (GLDrawMode mode : values()) {
            if (mode.value == glEnum) {
                return Optional.of(mode);
            }
        }

        return Optional.empty();
    }
}
