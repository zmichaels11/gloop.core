/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
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
     * @param msg the message to use
     * @param cause the cause to use
     * @since 15.05.27
     */
    public GLException(final String msg, final Throwable cause) {
        super(msg, cause);
    }
}
