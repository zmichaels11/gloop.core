/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.longlinkislong.gloop;

import com.longlinkislong.gloop.dsa.DSADriver;
import com.longlinkislong.gloop.dsa.EXTDSADriver;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.IntBuffer;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import org.lwjgl.opengl.ARBFramebufferObject;
import org.lwjgl.opengl.ContextCapabilities;
import org.lwjgl.opengl.EXTFramebufferObject;
import org.lwjgl.opengl.GL;
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
     * Checks if the framebuffer is complete.
     *
     * @return true if the framebuffer is complete.
     * @since 15.07.20
     */
    public boolean isComplete() {        
        return new IsCompleteQuery().glCall(this.getThread());
    }
    
    

    /**
     * A GLQuery that checks if the framebuffer is complete.
     *
     * @since 15.07.20
     */
    public class IsCompleteQuery extends GLQuery<Boolean> {

        private GLThread thread;

        @Override
        public Boolean call() throws Exception {
            this.thread = GLThread.getCurrent().orElseThrow(GLException::new);

            final ContextCapabilities cap = GL.getCapabilities();

            if (cap.OpenGL30) {
                final int currentFB = GL11.glGetInteger(GL30.GL_FRAMEBUFFER_BINDING);

                GL30.glBindFramebuffer(GL30.GL_FRAMEBUFFER, GLFramebuffer.this.framebufferId);

                final int complete = GL30.glCheckFramebufferStatus(GL30.GL_FRAMEBUFFER);
                final boolean res = complete == GL30.GL_FRAMEBUFFER_COMPLETE;

                GL30.glBindFramebuffer(GL30.GL_FRAMEBUFFER, currentFB);
                this.thread = null;

                return res;
            } else if (cap.GL_ARB_framebuffer_object) {
                final int currentFB = GL11.glGetInteger(ARBFramebufferObject.GL_FRAMEBUFFER_BINDING);

                ARBFramebufferObject.glBindFramebuffer(ARBFramebufferObject.GL_FRAMEBUFFER, GLFramebuffer.this.framebufferId);

                final int complete = ARBFramebufferObject.glCheckFramebufferStatus(ARBFramebufferObject.GL_FRAMEBUFFER);
                final boolean res = complete == ARBFramebufferObject.GL_FRAMEBUFFER_COMPLETE;

                ARBFramebufferObject.glBindFramebuffer(ARBFramebufferObject.GL_FRAMEBUFFER, currentFB);
                this.thread = null;

                return res;
            } else if (cap.GL_EXT_framebuffer_object) {
                final int currentFB = GL11.glGetInteger(EXTFramebufferObject.GL_FRAMEBUFFER_BINDING_EXT);

                EXTFramebufferObject.glBindFramebufferEXT(EXTFramebufferObject.GL_FRAMEBUFFER_EXT, GLFramebuffer.this.framebufferId);

                final int complete = EXTFramebufferObject.glCheckFramebufferStatusEXT(EXTFramebufferObject.GL_FRAMEBUFFER_EXT);
                final boolean res = complete == EXTFramebufferObject.GL_FRAMEBUFFER_COMPLETE_EXT;

                EXTFramebufferObject.glBindFramebufferEXT(EXTFramebufferObject.GL_FRAMEBUFFER_EXT, currentFB);
                this.thread = null;

                return res;
            } else {
                throw new UnsupportedOperationException("GLFramebuffer requires either an OpenGL3.0 context, arb_framebuffer_object, or ext_framebuffer_object.");
            }
        }

        @Override
        protected Boolean handleInterruption() {
            if (this.thread == null) {
                // dont do anything if the task is done.
                return false;
            } else if (GLThread.getCurrent().get() != this.thread) {
                // dont do anything if the thread is wrong.
                return false;
            }

            // attempt to reset the framebuffer state to the default.
            GLFramebuffer.getDefaultFramebuffer().bind();

            return false;
        }
    }

    /**
     * Executes a task with the current framebuffer bound. The framebuffer bind
     * will be undone after the task is executed.
     *
     * @since 15.07.20
     */
    public class BindlessUseTask extends GLTask {

        final GLTask taskToRun;
        final GLTask bindTask;

        public BindlessUseTask(final BindTask bindTask, final GLTask task) {
            this.taskToRun = Objects.requireNonNull(task);
            this.bindTask = Objects.requireNonNull(bindTask);
        }

        @Override
        public void run() {
            final ContextCapabilities cap = GL.getCapabilities();

            if (cap.OpenGL30) {
                final int currentFB = GL11.glGetInteger(GL30.GL_FRAMEBUFFER);

                this.bindTask.run();
                this.taskToRun.run();

                GL30.glBindFramebuffer(GL30.GL_FRAMEBUFFER, currentFB);
            } else if (cap.GL_ARB_framebuffer_object) {
                final int currentFB = GL11.glGetInteger(ARBFramebufferObject.GL_FRAMEBUFFER);

                this.bindTask.run();
                this.taskToRun.run();

                ARBFramebufferObject.glBindFramebuffer(ARBFramebufferObject.GL_FRAMEBUFFER, currentFB);
            } else if (cap.GL_EXT_framebuffer_object) {
                final int currentFB = GL11.glGetInteger(EXTFramebufferObject.GL_FRAMEBUFFER_EXT);

                this.bindTask.run();
                this.taskToRun.run();

                EXTFramebufferObject.glBindFramebufferEXT(EXTFramebufferObject.GL_FRAMEBUFFER_EXT, currentFB);
            }
        }

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

            GLFramebuffer.this.framebufferId = GLTools.getDSAInstance().glCreateFramebuffers();
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
            if (GLFramebuffer.this.isLocked) {
                throw new GLException("Cannot delete null instance of GLFramebuffer!");
            } else if (!GLFramebuffer.this.isValid()) {
                throw new GLException("GLFramebuffer is not valid!");
            }

            final ContextCapabilities cap = GL.getCapabilities();

            if (cap.OpenGL30) {
                GL30.glDeleteFramebuffers(GLFramebuffer.this.framebufferId);
                assert GL11.glGetError() == GL11.GL_NO_ERROR : String.format("glDeleteFramebuffers(%d) failed!", GLFramebuffer.this.framebufferId);
            } else if (cap.GL_ARB_framebuffer_object) {
                ARBFramebufferObject.glDeleteFramebuffers(GLFramebuffer.this.framebufferId);
                assert GL11.glGetError() == GL11.GL_NO_ERROR : String.format("glDeleteFramebuffersARB(%d) failed!", GLFramebuffer.this.framebufferId);
            } else if (cap.GL_EXT_framebuffer_object) {
                EXTFramebufferObject.glDeleteFramebuffersEXT(GLFramebuffer.this.framebufferId);
                assert GL11.glGetError() == GL11.GL_NO_ERROR : String.format("glDeleteFramebuffersEXT(%d) failed!", GLFramebuffer.this.framebufferId);
            } else {
                throw new GLException("glDeleteFramebuffers is not supported! glDeleteFramebuffers requires either: an OpenGL 3.0 context, ARB_framebuffer_Object, or EXT_framebuffer_object.");
            }

            GLFramebuffer.this.framebufferId = INVALID_FRAMEBUFFER_ID;
            GLFramebuffer.this.attachments.clear();
            GLFramebuffer.this.lastBindTask = null;
            GLFramebuffer.this.nextColorAttachment = GL30.GL_COLOR_ATTACHMENT0;
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
        if (this.lastBindTask != null && this.lastBindTask.attachmentNames.length == length) {
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
     * @return self reference
     * @since 15.07.06
     */
    public GLFramebuffer addDepthStencilAttachment(final GLTexture attachment) {
        this.addDepthStencilAttachment(attachment, 0);
        return this;
    }

    /**
     * Adds a depth stencil attachment to the framebuffer object.
     *
     * @param attachment the texture to write the stencil data to.
     * @param level the mipmap level of the texture.
     * @return self reference
     * @since 15.07.06
     */
    public GLFramebuffer addDepthStencilAttachment(final GLTexture attachment, final int level) {
        new AddDepthStencilAttachmentTask(attachment, level).glRun(this.getThread());
        return this;
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

            final DSADriver dsa = GLTools.getDSAInstance();

            if (dsa instanceof EXTDSADriver) {
                final EXTDSADriver patch = (EXTDSADriver) dsa;
                final GLTextureTarget target = this.depthStencilAttachment.getTarget();

                switch (target) {
                    case GL_TEXTURE_1D:
                        patch.glNamedFramebufferTexture1D(
                                GLFramebuffer.this.framebufferId,
                                GL30.GL_DEPTH_STENCIL_ATTACHMENT,
                                target.value,
                                this.depthStencilAttachment.textureId,
                                this.level);
                        break;
                    case GL_TEXTURE_2D:
                        patch.glNamedFramebufferTexture2D(
                                GLFramebuffer.this.framebufferId,
                                GL30.GL_DEPTH_STENCIL_ATTACHMENT,
                                target.value,
                                this.depthStencilAttachment.textureId,
                                this.level);
                        break;
                    default:
                        throw new GLException("Texture target type: " + target + " is currently not supported.");
                }
            } else {
                dsa.glNamedFramebufferTexture(
                        GLFramebuffer.this.framebufferId,
                        GL30.GL_DEPTH_STENCIL_ATTACHMENT,
                        this.depthStencilAttachment.textureId, level);
            }
        }
    }

    /**
     * Adds a depth attachment to the framebuffer. Mipmap level 0 is used.
     *
     * @param depthAttachment the texture to write the depth data to.
     * @return self reference
     * @since 15.07.06
     */
    public GLFramebuffer addDepthAttachment(final GLTexture depthAttachment) {
        this.addDepthAttachment(depthAttachment, 0);
        return this;
    }

    /**
     * Adds a depth attachment to the framebuffer.
     *
     * @param depthAttachment the texture to write the depth data to.
     * @param level the mipmap level to write the data to.
     * @return self reference
     * @since 15.07.06
     */
    public GLFramebuffer addDepthAttachment(final GLTexture depthAttachment, final int level) {
        new AddDepthAttachmentTask(depthAttachment, level).glRun(this.getThread());
        return this;
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
            if (GLFramebuffer.this.isLocked) {
                throw new GLException("Cannot add attachments to null instance of GLFramebuffer!");
            } else if (!GLFramebuffer.this.isValid()) {
                throw new GLException("Invalid GLFramebuffer!");
            }

            final DSADriver dsa = GLTools.getDSAInstance();

            if (dsa instanceof EXTDSADriver) {
                final EXTDSADriver patch = (EXTDSADriver) dsa;
                final GLTextureTarget target = this.depthAttachment.getTarget();

                switch (target) {
                    case GL_TEXTURE_1D:
                        patch.glNamedFramebufferTexture1D(
                                GLFramebuffer.this.framebufferId,
                                GL30.GL_DEPTH_ATTACHMENT,
                                target.value,
                                this.depthAttachment.textureId,
                                this.level);
                        break;
                    case GL_TEXTURE_2D:
                        patch.glNamedFramebufferTexture2D(
                                GLFramebuffer.this.framebufferId,
                                GL30.GL_DEPTH_ATTACHMENT,
                                target.value,
                                this.depthAttachment.textureId,
                                this.level);
                        break;
                    default:
                        throw new GLException("Texture target type: " + target + " is currently not supported!");
                }
            } else {
                dsa.glNamedFramebufferTexture(
                        GLFramebuffer.this.framebufferId,
                        GL30.GL_DEPTH_ATTACHMENT,
                        this.depthAttachment.textureId,
                        this.level);
            }
        }
    }

    /**
     * Adds a color attachment to the framebuffer. Mipmap level 0 is used.
     *
     * @param name the name to associate with the attachment.
     * @param attachment the texture to attach.
     * @return self reference
     * @since 15.07.06
     */
    public GLFramebuffer addColorAttachment(
            final CharSequence name, final GLTexture attachment) {

        this.addColorAttachment(name, attachment, 0);
        return this;
    }

    /**
     * Adds the color attachment to the framebuffer.
     *
     * @param name the name to associate with the attachment.
     * @param attachment the texture to attach.
     * @param level the mipmap level to use.
     * @return self reference
     * @since 15.07.06
     */
    public GLFramebuffer addColorAttachment(
            final CharSequence name,
            final GLTexture attachment, final int level) {

        new AddColorAttachmentTask(name, attachment, level).glRun(this.getThread());
        return this;
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
         *
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
            if (GLFramebuffer.this.isLocked) {
                throw new GLException("Cannot add color attachment to null instance of GLFramebuffer!");
            } else if (!GLFramebuffer.this.isValid()) {
                throw new GLException("Invalid GLFramebuffer!");
            }

            final DSADriver dsa = GLTools.getDSAInstance();

            if (dsa instanceof EXTDSADriver) {
                final EXTDSADriver patch = (EXTDSADriver) dsa;
                final GLTextureTarget target = this.colorAttachment.getTarget();

                switch (target) {
                    case GL_TEXTURE_1D:
                        patch.glNamedFramebufferTexture1D(
                                GLFramebuffer.this.framebufferId,
                                this.attachmentId,
                                target.value,
                                this.colorAttachment.textureId,
                                this.level);
                        break;
                    case GL_TEXTURE_2D:
                        patch.glNamedFramebufferTexture2D(
                                GLFramebuffer.this.framebufferId,
                                this.attachmentId,
                                target.value,
                                this.colorAttachment.textureId,
                                this.level);
                        break;
                    default:
                        throw new GLException("Texture target type: " + target + " is not supported!");
                }
            } else {
                dsa.glNamedFramebufferTexture(
                        GLFramebuffer.this.framebufferId,
                        this.attachmentId,
                        this.colorAttachment.textureId,
                        this.level);
            }
        }
    }

    /**
     * Blits a framebuffer to another framebuffer.
     *
     * @param readFB the framebuffer to read pixel data from.
     * @param writeFB the framebuffer to write pixel data to.
     * @param srcX0 the first x-coordinate from the read framebuffer.
     * @param srcY0 the first y-coordinate from the read framebuffer.
     * @param srcX1 the second x-coordinate from the read framebuffer.
     * @param srcY1 the second y-coordinate from the read framebuffer.
     * @param dstX0 the first x-coordinate from the write framebuffer.
     * @param dstY0 the first y-coordinate from the write framebuffer.
     * @param dstX1 the second x-coordinate from the read framebuffer.
     * @param dstY1 the second y-coordinate from the read framebuffer.
     * @param mask the mask for all of the buffers to copy.
     * @param filter the filter to use if the framebuffers are different sizes.
     * @since 15.07.20
     */
    public static void blit(final GLFramebuffer readFB, final GLFramebuffer writeFB,
            final int srcX0, final int srcY0, final int srcX1, final int srcY1,
            final int dstX0, final int dstY0, final int dstX1, final int dstY1,
            final Set<GLFramebufferMode> mask, final GLTextureMagFilter filter) {

        new BlitTask(readFB, writeFB, srcX0, srcY0, srcX1, srcY1, dstX0, dstY0, dstX1, dstY1, mask, filter).glRun();
    }

    /**
     * A GLTask that blits one framebuffer to another framebuffer.
     *
     * @since 15.07.20
     */
    public static class BlitTask extends GLTask {

        private final GLFramebuffer readFB;
        private final GLFramebuffer writeFB;
        private final int srcX0, srcX1, srcY0, srcY1;
        private final int dstX0, dstX1, dstY0, dstY1;
        private final int bitfield;
        private final GLTextureMagFilter filter;

        /**
         * Constructs a new BlitTask.
         *
         * @param readFB the framebuffer to read pixel data from.
         * @param writeFB the framebuffer to write pixel data to.
         * @param srcX0 the first x-coordinate from the read framebuffer.
         * @param srcY0 the first y-coordinate from the read framebuffer.
         * @param srcX1 the second x-coordinate from the read framebuffer.
         * @param srcY1 the second y-coordinate from the read framebuffer.
         * @param dstX0 the first x-coordinate from the write framebuffer.
         * @param dstY0 the first y-coordinate from the write framebuffer.
         * @param dstX1 the second x-coordinate from the read framebuffer.
         * @param dstY1 the second y-coordinate from the read framebuffer.
         * @param mask the mask for all of the buffers to copy.
         * @param filter the filter to use if the framebuffers are different
         * sizes.
         */
        public BlitTask(
                final GLFramebuffer readFB, final GLFramebuffer writeFB,
                final int srcX0, final int srcY0, final int srcX1, final int srcY1,
                final int dstX0, final int dstY0, final int dstX1, final int dstY1,
                final Set<GLFramebufferMode> mask, final GLTextureMagFilter filter) {

            this.readFB = Objects.requireNonNull(readFB);
            this.writeFB = Objects.requireNonNull(writeFB);

            this.srcX0 = srcX0;
            this.srcY0 = srcY0;
            this.srcX1 = srcX1;
            this.srcY1 = srcY1;

            this.dstX0 = dstX0;
            this.dstY0 = dstY0;
            this.dstX1 = dstX1;
            this.dstY1 = dstY1;

            int bits = 0;

            for (GLFramebufferMode clear : mask) {
                bits |= clear.value;
            }

            this.bitfield = bits;
            this.filter = Objects.requireNonNull(filter);
        }

        @Override
        public void run() {
            if (!this.readFB.isValid()) {
                throw new GLException("Read framebuffer is not valid!");
            } else if (!this.writeFB.isValid()) {
                throw new GLException("Write framebuffer is not valid!");
            }

            GLTools.getDSAInstance().glBlitNamedFramebuffer(
                    this.readFB.framebufferId, this.writeFB.framebufferId,
                    srcX0, srcY0, srcX1, srcY1,
                    dstX0, dstY0, dstX1, dstY1,
                    this.bitfield, this.filter.value);
        }
    }
    
    public static ByteBuffer readPixels(final int x, final int y, final int width, final int height, final GLTextureFormat format, final GLType type) {
        return new ReadPixelsQuery(x, y, width, height, format, type).glCall(GLThread.getAny());
    }
    
    public static ByteBuffer readPixels(final int x, final int y, final int width, final int height, final GLTextureFormat format, final GLType type, final ByteBuffer pixels) {
        return new ReadPixelsQuery(x, y, width, height, format, type, pixels).glCall(GLThread.getAny());
    }
    
    static class ReadPixelsQuery extends GLQuery<ByteBuffer> {
        final int x;
        final int y;
        final int width;
        final int height;
        final GLTextureFormat format;
        final GLType type;
        final ByteBuffer pixels;
        
        public ReadPixelsQuery(final int x, final int y, final int width, final int height, final GLTextureFormat format, final GLType type) {
            this.x = x;
            this.y = y;
            this.width = width;
            this.height = height;
            this.format = Objects.requireNonNull(format);
            this.type = Objects.requireNonNull(type);
            
            final int pixelSize;
            
            switch(format) {
                case GL_DEPTH_COMPONENT:
                case GL_STENCIL_INDEX:
                case GL_RED:
                case GL_GREEN:
                case GL_BLUE:
                    pixelSize = 2;
                    break;
                case GL_RGB:
                case GL_BGR:
                    pixelSize = 3;
                    break;
                case GL_RGBA:
                case GL_BGRA:
                    pixelSize = 4;
                    break;
                default:
                    throw new GLException("Unable to infer pixel region size! Invalid format for operation: " + format);                
            }

            final int pixelCount = width * height;
            final int imageSize;
            switch(type) {
                case GL_UNSIGNED_BYTE:
                case GL_BYTE:
                    imageSize = pixelSize * pixelCount;
                    break;
                case GL_SHORT:
                case GL_UNSIGNED_SHORT:
                    imageSize = pixelSize * pixelCount * 2;
                    break;
                case GL_INT:
                case GL_UNSIGNED_INT:
                case GL_FLOAT:
                    imageSize = pixelSize * pixelCount * 4;
                    break;
                case GL_UNSIGNED_BYTE_3_3_2:
                case GL_UNSIGNED_BYTE_2_3_3_REV:
                    imageSize = pixelCount;
                    break;
                case GL_UNSIGNED_SHORT_5_6_5:
                case GL_UNSIGNED_SHORT_5_6_5_REV:
                case GL_UNSIGNED_SHORT_4_4_4_4:
                case GL_UNSIGNED_SHORT_4_4_4_4_REV:
                case GL_UNSIGNED_SHORT_5_5_5_1:
                case GL_UNSIGNED_SHORT_1_5_5_5_REV:
                    imageSize = pixelCount * 2;
                    break;
                case GL_UNSIGNED_INT_8_8_8_8:
                case GL_UNSIGNED_INT_8_8_8_8_REV:
                case GL_UNSIGNED_INT_10_10_10_2:
                case GL_UNSIGNED_INT_2_10_10_10_REV:
                    imageSize = pixelCount * 4;
                    break;
                default:
                    throw new GLException("Unable to infer pixel region size! Invalid type for operation: " + type);
            }
            
            this.pixels = ByteBuffer.allocateDirect(imageSize).order(ByteOrder.nativeOrder());
        }
        
        public ReadPixelsQuery(final int x, final int y, final int width, final int height, final GLTextureFormat format, final GLType type, final ByteBuffer pixels) {
            this.x = x;
            this.y = y;
            this.width = width;
            this.height = height;
            this.format = Objects.requireNonNull(format);
            this.type = Objects.requireNonNull(type);
            this.pixels = Objects.requireNonNull(pixels);
        }
        
        @Override
        public ByteBuffer call() throws Exception {
            GL11.glReadPixels(
                    this.x, this.y, 
                    this.width, this.height, 
                    this.format.value, 
                    this.type.value, 
                    this.pixels);
            
            return this.pixels;
        }
        
    }
}
