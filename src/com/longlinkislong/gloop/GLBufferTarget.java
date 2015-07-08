/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.longlinkislong.gloop;

import static com.longlinkislong.gloop.GLTools.hasOpenGLVersion;
import org.lwjgl.opengl.GL15;
import org.lwjgl.opengl.GL21;
import org.lwjgl.opengl.GL30;
import org.lwjgl.opengl.GL31;
import org.lwjgl.opengl.GL40;
import org.lwjgl.opengl.GL42;
import org.lwjgl.opengl.GL43;
import org.lwjgl.opengl.GL44;

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

    GL_EXTERNAL_VIRTUAL_MEMORY_BUFFER(0x9160) {
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
    GL_ARRAY_BUFFER(GL15.GL_ARRAY_BUFFER) {
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
    GL_UNIFORM_BUFFER(GL31.GL_UNIFORM_BUFFER) {
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
    GL_ATOMIC_COUNTER_BUFFER(GL42.GL_ATOMIC_COUNTER_BUFFER) {
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
    GL_QUERY_BUFFER(GL44.GL_QUERY_BUFFER) {
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
    GL_COPY_READ_BUFFER(GL31.GL_COPY_READ_BUFFER) {
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
    GL_COPY_WRITE_BUFFER(GL31.GL_COPY_WRITE_BUFFER) {
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
    GL_DISPATCH_INDIRECT_BUFFER(GL43.GL_DISPATCH_INDIRECT_BUFFER){
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
    GL_DRAW_INDIRECT_BUFFER(GL40.GL_DRAW_INDIRECT_BUFFER) {
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
    GL_ELEMENT_ARRAY_BUFFER(GL15.GL_ELEMENT_ARRAY_BUFFER) {
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
    GL_TEXTURE_BUFFER(GL31.GL_TEXTURE_BUFFER) {
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
    GL_PIXEL_PACK_BUFFER(GL21.GL_PIXEL_PACK_BUFFER) {
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
    GL_PIXEL_UNPACK_BUFFER(GL21.GL_PIXEL_UNPACK_BUFFER) {
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
    GL_SHADER_STORAGE_BUFFER(GL43.GL_SHADER_STORAGE_BUFFER) {
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
    GL_TRANSFORM_FEEDBACK_BUFFER(GL30.GL_TRANSFORM_FEEDBACK_BUFFER) {
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
     * Converts an OpenGL GLenum value to a GLBufferTarget constant. This method
     * will return null if no GLBufferTarget constant matches the GLenum value.
     *
     * @param value the GLenum value to convert.
     * @return the GLBufferTarget constant or null.
     * @since 15.05.27
     */
    public static GLBufferTarget valueOf(final int value) {
        for (GLBufferTarget tgt : values()) {
            if (tgt.value == value) {
                return tgt;
            }
        }

        return null;
    }
}
