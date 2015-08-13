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
public class CLException extends RuntimeException {

    public CLException() {
        super();
    }

    public CLException(final String msg) {
        super(msg);
    }

    public CLException(final Throwable cause) {
        super(cause);
    }

    public CLException(final String msg, final Throwable cause) {
        super(msg, cause);
    }

}
