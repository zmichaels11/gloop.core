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

import java.util.Objects;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.Marker;
import org.slf4j.MarkerFactory;

/**
 * A representation of blend states to use.
 *
 * @author zmichaels
 * @since 15.06.18
 */
public class GLBlending extends GLObject {

    private static final Marker GLOOP_MARKER = MarkerFactory.getMarker("GLOOP");
    private static final Logger LOGGER = LoggerFactory.getLogger("GLBlending");
    /**
     * Default RGB blend equation used by OpenGL.
     *
     * @since 15.06.18
     */
    public static final GLBlendEquation DEFAULT_RGB_BLEND = GLBlendEquation.GL_FUNC_ADD;
    /**
     * Default alpha blend equation used by OpenGL.
     *
     * @since 15.06.18
     */
    public static final GLBlendEquation DEFAULT_ALPHA_BLEND = GLBlendEquation.GL_FUNC_ADD;
    /**
     * Default RGB blend function used on source color for OpenGL.
     *
     * @since 15.06.18
     */
    public static final GLBlendFunc DEFAULT_RGB_FUNC_SRC = GLBlendFunc.GL_ONE;
    /**
     * Default RGB blend function used on destination color for OpenGL.
     *
     * @since 15.06.18
     */
    public static final GLBlendFunc DEFAULT_RGB_FUNC_DST = GLBlendFunc.GL_ZERO;
    /**
     * Default alpha blend function used on source color for OpenGL.
     *
     * @since 15.06.18
     */
    public static final GLBlendFunc DEFAULT_ALPHA_FUNC_SRC = GLBlendFunc.GL_ONE;
    /**
     * Default alpha blend function used on destination color for OpenGL
     *
     * @since 15.06.18
     */
    public static final GLBlendFunc DEFAULT_ALPHA_FUNC_DST = GLBlendFunc.GL_ZERO;

    /**
     * Enabled status for blending. Blending will occur when set if this is set
     * to GL_ENABLED.
     *
     * @since 15.06.18
     */
    public final GLEnableStatus enabled;
    /**
     * Blend equation used on RGB color elements
     *
     * @since 15.06.18
     */
    public final GLBlendEquation rgbBlend;
    /**
     * Blend equation used on alpha color elements
     *
     * @since 15.06.18
     */
    public final GLBlendEquation alphaBlend;
    /**
     * Blend function used on RGB source color elements.
     *
     * @since 15.06.18
     */
    public final GLBlendFunc rgbFuncSrc;
    /**
     * Blend function used on RGB destination color elements.
     *
     * @since 15.06.18
     */
    public final GLBlendFunc rgbFuncDst;
    /**
     * Blend function used on alpha source color elements.
     *
     * @since 15.06.18
     */
    public final GLBlendFunc alphaFuncSrc;
    /**
     * Blend function used on alpha destination color elements.
     *
     * @since 15.06.18
     */
    public final GLBlendFunc alphaFuncDst;

    private volatile String name = "id=" + System.currentTimeMillis();

    /**
     * Sets the name of the GLBlending object
     *
     * @param newName sets the name of the blending object.
     * @since 16.04.04
     */
    public final void setName(final CharSequence newName) {
        GLTask.create(() -> {
            LOGGER.trace(
                    GLOOP_MARKER,
                    "Renamed GLBlending[{}] to GLBlending[{}]!",
                    this.name,
                    newName);

            this.name = newName.toString();
        }).glRun(this.getThread());
    }

    /**
     * Retrieves the name of the GLBlending object.
     *
     * @return the name.
     * @since 15.12.18
     */
    public final String getName() {
        return this.name;
    }

    /**
     * Constructs a new GLBlending object for the default OpenGL thread using
     * the default settings.
     *
     * @since 15.06.18
     */
    public GLBlending() {
        this(GLThread.getAny());
    }

    /**
     * Constructs a new GLBlending object for the specified OpenGL thread using
     * the default settings.
     *
     * @param thread the thread to create the GLBlending object on.
     * @since 15.06.18
     */
    public GLBlending(final GLThread thread) {
        this(
                thread,
                GLEnableStatus.GL_DISABLED,
                DEFAULT_RGB_BLEND, DEFAULT_ALPHA_BLEND,
                DEFAULT_RGB_FUNC_SRC, DEFAULT_RGB_FUNC_DST,
                DEFAULT_ALPHA_FUNC_SRC, DEFAULT_ALPHA_FUNC_DST);
    }

