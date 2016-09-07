/* 
 * Copyright (c) 2015, longlinkislong.com
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

import com.longlinkislong.gloop.glspi.Buffer;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.util.Arrays;
import java.util.Objects;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.Marker;
import org.slf4j.MarkerFactory;

/**
 * GLBuffer represents an OpenGL Buffer object.
 *
 * @author zmichaels
 * @see <a href="https://www.opengl.org/wiki/Buffer_Object">Buffer Object</a>
 * @since 15.05.14
 */
public class GLBuffer extends GLObject {

    private static final Marker GLOOP_MARKER = MarkerFactory.getMarker("GLOOP");
    private static final Logger LOGGER = LoggerFactory.getLogger("GLBuffer");
    
    transient volatile Buffer buffer;
    private String name = "";

    /**
     * Assigns a human-readable name to the GLBuffer.
     *
     * @param name the human-readable name.
     * @since 15.12.18
     */
    public final void setName(final CharSequence name) {
        GLTask.create(() -> {
            LOGGER.trace(
                    GLOOP_MARKER,
                    "Renamed GLBuffer[{}] to GLBuffer[{}]",
                    this.name,
                    name);

            this.name = name.toString();
        }).glRun(this.getThread());
    }

    /**
     * Retrieves the name of the GLBuffer.
     *
     * @return the name.
     * @since 15.12.18
     */
    public final String getName() {
        return this.name;
    }

    /**
     * Constructs a new GLBuffer when possible on the default GLThread.
     *
     * @since 15.05.15
     */
    public GLBuffer() {
        this(GLThread.getAny());
    }

    /**
     * Constructs a new GLBuffer when possible on the supplied GLThread.
     *
     * @param thread the GLThread to create the GLBuffer on.
     */
    public GLBuffer(final GLThread thread) {
        super(thread);

        LOGGER.trace(
                GLOOP_MARKER,
                "Constructed GLBuffer object on thread: {}",
                thread);

        this.init();
    }

    @Override
    public String toString() {
        return "GLBuffer[" + this.name + "]";
    }

    /**
     * Checks if the GLBuffer is valid. a
     *
     * @return true if the buffer is valid.
     * @since 15.05.13
     */
    public boolean isValid() {
        return buffer != null && buffer.isValid();
    }

    /**
     * Initializes this GLBuffer. All GLBuffers are initialized automatically
     * some time after the constructor is called. This method should only be
     * called if the GLBuffer has been deleted.
     *
     * @return self-reference
     * @since 15.05.13
     */
    public final GLBuffer init() {
        new InitTask().glRun(this.getThread());
        return this;
    }

    /**
     * Binds the buffer as the sink for program feedback.
     *
     * @param binding the binding point.
     * @since 16.07.15
     */
    public void bindFeedback(final int binding) {
        new BindFeedbackTask(binding).glRun(this.getThread());
    }

    /**
     * Binds the buffer as the sink for program feedback.
     *
     * @param binding the binding point
     * @param offset the offset
     * @param size the amount of bytes to bind.
     * @since 16.07.15
     */
    public void bindFeedback(final int binding, long offset, long size) {
        new BindFeedbackTask(binding, offset, size).glRun(this.getThread());
    }

    public final class BindFeedbackTask extends GLTask {

        final int binding;
        final long offset;
        final long size;

        public BindFeedbackTask(final int binding) {
            this.binding = binding;
            this.offset = 0L;
            this.size = -1L;
        }

        public BindFeedbackTask(final int binding, final long offset, final long size) {
            this.binding = binding;
            if ((this.offset = offset) < 0) {
                throw new IllegalArgumentException("Offset cannot be less than 0!");
            }

            this.size = size;
        }

        @Override
        public void run() {
            LOGGER.trace(GLOOP_MARKER, "############### Start GLBuffer Bind Feedback Task ###############");

            if (!GLBuffer.this.isValid()) {
                throw new GLException("Invalid GLBuffer!");
            } else {
                if (this.size == -1) {
                    LOGGER.trace(GLOOP_MARKER, "\tBinding GLBuffer[{}] at feedback buffer binding: [{}]!", GLBuffer.this.getName(), this.binding);

                    GLTools.getDriverInstance().bufferBindFeedback(buffer, this.binding);
                } else {
                    LOGGER.trace(GLOOP_MARKER, "\tBinding GLBuffer[{}],off={},size={} at feedback buffer binding: [{}]!", GLBuffer.this.getName(), this.offset, this.size, this.binding);

                    GLTools.getDriverInstance().bufferBindFeedback(buffer, this.binding, this.offset, this.size);
                }

                GLBuffer.this.updateTimeUsed();
            }

            LOGGER.trace(GLOOP_MARKER, "############### End GLBuffer Bind Feedback Task ###############");
        }
    }

