/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.longlinkislong.gloop;

import java.nio.IntBuffer;
import java.util.HashMap;
import java.util.Map;
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
 * A GLVertexArray is an OpenGL object that contains information relevant to
 * vertex array states.
 *
 * @author zmichaels
 * @since 15.06.05
 */
public class GLVertexArray extends GLObject {

    private static final Map<Thread, GLVertexArray> CURRENT = new HashMap<>();
    private static final int INVALID_VERTEX_ARRAY_ID = -1;
    private int vaoId = INVALID_VERTEX_ARRAY_ID;

    /**
     * Constructs a new GLVertexArray object on the default OpenGL thread.
     *
     * @since 15.06.05
     */
    public GLVertexArray() {
        super();
        this.init();
    }

    /**
     * Constructs a new GLVertexArray object on the specified OpenGL thread.
     *
     * @param thread the thread to create the object on.
     * @since 15.06.05
     */
    public GLVertexArray(final GLThread thread) {
        super(thread);
        this.init();
    }

    /**
     * Checks if the OpenGL object is valid. A GLVertexArray object is
     * considered valid if it has been initialized and until it is deleted.
     *
     * @return true if the object is valid.
     * @since 15.06.05
     */
    public boolean isValid() {
        return this.vaoId != INVALID_VERTEX_ARRAY_ID;
    }

    private boolean isCurrent() {
        return CURRENT.get(Thread.currentThread()) == this;
    }

    private void bind() {
        if (!this.isCurrent()) {
            GL30.glBindVertexArray(this.vaoId);
            CURRENT.put(Thread.currentThread(), this);
        }
    }

    private final InitTask initTask = new InitTask();

    /**
     * Initializes the GLVertexArray object on its default thread. This function
     * is automatically called by the constructor and only should be called if
     * the GLVertexArray object is to be recycled.
     *
     * @throws GLException if the GLVertexArray object is already initialized.
     * @since 15.06.05
     */
    public final void init() throws GLException {
        this.initTask.glRun(this.getThread());
    }

    /**
     * A GLTask that initializes the parent GLVertexArray object.
     *
     * @since 15.06.05
     */
    public class InitTask extends GLTask {

        @Override
        public void run() {
            if (GLVertexArray.this.isValid()) {
                throw new GLException("GLVertexArray is already initialized!");
            }

            GLVertexArray.this.vaoId = GL30.glGenVertexArrays();
        }
    }

    private DrawElementsIndirectTask lastDrawElementsIndirect = null;

    /**
     * Runs a draw elements indirect call on the object's default thread.
     *     
     * @param drawMode the draw mode to use.
     * @param indexType the index type to use.
     * @param indirectCommandBuffer the indirect buffer to use
     * @param offset the offset to start reading from the indirect buffer.
     * @throws GLException if the vertex array or indirect command
     * buffer is invalid.
     * @since 15.06.05
     */
    public void drawElementsIndirect(            
            final GLDrawMode drawMode,
            final GLIndexElementType indexType,
            final GLBuffer indirectCommandBuffer,
            final long offset) throws GLException {

        if (this.lastDrawElementsIndirect != null                
                && this.lastDrawElementsIndirect.indirectCommandBuffer == indirectCommandBuffer
                && this.lastDrawElementsIndirect.drawMode == drawMode
                && this.lastDrawElementsIndirect.indexType == indexType
                && this.lastDrawElementsIndirect.offset == offset) {

            this.lastDrawElementsIndirect.glRun(this.getThread());
        } else {
            this.lastDrawElementsIndirect = new DrawElementsIndirectTask(                    
                    drawMode,
                    indexType,
                    indirectCommandBuffer,
                    offset);

            this.lastDrawElementsIndirect.glRun(this.getThread());
        }
    }

    /**
     * A GLTask that executes an indirect draw elements task.
     *
     * @since 15.06.05
     */
    public class DrawElementsIndirectTask extends GLTask implements GLDrawTask {

        private final GLBuffer indirectCommandBuffer;        
        private final GLDrawMode drawMode;
        private final GLIndexElementType indexType;
        private final long offset;

        /**
         * Constructs a new DrawElementsIndirectTask using 0 for the offset.
         *         
         * @param mode the draw mode to use
         * @param indexType the index type to use
         * @param indirectCommandBuffer the indirect command buffer
         * @since 15.06.05
         */
        public DrawElementsIndirectTask(                
                final GLDrawMode mode, final GLIndexElementType indexType,
                final GLBuffer indirectCommandBuffer) {

            this(mode, indexType, indirectCommandBuffer, 0);
        }

