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

import com.longlinkislong.gloop.spi.Driver;
import com.longlinkislong.gloop.spi.Shader;
import java.util.Objects;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.Marker;
import org.slf4j.MarkerFactory;

/**
 * An OpenGL object that represents shader code.
 *
 * @author zmichaels
 * @since 15.05.27
 */
public class GLShader extends GLObject {

    private static final Marker GLOOP_MARKER = MarkerFactory.getMarker("GLOOP");
    private static final Logger LOGGER = LoggerFactory.getLogger("GLShader");

    private final String src;
    private final GLShaderType type;
    private String name = "";

    volatile transient Shader shader;

    /**
     * Sets the name of the GLShader.
     *
     * @param name the name of the shader.
     * @since 15.12.18
     */
    public final void setName(final CharSequence name) {
        GLTask.create(() -> {
            LOGGER.trace(
                    GLOOP_MARKER,
                    "Renamed GLShader[{}] to GLShader[{}]",
                    this.name,
                    name);

            this.name = name.toString();
        }).glRun(this.getThread());
    }

    /**
     * Retrieves the name of the GLShader.
     *
     * @return the name.
     * @since 15.12.18
     */
    public final String getName() {
        return this.name;
    }

    /**
     * Compiles the source as a vertex shader.
     *
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
        this(GLThread.getAny(), type, src);
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

        LOGGER.trace(
                GLOOP_MARKER,
                "Constructed GLShader object on thread: {}",
                thread);

        this.src = src.toString();
        this.type = Objects.requireNonNull(type);

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
        return shader != null && shader.isValid();
    }

    @Override
    public String toString() {
        return "GLShader [" + this.type + "]: " + this.name;
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
            LOGGER.trace(GLOOP_MARKER, "############### Start GLShader Compile Task ###############");
            LOGGER.trace(GLOOP_MARKER, "\tShader Type: {}", GLShader.this.type);

            final Driver driver = GLTools.getDriverInstance();

            if (!GLShader.this.isValid()) {
                shader = driver.shaderCompile(type.value, src);
                final int status = (int) driver.shaderGetParameter(shader, GLShaderParameterName.GL_COMPILE_STATUS.value);

                if (status == 0) {
                    final String info = GLShader.this.getInfoLog();
                    
                    throw new GLException(info);
                }

                LOGGER.trace(
                        GLOOP_MARKER,
                        "############### End GLShader Compile Task ###############");
            }
        }
    }

    /**
     * Deletes the GLShader.
     *
     * @since 15.05.27
     */
    public final void delete() {
        new DeleteTask().glRun(this.getThread());
    }

    /**
     * A GLTask that deletes the GLShader.
     *
     * @since 15.05.27
     */
    public class DeleteTask extends GLTask {

        @Override
        public void run() {
            LOGGER.trace(GLOOP_MARKER, "############### Start GLShader Delete Task ###############");
            LOGGER.trace(GLOOP_MARKER, "\tDeleting GLShader[{}]", GLShader.this.getName());

            if (GLShader.this.isValid()) {
                GLTools.getDriverInstance().shaderDelete(shader);
                shader = null;
            }

            LOGGER.trace(GLOOP_MARKER, "############### End GLShader Delete Task ###############");
        }
    }

    /**
     * Runs a GLQuery on the GLShader.
     *
     * @param pName the query parameter to run.
     * @return the result of the query.
     * @since 15.05.27
     */
    public int getParameter(final GLShaderParameterName pName) {
        return new ParameterQuery(pName).glCall(this.getThread());
    }

    /**
     * A GLQuery that requests information about the GLShader. The GLShader must
     * be valid for the query to run.
     *
     * @since 15.05.27
     */
    public final class ParameterQuery extends GLQuery<Integer> {

        private final GLShaderParameterName pName;

        public ParameterQuery(final GLShaderParameterName pName) {
            this.pName = Objects.requireNonNull(pName);
        }

        @Override
        public Integer call() throws Exception {
            LOGGER.trace(GLOOP_MARKER, "############### Start GLShader Parameter Query ###############");
            LOGGER.trace(GLOOP_MARKER, "\tQuerying GLShader[{}]", getName());
            LOGGER.trace(GLOOP_MARKER, "\tParameter: {}", this.pName);

            if (!GLShader.this.isValid()) {
                throw new GLException("Invalid GLShader!");
            }

            final int res = (int) GLTools.getDriverInstance().shaderGetParameter(shader, pName.value);

            LOGGER.trace(
                    GLOOP_MARKER,
                    "GLShader[{}].{} = {}",
                    GLShader.this.getName(),
                    this.pName,
                    res);

            LOGGER.trace(
                    GLOOP_MARKER,
                    "############### End GLShader Parameter Query ###############");

            return res;
        }
    }

    /**
     * Runs an InfoLogQuery on the GLShader.
     *
     * @return the info log.
     * @since 15.05.27
     */
    public String getInfoLog() {
        return new InfoLogQuery().glCall(this.getThread());
    }

    /**
     * A GLQuery that requests for the info log associated to the GLShader.
     *
     * @since 15.05.27
     */
    public class InfoLogQuery extends GLQuery<String> {

        @Override
        public String call() throws Exception {
            LOGGER.trace(GLOOP_MARKER, "############### Start GLShader Info Log Query ###############");
            LOGGER.trace(GLOOP_MARKER, "\tQuerying GLShader[{}]", GLShader.this.getName());

            if (!GLShader.this.isValid()) {
                throw new GLException("Invalid GLShader!");
            }

            final String infoLog = GLTools.getDriverInstance().shaderGetInfoLog(shader);

            LOGGER.trace(
                    GLOOP_MARKER,
                    "GLShader[{}].infoLog = {}",
                    GLShader.this.getName(),
                    infoLog);

            LOGGER.trace(GLOOP_MARKER, "############### End GLShader Info Log Query ###############");
            return infoLog;
        }
    }
}
