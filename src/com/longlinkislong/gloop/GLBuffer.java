/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.longlinkislong.gloop;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.util.Objects;
import org.lwjgl.opengl.GL15;
import org.lwjgl.opengl.GL31;

/**
 * GLBuffer represents an OpenGL Buffer object.
 *
 * @author zmichaels
 * @since 15.05.14
 */
public class GLBuffer extends GLObject {

    private static final int INVALID_BUFFER_ID = -1;
    protected int bufferId = INVALID_BUFFER_ID;
    private ByteBuffer mappedBuffer = null;

    /**
     * Constructs a new GLBuffer when possible on the default GLThread.
     *
     * @since 15.05.15
     */
    public GLBuffer() {
        super();
        this.init();
    }

    /**
     * Constructs a new GLBuffer when possible on the supplied GLThread.
     *
     * @param thread the GLThread to create the GLBuffer on.
     */
    public GLBuffer(final GLThread thread) {
        super(thread);
        this.init();
    }

    @Override
    public String toString() {
        return "GLBuffer: " + this.bufferId;
    }

    /**
     * Checks if the GLBuffer is valid. a
     *
     * @return true if the buffer is valid.
     * @since 15.05.13
     */
    public boolean isValid() {
        return this.bufferId != INVALID_BUFFER_ID;
    }

    private final GLTask initTask = new InitTask();

