/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.longlinkislong.gloop;

import java.nio.ByteBuffer;
import java.util.Objects;
import org.lwjgl.opencl.CL10;

/**
 *
 * @author zmichaels
 */
public class CLBuffer extends CLObject {
    
    private static final long INVALID_BUFFER_ID = -1;
    long bufferId = INVALID_BUFFER_ID;
    
    public CLBuffer() {
        super();
    }
    
    public CLBuffer(final CLThread thread) {
        super(thread);
    }
    
    public boolean isValid() {
        return this.bufferId != INVALID_BUFFER_ID;
    }
    
    public CLBuffer allocate(final long size, final long flags) {
        new AllocateTask(size, flags).clRun(this.getThread());
        return this;
    }
    
    public class AllocateTask extends CLTask {
        
        final long size;
        final long flags;
        
        public AllocateTask(final long size, final long flags) {
            this.size = size;
            this.flags = flags;
        }
        
        @Override
        public void run() {
            if (CLBuffer.this.isValid()) {
                throw new CLException("CLBuffer is already initialized!");
            }
            
            final CLThread thread = CLThread.getCurrent().orElseThrow(CLException::new);
            
            CLBuffer.this.bufferId = CL10.clCreateBuffer(
                    thread.context,
                    this.flags,
                    this.size,
                    thread.errcode_ret);
        }
    }
    
    public void upload(final ByteBuffer data, final long writeOffset, final boolean blockingWrite) {
        new UploadTask(data, writeOffset, blockingWrite).clRun(this.getThread());
    }
    
    public class UploadTask extends CLTask {
        
        final ByteBuffer data;
        final boolean blockingWrite;
        final long offset;
        
        public UploadTask(final ByteBuffer data, final long offset, final boolean blockingWrite) {
            this.data = Objects.requireNonNull(data);
            this.blockingWrite = blockingWrite;
            this.offset = offset;
        }
        
        @Override
        public void run() {
            if (!CLBuffer.this.isValid()) {
                throw new CLException("CLBuffer is invalid!");
            }
            
            final CLThread thread = CLThread.getCurrent().orElseThrow(CLException::new);
            final int block = this.blockingWrite ? 1 : 0;
            
            CL10.clEnqueueWriteBuffer(thread.commandQueue, CLBuffer.this.bufferId, block, this.offset, this.data, null, null);
        }
    }
    
    public ByteBuffer download(final ByteBuffer data, final long readOffset, final boolean blockingRead) {
        return new DownloadQuery(data, readOffset, blockingRead).clCall(this.getThread());
    }
    
    public class DownloadQuery extends CLQuery<ByteBuffer> {

        final ByteBuffer data;
        final boolean blockingRead;
        final long offset;
        
        public DownloadQuery(final ByteBuffer data, final long offset, final boolean blockingRead) {
            this.data = Objects.requireNonNull(data);
            this.blockingRead = blockingRead;
            this.offset = offset;
        }
        
        @Override
        public ByteBuffer call() throws Exception {
            if (!CLBuffer.this.isValid()) {
                throw new CLException("CLBuffer is invalid!");
            }
            
            final CLThread thread = CLThread.getCurrent().orElseThrow(CLException::new);
            final int block = this.blockingRead ? 1 : 0;
            
            CL10.clEnqueueReadBuffer(thread.commandQueue, CLBuffer.this.bufferId, block, this.offset, this.data, null, null);
            
            return this.data;
        }
    }
    
    public void delete() {
        new DeleteTask().clRun(this.getThread());        
    }

    public class DeleteTask extends CLTask {

        @Override
        public void run() {
            if (!CLBuffer.this.isValid()) {
                throw new CLException("CLBuffer must be valid to call delete!");
            }
            
            CL10.clReleaseMemObject(CLBuffer.this.bufferId);
            CLBuffer.this.bufferId = INVALID_BUFFER_ID;
        }
    }
}
