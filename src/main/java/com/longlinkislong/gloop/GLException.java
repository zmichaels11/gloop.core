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

    public static final boolean ENABLE_STACK_TRACE = Boolean.getBoolean("com.longlinkislong.gloop.glexception.enable_stack_trace");

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

    @Override
    public Throwable fillInStackTrace() {
        if (ENABLE_STACK_TRACE) {
            return super.fillInStackTrace();
        } else {
            return this;
        }
    }

    @SuppressWarnings("serial")
    public static final class DataStoreException extends GLException {
        public DataStoreException() {}
        
        public DataStoreException(final String msg) {
            super(msg);
        }
        
        public DataStoreException(final String msg, final Throwable cause) {
            super(msg, cause);
        }
        
        public DataStoreException(final Throwable cause) {
            super(cause);
        }
    }

    @SuppressWarnings("serial")
    public static final class InvalidThreadException extends GLException {

        public InvalidThreadException() {
        }

        public InvalidThreadException(final String msg) {
            super(msg);
        }

        public InvalidThreadException(final Throwable cause) {
            super(cause);
        }

        public InvalidThreadException(final String msg, final Throwable cause) {
            super(msg, cause);
        }
    }
    
    @SuppressWarnings("serial")
    public static final class InvalidValueException extends GLException {
        public InvalidValueException() {
            
        }
        
        public InvalidValueException(final String msg) {
            super(msg);
        }
        
        public InvalidValueException(final String msg, final Throwable cause) {
            super(msg, cause);
        }
        
        public InvalidValueException(final Throwable cause) {
            super(cause);
        }
    }

    @SuppressWarnings("serial")
    public static final class InvalidGLEnumException extends GLException {

        public InvalidGLEnumException() {
        }

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
    
    @SuppressWarnings("serial")
    public static final class InvalidTypeException extends GLException {
        public InvalidTypeException() {
            
        }
        
        public InvalidTypeException(final String msg) {
            super(msg);
        }
        
        public InvalidTypeException(final Throwable cause) {
            super(cause);
        }
        
        public InvalidTypeException(final String msg, final Throwable cause) {
            super(msg, cause);
        }
    }
    
    public static final class InvalidStateException extends GLException {
        public InvalidStateException() {
            
        }
        
        public InvalidStateException(final String msg) {
            super(msg);
        }
        
        public InvalidStateException(final Throwable cause) {
            super(cause);
        }
        
        public InvalidStateException(final String msg, Throwable cause) {
            super(msg, cause);
        }
    }
}
