/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.longlinkislong.gloop;

import java.util.Optional;

/**
 *
 * @author zmichaels
 */
public enum GLErrorType {
    GL_INVALID_ENUM(1280, "An unacceptable value is specified for an enumerated argument."),
    GL_INVALID_VALUE(1281, "A numeric argument is out of range."),
    GL_INVALID_OPERATION(1282, "The specified operation is not allowed in the current state."),
    GL_INVALID_FRAMEBUFFER_OPERATION(1286, "The framebuffer object is not complete."),
    GL_OUT_OF_MEMORY(1285, "There is not enough memory left to execute the command."),
    GL_STACK_UNDERFLOW(1284, "An attempt has been made to perform an operation that would cause an internal stack to underflow."),
    GL_STACK_OVERFLOW(1283, "An attempt has been made to perform an operation that would cause an internal stack to overflow.");
    
    final int value;
    final String msg;

    GLErrorType(final int value, final String msg) {
        this.value = value;
        this.msg = msg;
    }
    
    public GLException toGLException() {
        return new GLException(this.name() + ": " + this.msg);
    }
    
    public GLException toGLException(final Throwable cause) {
        return new GLException(this.name() + ": " + this.msg, cause);
    }
    
    @Deprecated
    public static GLErrorType valueOf(final int value) {
        return of(value).get();
    }
    
    public static Optional<GLErrorType> of(final int glError) {
        for(GLErrorType type : values()) {
            if(type.value == glError) {
                return Optional.of(type);
            }
        }
        
        return Optional.empty();
    }
}
