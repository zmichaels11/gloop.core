/* 
 * Copyright (c) 2014-2016, longlinkislong.com
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

import java.util.Objects;

/**
 * The base object for all OpenGL structures. Each GLObject retains information
 * regarding the thread it was created on.
 *
 * @author zmichaels
 * @since 15.05.27
 */
public abstract class GLObject {
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
        this.thread = Objects.requireNonNull(thread);
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

    /**
     * Checks if the OpenGL object can run in the current thread.
     *
     * @throws GLException if the OpenGL object cannot run in the current
     * thread.
     * @since 15.10.30
     */
    protected final void checkThread() {
        if (!this.isShareable() && !getThread().isCurrent()) {
            throw new GLException(
                    String.format(
                            "%s is not a shareable OpenGL object!",
                            this.getClass().getSimpleName()));
        }
    }

    /**
     * Checks if the GLObject is shareable.
     *
     * @return true if the OpenGL object can be accessed in shared contexts.
     * @since 15.10.30
     */
    public boolean isShareable() {
        return true; // most objects are shareable.
    }

    /**
     * Called when a context change occurs.
     *
     * @return self if the object is alive. Otherwise it should return null.
     * @since 16.08.31
     */
    protected GLObject migrate() {
        return this;
    }

    /**
     * Retrieves the time since the object was last used in nanoseconds. This
     * function requires no minimum resolution and may always return a value of
     * 0, indicating that the object is always in use.
     *
     * @return the time since the object was last used.
     * @since 16.09.06
     */
    public long getTimeSinceLastUsed() {
        return 0L;
    }

    protected transient long lastUsedTime = 0L;

    /**
     * Updates the internal timer used for getting the time since last update.
     * The default behavior for this method is to just set the timer to
     * [code]getThread().getFrameTime()[/code]. However, it may be replaced with
     * any precision timer as long as it resolves in nanoseconds.
     *
     * @since 16.09.06
     */
    public void updateTimeUsed() {
        this.lastUsedTime = this.getThread().getFrameTime();
    }
}
