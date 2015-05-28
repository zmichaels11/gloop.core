/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.longlinkislong.gloop;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.IntBuffer;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import org.lwjgl.opengl.GL20;
import org.lwjgl.opengl.GL30;

/**
 *
 * @author zmichaels
 */
public class GLFramebuffer extends GLObject {

    private static final int INVALID_FRAMEBUFFER_ID = -1;
    private int framebufferId = INVALID_FRAMEBUFFER_ID;
    private int nextColorAttachment = GL30.GL_COLOR_ATTACHMENT0;
    private final Map<String, Integer> attachments = new HashMap<>();

    public GLFramebuffer() {
        super();

        this.init();
    }

    public GLFramebuffer(final GLThread thread) {
        super(thread);

        this.init();
    }

    private GLFramebuffer(final GLThread thread, final int fbId) {
        super(thread);

        this.framebufferId = fbId;
    }

    private static final Map<GLThread, GLFramebuffer> defaultFramebuffers = new HashMap<>();

    public static GLFramebuffer getDefaultFramebuffer() {
        return getDefaultFramebuffer(GLThread.getDefaultInstance());
    }

    public static GLFramebuffer getDefaultFramebuffer(final GLThread thread) {
        if (!defaultFramebuffers.containsKey(thread)) {
            final GLFramebuffer fb = new GLFramebuffer(thread, 0);

            defaultFramebuffers.put(thread, fb);

            return fb;
        } else {
            return defaultFramebuffers.get(thread);
        }
    }

    public boolean isValid() {
        return this.framebufferId != INVALID_FRAMEBUFFER_ID;
    }

    public final void init() {
        new InitTask().glRun(this.getThread());
    }

    public class InitTask extends GLTask {

        @Override
        public void run() {
            if (GLFramebuffer.this.isValid()) {
                throw new GLException("GLFramebuffer already initialized!");
            }

            GLFramebuffer.this.framebufferId = GL30.glGenFramebuffers();
        }

    }

    public class DeleteTask extends GLTask {

        @Override
        public void run() {
            if (!GLFramebuffer.this.isValid()) {
                throw new GLException("GLFramebuffer is not valid!");
            }

            GL30.glDeleteFramebuffers(GLFramebuffer.this.framebufferId);
            GLFramebuffer.this.framebufferId = INVALID_FRAMEBUFFER_ID;
        }
    }

    public class BindTask extends GLTask {

        final IntBuffer attachments;

        public BindTask(final CharSequence[] attachments, final int offset, final int length) {
            if (length > 0) {
                this.attachments = ByteBuffer.allocateDirect(length * 4).order(ByteOrder.nativeOrder()).asIntBuffer();

                for (int i = offset; i < length; i++) {
                    final String name = attachments[i].toString();

                    if (!GLFramebuffer.this.attachments.containsKey(name)) {
                        throw new GLException("Invalid attachment [" + name + "]!");
                    }

                    final int attachId = GLFramebuffer.this.attachments.get(name);

                    this.attachments.put(attachId);
                }

                this.attachments.flip();
            } else {
                this.attachments = null;
            }
        }

        @Override
        public void run() {
            if (!GLFramebuffer.this.isValid()) {
                throw new GLException("Invalid GLFramebuffer!");
            }

            GL30.glBindFramebuffer(GL30.GL_FRAMEBUFFER, GLFramebuffer.this.framebufferId);
            if (this.attachments != null) {
                GL20.glDrawBuffers(this.attachments);
            }
        }
    }

    public class AddDepthStencilAttachmentTask extends GLTask {

        final GLTexture depthStencilAttachment;
        final int level;

        public AddDepthStencilAttachmentTask(
                final GLTexture attachment,
                final int level) {

            Objects.requireNonNull(this.depthStencilAttachment = attachment);
            if ((this.level = level) < 0) {
                throw new GLException("Mipmap level cannot be less than 0!");
            }
        }

