/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.longlinkislong.gloop;

import static com.longlinkislong.gloop.GLAsserts.NON_DIRECT_BUFFER_MSG;
import static com.longlinkislong.gloop.GLAsserts.bufferIsNotNativeMsg;
import static com.longlinkislong.gloop.GLAsserts.bufferTooSmallMsg;
import static com.longlinkislong.gloop.GLAsserts.checkBufferIsNative;
import static com.longlinkislong.gloop.GLAsserts.checkBufferSize;
import static com.longlinkislong.gloop.GLAsserts.checkGLError;
import static com.longlinkislong.gloop.GLAsserts.glErrorMsg;
import com.longlinkislong.gloop.dsa.DSADriver;
import com.longlinkislong.gloop.dsa.EXTDSADriver;
import static java.lang.Long.toHexString;
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
import org.lwjgl.opengl.GL15;
import org.lwjgl.opengl.GL20;
import org.lwjgl.opengl.GL21;
import org.lwjgl.opengl.GL30;
import static org.lwjgl.system.MemoryUtil.memAddress;

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

        @Override
        public Boolean call() throws Exception {
            final ContextCapabilities cap = GL.getCapabilities();

            if (cap.OpenGL30) {
                final int currentFB = GL11.glGetInteger(GL30.GL_FRAMEBUFFER_BINDING);
                assert checkGLError() : glErrorMsg("glGetInteger(I)", "GL_FRAMEBUFFER_BINDING");

                GL30.glBindFramebuffer(GL30.GL_FRAMEBUFFER, GLFramebuffer.this.framebufferId);
                assert checkGLError() : glErrorMsg("glBindFramebuffer(II)", "GL_FRAMEBUFFER", GLFramebuffer.this.framebufferId);

                final int complete = GL30.glCheckFramebufferStatus(GL30.GL_FRAMEBUFFER);
                assert checkGLError() : glErrorMsg("glCheckFramebufferStatus(I)", "GL_FRAMEBUFFER");
                final boolean res = complete == GL30.GL_FRAMEBUFFER_COMPLETE;

                GL30.glBindFramebuffer(GL30.GL_FRAMEBUFFER, currentFB);
                assert checkGLError() : glErrorMsg("glBindFramebuffer(II)", "GL_FRAMEBUFFER", currentFB);

                return res;
            } else if (cap.GL_ARB_framebuffer_object) {
                final int currentFB = GL11.glGetInteger(ARBFramebufferObject.GL_FRAMEBUFFER_BINDING);
                assert checkGLError() : glErrorMsg("glGetInteger(I)", "GL_FRAMEBUFFER_BINDING_ARB");

                ARBFramebufferObject.glBindFramebuffer(ARBFramebufferObject.GL_FRAMEBUFFER, GLFramebuffer.this.framebufferId);
                assert checkGLError() : glErrorMsg("glBindFramebufferARB(II)", "GL_FRAMEBUFFER", GLFramebuffer.this.framebufferId);

                final int complete = ARBFramebufferObject.glCheckFramebufferStatus(ARBFramebufferObject.GL_FRAMEBUFFER);
                assert checkGLError() : glErrorMsg("glCheckFramebufferStatusARB(I)", "GL_FRAMEBUFFER");

                final boolean res = complete == ARBFramebufferObject.GL_FRAMEBUFFER_COMPLETE;

                ARBFramebufferObject.glBindFramebuffer(ARBFramebufferObject.GL_FRAMEBUFFER, currentFB);
                assert checkGLError() : glErrorMsg("glBindFramebufferARB(II)", "GL_FRAMEBUFFER", currentFB);

                return res;
            } else if (cap.GL_EXT_framebuffer_object) {
                final int currentFB = GL11.glGetInteger(EXTFramebufferObject.GL_FRAMEBUFFER_BINDING_EXT);
                assert checkGLError() : glErrorMsg("glGetInteger(I)", "GL_FRAMEBUFFER_BINDING_EXT");

                EXTFramebufferObject.glBindFramebufferEXT(EXTFramebufferObject.GL_FRAMEBUFFER_EXT, GLFramebuffer.this.framebufferId);
                assert checkGLError() : glErrorMsg("glBindFramebufferEXT(II)", "GL_FRAMEBUFFER_EXT", GLFramebuffer.this.framebufferId);

                final int complete = EXTFramebufferObject.glCheckFramebufferStatusEXT(EXTFramebufferObject.GL_FRAMEBUFFER_EXT);
                assert checkGLError() : glErrorMsg("glCheckFramebufferStatusEXT(I)", "GL_FRAMEBUFFER_EXT");

                final boolean res = complete == EXTFramebufferObject.GL_FRAMEBUFFER_COMPLETE_EXT;

                EXTFramebufferObject.glBindFramebufferEXT(EXTFramebufferObject.GL_FRAMEBUFFER_EXT, currentFB);
                assert checkGLError() : glErrorMsg("glBindFramebufferEXT(II)", "GL_FRAMEBUFFER_EXT", currentFB);

                return res;
            } else {
                throw new UnsupportedOperationException("GLFramebuffer requires either an OpenGL3.0 context, arb_framebuffer_object, or ext_framebuffer_object.");
            }
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
                assert checkGLError() : glErrorMsg("glGetInteger(I)", "GL_FRAMEBUFFER");

                this.bindTask.run();
                this.taskToRun.run();

                GL30.glBindFramebuffer(GL30.GL_FRAMEBUFFER, currentFB);
                assert checkGLError() : glErrorMsg("glBindFramebuffer(II)", "GL_FRAMEBUFFER", currentFB);
            } else if (cap.GL_ARB_framebuffer_object) {
                final int currentFB = GL11.glGetInteger(ARBFramebufferObject.GL_FRAMEBUFFER);
                assert checkGLError() : glErrorMsg("glGetInteger(I)", "GL_FRAMEBUFFER_ARB");

                this.bindTask.run();
                this.taskToRun.run();

                ARBFramebufferObject.glBindFramebuffer(ARBFramebufferObject.GL_FRAMEBUFFER, currentFB);
                assert checkGLError() : glErrorMsg("glBindFramebufferARB(II)", "GL_FRAMEBUFFER_ARB", currentFB);
            } else if (cap.GL_EXT_framebuffer_object) {
                final int currentFB = GL11.glGetInteger(EXTFramebufferObject.GL_FRAMEBUFFER_EXT);
                assert checkGLError() : glErrorMsg("glGetIngeter(I)", "GL_FRAMEBUFFER_EXT");

                this.bindTask.run();
                this.taskToRun.run();

                EXTFramebufferObject.glBindFramebufferEXT(EXTFramebufferObject.GL_FRAMEBUFFER_EXT, currentFB);
                assert checkGLError() : glErrorMsg("GL_FRAMEBUFFER_EXT", currentFB);
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
            assert checkGLError() : glErrorMsg("glBindFramebuffer(II)", "GL_FRAMEBUFFER", GLFramebuffer.this.framebufferId);

            if (this.attachments != null) {
                GL20.glDrawBuffers(this.attachments);
                assert checkGLError() : glErrorMsg("glDrawBuffers(*)", toHexString(memAddress(this.attachments)));
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

            this.bitfield = mask.stream()
                    .map(m -> m.value)
                    .reduce(0, (prev, current) -> prev | current);
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

    /**
     * Reads pixels from the framebuffer and writes them to a Pixel pack buffer.
     *
     * @param x the x to start the read
     * @param y the y to start the read
     * @param width the width of the read rectangle
     * @param height the height of the read rectangle
     * @param format the pixel type
     * @param type the buffer to write the pixels to.
     * @param pixelPackBuffer the pixel buffer to write pixel data to.
     * @since 15.09.18
     */
    public void readPixels(
            final int x, final int y,
            final int width, final int height,
            final GLTextureFormat format, final GLType type,
            final GLBuffer pixelPackBuffer) {

        new ReadPixelsTask(x, y, width, height, format, type, pixelPackBuffer).glRun(this.getThread());
    }

    /**
     * Reads pixels from the framebuffer and writes them to a ByteBuffer object.
     *
     * @param x the x to start read.
     * @param y the y to start read
     * @param width the width of the read rectangle
     * @param height the height of the read rectangle
     * @param format the pixel format
     * @param type the pixel type
     * @param pixels the buffer to write the pixels to.
     * @since 15.09.18
     */
    public void readPixels(final int x, final int y, final int width, final int height, final GLTextureFormat format, final GLType type, final ByteBuffer pixels) {
        new ReadPixelsTask(x, y, width, height, format, type, pixels).glRun(this.getThread());
    }

    /**
     * A GLTask that reads pixels from the framebuffer.
     *
     * @since 15.09.18
     */
    public class ReadPixelsTask extends GLTask {

        final int x;
        final int y;
        final int width;
        final int height;
        final GLTextureFormat format;
        final GLType type;
        final ByteBuffer pixels;
        final GLBuffer pixelPackBuffer;

        /**
         * Constructs a new ReadPixelsTask for reading pixel data from the
         * framebuffer object into a pixel pack buffer.
         *
         * @param x the x coordinate to start reading pixel data from
         * @param y the y coordinate to start reading pixel data from
         * @param width the width of the read pixel rectangle
         * @param height the height of the read pixel rectangle
         * @param format the format of the pixels
         * @param type the type of the pixels
         * @param pixelBuffer the pixel pack buffer to write the data to
         * @since 15.09.18
         */
        public ReadPixelsTask(final int x, final int y, final int width, final int height, final GLTextureFormat format, final GLType type, final GLBuffer pixelBuffer) {
            this.x = x;
            this.y = y;
            this.width = width;
            this.height = height;
            this.format = Objects.requireNonNull(format);
            this.type = Objects.requireNonNull(type);
            this.pixels = null;
            this.pixelPackBuffer = Objects.requireNonNull(pixelBuffer);
        }

        /**
         * Constructs a new ReadPixelsTask for reading pixel data from the
         * framebuffer into a ByteBuffer object.
         *
         * @param x the x coordinate to start reading pixel data from.
         * @param y the y coordinate to start reading pixel data from.
         * @param width the width of the read pixel rectangle.
         * @param height the height of the read pixel rectangle.
         * @param format the format of the pixels.
         * @param type the type of the pixels.
         * @param pixels the ByteBuffer to write the pixel data to.
         * @since 15.09.18
         */
        public ReadPixelsTask(final int x, final int y, final int width, final int height, final GLTextureFormat format, final GLType type, final ByteBuffer pixels) {
            assert checkBufferIsNative(pixels) : bufferIsNotNativeMsg(pixels);
            assert pixels.isDirect() : NON_DIRECT_BUFFER_MSG;
            assert checkBufferSize(width, height, 1, format.value, type.value, pixels) : bufferTooSmallMsg(width, height, 1, format.value, type.value, pixels);

            this.x = x;
            this.y = y;
            this.width = width;
            this.height = height;
            this.format = Objects.requireNonNull(format);
            this.type = Objects.requireNonNull(type);
            this.pixels = Objects.requireNonNull(pixels);
            this.pixelPackBuffer = null;
        }

        @Override
        public void run() {
            final int currentFb = GL11.glGetInteger(GL30.GL_FRAMEBUFFER_BINDING);
            assert checkGLError() : glErrorMsg("glGetInteger(I)", "GL_FRAMEBUFFER_BINDING");

            boolean undoFBBind = false;

            if (currentFb != GLFramebuffer.this.framebufferId) {
                GL30.glBindFramebuffer(GL30.GL_FRAMEBUFFER, framebufferId);
                assert checkGLError() : glErrorMsg("glBindFramebuffer(II)", "GL_FRAMEBUFFER", GLFramebuffer.this.framebufferId);
                undoFBBind = true;
            }

            if (this.pixels == null) {
                final int currentPixelPackBuffer = GL11.glGetInteger(GL21.GL_PIXEL_PACK_BUFFER_BINDING);
                assert checkGLError() : glErrorMsg("glGetInteger(I)", "GL_PIXEL_PACK_BUFFER_BINDING");

                boolean undoBufferBind = false;

                if (this.pixelPackBuffer.bufferId != currentPixelPackBuffer) {
                    GL15.glBindBuffer(GL21.GL_PIXEL_PACK_BUFFER, this.pixelPackBuffer.bufferId);
                    assert checkGLError() : glErrorMsg("glBindBuffer(II)", "GL_PIXEL_PACK_BUFFER", this.pixelPackBuffer.bufferId);

                    undoBufferBind = true;
                }

                // read into pixel pack buffer                
                GL11.glReadPixels(this.x, this.y, this.width, this.height, this.format.value, this.type.value, 0L);
                assert checkGLError() : glErrorMsg("glReadPixels(IIIIIIL)", this.x, this.y, this.width, this.height, this.format, this.type, 0L);

                if (undoBufferBind) {
                    GL15.glBindBuffer(GL21.GL_PIXEL_PACK_BUFFER, this.pixelPackBuffer.bufferId);
                    assert checkGLError() : glErrorMsg("glBindBuffer(II)", "GL_PIXEL_PACK_BUFFER", this.pixelPackBuffer.bufferId);
                }
            } else {
                // read into a ByteBuffer
                GL11.glReadPixels(this.x, this.y, this.width, this.height, this.format.value, this.type.value, this.pixels);
                assert checkGLError() : glErrorMsg("glReadPixels(IIIIII*)", this.x, this.y, this.width, this.height, this.format, this.type, toHexString(memAddress(this.pixels)));
            }

            if (undoFBBind) {
                GL30.glBindFramebuffer(GL30.GL_FRAMEBUFFER, currentFb);
                assert checkGLError() : glErrorMsg("glBindFramebuffer(II)", "GL_FRAMEBUFFER", currentFb);
            }
        }
    }
}
