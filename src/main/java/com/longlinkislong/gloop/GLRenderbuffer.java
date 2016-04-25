/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.longlinkislong.gloop;

import com.longlinkislong.gloop.glspi.Renderbuffer;
import java.util.Objects;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.Marker;
import org.slf4j.MarkerFactory;

/**
 *
 * @author zmichaels
 */
public class GLRenderbuffer extends GLObject {

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

    public final void setName(final CharSequence name) {
        GLTask.create(() -> {
            LOGGER.trace(GL_MARKER, "Renamed GLRenderbuffer[{}] to GLRenderbuffer[{}]",
                    this.name,
                    name);

            this.name = name.toString();
        }).glRun(this.getThread());
    }

    public final String getName() {
        return this.name;
    }

    public GLRenderbuffer(final GLTextureInternalFormat internalFormat, final int width, final int height) {
        this(GLThread.getAny(), internalFormat, width, height);
    }

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

    public boolean isValid() {
        return renderbuffer != null && renderbuffer.isValid();
    }

    public final void init() {
        new InitTask().glRun(this.getThread());
    }

    public final void delete() {
        new DeleteTask().glRun(this.getThread());
    }

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

            LOGGER.trace(GL_MARKER, "Initialized GLRenderbuffer[{}]!", GLRenderbuffer.this.name);
            LOGGER.trace(GL_MARKER, "############### End GLRenderer Init Task ###############");
        }
    }

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
            }

            LOGGER.trace(GL_MARKER, "############### End GLRendebruffer Delete Task ###############");
        }
    }
}
