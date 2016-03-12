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
public class ALException extends RuntimeException {
    public ALException() {
        super();
    }
    
    public ALException(final String msg) {
        super(msg);
    }
    
    public ALException(final Throwable cause) {
        super(cause);
    }
    
    public ALException(final String msg, final Throwable cause) {
        super(msg, cause);
    }
}