    /**
     * Binds the GLBuffer as a uniform buffer object.
     *
     * @param binding the uniform buffer object binding point.
     * @since 16.07.05
     */
    public void bindUniform(final int binding) {
        new BindUniformTask(binding).glRun(this.getThread());
    }

    /**
     * Binds the GLBuffer as a uniform buffer object.
     *
     * @param binding the uniform buffer object binding point.
     * @param offset the offset of the buffer to bind.
     * @param size the amount of bytes to bind.
     * @since 16.07.05
     */
    public void bindUniform(final int binding, final long offset, final long size) {
        new BindUniformTask(binding, offset, size).glRun(this.getThread());
    }

    public final class BindUniformTask extends GLTask {

        final int binding;
        final long offset;
        final long size;

        public BindUniformTask(final int binding) {
            this.binding = binding;
            this.offset = 0L;
            this.size = -1L;
        }

        public BindUniformTask(final int binding, final long offset, final long size) {
            this.binding = binding;
            if ((this.offset = offset) < 0) {
                throw new IllegalArgumentException("Offset cannot be less than 0!");
            }

            this.size = size;
        }

        @Override
        public void run() {
            LOGGER.trace(GLOOP_MARKER, "############### Start GLBuffer Bind Uniform Task ###############");

            if (!GLBuffer.this.isValid()) {
                throw new GLException("Invalid GLBuffer!");
            } else {
                if (this.size == -1) {
                    LOGGER.trace(GLOOP_MARKER, "\tBinding GLBuffer[{}] at uniform buffer binding: [{}]!", GLBuffer.this.getName(), this.binding);

                    GLTools.getDriverInstance().bufferBindUniform(buffer, this.binding);
                } else {
                    LOGGER.trace(GLOOP_MARKER, "\tBinding GLBuffer[{}],off={},size={} at uniform buffer binding: [{}]!", GLBuffer.this.getName(), this.offset, this.size, this.binding);

                    GLTools.getDriverInstance().bufferBindUniform(buffer, this.binding, this.offset, this.size);
                }

                GLBuffer.this.updateTimeUsed();
            }

            LOGGER.trace(GLOOP_MARKER, "############### End GLBuffer Bind Uniform Task ###############");
        }
    }

    /**
     * Binds the GLBuffer as a shader storage buffer object.
     *
     * @param binding the shader storage buffer object binding point.
     * @since 16.07.05
     */
    public void bindStorage(final int binding) {
        new BindStorageTask(binding).glRun(this.getThread());
    }

    /**
     * Binds the GLBuffer as a shader storage buffer object.
     *
     * @param binding the shader storage buffer object binding point.
     * @param offset the offset of the GLBuffer to bind.
     * @param size the amount of bytes to bind.
     * @since 16.07.05
     */
    public void bindStorage(final int binding, final long offset, final long size) {
        new BindStorageTask(binding, offset, size).glRun(this.getThread());
    }

    /**
     * A GLTask that binds the GLBuffer to a shader storage buffer object
     * binding point.
     *
     * @since 16.07.05
     */
    public final class BindStorageTask extends GLTask {

        final int binding;
        final long offset;
        final long size;

        public BindStorageTask(final int binding, final long offset, final long size) {
            this.binding = binding;

            if ((this.offset = offset) < 0) {
                throw new IllegalArgumentException("Offset cannot be less than 0!");
            }

            this.size = size;
        }

        public BindStorageTask(final int binding) {
            this.binding = binding;
            this.offset = 0;
            this.size = -1;
        }

        @Override
        public void run() {
            LOGGER.trace(GLOOP_MARKER, "############### Start GLBuffer Bind Storage Task ###############");

            if (!GLBuffer.this.isValid()) {
                throw new GLException("Invalid GLBuffer!");
            } else {
                if (this.size == -1) {
                    GLTools.getDriverInstance().bufferBindStorage(buffer, this.binding);
                } else {
                    GLTools.getDriverInstance().bufferBindStorage(buffer, this.binding, this.offset, this.size);
                }

                GLBuffer.this.updateTimeUsed();
            }

            LOGGER.trace(GLOOP_MARKER, "############### End GLBuffer Bind Storage Task ###############");
        }
    }

