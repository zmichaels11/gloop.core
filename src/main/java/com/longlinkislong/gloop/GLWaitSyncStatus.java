/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.longlinkislong.gloop;

import java.util.Optional;

/**
 * Return statuses for a GPU sync block.
 *
 * @see
 * <a href="https://www.opengl.org/sdk/docs/man4/xhtml/glClientWaitSync.xml">OpenGL
 * SDK</a>
 * @author zmichaels
 * @since 15.07.06
 */
public enum GLWaitSyncStatus {

    /**
     * Indicates that sync was signaled already at the time that
     * glClientWaitSync was called.
     *
     * @since 15.07.06
     */
    GL_ALREADY_SIGNALED(37146),
    /**
     * Indicates that at least timeout nanoseconds passed and sync did not
     * become signaled.
     *
     * @since 15.07.06
     */
    GL_TIMEOUT_EXPIRED(37147),
    /**
     * Indicates that sync was signaled before the timeout expired.
     *
     * @since 15.07.06
     */
    GL_CONDITION_SATISFIED(37148),
    /**
     * Indicates that an error occurred. Additionally, an OpenGL error will be
     * generated.
     *
     * @since 15.07.06
     */
    GL_WAIT_FAILED(37149);

    final int value;
    
    GLWaitSyncStatus(final int value) {
        this.value = value;
    }
    
    @Deprecated
    public static GLWaitSyncStatus valueOf(final int glEnum) {
        return of(glEnum).get();
    }
    
    public static Optional<GLWaitSyncStatus> of(final int glEnum) {
        for(GLWaitSyncStatus status : values()) {
            if(status.value == glEnum) {
                return Optional.of(status);
            }
        }
        
        return Optional.empty();
    }
}
