/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.longlinkislong.gloop;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.util.Objects;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL15;

/**
 * GLBuffer represents an OpenGL Buffer object.
 *
 * @author zmichaels
 * @see <a href="https://www.opengl.org/wiki/Buffer_Object">Buffer Object</a>
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

    /**
     * Initializes this GLBuffer. All GLBuffers are initialized automatically
     * some time after the constructor is called. This method should only be
     * called if the GLBuffer has been deleted.
     *
     * @since 15.05.13
     */
    public final void init() {
        new InitTask().glRun(this.getThread());
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
                GLBuffer.this.bufferId = GLTools.getDSAInstance().glCreateBuffers();
            } else {
                throw new GLException("GLBuffer is already initialized!");
            }
        }
    }

    private ParameterQuery lastParameterQuery = null;

    /**
     * Sends an OpenGL query asking for a property
     *
     * @param pName the parameter name to query.
     * @return the GLBuffer parameter value.
     * @since 15.05.13
     */
    public final int getParameter(final GLBufferParameterName pName) {

        if (lastParameterQuery != null && pName == lastParameterQuery.pName) {
            return lastParameterQuery.glCall(this.getThread());
        } else {
            this.lastParameterQuery = new ParameterQuery(pName);

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

        @Override
        public Integer call() throws Exception {
            if (!GLBuffer.this.isValid()) {
                throw new GLException("Invalid GLBuffer!");
            }

            return GLTools.getDSAInstance().glGetNamedBufferParameteri(GLBuffer.this.bufferId, this.pName.value);
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

        @Override
        public void run() {
            if (GLBuffer.this.isValid()) {
                GL15.glDeleteBuffers(GLBuffer.this.bufferId);

                assert GL11.glGetError() == GL11.GL_NO_ERROR : String.format("glDeleteBuffers(%d) failed!", GLBuffer.this.bufferId);

                GLBuffer.this.bufferId = INVALID_BUFFER_ID;
            }
        }
    }

    private UploadTask lastUploadTask = null;

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

        if (lastUploadTask != null
                && lastUploadTask.usage == usage
                && lastUploadTask.data == data) {

            this.lastUploadTask.glRun(this.getThread());
        } else {
            this.lastUploadTask = new UploadTask(data, usage);
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

        @Override
        public void run() {
            if (!GLBuffer.this.isValid()) {
                throw new GLException("GLBuffer is invalid!");
            }

            GLTools.getDSAInstance().glNamedBufferData(GLBuffer.this.bufferId, this.data, this.usage.value);
        }
    }

    /**
     * Allocates the specified number of bytes for the GLBuffer
     *
     * @param size the number of bytes to allocate
     * @since 15.05.13
     */
    public void allocate(final long size) {
        this.allocate(size, GLBufferUsage.GL_DYNAMIC_DRAW);
    }

    /**
     * Allocates the specified number of bytes for the GLBuffer with the
     * requested usage hint.
     *
     * @param size the number of bytes to allocate.
     * @param usage the usage hint.
     * @since 15.07.06
     */
    public void allocate(final long size, final GLBufferUsage usage) {
        new AllocateTask(size, usage).glRun(this.getThread());
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

        @Override
        public void run() {
            if (!GLBuffer.this.isValid()) {
                throw new GLException("Invalid GLBuffer!");
            }

            GLTools.getDSAInstance().glNamedBufferData(GLBuffer.this.bufferId, this.size, this.usage.value);
        }

    }

    private DownloadQuery downloadQuery = null;

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

        if (this.downloadQuery != null
                && this.downloadQuery.offset == offset
                && this.downloadQuery.writeBuffer == writeBuffer) {

            return this.downloadQuery.glCall(this.getThread());
        } else {
            this.downloadQuery = new DownloadQuery(offset, writeBuffer);

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

        @Override
        public ByteBuffer call() throws Exception {
            if (!GLBuffer.this.isValid()) {
                throw new GLException("Invalid GLBuffer!");
            }

            final ByteBuffer out;

            if (this.writeBuffer == null) {
                final int size = GLTools.getDSAInstance().glGetNamedBufferParameteri(GLBuffer.this.bufferId, GLBufferParameterName.GL_BUFFER_SIZE.value);

                out = ByteBuffer.allocateDirect(size)
                        .order(ByteOrder.nativeOrder());

            } else {
                out = this.writeBuffer;
            }

            GLTools.getDSAInstance().glGetNamedBufferSubData(GLBuffer.this.bufferId, this.offset, out);

            return out;
        }
        
        @Override
        protected ByteBuffer handleInterruption() {
            return ByteBuffer.allocate(0).asReadOnlyBuffer();
        }
    }

    /**
     * Reads a chunk of the current framebuffer into a pixel buffer.
     *
     * @param x the starting x-location.
     * @param y the starting y-location.
     * @param width the number of pixels to read along the x-axis.
     * @param height the number of pixels to read along the y-axis.
     * @param format the format used to store elements inside the buffer.
     * @param type the data type used.
     * @since 15.07.13
     */
    public void readPixels(final int x, final int y, final int width, final int height, final GLTextureInternalFormat format, final GLType type) {
        new ReadPixelsTask(x, y, width, height, format, type).glRun(this.getThread());
    }

    /**
     * A GLTask that reads a chunk of pixel data from the current framebuffer
     * object.
     *
     * @since 15.07.13
     */
    public class ReadPixelsTask extends GLTask {

        public final int width;
        public final int height;
        public final int x;
        public final int y;
        public final GLTextureInternalFormat format;
        public final GLType type;

        /**
         * Reads a chunk of the current framebuffer into a pixel buffer.
         *
         * @param x the starting x-location.
         * @param y the starting y-location.
         * @param width the number of pixels to read along the x-axis.
         * @param height the number of pixels to read along the y-axis.
         * @param format the format used for element order.
         * @param type the data type used.
         * @since 15.07.13
         */
        public ReadPixelsTask(final int x, final int y, final int width, final int height, final GLTextureInternalFormat format, final GLType type) {
            if ((this.x = x) < 0) {
                throw new GLException("x cannot be less than 0!");
            } else if ((this.y = y) < 0) {
                throw new GLException("y cannot be less than 0!");
            } else if ((this.width = width) < 1) {
                throw new GLException("width cannot be less than 1!");
            } else if ((this.height = height) < 1) {
                throw new GLException("height cannot be less than 1!");
            }

            this.format = Objects.requireNonNull(format);
            this.type = Objects.requireNonNull(type);
        }

        @Override
        public void run() {
            GLTools.getDSAInstance().glNamedBufferReadPixels(bufferId, x, y, width, height, format.value, type.value, 0);
        }
    }

    /**
     * Maps the GLBuffer to a ByteBuffer for read-write access. This function
     * can force a thread sync.
     *     
     * @param offset the offset in bytes to begin the map.
     * @param length the number of bytes to map.
     * @return the mapped ByteBuffer.
     * @see
     * <a href="https://www.opengl.org/wiki/GLAPI/glMapBufferRange#Function_Definition">glMapBufferRange</a>
     * @since 15.05.13
     */
    public ByteBuffer map(final long offset, final long length) {
        return this.map(offset, length, GLBufferAccess.GL_MAP_READ, GLBufferAccess.GL_MAP_WRITE);
    }

    /**
     * Maps a GLBuffer to a ByteBuffer. This function can force a thread sync.
     *     
     * @param offset the offset in bytes to begin the map.
     * @param length the number of bytes to map.
     * @param accessBits the flags indicating access rights to the data.
     * @return the mapped ByteBuffer
     * @see
     * <a href="https://www.opengl.org/wiki/GLAPI/glMapBufferRange#Function_Definition">glMapBufferRange</a>
     * @since 15.06.23
     */
    public ByteBuffer map(final long offset, final long length,
            final GLBufferAccess... accessBits) {

        return this.map(offset, length, accessBits, 0, accessBits.length);
    }

    /**
     * Maps a GLBuffer to a ByteBuffer. This function can force a thread sync.
     *     
     * @param offset the offset in bytes to begin the map.
     * @param length the number of bytes to map.
     * @param accessBits the flags indicating access rights to the data.
     * @param accessOffset the offset to start reading the flags.
     * @param accessLength the number of accessFlags to read.
     * @return the mapped ByteBuffer
     * @see
     * <a href="https://www.opengl.org/wiki/GLAPI/glMapBufferRange#Function_Definition">glMapBufferRange</a>
     * @since 15.06.23
     */
    public ByteBuffer map(final long offset, final long length,
            final GLBufferAccess[] accessBits, final int accessOffset, final int accessLength) {

        return new MapQuery(offset, length, accessBits, accessOffset, accessLength).glCall(this.getThread());
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
        final int access;
        final long offset;
        final long length;

        /**
         * Constructs a new MapQuery using a sequence of accessbits.
         *         
         * @param offset the starting offset within the buffer range to be
         * mapped.
         * @param length the length of the range to be mapped.
         * @param accessBits the flags indicating the desired access.
         * @since 15.06.23
         */
        public MapQuery(final long offset, final long length, final GLBufferAccess... accessBits) {

            this(offset, length, accessBits, 0, accessBits.length);
        }

        /**
         * Constructs a new MapQuery.
         *         
         * @param offset the starting offset within the buffer range to be
         * mapped.
         * @param length the length of the range to be mapped.
         * @param accessBits the flags indicating the desired access to the
         * range.
         * @param accessOffset the offset to start reading from the access
         * flags.
         * @param accessLength the number of access flags to read.
         * @since 15.05.13
         */
        public MapQuery(                
                final long offset, final long length,
                final GLBufferAccess[] accessBits, final int accessOffset, final int accessLength) {
            
            this.offset = offset;
            this.length = length;

            int bitVal = 0;
            for (int i = accessOffset; i < accessOffset + accessLength; i++) {
                bitVal |= accessBits[i].value;
            }

            this.access = bitVal;
        }

        @Override
        public ByteBuffer call() throws Exception {
            if (!GLBuffer.this.isValid()) {
                throw new GLException("Invalid GLBuffer!");
            }

            final ByteBuffer oldBuffer = GLBuffer.this.mappedBuffer;
            final ByteBuffer newBuffer = GLTools.getDSAInstance()
                    .glMapNamedBufferRange(
                            GLBuffer.this.bufferId,
                            this.offset, this.length,
                            this.access, oldBuffer);

            GLBuffer.this.mappedBuffer = newBuffer;

            return newBuffer;
        }
        
        @Override
        protected ByteBuffer handleInterruption() {
            return ByteBuffer.allocate(0).asReadOnlyBuffer();
        }
    }

    /**
     * Maps the GLBuffer to a ByteBuffer for read-write access. This function
     * uses OpenGL 4.4 persist hints GL_MAP_PERSISTENT and GL_MAP_COHERENT as
     * well as allocated memory with GLBufferStorage. The returned ByteBuffer
     * will be a persistent mapping of memory between the GPU and the CPU. In
     * order to achieve thread safety, you should use glFenceSync to ensure you
     * are not writing data to the buffer as it is being read by the CPU as this
     * has undefined results. Calling this function can force a thread sync.
     *
     * @see
     * <a href="https://www.opengl.org/wiki/GLAPI/glMapBufferRange#Function_Definition">glMapBufferRange</a>
     *
     * @param length the number of bytes to map.
     * @return the mapped ByteBuffer.
     * @see
     * <a href="https://www.opengl.org/wiki/GLAPI/glMapBufferRange#Function_Definition">glMapBufferRange</a>
     * @since 15.05.13
     */
    public ByteBuffer mapPersist(final long length) {
        return this.mapPersist(length, GLBufferAccess.GL_MAP_READ, GLBufferAccess.GL_MAP_WRITE);
    }

    /**
     * Maps the GLBuffer to a ByteBuffer for read-write access. This function
     * uses OpenGL 4.4 persist hints GL_MAP_PERSISTENT and GL_MAP_COHERENT as
     * well as allocated memory with GLBufferStorage. The returned ByteBuffer
     * will be a persistent mapping of memory between the GPU and the CPU. In
     * order to achieve thread safety, you should use glFenceSync to ensure you
     * are not writing data to the buffer as it is being read by the CPU as this
     * has undefined results. Calling this function can force a thread sync.
     *
     * @see
     * <a href="https://www.opengl.org/wiki/GLAPI/glMapBufferRange#Function_Definition">glMapBufferRange</a>
     *
     * @param length the number of bytes to map.
     * @param accessBits the flags indicating the desired access.
     * @return the ByteBuffer containing the data.
     */
    public ByteBuffer mapPersist(final long length, final GLBufferAccess... accessBits) {

        return this.mapPersist(length, accessBits, 0, accessBits.length);
    }

    /**
     * Maps the GLBuffer to a ByteBuffer for read-write access. This function
     * uses OpenGL 4.4 persist hints GL_MAP_PERSISTENT and GL_MAP_COHERENT as
     * well as allocated memory with GLBufferStorage. The returned ByteBuffer
     * will be a persistent mapping of memory between the GPU and the CPU. In
     * order to achieve thread safety, you should use glFenceSync to ensure you
     * are not writing data to the buffer as it is being read by the CPU as this
     * has undefined results. Calling this function can force a thread sync.
     *
     * @see
     * <a href="https://www.opengl.org/wiki/GLAPI/glMapBufferRange#Function_Definition">glMapBufferRange</a>
     *
     * @param length the number of bytes to map.
     * @param accessBits the flags indicating the desired access.
     * @param accessOffset the offset to start reading from the access flags.
     * @param accessLength the number of access flags to read.
     * @return the ByteBuffer containing the data.
     * @since 15.07.08
     */
    public ByteBuffer mapPersist(final long length,
            final GLBufferAccess[] accessBits, final int accessOffset, final int accessLength) {

        return new MapPersistQuery(length, accessBits, accessOffset, accessLength).glCall(this.getThread());
    }

    /**
     * Maps the GLBuffer to a ByteBuffer for read-write access. This function
     * uses OpenGL 4.4 persist hints GL_MAP_PERSISTENT and GL_MAP_COHERENT as
     * well as allocated memory with GLBufferStorage. The returned ByteBuffer
     * will be a persistent mapping of memory between the GPU and the CPU. In
     * order to achieve thread safety, you should use glFenceSync to ensure you
     * are not writing data to the buffer as it is being read by the CPU as this
     * has undefined results. Calling this function can force a thread sync.
     *
     * @see
     * <a href="https://www.opengl.org/wiki/GLAPI/glMapBufferRange#Function_Definition">glMapBufferRange</a>
     *
     */
    public class MapPersistQuery extends GLQuery<ByteBuffer> {

        final int access;
        final long length;

        /**
         * Constructs a new MapQuery using a sequence of accessbits.
         *
         * @param length the length of the range to be mapped.
         * @param accessBits the flags indicating the desired access.
         * @since 15.06.23
         */
        public MapPersistQuery(final long length, final GLBufferAccess... accessBits) {

            this(length, accessBits, 0, accessBits.length);
        }

        /**
         * Constructs a new MapQuery.
         *
         * @param length the length of the range to be mapped.
         * @param accessBits the flags indicating the desired access to the
         * range.
         * @param accessOffset the offset to start reading from the access
         * flags.
         * @param accessLength the number of access flags to read.
         * @since 15.05.13
         */
        public MapPersistQuery(final long length,
                final GLBufferAccess[] accessBits, final int accessOffset, final int accessLength) {

            this.length = length;

            int bitVal = 0;
            for (int i = accessOffset; i < accessOffset + accessLength; i++) {
                bitVal |= accessBits[i].value;
            }

            bitVal |= GLBufferAccess.GL_MAP_PERSISTENT.value;
            bitVal |= GLBufferAccess.GL_MAP_COHERENT.value;

            this.access = bitVal;
        }

        @Override
        public ByteBuffer call() throws Exception {
            if (!GLBuffer.this.isValid()) {
                throw new GLException("Invalid GLBuffer!");
            }

            final ByteBuffer oldBuffer = GLBuffer.this.mappedBuffer;

            GLTools.getDSAInstance().glNamedBufferStorage(GLBuffer.this.bufferId, this.length, this.access);

            final ByteBuffer newBuffer = GLTools.getDSAInstance()
                    .glMapNamedBufferRange(
                            GLBuffer.this.bufferId,
                            0, this.length,
                            this.access, oldBuffer);

            GLBuffer.this.mappedBuffer = newBuffer;

            return newBuffer;
        }
        
        @Override
        protected ByteBuffer handleInterruption() {
            return ByteBuffer.allocate(0).asReadOnlyBuffer();
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
        @Override
        public void run() {
            if (!GLBuffer.this.isValid()) {
                throw new GLException("Invalid GLBuffer!");
            }

            GLTools.getDSAInstance().glUnmapNamedBuffer(GLBuffer.this.bufferId);
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

            GLTools.getDSAInstance().glCopyNamedBufferSubData(
                    src.bufferId, dest.bufferId,
                    srcOffset, destOffset, size);
        }
    }
}
