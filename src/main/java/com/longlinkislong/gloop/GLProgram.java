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

import com.longlinkislong.gloop.glspi.Driver;
import com.longlinkislong.gloop.glspi.Program;
import com.longlinkislong.gloop.glspi.Shader;
import java.nio.ByteOrder;
import java.nio.DoubleBuffer;
import java.nio.FloatBuffer;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Objects;
import java.util.OptionalInt;
import java.util.Set;
import java.util.stream.Collectors;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.Marker;
import org.slf4j.MarkerFactory;

/**
 * A GLObject that represents an OpenGL shader program.
 *
 * @author zmichaels
 * @since 15.05.27
 */
public class GLProgram extends GLObject {

    private static final Marker GL_MARKER = MarkerFactory.getMarker("GLOOP");
    private static final Logger LOGGER = LoggerFactory.getLogger("GLProgram");

    private transient volatile Program program;
    private final Map<String, Integer> uniforms = new HashMap<>(32);
    private String name = "";

    /**
     * Assigns a human-readable name to the GLProgram.
     *
     * @param name the name.
     * @since 15.12.18
     */
    public final void setName(final CharSequence name) {
        GLTask.create(() -> {
            LOGGER.trace(
                    GL_MARKER,
                    "Renamed GLProgram[{}] to GLProgram[{}]",
                    this.name,
                    name);

            this.name = name.toString();
        }).glRun(this.getThread());
    }

    /**
     * Retrieves the name of the GLProgram.
     *
     * @return the name.
     * @since 15.12.18
     */
    public final String getName() {
        return this.name;
    }

    /**
     * Constructs a new GLProgram using the default GLThread.
     *
     * @since 15.05.27
     */
    public GLProgram() {
        this(GLThread.getAny());
    }

    /**
     * Constructs a new GLProgram using the specified thread as the parent
     * thread.
     *
     * @param thread the thread to create the GLProgram on.
     * @since 15.05.27
     */
    public GLProgram(final GLThread thread) {
        super(thread);

        LOGGER.trace(
                GL_MARKER,
                "Constructed GLProgram object on thread: {}",
                thread);

        this.init();
    }

    /**
     * Checks if the GLProgram is valid. A GLProgram is considered valid after
     * it is created and before it is deleted.
     *
     * @return true if the GLProgram is valid.
     * @since 15.05.27
     */
    public boolean isValid() {
        return program != null && program.isValid();
    }

    /**
     * Binds the current program to the current thread.
     *
     * @since 15.05.27
     */
    public void use() {
        new UseTask().glRun(this.getThread());
    }

    /**
     * GLTask that binds the GLProgram.
     *
     * @since 15.12.18
     */
    public class UseTask extends GLTask {
        GLProgram program = GLProgram.this;
        
        @SuppressWarnings("unchecked")
        @Override
        public void run() {
            LOGGER.trace(GL_MARKER, "############### Start GLProgram Use Task ###############");
            LOGGER.trace(GL_MARKER, "\tUsing GLProgram[{}]", GLProgram.this.getName());

            if (!GLProgram.this.isValid()) {
                throw new GLException("Invalid GLProgram!");
            }

            GLThread.getCurrent().orElseThrow(GLException::new).currentProgramUse = this;

            GLTools.getDriverInstance().programUse(GLProgram.this.program);
            GLProgram.this.program.updateTime();
            LOGGER.trace(GL_MARKER, "############### End GLProgram Use Task ###############");
        }
    }

    @SuppressWarnings("unchecked")
    private int getUniformLoc(final String uName) {
        if (this.uniforms.containsKey(uName)) {
            return this.uniforms.get(uName);
        } else {
            final int uLoc = GLTools.getDriverInstance().programGetUniformLocation(program, uName);

            this.uniforms.put(uName, uLoc);

            return uLoc;
        }
    }

    /**
     * Registers all VertexAttribute properties. This should be called before
     * the link task is executed.
     *
     * @param attrib the VertexAttributes to set.
     * @since 15.05.27
     */
    public void setVertexAttributes(final GLVertexAttributes attrib) {
        new SetVertexAttributesTask(attrib).glRun(this.getThread());
    }

    /**
     * A GLTask that sets the VertexAttributes for the GLProgram.
     *
     * @since 15.05.27
     */
    public class SetVertexAttributesTask extends GLTask {

        private final GLVertexAttributes attribs;

        /**
         * Constructs a new SetVertexAttributes task with the specified
         * GLVertexAttributes.
         *
         * @param attrib the attributes to set.
         * @since 15.05.27
         */
        public SetVertexAttributesTask(final GLVertexAttributes attrib) {
            this.attribs = Objects.requireNonNull(attrib);
        }

        @SuppressWarnings({"rawtypes", "unchecked"})
        @Override
        public void run() {
            LOGGER.trace(GL_MARKER, "############### Start GLProgram Set Vertex Attributes Task ###############");
            LOGGER.trace(GL_MARKER, "\tSetting attributes for GLProgram[{}]", getName());
            LOGGER.trace(GL_MARKER, "\tAttributes: GLVertexAttributes[{}]", attribs.getName());

            final Driver driver = GLTools.getDriverInstance();

            if (!GLProgram.this.isValid()) {
                throw new GLException("Invalid GLProgram!");
            } else {
                this.attribs.nameMap.forEach((name, index) -> {
                    LOGGER.trace(
                            GL_MARKER,
                            "GLProgram[{}].attrib[{}] = {}",
                            GLProgram.this.name,
                            index,
                            name);

                    driver.programSetAttribLocation(program, index, name);
                });

                final Set<String> varyingSet = this.attribs.feedbackVaryings;

                if (!varyingSet.isEmpty()) {
                    final String[] varyings = new String[varyingSet.size()];
                    final Iterator<String> it = varyingSet.iterator();

                    for (int i = 0; i < varyingSet.size(); i++) {
                        varyings[i] = it.next();
                        LOGGER.trace(
                                GL_MARKER,
                                "GLProgram[{}].varying[{}] = {}",
                                GLProgram.this.name,
                                i,
                                varyings[i]);
                    }

                    driver.programSetFeedbackVaryings(program, varyings);
                }
            }

            GLProgram.this.program.updateTime();

            LOGGER.trace(
                    GL_MARKER,
                    "############### End GLProgram Set Vertex Attributes Task ###############");
        }
    }

