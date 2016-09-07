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

import com.longlinkislong.gloop.glspi.Renderbuffer;
import java.util.Objects;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.Marker;
import org.slf4j.MarkerFactory;

/**
 * A GLRenderbuffer is a write-only component for GLFramebuffer. The driver may
 * optimize these over GLTexture, which is read-write.
 *
 * @author zmichaels
 * @since 16.06.10
 */
public class GLRenderbuffer extends GLObject {

    /**
     * The type of attachment the GLRenderbuffer is used for.
     *
     * @since 16.06.10
     */
    enum TargetType {
        COLOR_ATTACHMENT,
        DEPTH_ATTACHMENT,
        STENCIL_ATTACHMENT,
        DEPTH_STENCIL_ATTACHMENT
    }

    private static final Marker GL_MARKER = MarkerFactory.getMarker("GLOOP");
    private static final Logger LOGGER = LoggerFactory.getLogger("GLRenderbuffer");

    volatile transient Renderbuffer renderbuffer = null;
    private final GLTextureInternalFormat internalFormat;
    private final int width;
    private final int height;
    final TargetType target;
    private String name = "";

    /**
     * Sets the name of the GLRenderbuffer.
     *
     * @param name the new name.
     * @since 16.06.10
     */
    public final void setName(final CharSequence name) {
        GLTask.create(() -> {
            LOGGER.trace(GL_MARKER, "Renamed GLRenderbuffer[{}] to GLRenderbuffer[{}]",
                    this.name,
                    name);

            this.name = name.toString();
        }).glRun(this.getThread());
    }

    /**
     * Retrieves the name of the GLRenderbuffer.
     *
     * @return the name
     * @since 16.06.10
     */
    public final String getName() {
        return this.name;
    }

    /**
     * Constructs a new GLRenderbuffer.
     *
     * @param internalFormat the pixel format.
     * @param width the width.
     * @param height the height.
     * @since 16.06.10
     */
    public GLRenderbuffer(final GLTextureInternalFormat internalFormat, final int width, final int height) {
        this(GLThread.getAny(), internalFormat, width, height);
    }

    /**
     * Constructs a new GLRenderbuffer on the given thread.
     *
     * @param thread the GLThread to construct the GLRenderbuffer on.
     * @param internalFormat the pixel format.
     * @param width the width.
     * @param height the height.
     * @since 16.06.10
     */
    public GLRenderbuffer(final GLThread thread, GLTextureInternalFormat internalFormat, final int width, final int height) {
        super(thread);

        this.internalFormat = Objects.requireNonNull(internalFormat);

        if ((this.width = width) < 1) {
            throw new IllegalArgumentException("Width cannot be less than 1!");
        } else if ((this.height = height) < 1) {
            throw new IllegalArgumentException("Height cannot be less than 1!");
        }

        switch (internalFormat) {
            case GL_DEPTH_COMPONENT16:
            case GL_DEPTH_COMPONENT24:
            case GL_DEPTH_COMPONENT32:
            case GL_DEPTH_COMPONENT32F:
                this.target = TargetType.DEPTH_ATTACHMENT;
                break;
            case GL_DEPTH24_STENCIL8:
            case GL_DEPTH32F_STENCIL8:
                this.target = TargetType.DEPTH_STENCIL_ATTACHMENT;
                break;
            default:
                this.target = TargetType.COLOR_ATTACHMENT;
                break;
        }

        this.init();
    }

    /**
     * Checks if the GLRenderbuffer is valid.
     *
     * @return true if it is a valid object.
     * @since 16.06.10
     */
    public boolean isValid() {
        return renderbuffer != null && renderbuffer.isValid();
    }

    /**
     * Initializes the GLRenderbuffer. This method will only succeed if this
     * GLRenderbuffer has already been deleted. It is preferred not to reuse
     * GLObjects.
     *
     * @since 16.06.10
     */
    public final void init() {
        new InitTask().glRun(this.getThread());
    }

    /**
     * Deletes the GLRenderbuffer. This will request that all resources are
     * freed and all states set by the GLRenderbuffer are reset.
     *
     * @since 16.06.10
     */
    public final void delete() {
        new DeleteTask().glRun(this.getThread());
    }

    /**
     * A GLTask that initializes this GLRenderbuffer. Executing this task is
     * only valid if the GLRenderbuffer is in an invalid state (happens after a
     * DeleteTask is executed).
     *
     * @since 16.06.10
     */
    public final class InitTask extends GLTask {

        @Override
        public void run() {
            LOGGER.trace(GL_MARKER, "############### Start GLRenderbuffer Init Task ###############");

            if (GLRenderbuffer.this.isValid()) {
                throw new GLException("GLRenderbuffer already initialized!");
            }

            GLRenderbuffer.this.renderbuffer = GLTools.getDriverInstance().renderbufferCreate(
                    GLRenderbuffer.this.internalFormat.value,
                    GLRenderbuffer.this.width,
                    GLRenderbuffer.this.height);

            GLRenderbuffer.this.name = "id=" + renderbuffer.hashCode();
            GLRenderbuffer.this.updateTimeUsed();

            LOGGER.trace(GL_MARKER, "Initialized GLRenderbuffer[{}]!", GLRenderbuffer.this.name);
            LOGGER.trace(GL_MARKER, "############### End GLRenderer Init Task ###############");
        }
    }

    /**
     * A GLTask that deletes the GLRenderbuffer. This will free all resources
     * allocated by the GLRenderbuffer and reset any states it set.
     *
     * @since 16.06.10
     */
    public final class DeleteTask extends GLTask {

        @Override
        public void run() {
            LOGGER.trace(GL_MARKER, "############### Start GLRenderbuffer Delete Task ###############");
            LOGGER.trace(GL_MARKER, "\tDeleting GLRenderbuffer[{}]", GLRenderbuffer.this.getName());

            if (!GLRenderbuffer.this.isValid()) {
                LOGGER.warn(GL_MARKER, "Attempted to delete invalid GLRenderbuffer!");
            } else {
                GLTools.getDriverInstance().renderbufferDelete(GLRenderbuffer.this.renderbuffer);
                GLRenderbuffer.this.renderbuffer = null;
                GLRenderbuffer.this.lastUsedTime = 0L;
            }

            LOGGER.trace(GL_MARKER, "############### End GLRendebruffer Delete Task ###############");
        }
    }

    @Override
    public long getTimeSinceLastUsed() {
        return (System.nanoTime() - this.lastUsedTime);
    }
}
