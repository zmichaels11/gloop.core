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
 * GLTextureParameters is an immutable structure that defines multiple texture
 * parameters.
 *
 * @author zmichaels
 * @since 15.12.18
 */
public class GLTextureParameters {

    private static final Marker GLOOP_MARKER = MarkerFactory.getMarker("GLOOP");
    private static final Logger LOGGER = LoggerFactory.getLogger("GLTextureParameters");

    /**
     * The default filtering method to use for shrinking a texture. OpenGL
     * specifies that the default minification filter is
     * GL_NEAREST_MIPMAP_LINEAR.
     *
     * @since 15.12.17
     */
    public static final GLTextureMinFilter DEFAULT_MIN_FILTER = GLTextureMinFilter.GL_NEAREST_MIPMAP_LINEAR;
    /**
     * The default filtering method to use for expanding a texture. OpenGL
     * specifies that the default magnification filter is GL_LINEAR.
     *
     * @since 15.12.17
     */
    public static final GLTextureMagFilter DEFAULT_MAG_FILTER = GLTextureMagFilter.GL_LINEAR;
    /**
     * The default constraints for minification mipmaps. OpenGL specifies that
     * the default minimum LOD is -1000.0.
     *
     * @since 15.12.17
     */
    public static final float DEFAULT_MIN_LOD = -1000f;
    /**
     * The default constraints for magnifying mipmaps. OpenGL specifies that the
     * default maximum LOD is 1000.0.
     *
     * @since 15.12.17
     */
    public static final float DEFAULT_MAX_LOD = 1000f;
    /**
     * The default rule for texture wrapping along the S-axis. OpenGL specifies
     * the default wrap rule to be GL_REPEAT.
     *
     * @since 15.12.17
     */
    public static final GLTextureWrap DEFAULT_WRAP_S = GLTextureWrap.GL_REPEAT;
    /**
     * The default rule for texture wrapping along the T-axis. OpenGL specifies
     * the default wrap rule to be GL_REPEAT.
     *
     * @since 15.12.17
     */
    public static final GLTextureWrap DEFAULT_WRAP_T = GLTextureWrap.GL_REPEAT;
    /**
     * The default rule for texture wrapping along the R-axis. OpenGL specifies
     * the default wrap rule to be GL_REPEAT.
     *
     * @since 15.12.17
     */
    public static final GLTextureWrap DEFAULT_WRAP_R = GLTextureWrap.GL_REPEAT;
    /**
     * The default level of anisotropic filtering. OpenGL specifies the default
     * as 1.0, which indicates isotropic filtering.
     *
     * @since 15.12.18
     */
    public static final float DEFAULT_ANISOTROPIC_LEVEL = 1f;

    /**
     * Default sparse texture allocation.
     *
     * @since 16.01.05
     */
    public static final boolean DEFAULT_SPARSE_ALLOCATION = false;

    /**
     * Specifies if the texture should be allocated as a sparse texture.
     *
     * @since 16.01.05
     */
    public final boolean isSparse;

    /**
     * The wrap rule along the S-axis. (Used in 1D, 2D, and 3D textures).
     *
     * @since 15.12.17
     */
    public final GLTextureWrap wrapS;
    /**
     * The wrap rule along the T-axis. (Used in 2D and 3D textures).
     *
     * @since 15.12.17
     */
    public final GLTextureWrap wrapT;
    /**
     * The wrap rule along the R-axis. (Used in 3D textures).
     *
     * @since 15.12.17
     */
    public final GLTextureWrap wrapR;

    /**
     * The filter applied to the texture when minifying it.
     *
     * @since 15.12.17
     */
    public final GLTextureMinFilter minFilter;
    /**
     * The filter applied to the texture when magnifying it.
     *
     * @since 15.12.17
     */
    public final GLTextureMagFilter magFilter;
    /**
     * The minimum level of detail.
     *
     * @since 15.12.17
     */
    public final float minLOD;
    /**
     * The maximum level of detail.
     *
     * @since 15.12.17
     */
    public final float maxLOD;
    /**
     * The level of anisotropic filtering. 1.0 specifies isotropic filtering.
     *
     * @since 15.12.18
     */
    public final float anisotropicLevel;

