/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.longlinkislong.gloop;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.DoubleBuffer;
import java.nio.FloatBuffer;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL20;
import org.lwjgl.opengl.GL30;
import org.lwjgl.opengl.GL40;
import org.lwjgl.opengl.GL43;

/**
 * A GLObject that represents an OpenGL shader program.
 *
 * @author zmichaels
 * @since 15.05.27
 */
public class GLProgram extends GLObject {
    
    private static final FloatBuffer TEMPF = ByteBuffer
            .allocateDirect(16 << 2)
            .order(ByteOrder.nativeOrder())
            .asFloatBuffer();
    private static final DoubleBuffer TEMPD = ByteBuffer
            .allocateDirect(16 << 3)
            .order(ByteOrder.nativeOrder())
            .asDoubleBuffer();
    
    private static final Map<Thread, GLProgram> CURRENT = new HashMap<>();
    private static final int INVALID_PROGRAM_ID = -1;
    protected int programId = INVALID_PROGRAM_ID;
    private final Map<String, Integer> uniforms = new HashMap<>();

    /**
     * Constructs a new GLProgram using the default GLThread.
     *
     * @since 15.05.27
     */
    public GLProgram() {
        super();
        this.init();
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
        return this.programId != INVALID_PROGRAM_ID;
    }

    /**
     * Checks if the GLProgram is currently bound on the current thraed.
     *
     * @return true if the GLProgram is currently being used.
     * @since 15.05.27
     */
    public boolean isCurrent() {
        return CURRENT.get(Thread.currentThread()) == this;
    }

    /**
     * Binds the current program to the current thread.
     *
     * @since 15.05.27
     */
    protected void bind() {
        if (!this.isCurrent()) {
            GL20.glUseProgram(this.programId);
            CURRENT.put(Thread.currentThread(), this);
        }
    }
    
