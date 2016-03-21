/* 
 * Copyright (c) 2016, longlinkislong.com
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
 * ALException represents an error that occurred while interfacing with OpenAL.
 * @author zmichaels
 * @since 16.03.21
 */
@SuppressWarnings("serial")
public class ALException extends RuntimeException {
    /**
     * Constructs a new ALException with no message nor cause.
     * @since 16.03.21
     */
    public ALException() {
        super();
    }
    
    /**
     * Constructs a new ALException with message and no cause.
     * @param msg the message.
     * @since 16.03.21
     */
    public ALException(final String msg) {
        super(msg);
    }
    
    /**
     * Constructs a new ALException with a cause and no message.
     * @param cause the cause of the ALException.
     * @since 16.03.21
     */
    public ALException(final Throwable cause) {
        super(cause);
    }
    
    /**
     * Constructs a new ALException with the supplied message and cause.
     * @param msg the message.
     * @param cause the cause of the ALException.
     * @since 16.03.21
     */
    public ALException(final String msg, final Throwable cause) {
        super(msg, cause);
    }
}
