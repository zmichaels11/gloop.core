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
import java.util.Map;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL20;
import org.lwjgl.opengl.GL40;

/**
 *
 * @author zmichaels
 */
public class GLProgram extends GLObject {

    private static final FloatBuffer TEMPF = ByteBuffer.allocateDirect(16 << 2).order(ByteOrder.nativeOrder()).asFloatBuffer();
    private static final DoubleBuffer TEMPD = ByteBuffer.allocateDirect(16 << 3).order(ByteOrder.nativeOrder()).asDoubleBuffer();

    private static GLProgram CURRENT = null;
    private static final int INVALID_PROGRAM_ID = -1;
    protected int programId = INVALID_PROGRAM_ID;
    private final Map<String, Integer> uniforms = new HashMap<>();

    public boolean isValid() {
        return this.programId != INVALID_PROGRAM_ID;
    }

    public void close() {
        final GLTask task = new DeleteTask();
        final GLThread thread = this.getGLThread();

        if (thread.isCurrent()) {
            task.run();
        } else {
            thread.submitGLTask(task);
        }
    }

    public boolean isCurrent() {
        return CURRENT == this;
    }

    private int getUniformLoc(final String uName) {
        if (this.uniforms.containsKey(uName)) {
            return this.uniforms.get(uName);
        } else {
            final int uLoc = GL20.glGetUniformLocation(programId, uName);

            this.uniforms.put(uName, uLoc);
            return uLoc;
        }
    }

    public class BindTask extends GLTask {

        @Override
        public void run() {
            if (GLProgram.this.isValid() && !GLProgram.this.isCurrent()) {
                GL20.glUseProgram(GLProgram.this.programId);
                CURRENT = GLProgram.this;
            }
        }
    }

    public class SetUniformMatrixDTask extends GLTask {

        private final String uName;
        private final double[] values;
        private final int count;

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

            final int uLoc = GLProgram.this.getUniformLoc(uName);

            TEMPD.clear();
            TEMPD.put(this.values).flip();

            switch (this.count) {
                case 4:
                    GL40.glUniformMatrix2(uLoc, false, TEMPD);
                    break;
                case 9:
                    GL40.glUniformMatrix3(uLoc, false, TEMPD);
                    break;
                case 16:
                    GL40.glUniformMatrix4(uLoc, false, TEMPD);
                    break;
            }
        }
    }

    public class SetUniformMatrixFTask extends GLTask {

        private final String uName;
        private final float[] values;
        private final int count;

        public SetUniformMatrixFTask(final CharSequence uName, GLMat<?, ?> m) {

            final int sz = m.size();

            if (!(sz == 2 || sz == 3 || sz == 4)) {
                throw new GLException("Invalid uniform count!");
            }

            final GLMatF mf = m.asGLMatF();

            this.uName = uName.toString();
            this.count = sz * sz;
            this.values = new float[this.count];

            System.arraycopy(mf.data(), mf.offset(), this.values, 0, this.count);
        }

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

            final int uLoc = GLProgram.this.getUniformLoc(this.uName);
            TEMPF.clear();
            TEMPF.put(values).flip();

            switch (this.count) {
                case 4:
                    GL20.glUniformMatrix2(uLoc, false, TEMPF);
                    break;
                case 9:
                    GL20.glUniformMatrix3(uLoc, false, TEMPF);
                    break;
                case 16:
                    GL20.glUniformMatrix4(uLoc, false, TEMPF);
                    break;
            }
        }
    }

    public class SetUniformDTask extends GLTask {

        private final String uName;
        private final double[] values;
        private final int count;

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

        public SetUniformDTask(final CharSequence uName, final double... data) {
            this(uName, data, 0, data.length);
        }

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

    public class SetUniformITask extends GLTask {

        private final String uName;
        private final int[] values;
        private final int count;

        public SetUniformITask(
                final CharSequence uName, final int... data) {
            this(uName, data, 0, data.length);
        }

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

    public class SetUniformFTask extends GLTask {

        private final String uName;
        private final float[] values;
        private final int count;

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

        public SetUniformFTask(
                final CharSequence uName, final float... data) {
            this(uName, data, 0, data.length);
        }

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

    public class LinkShadersTask extends GLTask {

        private final GLShader[] shaders;

        public LinkShadersTask(
                final GLShader... shaders) {
            this(shaders, 0, shaders.length);
        }

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
            
            if(isLinked == GL11.GL_FALSE) {
                final int length = GL20.glGetProgrami(
                        GLProgram.this.programId, GL20.GL_INFO_LOG_LENGTH);
                final String msg = GL20.glGetProgramInfoLog(
                        GLProgram.this.programId, length);                                
                
                throw new GLException(msg);                
            } else {
                for(GLShader shader : this.shaders) {
                    GL20.glDetachShader(GLProgram.this.programId, shader.shaderId);
                }
            }
        }

    }

    public class InitTask extends GLTask {

        @Override
        public void run() {
            if (GLProgram.this.isValid()) {
                throw new GLException("Cannot reinit GLProgram!");
            }

            GLProgram.this.programId = GL20.glCreateProgram();
        }
    }

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
}