    /**
     * A GLTask that initializes this GLBuffer. The task will fail if the
     * GLBuffer is already initialized.
     *
     * @since 15.05.13
     */
    public class InitTask extends GLTask {

        @Override
        public void run() {
            LOGGER.trace(GLOOP_MARKER, "############### Start GLBuffer Init Task ###############");

            if (!GLBuffer.this.isValid()) {
                buffer = GLTools.getDriverInstance().bufferCreate();
                GLBuffer.this.name = "id=" + buffer.hashCode();

                LOGGER.trace(GLOOP_MARKER, "Initialized GLBuffer[{}]!", GLBuffer.this.getName());
            } else {
                throw new GLException("GLBuffer is already initialized!");
            }

            GLBuffer.this.updateTimeUsed();
            LOGGER.trace(GLOOP_MARKER, "############### End GLBuffer Init Task ###############");
        }
    }

    /**
     * Sends an OpenGL query asking for a property
     *
     * @param pName the parameter name to query.
     * @return the GLBuffer parameter value.
     * @since 15.05.13
     */
    public final int getParameter(final GLBufferParameterName pName) {
        return new ParameterQuery(pName).glCall(this.getThread());
    }

    /**
     * A GLQuery that requests for state information from a GLBuffer. The query
     * will fail if the GLBuffer is not initialized.
     *
     * @since 15.05.13
     */
    public class ParameterQuery extends GLQuery<Integer> {

        final GLBufferParameterName pName;

        /**
         * Constructs a new ParameterQuery.
         *
         * @param pName the query to request
         * @since 15.05.13
         */
        public ParameterQuery(final GLBufferParameterName pName) {

            Objects.requireNonNull(this.pName = pName);
        }

        @SuppressWarnings("unchecked")
        @Override
        public Integer call() throws Exception {
            LOGGER.trace(GLOOP_MARKER, "############### Start GLBuffer Parameter Query ###############");
            LOGGER.trace(GLOOP_MARKER, "\tQuerying parameter of GLBuffer[{}]", GLBuffer.this.getName());
            LOGGER.trace(GLOOP_MARKER, "\tParameter name: {}", this.pName);

            if (!GLBuffer.this.isValid()) {
                throw new GLException("Invalid GLBuffer!");
            }

            final int result = GLTools.getDriverInstance().bufferGetParameterI(buffer, this.pName.value);

            GLBuffer.this.updateTimeUsed();
            LOGGER.trace(GLOOP_MARKER, "GLBuffer[{}].{} = {}!", GLBuffer.this.getName(), this.pName, result);
            LOGGER.trace(GLOOP_MARKER, "############### End GLBuffer Parameter Query ###############");

            return result;
        }

        @Override
        protected Integer handleInterruption() {
            return 0;
        }

    }

    /**
     * Deletes the GLBuffer. This call will fail if the GLBuffer has already
     * been deleted or is in an invalid state.
     *
     * @since 15.05.13
     */
    public void delete() {
        new DeleteTask().glRun(this.getThread());
    }

    /**
     * Constructs a new GLTask for deleting the GLBuffer. This task will fail if
     * the GLBuffer has already been deleted or is in an invalid state.
     *
     * @since 15.05.13
     */
    public class DeleteTask extends GLTask {

        @SuppressWarnings("unchecked")
        @Override
        public void run() {
            LOGGER.trace(GLOOP_MARKER, "############### Start GLBuffer Delete Task ###############");
            LOGGER.trace(GLOOP_MARKER, "\tDeleting GLBuffer[{}]", GLBuffer.this.getName());

            if (GLBuffer.this.isValid()) {
                GLTools.getDriverInstance().bufferDelete(buffer);
                GLBuffer.this.lastUsedTime = 0L;
                GLBuffer.this.buffer = null;
            }

            LOGGER.trace(GLOOP_MARKER, "############### End GLBuffer Delete Task ###############");
        }
    }

    /**
     * Uploads the supplied data to the GLBuffer. GL_STATIC_DRAW is used for
     * usage.
     *
     * @param data the data to upload
     * @since 15.05.13
     */
    public void upload(final ByteBuffer data) {
        this.upload(data, GLBufferUsage.GL_STATIC_DRAW);
    }

