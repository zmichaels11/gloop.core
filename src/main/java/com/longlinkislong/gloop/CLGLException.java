/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.longlinkislong.gloop;

/**
 *
 * @author zmichaels
 */
@SuppressWarnings("serial")
public class CLGLException extends RuntimeException {
    public CLGLException() {
        super();
    }
    
    public CLGLException(final String msg) {
        super(msg);
    }
    
    public CLGLException(final Throwable cause) {
        super(cause);
    }
    
    public CLGLException(final String msg, final Throwable cause) {
        super(msg, cause);
    }
}
