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
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL20;
import org.lwjgl.opengl.GL30;

/**
 * An OpenGL object that represents a framebuffer.
 *
 * @author zmichaels
 * @since 15.07.06
 */
public class GLFramebuffer extends GLObject {

    private static final int INVALID_FRAMEBUFFER_ID = -1;
    private int framebufferId = INVALID_FRAMEBUFFER_ID;
    private int nextColorAttachment = GL30.GL_COLOR_ATTACHMENT0;
    private final Map<String, Integer> attachments = new HashMap<>();
    private final boolean isLocked;

    /**
     * Constructs a new GLFramebuffer on the default OpenGL thread.
     *
     * @since 15.07.06
     */
    public GLFramebuffer() {
        this(GLThread.getDefaultInstance());
    }

    /**
     * Constructs a new GLFramebuffer on the specified OpenGL thread.
     *
     * @param thread the OpenGL thread.
     * @since 15.07.06
     */
    public GLFramebuffer(final GLThread thread) {
        super(thread);

        this.isLocked = false;
        this.init();
    }

    private GLFramebuffer(final GLThread thread, final int fbId) {
        super(thread);

        this.isLocked = true;
        this.framebufferId = fbId;
    }

    private static final Map<GLThread, GLFramebuffer> DEFAULT_FRAMEBUFFERS = new HashMap<>();

    /**
     * Retrieves the default framebuffer associated with the default OpenGL
     * thread.
     *
     * @return the default framebuffer.
     * @since 15.07.06
     */
    public static GLFramebuffer getDefaultFramebuffer() {
        return getDefaultFramebuffer(GLThread.getDefaultInstance());
    }

    /**
     * Retrieves the default framebuffer associated with the specified OpenGL
     * thread.
     *
     * @param thread the OpenGL thread.
     * @return the framebuffer.
     * @since 15.07.06
     */
    public static GLFramebuffer getDefaultFramebuffer(final GLThread thread) {
        if (!DEFAULT_FRAMEBUFFERS.containsKey(thread)) {
            final GLFramebuffer fb = new GLFramebuffer(thread, 0);            

            DEFAULT_FRAMEBUFFERS.put(thread, fb);

            return fb;
        } else {
            return DEFAULT_FRAMEBUFFERS.get(thread);
        }
    }

    /**
     * Checks if the GLFramebuffer is current.
     *
     * @return true if the framebuffer is current.
     * @since 15.07.06
     */
    public boolean isValid() {
        return this.framebufferId != INVALID_FRAMEBUFFER_ID;
    }

    /**
     * Initializes the GLFramebuffer.
     *
     * @since 15.07.06
     */
    public final void init() {
        new InitTask().glRun(this.getThread());
    }

    /**
     * A GLTask that initializes the framebuffer object.
     *
     * @since 15.07.06
     */
    public class InitTask extends GLTask {

        @Override
        public void run() {
            if (GLFramebuffer.this.isLocked) {
                throw new GLException("Cannot initialize null instance of GLFramebuffer!");
            } else if (GLFramebuffer.this.isValid()) {
                throw new GLException("GLFramebuffer already initialized!");
            }

            GLFramebuffer.this.framebufferId = GL30.glGenFramebuffers();

            assert GL11.glGetError() == GL11.GL_NO_ERROR : String.format("glGenFramebuffers() = %d failed!", GLFramebuffer.this.framebufferId);
        }

    }

    /**
     * Deletes the framebuffer object.
     *
     * @since 15.07.06
     */
    public void delete() {
        new DeleteTask().glRun(this.getThread());
    }

    /**
     * A GLTask that deletes the framebuffer object.
     *
     * @since 15.07.06
     */
    public class DeleteTask extends GLTask {

        @Override
        public void run() {
            if(GLFramebuffer.this.isLocked) {
                throw new GLException("Cannot delete null instance of GLFramebuffer!");
            } else if (!GLFramebuffer.this.isValid()) {
                throw new GLException("GLFramebuffer is not valid!");
            }

            GL30.glDeleteFramebuffers(GLFramebuffer.this.framebufferId);

            assert GL11.glGetError() == GL11.GL_NO_ERROR : String.format("glDeleteFramebuffers(%d) failed!", GLFramebuffer.this.framebufferId);

            GLFramebuffer.this.framebufferId = INVALID_FRAMEBUFFER_ID;
        }
    }

    private BindTask lastBindTask = null;

