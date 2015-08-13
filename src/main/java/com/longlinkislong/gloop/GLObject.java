/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.longlinkislong.gloop;

import java.util.Objects;

/**
 * The base object for all OpenGL structures. Each GLObject retains information
 * regarding the thread it was created on.
 *
 * @author zmichaels
 * @since 15.05.27
 */
public abstract class GLObject {

    static {
        NativeTools.getInstance().autoLoad();
    }

    private final GLThread thread;

    /**
     * Constructs a new GLObject associated with the default thread.
     *
     * @since 15.05.13
     */
    public GLObject() {
        this.thread = GLThread.getDefaultInstance();
    }

    /**
     * Constructs a new GLObject and associates a thread to it.
     *
     * @param thread the thread to associate.
     * @since 15.05.13
     */
    public GLObject(final GLThread thread) {
        Objects.requireNonNull(thread);
        this.thread = thread;
    }

    /**
     * Retrieves the GLThread associated with the GLObject.
     *
     * @return the GLThread
     * @since 15.05.13
     */
    public GLThread getThread() {
        return this.thread;
    }
}