    /**
     * Sets a uniform double matrix at the specified uniform attribute.
     *
     * @param uName the name of the attribute.
     * @param mat the matrix to set
     * @since 15.05.27
     */
    public void setUniformMatrixD(
            final CharSequence uName, final GLMat<?, ?> mat) {

        new SetUniformMatrixDTask(uName, mat).glRun(this.getThread());
    }

    /**
     * Sets a uniform float matrix at the specified uniform attribute.
     *
     * @param uName the name of the attribute.
     * @param mat the matrix to set.
     * @since 15.05.27
     */
    public void setUniformMatrixF(
            final CharSequence uName, final GLMat<?, ?> mat) {

        new SetUniformMatrixFTask(uName, mat).glRun(this.getThread());
    }

    /**
     * A GLTask that sets a uniform double matrix.
     *
     * @since 15.05.27
     */
    public final class SetUniformMatrixDTask extends GLTask {

        private final String uName;
        private final double[] values;
        private final int count;

        /**
         * Changes the matrix held by the SetUniformMatrixDTask.
         *
         * @param mat the new matrix.
         * @return self reference.
         * @since 15.12.18
         */
        public SetUniformMatrixDTask set(final GLMat<?, ?> mat) {
            if (mat.size() * mat.size() != this.count) {
                throw new ArrayIndexOutOfBoundsException("Invalid matrix size!");
            }

            final GLMatD<?, ?> matD = mat.asGLMatD();

            return this.set(matD.data(), matD.offset(), this.count);
        }

        /**
         * Sets the individual elements of the uniform matrix.
         *
         * @param data the elements.
         * @return self reference.
         */
        public SetUniformMatrixDTask set(final double... data) {
            return this.set(data, 0, data.length);
        }

        /**
         * Sets the elements of the uniform matrix to the specified array.
         *
         * @param data the array to read data from.
         * @param offset the offset from where to start reading from the array.
         * @param length the number of elements to read.
         * @return self reference.
         * @since 15.12.18
         */
        public SetUniformMatrixDTask set(
                final double[] data, final int offset, final int length) {

            if (length != this.count) {
                throw new ArrayIndexOutOfBoundsException();
            }

            System.arraycopy(data, offset, this.values, 0, length);
            return this;
        }

        /**
         * Constructs a new SetUniformMatrixDTask with the specified GLMat.
         *
         * @param uName the name of the uniform to set.
         * @param mat the matrix to set
         * @since 15.05.27
         */
        public SetUniformMatrixDTask(
                final CharSequence uName, final GLMat<?, ?> mat) {

            final int sz = mat.size();

            if (!(sz == 2 || sz == 3 || sz == 4)) {
                throw new GLException("Invalid uniform count!");
            }

            final GLMatD<?, ?> matD = mat.asGLMatD();

            this.uName = uName.toString();
            this.count = sz * sz;
            this.values = new double[this.count];

            System.arraycopy(
                    matD.data(), matD.offset(),
                    this.values, 0,
                    this.count);
        }

        /**
         * Constructs a new SetUniformMatrixDTask with the specified double
         * array.
         *
         * @param uName the name of the uniform to set.
         * @param data the data to set.
         * @param offset the offset to start reading from the data.
         * @param length the number of elements to set. Must be either 4 for a
         * 2x2 matrix, 9 for a 3x3 matrix, or 16 for a 4x4 matrix.
         * @since 15.05.27
         */
        public SetUniformMatrixDTask(
                final CharSequence uName,
                final double[] data, final int offset, final int length) {

            if (!(length == 4 || length == 9 || length == 16)) {
                throw new GLException("Invalid uniform count!");
            }

            this.uName = uName.toString();
            this.values = new double[length];
            this.count = length;

            System.arraycopy(data, offset, this.values, 0, length);
        }

        @SuppressWarnings("unchecked")
        @Override
        public void run() {
            LOGGER.trace(GL_MARKER, "############### Start GLProgram Set Uniform MatrixD Task ###############");
            LOGGER.trace(GL_MARKER, "\tSetting uniform matrix of GLProgram[{}]", getName());
            LOGGER.trace(GL_MARKER, "\tUniform name: {}", this.uName);

            if (!GLProgram.this.isValid()) {
                throw new GLException("GLProgram is not valid!");
            }

            final int uLoc = GLProgram.this.getUniformLoc(uName);
            final DoubleBuffer buffer = NativeTools.getInstance()
                    .nextOVWord()
                    .order(ByteOrder.nativeOrder())
                    .asDoubleBuffer();

            buffer.put(this.values).flip();

            GLTools.getDriverInstance().programSetUniformMatD(program, uLoc, buffer);
            GLProgram.this.program.updateTime();
            LOGGER.trace(GL_MARKER, "############### End GLProgram Set Uniform MatrixD Task ###############");
        }
    }

    /**
     * A GLTask that sets a uniform float matrix.
     *
     * @since 15.05.27
     */
    public class SetUniformMatrixFTask extends GLTask {

        private final String uName;
        private final float[] values;
        private final int count;

        /**
         * Sets the uniform matrix to the new matrix.
         *
         * @param mat the new matrix.
         * @return self reference.
         * @since 15.12.18
         */
        public final SetUniformMatrixFTask set(final GLMat<?, ?> mat) {
            if (mat.size() * mat.size() != this.count) {
                throw new ArrayIndexOutOfBoundsException("Invalid matrix size!");
            }

            final GLMatF<?, ?> matF = mat.asGLMatF();

            return this.set(matF.data(), matF.offset(), this.count);
        }

        /**
         * Sets the uniform matrix elements.
         *
         * @param data the elements to set.
         * @return self reference.
         * @since 15.12.18
         */
        public final SetUniformMatrixFTask set(final float... data) {
            return this.set(data, 0, data.length);
        }

        /**
         * Sets the uniform matrix elements from an array.
         *
         * @param data the array to read the data from.
         * @param offset the offset to start reading from the array.
         * @param length the number of elements to read.
         * @return self reference.
         * @since 15.12.18
         */
        public final SetUniformMatrixFTask set(
                final float[] data, final int offset, final int length) {

            if (length != count) {
                throw new ArrayIndexOutOfBoundsException();
            }

            System.arraycopy(data, offset, this.values, 0, length);

            return this;
        }

