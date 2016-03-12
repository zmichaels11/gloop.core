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

import com.longlinkislong.gloop.glspi.VertexArray;
import java.nio.IntBuffer;
import java.util.Objects;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.Marker;
import org.slf4j.MarkerFactory;

/**
 * A GLVertexArray is an OpenGL object that contains information relevant to
 * vertex array states.
 *
 * @author zmichaels
 * @since 15.06.05
 */
public class GLVertexArray extends GLObject {

    private static final Marker GLOOP_MARKER = MarkerFactory.getMarker("GLOOP");
    private static final Logger LOGGER = LoggerFactory.getLogger("GLVertexArray");
    private transient volatile VertexArray vao = null;
    private String name = "";

    /**
     * Assigns a human-readable name to the GLVertexArray object.
     *
     * @param name the human-readable name.
     * @since 15.12.18
     */
    public final void setName(final CharSequence name) {
        GLTask.create(() -> {
            LOGGER.trace(
                    GLOOP_MARKER,
                    "Renamed GLVertexArray[{}] to GLVertexArray[{}]",
                    this.name,
                    name);

            this.name = name.toString();
        }).glRun(this.getThread());
    }

    /**
     * Retrieves the name of the GLVertexArray object.
     *
     * @return the name.
     * @since 15.12.18
     */
    public final String getName() {
        return this.name;
    }

    /**
     * Constructs a new GLVertexArray object on the default OpenGL thread.
     *
     * @since 15.06.05
     */
    public GLVertexArray() {
        this(GLThread.getDefaultInstance());
        LOGGER.warn(
                GLOOP_MARKER,
                "Creating a GLVertexArray with an implicit OpenGL thread is discouraged.");
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
        return vao != null && vao.isValid();
    }

    /**
     * Initializes the GLVertexArray object on its default thread. This function
     * is automatically called by the constructor and only should be called if
     * the GLVertexArray object is to be recycled.
     *
     * @throws GLException if the GLVertexArray object is already initialized.
     * @since 15.06.05
     */
    public final void init() throws GLException {
        new InitTask().glRun(this.getThread());
    }

    /**
     * A GLTask that initializes the parent GLVertexArray object.
     *
     * @since 15.06.05
     */
    public class InitTask extends GLTask {

        @Override
        public void run() {
            LOGGER.trace(GLOOP_MARKER, "############### Start GLVertexArray Init Task ###############");

            checkThread();

            if (GLVertexArray.this.isValid()) {
                throw new GLException("GLVertexArray is already initialized!");
            }

            vao = GLTools.getDriverInstance().vertexArrayCreate();
            GLVertexArray.this.name = "id=" + vao.hashCode();

            LOGGER.trace(GLOOP_MARKER, "Initialized GLVertexArray[{}]", GLVertexArray.this.name);
            LOGGER.trace(GLOOP_MARKER, "############## End GLVertexArray Init Task ###############");
        }
    }

    /**
     * Runs a draw elements indirect call on the object's default thread.
     *
     * @param drawMode the draw mode to use.
     * @param indexType the index type to use.
     * @param indirectCommandBuffer the indirect buffer to use
     * @param offset the offset to start reading from the indirect buffer.
     * @throws GLException if the vertex array or indirect command buffer is
     * invalid.
     * @since 15.06.05
     */
    public void drawElementsIndirect(
            final GLDrawMode drawMode,
            final GLIndexElementType indexType,
            final GLBuffer indirectCommandBuffer,
            final long offset) throws GLException {

        new DrawElementsIndirectTask(drawMode, indexType, indirectCommandBuffer, offset).glRun(this.getThread());
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

            this.indexType = Objects.requireNonNull(indexType);
            this.drawMode = Objects.requireNonNull(mode);
            this.indirectCommandBuffer = Objects.requireNonNull(indirectCommandBuffer);

            if ((this.offset = offset) < 0) {
                throw new GLException("Offset value cannot be less than 0!");
            }
        }

