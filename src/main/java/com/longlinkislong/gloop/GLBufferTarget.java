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

import static com.longlinkislong.gloop.GLTools.hasOpenGLVersion;
import java.util.Optional;

/**
 * Targets that a GLBuffer may be bound to. Most buffer functions do not
 * actually care what target the buffer is bound to and buffers can be bound to
 * multiple targets. However there are some methods that are picky.
 * GL_ARRAY_BUFFER and GL_ELEMENT_BUFFER are accepted by most buffer
 * calls/queries.
 *
 * @see <a href="https://www.opengl.org/wiki/Buffer_Object#General_use"> Buffer
 * Object, General Use</a>
 * @author zmichaels
 * @since 15.05.27
 */
public enum GLBufferTarget {

    GL_EXTERNAL_VIRTUAL_MEMORY_BUFFER(37216) {
        @Override
        public boolean isSupported() {
            return GLTools.isGPUAmd() && hasOpenGLVersion(41);
        }
    },
    /**
     * An array buffer is used generally for vertex data. It can also be used as
     * a generic target.
     *
     * @since 15.05.27
     */
    GL_ARRAY_BUFFER(34962) {
        @Override
        public boolean isSupported() {
            return hasOpenGLVersion(15);
        }
    },
    /**
     * A uniform buffer allows buffers to be used to transport bulk uniform data
     * to a shader program.
     *
     * @since 15.05.27
     */
    GL_UNIFORM_BUFFER(35345) {
        @Override
        public boolean isSupported() {
            return hasOpenGLVersion(31);
        }
    },
    /**
     * An index buffer binding for buffers used as storage for atomic counters.
     * Requires OpenGL 4.2 or ARB_shader_atomic_counters
     *
     * @since 15.06.23
     */
    GL_ATOMIC_COUNTER_BUFFER(37568) {
        @Override
        public boolean isSupported() {
            return hasOpenGLVersion(42);
        }
    },
    /**
     * Used for performing direct writes from asynchronous queries to buffer
     * object memory. Requires OpenGL 4.4
     *
     * @since 15.06.23
     */
    GL_QUERY_BUFFER(37266) {
        @Override
        public boolean isSupported() {
            return hasOpenGLVersion(44);
        }
    },
    /**
     * A copy read buffer is the buffer that is read from in a copy task.
     *
     * @since 15.05.27
     */
    GL_COPY_READ_BUFFER(36662) {
        @Override
        public boolean isSupported() {
            return hasOpenGLVersion(31);
        }
    },
    /**
     * A copy write buffer is a buffer that is written to in a copy task.
     *
     * @since 15.05.27
     */
    GL_COPY_WRITE_BUFFER(36663) {
        @Override
        public boolean isSupported() {
            return hasOpenGLVersion(31);
        }
    },
    /**
     * The buffer bound to this target will be used as the source for indirect
     * compute dispatch operations. This requires OpenGL 4.3 or
     * ARB_compute_shader.
     *
     * @since 15.06.23
     */
    GL_DISPATCH_INDIRECT_BUFFER(37102) {
        @Override
        public boolean isSupported() {
            return hasOpenGLVersion(43);
        }
    },
    /**
     * A draw indirect buffer is a buffer that stores draw indirect commands.
     *
     * @since 15.05.27
     */
    GL_DRAW_INDIRECT_BUFFER(36671) {
        @Override
        public boolean isSupported() {
            return hasOpenGLVersion(40);
        }
    },
    /**
     * An element array buffer is a buffer that specifies the order of vertex
     * data.
     *
     * @since 15.05.27
     */
    GL_ELEMENT_ARRAY_BUFFER(34963) {
        @Override
        public boolean isSupported() {
            return hasOpenGLVersion(15);
        }
    },
    /**
     * GL_TEXTURE_BUFFER actually behaves no different than GL_ARRAY_BUFFER.
     * However it should be used when a buffer is bound for texture data.
     *
     * @since 15.06.23
     */
    GL_TEXTURE_BUFFER(35882) {
        @Override
        public boolean isSupported() {
            return hasOpenGLVersion(31);
        }
    },
    /**
     * Used for pixel pack operations.
     *
     * @since 15.06.23
     */
    GL_PIXEL_PACK_BUFFER(35051) {
        @Override
        public boolean isSupported() {
            return hasOpenGLVersion(21);
        }
    },
    /**
     * Used for pixel unpack operations.
     *
     * @since 15.06.23
     */
    GL_PIXEL_UNPACK_BUFFER(35052) {
        @Override
        public boolean isSupported() {
            return hasOpenGLVersion(21);
        }
    },
    /**
     * A shader storage buffer is a buffer that can be read directly from a
     * shader. This functions similarly to a uniform buffer, except with much
     * larger size.
     *
     * @since 15.05.27
     */
    GL_SHADER_STORAGE_BUFFER(37074) {
        @Override
        public boolean isSupported() {
            return hasOpenGLVersion(43);
        }
    },
    /**
     * A transform feedback buffer is a buffer that records the output of a
     * transform feedback.
     *
     * @since 15.05.27
     */
    GL_TRANSFORM_FEEDBACK_BUFFER(35982) {
        @Override
        public boolean isSupported() {
            return hasOpenGLVersion(30);
        }
    };

    final int value;

    GLBufferTarget(final int value) {
        this.value = value;
    }

    /**
     * Checks if the GLBufferTarget is supported by the current OpenGL context.
     *
     * @return true if the GLBufferTarget is supported.
     * @since 15.07.02
     */
    public abstract boolean isSupported();

    /**
     * Translates the GLenum into the corresponding GLBufferTarget object.
     *
     * @param glEnum the GLenum value.
     * @return the GLBufferTarget wrapped in an Optional.
     * @since 15.12.18
     */
    public static Optional<GLBufferTarget> of(final int glEnum) {
        for (GLBufferTarget tgt : values()) {
            if (tgt.value == glEnum) {
                return Optional.of(tgt);
            }
        }

        return Optional.empty();
    }
}