        /**
         * Constructs a new UniformMatrixTask using the supplied GLMat. The
         * values set will be the values of the GLMat on task creation.
         *
         * @param uName the name of the uniform to use.
         * @param mat the matrix to set
         * @since 15.05.27
         */
        public SetUniformMatrixFTask(
                final CharSequence uName, final GLMat<?, ?> mat) {

            final int sz = mat.size();

            if (!(sz == 2 || sz == 3 || sz == 4)) {
                throw new GLException("Invalid uniform count!");
            }

            final GLMatF<?, ?> mf = mat.asGLMatF();

            this.uName = uName.toString();
            this.count = sz * sz;
            this.values = new float[this.count];

            System.arraycopy(mf.data(), mf.offset(), this.values, 0, this.count);
        }

        /**
         * Constructs a new UniformMatrixTask using the supplied float array.
         * The values set will be the values on task creation.
         *
         * @param uName the name of the uniform to use.
         * @param values the matrix to set.
         * @param offset the offset to start reading from the matrix.
         * @param length the number of elements to use for the matrix. 4
         * elements defines a 2x2 matrix, 9 for a 3x3, and 16 for a 4x4.
         * @since 15.05.27
         */
        public SetUniformMatrixFTask(
                final CharSequence uName,
                final float[] values, final int offset, final int length) {

            if (!(length == 4 || length == 9 || length == 16)) {
                throw new GLException("Invalid uniform count!");
            }

            this.uName = uName.toString();
            this.count = length;
            this.values = new float[length];

            System.arraycopy(values, offset, this.values, 0, length);
        }

        @SuppressWarnings("unchecked")
        @Override
        public void run() {
            LOGGER.trace(GL_MARKER, "############### Start GLProgram Set Uniform MatrixF Task ###############");
            LOGGER.trace(GL_MARKER, "\tSetting uniform matrix for GLProgram[{}]", getName());
            LOGGER.trace(GL_MARKER, "\tUniform matrix location: {}", this.uName);

            if (!GLProgram.this.isValid()) {
                throw new GLException("GLProgram is not valid!");
            }

            final int uLoc = GLProgram.this.getUniformLoc(this.uName);
            final FloatBuffer buffer = NativeTools.getInstance()
                    .nextQVWord()
                    .order(ByteOrder.nativeOrder())
                    .asFloatBuffer();

            buffer.put(this.values).flip();

            GLTools.getDriverInstance().programSetUniformMatF(program, uLoc, buffer);
            GLProgram.this.program.updateTime();
            LOGGER.trace(GL_MARKER, "############### End GLProgram Set Uniform MatrixF Task ###############");
        }
    }

    /**
     * Sets a double uniform from the supplied GLVec. The GLVec must be of size
     * 1, 2, 3, or 4.
     *
     * @param uName the name of the uniform to set.
     * @param vec the vector to set
     * @since 15.05.27
     */
    public void setUniformD(final CharSequence uName, final GLVec<?> vec) {
        new SetUniformDTask(uName, vec).glRun(this.getThread());
    }

    /**
     * Sets a double uniform from the supplied double array.
     *
     * @param uName the name of the uniform to set.
     * @param values the data to read the uniform values from.
     * @param offset the offset to start the read.
     * @param length the number of elements to read. Must be 1, 2, 3, or 4.
     * @since 15.05.27
     */
    public void setUniformD(final CharSequence uName,
            final double[] values, final int offset, final int length) {

        new SetUniformDTask(uName, values, offset, length)
                .glRun(this.getThread());
    }

    /**
     * Sets a double uniform to the series of double values.
     *
     * @param uName the uniform to set.
     * @param values the values to set. At least one value and up to 4 values
     * must be defined.
     * @since 15.05.27
     */
    public void setUniformD(final CharSequence uName, final double... values) {
        new SetUniformDTask(uName, values).glRun(this.getThread());
    }

    /**
     * A GLTask that sets a uniform double.
     *
     * @since 15.05.27
     */
    public class SetUniformDTask extends GLTask {

        private final String uName;
        private final double[] values;
        private final int count;

        /**
         * Sets the uniform vector.
         *
         * @param vec the new vector.
         * @return self reference.
         * @since 15.12.18
         */
        public final SetUniformDTask set(final GLVec<?> vec) {
            if (vec.size() != this.count) {
                throw new ArrayIndexOutOfBoundsException("Invalid vector size!");
            }

            final GLVecD<?> vecD = vec.asGLVecD();

            return this.set(vecD.data(), vecD.offset(), vecD.size());
        }

        /**
         * Sets the uniform vector from the elements specified.
         *
         * @param data the elements.
         * @return self reference.
         * @since 15.12.18
         */
        public final SetUniformDTask set(final double... data) {
            return this.set(data, 0, data.length);
        }

        /**
         * Sets the uniform vector from elements read from an array.
         *
         * @param data the array to read elements from.
         * @param offset the offset to start reading from the array.
         * @param length the number of elements to read.
         * @return self reference.
         * @since 15.12.18
         */
        public final SetUniformDTask set(
                final double[] data, final int offset, final int length) {

            if (this.count != length) {
                throw new ArrayIndexOutOfBoundsException();
            }

            System.arraycopy(data, offset, this.values, 0, length);
            return this;
        }

        /**
         * Constructs a new SetUniformDTask using a GLVec. The values of the
         * GLVec are copied on task creation.
         *
         * @param uName the name of the uniform.
         * @param v the vector. Must be of size 1, 2, 3, or 4.
         * @since 15.05.27.
         */
        public SetUniformDTask(final CharSequence uName, final GLVec<?> v) {

            final int sz = v.size();

            if (sz < 1 || sz > 4) {
                throw new GLException("Invalid uniform count!");
            }

            this.values = new double[sz];
            this.uName = uName.toString();
            this.count = sz;

            final GLVecD<?> vd = v.asGLVecD();

            System.arraycopy(vd.data(), vd.offset(), this.values, 0, this.count);
        }

        /**
         * Constructs a new SetUniformDTask using a series of doubles. These
         * doubles are copied on task creation.
         *
         * @param uName the name of the uniform.
         * @param data the uniform data. At least one value and up to four
         * values must be defined.
         * @since 15.05.27
         */
        public SetUniformDTask(final CharSequence uName, final double... data) {
            this(uName, data, 0, data.length);
        }