    private int getUniformLoc(final String uName) {
        if (this.uniforms.containsKey(uName)) {
            return this.uniforms.get(uName);
        } else {
            this.bind();
            final int uLoc = GL20.glGetUniformLocation(programId, uName);
            
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
            Objects.requireNonNull(this.attribs = attrib);
        }
        
        @Override
        public void run() {
            if (!GLProgram.this.isValid()) {
                throw new GLException("Invalid GLProgram!");
            } else {
                this.attribs.nameMap.forEach((name, index) -> {
                    GL20.glBindAttribLocation(
                            GLProgram.this.programId,
                            index, name);
                });
                
                final Set<String> varyingSet = this.attribs.feedbackVaryings;
                
                if (!varyingSet.isEmpty()) {
                    final CharSequence[] varyings = new CharSequence[varyingSet.size()];
                    final Iterator<String> it = varyingSet.iterator();
                    
                    for (int i = 0; i < varyingSet.size(); i++) {
                        varyings[i] = it.next();
                    }
                    
                    GL30.glTransformFeedbackVaryings(
                            GLProgram.this.programId,
                            varyings,
                            GL30.GL_SEPARATE_ATTRIBS);
                }
            }
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
    public class SetUniformMatrixDTask extends GLTask {
        
        private final String uName;
        private final double[] values;
        private final int count;

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
        
        @Override
        public void run() {
            if (!GLProgram.this.isValid()) {
                throw new GLException("GLProgram is not valid!");
            }
            
            GLProgram.this.bind();
            
            final int uLoc = GLProgram.this.getUniformLoc(uName);

            //TODO check if this needs sync
            TEMPD.clear();
            TEMPD.put(this.values).flip();
            
            switch (this.count) {
                case 4:
                    GL40.glUniformMatrix2dv(uLoc, false, TEMPD);
                    break;
                case 9:
                    GL40.glUniformMatrix3dv(uLoc, false, TEMPD);
                    break;
                case 16:
                    GL40.glUniformMatrix4dv(uLoc, false, TEMPD);
                    break;
            }
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
         * Constructs a new UniformMatrixTask using the supplied GLMat. The
         * values set will be the values of the GLMat on task creation.
         *
         * @param uName the name of the uniform to use.
         * @param mat the matrix to set
         * @since 15.05.27
         */
        public SetUniformMatrixFTask(final CharSequence uName, final GLMat<?, ?> mat) {
            
            final int sz = mat.size();
            
            if (!(sz == 2 || sz == 3 || sz == 4)) {
                throw new GLException("Invalid uniform count!");
            }
            
            final GLMatF mf = mat.asGLMatF();
            
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
        
        @Override
        public void run() {
            if (!GLProgram.this.isValid()) {
                throw new GLException("GLProgram is not valid!");
            }
            
            GLProgram.this.bind();
            
            final int uLoc = GLProgram.this.getUniformLoc(this.uName);
            TEMPF.put(values);
            TEMPF.flip();
            
            switch (this.count) {
                case 4:
                    GL20.glUniformMatrix2fv(uLoc, false, TEMPF);
                    break;
                case 9:
                    GL20.glUniformMatrix3fv(uLoc, false, TEMPF);
                    break;
                case 16:
                    GL20.glUniformMatrix4fv(uLoc, false, TEMPF);
                    break;
            }
            TEMPF.clear();
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
            
            final GLVecD vd = v.asGLVecD();
            
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
        
        @Override
        public void run() {
            if (!GLProgram.this.isValid()) {
                throw new GLException("GLProgram is not valid!");
            }
            
            GLProgram.this.bind();
            
            final int uLoc = GLProgram.this.getUniformLoc(this.uName);
            
            switch (this.count) {
                case 1:
                    GL40.glUniform1d(uLoc, this.values[0]);
                    break;
                case 2:
                    GL40.glUniform2d(uLoc, this.values[0], this.values[1]);
                    break;
                case 3:
                    GL40.glUniform3d(uLoc, this.values[0], this.values[1], this.values[2]);
                    break;
                case 4:
                    GL40.glUniform4d(uLoc, this.values[0], this.values[1], this.values[2], this.values[3]);
                    break;
            }
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
    public class SetUniformITask extends GLTask {
        
        private final String uName;
        private final int[] values;
        private final int count;

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
        
        @Override
        public void run() {
            if (!GLProgram.this.isValid()) {
                throw new GLException("GLProgram is not valid!");
            }
            
            GLProgram.this.bind();
            
            final int uLoc = GLProgram.this.getUniformLoc(this.uName);
            
            switch (this.count) {
                case 1:
                    GL20.glUniform1i(uLoc, this.values[0]);
                    
                    break;
                case 2:
                    GL20.glUniform2i(uLoc, this.values[0], this.values[1]);
                    break;
                case 3:
                    GL20.glUniform3i(uLoc, this.values[0], this.values[1], this.values[2]);
                    break;
                case 4:
                    GL20.glUniform4i(uLoc, this.values[0], this.values[1], this.values[2], this.values[3]);
                    break;
            }
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
     */
    public class SetUniformFTask extends GLTask {
        
        private final String uName;
        private final float[] values;
        private final int count;

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
            
            final GLVecF vecF = vec.asGLVecF();
            
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
        
        @Override
        public void run() {
            if (!GLProgram.this.isValid()) {
                throw new GLException("GLProgram is not valid!");
            }
            
            GLProgram.this.bind();
            
            final int uLoc = GLProgram.this.getUniformLoc(this.uName);
            
            switch (this.count) {
                case 1:
                    GL20.glUniform1f(uLoc, this.values[0]);
                    break;
                case 2:
                    GL20.glUniform2f(uLoc, this.values[0], this.values[1]);
                    break;
                case 3:
                    GL20.glUniform3f(uLoc, this.values[0], this.values[1], this.values[2]);
                    break;
                case 4:
                    GL20.glUniform4f(uLoc, this.values[0], this.values[1], this.values[2], this.values[3]);
                    break;
            }
        }
        
        @Override
        public String toString() {
            final StringBuilder out = new StringBuilder();
            
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
    public class LinkShadersTask extends GLTask {
        
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
        
        @Override
        public void run() {
            if (!GLProgram.this.isValid()) {
                throw new GLException("GLProgram is not valid!");
            }
            
            for (GLShader shader : this.shaders) {
                if (!shader.isValid()) {
                    throw new GLException("GLShader is not valid!");
                }
                
                GL20.glAttachShader(GLProgram.this.programId, shader.shaderId);
            }
            
            GL20.glLinkProgram(GLProgram.this.programId);
            final int isLinked = GL20.glGetProgrami(
                    GLProgram.this.programId,
                    GL20.GL_LINK_STATUS);
            
            if (isLinked == GL11.GL_FALSE) {
                final int length = GL20.glGetProgrami(
                        GLProgram.this.programId, GL20.GL_INFO_LOG_LENGTH);
                final String msg = GL20.glGetProgramInfoLog(
                        GLProgram.this.programId, length);
                
                throw new GLException(msg);
            } else {
                for (GLShader shader : this.shaders) {
                    GL20.glDetachShader(GLProgram.this.programId, shader.shaderId);
                }
            }
        }
        
    }
    
    private final GLTask initTask = new InitTask();
    
    public final void init() {
        this.initTask.glRun(this.getThread());
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
            if (GLProgram.this.isValid()) {
                throw new GLException("Cannot reinit GLProgram!");
            }
            
            GLProgram.this.programId = GL20.glCreateProgram();
        }
    }
    
    private final GLTask deleteTask = new DeleteTask();

    /**
     * Deletes the GLProgram.
     *
     * @since 15.05.27
     */
    public final void delete() {
        this.deleteTask.glRun(this.getThread());
    }

    /**
     * A GLTask that deletes the GLProgram.
     *
     * @since 15.05.27
     */
    public class DeleteTask extends GLTask {
        
        @Override
        public void run() {
            if (!GLProgram.this.isValid()) {
                throw new GLException("Cannot delete invalid GLProgram!");
            }
            
            GL20.glDeleteProgram(GLProgram.this.programId);
            GLProgram.this.programId = INVALID_PROGRAM_ID;
        }
    }

    /**
     * A GLTask that sets a uniform sampler.
     *
     * @since 15.05.27
     */
    public class SetUniformSamplerTask extends GLTask {
        
        private final GLTexture.BindTask bindTask;
        private final String uName;

        /**
         * Constructs a new SetUniformSamplerTask with the specified GLTexture
         * BindTask.
         *
         * @param uName the name of the uniform to set.
         * @param bindTask the bind task to associate the uniform to.
         * @since 15.05.27
         */
        public SetUniformSamplerTask(
                final CharSequence uName, final GLTexture.BindTask bindTask) {
            
            Objects.requireNonNull(this.bindTask = bindTask);
            this.uName = uName.toString();
        }
        
        @Override
        public void run() {
            if (!GLProgram.this.isValid()) {
                throw new GLException("GLProgram is invalid!");
            }
            
            GLProgram.this.bind();
            
            final int uLoc = GLProgram.this.getUniformLoc(uName);
            
            GL20.glUniform1i(uLoc, this.bindTask.activeTexture);
        }
    }
    
    private ComputeTask lastComputeTask = null;
    
    public void compute(final int groupsX, final int groupsY, final int groupsZ) {
        if (this.lastComputeTask != null
                && this.lastComputeTask.numX == groupsX
                && this.lastComputeTask.numY == groupsY
                && this.lastComputeTask.numZ == groupsZ) {
            
            this.lastComputeTask.glRun(this.getThread());
        } else {
            this.lastComputeTask = new ComputeTask(groupsX, groupsY, groupsZ);
            this.lastComputeTask.glRun(this.getThread());
        }
    }
    
    public class ComputeTask extends GLTask {
        
        final int numX;
        final int numY;
        final int numZ;
        
        public ComputeTask(final int groupsX) {
            this(groupsX, 1, 1);
        }
        
        public ComputeTask(final int groupsX, final int groupsY) {
            this(groupsX, groupsY, 1);
        }
        
        public ComputeTask(final int groupsX, final int groupsY, final int groupsZ) {
            if ((this.numX = groupsX) < 1) {
                throw new GLException("groupsX cannot be less than 1!");
            } else if ((this.numY = groupsY) < 1) {
                throw new GLException("groupsY cannot be less than 1!");
            } else if ((this.numZ = groupsZ) < 1) {
                throw new GLException("groupsZ cannot be less than 1!");
            }
        }
        
        @Override
        public void run() {
            if (!GLProgram.this.isValid()) {
                throw new GLException("Invalid GLProgram!");
            }
            
            GLProgram.this.bind();
            GL43.glDispatchCompute(this.numX, this.numY, this.numZ);
        }
    }
}