    /**
     * Uploads the supplied data to the GLBuffer.
     *
     * @param data the data to upload
     * @param usage the usage of the data. Under most circumstances, this should
     * be GL_STATIC_DRAW. GL_DYNAMIC_DRAW should be used if the GLBuffer is ever
     * mapped.
     * @since 15.05.13
     */
    public void upload(final ByteBuffer data, final GLBufferUsage usage) {
        new UploadTask(data, usage).glRun(this.getThread());
    }

    /**
     * A GLTask that uploads data to a GLBuffer. This task will fail if the
     * GLBuffer is in an invalid state.
     *
     * @since 15.05.13
     */
    public class UploadTask extends GLTask {

        final GLBufferUsage usage;
        final ByteBuffer data;

        /**
         * Constructs a new UploadTask. The GLBuffer is bound to the specified
         * target and GL_STATIC_DRAW is used for the mode.
         *
         * @param data the data to upload.
         * @since 15.05.13
         */
        public UploadTask(final ByteBuffer data) {
            this(data, GLBufferUsage.GL_STATIC_DRAW);
        }

        /**
         * Constructs a new UploadTask. The GLBuffer is bound to the specified
         * target for upload.
         *
         * @param data the data to upload
         * @param usage the mode to set the data to. GL_STATIC_DRAW or
         * GL_DYNAMIC_DRAW are satisfactory for most tasks.
         * @since 15.05.13
         */
        public UploadTask(final ByteBuffer data, final GLBufferUsage usage) {
            if (!data.isDirect()) {
                throw new GLException("Backing data buffer is not direct!");
            } else if (data.order() != ByteOrder.nativeOrder()) {
                throw new GLException("Data is not in native order!");
            }

            this.usage = usage;
            this.data = data;
        }

        @SuppressWarnings("unchecked")
        @Override
        public void run() {
            LOGGER.trace(GLOOP_MARKER, "############### Start GLBuffer Upload Task ###############");
            LOGGER.trace(GLOOP_MARKER, "\tUploading data to GLBuffer[{}]", GLBuffer.this.getName());
            LOGGER.trace(GLOOP_MARKER, "\tUploading {} bytes", this.data.remaining());
            LOGGER.trace(GLOOP_MARKER, "\tSetting buffer usage hint: {}", this.usage);

            if (!GLBuffer.this.isValid()) {
                throw new GLException("GLBuffer is invalid!");
            }

            GLTools.getDriverInstance().bufferSetData(buffer, data, this.usage.value);
            GLBuffer.this.updateTimeUsed();
            LOGGER.trace(GLOOP_MARKER, "############### End GLBuffer Upload Task ###############");
        }
    }

    /**
     * Allocates the specified number of bytes for the GLBuffer
     *
     * @param size the number of bytes to allocate
     * @return self reference
     * @since 15.05.13
     */
    public GLBuffer allocate(final long size) {
        return this.allocate(size, GLBufferUsage.GL_DYNAMIC_DRAW);
    }

    /**
     * Allocates the specified number of bytes for the GLBuffer with the
     * requested usage hint.
     *
     * @param size the number of bytes to allocate.
     * @param usage the usage hint.
     * @return self reference
     * @since 15.07.06
     */
    public GLBuffer allocate(final long size, final GLBufferUsage usage) {
        new AllocateTask(size, usage).glRun(this.getThread());
        return this;
    }

    /**
     * Allocates immutable buffer storage with read and write flags.
     *
     * @param size the number of bytes to allocate
     * @return self reference.
     * @since 15.12.14
     */
    public GLBuffer allocateImmutable(final long size) {
        return this.allocateImmutable(size, GLBufferAccess.GL_MAP_READ, GLBufferAccess.GL_MAP_WRITE);
    }

    /**
     * Allocates immutable buffer storage with the specified flags.
     *
     * @param size the number of bytes to allocate.
     * @param accessFlags the access flags
     * @return self reference.
     * @since 15.12.14
     */
    public GLBuffer allocateImmutable(final long size, final GLBufferAccess... accessFlags) {
        new AllocateImmutableTask(size, accessFlags, 0, accessFlags.length).glRun(this.getThread());
        return this;
    }

    /**
     * A GLTask that allocates buffer storage for the GLBuffer and sets the
     * access flags.
     *
     * @since 15.12.14
     */
    public class AllocateImmutableTask extends GLTask {

        private final long size;
        private final int flags;