    /**
     * Binds the select color attachments to the render buffer.
     *
     * @param attachments the color attachments to bind.
     * @since 15.07.06
     */
    public void bind(final CharSequence... attachments) {
        this.bind(attachments, 0, attachments.length);
    }

    /**
     * Binds the select color attachments to the render buffer. The color
     * attachments are read from an array.
     *
     * @param attachments the color attachments array.
     * @param offset the offset to start reading from the color attachment
     * array.
     * @param length the number of color attachments to bind.
     * @since 15.07.06
     */
    public void bind(final CharSequence[] attachments, final int offset, final int length) {
        if (this.lastBindTask != null) {
            for (int i = offset; i < length; i++) {
                if (!attachments[i].toString().equals(this.lastBindTask.attachmentNames[i - offset])) {
                    this.lastBindTask = null;
                    return;
                }
            }

            this.lastBindTask.glRun(this.getThread());
        } else {
            this.lastBindTask = new BindTask(attachments, offset, length);
            this.lastBindTask.glRun(this.getThread());
        }
    }

    /**
     * A GLTask that binds the GLFramebuffer.
     *
     * @since 15.07.06
     */
    public class BindTask extends GLTask {

        final IntBuffer attachments;
        final String[] attachmentNames;

        /**
         * Constructs a new BindTask with the specified color attachments.
         *
         * @param attachments the color attachments.
         * @since 15.07.06
         */
        public BindTask(final CharSequence... attachments) {
            this(attachments, 0, attachments.length);
        }

        /**
         * Constructs a new BindTask with the specified color attachments.
         *
         * @param attachments the array containing the color attachments.
         * @param offset the offset to start reading from the color attachment
         * array.
         * @param length the number of color attachments to read.
         * @since 15.07.06
         */
        public BindTask(final CharSequence[] attachments, final int offset, final int length) {
            this.attachmentNames = new String[length];

            if (length > 0) {
                this.attachments = ByteBuffer.allocateDirect(length * 4).order(ByteOrder.nativeOrder()).asIntBuffer();

                for (int i = offset; i < length; i++) {
                    final String name = attachments[i].toString();

                    if (!GLFramebuffer.this.attachments.containsKey(name)) {
                        throw new GLException("Invalid attachment [" + name + "]!");
                    }

                    final int attachId = GLFramebuffer.this.attachments.get(name);

                    this.attachments.put(attachId);
                    this.attachmentNames[i - offset] = name;
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

            assert GL11.glGetError() == GL11.GL_NO_ERROR : String.format("glBindFramebuffer(GL_FRAMEBUFFER, %d) failed!", GLFramebuffer.this.framebufferId);

            if (this.attachments != null) {
                GL20.glDrawBuffers(this.attachments);

                assert GL11.glGetError() == GL11.GL_NO_ERROR : String.format("glDrawBuffers(%s) failed!", GLTools.IntBufferToString(attachments));
            }
        }
    }

    /**
     * Adds a depth stencil attachment to the framebuffer object. Mipmap level 0
     * is used.
     *
     * @param attachment the texture to write the stencil data to.
     * @since 15.07.06
     */
    public void addDepthStencilAttachment(final GLTexture attachment) {
        this.addDepthStencilAttachment(attachment, 0);
    }

    /**
     * Adds a depth stencil attachment to the framebuffer object.
     *
     * @param attachment the texture to write the stencil data to.
     * @param level the mipmap level of the texture.
     * @since 15.07.06
     */
    public void addDepthStencilAttachment(final GLTexture attachment, final int level) {
        new AddDepthStencilAttachmentTask(attachment, level).glRun(this.getThread());
    }

    /**
     * A GLTask that adds a depth stencil attachment to the GLFramebuffer
     * object.
     *
     * @since 15.07.06
     */
    public class AddDepthStencilAttachmentTask extends GLTask {

        final GLTexture depthStencilAttachment;
        final int level;

        /**
         * Constructs a new AddDepthStencilAttachmentTask using the specified
         * attachment. Mipmap level 0 is used.
         *
         * @param attachment the attachment to use.
         * @since 15.07.06
         */
        public AddDepthStencilAttachmentTask(final GLTexture attachment) {
            this(attachment, 0);
        }

        /**
         * Constructs a new AddDepthStencilAttachmentTask using the specified
         * attachment and mipmap level.
         *
         * @param attachment the attachment to use.
         * @param level the mipmap level.
         * @since 15.07.06
         */
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

            assert GL11.glGetError() == GL11.GL_NO_ERROR : String.format("glBindFramebuffer(GL_FRAMEBUFFER, %d) failed!", GLFramebuffer.this.framebufferId);

            final GLTextureTarget target = this.depthStencilAttachment.getTarget();

            switch (target) {
                case GL_TEXTURE_1D:
                    GL30.glFramebufferTexture1D(
                            GL30.GL_FRAMEBUFFER,
                            GL30.GL_DEPTH_STENCIL_ATTACHMENT,
                            target.value,
                            this.depthStencilAttachment.textureId,
                            this.level);

                    assert GL11.glGetError() == GL11.GL_NO_ERROR : String.format("glFramebufferTexture1D(GL_FRAMEBUFFER, GL_DEPTH_STENCIL_ATTACHMENT, %d, %d, %d) failed!",
                            target.value, this.depthStencilAttachment.textureId, this.level);

                    break;
                case GL_TEXTURE_2D:
                    GL30.glFramebufferTexture2D(
                            GL30.GL_FRAMEBUFFER,
                            GL30.GL_DEPTH_STENCIL_ATTACHMENT,
                            target.value,
                            this.depthStencilAttachment.textureId,
                            this.level);

                    assert GL11.glGetError() == GL11.GL_NO_ERROR : String.format("glFramebufferTexture2D(GL_FRAMEBUFFER, GL_DEPTH_STENCIL_ATTACHMENT, %d, %d, %d) failed!",
                            target.value, this.depthStencilAttachment.textureId, this.level);

                    break;
                default:
                    throw new GLException("Texture target type: " + target + " is currently unsupported.");
            }

            GL30.glBindFramebuffer(GL30.GL_FRAMEBUFFER, 0);

            assert GL11.glGetError() == GL11.GL_NO_ERROR : "glBindFramebuffer(GL_FRAMEBUFFER, 0) failed!";
        }
    }