    /**
     * Constructs a new GLBlending object for the specified OpenGL thread using
     * the specified parameters.
     *
     * @param thread the thread to create the GLBlending object on.
     * @param enabled specifies if blending is enabled.
     * @param rgbBlend the RGB blend equation.
     * @param alphaBlend the alpha blend equation.
     * @param rgbFuncSrc the RGB source blend function.
     * @param rgbFuncDst the RGB destination blend function.
     * @param alphaFuncSrc the alpha source blend function.
     * @param alphaFuncDst the alpha destination blend function.
     * @since 15.06.18
     */
    public GLBlending(
            final GLThread thread,
            final GLEnableStatus enabled,
            final GLBlendEquation rgbBlend,
            final GLBlendEquation alphaBlend,
            final GLBlendFunc rgbFuncSrc, final GLBlendFunc rgbFuncDst,
            final GLBlendFunc alphaFuncSrc, final GLBlendFunc alphaFuncDst) {

        super(thread);

        LOGGER.trace(
                GLOOP_MARKER,
                "Constructed GLBlending object on thread: {}",
                thread);

        this.enabled = Objects.requireNonNull(enabled);
        this.rgbBlend = Objects.requireNonNull(rgbBlend);
        this.alphaBlend = Objects.requireNonNull(alphaBlend);
        this.rgbFuncSrc = Objects.requireNonNull(rgbFuncSrc);
        this.rgbFuncDst = Objects.requireNonNull(rgbFuncDst);
        this.alphaFuncSrc = Objects.requireNonNull(alphaFuncSrc);
        this.alphaFuncDst = Objects.requireNonNull(alphaFuncDst);
    }

    /**
     * Copies the GLBlending object onto the specified OpenGL thread.
     *
     * @param thread the OpenGL thread to copy the object to.
     * @return the GLBlending object.
     * @since 15.07.01
     */
    public GLBlending withGLThread(final GLThread thread) {
        return new GLBlending(
                thread,
                this.enabled,
                this.rgbBlend, this.alphaBlend,
                this.rgbFuncSrc, this.rgbFuncDst,
                this.alphaFuncSrc, this.alphaFuncDst);
    }

    /**
     * Copies the GLBlending object and overrides the blend equations.
     *
     * @param rgb the RGB blend equation.
     * @param alpha the alpha blend equation.
     * @return the GLBlending object.
     * @see
     * <a href="https://www.opengl.org/wiki/GLAPI/glBlendEquationSeparate">glBlendEquation
     * (OpenGL Wiki)</a>
     * @see
     * <a href="https://www.opengl.org/sdk/docs/man/html/glBlendEquation.xhtml">glBlendEquation
     * (OpenGL Wiki)</a>
     * @since 15.06.18
     */
    public GLBlending withBlendEquation(
            final GLBlendEquation rgb, final GLBlendEquation alpha) {

        return new GLBlending(
                this.getThread(),
                this.enabled,
                rgb, alpha,
                this.rgbFuncSrc, this.rgbFuncDst,
                this.alphaFuncSrc, this.alphaFuncDst);
    }

    /**
     * Copies the GLBlending object and overrides the enabled status.
     *
     * @param isEnabled if blending should be enabled.
     * @return the GLBlending object
     * @see <a href="https://www.opengl.org/wiki/GLAPI/glEnable">glEnable
     * (OpenGL Wiki)</a>
     * @see
     * <a href="https://www.opengl.org/sdk/docs/man/html/glEnable.xhtml">glEnable
     * (OpenGL SDK)</a>
     * @since 15.06.18
     */
    public GLBlending withEnabled(final GLEnableStatus isEnabled) {

        return new GLBlending(
                this.getThread(),
                isEnabled,
                this.rgbBlend, this.alphaBlend,
                this.rgbFuncSrc, this.rgbFuncDst,
                this.alphaFuncSrc, this.alphaFuncDst);
    }

    /**
     * Copies the GLBlending object and overrides the source and destination
     * blend functions.
     *
     * @param rgbSrc the blend function for RGB source color.
     * @param rgbDst the blend function for RGB destination color.
     * @param alphaSrc the blend function for alpha source color.
     * @param alphaDst the blend function for alpha destination color.
     * @return the blend function
     * @see
     * <a href="https://www.opengl.org/wiki/GLAPI/glBlendFuncSeparate">glBlendFunc/Separate
     * (OpenGL Wiki)</a>
     * @see
     * <a href="https://www.opengl.org/sdk/docs/man/html/glBlendFuncSeparate.xhtml">glBlendFuncSeparate
     * (OpenGL SDK)</a>
     * @since 15.06.18
     */
    public GLBlending withBlendFunc(
            final GLBlendFunc rgbSrc, final GLBlendFunc rgbDst,
            final GLBlendFunc alphaSrc, final GLBlendFunc alphaDst) {

        return new GLBlending(
                this.getThread(),
                this.enabled,
                this.rgbBlend, this.alphaBlend,
                rgbSrc, rgbDst,
                alphaSrc, alphaDst);
    }

