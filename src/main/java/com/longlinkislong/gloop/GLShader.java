/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.longlinkislong.gloop;

import static com.longlinkislong.gloop.GLAsserts.checkGLError;
import static com.longlinkislong.gloop.GLAsserts.glErrorMsg;
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
    int shaderId = INVALID_SHADER_ID;

    /**
     * Compiles the source as a vertex shader.
     * @param src the source to compile.
     * @return the shader object.
     * @since 15.11.04
     */
    public static GLShader newVertexShader(final CharSequence src) {
        return new GLShader(GLShaderType.GL_VERTEX_SHADER, src);
    }

    /**
     * Compiles the source as a fragment shader.
     *
     * @param src the source to compile.
     * @return the shader object.
     * @since 15.11.04
     */
    public static GLShader newFragmentShader(final CharSequence src) {
        return new GLShader(GLShaderType.GL_FRAGMENT_SHADER, src);
    }

    /**
     * Compiles the source as a geometry shader.
     *
     * @param src the source to compile.
     * @return the shader object.
     * @since 15.11.04
     */
    public static GLShader newGeometryShader(final CharSequence src) {
        return new GLShader(GLShaderType.GL_GEOMETRY_SHADER, src);
    }

    /**
     * Compiles the source as a compute shader.
     *
     * @param src the source to compile.
     * @return the shader object.
     * @since 15.11.04
     */
    public static GLShader newComputeShader(final CharSequence src) {
        return new GLShader(GLShaderType.GL_COMPUTE_SHADER, src);
    }

    /**
     * Compiles the source as a tessellation control shader.
     *
     * @param src the source to compile.
     * @return the shader object.
     * @since 15.11.04
     */
    public static GLShader newTessControlShader(final CharSequence src) {
        return new GLShader(GLShaderType.GL_TESS_CONTROL_SHADER, src);
    }

    /**
     * Compiles the source as a tessellation evaluation shader.
     *
     * @param src the source to compile.
     * @return the shader object.
     * @since 15.11.04
     */
    public static GLShader newTessEvaluationShader(final CharSequence src) {
        return new GLShader(GLShaderType.GL_TESS_EVALUATION_SHADER, src);
    }

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
                assert checkGLError() : glErrorMsg("glCreateShader(I)", GLShader.this.type);

                GL20.glShaderSource(GLShader.this.shaderId, GLShader.this.src);
                assert checkGLError() : glErrorMsg("glShaderSource(IS)", GLShader.this.shaderId, GLShader.this.src);

                GL20.glCompileShader(GLShader.this.shaderId);
                assert checkGLError() : glErrorMsg("glCompileShader(I)", GLShader.this.shaderId);

                final int status = GL20.glGetShaderi(GLShader.this.shaderId, GLShaderParameterName.GL_COMPILE_STATUS.value);
                assert checkGLError() : glErrorMsg("glGetShaderi(II)", GLShader.this.shaderId, GLShaderParameterName.GL_COMPILE_STATUS);

                if (status == GL11.GL_FALSE) {
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
                assert checkGLError() : glErrorMsg("glDeleteShader(I)", GLShader.this.shaderId);

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
            assert checkGLError() : glErrorMsg("glGetShaderi(II)", GLShader.this.shaderId, pName);

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

            final int length = GL20.glGetShaderi(GLShader.this.shaderId, GLShaderParameterName.GL_INFO_LOG_LENGTH.value);
            assert checkGLError() : glErrorMsg("glGetShaderi(II)", GLShader.this.shaderId, GLShaderParameterName.GL_INFO_LOG_LENGTH);

            final String log = GL20.glGetShaderInfoLog(GLShader.this.shaderId, length);
            assert checkGLError() : glErrorMsg("glGetShaderInfoLog(II)", GLShader.this.shaderId, length);

            return log;
        }
    }
}