        /**
         * Constructs a new SetUniformDTask using an array of doubles.
         *
         * @param uName the name of the uniform.
         * @param data the uniform data.
         * @param offset the offset to start reading from the data.
         * @param length the number of values to read. Must be either 1, 2, 3,
         * or 4.
         * @since 15.05.27
         */
        public SetUniformDTask(final CharSequence uName,
                final double[] data, final int offset, final int length) {

            if (length < 1 || length > 4) {
                throw new GLException("Invalid uniform count!");
            }

            this.values = new double[length];
            System.arraycopy(data, offset, this.values, 0, length);
            this.uName = uName.toString();
            this.count = length;
        }

        @SuppressWarnings("unchecked")
        @Override
        public void run() {
            LOGGER.trace(GL_MARKER, "############### Start GLProgram Set UniformF Task ###############");
            LOGGER.trace(GL_MARKER, "\tSetting uniform of GLProgram[{}]", getName());
            LOGGER.trace(GL_MARKER, "\tUniform location: {}", this.uName);

            if (!GLProgram.this.isValid()) {
                throw new GLException("GLProgram is not valid!");
            }

            final int uLoc = GLProgram.this.getUniformLoc(this.uName);

            GLProgram.this.program.updateTime();
            GLTools.getDriverInstance().programSetUniformD(program, uLoc, values);
            LOGGER.trace(GL_MARKER, "############### End GLProgram Set UniformF Task ###############");
        }
    }

    /**
     * Sets a uniform integer to values read from an array.
     *
     * @param uName the name of the uniform to set.
     * @param data the data to read
     * @param offset the offset to start reading.
     * @param length the number of elements to read. Must be 1, 2, 3, or 4.
     * @since 15.05.27
     */
    public void setUniformI(
            final CharSequence uName,
            final int[] data, final int offset, final int length) {

        new SetUniformITask(uName, data, offset, length).glRun(this.getThread());
    }

    /**
     * Sets a uniform integer to a series of values.
     *
     * @param uName the name of the uniform to set.
     * @param data the series of data to read. At least one and up to four
     * values must be defined.
     * @since 15.05.27
     */
    public void setUniformI(final CharSequence uName, final int... data) {
        new SetUniformITask(uName, data).glRun(this.getThread());
    }

    /**
     * A GLTask that sets an integer uniform.
     *
     * @since 15.05.27
     */
    public final class SetUniformITask extends GLTask {

        private final String uName;
        private final int[] values;
        private final int count;

        /**
         * Sets the uniform vector from elements.
         *
         * @param data the integer elements to assign.
         * @return self reference.
         * @since 15.12.18
         */
        public SetUniformITask set(final int... data) {
            return this.set(data, 0, data.length);
        }

        /**
         * Sets the uniform vector from elements read from an array.
         *
         * @param data the array to read the elements from.
         * @param offset the offset to begin reading elements.
         * @param length the number of elements to read.
         * @return self reference.
         * @since 15.12.18
         */
        public SetUniformITask set(
                final int[] data, final int offset, final int length) {

            if (this.count != length) {
                throw new ArrayIndexOutOfBoundsException();
            }

            System.arraycopy(data, offset, this.values, 0, length);
            return this;
        }

        /**
         * Constructs a new SetUniformITask using the series of integers.
         *
         * @param uName the name of the uniform to set.
         * @param data the series of data to set.
         * @since 15.05.27
         */
        public SetUniformITask(
                final CharSequence uName, final int... data) {

            this(uName, data, 0, data.length);
        }

        /**
         * Constructs a new SetUniformITask using an array of data to read from.
         *
         * @param uName the name of the uniform to set
         * @param data the array to read data from.
         * @param offset the offset to start reading data.
         * @param length the number of elements to read. Must be 1, 2, 3, or 4.
         * @since 15.05.27
         */
        public SetUniformITask(
                final CharSequence uName,
                final int[] data, final int offset, final int length) {

            if (length < 1 || length > 4) {
                throw new GLException("Invalid uniform count!");
            }

            this.values = new int[length];
            System.arraycopy(data, offset, this.values, 0, length);
            this.count = length;
            this.uName = uName.toString();
        }

        @SuppressWarnings("unchecked")
        @Override
        public void run() {
            LOGGER.trace(GL_MARKER, "############### Start GLProgram Set UniformI Task ###############");
            LOGGER.trace(GL_MARKER, "\tSetting uniform of GLProgram[{}]", getName());
            LOGGER.trace(GL_MARKER, "\tUniform location: {}", this.uName);

            if (!GLProgram.this.isValid()) {
                throw new GLException("GLProgram is not valid!");
            }

            final int uLoc = GLProgram.this.getUniformLoc(this.uName);

            GLTools.getDriverInstance().programSetUniformI(program, uLoc, values);
            GLProgram.this.program.updateTime();
            LOGGER.trace(GL_MARKER, "############### End GLProgram Set UniformI Task ###############");
        }
    }

    /**
     * Sets a float uniform to values read from a GLVec.
     *
     * @param uName the name of the uniform to set
     * @param vec the vector to read the uniform data from. Must be of size 1,
     * 2, 3, or 4.
     * @since 15.05.27
     */
    public void setUniformF(final CharSequence uName, final GLVec<?> vec) {
        new SetUniformFTask(uName, vec).glRun(this.getThread());
    }

    /**
     * Sets a float uniform to values read from an array of floats.
     *
     * @param uName the name of the uniform to set.
     * @param data the array to read values from.
     * @param offset the offset to start reading values from.
     * @param length the number of values to read. Must be 1, 2, 3, or 4.
     * @since 15.05.27
     */
    public void setUniformF(
            final CharSequence uName,
            final float[] data, final int offset, final int length) {

        new SetUniformFTask(uName, data, offset, length).glRun(this.getThread());
    }

    /**
     * Sets a float uniform to values read from a series of floats.
     *
     * @param uName the name of the uniform to set.
     * @param values the series of floats to set.
     * @since 15.05.27
     */
    public void setUniformF(final CharSequence uName, final float... values) {
        new SetUniformFTask(uName, values).glRun(this.getThread());
    }