    private ApplyBlendingTask applyTask = null;

    /**
     * Applies all of the blend parameters on the GLBlending object's GLThread.
     *
     * @since 15.06.18
     */
    public final void applyBlending() {
        if (this.applyTask == null) {
            this.applyTask = new ApplyBlendingTask();
        }

        this.applyTask.glRun(this.getThread());
    }

    /**
     * GLTask that sets the OpenGL blending parameters.
     *
     * @since 15.06.18
     */
    public class ApplyBlendingTask extends GLTask {

        @Override
        public void run() {
            LOGGER.trace(GLOOP_MARKER, "############## Start GLBlending Apply Blending Task ##############");
            LOGGER.trace(GLOOP_MARKER, "\tApplying GLBlending[{}]", GLBlending.this.getName());
            LOGGER.trace(GLOOP_MARKER, "\tEnabled: {}", GLBlending.this.getName());
            LOGGER.trace(GLOOP_MARKER, "\tRGB blend equation: {} alpha blend equation: {}", GLBlending.this.rgbBlend, GLBlending.this.alphaBlend);
            LOGGER.trace(GLOOP_MARKER, "\trgb func src: {} rgb func dst: {}", GLBlending.this.rgbFuncSrc, GLBlending.this.rgbFuncDst);
            LOGGER.trace(GLOOP_MARKER, "\talpha func src: {} alpha func dst: {}", GLBlending.this.alphaFuncSrc, GLBlending.this.alphaFuncDst);

            final GLThread thread = GLThread.getCurrent().orElseThrow(GLException::new);            

            thread.runOnBlendChangeCallback(thread.currentBlend, GLBlending.this);
            thread.currentBlend = GLBlending.this.withGLThread(thread);
            
            switch (GLBlending.this.enabled) {
                case GL_ENABLED:
                    GLTools.getDriverInstance().blendingEnable(
                            GLBlending.this.rgbBlend.value, 
                            GLBlending.this.alphaBlend.value, 
                            GLBlending.this.rgbFuncSrc.value, GLBlending.this.rgbFuncDst.value,
                            GLBlending.this.alphaFuncSrc.value, GLBlending.this.alphaFuncDst.value);
                    
                    break;
                case GL_DISABLED:
                    GLTools.getDriverInstance().blendingDisable();
                    break;
            }

            LOGGER.trace(GLOOP_MARKER, "############### End GLBlending Apply Blending Task ###############");
        }
    }

    @Override
    public boolean equals(final Object other) {
        if (this == other) {
            return true;
        } else if (other instanceof GLBlending) {
            final GLBlending oBlend = (GLBlending) other;

            return this.enabled == oBlend.enabled
                    && this.alphaBlend == oBlend.alphaBlend
                    && this.alphaFuncDst == oBlend.alphaFuncDst
                    && this.alphaFuncSrc == oBlend.alphaFuncSrc
                    && this.rgbBlend == oBlend.rgbBlend
                    && this.rgbFuncDst == oBlend.rgbFuncDst
                    && this.rgbFuncSrc == oBlend.rgbFuncSrc;
        } else {
            return false;
        }
    }

    @Override
    public int hashCode() {
        int hash = 3;
        hash = 59 * hash + Objects.hashCode(this.enabled);
        hash = 59 * hash + Objects.hashCode(this.rgbBlend);
        hash = 59 * hash + Objects.hashCode(this.alphaBlend);
        hash = 59 * hash + Objects.hashCode(this.rgbFuncSrc);
        hash = 59 * hash + Objects.hashCode(this.rgbFuncDst);
        hash = 59 * hash + Objects.hashCode(this.alphaFuncSrc);
        hash = 59 * hash + Objects.hashCode(this.alphaFuncDst);
        return hash;
    }

    @Override
    public String toString() {
        return String.format("GLBlending: blend=%s rgb=[src=%s dst=%s] alpha=[src=%s dst=%s]",
                this.rgbBlend,
                this.rgbFuncSrc, this.rgbFuncDst,
                this.alphaFuncSrc, this.alphaFuncDst);
    }
}
