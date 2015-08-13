/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.longlinkislong.gloop;

import java.util.Objects;
import org.lwjgl.opencl.CL10GL;

public class CLGLBuffer extends CLBuffer{
    public CLGLBuffer() {
        super();
    }
    
    public CLGLBuffer(final CLThread thread) {
        super(thread);
    }
    
    public class CreateTask extends GLTask {
        final GLBuffer buffer;
        final long flags;
        
        public CreateTask(final GLBuffer buffer, final long flags) {
            this.buffer = Objects.requireNonNull(buffer);
            this.flags = flags;
            
        }
        
        @Override
        public void run() {
            if(CLGLBuffer.this.isValid()) {
                throw new CLGLException("CLGLBuffer already exists!");
            } else if(!this.buffer.isValid()) {
                throw new CLGLException("GLBuffer is not valid!");
            }
            
            final CLThread thread = CLThread.getCurrent().orElseThrow(CLGLException::new);
            
            CLGLBuffer.this.bufferId = CL10GL.clCreateFromGLBuffer(thread.context, this.flags, buffer.bufferId, thread.errcode_ret);                        
        }
    }
    
    public void acquire(){
        new AcquireTask().clRun(this.getThread());
    }
    
    public class AcquireTask extends CLTask {

        @Override
        public void run() {
            if(!CLGLBuffer.this.isValid()) {
                throw new CLGLException("CLGLBuffer is not valid!");
            }
            
            final CLThread thread = CLThread.getCurrent().orElseThrow(CLGLException::new);
            
            CL10GL.clEnqueueAcquireGLObjects(thread.commandQueue, CLGLBuffer.this.bufferId, null, null);
        }        
    }
    
    public void release() {
        new ReleaseTask().clRun(this.getThread());
    }
    
    public class ReleaseTask extends CLTask {
        @Override
        public void run() {
            if(!CLGLBuffer.this.isValid()) {
                throw new CLGLException("CLGLBuffer is not valid!");
            }
            
            final CLThread thread = CLThread.getCurrent().orElseThrow(CLGLException::new);
            
            CL10GL.clEnqueueReleaseGLObjects(thread.commandQueue, CLGLBuffer.this.bufferId, null, null);
        }
    }
}
