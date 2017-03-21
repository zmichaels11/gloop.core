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

import static com.longlinkislong.gloop.GLAsserts.NON_DIRECT_BUFFER_MSG;
import static com.longlinkislong.gloop.GLAsserts.bufferIsNotNativeMsg;
import static com.longlinkislong.gloop.GLAsserts.bufferTooSmallMsg;
import static com.longlinkislong.gloop.GLAsserts.checkBufferIsNative;
import static com.longlinkislong.gloop.GLAsserts.checkBufferSize;
import com.longlinkislong.gloop.glspi.Framebuffer;
import java.lang.ref.WeakReference;
import java.nio.ByteBuffer;
import java.nio.IntBuffer;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import org.lwjgl.opengl.GL30;
import org.lwjgl.system.MemoryStack;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.Marker;
import org.slf4j.MarkerFactory;

/**
 * An OpenGL object that represents a framebuffer.
 *
 * @author zmichaels
 * @since 15.07.06
 */
public class GLFramebuffer extends GLObject {

    private static final Marker GL_MARKER = MarkerFactory.getMarker("GLOOP");
    private static final Logger LOGGER = LoggerFactory.getLogger("GLFramebuffer");

    volatile transient Framebuffer framebuffer;
    private volatile int nextColorAttachment = 36064 /* GL_COLOR_ATTACHMENT0 */;
    private final Map<String, Integer> colorAttachments = new HashMap<>(4);
    private final Map<String, WeakReference<GLObject>> attachments = new HashMap<>(4);
    private final boolean isLocked;
    private String name = "";
    private final List<GLTask> buildInstructions = new ArrayList<>(0);    

    /**
     * Assigns a human-readable name to the GLFramebuffer.
     *
     * @param name the human-readable name.
     * @since 15.12.18
     */
    public final void setName(final CharSequence name) {
        GLTask.create(() -> {
            LOGGER.trace(
                    GL_MARKER,
                    "Renamed GLFramebuffer[{}] to GLFramebuffer[{}]",
                    this.name,
                    name);

            this.name = name.toString();
        }).glRun(this.getThread());
    }

    /**
     * Retrieves the name of the GLFramebuffer.
     *
     * @return the name.
     * @since 15.12.18
     */
    public final String getName() {
        return this.name;
    }

    /**
     * Constructs a new GLFramebuffer on the default OpenGL thread.
     *
     * @since 15.07.06
     */
    public GLFramebuffer() {
        this(GLThread.getDefaultInstance());

        LOGGER.warn(
                GL_MARKER,
                "Constructing GLFramebuffer object on arbitrary GLThreads is discouraged.");
    }

    /**
     * Constructs a new GLFramebuffer on the specified OpenGL thread.
     *
     * @param thread the OpenGL thread.
     * @since 15.07.06
     */
    public GLFramebuffer(final GLThread thread) {
        super(thread);

        LOGGER.trace(
                GL_MARKER,
                "Constructed GLFramebuffer on thread: ",
                thread);

        this.isLocked = false;
        this.init();
    }

    private GLFramebuffer(final GLThread thread, final int fbId) {
        super(thread);

        if (fbId != 0) {
            throw new UnsupportedOperationException("Only fb0 is allowed to be initialized this way!");
        }

        this.isLocked = true;
        this.framebuffer = GLTools.getDriverInstance().framebufferGetDefault();
        this.name = "id=" + framebuffer.hashCode();
    }

    // replace with thread local storage
    private static final Map<GLThread, GLFramebuffer> DEFAULT_FRAMEBUFFERS = new HashMap<>(1);

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
        return framebuffer != null && framebuffer.isValid();
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
    public final class IsCompleteQuery extends GLQuery<Boolean> {