    /**
     * Adds a depth attachment to the framebuffer. Mipmap level 0 is used.
     *
     * @param depthAttachment the texture to write the depth data to.
     * @since 15.07.06
     */
    public void addDepthAttachment(final GLTexture depthAttachment) {
        this.addDepthAttachment(depthAttachment, 0);
    }

    /**
     * Adds a depth attachment to the framebuffer.
     *
     * @param depthAttachment the texture to write the depth data to.
     * @param level the mipmap level to write the data to.
     * @since 15.07.06
     */
    public void addDepthAttachment(final GLTexture depthAttachment, final int level) {
        new AddDepthAttachmentTask(depthAttachment, level).glRun(this.getThread());
    }

    /**
     * A GLTask that adds a depth attachment to a GLFramebuffer.
     *
     * @since 15.07.06
     */
    public class AddDepthAttachmentTask extends GLTask {

        final GLTexture depthAttachment;
        final int level;

        /**
         * Constructs a new AddDepthAttachmentTask. Mipmap level 0 is used.
         *
         * @param attachment the texture to attach.
         * @since 15.07.06
         */
        public AddDepthAttachmentTask(final GLTexture attachment) {
            this(attachment, 0);
        }

        /**
         * Constructs a new AddDepthAttachmentTask.
         *
         * @param attachment the attachment texture to use.
         * @param level the mipmap level to use
         * @since 15.07.06
         */
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
            if(GLFramebuffer.this.isLocked) {
                throw new GLException("Cannot add attachments to null instance of GLFramebuffer!");
            } else if (!GLFramebuffer.this.isValid()) {
                throw new GLException("Invalid GLFramebuffer!");
            }

            GL30.glBindFramebuffer(GL30.GL_FRAMEBUFFER, GLFramebuffer.this.framebufferId);

            assert GL11.glGetError() == GL11.GL_NO_ERROR : String.format("glBindFramebuffer(GL_FRAMEBUFFER, %d) failed!", GLFramebuffer.this.framebufferId);

            final GLTextureTarget target = this.depthAttachment.getTarget();

            switch (target) {
                case GL_TEXTURE_1D:
                    GL30.glFramebufferTexture1D(
                            GL30.GL_FRAMEBUFFER,
                            GL30.GL_DEPTH_ATTACHMENT,
                            target.value,
                            this.depthAttachment.textureId,
                            this.level);

                    assert GL11.glGetError() == GL11.GL_NO_ERROR : String.format("glFramebufferTexture1D(GL_FRAMEBUFFER, GL_DEPTH_ATTACHMENT, %d, %d, %d) failed!",
                            target.value, this.depthAttachment.textureId, this.level);
                    break;
                case GL_TEXTURE_2D:
                    GL30.glFramebufferTexture2D(
                            GL30.GL_FRAMEBUFFER,
                            GL30.GL_DEPTH_ATTACHMENT,
                            target.value,
                            this.depthAttachment.textureId,
                            this.level);

                    assert GL11.glGetError() == GL11.GL_NO_ERROR : String.format("glFramebufferTexture2D(GL_FRAMEBUFFER, GL_DEPTH_ATTACHMENT, %d, %d, %d) failed!",
                            target.value, this.depthAttachment.textureId, this.level);
                    break;
                default:
                    throw new GLException("Texture target type: " + target + " is currently unsupported.");
            }

