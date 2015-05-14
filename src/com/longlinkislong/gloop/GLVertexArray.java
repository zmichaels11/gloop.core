/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.longlinkislong.gloop;

import java.nio.IntBuffer;
import java.util.Objects;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL14;
import org.lwjgl.opengl.GL15;
import org.lwjgl.opengl.GL20;
import org.lwjgl.opengl.GL30;
import org.lwjgl.opengl.GL31;
import org.lwjgl.opengl.GL33;
import org.lwjgl.opengl.GL40;
import org.lwjgl.opengl.GL41;

/**
 *
 * @author zmichaels
 */
public class GLVertexArray extends GLObject {

    private static GLVertexArray CURRENT_VAO = null;
    private static final int INVALID_VERTEX_ARRAY_ID = -1;
    private int vaoId = INVALID_VERTEX_ARRAY_ID;

    public GLVertexArray() {
        super();
        this.init();
    }

    public GLVertexArray(final GLThread thread) {
        super(thread);
        this.init();
    }

    public boolean isValid() {
        return this.vaoId != INVALID_VERTEX_ARRAY_ID;
    }

    private void bind() {
        if (CURRENT_VAO != this) {
            GL30.glBindVertexArray(this.vaoId);
            CURRENT_VAO = this;
        }
    }

    private final InitTask initTask = new InitTask();

    public final void init() {
        this.initTask.glRun(this.getThread());
    }

    public class InitTask extends GLTask {

        @Override
        public void run() {
            if (!GLVertexArray.this.isValid()) {
                GLVertexArray.this.vaoId = GL30.glGenVertexArrays();
            }
        }
    }

    private DrawElementsIndirectTask lastDrawElementsIndirect = null;

    public void drawElementsIndirect(
            final GLProgram program,
            final GLDrawMode drawMode,
            final GLIndexElementType indexType,
            final GLBuffer indirectCommandBuffer,
            final long offset) {

        if (this.lastDrawElementsIndirect != null
                && this.lastDrawElementsIndirect.program == program
                && this.lastDrawElementsIndirect.indirectCommandBuffer == indirectCommandBuffer
                && this.lastDrawElementsIndirect.drawMode == drawMode
                && this.lastDrawElementsIndirect.indexType == indexType
                && this.lastDrawElementsIndirect.offset == offset) {

            this.lastDrawElementsIndirect.glRun(this.getThread());
        } else {
            this.lastDrawElementsIndirect = new DrawElementsIndirectTask(
                    program,
                    drawMode,
                    indexType,
                    indirectCommandBuffer,
                    offset);

            this.lastDrawElementsIndirect.glRun(this.getThread());
        }
    }

    public class DrawElementsIndirectTask extends GLTask {

        private final GLBuffer indirectCommandBuffer;
        private final GLProgram program;
        private final GLDrawMode drawMode;
        private final GLIndexElementType indexType;
        private final long offset;

        public DrawElementsIndirectTask(
                final GLProgram program,
                final GLDrawMode mode, final GLIndexElementType indexType,
                final GLBuffer indirectCommandBuffer) {

            this(program, mode, indexType, indirectCommandBuffer, 0);
        }

        public DrawElementsIndirectTask(
                final GLProgram program,
                final GLDrawMode mode, final GLIndexElementType indexType,
                final GLBuffer indirectCommandBuffer,
                final long offset) {

            Objects.requireNonNull(this.program = program);
            Objects.requireNonNull(this.indexType = indexType);
            Objects.requireNonNull(this.drawMode = mode);
            Objects.requireNonNull(this.indirectCommandBuffer = indirectCommandBuffer);

            if ((this.offset = offset) < 0) {
                throw new GLException("Offset value cannot be less than 0!");
            }
        }

        @Override
        public void run() {
            if (!GLVertexArray.this.isValid()) {
                throw new GLException("Invalid GLVertexArray!");
            } else if (!this.indirectCommandBuffer.isValid()) {
                throw new GLException("Invalid GLBuffer!");
            }

            this.program.bind();
            GLVertexArray.this.bind();

            GL15.glBindBuffer(
                    GLBufferTarget.GL_DRAW_INDIRECT_BUFFER.value,
                    this.indirectCommandBuffer.bufferId);
            GL40.glDrawElementsIndirect(
                    this.drawMode.value,
                    this.indexType.value,
                    this.offset);
        }
    }