        /**
         * Constructs a new DrawElementsIndirectTask.
         *         
         * @param mode the draw mode to use
         * @param indexType the index type to use
         * @param indirectCommandBuffer the indirect command buffer
         * @param offset the offset to use
         * @throws GLException if the offset is less than 0.
         * @since 15.06.05
         */
        public DrawElementsIndirectTask(                
                final GLDrawMode mode, final GLIndexElementType indexType,
                final GLBuffer indirectCommandBuffer,
                final long offset) throws GLException {

            
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

    /**
     * Runs an indirect draw arrays task on the object's default GLThread.
     *     
     * @param drawMode the draw mode to use
     * @param indirectCommandBuffer the indirect command buffer to grab
     * parameters from.
     * @param offset the offset to use
     * @throws GLException if the vertex array or indirect command
     * buffer is invalid.
     * @since 15.06.05
     */
    public void drawArraysIndirect(            
            final GLDrawMode drawMode,
            final GLBuffer indirectCommandBuffer,
            final long offset) throws GLException {

        if (this.lastDrawArraysIndirect != null
                && this.lastDrawArraysIndirect.indirectCommandBuffer == indirectCommandBuffer                
                && this.lastDrawArraysIndirect.drawMode == drawMode
                && this.lastDrawArraysIndirect.offset == offset) {

            this.lastDrawArraysIndirect.glRun(this.getThread());
        }
    }

    public class DrawArraysIndirectTask extends GLTask implements GLDrawTask {

        private final GLBuffer indirectCommandBuffer;        
        private final GLDrawMode drawMode;
        private final long offset;

        public DrawArraysIndirectTask(                
                final GLDrawMode drawMode,
                final GLBuffer indirectCommandBuffer,
                final long offset) {

            Objects.requireNonNull(this.indirectCommandBuffer = indirectCommandBuffer);
            Objects.requireNonNull(this.drawMode = drawMode);            

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
            final GLDrawMode drawMode,
            final IntBuffer first, final IntBuffer count) {

        if (this.lastMultiDrawArrays != null                
                && this.lastMultiDrawArrays.first == first
                && this.lastMultiDrawArrays.count == count
                && this.lastMultiDrawArrays.drawMode == drawMode) {

            this.lastMultiDrawArrays.glRun(this.getThread());
        } else {
            this.lastMultiDrawArrays = new MultiDrawArraysTask(
                    drawMode, first, count);

            this.lastMultiDrawArrays.glRun(this.getThread());
        }
    }

    public class MultiDrawArraysTask extends GLTask implements GLDrawTask {
        
        private final IntBuffer first;
        private final IntBuffer count;
        private final GLDrawMode drawMode;

        public MultiDrawArraysTask(                
                final GLDrawMode drawMode,
                final IntBuffer first, final IntBuffer count) {
            
            Objects.requireNonNull(this.first = first);
            Objects.requireNonNull(this.count = count);
            Objects.requireNonNull(this.drawMode = drawMode);
        }

        @Override
        public void run() {
            if (!GLVertexArray.this.isValid()) {
                throw new GLException("Invalid GLVertexArray!");
            }
            
            GLVertexArray.this.bind();
            GL14.glMultiDrawArrays(
                    this.drawMode.value,
                    this.first,
                    this.count);

        }
    }

    private DrawElementsInstancedTask lastDrawElementsInstanced = null;

    public void drawElementsInstanced(
            final GLDrawMode drawMode,
            final int count, final GLIndexElementType indexType,
            final long offset, final int instanceCount) {

        if (this.lastDrawElementsInstanced != null                
                && this.lastDrawElementsInstanced.drawMode == drawMode
                && this.lastDrawElementsInstanced.count == count
                && this.lastDrawElementsInstanced.type == indexType
                && this.lastDrawElementsInstanced.offset == offset
                && this.lastDrawElementsInstanced.instanceCount == instanceCount) {

            this.lastDrawElementsInstanced.glRun(this.getThread());
        } else {
            this.lastDrawElementsInstanced = new DrawElementsInstancedTask(
                    drawMode, count, indexType, offset, instanceCount);

            this.lastDrawElementsInstanced.glRun(this.getThread());
        }
    }

    public class DrawElementsInstancedTask extends GLTask implements GLDrawTask {
        
        private final int count;
        private GLIndexElementType type;
        private GLDrawMode drawMode;
        private final int instanceCount;
        private final long offset;

        public DrawElementsInstancedTask(                
                final GLDrawMode drawMode,
                final int count,
                final GLIndexElementType indexType,
                final long offset,
                final int instanceCount) {
            
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

            GLVertexArray.this.bind();
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
            final GLDrawMode mode,
            final int first, final int count, final int instanceCount) {

        if (this.lastDrawArraysInstanced != null                
                && this.lastDrawArraysInstanced.mode == mode
                && this.lastDrawArraysInstanced.first == first
                && this.lastDrawArraysInstanced.count == count
                && this.lastDrawArraysInstanced.instanceCount == instanceCount) {

            this.lastDrawArraysInstanced.glRun(this.getThread());
        } else {
            this.lastDrawArraysInstanced = new DrawArraysInstancedTask(
                    mode,
                    first, count, instanceCount);

            this.lastDrawArraysInstanced.glRun(this.getThread());
        }
    }

    public class DrawArraysInstancedTask extends GLTask implements GLDrawTask {
        
        private final GLDrawMode mode;
        private final int first;
        private final int count;
        private final int instanceCount;

        public DrawArraysInstancedTask(                
                final GLDrawMode mode,
                final int first,
                final int count,
                final int instanceCount) {
            
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
            final GLDrawMode mode,
            final int count, final GLIndexElementType type, final long offset) {

        if (this.lastDrawElements != null                
                && this.lastDrawElements.mode == mode
                && this.lastDrawElements.count == count
                && this.lastDrawElements.type == type
                && this.lastDrawElements.offset == offset) {

            this.lastDrawElements.glRun(this.getThread());
        } else {
            this.lastDrawElements = new DrawElementsTask(
                    mode,
                    count, type, offset);

            this.lastDrawElements.glRun(this.getThread());
        }
    }

    public class DrawElementsTask extends GLTask {
        
        private final GLDrawMode mode;
        private final int count;
        private final GLIndexElementType type;
        private final long offset;

        public DrawElementsTask(
                final GLDrawMode mode,
                final int count,
                final GLIndexElementType type,
                final long offset) {
            
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
            final GLDrawMode mode,
            final int start, final int count) {

        if (this.lastDrawArrays != null                
                && this.lastDrawArrays.mode == mode
                && this.lastDrawArrays.start == start
                && this.lastDrawArrays.count == count) {

            this.lastDrawArrays.glRun(this.getThread());
        } else {
            this.lastDrawArrays = new DrawArraysTask(mode, start, count);
            this.lastDrawArrays.glRun(this.getThread());
        }
    }

    public class DrawTransformFeedbackTask extends GLTask {

        final GLTransformFeedback tfb;
        final GLDrawMode mode;        

        public DrawTransformFeedbackTask(                
                final GLDrawMode mode,
                final GLTransformFeedback tfb) {
            
            Objects.requireNonNull(this.mode = mode);
            Objects.requireNonNull(this.tfb = tfb);
        }

        @Override
        public void run() {
            if (!GLVertexArray.this.isValid()) {
                throw new GLException("Invalid GLVertexArray!");
            }
            
            GLVertexArray.this.bind();
            GL40.glDrawTransformFeedback(this.mode.value, this.tfb.tfbId);
        }
    }

    public class DrawArraysTask extends GLTask implements GLDrawTask {

        private final GLDrawMode mode;
        private final int start;
        private final int count;        

        public DrawArraysTask(                
                final GLDrawMode mode,
                final int start, final int count) {
            
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
        private final int divisor;

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

            this(index, buffer, type, size, normalized, stride, offset, 0);
        }

        public AttachBufferTask(
                final int index,
                final GLBuffer buffer,
                final GLVertexAttributeType type,
                final GLVertexAttributeSize size,
                final boolean normalized,
                final int stride, final long offset,
                final int divisor) {

            if ((this.index = index) < 0) {
                throw new GLException("Invalid index value [" + index + "]! Index cannot be less than 0.");
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

            if ((this.divisor = divisor) < 0) {
                throw new GLException("Invalid divisor value! Divisor cannot be less than 0.");
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

            GL20.glEnableVertexAttribArray(this.index);
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

            GL33.glVertexAttribDivisor(this.index, this.divisor);
        }
    }

}
