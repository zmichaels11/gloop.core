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

/**
 * An exception that represents a caught error in a GLTask or GLQuery.
 *
 * @author zmichaels
 * @since 15.05.27
 */
@SuppressWarnings("serial")
public class GLException extends RuntimeException {

    /**
     * Constructs a new GLException with no message or cause.
     *
     * @since 15.05.27
     */
    public GLException() {
    }

    /**
     * Constructs a new GLException with the specified message and no cause.
     * This should be used for the root cause of the exception. If the exception
     * is caused elsewhere, the cause should also be included.
     *
     * @param msg the related message to the exception.
     * @since 15.05.27
     */
    public GLException(final String msg) {
        super(msg);
    }

    /**
     * Constructs a new GLException with only a cause. It is recommended to
     * include a message with the exception.
     *
     * @param cause the cause of the exception.
     * @since 15.05.27
     */
    public GLException(final Throwable cause) {
        super(cause);
    }

    /**
     * Constructs a new GLException with both a message and a cause.
     *
     * @param msg the message to use
     * @param cause the cause to use
     * @since 15.05.27
     */
    public GLException(final String msg, final Throwable cause) {
        super(msg, cause);
    }

    public static class InvalidGLEnumException extends GLException {
        public InvalidGLEnumException() {}
        public InvalidGLEnumException(final String msg) {
            super(msg);
        }
        public InvalidGLEnumException(final Throwable cause) {
            super(cause);
        }
        public InvalidGLEnumException(final String msg, final Throwable cause) {
            super(msg, cause);
        }
    }
}