    private DrawArraysIndirectTask lastDrawArraysIndirect = null;

    public void drawArraysIndirect(
            final GLProgram program,
            final GLDrawMode drawMode,
            final GLBuffer indirectCommandBuffer,
            final long offset) {

        if (this.lastDrawArraysIndirect != null
                && this.lastDrawArraysIndirect.indirectCommandBuffer == indirectCommandBuffer
                && this.lastDrawArraysIndirect.program == program
                && this.lastDrawArraysIndirect.drawMode == drawMode
                && this.lastDrawArraysIndirect.offset == offset) {

            this.lastDrawArraysIndirect.glRun(this.getThread());
        }
    }

    public class DrawArraysIndirectTask extends GLTask {

        private final GLBuffer indirectCommandBuffer;
        private final GLProgram program;
        private final GLDrawMode drawMode;
        private final long offset;

        public DrawArraysIndirectTask(
                final GLProgram program,
                final GLDrawMode drawMode,
                final GLBuffer indirectCommandBuffer,
                final long offset) {

            Objects.requireNonNull(this.indirectCommandBuffer = indirectCommandBuffer);
            Objects.requireNonNull(this.drawMode = drawMode);
            Objects.requireNonNull(this.program = program);

            if ((this.offset = offset) < 0) {
                throw new GLException("Offset cannot be less than 0!");
            }
        }

        @Override
        public void run() {
            if (!GLVertexArray.this.isValid()) {
                throw new GLException("Invalid GLVertexArray!");
            } else if (!this.indirectCommandBuffer.isValid()) {
                throw new GLException("Invalid GLBuffer!");
            }

            this.program.bind();
            GLVertexArray.this.bind();

            GL15.glBindBuffer(
                    GLBufferTarget.GL_DRAW_INDIRECT_BUFFER.value,
                    this.indirectCommandBuffer.bufferId);

            GL40.glDrawArraysIndirect(
                    this.drawMode.value,
                    this.offset);
        }
    }

    private MultiDrawArraysTask lastMultiDrawArrays = null;

    public void multiDrawArrays(
            final GLProgram program, final GLDrawMode drawMode,
            final IntBuffer first, final IntBuffer count) {

        if (this.lastMultiDrawArrays != null
                && this.lastMultiDrawArrays.program == program
                && this.lastMultiDrawArrays.first == first
                && this.lastMultiDrawArrays.count == count
                && this.lastMultiDrawArrays.drawMode == drawMode) {

            this.lastMultiDrawArrays.glRun(this.getThread());
        } else {
            this.lastMultiDrawArrays = new MultiDrawArraysTask(
                    program, drawMode, first, count);

            this.lastMultiDrawArrays.glRun(this.getThread());
        }
    }

    public class MultiDrawArraysTask extends GLTask {

        private final GLProgram program;
        private final IntBuffer first;
        private final IntBuffer count;
        private final GLDrawMode drawMode;

        public MultiDrawArraysTask(
                final GLProgram program,
                final GLDrawMode drawMode,
                final IntBuffer first, final IntBuffer count) {

            Objects.requireNonNull(this.program = program);
            Objects.requireNonNull(this.first = first);
            Objects.requireNonNull(this.count = count);
            Objects.requireNonNull(this.drawMode = drawMode);
        }

        @Override
        public void run() {
            if (!GLVertexArray.this.isValid()) {
                throw new GLException("Invalid GLVertexArray!");
            }

            this.program.bind();
            GLVertexArray.this.bind();
            GL14.glMultiDrawArrays(
                    this.drawMode.value,
                    this.first,
                    this.count);

        }
    }

    private DrawElementsInstancedTask lastDrawElementsInstanced = null;