    /**
     * A GLTask that sets a floating point uniform.
     *
     * @since 15.05.27
     */
    public final class SetUniformFTask extends GLTask {

        private final String uName;
        private final float[] values;
        private final int count;

        /**
         * Sets the uniform vector from a GLVec.
         *
         * @param vec the vector to read elements from.
         * @return self reference.
         * @since 15.12.18
         */
        public SetUniformFTask set(final GLVec<?> vec) {
            if (vec.size() != this.count) {
                throw new ArrayIndexOutOfBoundsException("Invalid vector size!");
            }

            final GLVecF<?> vecF = vec.asGLVecF();

            return this.set(vecF.data(), vecF.offset(), vecF.size());
        }

        /**
         * Sets the uniform vector from elements.
         *
         * @param data the elements to read.
         * @return self reference.
         * @since 15.12.18
         */
        public SetUniformFTask set(final float... data) {
            return this.set(data, 0, data.length);
        }

        /**
         * Sets the uniform vector from elements read from an array.
         *
         * @param data the array to read elements from.
         * @param offset the offset to read elements.
         * @param length the number of elements to read.
         * @return self reference.
         * @since 15.12.18
         */
        public SetUniformFTask set(
                final float[] data, final int offset, final int length) {

            if (this.count != length) {
                throw new ArrayIndexOutOfBoundsException();
            }

            System.arraycopy(data, offset, this.values, 0, length);
            return this;
        }

        /**
         * Constructs a new SetUniformFTask with the specified vector.
         *
         * @param uName the name of the uniform.
         * @param vec the vector to set. The vector must be of size 1, 2, 3, or
         * 4. The values from the vector will be copied on task creation.
         * @since 15.05.27
         */
        public SetUniformFTask(final CharSequence uName, final GLVec<?> vec) {
            final int sz = vec.size();

            if (sz < 1 || sz > 4) {
                throw new GLException("Invalid uniform vector size!");
            }

            final GLVecF<?> vecF = vec.asGLVecF();

            this.values = new float[sz];
            System.arraycopy(vecF.data(), vecF.offset(), this.values, 0, sz);
            this.count = sz;
            this.uName = uName.toString();
        }

        /**
         * Constructs a new SetUniformFTask with a series of float data.
         *
         * @param uName the name of the uniform.
         * @param data the values to read. At least one and at most four values
         * must be defined.
         * @since 15.05.27
         */
        public SetUniformFTask(
                final CharSequence uName, final float... data) {
            this(uName, data, 0, data.length);
        }

        /**
         * Constructs a new SetUniformTask with an array of floats.
         *
         * @param uName the name of the uniform.
         * @param data the array to read data from.
         * @param offset the offset to start reading data.
         * @param length the number of elements to read.
         * @since 15.05.27
         */
        public SetUniformFTask(
                final CharSequence uName,
                final float[] data, final int offset, final int length) {

            if (length < 1 || length > 4) {
                throw new GLException("Invalid uniform count!");
            }

            this.values = new float[length];
            System.arraycopy(data, offset, this.values, 0, length);
            this.uName = uName.toString();
            this.count = length;
        }

        @SuppressWarnings("unchecked")
        @Override
        public void run() {
            LOGGER.trace(GL_MARKER, "############### Start GLProgram Set UniformF Task ###############");
            LOGGER.trace(GL_MARKER, "\tSetting uniform of GLProgram[{}]", getName());
            LOGGER.trace(GL_MARKER, "\tUniform location: {}", this.uName);

            if (!GLProgram.this.isValid()) {
                throw new GLException("GLProgram is not valid!");
            }

            final int uLoc = GLProgram.this.getUniformLoc(this.uName);

            GLProgram.this.program.updateTime();
            GLTools.getDriverInstance().programSetUniformF(program, uLoc, values);
            LOGGER.trace(GL_MARKER, "############### End GLProgram Set UniformF Task ###############");
        }

        @Override
        public String toString() {
            final StringBuilder out = new StringBuilder(128);

            out.append("glUniform");

            switch (this.count) {
                case 1:
                    out.append("1f: ");
                    break;
                case 2:
                    out.append("2f: ");
                    break;
                case 3:
                    out.append("3f: ");
                    break;
                case 4:
                    out.append("4f: ");
                    break;
            }

            out.append(Arrays.toString(this.values));

            return out.toString();
        }
    }

    /**
     * Links the shaders for the GLProgram.
     *
     * @param shaders series of shaders to link.
     * @since 15.05.27
     */
    public void linkShaders(final GLShader... shaders) {
        new LinkShadersTask(shaders).glRun(this.getThread());
    }

    /**
     * Links a collection of shaders for the GLProgram.
     *
     * @param shaders the array of shaders to link.
     * @param offset the offset to start reading from the list of shaders.
     * @param length the number of shaders to read.
     * @since 15.05.27
     */
    public void linkShaders(
            final GLShader[] shaders, final int offset, final int length) {

        new LinkShadersTask(shaders, offset, length).glRun(this.getThread());
    }

    /**
     * A GLTask that links shaders for the GLProgram.
     *
     * @since 15.05.27
     */
    public final class LinkShadersTask extends GLTask {

        private final GLShader[] shaders;

        /**
         * Constructs a new LinkShaderTask using a series of shaders.
         *
         * @param shaders the shaders to link.
         * @since 15.05.27
         */
        public LinkShadersTask(
                final GLShader... shaders) {
            this(shaders, 0, shaders.length);
        }

        /**
         * Constructs a new LinkShadersTask using an array of shaders.
         *
         * @param shaders the array to get shaders from.
         * @param offset the offset to start reading shaders.
         * @param length the number of shaders to read.
         * @since 15.05.27
         */
        public LinkShadersTask(
                final GLShader[] shaders, final int offset, final int length) {

            this.shaders = new GLShader[length];
            System.arraycopy(shaders, offset, this.shaders, 0, length);
        }

