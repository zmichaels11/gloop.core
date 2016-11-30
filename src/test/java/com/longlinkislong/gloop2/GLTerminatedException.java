/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.longlinkislong.gloop2;

/**
 *
 * @author zmichaels
 */
@SuppressWarnings("serial")
public final class GLTerminatedException extends RuntimeException {
    public GLTerminatedException(final String msg) {
        super(msg);
    }
    
    public GLTerminatedException(final Throwable cause) {
        super(cause);
    }
    
    public GLTerminatedException(final String msg, final Throwable cause) {
        super(msg, cause);
    }
    
    public GLTerminatedException() {
        super();
    }
}