            GL30.glBindFramebuffer(GL30.GL_FRAMEBUFFER, 0);

            assert GL11.glGetError() == GL11.GL_NO_ERROR : "glBindFramebuffer(GL_FRAMEBUFFER, 0) failed!";
        }
    }

    /**
     * Adds a color attachment to the framebuffer. Mipmap level 0 is used.
     *
     * @param name the name to associate with the attachment.
     * @param attachment the texture to attach.
     * @since 15.07.06
     */
    public void addColorAttachment(
            final CharSequence name, final GLTexture attachment) {

        this.addColorAttachment(name, attachment, 0);
    }

    /**
     * Adds the color attachment to the framebuffer.
     *
     * @param name the name to associate with the attachment.
     * @param attachment the texture to attach.
     * @param level the mipmap level to use.
     * @since 15.07.06
     */
    public void addColorAttachment(
            final CharSequence name,
            final GLTexture attachment, final int level) {

        new AddColorAttachmentTask(name, attachment, level).glRun(this.getThread());
    }

    /**
     * A GLTask that adds a color attachment to a GLFramebuffer.
     *
     * @since 15.07.06
     */
    public class AddColorAttachmentTask extends GLTask {

        final GLTexture colorAttachment;
        final int attachmentId;
        final int level;

        /**
         * Constructs a new AddColorAttachmentTask. Mipmap level 0 is used.
         *
         * @param name the name to associate the attachment with.
         * @param attachment the attachment.
         * @since 15.07.06
         */
        public AddColorAttachmentTask(final CharSequence name, final GLTexture attachment) {
            this(name, attachment, 0);
        }

        /**
         * Constructs a new AddColorAttachmentTask.
         * @param name the name to associate the attachment with.
         * @param attachment the attachment.
         * @param level the mipmap level.
         * @since 15.07.06
         */
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
            if(GLFramebuffer.this.isLocked) {
                throw new GLException("Cannot add color attachment to null instance of GLFramebuffer!");
            } else if (!GLFramebuffer.this.isValid()) {
                throw new GLException("Invalid GLFramebuffer!");
            }

            GL30.glBindFramebuffer(GL30.GL_FRAMEBUFFER, GLFramebuffer.this.framebufferId);
            
            assert GL11.glGetError() == GL11.GL_NO_ERROR : String.format("glBindFramebuffer(GL_FRAMEBUFFER, %d) failed!", GLFramebuffer.this.framebufferId);
            
            final GLTextureTarget target = this.colorAttachment.getTarget();

            switch (target) {
                case GL_TEXTURE_1D:
                    GL30.glFramebufferTexture1D(
                            GL30.GL_FRAMEBUFFER,
                            this.attachmentId,
                            target.value,
                            this.colorAttachment.textureId,
                            this.level);
                    
                    assert GL11.glGetError() == GL11.GL_NO_ERROR : String.format("glFramebufferTexture1D(GL_FRAMEBUFFER, %d, %d, %d, %d) failed!",
                            this.attachmentId, target.value, this.colorAttachment.textureId, this.level);
                    break;
                case GL_TEXTURE_2D:
                    GL30.glFramebufferTexture2D(
                            GL30.GL_FRAMEBUFFER,
                            this.attachmentId,
                            target.value,
                            this.colorAttachment.textureId,
                            this.level);
                    
                    assert GL11.glGetError() == GL11.GL_NO_ERROR : String.format("glFramebufferTexture2D(GL_FRAMEBUFFER, %d, %d, %d, %d) failed!",
                            this.attachmentId, target.value, this.colorAttachment.textureId, this.level);
                    break;
                default:
                    throw new GLException("Texture target type: " + target + " is currently unsupported.");
            }

            GL30.glBindFramebuffer(GL30.GL_FRAMEBUFFER, 0);
            
            assert GL11.glGetError() == GL11.GL_NO_ERROR : "glBindFramebuffer(GL_FRAMEBUFFER, 0) failed!";
        }

    }
}