        @SuppressWarnings({"rawtypes", "unchecked"})
        @Override
        public void run() {
            LOGGER.trace(GL_MARKER, "############### Start GLProgram Link Shaders Task ###############");
            LOGGER.trace(GL_MARKER, "\tLinking shaders for GLProgram[{}]", GLProgram.this.getName());

            if (!GLProgram.this.isValid()) {
                throw new GLException("GLProgram is not valid!");
            }

            final Driver driver = GLTools.getDriverInstance();

            driver.programLinkShaders(
                    program,
                    Arrays.stream(shaders)
                    .map(shader -> shader.shader)
                    .collect(Collectors.toList())
                    .toArray(new Shader[shaders.length]));

            GLProgram.this.program.updateTime();
            LOGGER.trace(GL_MARKER, "Linked shaders for GLProgram[{}]!", GLProgram.this.getName());
            LOGGER.trace(GL_MARKER, "############### End GLProgram Link Shaders Task ###############");
        }
    }

    /**
     * Initialized the OpenGL program.
     *
     * @since 15.07.06
     */
    public final void init() {
        new InitTask().glRun(this.getThread());
    }

    /**
     * A GLTask that initializes the GLProgram. This is automatically ran on
     * GLProgram construction. GLProgram objects can be recycled by first
     * deleting them and then initializing them.
     *
     * @since 15.05.27
     */
    public class InitTask extends GLTask {

        @Override
        public void run() {
            LOGGER.trace(GL_MARKER, "############### Start GLProgram Init Task ###############");

            if (GLProgram.this.isValid()) {
                throw new GLException("Cannot reinit GLProgram!");
            }

            program = GLTools.getDriverInstance().programCreate();
            GLProgram.this.name = "id=" + program.hashCode();
            GLProgram.this.program.updateTime();

            LOGGER.trace(GL_MARKER, "Initialized GLProgram[{}]!", GLProgram.this.name);
            LOGGER.trace(GL_MARKER, "############### End GLProgram Init Task ###############");
        }
    }

    /**
     * Deletes the GLProgram.
     *
     * @since 15.05.27
     */
    public final void delete() {
        new DeleteTask().glRun(this.getThread());
    }

    /**
     * A GLTask that deletes the GLProgram.
     *
     * @since 15.05.27
     */
    public class DeleteTask extends GLTask {

        @SuppressWarnings("unchecked")
        @Override
        public void run() {
            LOGGER.trace(GL_MARKER, "############### Start GLProgram Delete Task ###############");
            LOGGER.trace(GL_MARKER, "\tDeleting GLProgram[{}]", GLProgram.this.getName());

            if (!GLProgram.this.isValid()) {
                LOGGER.warn(GL_MARKER, "Attempted to delete invalid GLProgram!");
            } else {
                GLTools.getDriverInstance().programDelete(program);
                GLProgram.this.program.resetTime();
                program = null;
            }
            LOGGER.trace(GL_MARKER, "############### End GLProgram Delete Task ###############");
        }
    }

    /**
     * Retrieves the binding assigned to the shader storage buffer object.
     *
     * @param sBlockName the uniform block name.
     * @return the binding point. An empty OptionalInt is returned when a
     * binding point was not assigned to the uniform block name.
     * @since 16.07.05
     */
    public OptionalInt getStorageBlockBinding(final String sBlockName) {
        return new GetStorageBlockBindingQuery(sBlockName).glCall(this.getThread());
    }

    /**
     * A GLQuery that retrieves the binding point for a shader storage buffer
     * object.
     *
     * @since 16.07.05
     */
    public final class GetStorageBlockBindingQuery extends GLQuery<OptionalInt> {

        final String sblockName;

        public GetStorageBlockBindingQuery(final CharSequence sBlockName) {
            this.sblockName = sBlockName.toString();
        }

        @Override
        public OptionalInt call() throws Exception {
            if (!GLProgram.this.program.isValid()) {
                throw new GLException("Invalid GLProgram!");
            } else {
                final int binding = GLTools.getDriverInstance().programGetStorageBlockBinding(program, this.sblockName);

                if (binding == -1) {
                    return OptionalInt.empty();
                } else {
                    return OptionalInt.of(binding);
                }
            }
        }
    }

    /**
     * Retrieves the binding assigned to a uniform buffer object.
     *
     * @param ublockName the uniform block name.
     * @return the binding point. An empty OptionalInt is returned when a
     * binding point was not assigned to the uniform block name.
     *
     * @since 16.07.05
     */
    public OptionalInt getUniformBlockBinding(final String ublockName) {
        return new GetUniformBlockBindingQuery(ublockName).glCall(this.getThread());
    }

    /**
     * A GLQuery that retrieves the binding point assigned to a uniform buffer
     * object. The binding point must have been assigned prior to executing this
     * query.
     *
     * @since 16.07.05
     */
    public final class GetUniformBlockBindingQuery extends GLQuery<OptionalInt> {

        final String ublockName;

        public GetUniformBlockBindingQuery(final CharSequence ublockName) {
            this.ublockName = ublockName.toString();
        }

        @Override
        public OptionalInt call() throws Exception {
            if (!GLProgram.this.isValid()) {
                throw new GLException("Invalid GLProgram!");
            }

            final int binding = GLTools.getDriverInstance().programGetUniformBlockBinding(program, this.ublockName);

            if (binding == -1) {
                return OptionalInt.empty();
            } else {
                return OptionalInt.of(binding);
            }
        }
    }

    /**
     * Assigns a binding point for a uniform buffer object. The binding can then
     * be used by GLBuffer in [code]bindUniform(binding)[/code]
     *
     * @param ublockName the uniform block name.
     * @param binding the binding point.
     * @since 16.07.05
     */
    public void setUniformBlockBinding(final String ublockName, final int binding) {
        new SetUniformBlockBinding(ublockName, binding).glRun(this.getThread());
    }

    /**
     * A GLTask that assigns a binding point for a uniform buffer object.
     *
     * @since 16.07.05
     */
    public final class SetUniformBlockBinding extends GLTask {

        final int binding;
        final String ublockName;

        public SetUniformBlockBinding(final CharSequence ublockName, final int binding) {
            this.ublockName = ublockName.toString();
            this.binding = binding;
        }

        @Override
        public void run() {
            LOGGER.trace(GL_MARKER, "############### Start GLProgram Set Uniform Block Binding Task ###############");
            LOGGER.trace(GL_MARKER, "\tAssigning uniform block: [{}] to uniform buffer binding: [{}]!", this.ublockName, this.binding);

            if (!GLProgram.this.isValid()) {
                throw new GLException("Invalid GLProgram!");
            } else {
                GLTools.getDriverInstance().programSetUniformBlockBinding(program, this.ublockName, this.binding);
                GLProgram.this.program.updateTime();
            }

            LOGGER.trace(GL_MARKER, "############### End GLProgram Set Uniform Block Binding Task ###############");
        }
    }

