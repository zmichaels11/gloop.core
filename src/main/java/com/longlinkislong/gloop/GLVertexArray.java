/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.longlinkislong.gloop;

import static com.longlinkislong.gloop.GLAsserts.checkGLError;
import static com.longlinkislong.gloop.GLAsserts.glErrorMsg;
import static java.lang.Long.toHexString;
import java.nio.IntBuffer;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import org.lwjgl.opengl.ARBDrawIndirect;
import org.lwjgl.opengl.ARBDrawInstanced;
import org.lwjgl.opengl.ARBInstancedArrays;
import org.lwjgl.opengl.ARBTransformFeedback2;
import org.lwjgl.opengl.ARBVertexArrayObject;
import org.lwjgl.opengl.ARBVertexAttrib64Bit;
import org.lwjgl.opengl.ContextCapabilities;
import org.lwjgl.opengl.GL;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL14;
import org.lwjgl.opengl.GL15;
import org.lwjgl.opengl.GL20;
import org.lwjgl.opengl.GL30;
import org.lwjgl.opengl.GL31;
import org.lwjgl.opengl.GL33;
import org.lwjgl.opengl.GL40;
import org.lwjgl.opengl.GL41;
import static org.lwjgl.system.MemoryUtil.memAddress;

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
            GLVertexArray.this.glBindVertexArray.call(this.vaoId);
            assert GL11.glGetError() == GL11.GL_NO_ERROR : String.format("glBindVertexArray(%d) failed!", this.vaoId);

            CURRENT.put(Thread.currentThread(), this);
        }
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

    @FunctionalInterface
    private interface BindVertexArray {

        void call(int vaobj);
    }

    private static final BindVertexArray NULL_BIND_VERTEX_ARRAY = (vaobj) -> {
        throw new IllegalStateException("glBindVertexArray was called before being fetched! An instance of GLVertexArray.InitTask must run prior to calling glBindVertexArray.");
    };
    private BindVertexArray glBindVertexArray = NULL_BIND_VERTEX_ARRAY;

    @FunctionalInterface
    private interface GenVertexArrays {

        int get();
    }

    @FunctionalInterface
    private interface DeleteVertexArrays {

        void call(int vaobj);
    }

    private static final DeleteVertexArrays NULL_DELETE_VERTEX_ARRAYS = (vaobj) -> {
        throw new IllegalStateException("glDeleteVertexArrays was called before being fetched! An instance of GLVertexArray.InitTask must run prior to calling glDeleteVertexArrays.");
    };
    private DeleteVertexArrays glDeleteVertexArrays = NULL_DELETE_VERTEX_ARRAYS;

    private static final GenVertexArrays NULL_GEN_VERTEX_ARRAYS = () -> {
        throw new IllegalStateException("glGenVertexArrays was called before being fetched! An instance of GLVertexArray.InitTask must run prior to calling glGenVertexArrays.");
    };
    private GenVertexArrays glGenVertexArrays = NULL_GEN_VERTEX_ARRAYS;

    @FunctionalInterface
    private interface DrawArraysIndirect {

        void call(int mode, long offset);
    }

    private static final DrawArraysIndirect NULL_DRAW_ARRAYS_INDIRECT = (mode, offset) -> {
        throw new IllegalStateException("glDrawArraysIndirect was called before being fetched! An instance of GLVertexArray.InitTask must be run prior to calling glDrawArraysIndirect.");
    };
    private DrawArraysIndirect glDrawArraysIndirect = NULL_DRAW_ARRAYS_INDIRECT;

    @FunctionalInterface
    private interface DrawElementsIndirect {

        void call(int mode, int type, long offset);
    }

    private static final DrawElementsIndirect NULL_DRAW_ELEMENTS_INDIRECT = (mode, type, offset) -> {
        throw new IllegalStateException("glDrawElementsIndirect was called before being fetched! An instance of GLVertexArray.InitTask must run prior to calling glDrawElementsIndirect.");
    };
    private DrawElementsIndirect glDrawElementsIndirect = NULL_DRAW_ELEMENTS_INDIRECT;

    @FunctionalInterface
    private interface DrawElementsInstanced {

        void call(int drawMode, int count, int type, long offset, int instanceCount);
    }

    private static final DrawElementsInstanced NULL_DRAW_ELEMENTS_INSTANCED = (drawMode, count, type, offset, instanceCount) -> {
        throw new IllegalStateException("glDrawElementsInstanced was called before being feteched! An instance of GLVertexArray.InitTask must run prior to calling glDrawElementsInstanced.");
    };
    private DrawElementsInstanced glDrawElementsInstanced = NULL_DRAW_ELEMENTS_INSTANCED;

    @FunctionalInterface
    private interface DrawArraysInstanced {

        void call(int drawMode, int first, int count, int instanceCount);
    }

    private static final DrawArraysInstanced NULL_DRAW_ARRAYS_INSTANCED = (drawMode, first, count, instanceCount) -> {
        throw new IllegalStateException("glDrawArraysInstanced was called before being fetched! An instance of GLVertexArray.InitTask must run prior to calling glDrawArraysInstanced.");
    };
    private DrawArraysInstanced glDrawArraysInstanced = NULL_DRAW_ARRAYS_INSTANCED;

    @FunctionalInterface
    private interface DrawTransformFeedback {

        void call(int mode, int id);
    }

    private static final DrawTransformFeedback NULL_DRAW_TRANSFORM_FEEDBACK = (mode, id) -> {
        throw new IllegalStateException("glDrawTransformFeedback was called before being fetched! An instance of GLVertexArray.InitTask must run prior to calling glDrawTransformFeedback.");
    };
    private DrawTransformFeedback glDrawTransformFeedback = NULL_DRAW_TRANSFORM_FEEDBACK;

    @FunctionalInterface
    private interface VertexAttribLPointer {

        void call(int index, int size, int type, int stride, long offset);
    }
    private static final VertexAttribLPointer NULL_VERTEX_ATTRIBL_POINTER = (index, size, type, stride, offset) -> {
        throw new IllegalStateException("glVertexAttribLPointer was called before being fetched! An instance of GLVertexArray.InitTask must run prior to calling glVertexAttribLPointer.");
    };
    private VertexAttribLPointer glVertexAttribLPointer = NULL_VERTEX_ATTRIBL_POINTER;

    @FunctionalInterface
    private interface VertexAttribDivisor {

        void call(int attribIndex, int divisor);
    }

    private static final VertexAttribDivisor NULL_VERTEX_ATTRIB_DIVISOR = (attribIndex, divisor) -> {
        throw new IllegalStateException("glVertexAttribDivisor was called before being fetched! An instance of GLVertexArray.InitTask must run prior to calling glVertexAttribDivisor.");
    };
    private VertexAttribDivisor glVertexAttribDivisor = NULL_VERTEX_ATTRIB_DIVISOR;

    /**
     * A GLTask that initializes the parent GLVertexArray object.
     *
     * @since 15.06.05
     */
    public class InitTask extends GLTask {

        @Override
        public void run() {
            checkThread();
            
            if (GLVertexArray.this.isValid()) {
                throw new GLException("GLVertexArray is already initialized!");
            }

            final ContextCapabilities cap = GL.getCapabilities();

            if (cap.OpenGL30) {
                GLVertexArray.this.glBindVertexArray = GL30::glBindVertexArray;
                GLVertexArray.this.glGenVertexArrays = GL30::glGenVertexArrays;
                GLVertexArray.this.glDeleteVertexArrays = GL30::glDeleteVertexArrays;
            } else if (cap.GL_ARB_vertex_array_object) {
                GLVertexArray.this.glBindVertexArray = ARBVertexArrayObject::glBindVertexArray;
                GLVertexArray.this.glGenVertexArrays = ARBVertexArrayObject::glGenVertexArrays;
                GLVertexArray.this.glDeleteVertexArrays = ARBVertexArrayObject::glDeleteVertexArrays;
            } else {
                GLVertexArray.this.glBindVertexArray = (vaobj) -> {
                    throw new UnsupportedOperationException("glBindVertexArray is not supported! glBindVertexArray requires either an OpenGL 3.0 context or ARB_vertex_array_object.");
                };
                GLVertexArray.this.glGenVertexArrays = () -> {
                    throw new UnsupportedOperationException("glGenVertexArrays is not supported! glGenVertexArrays requires either an OpenGL 3.0 context or ARB_vertex_array_object.");
                };
                GLVertexArray.this.glDeleteVertexArrays = (vaobj) -> {
                    throw new UnsupportedOperationException("glDeleteVertexArrays is not supported! glDeleteVertexArrays requires either an OpenGL 3.0 context or ARB_vertex_array_object.");
                };
            }

            if (cap.OpenGL40) {
                GLVertexArray.this.glDrawArraysIndirect = GL40::glDrawArraysIndirect;
                GLVertexArray.this.glDrawElementsIndirect = GL40::glDrawElementsIndirect;
            } else if (cap.GL_ARB_draw_indirect) {
                GLVertexArray.this.glDrawArraysIndirect = ARBDrawIndirect::glDrawArraysIndirect;
                GLVertexArray.this.glDrawElementsIndirect = ARBDrawIndirect::glDrawElementsIndirect;
            } else {
                GLVertexArray.this.glDrawArraysIndirect = (mode, offset) -> {
                    throw new UnsupportedOperationException("glDrawArraysIndirec) is not supported! glDrawArraysIndirect requires either an OpenGL 4.0 context or ARB_draw_indirect.");
                };
                GLVertexArray.this.glDrawElementsIndirect = (mode, index, offset) -> {
                    throw new UnsupportedOperationException("glDrawElementsIndirect is not supported! glDrawElementsIndirect requires either an OpenGL 4.0 context or ARB_draw_indirect.");
                };
            }

            if (cap.OpenGL31) {
                GLVertexArray.this.glDrawElementsInstanced = GL31::glDrawElementsInstanced;
                GLVertexArray.this.glDrawArraysInstanced = GL31::glDrawArraysInstanced;
            } else if (cap.GL_ARB_draw_instanced) {
                GLVertexArray.this.glDrawElementsInstanced = ARBDrawInstanced::glDrawElementsInstancedARB;
                GLVertexArray.this.glDrawArraysInstanced = ARBDrawInstanced::glDrawArraysInstancedARB;
            } else {
                GLVertexArray.this.glDrawElementsInstanced = (drawMode, count, type, offset, instanceCount) -> {
                    final int currentProgram = GL11.glGetInteger(GL20.GL_CURRENT_PROGRAM);
                    assert GL11.glGetError() == GL11.GL_NO_ERROR : String.format("glGetInteger(GL_CURRENT_PROGRAM) = %d failed!", currentProgram);

                    final int uLoc = GL20.glGetUniformLocation(currentProgram, "gl_InstanceID");
                    assert GL11.glGetError() == GL11.GL_NO_ERROR : String.format("glGetUniformLocation(%d, gl_InstanceID) = %d failed!", currentProgram, uLoc);

                    for (int i = 0; i < instanceCount; i++) {
                        GL20.glUniform1i(uLoc, i);
                        assert GL11.glGetError() == GL11.GL_NO_ERROR : String.format("glUniform1i(%d, %d) failed!", uLoc, i);

                        GL11.glDrawElements(drawMode, count, type, offset);
                        assert GL11.glGetError() == GL11.GL_NO_ERROR : String.format("glDrawElements(%d, %d, %d, %d) failed!", drawMode, count, type, offset);
                    }
                };

                GLVertexArray.this.glDrawArraysInstanced = (drawMode, first, count, instanceCount) -> {
                    final int currentProgram = GL11.glGetInteger(GL20.GL_CURRENT_PROGRAM);
                    assert GL11.glGetError() == GL11.GL_NO_ERROR : String.format("glGetInteger(GL_CURRENT_PROGRAM) = %d failed!", currentProgram);

                    final int uLoc = GL20.glGetUniformLocation(currentProgram, "gl_InstanceID");
                    assert GL11.glGetError() == GL11.GL_NO_ERROR : String.format("glGetUniformLocation(%d, gl_InstanceID) = %d failed!", currentProgram, uLoc);

                    for (int i = 0; i < instanceCount; i++) {
                        GL20.glUniform1i(uLoc, i);
                        assert GL11.glGetError() == GL11.GL_NO_ERROR : String.format("glUniform1i(%d, %d) failed!", uLoc, i);

                        GL11.glDrawArrays(drawMode, first, count);
                        assert GL11.glGetError() == GL11.GL_NO_ERROR : String.format("glDrawArrays(%d, %d, %d) failed!", drawMode, first, count);
                    }
                };
            }

            if (cap.OpenGL40) {
                GLVertexArray.this.glDrawTransformFeedback = GL40::glDrawTransformFeedback;
            } else if (cap.GL_ARB_transform_feedback2) {
                GLVertexArray.this.glDrawTransformFeedback = ARBTransformFeedback2::glDrawTransformFeedback;
            } else {
                GLVertexArray.this.glDrawTransformFeedback = (mode, id) -> {
                    throw new UnsupportedOperationException("glDrawTransformFeedback is not supported! glDrawTransformFeedback requires either an OpenGL 4.0 context or ARB_transform_feedback2.");
                };
            }

            if (cap.OpenGL41) {
                GLVertexArray.this.glVertexAttribLPointer = GL41::glVertexAttribLPointer;
            } else if (cap.GL_ARB_vertex_attrib_64bit) {
                GLVertexArray.this.glVertexAttribLPointer = ARBVertexAttrib64Bit::glVertexAttribLPointer;
            } else {
                GLVertexArray.this.glVertexAttribLPointer = (index, size, type, stride, offset) -> {
                    throw new UnsupportedOperationException("glVertexAttribLPointer is not supported! glVertexAttribLPointer requires OpenGL 4.1 or ARB_vertex_attrib_64bit.");
                };
            }

            if (cap.OpenGL33) {
                GLVertexArray.this.glVertexAttribDivisor = GL33::glVertexAttribDivisor;
            } else if (cap.GL_ARB_instanced_arrays) {
                GLVertexArray.this.glVertexAttribDivisor = ARBInstancedArrays::glVertexAttribDivisorARB;
            } else {
                GLVertexArray.this.glVertexAttribDivisor = (index, divisor) -> {
                    throw new UnsupportedOperationException("glVertexAttribDivisor is not supported! glVertexAttribDivisor requires OpenGL 3.3 or ARB_instanecd_arrays.");
                };
            }

            GLVertexArray.this.vaoId = GLVertexArray.this.glGenVertexArrays.get();
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
     * @throws GLException if the vertex array or indirect command buffer is
     * invalid.
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
            checkThread();
            
            if (!GLVertexArray.this.isValid()) {
                throw new GLException("Invalid GLVertexArray!");
            } else if (!this.indirectCommandBuffer.isValid()) {
                throw new GLException("Invalid GLBuffer!");
            }

            GLVertexArray.this.bind();

            GL15.glBindBuffer(GLBufferTarget.GL_DRAW_INDIRECT_BUFFER.value, this.indirectCommandBuffer.bufferId);
            assert checkGLError() : glErrorMsg("glBindBuffer(II)", "GL_DRAW_INDIRECT_BUFFER", this.indirectCommandBuffer.bufferId);

            GLVertexArray.this.glDrawElementsIndirect.call(this.drawMode.value, this.indexType.value, this.offset);
            assert checkGLError() : glErrorMsg("glDrawElementsIndirect(IIL)", this.drawMode, this.indexType, this.offset);
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
     * @throws GLException if the vertex array or indirect command buffer is
     * invalid.
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

            Objects.requireNonNull(this.indirectCommandBuffer = indirectCommandBuffer);
            Objects.requireNonNull(this.drawMode = drawMode);

            if ((this.offset = offset) < 0) {
                throw new GLException("Offset cannot be less than 0!");
            }
        }

        @Override
        public void run() {
            checkThread();
            
            if (!GLVertexArray.this.isValid()) {
                throw new GLException("Invalid GLVertexArray!");
            } else if (!this.indirectCommandBuffer.isValid()) {
                throw new GLException("Invalid GLBuffer!");
            }

            GLVertexArray.this.bind();

            GL15.glBindBuffer(GLBufferTarget.GL_DRAW_INDIRECT_BUFFER.value, this.indirectCommandBuffer.bufferId);
            assert checkGLError() : glErrorMsg("glBindBuffer(II)", "GL_DRAW_INDIRECT_BUFFER", this.indirectCommandBuffer.bufferId);

            GLVertexArray.this.glDrawArraysIndirect.call(this.drawMode.value, this.offset);
            assert checkGLError() : glErrorMsg("glDrawArraysIndirect(IL)", this.drawMode, this.offset);
        }
    }

    private MultiDrawArraysTask lastMultiDrawArrays = null;

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

        @Override
        public void run() {
            checkThread();
            
            if (!GLVertexArray.this.isValid()) {
                throw new GLException("Invalid GLVertexArray!");
            }

            GLVertexArray.this.bind();
            GL14.glMultiDrawArrays(this.drawMode.value, this.first, this.count);
            assert checkGLError() : glErrorMsg("glMultiDrawArrays(I*I)", this.drawMode, toHexString(memAddress(this.first)), this.count);
        }
    }

    private DrawElementsInstancedTask lastDrawElementsInstanced = null;

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
            checkThread();
            
            if (!GLVertexArray.this.isValid()) {
                throw new GLException("Invalid GLVertexArray!");
            }

            GLVertexArray.this.bind();

            GLVertexArray.this.glDrawElementsInstanced.call(this.drawMode.value, this.count, this.type.value, this.offset, this.instanceCount);
            assert checkGLError() : glErrorMsg("glDrawElementsInstanced(IIILI)", this.drawMode, this.count, this.type, this.offset, this.instanceCount);
        }

    }

    private DrawArraysInstancedTask lastDrawArraysInstanced = null;

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

    /**
     * A GLTask that runs a draw arrays instanced task.
     *
     * @since 15.06.24
     */
    public class DrawArraysInstancedTask extends GLTask implements GLDrawTask {

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
            checkThread();
            
            if (!GLVertexArray.this.isValid()) {
                throw new GLException("GLVertexArray is not valid!");
            }

            GLVertexArray.this.bind();

            GLVertexArray.this.glDrawArraysInstanced.call(this.mode.value, this.first, this.count, this.instanceCount);
            assert checkGLError() : glErrorMsg("glDrawArraysInstanced(IIII)", this.mode, this.first, this.count, this.instanceCount);
        }
    }

    private DrawElementsTask lastDrawElements = null;

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
            checkThread();
            
            if (!GLVertexArray.this.isValid()) {
                throw new GLException("Invalid GLVertex!");
            }

            GLVertexArray.this.bind();

            GL11.glDrawElements(this.mode.value, this.count, this.type.value, this.offset);
            assert checkGLError() : glErrorMsg("glDrawElements(IIIL)", this.mode, this.count, this.type, this.offset);
        }
    }

    private DrawArraysTask lastDrawArrays = null;

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
            checkThread();
            
            if (!GLVertexArray.this.isValid()) {
                throw new GLException("Invalid GLVertexArray!");
            }

            GLVertexArray.this.bind();
            GLVertexArray.this.glDrawTransformFeedback.call(this.mode.value, this.tfb.tfbId);
            assert checkGLError() : glErrorMsg("glDrawTransformFeedback(II)", this.mode, this.tfb.tfbId);
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
            checkThread();
            
            if (!GLVertexArray.this.isValid()) {
                throw new GLException("GLVertexArray is not valid!");
            }

            GLVertexArray.this.bind();

            GL11.glDrawArrays(this.mode.value, this.start, this.count);
            assert checkGLError() : glErrorMsg("glDrawArrays(III)", this.mode, this.start, this.count);
        }

    }

    private final DeleteTask deleteTask = new DeleteTask();

    public void delete() {
        this.deleteTask.glRun(this.getThread());
    }

    public class DeleteTask extends GLTask {

        @Override
        public void run() {
            checkThread();
            
            if (GLVertexArray.this.isValid()) {
                GLVertexArray.this.glDeleteVertexArrays.call(GLVertexArray.this.vaoId);
                assert checkGLError() : glErrorMsg("glDeleteVertexArrays(I)", GLVertexArray.this.vaoId);

                GLVertexArray.this.vaoId = INVALID_VERTEX_ARRAY_ID;
                GLVertexArray.this.glBindVertexArray = NULL_BIND_VERTEX_ARRAY;
                GLVertexArray.this.glDeleteVertexArrays = NULL_DELETE_VERTEX_ARRAYS;
                GLVertexArray.this.glDrawArraysIndirect = NULL_DRAW_ARRAYS_INDIRECT;
                GLVertexArray.this.glDrawArraysInstanced = NULL_DRAW_ARRAYS_INSTANCED;
                GLVertexArray.this.glDrawElementsIndirect = NULL_DRAW_ELEMENTS_INDIRECT;
                GLVertexArray.this.glDrawElementsInstanced = NULL_DRAW_ELEMENTS_INSTANCED;
                GLVertexArray.this.glDrawTransformFeedback = NULL_DRAW_TRANSFORM_FEEDBACK;
                GLVertexArray.this.glGenVertexArrays = NULL_GEN_VERTEX_ARRAYS;
                GLVertexArray.this.glVertexAttribLPointer = NULL_VERTEX_ATTRIBL_POINTER;
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

            GL15.glBindBuffer(GLBufferTarget.GL_ELEMENT_ARRAY_BUFFER.value, this.buffer.bufferId);
            assert checkGLError() : glErrorMsg("glBindBuffer(II)", "GL_ELEMENT_ARRAY_BUFFER", this.buffer.bufferId);
        }
    }

    public void attachBuffer(
            final int index, final GLBuffer buffer,
            final GLVertexAttributeType type, final GLVertexAttributeSize size) {

        new AttachBufferTask(index, buffer, type, size).glRun(this.getThread());
    }

    public void attachBuffer(
            final int index, final GLBuffer buffer,
            final GLVertexAttributeType type, final GLVertexAttributeSize size,
            final int offset, final int stride) {

        new AttachBufferTask(index, buffer, type, size, false, stride, offset).glRun(this.getThread());
    }

    public void attachBuffer(
            final int index, final GLBuffer buffer,
            final GLVertexAttributeType type, final GLVertexAttributeSize size,
            final int offset, final int stride,
            final int divisor) {

        new AttachBufferTask(index, buffer, type, size, false, stride, offset, divisor).glRun(this.getThread());
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

            Objects.requireNonNull(this.size = size);

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
            checkThread();
            
            if (!GLVertexArray.this.isValid()) {
                throw new GLException("Invalid GLVertexArray!");
            }

            if (!buffer.isValid()) {
                throw new GLException("Invalid GLBuffer!");
            }

            // bind the VAO if it isn't bound already.
            GLVertexArray.this.bind();

            GL15.glBindBuffer(GLBufferTarget.GL_ARRAY_BUFFER.value, this.buffer.bufferId);
            assert checkGLError() : glErrorMsg("glBindBuffer(II)", "GL_ARRAY_BUFFER", this.buffer.bufferId);

            GL20.glEnableVertexAttribArray(this.index);
            assert checkGLError() : glErrorMsg("glEnableVertexAttribArray(I)", this.index);

            if (this.type == GLVertexAttributeType.GL_DOUBLE) {
                GLVertexArray.this.glVertexAttribLPointer.call(this.index, this.size.value, this.type.value, this.stride, this.offset);
                assert checkGLError() : glErrorMsg("glVertexAttribLPointer(IIIIL)", this.index, this.size, this.type, this.stride, this.offset);
            } else {
                GL20.glVertexAttribPointer(this.index, this.size.value, this.type.value, this.normalized, this.stride, this.offset);
                assert checkGLError() : glErrorMsg("glVertexAttribPointer(IIIBIL)", this.index, this.size, this.type, this.normalized, this.stride, this.offset);
            }

            if (this.divisor > 0) {
                GLVertexArray.this.glVertexAttribDivisor.call(this.index, this.divisor);
                assert checkGLError() : glErrorMsg("glVertexAttribDivisor(II)", this.index, this.divisor);
            }
        }
    }
    
    @Override
    public final boolean isShareable() {
        return false;
    }
}
