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

import static com.longlinkislong.gloop.GLEnableStatus.GL_ENABLED;
import com.longlinkislong.gloop.dsa.DSADriver;
import java.util.Objects;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.Marker;
import org.slf4j.MarkerFactory;

/**
 *
 * @author zmichaels
 */
public class GLPolygonParameters extends GLObject {

    private static final Marker GLOOP_MARKER = MarkerFactory.getMarker("GLOOP");
    private static final Logger LOGGER = LoggerFactory.getLogger("GLPolygonParameters");

    public static final float DEFAULT_POINT_SIZE = 1f;
    public static final float DEFAULT_LINE_SIZE = 1f;
    public static final GLFrontFaceMode DEFAULT_FRONT_FACE = GLFrontFaceMode.GL_CCW;
    public static final GLPolygonMode DEFAULT_MODE = GLPolygonMode.GL_FILL;
    public static final float DEFAULT_POLYGON_OFFSET_FACTOR = 0f;
    public static final float DEFAULT_POLYGON_OFFSET_UNITS = 0f;
    public static final GLCullMode DEFAULT_CULL_MODE = GLCullMode.GL_BACK;

    public final GLEnableStatus cullEnabled;
    public final GLCullMode cullMode;
    public final float pointSize, lineSize;
    public final GLFrontFaceMode frontFace;
    public final GLPolygonMode mode;
    public final float polygonOffsetFactor, polygonOffsetUnits;

    private String name = "";

    /**
     * Assigns a human-readable name to the GLPolygonParameters
     *
     * @param newName the human-readable name.
     * @since 15.12.18
     */
    public final void setName(final CharSequence newName) {
        GLTask.create(() -> {
            LOGGER.trace(
                    GLOOP_MARKER,
                    "Renamed GLPolygonParameters[{}] to GLPolygonParameters[{}]!",
                    this.name,
                    newName);

            this.name = newName.toString();
        }).glRun(this.getThread());
    }

    /**
     * Retrieves the name of the GLPolygonParameters.
     *
     * @return the name.
     * @since 15.12.18
     */
    public final String getName() {
        return this.name;
    }

    /**
     * Constructs a new instance of GLPolygonParameters associated to any OpenGL
     * thread.
     *
     * @since 15.12.18
     */
    public GLPolygonParameters() {
        this(GLThread.getAny());
    }

    /**
     * Constructs a new GLPolygonParameters using default settings on the
     * specified thread.
     *
     * @param thread the thread to create the GLPolygonParmaters on.
     * @since 15.12.18
     */
    public GLPolygonParameters(final GLThread thread) {
        this(
                thread,
                DEFAULT_POINT_SIZE, DEFAULT_LINE_SIZE,
                DEFAULT_FRONT_FACE,
                DEFAULT_MODE,
                DEFAULT_POLYGON_OFFSET_FACTOR, DEFAULT_POLYGON_OFFSET_UNITS,
                GLEnableStatus.GL_ENABLED, DEFAULT_CULL_MODE);
    }

    /**
     * Constructs a new GLPolygonParameters with the specified values.
     *
     * @param thread the thread to construct the GLPolygonParameters on.
     * @param pointSize the point size.
     * @param lineSize the line size.
     * @param frontFace the front face.
     * @param mode the polygon mode.
     * @param polygonOffsetFactor the polygon offset factor.
     * @param polygonOffsetUnits the polygon offset units.
     * @param cullEnabled enable cull mode.
     * @param cullMode type of cull mode to use.
     * @since 15.12.18
     */
    public GLPolygonParameters(
            final GLThread thread,
            final float pointSize, final float lineSize,
            final GLFrontFaceMode frontFace,
            final GLPolygonMode mode,
            final float polygonOffsetFactor, final float polygonOffsetUnits,
            final GLEnableStatus cullEnabled, final GLCullMode cullMode) {

        super(thread);

        LOGGER.trace(
                GLOOP_MARKER,
                "Constructed GLPolygonParameters object on thread: {}",
                thread);

        this.pointSize = pointSize;
        this.lineSize = lineSize;
        this.frontFace = Objects.requireNonNull(frontFace);
        this.mode = Objects.requireNonNull(mode);
        this.polygonOffsetFactor = polygonOffsetFactor;
        this.polygonOffsetUnits = polygonOffsetUnits;
        this.cullEnabled = cullEnabled;
        this.cullMode = Objects.requireNonNull(cullMode);
    }

    /**
     * Constructs a new instance of GLPolygonParameters by copying all of the
     * values and overriding the associated thread.
     *
     * @param thread the OpenGL thread to construct the new GLPolygonParameters
     * on.
     * @return the new GLPolygonParameters object.
     * @since 15.12.18
     */
    public GLPolygonParameters withGLThread(final GLThread thread) {
        return new GLPolygonParameters(
                thread,
                this.pointSize, this.lineSize,
                this.frontFace,
                this.mode,
                this.polygonOffsetFactor, this.polygonOffsetUnits,
                this.cullEnabled, this.cullMode);
    }

    /**
     * Constructs a new instance of GLPolygonParameters by copying all of the
     * values and overriding the point size.
     *
     * @param size the point size of the new GLPolygonParameters object.
     * @return the new GLPolygonParameters object.
     * @since 15.12.18
     */
    public GLPolygonParameters withPointSize(final float size) {
        return new GLPolygonParameters(
                this.getThread(),
                size, this.lineSize,
                this.frontFace,
                this.mode,
                this.polygonOffsetFactor, this.polygonOffsetUnits,
                this.cullEnabled, this.cullMode);
    }