        @SuppressWarnings("unchecked")
        @Override
        public Boolean call() throws Exception {
            LOGGER.trace(GL_MARKER, "############### Start GLFramebuffer Is Complete Query ###############");
            LOGGER.trace(GL_MARKER, "\tQuerying GLFramebuffer[{}]", GLFramebuffer.this.getName());

            checkThread();

            final boolean res = GLTools.getDriverInstance().framebufferIsComplete(framebuffer);

            if (!res) {
                LOGGER.warn(GL_MARKER, "GLFramebuffer[{}] is not complete!", GLFramebuffer.this.getName());
            }

            GLFramebuffer.this.updateTimeUsed();
            LOGGER.trace(GL_MARKER, "############### End GLFramebuffer Is Complete Query ###############");

            return res;
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
    public final class InitTask extends GLTask {

        @Override
        public void run() {
            checkThread();

            if (GLFramebuffer.this.isLocked) {
                throw new GLException("Cannot initialize null instance of GLFramebuffer!");
            } else if (GLFramebuffer.this.isValid()) {
                throw new GLException("GLFramebuffer already initialized!");
            }

            framebuffer = GLTools.getDriverInstance().framebufferCreate();
            GLFramebuffer.this.name = "id=" + framebuffer.hashCode();
            GLFramebuffer.this.updateTimeUsed();
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
    public final class DeleteTask extends GLTask {

        @SuppressWarnings("unchecked")
        @Override
        public void run() {
            checkThread();

            if (GLFramebuffer.this.isLocked) {
                LOGGER.error(GL_MARKER, "Attempted to delete default GLFramebuffer!");
            } else if (!GLFramebuffer.this.isValid()) {
                LOGGER.warn(GL_MARKER, "Attempted to delete invalid GLFramebuffer!");
            } else {
                GLTools.getDriverInstance().framebufferDelete(framebuffer);
                GLFramebuffer.this.lastUsedTime = 0L;
                GLFramebuffer.this.framebuffer = null;
                GLFramebuffer.this.colorAttachments.clear();
                GLFramebuffer.this.attachments.clear();
                GLFramebuffer.this.nextColorAttachment = 36064 /* GL_COLOR_ATTACHMENT0 */;
            }            
        }
    }

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
    public void bind(
            final CharSequence[] attachments,
            final int offset,
            final int length) {

        new BindTask(attachments, offset, length).glRun(this.getThread());
    }

    /**
     * A GLTask that binds the GLFramebuffer.
     *
     * @since 15.07.06
     */
    public final class BindTask extends GLTask {

        private final int[] attachments;
        final String[] attachmentNames;
        GLFramebuffer fb = GLFramebuffer.this;

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
                this.attachments = new int[length];

                for (int i = offset; i < length; i++) {
                    final String name = attachments[i].toString();

                    if (!GLFramebuffer.this.colorAttachments.containsKey(name)) {
                        throw new GLException("Invalid attachment [" + name + "]!");
                    }

                    final int attachId = GLFramebuffer.this.colorAttachments.get(name);

                    this.attachments[i - offset] = attachId;
                    this.attachmentNames[i - offset] = name;
                }
            } else {
                this.attachments = null;
            }
        }

        @SuppressWarnings("unchecked")
        @Override
        public void run() {
            checkThread();

            if (!GLFramebuffer.this.isValid()) {
                throw new GLException("Invalid GLFramebuffer!");
            }

            GLThread.getCurrent().orElseThrow(GLException::new).currentFramebufferBind = this;

            if (this.attachments == null) {
                GLTools.getDriverInstance().framebufferBind(framebuffer, null);

                // update all attachments
                GLFramebuffer.this.attachments.values().stream()
                        .filter(Objects::nonNull)
                        .map(WeakReference::get)
                        .filter(Objects::nonNull)
                        .forEach(GLObject::updateTimeUsed);
            } else {
                try (MemoryStack mem = MemoryStack.stackPush()) {
                    final IntBuffer iAttachments = mem.ints(this.attachments);

                    GLTools.getDriverInstance().framebufferBind(framebuffer, iAttachments);

                    // update all bound attachments
                    Arrays.stream(this.attachmentNames)
                            .map(GLFramebuffer.this.attachments::get)
                            .filter(Objects::nonNull)
                            .map(WeakReference::get)
                            .filter(Objects::nonNull)
                            .forEach(GLObject::updateTimeUsed);
                }
            }            

            GLFramebuffer.this.updateTimeUsed();            
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
    public GLFramebuffer addDepthStencilAttachment(
            final GLTexture attachment,
            final int level) {

        new AddDepthStencilAttachmentTask(
                attachment,
                level)
                .glRun(this.getThread());

        return this;
    }

    /**
     * A GLTask that adds a depth stencil attachment to the GLFramebuffer
     * object.
     *
     * @since 15.07.06
     */
    public final class AddDepthStencilAttachmentTask extends GLTask {

        private final GLTexture depthStencilAttachment;
        private final int level;

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

            this.depthStencilAttachment = Objects.requireNonNull(attachment);

            if ((this.level = level) < 0) {
                throw new GLException("Mipmap level cannot be less than 0!");
            }
        }

        @SuppressWarnings("unchecked")
        @Override
        public void run() {
            checkThread();

            if (!GLFramebuffer.this.isValid()) {
                throw new GLException("Invalid GLFramebuffer!");
            }

            this.depthStencilAttachment.updateTimeUsed();

            GLTools.getDriverInstance().framebufferAddAttachment(framebuffer, GL30.GL_DEPTH_STENCIL_ATTACHMENT, depthStencilAttachment.texture, level);

            GLFramebuffer.this.buildInstructions.add(this);
            GLFramebuffer.this.updateTimeUsed();
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
    public GLFramebuffer addDepthAttachment(
            final GLTexture depthAttachment,
            final int level) {

        new AddDepthAttachmentTask(
                depthAttachment,
                level)
                .glRun(this.getThread());

        return this;
    }

    /**
     * A GLTask that adds a depth attachment to a GLFramebuffer.
     *
     * @since 15.07.06
     */
    public final class AddDepthAttachmentTask extends GLTask {

        private final GLTexture depthAttachment;
        private final int level;

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

            GLFramebuffer.this.attachments.put("depth", new WeakReference<>(attachment));
        }

        @SuppressWarnings("unchecked")
        @Override
        public void run() {
            checkThread();

            if (GLFramebuffer.this.isLocked) {
                throw new GLException("Cannot add attachments to null instance of GLFramebuffer!");
            } else if (!GLFramebuffer.this.isValid()) {
                throw new GLException("Invalid GLFramebuffer!");
            }

            this.depthAttachment.updateTimeUsed();
            GLTools.getDriverInstance().framebufferAddAttachment(framebuffer, GL30.GL_DEPTH_ATTACHMENT, depthAttachment.texture, level);

            GLFramebuffer.this.buildInstructions.add(this);
            GLFramebuffer.this.updateTimeUsed();            
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
     * Adds a renderbuffer attachment to the framebuffer. This method will fail
     * if the renderbuffer is a color attachment.
     *
     * @param renderbuffer the renderbuffer object.
     * @return self reference
     * @since 16.04.04
     */
    public GLFramebuffer addRenderbufferAttachment(final GLRenderbuffer renderbuffer) {
        new AddRenderbufferAttachmentTask(null, renderbuffer).glRun(this.getThread());
        return this;
    }

    /**
     * Adds a renderbuffer attachment to the framebuffer. Name is ignored if the
     * renderbuffer is not a color attachment.
     *
     * @param name the name of the renderbuffer. Only makes sense for color
     * renderbuffer objects.
     * @param renderbuffer the renderbuffer object.
     * @return self reference.
     * @since 16.04.04
     */
    public GLFramebuffer addRenderbufferAttachment(final CharSequence name, final GLRenderbuffer renderbuffer) {
        new AddRenderbufferAttachmentTask(name, renderbuffer).glRun(this.getThread());
        return this;
    }

    /**
     * GLTask that adds a GLRenderbuffer as an attachment.
     *
     * @since 16.04.04
     */
    public class AddRenderbufferAttachmentTask extends GLTask {

        private final String name;
        private final GLRenderbuffer renderbuffer;
        private final int attachmentId;

        public AddRenderbufferAttachmentTask(
                final CharSequence name,
                final GLRenderbuffer renderbuffer) {

            this.renderbuffer = Objects.requireNonNull(renderbuffer);

            switch (renderbuffer.target) {
                case COLOR_ATTACHMENT:
                    this.name = (name == null) ? "color" : name.toString();
                    this.attachmentId = GLFramebuffer.this.nextColorAttachment;
                    GLFramebuffer.this.nextColorAttachment++;
                    break;
                case DEPTH_ATTACHMENT:
                    this.name = "depth";
                    this.attachmentId = GL30.GL_DEPTH_ATTACHMENT;
                    break;
                case STENCIL_ATTACHMENT:
                    this.name = "stencil";
                    this.attachmentId = GL30.GL_STENCIL_ATTACHMENT;
                    break;
                case DEPTH_STENCIL_ATTACHMENT:
                    this.name = "depth-stencil";
                    this.attachmentId = GL30.GL_DEPTH_STENCIL_ATTACHMENT;
                    break;
                default:
                    throw new GLException("Unsupported renderbuffer attachment!");
            }

            GLFramebuffer.this.attachments.put(this.name, new WeakReference<>(renderbuffer));
        }

        @Override
        public void run() {
            if (GLFramebuffer.this.isLocked) {
                throw new GLException("Cannot add attachment to locked GLFramebuffer!");
            } else if (!GLFramebuffer.this.isValid()) {
                throw new GLException("GLFramebuffer is not valid!");
            } else if (!this.renderbuffer.isValid()) {
                throw new GLException("GLRenderbuffer is not valid!");
            }

            GLTools.getDriverInstance().framebufferAddRenderbuffer(framebuffer, attachmentId, this.renderbuffer.renderbuffer);

            if (this.renderbuffer.target == GLRenderbuffer.TargetType.COLOR_ATTACHMENT) {
                GLFramebuffer.this.colorAttachments.put(this.name, this.attachmentId);
            }

            this.renderbuffer.updateTimeUsed();

            GLFramebuffer.this.buildInstructions.add(this);
            GLFramebuffer.this.updateTimeUsed();           
        }
    }

    /**
     * A GLTask that adds a color attachment to a GLFramebuffer.
     *
     * @since 15.07.06
     */
    public class AddColorAttachmentTask extends GLTask {

        private final GLTexture colorAttachment;
        private final int attachmentId;
        private final int level;
        private final String attachmentName;

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

            this.colorAttachment = Objects.requireNonNull(attachment);

            if ((this.level = level) < 0) {
                throw new GLException("Color attachment level cannot be less than 0!");
            }

            this.attachmentId = GLFramebuffer.this.nextColorAttachment;
            this.attachmentName = name.toString();

            //TODO: this should probably be moved into the run
            GLFramebuffer.this.nextColorAttachment++;
            GLFramebuffer.this.colorAttachments.put(this.attachmentName, this.attachmentId);
            GLFramebuffer.this.attachments.put(this.attachmentName, new WeakReference<>(attachment));
        }

        @SuppressWarnings("unchecked")
        @Override
        public void run() {
            checkThread();

            if (GLFramebuffer.this.isLocked) {
                throw new GLException("Cannot add color attachment to null instance of GLFramebuffer!");
            } else if (!GLFramebuffer.this.isValid()) {
                throw new GLException("Invalid GLFramebuffer!");
            }

            this.colorAttachment.updateTimeUsed();


            GLTools.getDriverInstance().framebufferAddAttachment(framebuffer, attachmentId, colorAttachment.texture, level);

            GLFramebuffer.this.buildInstructions.add(this);
            GLFramebuffer.this.updateTimeUsed();            
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

        new BlitTask(
                readFB,
                writeFB,
                srcX0, srcY0, srcX1, srcY1,
                dstX0, dstY0, dstX1, dstY1,
                mask,
                filter).glRun();
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

            this.bitfield = mask
                    .stream()
                    .map(m -> m.value)
                    .reduce(0, (prev, current) -> prev | current);

            this.filter = Objects.requireNonNull(filter);
        }

        @SuppressWarnings("unchecked")
        @Override
        public void run() {
            this.readFB.checkThread();
            this.writeFB.checkThread();

            if (!this.readFB.isValid()) {
                throw new GLException("Read framebuffer is not valid!");
            } else if (!this.writeFB.isValid()) {
                throw new GLException("Write framebuffer is not valid!");
            }

            GLTools.getDriverInstance().framebufferBlit(
                    readFB.framebuffer, srcX0, srcY0, srcX1, srcY1,
                    writeFB.framebuffer, dstX0, dstY0, dstX1, dstY1,
                    bitfield, filter.value);
            readFB.updateTimeUsed();
            writeFB.updateTimeUsed();            
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
    public final class ReadPixelsTask extends GLTask {

        private final int x;
        private final int y;
        private final int width;
        private final int height;
        private final GLTextureFormat format;
        private final GLType type;
        private final ByteBuffer pixels;
        private final GLBuffer pixelPackBuffer;

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

        @SuppressWarnings("unchecked")
        @Override
        public void run() {            
            checkThread();

            if (pixels != null) {
                GLTools.getDriverInstance().framebufferGetPixels(framebuffer, x, y, width, height, format.value, type.value, pixels);
            } else {
                GLTools.getDriverInstance().framebufferGetPixels(framebuffer, x, y, width, height, format.value, type.value, pixelPackBuffer.buffer);
            }

            GLFramebuffer.this.updateTimeUsed();
        }
    }

    @Override
    public final boolean isShareable() {
        return false;
    }

    @Override
    public long getTimeSinceLastUsed() {
        return (System.nanoTime() - this.lastUsedTime);
    }

    @Override
    protected GLObject migrate() {
        if (this.isValid()) {
            this.delete();
            this.init();
            this.buildInstructions.forEach(task -> task.glRun(this.getThread()));
            return this;
        } else {
            return null;
        }
    }
}
