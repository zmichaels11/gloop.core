/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.longlinkislong.gloop;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import org.lwjgl.opengl.GL15;
import org.lwjgl.opengl.GL31;

/**
 *
 * @author zmichaels
 */
public class GLBuffer {

    private static final int INVALID_BUFFER_ID = -1;
    protected int bufferId = INVALID_BUFFER_ID;
    private ByteBuffer mappedBuffer = null;           

    @Override
    public String toString() {
        return "GLBuffer: " + this.bufferId;
    }

    public boolean isValid() {
        return this.bufferId != INVALID_BUFFER_ID;
    }    

    public class InitTask extends GLTask {

        @Override
        public void run() {
            if (!GLBuffer.this.isValid()) {
                GLBuffer.this.bufferId = GL15.glGenBuffers();
            }
            org.lwjgl.opengl.Util.checkGLError();
        }
    }

    public class ParameterQuery extends GLQuery<Integer> {

        final GLBufferTarget target;
        final GLBufferParameterName pName;

        public ParameterQuery(
                final GLBufferTarget target,
                final GLBufferParameterName pName) {

            this.target = target;
            this.pName = pName;
        }

        @Override
        public Integer call() throws Exception {
            GL15.glBindBuffer(this.target.value, bufferId);
            return GL15.glGetBufferParameteri(this.target.value, this.pName.value);
        }

    }

    public class DeleteTask extends GLTask {

        @Override
        public void run() {
            if (GLBuffer.this.isValid()) {
                GL15.glDeleteBuffers(GLBuffer.this.bufferId);
                GLBuffer.this.bufferId = INVALID_BUFFER_ID;
            }
        }
    }

    public class UploadTask extends GLTask {

        final GLBufferTarget target;
        final GLBufferUsage usage;
        final ByteBuffer data;

        public UploadTask(final GLBufferTarget target, final ByteBuffer data) {
            this(target, data, GLBufferUsage.GL_STATIC_DRAW);
        }

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
            GL15.glBindBuffer(this.target.value, GLBuffer.this.bufferId);
            GL15.glBufferData(this.target.value, this.data, this.usage.value);
            org.lwjgl.opengl.Util.checkGLError();
        }
    }

    public class DownloadQuery extends GLQuery<ByteBuffer> {

        final GLBufferTarget target;
        final long offset;
        final ByteBuffer writeBuffer;

        public DownloadQuery() {
            this(GLBufferTarget.GL_ARRAY_BUFFER, 0);
        }

        public DownloadQuery(final GLBufferTarget target) {
            this(target, 0);
        }

        public DownloadQuery(
                final GLBufferTarget target,
                final long offset) {

            this.target = target;
            this.offset = offset;
            this.writeBuffer = null;
        }

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

    public class MapQuery extends GLQuery<ByteBuffer> {

        final GLBufferTarget target;
        final GLBufferAccess access;

        public MapQuery() {
            this(GLBufferTarget.GL_ARRAY_BUFFER, GLBufferAccess.GL_READ_WRITE);
        }

        public MapQuery(final GLBufferTarget target) {
            this(target, GLBufferAccess.GL_READ_WRITE);
        }

        public MapQuery(
                final GLBufferTarget target, final GLBufferAccess access) {

            this.target = target;
            this.access = access;
        }

        @Override
        public ByteBuffer call() throws Exception {
            final ByteBuffer oldBuffer = GLBuffer.this.mappedBuffer;

            GL15.glBindBuffer(this.target.value, bufferId);
            final ByteBuffer newBuffer = GL15.glMapBuffer(
                    this.target.value, bufferId, oldBuffer);

            GLBuffer.this.mappedBuffer = newBuffer;

            return newBuffer;
        }
    }

    public class UnmapTask extends GLTask {

        final GLBufferTarget target;

        public UnmapTask() {
            this(GLBufferTarget.GL_ARRAY_BUFFER);
        }

        public UnmapTask(final GLBufferTarget target) {
            this.target = target;
        }

        @Override
        public void run() {
            GL15.glBindBuffer(this.target.value, bufferId);
            GL15.glUnmapBuffer(this.target.value);
        }

    }

    public static class CopyTask extends GLTask {

        final GLBuffer src;
        final long srcOffset;
        final GLBuffer dest;
        final long destOffset;
        final long size;

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
            GL15.glBindBuffer(GLBufferTarget.GL_COPY_READ_BUFFER.value, this.src.bufferId);
            GL15.glBindBuffer(GLBufferTarget.GL_COPY_WRITE_BUFFER.value, this.dest.bufferId);
            GL15.glBufferData(
                    GLBufferTarget.GL_COPY_WRITE_BUFFER.value, size, GLBufferUsage.GL_STATIC_DRAW.value);
            GL31.glCopyBufferSubData(GLBufferTarget.GL_COPY_READ_BUFFER.value,
                    GLBufferTarget.GL_COPY_WRITE_BUFFER.value,
                    this.srcOffset,
                    this.destOffset,
                    this.size);                        
        }
    }
}
