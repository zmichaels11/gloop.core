/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.longlinkislong.gloop;

import org.lwjgl.opengl.GL32;

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
    GL_ALREADY_SIGNALED(GL32.GL_ALREADY_SIGNALED),
    /**
     * Indicates that at least timeout nanoseconds passed and sync did not
     * become signaled.
     *
     * @since 15.07.06
     */
    GL_TIMEOUT_EXPIRED(GL32.GL_TIMEOUT_EXPIRED),
    /**
     * Indicates that sync was signaled before the timeout expired.
     *
     * @since 15.07.06
     */
    GL_CONDITION_SATISFIED(GL32.GL_CONDITION_SATISFIED),
    /**
     * Indicates that an error occurred. Additionally, an OpenGL error will be
     * generated.
     *
     * @since 15.07.06
     */
    GL_WAIT_FAILED(GL32.GL_WAIT_FAILED);

    final int value;
    
    GLWaitSyncStatus(final int value) {
        this.value = value;
    }
    
    public static GLWaitSyncStatus valueOf(final int glEnum) {
        for(GLWaitSyncStatus status : values()) {
            if(status.value == glEnum) {
                return status;
            }
        }
        
        return null;
    }
}