    /**
     * Initializes this GLBuffer. All GLBuffers are initialized automatically
     * some time after the constructor is called. This method should only be
     * called if the GLBuffer has been deleted.
     *
     * @since 15.05.13
     */
    public final void init() {
        this.initTask.glRun(this.getThread());
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
            if (!GLBuffer.this.isValid()) {
                GLBuffer.this.bufferId = GL15.glGenBuffers();
            } else {
                throw new GLException("GLBuffer is already initialized!");
            }
        }
    }

    private ParameterQuery lastParameterQuery = null;

    /**
     * Sends an OpenGL query asking for a property. It will bind the GLBuffer as
     * an GL_ARRAY_BUFFER prior to sending the request. This function can cause
     * thread sync.
     *
     * @param pName the parameter name to query
     * @return th result of the query
     * @since 15.05.13
     */
    public final int getParameter(final GLBufferParameterName pName) {
        return this.getParameter(GLBufferTarget.GL_ARRAY_BUFFER, pName);
    }

    /**
     * Sends an OpenGL query asking for a property
     *
     * @param target the target to bind the GLBuffer to. Default is
     * GL_ARRAY_BUFFER. This function can cause thread sync.
     * @param pName the parameter name to query.
     * @return the GLBuffer parameter value.
     * @since 15.05.13
     */
    public final int getParameter(
            final GLBufferTarget target, final GLBufferParameterName pName) {

        if (lastParameterQuery != null
                && target == lastParameterQuery.target
                && pName == lastParameterQuery.pName) {

            return lastParameterQuery.glCall(this.getThread());
        } else {
            this.lastParameterQuery = new ParameterQuery(target, pName);

            return this.lastParameterQuery.glCall(this.getThread());
        }
    }

    /**
     * A GLQuery that requests for state information from a GLBuffer. The query
     * will fail if the GLBuffer is not initialized.
     *
     * @since 15.05.13
     */
    public class ParameterQuery extends GLQuery<Integer> {

        final GLBufferTarget target;
        final GLBufferParameterName pName;

        /**
         * Constructs a new ParameterQuery, binding the GLBuffer to the target
         * GL_ARRAY_BUFFER.
         *
         * @param pName the query to request
         * @since 15.05.13
         */
        public ParameterQuery(
                final GLBufferParameterName pName) {
            this(GLBufferTarget.GL_ARRAY_BUFFER, pName);
        }

        /**
         * Constructs a new ParameterQuery.
         *
         * @param target the target to bind the GLBuffer to
         * @param pName the query to request
         * @since 15.05.13
         */
        public ParameterQuery(
                final GLBufferTarget target,
                final GLBufferParameterName pName) {

            Objects.requireNonNull(this.target = target);
            Objects.requireNonNull(this.pName = pName);

            if (this.target != GLBufferTarget.GL_ARRAY_BUFFER
                    || this.target != GLBufferTarget.GL_ELEMENT_ARRAY_BUFFER) {

                throw new GLException("Target must be either GL_ARRAY_BUFFER or GL_ELEMENT_BUFFER!");
            }
        }

        @Override
        public Integer call() throws Exception {
            if (!GLBuffer.this.isValid()) {
                throw new GLException("Invalid GLBuffer!");
            }

            GL15.glBindBuffer(this.target.value, GLBuffer.this.bufferId);
            return GL15.glGetBufferParameteri(
                    this.target.value,
                    this.pName.value);
        }

    }

    private final GLTask deleteTask = new DeleteTask();

    /**
     * Deletes the GLBuffer. This call will fail if the GLBuffer has already
     * been deleted or is in an invalid state.
     *
     * @since 15.05.13
     */
    public void delete() {
        this.deleteTask.glRun(this.getThread());
    }

    /**
     * Constructs a new GLTask for deleting the GLBuffer. This task will fail if
     * the GLBuffer has already been deleted or is in an invalid state.
     *
     * @since 15.05.13
     */
    public class DeleteTask extends GLTask {

        @Override
        public void run() {
            if (GLBuffer.this.isValid()) {
                GL15.glDeleteBuffers(GLBuffer.this.bufferId);
                GLBuffer.this.bufferId = INVALID_BUFFER_ID;
            }
        }
    }

    private UploadTask lastUploadTask = null;

    /**
     * Uploads the supplied data to the GLBuffer. GL_ARRAY_BUFFER is used for
     * the target and GL_STATIC_DRAW is used for the usage.
     *
     * @param data
     */
    public void upload(final ByteBuffer data) {
        this.upload(
                GLBufferTarget.GL_ARRAY_BUFFER,
                data,
                GLBufferUsage.GL_STATIC_DRAW);
    }

    /**
     * Uploads the supplied data to the GLBuffer. GL_STATIC_DRAW is used for
     * usage.
     *
     * @param target the target to bind to. Under most circumstances, this
     * should be GL_ARRAY_BUFFER or GL_ELEMENT_ARRAY_BUFFER.
     * @param data the data to upload
     * @since 15.05.13
     */
    public void upload(final GLBufferTarget target, final ByteBuffer data) {
        this.upload(target, data, GLBufferUsage.GL_STATIC_DRAW);
    }

    /**
     * Uploads the supplied data to the GLBuffer.
     *
     * @param target the target to bind to. Under most circumstances, this
     * should be GL_ARRAY_BUFFER or GL_ELEMENT_ARRAY_BUFFER.
     * @param data the data to upload
     * @param usage the usage of the data. Under most circumstances, this should
     * be GL_STATIC_DRAW. GL_DYNAMIC_DRAW should be used if the GLBuffer is ever
     * mapped.
     * @since 15.05.13
     */
    public void upload(
            final GLBufferTarget target,
            final ByteBuffer data,
            final GLBufferUsage usage) {

        if (lastUploadTask != null
                && lastUploadTask.target == target
                && lastUploadTask.usage == usage
                && lastUploadTask.data == data) {

            this.lastUploadTask.glRun(this.getThread());
        } else {
            this.lastUploadTask = new UploadTask(
                    target,
                    data,
                    usage);
            this.lastUploadTask.glRun(this.getThread());
        }
    }

    /**
     * A GLTask that uploads data to a GLBuffer. This task will fail if the
     * GLBuffer is in an invalid state.
     *
     * @since 15.05.13
     */
    public class UploadTask extends GLTask {

        final GLBufferTarget target;
        final GLBufferUsage usage;
        final ByteBuffer data;

        /**
         * Constructs a new UploadTask. The GLBuffer is bound to the specified
         * target and GL_STATIC_DRAW is used for the mode.
         *
         * @param target the target to bind the GLBuffer to.
         * @param data the data to upload.
         * @since 15.05.13
         */
        public UploadTask(final GLBufferTarget target, final ByteBuffer data) {
            this(target, data, GLBufferUsage.GL_STATIC_DRAW);
        }

        /**
         * Constructs a new UploadTask. The GLBuffer is bound to the specified
         * target for upload.
         *
         * @param target the target to bind to.
         * @param data the data to upload
         * @param usage the mode to set the data to. GL_STATIC_DRAW or
         * GL_DYNAMIC_DRAW are satisfactory for most tasks.
         * @since 15.05.13
         */
        public UploadTask(
                final GLBufferTarget target,
                final ByteBuffer data,
                final GLBufferUsage usage) {

            if (!data.isDirect()) {
                throw new GLException("Backing data buffer is not direct!");
            } else if (data.order() != ByteOrder.nativeOrder()) {
                throw new GLException("Data is not in native order!");
            }

            this.target = target;
            this.usage = usage;
            this.data = data;
        }

        @Override
        public void run() {
            if (!GLBuffer.this.isValid()) {
                throw new GLException("GLBuffer is invalid!");
            }

            GL15.glBindBuffer(this.target.value, GLBuffer.this.bufferId);
            GL15.glBufferData(this.target.value, this.data, this.usage.value);
        }
    }

    private AllocateTask allocateTask = null;

    /**
     * Allocates the specified number of bytes for the GLBuffer. GL_ARRAY_BUFFER
     * is used for the target.
     *
     * @param size the number of bytes to allocate
     * @since 15.05.13
     */
    public void allocate(final long size) {
        this.allocate(GLBufferTarget.GL_ARRAY_BUFFER, size);
    }

    /**
     * Allocates the specified number of bytes for the GLBuffer
     *
     * @param target the target to bind the GLBuffer to.
     * @param size the number of bytes to allocate
     * @since 15.05.13
     */
    public void allocate(final GLBufferTarget target, final long size) {
        if (this.allocateTask != null
                && this.allocateTask.target == target
                && this.allocateTask.size == size) {

            this.allocateTask.glRun(this.getThread());
        } else {
            this.allocateTask = new AllocateTask(target, size);

            this.allocateTask.glRun(this.getThread());
        }
    }

    /**
     * A GLTask that allocates the specified number of bytes for the GLBuffer.
     * This task will fail if the GLBuffer is in an invalid state.
     *
     * @since 15.05.13
     */
    public class AllocateTask extends GLTask {

        private final GLBufferTarget target;
        private final long size;
        private final GLBufferUsage usage;

        /**
         * Constructs a new AllocateTask using the default usage
         * (GL_DYNAMIC_DRAW).
         *
         * @param target the target to bind the buffer to
         * @param size the number of bytes to allocate
         * @since 15.05.13
         */
        public AllocateTask(final GLBufferTarget target, final long size) {

            this(target, size, GLBufferUsage.GL_DYNAMIC_DRAW);
        }

        /**
         * Constructs a new AllocateTask.
         *
         * @param target the target to bind the buffer to
         * @param size the number of bytes to allocate
         * @param usage the usage to use.
         * @since 15.05.13
         */
        public AllocateTask(
                final GLBufferTarget target,
                final long size,
                final GLBufferUsage usage) {

            Objects.requireNonNull(this.target = target);
            Objects.requireNonNull(this.usage = usage);

            if ((this.size = size) < 0) {
                throw new GLException("Size cannot be less than 0!");
            }
        }

        @Override
        public void run() {
            if (!GLBuffer.this.isValid()) {
                throw new GLException("Invalid GLBuffer!");
            }

            GL15.glBindBuffer(this.target.value, GLBuffer.this.bufferId);
            GL15.glBufferData(this.target.value, this.size, this.usage.value);
        }

    }

    private DownloadQuery downloadQuery = null;

    /**
     * Requests a copy of the data stored inside the GLBuffer. The buffer is
     * bound to GL_ARRAY_BUFFER and a new ByteBuffer is allocated to write the
     * data to. This function can cause a thread sync.
     *
     * @return the ByteBuffer.
     * @since 15.05.13
     */
    public ByteBuffer download() {
        return this.download(GLBufferTarget.GL_ARRAY_BUFFER, 0, null);
    }

    /**
     * Requests a copy of the data stored inside the GLBuffer. A new ByteBuffer
     * is allocated to write the data to. This function can cause a thread sync.
     *
     * @param target the target the buffer will be bound to.
     * @return the data
     * @since 15.05.27
     */
    public ByteBuffer download(final GLBufferTarget target) {
        return this.download(target, 0, null);
    }

    /**
     * Requests a copy of the data stored inside the GLBuffer. This function can
     * cause a thread sync.
     *
     * @param target the target to bind the buffer to
     * @param offset the offset to start reading from
     * @param writeBuffer the buffer to write the data to
     * @return the buffer post write.
     * @since 15.05.13
     */
    public ByteBuffer download(
            final GLBufferTarget target,
            final long offset,
            final ByteBuffer writeBuffer) {

        if (this.downloadQuery != null
                && this.downloadQuery.target == target
                && this.downloadQuery.offset == offset
                && this.downloadQuery.writeBuffer == writeBuffer) {

            return this.downloadQuery.glCall(this.getThread());
        } else {
            this.downloadQuery = new DownloadQuery(target, offset, writeBuffer);

            return this.downloadQuery.glCall(this.getThread());
        }
    }

    /**
     * Constructs a new DownloadQuery. This requests OpenGL to download the data
     * stored in the GLBuffer and to store it into a ByteBuffer.
     *
     * @since 15.05.13
     */
    public class DownloadQuery extends GLQuery<ByteBuffer> {

        final GLBufferTarget target;
        final long offset;
        final ByteBuffer writeBuffer;

        /**
         * Constructs a new DownloadQuery, binding the GLBuffer to
         * GL_ARRAY_BUFFER and constructing a new ByteBuffer to write to.
         *
         * @since 15.05.13
         */
        public DownloadQuery() {
            this(GLBufferTarget.GL_ARRAY_BUFFER, 0);
        }

        /**
         * Constructs a new DownloadQuery using a new ByteBuffer to write to.
         *
         * @param target the target to bind the GLBuffer to prior to download
         * @since 15.05.13
         */
        public DownloadQuery(final GLBufferTarget target) {
            this(target, 0);
        }

        /**
         * Constructs a new DownloadQuery using a new ByteBuffer to write to.
         *
         * @param target the target to bind the GLBuffer to
         * @param offset the offset to start reading at
         * @since 15.05.13
         */
        public DownloadQuery(
                final GLBufferTarget target,
                final long offset) {

            this.target = target;
            this.offset = offset;
            this.writeBuffer = null;
        }

        /**
         * Constructs a new DownloadQuery.
         *
         * @param target the target to bind to
         * @param offset the offset to start reading from
         * @param writeBuffer the buffer to write the data to
         * @since 15.05.13
         */
        public DownloadQuery(
                final GLBufferTarget target,
                final long offset, final ByteBuffer writeBuffer) {

            if (!writeBuffer.isDirect()) {
                throw new GLException("Output buffer must be direct!");
            } else if (writeBuffer.order() != ByteOrder.nativeOrder()) {
                throw new GLException("Output buffer must be in native order!");
            }

            this.target = target;
            this.offset = offset;
            this.writeBuffer = writeBuffer;
        }

        @Override
        public ByteBuffer call() throws Exception {
            if (!GLBuffer.this.isValid()) {
                throw new GLException("Invalid GLBuffer!");
            }

            final ByteBuffer out;

            GL15.glBindBuffer(this.target.value, bufferId);

            if (this.writeBuffer == null) {
                final int size = GL15.glGetBufferParameteri(
                        this.target.value,
                        GLBufferParameterName.GL_BUFFER_SIZE.value);

                out = ByteBuffer.allocateDirect(size)
                        .order(ByteOrder.nativeOrder());

            } else {
                out = this.writeBuffer;
            }

            GL15.glGetBufferSubData(
                    this.target.value,
                    this.offset,
                    out);

            return out;
        }
    }

    private MapQuery mapQuery = null;

    /**
     * Maps the GLBuffer to a ByteBuffer. The GLBuffer is bound to
     * GL_ARRAY_BUFFER. This function can force a thread sync.
     *
     * @return the mapped ByteBuffer
     * @since 15.05.13
     */
    public ByteBuffer map() {
        return this.map(GLBufferTarget.GL_ARRAY_BUFFER);
    }

    /**
     * Maps the GLBuffer to a ByteBuffer. This function can force a thread sync.
     *
     * @param target the target to bind the GLBuffer to.
     * @return the mapped ByteBuffer.
     * @since 15.05.13
     */
    public ByteBuffer map(final GLBufferTarget target) {
        if (this.mapQuery != null && this.mapQuery.target == target) {
            return this.mapQuery.glCall(this.getThread());
        } else {
            this.mapQuery = new MapQuery(target, GLBufferAccess.GL_READ_WRITE);

            return this.mapQuery.glCall(this.getThread());
        }
    }

    /**
     * A GLQuery that requests the buffer to be mapped. A GLBuffer can only be
     * mapped once at a time and should be unmapped before being used elsewhere.
     *
     * @since 15.05.13
     */
    public class MapQuery extends GLQuery<ByteBuffer> {

        final GLBufferTarget target;
        final GLBufferAccess access;

        /**
         * Constructs a new MapQuery using the default GL_ARRAY and setting the
         * access to READ_WRITE.
         *
         * @since 15.05.13
         */
        public MapQuery() {
            this(GLBufferTarget.GL_ARRAY_BUFFER, GLBufferAccess.GL_READ_WRITE);
        }

        /**
         * Constructs a new MapQuery. The access is set to READ_WRITE.
         *
         * @param target the target to bind to
         * @since 15.05.13
         */
        public MapQuery(final GLBufferTarget target) {
            this(target, GLBufferAccess.GL_READ_WRITE);
        }

        /**
         * Constructs a new MapQuery.
         *
         * @param target the target to bind to
         * @param access the access privileges to use
         * @since 15.05.13
         */
        public MapQuery(
                final GLBufferTarget target, final GLBufferAccess access) {

            this.target = target;
            this.access = access;
        }

        @Override
        public ByteBuffer call() throws Exception {
            if (!GLBuffer.this.isValid()) {
                throw new GLException("Invalid GLBuffer!");
            }

            final ByteBuffer oldBuffer = GLBuffer.this.mappedBuffer;

            GL15.glBindBuffer(this.target.value, bufferId);
            final ByteBuffer newBuffer = GL15.glMapBuffer(
                    this.target.value, bufferId, oldBuffer);

            GLBuffer.this.mappedBuffer = newBuffer;

            return newBuffer;
        }
    }

    private final GLTask unmapTask = new UnmapTask();

    /**
     * Requests that the GLBuffer is unmapped. This function should be called
     * after GLBuffer.map is called and before OpenGL next tries to read from
     * the GLBuffer.
     *
     * @since 15.05.13
     */
    public void unmap() {
        this.unmapTask.glRun(this.getThread());
    }

    /**
     * A GLTask that requests for the GLBuffer to be unmapped.
     *
     * @since 15.05.13
     */
    public class UnmapTask extends GLTask {

        final GLBufferTarget target;

        /**
         * Constructs a new UnmapTask. The buffer is bound to GL_ARRAY_BUFFER.
         *
         * @since 15.05.13
         */
        public UnmapTask() {
            this(GLBufferTarget.GL_ARRAY_BUFFER);
        }

        /**
         * Constructs a new UnmapTask.
         *
         * @param target the target to bind to
         * @since 15.05.13
         */
        public UnmapTask(final GLBufferTarget target) {
            this.target = target;
        }

        @Override
        public void run() {
            if (!GLBuffer.this.isValid()) {
                throw new GLException("Invalid GLBuffer!");
            }

            GL15.glBindBuffer(this.target.value, bufferId);
            GL15.glUnmapBuffer(this.target.value);
        }

    }

    private CopyTask lastCopyTask = null;

    public static void copy(
            final GLBuffer src, final long srcOffset,
            final GLBuffer dest, final long destOffset,
            final long size) {

        if (src.getThread() != dest.getThread()) {
            throw new GLException("Source GLBuffer and destination GLBuffer are on different GLThreads!");
        }

        if (src.lastCopyTask != null
                && src.lastCopyTask.src == src
                && src.lastCopyTask.srcOffset == srcOffset
                && src.lastCopyTask.dest == dest
                && src.lastCopyTask.destOffset == destOffset
                && src.lastCopyTask.size == size) {

            src.lastCopyTask.glRun(src.getThread());
        }
    }

    /**
     * A GLTask that requests for data to be copied from one GLBuffer into
     * another GLBuffer.
     *
     * @since 15.05.13
     */
    public static class CopyTask extends GLTask {

        final GLBuffer src;
        final long srcOffset;
        final GLBuffer dest;
        final long destOffset;
        final long size;

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

        @Override
        public void run() {
            if (!src.isValid() || !dest.isValid()) {
                throw new GLException("Invalid GLBuffer!");
            }

            GL15.glBindBuffer(
                    GLBufferTarget.GL_COPY_READ_BUFFER.value,
                    this.src.bufferId);

            GL15.glBindBuffer(
                    GLBufferTarget.GL_COPY_WRITE_BUFFER.value,
                    this.dest.bufferId);

            GL31.glCopyBufferSubData(
                    GLBufferTarget.GL_COPY_READ_BUFFER.value,
                    GLBufferTarget.GL_COPY_WRITE_BUFFER.value,
                    this.srcOffset,
                    this.destOffset,
                    this.size);
        }
    }
}
