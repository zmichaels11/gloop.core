/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.longlinkislong.gloop;

import org.lwjgl.opengl.GL20;

/**
 *
 * @author zmichaels
 */
public class GLShader extends GLObject implements AutoCloseable {

    private static final int INVALID_SHADER_ID = -1;
    private final String src;
    private final GLShaderType type;
    protected int shaderId = INVALID_SHADER_ID;

    public GLShader(final GLShaderType type, final CharSequence src) {
        this.src = src.toString();
        this.type = type;
        this.init();
    }

    private void init() {
        final GLTask task = new InitTask();
        final GLThread thread = this.getGLThread();

        if (thread.isCurrent()) {
            task.run();
        } else {
            thread.submitGLTask(task);
        }
    }

    public boolean isValid() {
        return this.shaderId != INVALID_SHADER_ID;
    }

    @Override
    public void close() throws Exception {
        final GLTask task = new DeleteTask();
        final GLThread thread = this.getGLThread();

        if (thread.isCurrent()) {
            task.run();
        } else {
            thread.submitGLTask(task);
        }
    }

    @Override
    public String toString() {
        return "GLShader [" + this.type + "]: " + this.shaderId;
    }

    public String getSource() {
        return this.src;
    }

    public class InitTask extends GLTask {

        @Override
        public void run() {
            if (!GLShader.this.isValid()) {
                GLShader.this.shaderId = GL20.glCreateShader(GLShader.this.shaderId);
                GL20.glShaderSource(GLShader.this.shaderId, GLShader.this.src);
                GL20.glCompileShader(GLShader.this.shaderId);

                final int compStatus
                        = new ParameterQuery(GLShaderParameterName.GL_COMPILE_STATUS)
                        .glCall();

                if (compStatus == 0) {
                    final String info = new InfoLogQuery().glCall();

                    throw new GLException(info);
                }

            }
        }
    }

    public class DeleteTask extends GLTask {

        @Override
        public void run() {
            if (GLShader.this.isValid()) {
                GL20.glDeleteShader(GLShader.this.shaderId);
                GLShader.this.shaderId = INVALID_SHADER_ID;
            }
        }
    }

    public class ParameterQuery extends GLQuery<Integer> {

        final GLShaderParameterName pName;

        public ParameterQuery(final GLShaderParameterName pName) {
            this.pName = pName;
        }

        @Override
        public Integer call() throws Exception {
            return GL20.glGetShaderi(GLShader.this.shaderId, pName.value);
        }
    }

    public class InfoLogQuery extends GLQuery<String> {

        @Override
        public String call() throws Exception {
            final int length = GL20.glGetShaderi(
                    GLShader.this.shaderId,
                    GLShaderParameterName.GL_INFO_LOG_LENGTH.value);

            return GL20.glGetShaderInfoLog(GLShader.this.shaderId, length);
        }
    }
}