        /**
         * Constructs a new AllocateImmutableTask with the specified size and
         * flags.
         *
         * @param size the number of bytes to allocate for the storage.
         * @param accessFlags the array containing the access flags.
         * @param accessOffset the offset to begin reading the flags.
         * @param flagCount the number of flags to read.
         * @since 15.12.14
         */
        public AllocateImmutableTask(final long size, final GLBufferAccess[] accessFlags, final int accessOffset, final int flagCount) {
            if ((this.size = size) < 0) {
                throw new GLException("Size cannot be less than 0!");
            }

            int access = 0;

            for (int i = 0; i < flagCount; i++) {
                access |= accessFlags[i + accessOffset].value;
            }

            this.flags = access;
        }

        @SuppressWarnings("unchecked")
        @Override
        public void run() {
            LOGGER.trace(GLOOP_MARKER, "############### Start GLBuffer Allocate Immutable Task ###############");
            LOGGER.trace(GLOOP_MARKER, "\tApplying allocate to GLBuffer[{}]", GLBuffer.this.getName());
            LOGGER.trace(GLOOP_MARKER, "\tAllocating {} bytes", this.size);
            LOGGER.trace(GLOOP_MARKER, "\tBuffer storage bitfield: {}", this.flags);

            if (!GLBuffer.this.isValid()) {
                throw new GLException("Invalid GLBuffer!");
            }

            GLTools.getDriverInstance().bufferAllocateImmutable(buffer, size, this.flags);
            GLBuffer.this.updateTimeUsed();

            LOGGER.trace(GLOOP_MARKER, "############# End GLBuffer Allocate Immutable Task ##############");
        }

    }

    /**
     * A GLTask that allocates the specified number of bytes for the GLBuffer.
     * This task will fail if the GLBuffer is in an invalid state.
     *
     * @since 15.05.13
     */
    public class AllocateTask extends GLTask {

        public final long size;
        public final GLBufferUsage usage;

        /**
         * Constructs a new AllocateTask using the default usage
         * (GL_DYNAMIC_DRAW).
         *
         * @param size the number of bytes to allocate
         * @since 15.05.13
         */
        public AllocateTask(final long size) {
            this(size, GLBufferUsage.GL_DYNAMIC_DRAW);
        }

        /**
         * Constructs a new AllocateTask.
         *
         * @param size the number of bytes to allocate
         * @param usage the usage to use.
         * @since 15.05.13
         */
        public AllocateTask(final long size, final GLBufferUsage usage) {

            Objects.requireNonNull(this.usage = usage);

            if ((this.size = size) < 0) {
                throw new GLException("Size cannot be less than 0!");
            }
        }

        @SuppressWarnings("unchecked")
        @Override
        public void run() {
            LOGGER.trace(GLOOP_MARKER, "############### Start GLBuffer Allocate Task ###############");
            LOGGER.trace(GLOOP_MARKER, "\tApplying allocate on GLBuffer[{}]", GLBuffer.this.getName());
            LOGGER.trace(GLOOP_MARKER, "\tAllocating {} bytes", this.size);
            LOGGER.trace(GLOOP_MARKER, "\tSetting buffer usage hint: {}", this.usage);

            if (!GLBuffer.this.isValid()) {
                throw new GLException("Invalid GLBuffer!");
            }

            GLTools.getDriverInstance().bufferAllocate(buffer, size, this.usage.value);
            GLBuffer.this.updateTimeUsed();
            LOGGER.trace(GLOOP_MARKER, "############### End GLBuffer Allocate Task ###############");
        }

    }

    /**
     * Requests a copy of the data stored inside the GLBuffer. A new ByteBuffer
     * is allocated to write the data to. This function can cause a thread sync.
     *
     * @return the ByteBuffer.
     * @since 15.05.13
     */
    public ByteBuffer download() {
        return this.download(0, null);
    }

    /**
     * Requests a copy of the data stored inside the GLBuffer. The data is read
     * starting at the specified offset.
     *
     * @param offset the offset to start reading the data.
     * @return the data.
     * @since 15.07.13
     */
    public ByteBuffer download(final long offset) {
        return this.download(offset, null);
    }

