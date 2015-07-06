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
import org.lwjgl.opengl.GL30;
import org.lwjgl.opengl.GL31;
import org.lwjgl.opengl.GL44;

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
            
            assert GL11.glGetError() == GL11.GL_NO_ERROR : String.format("glBindBuffer(%s, %d) failed!",
                    this.target, GLBuffer.this.bufferId);
            
            final int rVal = GL15.glGetBufferParameteri(this.target.value, this.pName.value);
            
            assert GL11.glGetError() == GL11.GL_NO_ERROR : String.format("glGetBufferParameteri(%s, %s) failed!",
                    this.target, this.pName);
            
            return rVal;
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
                
                assert GL11.glGetError() == GL11.GL_NO_ERROR : String.format("glDeleteBuffers(%d) failed!", GLBuffer.this.bufferId);
                
                GLBuffer.this.bufferId = INVALID_BUFFER_ID;
            }
        }
    }

    private UploadTask lastUploadTask = null;

    /**
     * Uploads the supplied data to the GLBuffer. GL_ARRAY_BUFFER is used for
     * the target and GL_STATIC_DRAW is used for the usage.
     *
     * @param data the date to upload.
     * @since 15.06.05
     */
    public void upload(final ByteBuffer data) {
        this.upload(GLBufferTarget.GL_ARRAY_BUFFER, data, GLBufferUsage.GL_STATIC_DRAW);        
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
            
            assert GL11.glGetError() == GL11.GL_NO_ERROR : String.format("glBindBuffer(%s, %d) failed!",
                    this.target, GLBuffer.this.bufferId);
            
            GL15.glBufferData(this.target.value, this.data, this.usage.value);
            
            assert GL11.glGetError() == GL11.GL_NO_ERROR : String.format("glBufferData(%s, [data], %s) failed!",
                    this.target, this.usage);
        }
    }

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
        this.allocate(target, size, GLBufferUsage.GL_DYNAMIC_DRAW);
    }

    public void allocate(final GLBufferTarget target, final long size, final GLBufferUsage usage) {
        new AllocateTask(target, size, usage).glRun(this.getThread());
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
            
            assert GL11.glGetError() == GL11.GL_NO_ERROR : String.format("glBindBuffer(%s, %d) failed!",
                    this.target, GLBuffer.this.bufferId);
            
            GL15.glBufferData(this.target.value, this.size, this.usage.value);
            
            assert GL11.glGetError() == GL11.GL_NO_ERROR : String.format("glBufferData(%s, %d, %s) failed!",
                    this.target, this.size, this.usage);
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
            
            assert GL11.glGetError() == GL11.GL_NO_ERROR: String.format("glBindBuffer(%s, %d) failed!",
                    this.target, bufferId);
            

            if (this.writeBuffer == null) {
                final int size = GL15.glGetBufferParameteri(
                        this.target.value,
                        GLBufferParameterName.GL_BUFFER_SIZE.value);
                
                assert GL11.glGetError() == GL11.GL_NO_ERROR : String.format("glGetBufferParameteri(%s, GL_BUFFER_SIZE) = %d failed!",
                        this.target, size);

                out = ByteBuffer.allocateDirect(size)
                        .order(ByteOrder.nativeOrder());

            } else {
                out = this.writeBuffer;
            }

            GL15.glGetBufferSubData(
                    this.target.value,
                    this.offset,
                    out);
            
            assert GL11.glGetError() == GL11.GL_NO_ERROR : String.format("glGetBufferSubData(%s, %d, [data]) failed!",
                    this.target.value, this.offset);

            return out;
        }
    }

    /**
     * Maps the GLBuffer to a ByteBuffer for read-write access. This function
     * can force a thread sync.
     *
     * @param target the target to bind the GLBuffer to.
     * @param offset the offset in bytes to begin the map.
     * @param length the number of bytes to map.
     * @return the mapped ByteBuffer.
     * @see
     * <a href="https://www.opengl.org/wiki/GLAPI/glMapBufferRange#Function_Definition">glMapBufferRange</a>
     * @since 15.05.13
     */
    public ByteBuffer map(final GLBufferTarget target, final long offset, final long length) {
        return this.map(target, offset, length, GLBufferAccess.GL_MAP_READ, GLBufferAccess.GL_MAP_WRITE);
    }

    /**
     * Maps a GLBuffer to a ByteBuffer. This function can force a thread sync.
     *
     * @param target the target to bind the GLBuffer to.
     * @param offset the offset in bytes to begin the map.
     * @param length the number of bytes to map.
     * @param accessBits the flags indicating access rights to the data.
     * @return the mapped ByteBuffer
     * @see
     * <a href="https://www.opengl.org/wiki/GLAPI/glMapBufferRange#Function_Definition">glMapBufferRange</a>
     * @since 15.06.23
     */
    public ByteBuffer map(
            final GLBufferTarget target, final long offset, final long length,
            final GLBufferAccess... accessBits) {

        return this.map(target, offset, length, accessBits, 0, accessBits.length);
    }

    /**
     * Maps a GLBuffer to a ByteBuffer. This function can force a thread sync.
     *
     * @param target the target to bind the GLBuffer to.
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
    public ByteBuffer map(
            final GLBufferTarget target, final long offset, final long length,
            final GLBufferAccess[] accessBits, final int accessOffset, final int accessLength) {

        return new MapQuery(
                target, offset, length,
                accessBits, accessOffset, accessLength).glCall(this.getThread());
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

        final GLBufferTarget target;
        final int access;
        final long offset;
        final long length;

        /**
         * Constructs a new MapQuery using a sequence of accessbits.
         *
         * @param target the target to bind the GLBuffer to.
         * @param offset the starting offset within the buffer range to be
         * mapped.
         * @param length the length of the range to be mapped.
         * @param accessBits the flags indicating the desired access.
         * @since 15.06.23
         */
        public MapQuery(final GLBufferTarget target,
                final long offset, final long length,
                final GLBufferAccess... accessBits) {

            this(target, offset, length, accessBits, 0, accessBits.length);
        }

        /**
         * Constructs a new MapQuery.
         *
         * @param target the target to bind to
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
                final GLBufferTarget target,
                final long offset, final long length,
                final GLBufferAccess[] accessBits, final int accessOffset, final int accessLength) {

            this.target = target;
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

            GL15.glBindBuffer(this.target.value, GLBuffer.this.bufferId);
            
            assert GL11.glGetError() == GL11.GL_NO_ERROR : String.format("glBindBuffer(%s, %d) failed!",
                    this.target, GLBuffer.this.bufferId);
            
            final ByteBuffer newBuffer = GL30.glMapBufferRange(
                    this.target.value,
                    this.offset, this.length,
                    this.access,
                    oldBuffer);
            
            assert GL11.glGetError() == GL11.GL_NO_ERROR: String.format("glMapBufferRange(%s, %d, %d, %d, [data]) failed!",
                    this.target, this.offset, this.length, this.access);

            GLBuffer.this.mappedBuffer = newBuffer;

            return newBuffer;
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
     * @param target the target to bind the GLBuffer to.
     * @param length the number of bytes to map.
     * @return the mapped ByteBuffer.
     * @see
     * <a href="https://www.opengl.org/wiki/GLAPI/glMapBufferRange#Function_Definition">glMapBufferRange</a>
     * @since 15.05.13
     */
    public ByteBuffer mapPersist(final GLBufferTarget target, final long length) {
        return this.mapPersist(target, length, GLBufferAccess.GL_MAP_READ, GLBufferAccess.GL_MAP_WRITE);
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
     * @param target the target to bind the GLBuffer to.
     * @param length the number of bytes to map.
     * @param accessBits the flags indicating the desired access.
     * @return 
     */
    public ByteBuffer mapPersist(
            final GLBufferTarget target, final long length,
            final GLBufferAccess... accessBits) {

        return this.mapPersist(target, length, accessBits, 0, accessBits.length);
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
     * @param target the target to bind the GLBuffer to.
     * @param length the number of bytes to map.
     * @param accessBits the flags indicating the desired access.
     * @param accessOffset the offset to start reading from the access
     * flags.
     * @param accessLength the number of access flags to read.
     * @return 
     */
    public ByteBuffer mapPersist(
            final GLBufferTarget target, final long length,
            final GLBufferAccess[] accessBits, final int accessOffset, final int accessLength) {

        return new MapPersistQuery(
                target, length,
                accessBits, accessOffset, accessLength).glCall(this.getThread());
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
        final GLBufferTarget target;
        final int access;
        final long length;

        /**
         * Constructs a new MapQuery using a sequence of accessbits.
         *
         * @param target the target to bind the GLBuffer to.
         * @param length the length of the range to be mapped.
         * @param accessBits the flags indicating the desired access.
         * @since 15.06.23
         */
        public MapPersistQuery(final GLBufferTarget target, final long length,
                final GLBufferAccess... accessBits) {

            this(target, length, accessBits, 0, accessBits.length);
        }

        /**
         * Constructs a new MapQuery.
         *
         * @param target the target to bind to
         * @param length the length of the range to be mapped.
         * @param accessBits the flags indicating the desired access to the
         * range.
         * @param accessOffset the offset to start reading from the access
         * flags.
         * @param accessLength the number of access flags to read.
         * @since 15.05.13
         */
        public MapPersistQuery(
                final GLBufferTarget target, final long length,
                final GLBufferAccess[] accessBits, final int accessOffset, final int accessLength) {

            this.target = target;
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

            GL15.glBindBuffer(this.target.value, GLBuffer.this.bufferId);
            
            assert GL11.glGetError() == GL11.GL_NO_ERROR : String.format("glBindBuffer(%s, %d) failed!",
                    this.target, GLBuffer.this.bufferId);
            
            GL44.glBufferStorage(this.target.value, this.length, this.access);
            
            assert GL11.glGetError() == GL11.GL_NO_ERROR : String.format("glBufferStorage(%s, %d, %d) failed!",
                    this.target, this.length, this.access);
            
            final ByteBuffer newBuffer = GL30.glMapBufferRange(
                    this.target.value,
                    0, this.length,
                    this.access,
                    oldBuffer);
            
            assert GL11.glGetError() == GL11.GL_NO_ERROR : String.format("glMapBufferRange(%s, 0, %d, %d, [data]) failed!",
                    this.target, this.length, this.access);

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
            
            assert GL11.glGetError() == GL11.GL_NO_ERROR : String.format("glBindBuffer(%s, %d) failed!",
                    this.target, bufferId);
            
            GL15.glUnmapBuffer(this.target.value);
            
            assert GL11.glGetError() == GL11.GL_NO_ERROR : String.format("glUnmapBuffer(%s) failed!", this.target);
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

            assert GL11.glGetError() == GL11.GL_NO_ERROR : String.format("glBindBuffer(GL_COPY_READ_BUFFER, %d) failed!",
                    this.src.bufferId);
            
            GL15.glBindBuffer(
                    GLBufferTarget.GL_COPY_WRITE_BUFFER.value,
                    this.dest.bufferId);
            
            assert GL11.glGetError() == GL11.GL_NO_ERROR: String.format("glBindBuffer(GL_COPY_WRITE_BUFFER, %d) failed!",
                    this.dest.bufferId);

            GL31.glCopyBufferSubData(
                    GLBufferTarget.GL_COPY_READ_BUFFER.value,
                    GLBufferTarget.GL_COPY_WRITE_BUFFER.value,
                    this.srcOffset,
                    this.destOffset,
                    this.size);
            
            assert GL11.glGetError() == GL11.GL_NO_ERROR : String.format("glCopyBufferSubData(GL_COPY_READ_BUFFER, GL_COPY_WRITE_BUFFER, %d, %d, %d) failed!",
                    this.srcOffset, this.destOffset, this.size);
        }
    }
}
