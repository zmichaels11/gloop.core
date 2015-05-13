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
public class GLVertexArray {

    public static final int CHECK_POLYGON_COUNT = -1;

    private static GLVertexArray CURRENT_VAO = null;
    private static final int INVALID_VERTEX_ARRAY_ID = -1;
    private int vaoId = INVALID_VERTEX_ARRAY_ID;
    private int polygons = 0;

    public boolean isValid() {
        return this.vaoId != INVALID_VERTEX_ARRAY_ID;
    }

    public int count() {
        return this.polygons;
    }

    private void bind() {
        if (CURRENT_VAO != this) {
            GL30.glBindVertexArray(this.vaoId);
            CURRENT_VAO = this;
        }
    }

    public class InitTask extends GLTask {

        @Override
        public void run() {
            if (!GLVertexArray.this.isValid()) {
                GLVertexArray.this.vaoId = GL30.glGenVertexArrays();
            }
            org.lwjgl.opengl.Util.checkGLError();
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
                    this.count == CHECK_POLYGON_COUNT
                            ? GLVertexArray.this.polygons
                            : this.count,
                    this.type.value,
                    this.offset,
                    this.instanceCount);
        }

    }

    public class DrawArraysInstancedTask extends GLTask {

        private final GLProgram program;
        private final GLDrawMode mode;
        private final int first;
        private final int count;
        private final int instanceCount;

        public DrawArraysInstancedTask(
                final GLProgram program, final int instanceCount) {

            this(program, GLDrawMode.GL_TRIANGLES, 0, CHECK_POLYGON_COUNT, instanceCount);
        }

        public DrawArraysInstancedTask(
                final GLProgram program,
                final GLDrawMode mode,
                final int instanceCount) {

            this(program, mode, 0, CHECK_POLYGON_COUNT, instanceCount);
        }

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
                    this.count == CHECK_POLYGON_COUNT
                            ? GLVertexArray.this.polygons
                            : this.count,
                    this.instanceCount);
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

            if((this.count = count) < 0) {
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

    public class DrawArraysTask extends GLTask {

        private final GLDrawMode mode;
        private final int start;
        private final int count;
        private final GLProgram program;

        public DrawArraysTask(final GLProgram program) {
            this(program, GLDrawMode.GL_TRIANGLES, 0, CHECK_POLYGON_COUNT);
        }

        public DrawArraysTask(
                final GLProgram program,
                final GLDrawMode mode) {

            this(program, mode, 0, CHECK_POLYGON_COUNT);
        }

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

            GL11.glDrawArrays(
                    this.mode.value,
                    this.start,
                    this.count == CHECK_POLYGON_COUNT
                            ? GLVertexArray.this.polygons
                            : this.count);
        }

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

        private void checkSize() {
            final int polyCount = GLVertexArray.this.polygons;
            final boolean sizeSet = (polyCount > 0);

            try {
                final int bufferSize = this.buffer.new ParameterQuery(
                        GLBufferTarget.GL_ARRAY_BUFFER,
                        GLBufferParameterName.GL_BUFFER_SIZE).call();

                final int pointWidth = this.type.width * this.size.value;
                final int bufferPolyCount = bufferSize / pointWidth;

                if (!sizeSet) {
                    GLVertexArray.this.polygons = bufferPolyCount;
                } else {
                    GLVertexArray.this.polygons = Math.min(polyCount, bufferPolyCount);
                }
            } catch (Exception ex) {
                throw new GLException("Unable to check buffer size!", ex);
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

            this.checkSize();

            // bind the VAO if it isn't bound already.
            GLVertexArray.this.bind();

            GL15.glBindBuffer(
                    GLBufferTarget.GL_ARRAY_BUFFER.value,
                    this.buffer.bufferId);

            if (this.type == GLVertexAttributeType.GL_DOUBLE) {
                GL41.glVertexAttribLPointer(
                        this.index,
                        this.size.value,
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

            org.lwjgl.opengl.Util.checkGLError();
        }
    }
}