    /**
     * Requests a copy of the data stored inside the GLBuffer. This function can
     * cause a thread sync.
     *
     * @param offset the offset to start reading from
     * @param writeBuffer the buffer to write the data to
     * @return the buffer post write.
     * @since 15.05.13
     */
    public ByteBuffer download(final long offset, final ByteBuffer writeBuffer) {
        if (writeBuffer == null) {
            final int size = this.getParameter(GLBufferParameterName.GL_BUFFER_SIZE);

            return download(offset, ByteBuffer.allocateDirect(size).order(ByteOrder.nativeOrder()));
        } else {
            return new DownloadQuery(offset, writeBuffer).glCall(this.getThread());
        }
    }

    /**
     * Constructs a new DownloadQuery. This requests OpenGL to download the data
     * stored in the GLBuffer and to store it into a ByteBuffer.
     *
     * @since 15.05.13
     */
    public class DownloadQuery extends GLQuery<ByteBuffer> {

        final long offset;
        final ByteBuffer writeBuffer;

        /**
         * Constructs a new DownloadQuery, binding the GLBuffer to
         * GL_ARRAY_BUFFER and constructing a new ByteBuffer to write to.
         *
         * @since 15.05.13
         */
        public DownloadQuery() {
            this(0);
        }

        /**
         * Constructs a new DownloadQuery using a new ByteBuffer to write to.
         *
         * @param offset the offset to start reading at
         * @since 15.05.13
         */
        public DownloadQuery(final long offset) {

            this.offset = offset;
            this.writeBuffer = null;
        }

        /**
         * Constructs a new DownloadQuery.
         *
         * @param offset the offset to start reading from
         * @param writeBuffer the buffer to write the data to
         * @since 15.05.13
         */
        public DownloadQuery(final long offset, final ByteBuffer writeBuffer) {

            if (!writeBuffer.isDirect()) {
                throw new GLException("Output buffer must be direct!");
            } else if (writeBuffer.order() != ByteOrder.nativeOrder()) {
                throw new GLException("Output buffer must be in native order!");
            }

            this.offset = offset;
            this.writeBuffer = writeBuffer;
        }

        @SuppressWarnings("unchecked")
        @Override
        public ByteBuffer call() throws Exception {
            LOGGER.trace(GLOOP_MARKER, "############### Start GLBuffer Download Query ###############");
            LOGGER.trace(GLOOP_MARKER, "\tDownloading GLBuffer[{}]", GLBuffer.this.getName());
            LOGGER.trace(GLOOP_MARKER, "\tOffset: {} bytes", this.offset);

            if (!GLBuffer.this.isValid()) {
                throw new GLException("Invalid GLBuffer!");
            }

            final ByteBuffer out;

            if (this.writeBuffer == null) {
                final int size = GLTools.getDriverInstance().bufferGetParameterI(buffer, GLBufferParameterName.GL_BUFFER_SIZE.value);

                out = ByteBuffer.allocateDirect(size).order(ByteOrder.nativeOrder());
            } else {
                out = this.writeBuffer;
            }

            LOGGER.trace(GLOOP_MARKER, "GLBuffer[{}]: download {} bytes", GLBuffer.this.name, out.capacity());
            GLTools.getDriverInstance().bufferGetData(buffer, offset, out);
            GLBuffer.this.updateTimeUsed();
            LOGGER.trace(GLOOP_MARKER, "############### End GLBuffer Download Query ###############");

            return out;
        }

        @Override
        protected ByteBuffer handleInterruption() {
            return ByteBuffer.allocate(0).asReadOnlyBuffer();
        }
    }

    /**
     * Maps a GLBuffer to a ByteBuffer. This function can force a thread sync.
     *
     * @param offset the offset in bytes to begin the map.
     * @param length the number of bytes to map.
     * @param access the buffer access. If no flags are specified, GL_MAP_READ |
     * GL_MAP_WRITE will be used.
     * @return the mapped ByteBuffer.
     * @since 16.07.06
     */
    public ByteBuffer map(final long offset, final long length, GLBufferAccess... access) {
        return (access.length == 0)
                ? map(offset, length, GLBufferAccess.GL_MAP_READ, GLBufferAccess.GL_MAP_WRITE)
                : new MapQuery(offset, length, access, 0, access.length).glCall(this.getThread());
    }

    /**
     * A GLQuery that requests the buffer to be mapped. A GLBuffer can only be
     * mapped once at a time and should be unmapped before being used elsewhere.
     *
     * @see
     * <a href="https://www.opengl.org/wiki/GLAPI/glMapBufferRange#Function_Definition">glMapBufferRange</a>
     * @since 15.05.13
     */
    public class MapQuery extends GLQuery<ByteBuffer> {