    private String name = "id=" + System.currentTimeMillis();

    /**
     * Assigns a human-readable name to the GLTextureParameters object.
     *
     * @param newName the new name.
     * @since 15.12.18
     */
    public final void setName(final CharSequence newName) {
        LOGGER.trace(
                GLOOP_MARKER,
                "Renamed GLTextureParameters[{}] to GLTextureParameters[{}]!",
                this.name, newName);

        this.name = newName.toString();
    }

    /**
     * Retrieves the name of the GLTextureParameters object.
     *
     * @return the name.
     * @since 15.12.18
     */
    public final String getName() {
        return this.name;
    }

    /**
     * Constructs a new instance of GLTextureParameters using the OpenGL
     * defaults.
     *
     * @since 15.12.17
     */
    public GLTextureParameters() {
        this(
                DEFAULT_MIN_FILTER, DEFAULT_MAG_FILTER,
                DEFAULT_MIN_LOD, DEFAULT_MAX_LOD,
                DEFAULT_WRAP_S, DEFAULT_WRAP_T, DEFAULT_WRAP_R,
                DEFAULT_ANISOTROPIC_LEVEL,
                DEFAULT_SPARSE_ALLOCATION);
    }

    /**
     * Constructs a new instance of GLTextureParameters using the specified
     * values.
     *
     * @param minFilter the minification filter.
     * @param magFilter the magnification filter.
     * @param minLOD the minimum level of detail.
     * @param maxLOD the maximum level of detail.
     * @param wrapS the wrap rule along the s-axis.
     * @param wrapT the wrap rule along the t-axis. (ignored for 1D textures)
     * @param wrapR the wrap rule along the r-axis. (ignored for 1D and 2D
     * textures).
     * @param anisotropicLevel the level of anisotropic filtering. 1.0 specifies
     * isotropic filtering.
     * @param isSparse true to allocate texture as a sparse texture.
     * @since 15.12.17
     */
    public GLTextureParameters(
            final GLTextureMinFilter minFilter, final GLTextureMagFilter magFilter,
            final float minLOD, final float maxLOD,
            final GLTextureWrap wrapS, final GLTextureWrap wrapT, final GLTextureWrap wrapR,
            final float anisotropicLevel, final boolean isSparse) {

        this.isSparse = isSparse;
        this.wrapR = Objects.requireNonNull(wrapR);
        this.wrapS = Objects.requireNonNull(wrapS);
        this.wrapT = Objects.requireNonNull(wrapT);
        this.minFilter = Objects.requireNonNull(minFilter);
        this.magFilter = Objects.requireNonNull(magFilter);

        if (!Float.isFinite(this.minLOD = minLOD)) {
            throw new ArithmeticException("MinLOD must be a finite number!");
        } else if (!Float.isFinite(this.maxLOD = maxLOD)) {
            throw new ArithmeticException("MaxLOD must be a finite number!");
        } else if (!Float.isFinite(anisotropicLevel)) {
            throw new ArithmeticException("Anisotropic level must be a finite number!");
        } else if (anisotropicLevel < 1.0f) {
            throw new ArithmeticException("Anisotropic level must be greater than or equal to 1.0!");
        } else {
            this.anisotropicLevel = anisotropicLevel;
        }
    }

    /**
     * Creates a new instance of GLTextureParameters by copying all of the
     * values and overriding the wrap rules.
     *
     * @param wrapS the wrap rule along the s-axis.
     * @param wrapT the wrap rule along the t-axis. (ignored in 1D textures)
     * @param wrapR the wrap rule along the R-axis. (ignored in 1D and 2D
     * textures).
     * @return the new GLTextureParameter.
     * @since 15.12.17
     */
    public GLTextureParameters withWrap(
            final GLTextureWrap wrapS, final GLTextureWrap wrapT, final GLTextureWrap wrapR) {

        return new GLTextureParameters(
                this.minFilter, this.magFilter,
                this.minLOD, this.maxLOD,
                wrapS, wrapT, wrapR,
                this.anisotropicLevel,
                this.isSparse);
    }