    /**
     * Assigns a binding point for a shader storage buffer. The binding can then
     * be used by GLBuffer in [code]bindStorage(binding)[/code]
     *
     * @param sblockName the uniform block name.
     * @param binding the binding point.
     * @since 16.07.05
     */
    public final void setStorageBlockBinding(final String sblockName, final int binding) {
        new SetStorageBlockBindingTask(sblockName, binding).glRun(this.getThread());
    }

    /**
     * A GLTask that assigns a binding point for a shader storage buffer.
     *
     * @since 16.07.05
     */
    public final class SetStorageBlockBindingTask extends GLTask {

        final int binding;
        final String sblockName;

        public SetStorageBlockBindingTask(final CharSequence sblockName, final int binding) {
            this.binding = binding;
            this.sblockName = sblockName.toString();
        }

        @Override
        public void run() {
            LOGGER.trace(GL_MARKER, "############### Start GLProgram Set Storage Block Binding Task ###############");
            LOGGER.trace(GL_MARKER, "\tAssigning uniform block: [{}] to storage binding: [{}]!", this.sblockName, this.binding);

            if (!GLProgram.this.isValid()) {
                throw new GLException("Invalid GLProgram!");
            } else {
                GLTools.getDriverInstance().programSetStorageBlockBinding(program, this.sblockName, this.binding);
                GLProgram.this.program.updateTime();
            }

            LOGGER.trace(GL_MARKER, "############### End GLProgram Set Storage Block Binding Task ###############");
        }
    }

    /**
     * Sets the shader storage with a GLBuffer.
     *
     * @param storageName the name of the storage block.
     * @param buffer the GLBuffer to bind.
     * @param bindingPoint the index to bind the GLBuffer to.
     * @since 15.07.06
     * @deprecated binding points are actually independent of the program
     * object!
     */
    @Deprecated
    public void setShaderStorage(
            final CharSequence storageName,
            final GLBuffer buffer,
            final int bindingPoint) {

        new SetShaderStorageTask(
                storageName,
                buffer,
                bindingPoint).glRun(this.getThread());
    }

    /**
     * A GLTask that sets a Shader Storage block.
     *
     * @since 15.07.06
     */
    public class SetShaderStorageTask extends GLTask {

        final String storageName;
        final GLBuffer buffer;
        final int bindingPoint;

        /**
         * Constructs a new SetShaderStorageTask.
         *
         * @param storageName the name of the shader storage
         * @param buffer the buffer to bind.
         * @param bindingPoint the index to bind the buffer to.
         * @since 15.07.06
         */
        public SetShaderStorageTask(
                final CharSequence storageName,
                final GLBuffer buffer,
                final int bindingPoint) {

            this.storageName = storageName.toString();
            this.buffer = Objects.requireNonNull(buffer);

            if ((this.bindingPoint = bindingPoint) < 0) {
                throw new GLException("Invalid binding point: " + bindingPoint);
            }
        }

        @SuppressWarnings("unchecked")
        @Override
        public void run() {
            LOGGER.trace(GL_MARKER, "############### Start GLProgram Set Shader Storage Task ###############");
            LOGGER.trace(GL_MARKER, "\tSetting shader storage for GLProgram[{}]", getName());
            LOGGER.trace(GL_MARKER, "\tStorage Name: {}", storageName);
            LOGGER.trace(GL_MARKER, "\tShader storage: GLBuffer[{}]\n", buffer.getName());
            LOGGER.trace(GL_MARKER, "\tBinding point: {}", bindingPoint);

            if (!GLProgram.this.isValid()) {
                throw new GLException("Invalid GLProgram object!");
            } else if (!this.buffer.isValid()) {
                throw new GLException("Invalid GLBuffer object!");
            }

            GLProgram.this.program.updateTime();
            GLTools.getDriverInstance().programSetStorage(program, storageName, buffer.buffer, bindingPoint);
            LOGGER.trace(GL_MARKER, "############### End GLProgram Set Shader Storage Task ###############");
        }
    }

    /**
     * Binds a GLBuffer to the specified uniform block.
     *
     * @param uBlock the name of the uniform block.
     * @param buffer the buffer to use.
     * @param bindingPoint the index to bind the buffer to.
     * @since 15.07.06
     */
    @Deprecated
    public void setUniformBlock(
            final CharSequence uBlock,
            final GLBuffer buffer,
            final int bindingPoint) {

        new SetUniformBlockTask(
                uBlock,
                buffer,
                bindingPoint).glRun(this.getThread());
    }

    /**
     * A GLTask that sets a uniform block.
     *
     * @since 15.07.06
     */
    public final class SetUniformBlockTask extends GLTask {

        private final String blockName;
        private final GLBuffer buffer;
        private final int bindingPoint;

        /**
         * Constructs a new SetUniformBlockTask.
         *
         * @param uBlock the name of the uniform block.
         * @param buffer the buffer to bind.
         * @param bindingPoint the index to bind the buffer to.
         * @since 15.07.06
         */
        public SetUniformBlockTask(
                final CharSequence uBlock,
                final GLBuffer buffer,
                final int bindingPoint) {

            this.blockName = uBlock.toString();
            this.buffer = Objects.requireNonNull(buffer);

            if ((this.bindingPoint = bindingPoint) < 0) {
                throw new GLException(
                        "Invalid binding point address: " + bindingPoint);
            }
        }

        @SuppressWarnings("unchecked")
        @Override
        public void run() {
            LOGGER.trace(GL_MARKER, "############### Start GLProgram Set Uniform Block Task ###############");
            LOGGER.trace(GL_MARKER, "\tSetting uniform block of GLProgram[{}]", getName());
            LOGGER.trace(GL_MARKER, "\tUniform block name: {}", blockName);
            LOGGER.trace(GL_MARKER, "\tUniform block: GLBuffer[{}]", buffer.getName());
            LOGGER.trace(GL_MARKER, "\tBinding Point: {}", this.bindingPoint);

            if (!GLProgram.this.isValid()) {
                throw new GLException("Invalid GLProgram object!");
            } else if (!this.buffer.isValid()) {
                throw new GLException("Invalid GLBuffer object!");
            }

            GLProgram.this.program.updateTime();
            GLTools.getDriverInstance().programSetUniformBlock(program, blockName, buffer.buffer, bindingPoint);
            LOGGER.trace(GL_MARKER, "############### End GLProgram Set Uniform Block Task ###############");
        }
    }

