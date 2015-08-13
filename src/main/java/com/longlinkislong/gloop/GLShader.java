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
 * An OpenGL object that represents shader code.
 *
 * @author zmichaels
 * @since 15.05.27
 */
public class GLShader extends GLObject {

    private static final int INVALID_SHADER_ID = -1;
    private final String src;
    private final GLShaderType type;
    protected int shaderId = INVALID_SHADER_ID;

    /**
     * Constructs a new GLShader on the default GLThread of the specified shader
     * type with the specified source. The GLShader will be compiled on
     * construction.
     *
     * @param type the type of shader to create
     * @param src the source to use
     * @since 15.05.27
     */
    public GLShader(final GLShaderType type, final CharSequence src) {
        super();
        this.src = src.toString();
        Objects.requireNonNull(this.type = type);
        this.compile();
    }

    /**
     * Constructs a new GLShader on the specified GLThread of the specified
     * shader type and specified source. The GLShader will be compiled on
     * construction.
     *
     * @param thread the thread to create the GLShader on.
     * @param type the type of shader to create.
     * @param src the source to create the shader with.
     * @since 15.05.27
     */
    public GLShader(
            final GLThread thread,
            final GLShaderType type, final CharSequence src) {

        super(thread);
        this.src = src.toString();
        Objects.requireNonNull(this.type = type);
        this.compile();
    }

    /**
     * Checks if the GLShader is valid. A GLShader is considered valid after it
     * is compiled and before it is deleted.
     *
     * @return true if the GLShader is valid.
     * @since 15.05.27
     */
    public boolean isValid() {
        return this.shaderId != INVALID_SHADER_ID;
    }

    @Override
    public String toString() {
        return "GLShader [" + this.type + "]: " + this.shaderId;
    }

    /**
     * Retrieves the source of the GLShader.
     *
     * @return the source
     * @since 15.05.27
     */
    public String getSource() {
        return this.src;
    }

    /**
     * Compiles the GLShader.
     *
     * @since 15.05.27
     */
    public final void compile() {
        new CompileTask().glRun(this.getThread());
    }

    /**
     * A GLTask that compiles and initialized the GLShader.
     *
     * @since 15.05.27
     */
    public class CompileTask extends GLTask {

        @Override
        public void run() {
            if (!GLShader.this.isValid()) {
                GLShader.this.shaderId = GL20.glCreateShader(GLShader.this.type.value);
                
                assert GL11.glGetError() == GL11.GL_NO_ERROR : String.format("glCreateShader(%s) failed!", GLShader.this.type);                
                assert GLShader.this.shaderId != INVALID_SHADER_ID : "glCreateShader did not return a valid shader id!";
                
                GL20.glShaderSource(GLShader.this.shaderId, GLShader.this.src);
                
                assert GL11.glGetError() == GL11.GL_NO_ERROR : String.format("glShaderSource(%d, [source]) failed!", GLShader.this.shaderId);
                
                GL20.glCompileShader(GLShader.this.shaderId);

                assert GL11.glGetError() == GL11.GL_NO_ERROR : String.format("glCompileShader(%d) failed!", GLShader.this.shaderId);
                
                final GLBoolean status = GLBoolean.valueOf(
                        GLShader.this.getParameter(
                                GLShaderParameterName.GL_COMPILE_STATUS));                
                
                if (status == GLBoolean.GL_FALSE) {
                    final String info = GLShader.this.getInfoLog();

                    throw new GLException(info);
                }
            }
        }
    }

    private final DeleteTask deleteTask = new DeleteTask();

    /**
     * Deletes the GLShader.
     *
     * @since 15.05.27
     */
    public final void delete() {
        this.deleteTask.glRun(this.getThread());
    }

    /**
     * A GLTask that deletes the GLShader.
     *
     * @since 15.05.27
     */
    public class DeleteTask extends GLTask {

        @Override
        public void run() {
            if (GLShader.this.isValid()) {
                GL20.glDeleteShader(GLShader.this.shaderId);
                
                assert GL11.glGetError() == GL11.GL_NO_ERROR : String.format("glDeleteShader(%d) failed!", GLShader.this.shaderId);
                
                GLShader.this.shaderId = INVALID_SHADER_ID;
            }
        }
    }

    private ParameterQuery lastParameterQuery = null;

    /**
     * Runs a GLQuery on the GLShader.
     *
     * @param pName the query parameter to run.
     * @return the result of the query.
     * @since 15.05.27
     */
    public int getParameter(final GLShaderParameterName pName) {
        if (this.lastParameterQuery != null
                && this.lastParameterQuery.pName == pName) {

            return this.lastParameterQuery.glCall(this.getThread());
        } else {
            this.lastParameterQuery = new ParameterQuery(pName);

            return this.lastParameterQuery.glCall(this.getThread());
        }
    }

    /**
     * A GLQuery that requests information about the GLShader. The GLShader must
     * be valid for the query to run.
     *
     * @since 15.05.27
     */
    public class ParameterQuery extends GLQuery<Integer> {

        final GLShaderParameterName pName;

        public ParameterQuery(final GLShaderParameterName pName) {
            this.pName = pName;
        }

        @Override
        public Integer call() throws Exception {
            if (!GLShader.this.isValid()) {
                throw new GLException("Invalid GLShader!");
            }

            final int rVal = GL20.glGetShaderi(GLShader.this.shaderId, pName.value);
            
            assert GL11.glGetError() == GL11.GL_NO_ERROR : String.format("glGetShaderi(%d, %s) = %d failed!", GLShader.this.shaderId, pName, rVal);
            
            return rVal;
        }
    }

    private final InfoLogQuery infoLogQuery = new InfoLogQuery();

    /**
     * Runs an InfoLogQuery on the GLShader.
     *
     * @return the info log.
     * @since 15.05.27
     */
    public String getInfoLog() {
        return this.infoLogQuery.glCall(this.getThread());
    }

    /**
     * A GLQuery that requests for the info log associated to the GLShader.
     *
     * @since 15.05.27
     */
    public class InfoLogQuery extends GLQuery<String> {

        @Override
        public String call() throws Exception {
            if (!GLShader.this.isValid()) {
                throw new GLException("Invalid GLShader!");
            }

            final int length = GL20.glGetShaderi(
                    GLShader.this.shaderId,
                    GLShaderParameterName.GL_INFO_LOG_LENGTH.value);

            assert GL11.glGetError() == GL11.GL_NO_ERROR : String.format("glGetShaderi(%d, GL_INFO_LOG_LENGTH) = %d failed!", GLShader.this.shaderId, length);

            final String log = GL20.glGetShaderInfoLog(GLShader.this.shaderId, length);
            
            assert GL11.glGetError() == GL11.GL_NO_ERROR : String.format("glGetShaderInfoLog(%d, %d) = %s failed!", GLShader.this.shaderId, length, log);
            
            return log;
        }
    }
}