    /**
     * Constructs a new instance of GLPolygonParameters by copying all of the
     * values and overriding the line width.
     *
     * @param size the line width of the new GLPolygonParameters object.
     * @return the new GLPolygonParameters object.
     * @since 15.12.18
     */
    public GLPolygonParameters withLineWidth(final float size) {
        return new GLPolygonParameters(
                this.getThread(),
                this.pointSize, size,
                this.frontFace,
                this.mode,
                this.polygonOffsetFactor, this.polygonOffsetUnits,
                this.cullEnabled, this.cullMode);
    }

    /**
     * Constructs a new instance of GLPolygonParameters by copying all of the
     * values and overriding the front face mode.
     *
     * @param frontFace the front face mode of the new GLPolygonParameters
     * object.
     * @return the new GLPolygonParameters object.
     * @since 15.12.18
     */
    public GLPolygonParameters withFrontFace(final GLFrontFaceMode frontFace) {
        return new GLPolygonParameters(
                this.getThread(),
                this.pointSize, this.lineSize,
                frontFace,
                this.mode,
                this.polygonOffsetFactor, this.polygonOffsetUnits,
                this.cullEnabled, this.cullMode);
    }

    /**
     * Constructs a new instance of GLPolygonParameters by copying all of the
     * values and overriding the polygon mode.
     *
     * @param mode the polygon mode of the new GLPolygonParameters object.
     * @return the new GLPolygonParameters object.
     * @since 15.12.18
     */
    public GLPolygonParameters withPolygonMode(final GLPolygonMode mode) {

        return new GLPolygonParameters(
                this.getThread(),
                this.pointSize, this.lineSize,
                this.frontFace,
                mode,
                this.polygonOffsetFactor, this.polygonOffsetUnits,
                this.cullEnabled, this.cullMode);
    }

    /**
     * Constructs a new instance of GLPolygonParameters by copying all of the
     * values and overriding the polygon offset.
     *
     * @param factor the polygon offset factor of the new GLPolygonParameters
     * object.
     * @param units the polygon offset units of the new GLPolygonParameters
     * object.
     * @return the new GLPolygonParameters object.
     * @since 15.12.18
     */
    public GLPolygonParameters withPolygonOffset(
            final float factor, final float units) {

        return new GLPolygonParameters(
                this.getThread(),
                this.pointSize, this.lineSize,
                this.frontFace,
                this.mode,
                factor, units,
                this.cullEnabled, this.cullMode);
    }

    /**
     * Constructs a new instance of GLPolygonParameters by copying all of the
     * values and overriding the cull mode.
     *
     * @param enabled enables culling of the new GLPolygonParameters object.
     * @param mode the cull mode of the new GLPolygonParameters object.
     * @return the new GLPolygonParameters object.
     * @since 15.12.18
     */
    public GLPolygonParameters withCullMode(final GLEnableStatus enabled, final GLCullMode mode) {
        return new GLPolygonParameters(
                this.getThread(),
                this.pointSize, this.lineSize,
                this.frontFace,
                this.mode,
                this.polygonOffsetFactor, this.polygonOffsetUnits,
                enabled, mode);
    }

    /**
     * Applies the GLPolygonParameters.
     *
     * @since 15.12.18
     */
    public void applyParameters() {
        new ApplyPolygonParametersTask().glRun(this.getThread());
    }

    public class ApplyPolygonParametersTask extends GLTask {

        @Override
        public void run() {
            LOGGER.trace(GLOOP_MARKER, "############### Start GLPolygonParameters Apply Polygon Parameters Task ###############");
            LOGGER.trace(GLOOP_MARKER, "\tApplying GLPolygonParameters[{}]", getName());
            LOGGER.trace(GLOOP_MARKER, "\tPoint size: {}", pointSize);
            LOGGER.trace(GLOOP_MARKER, "\tLine size: {}", lineSize);
            LOGGER.trace(GLOOP_MARKER, "\tFront face: {}", frontFace);
            LOGGER.trace(GLOOP_MARKER, "\tPolygon mode: {}", mode);
            LOGGER.trace(GLOOP_MARKER, "\tPolygon offset factor{} Polygon offset units: {}", polygonOffsetFactor, polygonOffsetUnits);
            LOGGER.trace(GLOOP_MARKER, "\tFace culling: {}, {}", GLPolygonParameters.this.cullEnabled, GLPolygonParameters.this.cullMode);

            final GLThread thread = GLThread
                    .getCurrent()
                    .orElseThrow(GLException::new);

            if (GLPolygonParameters.this.getThread() == thread) {
                thread.currentPolygonParameters = GLPolygonParameters.this;
            } else {
                thread.currentPolygonParameters = GLPolygonParameters.this
                        .withGLThread(thread);
            }

                                    
            GLTools.getDriverInstance().polygonSetParameters(
                    pointSize, 
                    lineSize, 
                    frontFace.value,
                    cullEnabled == GL_ENABLED ? cullMode.value : 0L,
                    mode.value,
                    polygonOffsetFactor, polygonOffsetUnits);            

            LOGGER.trace(GLOOP_MARKER, "############### End GLPolygonParameters Apply Polygon Parameters Task ###############");
        }
    }
}
