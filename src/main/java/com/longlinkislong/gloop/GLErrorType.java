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
 * Collection of possible errors that may be reported by OpenGL.
 *
 * @author zmichaels
 * @since 16.06.10
 */
public enum GLErrorType {
    /**
     * The wrong enumeration type was supplied to an OpenGL function.
     *
     * @since 16.06.10
     */
    GL_INVALID_ENUM(1280, "An unacceptable value is specified for an enumerated argument."),
    /**
     * The wrong enumeration value was supplied to an OpenGL function.
     *
     * @since 16.06.10
     */
    GL_INVALID_VALUE(1281, "A numeric argument is out of range."),
    /**
     * An OpenGL function was called in an invalid state.
     *
     * @since 16.06.10
     */
    GL_INVALID_OPERATION(1282, "The specified operation is not allowed in the current state."),
    /**
     * An operation on a framebuffer object was attempted when the framebuffer
     * is not complete. This may also apply to attempting to perform operations
     * on the default framebuffer.
     *
     * @since 16.06.10
     */
    GL_INVALID_FRAMEBUFFER_OPERATION(1286, "The framebuffer object is not complete."),
    /**
     * OpenGL ran out of accessible memory.
     *
     * @since 16.06.10
     */
    GL_OUT_OF_MEMORY(1285, "There is not enough memory left to execute the command."),
    /**
     * An operation on a stack held by OpenGL has experienced undeflow.
     *
     * @deprecated the stacks held by OpenGL are only accessible via legacy
     * functions.
     * @since 16.06.10
     */
    GL_STACK_UNDERFLOW(1284, "An attempt has been made to perform an operation that would cause an internal stack to underflow."),
    /**
     * An operation on a stack held by OpenGL has experienced overflow.
     *
     * @deprecated the stacks held by OpenGL are only accessible via legacy
     * functions.
     */
    GL_STACK_OVERFLOW(1283, "An attempt has been made to perform an operation that would cause an internal stack to overflow.");

    final int value;
    final String msg;

    GLErrorType(final int value, final String msg) {
        this.value = value;
        this.msg = msg;
    }

    /**
     * Translates a GLErrorType into a GLException. The error message is used as
     * the exception message.
     *
     * @return the GLException.
     * @since 15.12.18
     */
    public GLException toGLException() {
        return new GLException(this.name() + ": " + this.msg);
    }

    /**
     * Translates a GLErrorType into a GLException. The error message is used as
     * the exception message.
     *
     * @param cause the cause of the exception.
     * @return the GLException.
     * @since 15.12.18
     */
    public GLException toGLException(final Throwable cause) {
        return new GLException(this.name() + ": " + this.msg, cause);
    }

    /**
     * Translates a GLenum into the corresponding GLErrorType.
     *
     * @param glError the GLenum value.
     * @return the GLErrorType wrapped in an Optional.
     * @since 15.12.18
     */
    public static Optional<GLErrorType> of(final int glError) {
        for (GLErrorType type : values()) {
            if (type.value == glError) {
                return Optional.of(type);
            }
        }

        return Optional.empty();
    }
}
