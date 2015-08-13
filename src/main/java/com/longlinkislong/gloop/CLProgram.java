/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.longlinkislong.gloop;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.util.Objects;
import org.lwjgl.opencl.CL10;
import org.lwjgl.opencl.CLUtil;

/**
 *
 * @author root
 */
public class CLProgram extends CLObject {

    private static final long INVALID_PROGRAM_ID = -1;
    private static final long INVALID_KERNEL_ID = -1;
    private long programId = INVALID_PROGRAM_ID;

    public CLProgram() {
        super();
    }

    public CLProgram(final CLThread thread) {
        super(thread);
    }

    public boolean isValid() {
        return this.programId != INVALID_PROGRAM_ID;
    }

    public void compile(final CharSequence src) {
        new CompileTask(src).clRun(this.getThread());
    }

    public class CompileTask extends CLTask {

        final String source;

        public CompileTask(final CharSequence src) {
            this.source = src.toString();
        }

        @Override
        public void run() {
            if (CLProgram.this.isValid()) {
                throw new CLException("CLProgram has already been constructed!");
            }

            final CLThread thread = CLThread.getCurrent().orElseThrow(CLException::new);
            final long pId = CL10.clCreateProgramWithSource(thread.context, this.source, null);

            CLUtil.checkCLError(CL10.clBuildProgram(pId, thread.device, "", null, 0L));
            CLProgram.this.programId = pId;
        }
    }

    public void delete() {
        new DeleteTask().clRun(this.getThread());
    }

    public class DeleteTask extends CLTask {

        @Override
        public void run() {
            if (!CLProgram.this.isValid()) {
                throw new CLException("CLProgram must be valid before calling delete!");
            }

            CL10.clReleaseProgram(CLProgram.this.programId);
            CLProgram.this.programId = INVALID_PROGRAM_ID;
        }
    }

    public class Kernel extends CLObject {

        private final long[] kId;
        private final String entry;
        private final int dimensions;
        private final long[] gWorkSize;
        private final long[] lWorkSize;

        private Kernel(final long[] kId, final String entry, final int dimensions, final long[] gWorkSize, final long[] lWorkSize) {
            super(CLProgram.this.getThread());

            this.kId = kId;
            this.entry = entry;
            this.dimensions = dimensions;
            this.gWorkSize = new long[dimensions];
            this.lWorkSize = lWorkSize;

            System.arraycopy(gWorkSize, 0, this.gWorkSize, 0, dimensions);
        }

        public Kernel(final CharSequence entry, final int dim, final long[] gWorkSize) {
            this(new long[]{INVALID_KERNEL_ID}, entry.toString(), dim, gWorkSize, null);
            
            new InitTask().clRun(this.getThread());
        }

        public Kernel withLocalWorkSize(final long[] lWorkSize) {
            return new Kernel(this.kId, this.entry, this.dimensions, this.gWorkSize, lWorkSize);
        }

        public Kernel withGlobalWorkSize(final long[] gWorkSize) {
            return new Kernel(this.kId, this.entry, this.dimensions, gWorkSize, this.lWorkSize);
        }

        public boolean isValid() {
            return this.kId[0] != INVALID_KERNEL_ID;
        }

        public void delete() {
            new DeleteTask().clRun(this.getThread());
        }

        public class DeleteTask extends CLTask {

            @Override
            public void run() {
                if (!Kernel.this.isValid()) {
                    throw new CLException("Kernel needs to exist before it can be deleted!");
                }

                CL10.clReleaseKernel(Kernel.this.kId[0]);
                Kernel.this.kId[0] = INVALID_KERNEL_ID;
            }
        }

        private class InitTask extends CLTask {

            @Override
            public void run() {
                if(!CLProgram.this.isValid()) {
                    throw new CLException("CLProgram is not valid!");
                }
                if (Kernel.this.isValid()) {
                    throw new CLException("Kernel has already been initialized!");
                }

                Kernel.this.kId[0] = CL10.clCreateKernel(CLProgram.this.programId, Kernel.this.entry, null);
            }
        }

        public void setArgument(final int argId, final CLBuffer buffer) {
            new SetArgumentTask(argId, buffer).clRun(this.getThread());
        }

        public class SetArgumentTask extends CLTask {

            private final int argId;
            private final CLBuffer buffer;

            public SetArgumentTask(final int argId, final CLBuffer buffer) {
                this.argId = argId;
                this.buffer = Objects.requireNonNull(buffer);
            }

            @Override
            public void run() {
                if (!Kernel.this.isValid()) {
                    throw new CLException("Invalid Kernel!");
                } else if (!this.buffer.isValid()) {
                    throw new CLException("Invalid CLBuffer!");
                }

                CL10.clSetKernelArg1p(Kernel.this.kId[0], this.argId, this.buffer.bufferId);
            }
        }

        public void execute() {
            new ExecuteTask().clRun(this.getThread());
        }

        public class ExecuteTask extends CLTask {

            private final ByteBuffer gWorkSize;
            private final ByteBuffer lWorkSize;

            public ExecuteTask() {
                this.gWorkSize = ByteBuffer.allocateDirect(Long.BYTES * Kernel.this.dimensions).order(ByteOrder.nativeOrder());
                
                for (int i = 0; i < Kernel.this.dimensions; i++) {
                    this.gWorkSize.putLong(Kernel.this.gWorkSize[i]);
                }
                
                this.gWorkSize.flip();

                if (Kernel.this.lWorkSize != null) {
                    this.lWorkSize = ByteBuffer.allocateDirect(Long.BYTES * Kernel.this.dimensions).order(ByteOrder.nativeOrder());

                    for (int i = 0; i < Kernel.this.dimensions; i++) {
                        this.lWorkSize.putLong(Kernel.this.lWorkSize[i]);
                    }
                    
                    this.lWorkSize.flip();
                } else {
                    this.lWorkSize = null;
                }
            }

            @Override
            public void run() {
                if (!Kernel.this.isValid()) {
                    throw new CLException("Invalid Kernel!");
                }

                final CLThread thread = CLThread.getCurrent().orElseThrow(CLException::new);
                  
                
                CL10.clEnqueueNDRangeKernel(
                        thread.commandQueue,
                        Kernel.this.kId[0],
                        Kernel.this.dimensions,
                        null,
                        this.gWorkSize, this.lWorkSize,
                        0, null,
                        null);                
            }
        }
    }
}