        @SuppressWarnings("unchecked")
        @Override
        public void run() {
            LOGGER.trace(GLOOP_MARKER, "############### Start GLVertexArray Draw Elements Indirect Task ##############");
            LOGGER.trace(GLOOP_MARKER, "\tDrawing GLVertexArray[{}]", getName());
            LOGGER.trace(GLOOP_MARKER, "\tDraw mode: {}", drawMode);
            LOGGER.trace(GLOOP_MARKER, "\tIndex type: {}", indexType);
            LOGGER.trace(GLOOP_MARKER, "\tIndirect command buffer: GLBuffer[{}]", indirectCommandBuffer.getName());
            LOGGER.trace(GLOOP_MARKER, "\tOffset: {} bytes", this.offset);

            checkThread();

            if (!GLVertexArray.this.isValid()) {
                throw new GLException("Invalid GLVertexArray!");
            } else if (!this.indirectCommandBuffer.isValid()) {
                throw new GLException("Invalid GLBuffer!");
            }

            GLTools.getDriverInstance().vertexArrayDrawElementsIndirect(vao, indirectCommandBuffer.buffer, drawMode.value, indexType.value, offset);
            LOGGER.trace(GLOOP_MARKER, "############ End GLVertexArray Draw Elements Indirect Task ###########");
        }
    }

    /**
     * Runs an indirect draw arrays task on the object's default GLThread.
     *
     * @param drawMode the draw mode to use
     * @param indirectCommandBuffer the indirect command buffer to grab
     * parameters from.
     * @param offset the offset to use
     * @throws GLException if the vertex array or indirect command buffer is
     * invalid.
     * @since 15.06.05
     */
    public void drawArraysIndirect(
            final GLDrawMode drawMode,
            final GLBuffer indirectCommandBuffer,
            final long offset) throws GLException {

        new DrawArraysIndirectTask(drawMode, indirectCommandBuffer, offset).glRun(this.getThread());
    }

    /**
     * A GLTask that runs an indirect draw arrays call.
     *
     * @since 15.06.24
     */
    public class DrawArraysIndirectTask extends GLTask implements GLDrawTask {

        private final GLBuffer indirectCommandBuffer;
        private final GLDrawMode drawMode;
        private final long offset;

        /**
         * Constructs a new DrawArraysIndirect task.
         *
         * @param drawMode the draw mode to use.
         * @param indirectCommandBuffer the GLBuffer to read indirect commands
         * from.
         * @param offset the offset to use
         * @since 15.06.24
         */
        public DrawArraysIndirectTask(
                final GLDrawMode drawMode,
                final GLBuffer indirectCommandBuffer,
                final long offset) {

            this.indirectCommandBuffer = Objects.requireNonNull(indirectCommandBuffer);
            this.drawMode = Objects.requireNonNull(drawMode);

            if ((this.offset = offset) < 0) {
                throw new GLException("Offset cannot be less than 0!");
            }
        }

        @SuppressWarnings("unchecked")
        @Override
        public void run() {
            LOGGER.trace(GLOOP_MARKER, "############### Start GLVertexArray Draw Arrays Indirect Task ###############");
            LOGGER.trace(GLOOP_MARKER, "\tDrawing GLVertexArray[{}]", getName());
            LOGGER.trace(GLOOP_MARKER, "\tDraw mode: {}", drawMode);
            LOGGER.trace(GLOOP_MARKER, "\tIndirect command buffer: GLBuffer[{}]", this.indirectCommandBuffer.getName());

            checkThread();

            if (!GLVertexArray.this.isValid()) {
                throw new GLException("Invalid GLVertexArray!");
            } else if (!this.indirectCommandBuffer.isValid()) {
                throw new GLException("Invalid GLBuffer!");
            }

            GLTools.getDriverInstance().vertexArrayDrawArraysIndirect(vao, indirectCommandBuffer.buffer, drawMode.value, offset);
            LOGGER.trace(GLOOP_MARKER, "############### End GLVertexArray Draw Arrays Indirect Task ###############");
        }
    }

    /**
     * Performs a multidraw arrays task on the default OpenGL thread.
     *
     * @param drawMode the draw mode to use.
     * @param first the individual offsets.
     * @param count the number of elements to render per offset.
     * @since 15.06.24
     */
    public void multiDrawArrays(
            final GLDrawMode drawMode,
            final IntBuffer first, final IntBuffer count) {

        new MultiDrawArraysTask(drawMode, first, count).glRun(this.getThread());
    }

    /**
     * A GLTask that performs a MultiDraw operation.
     *
     * @since 15.06.24
     */
    public class MultiDrawArraysTask extends GLTask implements GLDrawTask {

        private final IntBuffer first;
        private final IntBuffer count;
        private final GLDrawMode drawMode;

        /**
         * Constructs a new MultiDrawArraysTask.
         *
         * @param drawMode the draw mode to use.
         * @param first the offset for each draw task
         * @param count the number of elements to draw per task
         * @since 15.06.24
         */
        public MultiDrawArraysTask(
                final GLDrawMode drawMode,
                final IntBuffer first, final IntBuffer count) {

            Objects.requireNonNull(this.first = first);
            Objects.requireNonNull(this.count = count);
            Objects.requireNonNull(this.drawMode = drawMode);
        }

        @SuppressWarnings("unchecked")
        @Override
        public void run() {
            LOGGER.trace(GLOOP_MARKER, "############### Start GLVertexArray Multi Draw Arrays Task ###############");
            LOGGER.trace(GLOOP_MARKER, "\tDrawing GLVertexArray[{}]", getName());
            LOGGER.trace(GLOOP_MARKER, "\tDraw mode: {}", this.drawMode);

            checkThread();

            if (!GLVertexArray.this.isValid()) {
                throw new GLException("Invalid GLVertexArray!");
            }

            GLTools.getDriverInstance().vertexArrayMultiDrawArrays(vao, drawMode.value, first, count);
            LOGGER.trace(GLOOP_MARKER, "############### End GLVertexArray Multi Draw Arrays Task ###############");
        }
    }

    /**
     * Performs a draw elements instanced task on the default OpenGL thread.
     *
     * @param drawMode the draw mode to use.
     * @param count the number of elements to draw.
     * @param indexType the data type the indices are stored as.
     * @param offset the offset for the start buffer.
     * @param instanceCount the number of copies to draw.
     * @since 15.06.24
     */
    public void drawElementsInstanced(
            final GLDrawMode drawMode,
            final int count, final GLIndexElementType indexType,
            final long offset, final int instanceCount) {

        new DrawElementsInstancedTask(drawMode, count, indexType, offset, instanceCount).glRun(this.getThread());
    }

    /**
     * A GLTask that performs a draw elements instanced operation.
     *
     * @since 15.60.24
     */
    public class DrawElementsInstancedTask extends GLTask implements GLDrawTask {

        private final int count;
        private GLIndexElementType type;
        private GLDrawMode drawMode;
        private final int instanceCount;
        private final long offset;

        /**
         * Constructs a new DrawElementsInstancedTask.
         *
         * @param drawMode the drawmode to use.
         * @param count the number of elements to draw for the base instance.
         * @param indexType the data type the indices are stored as.
         * @param offset the offset for the first instance.
         * @param instanceCount the number of instances to draw.
         * @since 15.06.24
         */
        public DrawElementsInstancedTask(
                final GLDrawMode drawMode,
                final int count,
                final GLIndexElementType indexType,
                final long offset,
                final int instanceCount) {

            this.count = count;
            this.type = Objects.requireNonNull(indexType);
            this.drawMode = Objects.requireNonNull(drawMode);

            if ((this.offset = offset) < 0) {
                throw new GLException("Offset cannot be less than 0!");
            }

            if ((this.instanceCount = instanceCount) < 0) {
                throw new GLException("Instance count cannot be less than 0!");
            }
        }

        @SuppressWarnings("unchecked")
        @Override
        public void run() {
            LOGGER.trace(GLOOP_MARKER, "############### Start GLVertexArray Draw Elements Instanced Task ###############");
            LOGGER.trace(GLOOP_MARKER, "\tDrawing GLVertexArray[{}]", getName());
            LOGGER.trace(GLOOP_MARKER, "\tDraw mode: {}", drawMode);
            LOGGER.trace(GLOOP_MARKER, "\tCount: {}", count);
            LOGGER.trace(GLOOP_MARKER, "\tIndex type: {}", type);
            LOGGER.trace(GLOOP_MARKER, "\tOffset: {} bytes", offset);
            LOGGER.trace(GLOOP_MARKER, "\tInstance count: {}", this.instanceCount
            );

            checkThread();

            if (!GLVertexArray.this.isValid()) {
                throw new GLException("Invalid GLVertexArray!");
            }
            
            GLTools.getDriverInstance().vertexArrayDrawElementsInstanced(vao, drawMode.value, count, type.value, offset, instanceCount);            
            LOGGER.trace(GLOOP_MARKER, "############### End GLVertexArray Draw Elements Instanced Task ###############");
        }

    }

    /**
     * Performs a draw arrays instanced task on the OpenGL thread associated
     * with this object.
     *
     * @param mode the draw mode to use.
     * @param first the offset to the first element.
     * @param count the number of elements to draw.
     * @param instanceCount the number of instances to draw.
     * @since 15.06.24
     */
    public void drawArraysInstanced(
            final GLDrawMode mode,
            final int first, final int count, final int instanceCount) {

        new DrawArraysInstancedTask(
                mode,
                first,
                count,
                instanceCount).glRun(this.getThread());
    }

    /**
     * A GLTask that runs a draw arrays instanced task.
     *
     * @since 15.06.24
     */
    public final class DrawArraysInstancedTask extends GLTask implements GLDrawTask {

        private final GLDrawMode mode;
        private final int first;
        private final int count;
        private final int instanceCount;

        /**
         * Constructs a new Draw Arrays Instanced task.
         *
         * @param mode the draw mode to use.
         * @param first the offset to the first instance.
         * @param count the number of vertices to draw.
         * @param instanceCount the number of instances to draw.
         * @since 15.06.24
         */
        public DrawArraysInstancedTask(
                final GLDrawMode mode,
                final int first,
                final int count,
                final int instanceCount) {

            this.mode = Objects.requireNonNull(mode);
            this.count = count;

            if ((this.first = first) < 0) {
                throw new GLException("First cannot be less than 0!");
            }

            if ((this.instanceCount = instanceCount) < 0) {
                throw new GLException("Instance count cannot be less than 0!");
            }
        }

        @SuppressWarnings("unchecked")
        @Override
        public void run() {
            LOGGER.trace(GLOOP_MARKER, "############### Start GLVertexArray Draw Arrays Instanced Task ###############");
            LOGGER.trace(GLOOP_MARKER, "\tDrawing GLVertexArray[{}]", getName());
            LOGGER.trace(GLOOP_MARKER, "\tDraw mode: {}", mode);
            LOGGER.trace(GLOOP_MARKER, "\tFirst: {}", first);
            LOGGER.trace(GLOOP_MARKER, "\tCount: {}", count);
            LOGGER.trace(GLOOP_MARKER, "\tInstance count: {}", this.instanceCount);

            checkThread();

            if (!GLVertexArray.this.isValid()) {
                throw new GLException("GLVertexArray is not valid!");
            }

            GLTools.getDriverInstance().vertexArrayDrawArraysInstanced(vao, mode.value, first, count, instanceCount);            
            LOGGER.trace(GLOOP_MARKER, "############### End GLVertexArray Draw Arrays Instanced Task ###############");
        }
    }

    /**
     * Performs a draw elements task on the OpenGL thread associated with this
     * object.
     *
     * @param mode the draw mode to use.
     * @param count the number of elements to draw.
     * @param type the data type the elements are stored in.
     * @param offset the offset to the first element.
     * @since 15.06.24
     */
    public void drawElements(
            final GLDrawMode mode,
            final int count, final GLIndexElementType type, final long offset) {

        new DrawElementsTask(mode, count, type, offset).glRun(this.getThread());
    }

    /**
     * A GLTask that executes a draw elements task.
     *
     * @since 15.06.24
     */
    public class DrawElementsTask extends GLTask {

        private final GLDrawMode mode;
        private final int count;
        private final GLIndexElementType type;
        private final long offset;

        /**
         * Constructs a new DrawElementsTask
         *
         * @param mode the draw mode used for the draw elements task.
         * @param count the number of elements drawn.
         * @param type the data type the elements are stored as.
         * @param offset the offset to the first element
         * @since 15.06.24
         */
        public DrawElementsTask(
                final GLDrawMode mode,
                final int count,
                final GLIndexElementType type,
                final long offset) {

            this.mode = Objects.requireNonNull(mode);
            this.type = Objects.requireNonNull(type);

            if ((this.count = count) < 0) {
                throw new GLException("Count cannot be less than 0!");
            } else if ((this.offset = offset) < 0) {
                throw new GLException("Offset cannot be less than 0!");
            }
        }

        @SuppressWarnings("unchecked")
        @Override
        public void run() {
            LOGGER.trace(GLOOP_MARKER, "############### Start GLVertexArray Draw Elements Task ###############");
            LOGGER.trace(GLOOP_MARKER, "\tDrawing GLVertexArray[{}]", getName());
            LOGGER.trace(GLOOP_MARKER, "\tDraw mode: {}", mode);
            LOGGER.trace(GLOOP_MARKER, "\tCount: {}", count);
            LOGGER.trace(GLOOP_MARKER, "\tIndex type: {}", type);
            LOGGER.trace(GLOOP_MARKER, "\tOffset: {} bytes", this.offset);

            checkThread();

            if (!GLVertexArray.this.isValid()) {
                throw new GLException("Invalid GLVertex!");
            }

            GLTools.getDriverInstance().vertexArrayDrawElements(vao, mode.value, count, type.value, offset);
            LOGGER.trace(GLOOP_MARKER, "############### End GLVertexArray Draw Elements Task ###############");
        }
    }

    /**
     * Performs a draw arrays task on the OpenGL thread associated with this
     * object.
     *
     * @param mode the draw mode used.
     * @param start the start offset for the draw arrays call.
     * @param count the number of vertices drawn.
     * @since 15.06.24
     */
    public void drawArrays(
            final GLDrawMode mode,
            final int start, final int count) {

        new DrawArraysTask(mode, start, count).glRun(this.getThread());
    }

    /**
     * Draws a transform feedback. The rasterizer is discarded.
     *
     * @param mode the polygon mode for drawing the feedback.
     * @param start the index of the first element to draw.
     * @param count the number of elements to draw.
     * @since 15.10.30
     */
    public void drawTransformFeedback(
            final GLDrawMode mode,
            final int start,
            final int count) {

        new DrawTransformFeedbackTask(
                mode,
                start,
                count).glRun(this.getThread());
    }

    /**
     * A GLTask that draws a transform feedback.
     *
     * @since 15.10.30
     */
    public final class DrawTransformFeedbackTask
            extends GLTask
            implements GLDrawTask {

        private final GLDrawMode mode;
        private final int start;
        private final int count;

        /**
         * Constructs a new DrawTransformFeedbackTask
         *
         * @param mode the type of feedback primitive.
         * @param start the index of the first element to draw.
         * @param count the number of elements to draw.
         * @since 15.10.30
         */
        public DrawTransformFeedbackTask(GLDrawMode mode, int start, int count) {

            this.mode = Objects.requireNonNull(mode);
            this.count = count;

            if ((this.start = start) < 0) {
                throw new GLException("Start value cannot be less than 0!");
            }
        }

        @SuppressWarnings("unchecked")
        @Override
        public void run() {
            LOGGER.trace(GLOOP_MARKER, "############### Start GLVertexArray Draw Transform Feedback Task ###############");
            LOGGER.trace(GLOOP_MARKER, "\tDrawing GLVertexArray[{}]", getName());
            LOGGER.trace(GLOOP_MARKER, "\tDraw mode: {}", mode);
            LOGGER.trace(GLOOP_MARKER, "\tStart: {}", start);
            LOGGER.trace(GLOOP_MARKER, "\tCount: {}", this.count);

            checkThread();

            if (!GLVertexArray.this.isValid()) {
                throw new GLException("GLVertexArray is not valid!");
            }

            GLTools.getDriverInstance().vertexArrayDrawTransformFeedback(vao, mode.value, start, count);            
            LOGGER.trace(GLOOP_MARKER, "############### End GLVertexArray Draw Transform Feedback Task ###############");
        }
    }

    public final class DrawArraysTask extends GLTask implements GLDrawTask {

        private final GLDrawMode mode;
        private final int start;
        private final int count;

        /**
         * Constructs a new DrawArraysTask.
         *
         * @param mode the draw mode.
         * @param start the starting vertex id.
         * @param count the number of vertices to draw.
         * @since 15.12.18
         */
        public DrawArraysTask(
                final GLDrawMode mode,
                final int start, final int count) {

            this.mode = Objects.requireNonNull(mode);

            if ((this.count = count) < 1) {
                throw new GLException("Count value cannot be less than 1!");
            }

            if ((this.start = start) < 0) {
                throw new GLException("Start value cannot be less than 0!");
            }
        }

        @SuppressWarnings("unchecked")
        @Override
        public void run() {
            LOGGER.trace(GLOOP_MARKER, "############### Start GLVertexArray Draw Arrays Task ###############");
            LOGGER.trace(GLOOP_MARKER, "Drawing GLVertexArray[{}]", getName());
            LOGGER.trace(GLOOP_MARKER, "\tDraw mode: {}", mode);
            LOGGER.trace(GLOOP_MARKER, "\tStart: {}", start);
            LOGGER.trace(GLOOP_MARKER, "\tCount: {}", this.count);

            checkThread();

            if (!GLVertexArray.this.isValid()) {
                throw new GLException("GLVertexArray is not valid!");
            }
            
            GLTools.getDriverInstance().vertexArrayDrawArrays(vao, mode.value, start, count);
            LOGGER.trace(GLOOP_MARKER, "############### End GLVertexArray Draw Arrays Task ###############");
        }

    }

    /**
     * Deletes the GLVertexArray object.
     *
     * @since 15.12.18
     */
    public void delete() {
        new DeleteTask().glRun(this.getThread());
    }

    /**
     * A GLTask that deletes the GLVertexArray object.
     *
     * @since 15.12.18
     */
    public final class DeleteTask extends GLTask {

        @SuppressWarnings("unchecked")
        @Override
        public void run() {
            LOGGER.trace(GLOOP_MARKER, "############### Start GLVertexArray Delete Task ###############");
            LOGGER.trace(GLOOP_MARKER, "\tDeleting GLVertexArray[{}]", GLVertexArray.this.getName());

            checkThread();

            if (GLVertexArray.this.isValid()) {
                GLTools.getDriverInstance().vertexArrayDelete(vao);
                vao = null;
            }

            LOGGER.trace(GLOOP_MARKER, "############### End GLVertexArray Delete Task ###############");
        }
    }

    /**
     * Attaches a GLBuffer as an index buffer.
     *
     * @param buffer the GLBuffer to attach.
     * @since 15.12.18
     */
    public void attachIndexBuffer(final GLBuffer buffer) {
        new AttachIndexBufferTask(buffer).glRun(this.getThread());
    }

    /**
     * A GLTask that attaches a GLBuffer as an Index Buffer.
     *
     * @since 15.12.18
     */
    public final class AttachIndexBufferTask extends GLTask {

        private final GLBuffer buffer;

        public AttachIndexBufferTask(final GLBuffer buffer) {
            this.buffer = Objects.requireNonNull(buffer);
        }

        @SuppressWarnings("unchecked")
        @Override
        public void run() {
            LOGGER.trace(GLOOP_MARKER, "############### Start GLVertexArray Attach Index Buffer Task ###############");
            LOGGER.trace(GLOOP_MARKER, "\tAttaching to GLVertexArray[{}]", getName());
            LOGGER.trace(GLOOP_MARKER, "\tIndex buffer: GLBuffer[{}]", this.buffer.getName());

            if (!GLVertexArray.this.isValid()) {
                throw new GLException("Invalid GLVertexArray!");
            } else if (!this.buffer.isValid()) {
                throw new GLException("Invalid GLBuffer!");
            }

            GLTools.getDriverInstance().vertexArrayAttachIndexBuffer(vao, buffer.buffer);            
            LOGGER.trace(GLOOP_MARKER, "############### End GLVertexArray Attach Index Buffer Task ###############");
        }
    }

    /**
     * Attaches a GLBuffer to the GLVertexArray object.
     *
     * @param index the index to attach the GLBuffer.
     * @param buffer the GLBuffer to attach.
     * @param type the type of data held by the GLBuffer.
     * @param size the attribute element size.
     * @since 15.12.18
     */
    public void attachBuffer(
            final int index, final GLBuffer buffer,
            final GLVertexAttributeType type, final GLVertexAttributeSize size) {

        new AttachBufferTask(index, buffer, type, size).glRun(this.getThread());
    }

    /**
     * Attaches a GLBuffer to the GLVertexArray object.
     *
     * @param index the index to attach the GLBuffer.
     * @param buffer the GLBuffer to attach.
     * @param type the type of data held by the GLBuffer.
     * @param size the attribute element size.
     * @param offset the offset to begin reading from the GLBuffer.
     * @param stride the space between elements in the GLBuffer.
     * @since 15.12.18
     */
    public void attachBuffer(
            final int index, final GLBuffer buffer,
            final GLVertexAttributeType type, final GLVertexAttributeSize size,
            final int offset, final int stride) {

        new AttachBufferTask(
                index,
                buffer,
                type,
                size,
                false,
                stride,
                offset).glRun(this.getThread());
    }

    /**
     * Attaches a GLBuffer to the GLVertexArray object.
     *
     * @param index the index to attach the GLBuffer.
     * @param buffer the GLBuffer to attach.
     * @param type the type of data held by the GLBuffer.
     * @param size the attribute element size.
     * @param offset the offset to begin reading from the GLBuffer.
     * @param stride the space between elements in the GLBuffer.
     * @param divisor the rate at which the element index increases.
     * @since 15.12.18
     */
    public void attachBuffer(
            final int index, final GLBuffer buffer,
            final GLVertexAttributeType type, final GLVertexAttributeSize size,
            final int offset, final int stride,
            final int divisor) {

        new AttachBufferTask(
                index,
                buffer,
                type,
                size,
                false,
                stride,
                offset,
                divisor).glRun(this.getThread());
    }

    /**
     * A GLTask that attaches a GLBuffer to the GLVertexArray object.
     *
     * @since 15.12.18
     */
    public final class AttachBufferTask extends GLTask {

        private final int index;
        private final GLBuffer buffer;
        private final GLVertexAttributeType type;
        private final GLVertexAttributeSize size;
        private final int stride;
        private final long offset;
        private final boolean normalized;
        private final int divisor;

        /**
         * Constructs a new AttachBufferTask.
         *
         * @param index the index to attach the GLBuffer to.
         * @param buffer the GLBuffer to attach.
         * @param type the type of data held by the GLBuffer.
         * @param size the size of the elements in the GLBuffer.
         * @since 15.12.18
         */
        public AttachBufferTask(
                final int index,
                final GLBuffer buffer,
                final GLVertexAttributeType type,
                final GLVertexAttributeSize size) {

            this(index, buffer, type, size, false, 0, 0);

        }

        /**
         * Constructs a new AttachBufferTask.
         *
         * @param index the index to attach the GLBuffer to.
         * @param buffer the GLBuffer to attach.
         * @param type the type of data held by the GLBuffer.
         * @param size the size of the elements in the GLBuffer.
         * @param normalized if the data needs to be normalized.
         * @since 15.12.19
         */
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

            this.size = Objects.requireNonNull(size);

            if ((this.offset = offset) < 0) {
                throw new GLException("Invalid offset value! Offset cannot be less than 0.");
            }

            if ((this.stride = stride) < 0) {
                throw new GLException("Invalid stride value! Stride cannot be less than 0.");
            }

            if ((this.divisor = divisor) < 0) {
                throw new GLException("Invalid divisor value! Divisor cannot be less than 0.");
            }

            this.buffer = Objects.requireNonNull(buffer);
            this.type = Objects.requireNonNull(type);
            this.normalized = normalized;

            switch (this.type) {
                case GL_FLOAT:
                case GL_DOUBLE:
                    if (this.normalized) {
                        throw new GLException("Normalized must be set to false if a floating-point type is used!");
                    }
            }
        }

        @SuppressWarnings("unchecked")
        @Override
        public void run() {
            LOGGER.trace(GLOOP_MARKER, "############### Start GLVertexArray Attach Buffer Task ###############");
            LOGGER.trace(GLOOP_MARKER, "\tAttaching buffer to GLVertexArray[{}]", getName());
            LOGGER.trace(GLOOP_MARKER, "\tIndex: {}", index);
            LOGGER.trace(GLOOP_MARKER, "\tVertex buffer: GLBuffer[{}]", buffer.getName());
            LOGGER.trace(GLOOP_MARKER, "\tType: {}", type);
            LOGGER.trace(GLOOP_MARKER, "\tSize: {}", size);
            LOGGER.trace(GLOOP_MARKER, "\tNormalized: {}", normalized);
            LOGGER.trace(GLOOP_MARKER, "\tStride: {}", stride);
            LOGGER.trace(GLOOP_MARKER, "\tOffset: {}", offset);
            LOGGER.trace(GLOOP_MARKER, "\tDivisor: {}", this.divisor);

            checkThread();

            if (!GLVertexArray.this.isValid()) {
                throw new GLException("Invalid GLVertexArray!");
            }

            if (!buffer.isValid()) {
                throw new GLException("Invalid GLBuffer!");
            }

            GLTools.getDriverInstance().vertexArrayAttachBuffer(vao, index, buffer.buffer, size.value, type.value, stride, offset, divisor);                        
            LOGGER.trace(GLOOP_MARKER, "############### End GLVertexArray Attach Buffer Task ###############");
        }
    }

    @Override
    public final boolean isShareable() {
        return false;
    }
}