    /**
     * Checks if the OpenGL context supports compute shaders.
     *
     * @return true if compute shaders are supported.
     * @since 15.12.18
     */
    public static boolean isComputeSupported() {
        return GLTools.hasOpenGLVersion(42);
    }

    /**
     * Dispatches a 1D compute task.
     *
     * @param groupsX the number of groups to compute.
     * @since 15.12.18
     */
    public void compute(final int groupsX) {
        new ComputeTask(groupsX).glRun(this.getThread());
    }

    /**
     * Dispatches a 2D compute task.
     *
     * @param groupsX number of groups along the x-axis.
     * @param groupsY number of groups along the y-axis.
     * @since 15.12.18
     */
    public void compute(final int groupsX, final int groupsY) {
        new ComputeTask(groupsX, groupsY).glRun(this.getThread());
    }

    /**
     * Dispatches a 3D compute task.
     *
     * @param groupsX number of groups along the x-axis.
     * @param groupsY number of groups along the y-axis.
     * @param groupsZ number of groups along the z-axis.
     * @since 15.12.18
     */
    public void compute(
            final int groupsX, final int groupsY, final int groupsZ) {

        new ComputeTask(groupsX, groupsY, groupsZ).glRun(this.getThread());
    }

    /**
     * A GLTask that dispatches a compute task for the GLProgram.
     *
     * @since 15.12.18
     */
    public final class ComputeTask extends GLTask {

        private final int numX;
        private final int numY;
        private final int numZ;

        /**
         * Constructs a new 1D compute task.
         *
         * @param groupsX number of groups along the x-axis.
         * @since 15.12.18
         */
        public ComputeTask(final int groupsX) {
            this(groupsX, 1, 1);
        }

        /**
         * Constructs a new 2D compute task.
         *
         * @param groupsX number of groups along the x-axis.
         * @param groupsY number of groups along the y-axis.
         * @since 15.12.18
         */
        public ComputeTask(final int groupsX, final int groupsY) {
            this(groupsX, groupsY, 1);
        }

        /**
         * Constructs a new 3D compute task.
         *
         * @param groupsX number of groups along the x-axis.
         * @param groupsY number of groups along the y-axis.
         * @param groupsZ number of groups along the z-axis.
         * @since 15.12.18
         */
        public ComputeTask(
                final int groupsX, final int groupsY, final int groupsZ) {

            if ((this.numX = groupsX) < 1) {
                throw new GLException("groupsX cannot be less than 1!");
            } else if ((this.numY = groupsY) < 1) {
                throw new GLException("groupsY cannot be less than 1!");
            } else if ((this.numZ = groupsZ) < 1) {
                throw new GLException("groupsZ cannot be less than 1!");
            }
        }

        @SuppressWarnings("unchecked")
        @Override
        public void run() {
            LOGGER.trace(GL_MARKER, "############### Start GLProgram Compute Task ###############");
            LOGGER.trace(GL_MARKER, "\tComputing GLProgram[{}]", getName());
            LOGGER.trace(GL_MARKER, "\tGroup size: <{}, {}, {}>", this.numX, this.numY, this.numZ);

            if (!GLProgram.this.isValid()) {
                throw new GLException("Invalid GLProgram!");
            }

            GLTools.getDriverInstance().programDispatchCompute(program, numX, numY, numZ);
            GLProgram.this.program.updateTime();
            LOGGER.trace(GL_MARKER, "############### End GLProgram Compute Task ###############");
        }
    }

    /**
     * Binds a GLBuffer to a vertex varying location.
     *
     * @param varyingLoc the location of the feedback varying.
     * @param fbBuffer the buffer to bind.
     * @since 15.10.30
     * @deprecated use GLBuffer.bindFeedback instead
     */
    @Deprecated
    public void setFeedbackBuffer(final int varyingLoc, final GLBuffer fbBuffer) {
        new SetFeedbackBufferTask(varyingLoc, fbBuffer).glRun(this.getThread());
    }

    /**
     * A GLTask that binds a GLBuffer to a vertex varying location.
     *
     * @since 15.10.30
     */
    @Deprecated
    public class SetFeedbackBufferTask extends GLTask {

        private final GLBuffer fbBuffer;
        private final int varyingLoc;

        /**
         * Constructs a new SetFeedbackBufferTask.
         *
         * @param varyingLoc the location of the feedback varying.
         * @param fbBuffer the buffer to bind.
         * @since 15.10.30
         */
        public SetFeedbackBufferTask(final int varyingLoc, final GLBuffer fbBuffer) {
            if ((this.varyingLoc = varyingLoc) == GLVertexAttributes.INVALID_VARYING_LOCATION) {
                throw new GLException("Invalid varying location!");
            }

            this.fbBuffer = Objects.requireNonNull(fbBuffer);
        }

        @SuppressWarnings("unchecked")
        @Override
        public void run() {
            LOGGER.trace(GL_MARKER, "############### Start GLProgram Set Feedback Buffer Task ###############");
            LOGGER.trace(GL_MARKER, "\tFeedback program: GLProgram[{}]", getName());
            LOGGER.trace(GL_MARKER, "\tFeedback write buffer: GLBuffer[{}]", fbBuffer.getName());
            LOGGER.trace(GL_MARKER, "\tVarying: {}", this.varyingLoc);

            if (!GLProgram.this.isValid()) {
                throw new GLException("Invalid GLProgram!");
            } else if (!fbBuffer.isValid()) {
                throw new GLException("Invalid GLBuffer!");
            }

            GLProgram.this.program.updateTime();
            GLTools.getDriverInstance().programSetFeedbackBuffer(program, varyingLoc, fbBuffer.buffer);
            LOGGER.trace(GL_MARKER, "############### End GLProgram Set Feedback Buffer Task ###############");
        }
    }

    public long getTimeSinceLastUpdate() {
        if (this.program != null) {
            return this.program.getTimeSinceLastUsed();
        } else {
            return System.nanoTime();
        }
    }
}
