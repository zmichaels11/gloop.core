/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.longlinkislong.gloop;

import org.lwjgl.opengl.GL32;

/**
 * The result of a SyncStatus task.
 *
 * @author zmichaels
 * @since 15.07.06
 */
public enum GLSyncStatus {

    /**
     * Indicates that the GLSync has signaled.
     *
     * @since 15.07.06
     */
    GL_SIGNALED(GL32.GL_SIGNALED),
    /**
     * Indicates that the GLSync has not yet signaled.
     *
     * @since 15.07.06
     */
    GL_UNSIGNALED(GL32.GL_UNSIGNALED);

    final int value;

    GLSyncStatus(final int value) {
        this.value = value;
    }

    /**
     * Converts a GLenum value to a GLSyncStatus value.
     *
     * @param glEnum the glEnum value to check.
     * @return the associated GLSyncStatus if applicable.
     * @since 15.07.06
     */
    public static GLSyncStatus valueOf(final int glEnum) {
        for (GLSyncStatus status : values()) {
            if (status.value == glEnum) {
                return status;
            }
        }

        return null;
    }
}