    public void drawElementsInstanced(
            final GLProgram program, final GLDrawMode drawMode,
            final int count, final GLIndexElementType indexType,
            final long offset, final int instanceCount) {

        if (this.lastDrawElementsInstanced != null
                && this.lastDrawElementsInstanced.program == program
                && this.lastDrawElementsInstanced.drawMode == drawMode
                && this.lastDrawElementsInstanced.count == count
                && this.lastDrawElementsInstanced.type == indexType
                && this.lastDrawElementsInstanced.offset == offset
                && this.lastDrawElementsInstanced.instanceCount == instanceCount) {

            this.lastDrawElementsInstanced.glRun(this.getThread());
        } else {
            this.lastDrawElementsInstanced = new DrawElementsInstancedTask(
                    program, drawMode, count, indexType, offset, instanceCount);

            this.lastDrawElementsInstanced.glRun(this.getThread());
        }
    }

    public class DrawElementsInstancedTask extends GLTask {

        private final GLProgram program;
        private final int count;
        private GLIndexElementType type;
        private GLDrawMode drawMode;
        private final int instanceCount;
        private final long offset;

        public DrawElementsInstancedTask(
                final GLProgram program,
                final GLDrawMode drawMode,
                final int count,
                final GLIndexElementType indexType,
                final long offset,
                final int instanceCount) {

            Objects.requireNonNull(this.program = program);
            this.count = count;
            Objects.requireNonNull(this.type = indexType);
            Objects.requireNonNull(this.drawMode = drawMode);

            if ((this.offset = offset) < 0) {
                throw new GLException("Offset cannot be less than 0!");
            }

            if ((this.instanceCount = instanceCount) < 0) {
                throw new GLException("Instance count cannot be less than 0!");
            }
        }

        @Override
        public void run() {
            if (!GLVertexArray.this.isValid()) {
                throw new GLException("Invalid GLVertexArray!");
            }

            this.program.bind();
            GL31.glDrawElementsInstanced(
                    this.drawMode.value,
                    this.count,
                    this.type.value,
                    this.offset,
                    this.instanceCount);
        }

    }

    private DrawArraysInstancedTask lastDrawArraysInstanced = null;

    public void drawArraysInstanced(
            final GLProgram program, final GLDrawMode mode,
            final int first, final int count, final int instanceCount) {

        if (this.lastDrawArraysInstanced != null
                && this.lastDrawArraysInstanced.program == program
                && this.lastDrawArraysInstanced.mode == mode
                && this.lastDrawArraysInstanced.first == first
                && this.lastDrawArraysInstanced.count == count
                && this.lastDrawArraysInstanced.instanceCount == instanceCount) {

            this.lastDrawArraysInstanced.glRun(this.getThread());
        } else {
            this.lastDrawArraysInstanced = new DrawArraysInstancedTask(
                    program, mode,
                    first, count, instanceCount);

            this.lastDrawArraysInstanced.glRun(this.getThread());
        }
    }

    public class DrawArraysInstancedTask extends GLTask {

        private final GLProgram program;
        private final GLDrawMode mode;
        private final int first;
        private final int count;
        private final int instanceCount;

        public DrawArraysInstancedTask(
                final GLProgram program,
                final GLDrawMode mode,
                final int first,
                final int count,
                final int instanceCount) {

            Objects.requireNonNull(this.program = program);
            Objects.requireNonNull(this.mode = mode);

            this.count = count;

            if ((this.first = first) < 0) {
                throw new GLException("First cannot be less than 0!");
            }

            if ((this.instanceCount = instanceCount) < 0) {
                throw new GLException("Instance count cannot be less than 0!");
            }
        }

        @Override
        public void run() {
            if (!GLVertexArray.this.isValid()) {
                throw new GLException("GLVertexArray is not valid!");
            }

            this.program.bind();
            GLVertexArray.this.bind();

            GL31.glDrawArraysInstanced(
                    this.mode.value,
                    this.first,
                    this.count,
                    this.instanceCount);
        }
    }

    private DrawElementsTask lastDrawElements = null;

    public void drawElements(
            final GLProgram program, final GLDrawMode mode,
            final int count, final GLIndexElementType type, final long offset) {

        if (this.lastDrawElements != null
                && this.lastDrawElements.program == program
                && this.lastDrawElements.mode == mode
                && this.lastDrawElements.count == count
                && this.lastDrawElements.type == type
                && this.lastDrawElements.offset == offset) {

            this.lastDrawElements.glRun(this.getThread());
        } else {
            this.lastDrawElements = new DrawElementsTask(
                    program, mode,
                    count, type, offset);

            this.lastDrawElements.glRun(this.getThread());
        }
    }

