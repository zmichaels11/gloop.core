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
public class GLException extends RuntimeException {
    public GLException() {}
    
    public GLException(final String msg) {
        super(msg);
    }
    
    public GLException(final Throwable cause) {
        super(cause);
    }
    
    public GLException(final String msg, final Throwable cause) {
        super(msg, cause);
    }
}
