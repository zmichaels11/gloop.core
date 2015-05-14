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
    
    public GLProgram() {
        super();
        this.init();
    }
    
    public GLProgram(final GLThread thread) {
        super(thread);
        this.init();
    }
    
    public boolean isValid() {
        return this.programId != INVALID_PROGRAM_ID;
    }
    
    public boolean isCurrent() {
        return CURRENT == this;
    }
    
    protected void bind() {
        if (!this.isCurrent()) {
            GL20.glUseProgram(this.programId);
            GLProgram.CURRENT = this;
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
    
    public void setVertexAttributes(final GLVertexAttributes attrib) {
        new SetVertexAttributesTask(attrib).glRun(this.getThread());
    }
    
    public class SetVertexAttributesTask extends GLTask {
        
        private final GLVertexAttributes attribs;
        
        public SetVertexAttributesTask(final GLVertexAttributes attrib) {
            Objects.requireNonNull(this.attribs = attrib);
        }
        
        @Override
        public void run() {
            if (!GLProgram.this.isValid()) {
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
    
    public void setUniformMatrixD(
            final CharSequence uName, final GLMat<?, ?> mat) {
        
        new SetUniformMatrixDTask(uName, mat).glRun(this.getThread());
    }    
    
    public void setUniformMatrixF(
            final CharSequence uName, final GLMat<?, ?> mat) {
        
        new SetUniformMatrixFTask(uName, mat).glRun(this.getThread());
    }
    
    public class SetUniformMatrixDTask extends GLTask {
        
        private final String uName;
        private final double[] values;
        private final int count;
        
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
    
    public class SetUniformMatrixFTask extends GLTask {
        
        private final String uName;
        private final float[] values;
        private final int count;
        
        public SetUniformMatrixFTask(final CharSequence uName, GLMat<?, ?> mat) {
            
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
    
    public void setUniformD(final CharSequence uName, final GLVec<?> vec) {
        new SetUniformDTask(uName, vec).glRun(this.getThread());
    }
    
    public void setUniformD(final CharSequence uName,
            final double[] values, final int offset, final int length) {
        
        new SetUniformDTask(uName, values, offset, length)
                .glRun(this.getThread());
    }
    
    public void setUniformD(final CharSequence uName, final double... values) {
        new SetUniformDTask(uName, values).glRun(this.getThread());
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
    
    public void setUniformI(
            final CharSequence uName,
            final int[] data, final int offset, final int length) {
        
        new SetUniformITask(uName, data, offset, length).glRun(this.getThread());
    }
    
    public void setUniformI(final CharSequence uName, final int... data) {
        new SetUniformITask(uName, data).glRun(this.getThread());
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
    
    public void setUniformF(final CharSequence uName, final GLVec<?> vec) {
        new SetUniformFTask(uName, vec).glRun(this.getThread());
    }
    
    public void setUniformF(
            final CharSequence uName,
            final float[] data, final int offset, final int length) {
        
        new SetUniformFTask(uName, data, offset, length).glRun(this.getThread());
    }
    
    public void setUniformF(final CharSequence uName, final float... values) {
        new SetUniformFTask(uName, values).glRun(this.getThread());
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
    
    public void linkShaders(final GLShader... shaders) {
        new LinkShadersTask(shaders).glRun(this.getThread());
    }
    
    public void linkShaders(
            final GLShader[] shaders, final int offset, final int length) {
        
        new LinkShadersTask(shaders, offset, length).glRun(this.getThread());
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
    public final void delete() {
        this.deleteTask.glRun(this.getThread());
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