    /**
     * Creates a new instance of GLTextureParameters by copying all of the
     * values and overriding the level of detail.
     *
     * @param minLOD the minimum level of detail.
     * @param maxLOD the maximum level of detail.
     * @return the new GLTextureParameter
     * @since 15.12.17
     */
    public GLTextureParameters withLOD(final float minLOD, final float maxLOD) {

        return new GLTextureParameters(
                this.minFilter, this.magFilter,
                minLOD, maxLOD,
                this.wrapS, this.wrapT, this.wrapR,
                this.anisotropicLevel,
                this.isSparse);
    }

    /**
     * Creates a new instance of GLTextureParameters by copying all of the
     * values and overriding the filters.
     *
     * @param minFilter the minification filter.
     * @param magFilter the magnification filter.
     * @return the new GLTextureParameter.
     * @since 15.12.17
     */
    public GLTextureParameters withFilter(
            final GLTextureMinFilter minFilter, final GLTextureMagFilter magFilter) {

        return new GLTextureParameters(
                minFilter, magFilter,
                this.minLOD, this.maxLOD,
                this.wrapS, this.wrapT, this.wrapR,
                this.anisotropicLevel,
                this.isSparse);
    }

    /**
     * Creates a new instance of GLTextureParameters by copying all of the
     * values and overriding the anisotropic level.
     *
     * @param anisoLevel the anisotropic level. 1.0 specifies isotropic
     * filtering.
     * @return the new GLTextureParameters.
     * @since 15.12.18
     */
    public GLTextureParameters withAnisotropic(final float anisoLevel) {

        return new GLTextureParameters(
                this.minFilter, this.magFilter,
                this.minLOD, this.maxLOD,
                this.wrapS, this.wrapT, this.wrapR,
                anisoLevel,
                this.isSparse);
    }

    /**
     * Creates a new instance of GLTextureParameters by copying all of the
     * values and overriding the sparse allocation.
     *
     * @param allocateSparse true to enable sparse texture.
     * @return the new GLTextureParameters.
     * @since 16.01.05
     */
    public GLTextureParameters withSparseAllocation(final boolean allocateSparse) {
        return new GLTextureParameters(
                this.minFilter, this.magFilter,
                this.minLOD, this.maxLOD,
                this.wrapS, this.wrapT, this.wrapR,
                this.anisotropicLevel,
                allocateSparse);
    }

    private static MaxAnisotropyQuery ANISO_QUERY = new MaxAnisotropyQuery();

    /**
     * Retrieves the supported maximum supported anisotropy level. This will
     * only sync the threads on the first call.
     *
     * @return the maximum supported anisotoropy filter level.
     * @since 15.05.28
     */
    public static float getTextureMaxAnisotropyLevel() {
        if (ANISO_QUERY == null) {
            ANISO_QUERY = new MaxAnisotropyQuery();
        }

        if (ANISO_QUERY.checked) {
            return ANISO_QUERY.maxLevel;
        } else {
            return ANISO_QUERY.glCall();
        }
    }

    /**
     * GLQuery that checks the maximum anisotropy level supported. This query
     * caches its result so future calls will not poll the device.
     *
     * @since 15.05.28
     */
    public static class MaxAnisotropyQuery extends GLQuery<Float> {

        boolean checked = false;
        float maxLevel = 0f;

        @Override
        public Float call() throws Exception {
            if (checked) {
                return this.maxLevel;
            }

            this.maxLevel = GLTools.getDriverInstance().textureGetMaxAnisotropy();
            this.checked = true;            

            return this.maxLevel;
        }
    }

    /**
     * An instance of GLTextureParameters that contains only the default values.
     *
     * @since 15.05.28
     */
    public static final GLTextureParameters DEFAULT_PARAMETERS = new GLTextureParameters();
}