        @Override
        public void run() {
            if (!GLFramebuffer.this.isValid()) {
                throw new GLException("Invalid GLFramebuffer!");
            }

            GL30.glBindFramebuffer(GL30.GL_FRAMEBUFFER, GLFramebuffer.this.framebufferId);
            final GLTextureTarget target = this.depthStencilAttachment.getTarget();

            switch (target) {
                case GL_TEXTURE_1D:
                    GL30.glFramebufferTexture1D(
                            GL30.GL_FRAMEBUFFER,
                            GL30.GL_DEPTH_STENCIL_ATTACHMENT,
                            target.value,
                            this.depthStencilAttachment.textureId,
                            this.level);
                    break;
                case GL_TEXTURE_2D:
                    GL30.glFramebufferTexture2D(
                            GL30.GL_FRAMEBUFFER,
                            GL30.GL_DEPTH_ATTACHMENT,
                            target.value,
                            this.depthStencilAttachment.textureId,
                            this.level);
                    break;
                default:
                    throw new GLException("Texture target type: " + target + " is currently unsupported.");
            }

            GL30.glBindFramebuffer(GL30.GL_FRAMEBUFFER, 0);
        }
    }

    public class AddDepthAttachmentTask extends GLTask {

        final GLTexture depthAttachment;
        final int level;

        public AddDepthAttachmentTask(
                final GLTexture attachment,
                final int level) {

            Objects.requireNonNull(this.depthAttachment = attachment);
            if ((this.level = level) < 0) {
                throw new GLException("Invalid mipmap level!");
            }
        }

        @Override
        public void run() {
            if (!GLFramebuffer.this.isValid()) {
                throw new GLException("Invalid GLFramebuffer!");
            }

            GL30.glBindFramebuffer(GL30.GL_FRAMEBUFFER, GLFramebuffer.this.framebufferId);
            final GLTextureTarget target = this.depthAttachment.getTarget();

            switch (target) {
                case GL_TEXTURE_1D:
                    GL30.glFramebufferTexture1D(
                            GL30.GL_FRAMEBUFFER,
                            GL30.GL_DEPTH_ATTACHMENT,
                            target.value,
                            this.depthAttachment.textureId,
                            this.level);
                    break;
                case GL_TEXTURE_2D:
                    GL30.glFramebufferTexture2D(
                            GL30.GL_FRAMEBUFFER,
                            GL30.GL_DEPTH_ATTACHMENT,
                            target.value,
                            this.depthAttachment.textureId,
                            this.level);
                    break;
                default:
                    throw new GLException("Texture target type: " + target + " is currently unsupported.");
            }

            GL30.glBindFramebuffer(GL30.GL_FRAMEBUFFER, 0);
        }
    }

    public class AddColorAttachmentTask extends GLTask {

        final GLTexture colorAttachment;
        final int attachmentId;
        final int level;

        public AddColorAttachmentTask(
                final CharSequence name,
                final GLTexture attachment,
                final int level) {

            Objects.requireNonNull(this.colorAttachment = attachment);
            if ((this.level = level) < 0) {
                throw new GLException("Color attachment level cannot be less than 0!");
            }

            this.attachmentId = GLFramebuffer.this.nextColorAttachment;
            GLFramebuffer.this.nextColorAttachment++;

            GLFramebuffer.this.attachments.put(name.toString(), attachmentId);
        }

        @Override
        public void run() {
            if (!GLFramebuffer.this.isValid()) {
                throw new GLException("Invalid GLFramebuffer!");
            }

            GL30.glBindFramebuffer(GL30.GL_FRAMEBUFFER, GLFramebuffer.this.framebufferId);
            final GLTextureTarget target = this.colorAttachment.getTarget();

            switch (target) {
                case GL_TEXTURE_1D:
                    GL30.glFramebufferTexture1D(
                            GL30.GL_FRAMEBUFFER,
                            this.attachmentId,
                            target.value,
                            this.colorAttachment.textureId,
                            this.level);
                    break;
                case GL_TEXTURE_2D:
                    GL30.glFramebufferTexture2D(
                            GL30.GL_FRAMEBUFFER,
                            this.attachmentId,
                            target.value,
                            this.colorAttachment.textureId,
                            this.level);
                    break;
                default:
                    throw new GLException("Texture target type: " + target + " is currently unsupported.");
            }

            GL30.glBindFramebuffer(GL30.GL_FRAMEBUFFER, 0);
        }

    }
}