    public class DrawElementsTask extends GLTask {

        private final GLProgram program;
        private final GLDrawMode mode;
        private final int count;
        private final GLIndexElementType type;
        private final long offset;

        public DrawElementsTask(
                final GLProgram program,
                final GLDrawMode mode,
                final int count,
                final GLIndexElementType type,
                final long offset) {

            Objects.requireNonNull(this.program = program);
            Objects.requireNonNull(this.mode = mode);
            Objects.requireNonNull(this.type = type);

            if ((this.count = count) < 0) {
                throw new GLException("Count cannot be less than 0!");
            } else if ((this.offset = offset) < 0) {
                throw new GLException("Offset cannot be less than 0!");
            }
        }

        @Override
        public void run() {
            if (!GLVertexArray.this.isValid()) {
                throw new GLException("Invalid GLVertex!");
            }

            this.program.bind();
            GLVertexArray.this.bind();

            GL11.glDrawElements(
                    this.mode.value,
                    this.count,
                    this.type.value,
                    this.offset);
        }
    }

    private DrawArraysTask lastDrawArrays = null;

    public void drawArrays(
            final GLProgram program, final GLDrawMode mode,
            final int start, final int count) {

        if (this.lastDrawArrays != null
                && this.lastDrawArrays.program == program
                && this.lastDrawArrays.mode == mode
                && this.lastDrawArrays.start == start
                && this.lastDrawArrays.count == count) {

            this.lastDrawArrays.glRun(this.getThread());
        } else {
            this.lastDrawArrays = new DrawArraysTask(program, mode, start, count);
            this.lastDrawArrays.glRun(this.getThread());
        }
    }

    public class DrawArraysTask extends GLTask {

        private final GLDrawMode mode;
        private final int start;
        private final int count;
        private final GLProgram program;

        public DrawArraysTask(
                final GLProgram program,
                final GLDrawMode mode,
                final int start, final int count) {

            Objects.requireNonNull(this.program = program);
            Objects.requireNonNull(this.mode = mode);

            this.count = count;

            if ((this.start = start) < 0) {
                throw new GLException("Start value cannot be less than 0!");
            }
        }

        @Override
        public void run() {
            if (!GLVertexArray.this.isValid()) {
                throw new GLException("GLVertexArray is not valid!");
            }

            this.program.bind();
            GLVertexArray.this.bind();

            GL11.glDrawArrays(this.mode.value, this.start, this.count);
        }

    }

    private final DeleteTask deleteTask = new DeleteTask();
    
    public void delete() {
        this.deleteTask.glRun(this.getThread());
    }
    
    public class DeleteTask extends GLTask {

        @Override
        public void run() {
            if (GLVertexArray.this.isValid()) {
                GL30.glDeleteVertexArrays(GLVertexArray.this.vaoId);
                GLVertexArray.this.vaoId = INVALID_VERTEX_ARRAY_ID;
            }
        }
    }
    
    public void setDivisor(final int index, final int divisor) {
        new SetDivisorTask(index, divisor).glRun(this.getThread());
    }
    
    public class SetDivisorTask extends GLTask {

        private final int index;
        private final int divisor;

        public SetDivisorTask(final int index, final int divisor) {
            if ((this.index = index) < 0) {
                throw new GLException("Invalid index value! Index cannot be less than 0.");
            }

            if ((this.divisor = divisor) < 0) {
                throw new GLException("Invalid divisor value! Divisor cannot be less than 0.");
            }
        }

        @Override
        public void run() {
            if (!GLVertexArray.this.isValid()) {
                throw new GLException("Invalid GLVertexArray!");
            }

            GLVertexArray.this.bind();
            GL33.glVertexAttribDivisor(this.index, this.divisor);
        }
    }

    public void attachIndexBuffer(final GLBuffer buffer) {
        new AttachIndexBufferTask(buffer).glRun(this.getThread());
    }
    
