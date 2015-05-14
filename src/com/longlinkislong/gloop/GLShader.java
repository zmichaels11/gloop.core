/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.longlinkislong.gloop;

import java.util.Objects;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL20;

/**
 *
 * @author zmichaels
 */
public class GLShader extends GLObject{

    private static final int INVALID_SHADER_ID = -1;
    private final String src;
    private final GLShaderType type;
    protected int shaderId = INVALID_SHADER_ID;

    public GLShader(final GLShaderType type, final CharSequence src) {
        super();
        this.src = src.toString();
        Objects.requireNonNull(this.type = type);
    }
    
    public GLShader(
            final GLThread thread, 
            final GLShaderType type, final CharSequence src) {
        
        super(thread);
        this.src = src.toString();
        Objects.requireNonNull(this.type = type);
    }

    public boolean isValid() {
        return this.shaderId != INVALID_SHADER_ID;
    }

    @Override
    public String toString() {
        return "GLShader [" + this.type + "]: " + this.shaderId;
    }

    public String getSource() {
        return this.src;
    }

    public void compile() {
        new CompileTask().glRun(this.getThread());
    }
    
    public class CompileTask extends GLTask {

        @Override
        public void run() {
            if (!GLShader.this.isValid()) {
                GLShader.this.shaderId = GL20.glCreateShader(GLShader.this.type.value);                
                GL20.glShaderSource(GLShader.this.shaderId, GLShader.this.src);
                GL20.glCompileShader(GLShader.this.shaderId);

                try {
                    final int compStatus = new ParameterQuery(GLShaderParameterName.GL_COMPILE_STATUS)
                            .call();

                    if (compStatus == GL11.GL_FALSE) {
                        final String info = new InfoLogQuery().call();

                        throw new GLException(info);
                    }
                } catch (Exception ex) {
                    throw new GLException("Error querying cause of exception!", ex);
                }               
            }            
        }
    }

    private final DeleteTask deleteTask = new DeleteTask();
    public final void delete() {
        this.deleteTask.glRun(this.getThread());
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

    private ParameterQuery lastParameterQuery = null;
    public int getParameter(final GLShaderParameterName pName) {
        if(this.lastParameterQuery != null
                && this.lastParameterQuery.pName == pName) {
            
            return this.lastParameterQuery.glCall(this.getThread());
        } else {
            this.lastParameterQuery = new ParameterQuery(pName);
            
            return this.lastParameterQuery.glCall(this.getThread());
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

    private final InfoLogQuery infoLogQuery = new InfoLogQuery();
    public String getInfoLog() {
        return this.infoLogQuery.glCall(this.getThread());
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