        final long offset;
        final long length;
        final int access;

        public MapQuery(final long offset, final long length, final GLBufferAccess[] accessFlags, final int flagOffset, final int flatCount) {
            this.offset = offset;
            this.length = length;

            this.access = Arrays.stream(accessFlags, flagOffset, flagOffset + flatCount)
                    .map(flag -> flag.value)
                    .reduce(0, (bitfield, flag) -> bitfield | flag);
        }

        @SuppressWarnings("unchecked")
        @Override
        public ByteBuffer call() throws Exception {
            LOGGER.trace(GLOOP_MARKER, "############### Start GLBuffer Map Query ###############");
            LOGGER.trace(GLOOP_MARKER, "\tMapping GLBuffer[{}]", GLBuffer.this.getName());
            LOGGER.trace(GLOOP_MARKER, "\tOffset: {} bytes", this.offset);
            LOGGER.trace(GLOOP_MARKER, "\tSize: {} bytes", this.length);

            if (!GLBuffer.this.isValid()) {
                throw new GLException("Invalid GLBuffer!");
            }

            final ByteBuffer out = GLTools.getDriverInstance().bufferMapData(buffer, this.offset, this.length, this.access);

            if (out == null) {
                LOGGER.error(
                        GLOOP_MARKER,
                        "gloop call: bufferMapData(buffer={}, offset={}, length={}, access={}) returned null!",
                        buffer, offset, length, access);
            }

            GLBuffer.this.updateTimeUsed();
            LOGGER.trace(GLOOP_MARKER, "############### End GLBuffer Map Query ###############");

            return out;
        }

        @Override
        protected ByteBuffer handleInterruption() {
            return ByteBuffer.allocate(0).asReadOnlyBuffer();
        }
    }

    /**
     * Requests that the GLBuffer is unmapped. This function should be called
     * after GLBuffer.map is called and before OpenGL next tries to read from
     * the GLBuffer.
     *
     * @return self reference.
     * @since 15.05.13
     */
    public GLBuffer unmap() {
        new UnmapTask().glRun(this.getThread());
        return this;
    }

    /**
     * A GLTask that requests for the GLBuffer to be unmapped.
     *
     * @since 15.05.13
     */
    public class UnmapTask extends GLTask {

        @SuppressWarnings("unchecked")
        @Override
        public void run() {
            LOGGER.trace(GLOOP_MARKER, "############### Start GLBuffer Unmap Task ###############");
            LOGGER.trace(GLOOP_MARKER, "\tUnmapping GLBuffer[{}]", GLBuffer.this.getName());

            if (!GLBuffer.this.isValid()) {
                throw new GLException("Invalid GLBuffer!");
            }

            GLTools.getDriverInstance().bufferUnmapData(buffer);
            GLBuffer.this.updateTimeUsed();
            LOGGER.trace(GLOOP_MARKER, "############### End GLBuffer Unmap Task ###############");
        }
    }

    /**
     * Copies data from one GLBuffer to another GLBuffer.
     *
     * @param src the buffer to copy data from.
     * @param srcOffset the offset to start copying data.
     * @param dest the buffer to copy data to.
     * @param destOffset the offset to start copying data.
     * @param size the number of bytes to copy.
     * @since 15.09.18
     */
    public static void copy(
            final GLBuffer src, final long srcOffset,
            final GLBuffer dest, final long destOffset,
            final long size) {

        new CopyTask(src, srcOffset, dest, destOffset, size).glRun(GLThread.getAny());
    }

    /**
     * A GLTask that requests for data to be copied from one GLBuffer into
     * another GLBuffer.
     *
     * @since 15.05.13
     */
    public static final class CopyTask extends GLTask {

        private final GLBuffer src;
        private final long srcOffset;
        private final GLBuffer dest;
        private final long destOffset;
        private final long size;

        /**
         * Constructs a new CopyTask.
         *
         * @param src the buffer to copy the data from
         * @param srcOffset the offset to start the copy
         * @param dest the buffer to copy the data to
         * @param destOffset the offset to start writing the data
         * @param size the number of bytes to copy
         * @since 15.05.13
         */
        public CopyTask(
                final GLBuffer src, final long srcOffset,
                final GLBuffer dest, final long destOffset,
                final long size) {

            this.src = src;
            this.dest = dest;
            this.srcOffset = srcOffset;
            this.destOffset = destOffset;
            this.size = size;
        }