    public class AttachIndexBufferTask extends GLTask {

        private final GLBuffer buffer;

        public AttachIndexBufferTask(final GLBuffer buffer) {
            this.buffer = buffer;
        }

        @Override
        public void run() {
            if (!GLVertexArray.this.isValid()) {
                throw new GLException("Invalid GLVertexArray!");
            } else if (!this.buffer.isValid()) {
                throw new GLException("Invalid GLBuffer!");
            }

            GLVertexArray.this.bind();

            GL15.glBindBuffer(
                    GLBufferTarget.GL_ELEMENT_ARRAY_BUFFER.value,
                    this.buffer.bufferId);
        }
    }

    public void attachBuffer(
            final int index, final GLBuffer buffer, 
            final GLVertexAttributeType type, final GLVertexAttributeSize size) {
        
        new AttachBufferTask(index, buffer, type, size).glRun(this.getThread());
    }
    
    public class AttachBufferTask extends GLTask {

        private final int index;
        private final GLBuffer buffer;
        private final GLVertexAttributeType type;
        private final GLVertexAttributeSize size;
        private final int stride;
        private final long offset;
        private final boolean normalized;

        public AttachBufferTask(
                final int index,
                final GLBuffer buffer,
                final GLVertexAttributeType type,
                final GLVertexAttributeSize size) {

            this(index, buffer, type, size, false, 0, 0);

        }

        public AttachBufferTask(
                final int index,
                final GLBuffer buffer,
                final GLVertexAttributeType type,
                final GLVertexAttributeSize size,
                final boolean normalized) {

            this(index, buffer, type, size, normalized, 0, 0);
        }

        public AttachBufferTask(
                final int index,
                final GLBuffer buffer,
                final GLVertexAttributeType type,
                final GLVertexAttributeSize size,
                final boolean normalized,
                final int stride, final long offset) {

            if ((this.index = index) < 0) {
                throw new GLException("Invalid index value! Index cannot be less than 0.");
            }

            if (size != GLVertexAttributeSize.VEC4
                    && (type == GLVertexAttributeType.GL_INT_2_10_10_10_REV
                    || type == GLVertexAttributeType.GL_UNSIGNED_INT_10F_11F_11F_REV
                    || type == GLVertexAttributeType.GL_UNSIGNED_INT_2_10_10_10_REV)) {

                throw new GLException("GL_INT_2_10_10_10_REV, GL_UNSIGNED_INT_2_10_10_10_REV, and GL_UNSIGNED_INT_10F_11F_11F_REV require size = 4!");
            } else {
                Objects.requireNonNull(this.size = size);
            }

            if ((this.offset = offset) < 0) {
                throw new GLException("Invalid offset value! Offset cannot be less than 0.");
            }

            if ((this.stride = stride) < 0) {
                throw new GLException("Invalid stride value! Stride cannot be less than 0.");
            }

            Objects.requireNonNull(this.buffer = buffer);
            Objects.requireNonNull(this.type = type);

            this.normalized = normalized;

            switch (this.type) {
                case GL_FLOAT:
                case GL_DOUBLE:
                    if (this.normalized) {
                        throw new GLException("Normalized must be set to false if a floating-point type is used!");
                    }
            }
        }

        @Override
        public void run() {
            if (!GLVertexArray.this.isValid()) {
                throw new GLException("Invalid GLVertexArray!");
            }

            if (!buffer.isValid()) {
                throw new GLException("Invalid GLBuffer!");
            }

            // bind the VAO if it isn't bound already.
            GLVertexArray.this.bind();

            GL15.glBindBuffer(
                    GLBufferTarget.GL_ARRAY_BUFFER.value,
                    this.buffer.bufferId);

            if (this.type == GLVertexAttributeType.GL_DOUBLE) {
                GL41.glVertexAttribLPointer(
                        this.index,
                        this.size.value,
                        this.type.value,
                        this.stride, this.offset);
            } else {
                GL20.glVertexAttribPointer(
                        this.index,
                        this.size.value,
                        this.type.value,
                        this.normalized,
                        this.stride, this.offset);
            }
            GL20.glEnableVertexAttribArray(this.index);
        }
    }
}
