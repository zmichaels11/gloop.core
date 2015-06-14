/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.longlinkislong.gloop;

import java.util.Optional;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL30;

/**
 *
 * @author zmichaels
 */
public enum GLErrorType {
    GL_INVALID_ENUM(GL11.GL_INVALID_ENUM, "An unacceptable value is specified for an enumerated argument."),
    GL_INVALID_VALUE(GL11.GL_INVALID_VALUE, "A numeric argument is out of range."),
    GL_INVALID_OPERATION(GL11.GL_INVALID_OPERATION, "The specified operation is not allowed in the current state."),
    GL_INVALID_FRAMEBUFFER_OPERATION(GL30.GL_INVALID_FRAMEBUFFER_OPERATION, "The framebuffer object is not complete."),
    GL_OUT_OF_MEMORY(GL11.GL_OUT_OF_MEMORY, "There is not enough memory left to execute the command."),
    GL_STACK_UNDERFLOW(GL11.GL_STACK_UNDERFLOW, "An attempt has been made to perform an operation that would cause an internal stack to underflow."),
    GL_STACK_OVERFLOW(GL11.GL_STACK_OVERFLOW, "An attempt has been made to perform an operation that would cause an internal stack to overflow.");
    
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
    
    public static GLErrorType valueOf(final int value) {
        for(GLErrorType err : values()) {
            if(err.value == value) {
                return err;
            }
        }
        
        return null;
    }
    
    public static Optional<GLErrorType> getGLError() {
        return Optional.ofNullable(valueOf(GL11.glGetError()));
    }
}