        @SuppressWarnings("unchecked")
        @Override
        public void run() {
            LOGGER.trace(GLOOP_MARKER, "############### Start GLBuffer Copy Task ###############");
            LOGGER.trace(GLOOP_MARKER, "\tSrc GLBuffer[{}]", this.src.getName());
            LOGGER.trace(GLOOP_MARKER, "\tDest GLBUffer[{}]", this.dest.getName());
            LOGGER.trace(GLOOP_MARKER, "\tSrc offset: {} bytes", this.srcOffset);
            LOGGER.trace(GLOOP_MARKER, "\tDest offset: {} bytes", this.destOffset);
            LOGGER.trace(GLOOP_MARKER, "\tSize: {} bytes", this.size);

            if (!src.isValid() || !dest.isValid()) {
                throw new GLException("Invalid GLBuffer!");
            }

            GLTools.getDriverInstance().bufferCopyData(src.buffer, srcOffset, dest.buffer, destOffset, size);
            final GLThread thread = GLThread.getCurrent().get();

            src.updateTimeUsed();
            dest.updateTimeUsed();
            LOGGER.trace(GLOOP_MARKER, "############### End GLBuffer Copy Task ###############");
        }
    }

    /**
     * Checks if immutable storage is supported.
     *
     * @return true if context supports OpenGL 4.4 or ARB_buffer_storage.
     * @since 15.12.14
     */
    public static boolean isImmutableStorageSupported() {
        return new ImmutableStorageSupportedQuery().glCall();
    }

    /**
     * OpenGL query that checks if immutable storage is supported.
     *
     * @since 15.12.14
     */
    public static final class ImmutableStorageSupportedQuery extends GLQuery<Boolean> {

        @Override
        public Boolean call() throws Exception {
            return GLTools.hasOpenGLVersion(44);
        }

    }

    /**
     * Invalidates a sub-section of the GLBuffer.
     *
     * @param offset the offset to invalidate.
     * @param length the number of bytes to invalidate.
     * @since 16.01.04
     */
    public void invalidateSubData(final int offset, final int length) {
        new InvalidateSubDataTask(offset, length).glRun(this.getThread());
    }

    /**
     * A GLTask that invalidates a segment of a GLBuffer.
     *
     * @since 16.01.04
     */
    public final class InvalidateSubDataTask extends GLTask {

        private final int offset;
        private final int length;

        /**
         * Constructs a new InvalidateSubDataTask.
         *
         * @param offset the offset
         * @param length the length
         * @since 16.01.04s
         */
        public InvalidateSubDataTask(final int offset, final int length) {
            if ((this.offset = offset) < 0) {
                throw new GLException("Offset cannot be less than 0!");
            } else if ((this.length = length) < 0) {
                throw new GLException("Length cannot be less than 0!");
            }
        }

        @SuppressWarnings("unchecked")
        @Override
        public void run() {
            LOGGER.trace(GLOOP_MARKER, "############### Start GLBuffer InvalidateSubData Task ###############");
            if (!GLBuffer.this.isValid()) {
                throw new GLException("Invalid GLBuffer!");
            }

            GLTools.getDriverInstance().bufferInvalidateRange(buffer, offset, length);
            GLBuffer.this.updateTimeUsed();
            LOGGER.trace(GLOOP_MARKER, "############### End GLBuffer InvalidateSubData Task ###############");
        }
    }

    /**
     * Invalidates the GLBuffer.
     *
     * @since 16.01.04
     */
    public void invalidate() {
        new InvalidateTask().glRun(this.getThread());
    }

    /**
     * A GLTask that invalidates the GLBuffer.
     *
     * @since 16.01.04
     */
    public final class InvalidateTask extends GLTask {

        @SuppressWarnings("unchecked")
        @Override
        public void run() {
            LOGGER.trace(GLOOP_MARKER, "############### Start GLBuffer Invalidate Task ###############");

            if (!GLBuffer.this.isValid()) {
                throw new GLException("Invalid GLBuffer!");
            }

            GLTools.getDriverInstance().bufferInvalidateData(buffer);
            GLBuffer.this.updateTimeUsed();
            LOGGER.trace(GLOOP_MARKER, "############### End GLBuffer Invalidate Task ###############");
        }
    }

    @Override
    public long getTimeSinceLastUsed() {
        return (System.nanoTime() - this.lastUsedTime);
    }
}
