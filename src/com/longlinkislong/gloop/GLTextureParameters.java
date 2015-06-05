/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.longlinkislong.gloop;

import java.util.Objects;
import org.lwjgl.opengl.EXTTextureFilterAnisotropic;
import org.lwjgl.opengl.GL11;

/**
 *
 * @author zmichaels
 */
public class GLTextureParameters {

    public static final GLTextureMinFilter DEFAULT_MIN_FILTER = GLTextureMinFilter.GL_NEAREST_MIPMAP_LINEAR;
    public static final GLTextureMagFilter DEFAULT_MAG_FILTER = GLTextureMagFilter.GL_LINEAR;
    public static final float DEFAULT_MIN_LOD = -1000f;
    public static final float DEFAULT_MAX_LOD = 1000f;
    public static final GLTextureWrap DEFAULT_WRAP_S = GLTextureWrap.GL_REPEAT;
    public static final GLTextureWrap DEFAULT_WRAP_T = GLTextureWrap.GL_REPEAT;
    public static final GLTextureWrap DEFAULT_WRAP_R = GLTextureWrap.GL_REPEAT;
    public static final float DEFAULT_ANISOTROPIC_LEVEL = 0f;

    public final GLTextureWrap wrapS, wrapT, wrapR;
    public final GLTextureMinFilter minFilter;
    public final GLTextureMagFilter magFilter;
    public final float minLOD;
    public final float maxLOD;
    public final float anisotropicLevel;

    public GLTextureParameters() {
        this(
                DEFAULT_MIN_FILTER, DEFAULT_MAG_FILTER,
                DEFAULT_MIN_LOD, DEFAULT_MAX_LOD,
                DEFAULT_WRAP_S, DEFAULT_WRAP_T, DEFAULT_WRAP_R,
                DEFAULT_ANISOTROPIC_LEVEL);
    }

    public GLTextureParameters(
            final GLTextureMinFilter minFilter, final GLTextureMagFilter magFilter,
            final float minLOD, final float maxLOD,
            final GLTextureWrap wrapS, final GLTextureWrap wrapT, final GLTextureWrap wrapR,
            final float anisotropicLevel) {

        Objects.requireNonNull(this.wrapR = wrapR);
        Objects.requireNonNull(this.wrapS = wrapS);
        Objects.requireNonNull(this.wrapT = wrapT);
        Objects.requireNonNull(this.minFilter = minFilter);
        Objects.requireNonNull(this.magFilter = magFilter);

        this.minLOD = minLOD;
        this.maxLOD = maxLOD;
        this.anisotropicLevel = anisotropicLevel;
    }

    public GLTextureParameters withWrap(
            final GLTextureWrap wrapS, final GLTextureWrap wrapT, final GLTextureWrap wrapR) {

        return new GLTextureParameters(
                this.minFilter, this.magFilter,
                this.minLOD, this.maxLOD,
                wrapS, wrapT, wrapR,
                this.anisotropicLevel);
    }

    public GLTextureParameters withLOD(final float minLOD, final float maxLOD) {

        return new GLTextureParameters(
                this.minFilter, this.magFilter,
                minLOD, maxLOD,
                this.wrapS, this.wrapT, this.wrapR,
                this.anisotropicLevel);
    }

    public GLTextureParameters withFilter(
            final GLTextureMinFilter minFilter, final GLTextureMagFilter magFilter) {

        return new GLTextureParameters(
                minFilter, magFilter,
                this.minLOD, this.maxLOD,
                this.wrapS, this.wrapT, this.wrapR,
                this.anisotropicLevel);
    }

    public GLTextureParameters withAnisotropic(final float anisoLevel) {

        return new GLTextureParameters(
                this.minFilter, this.magFilter,
                this.minLOD, this.maxLOD,
                this.wrapS, this.wrapT, this.wrapR,
                anisoLevel);
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

    public static class MaxAnisotropyQuery extends GLQuery<Float> {

        boolean checked = false;
        float maxLevel = 0f;

        @Override
        public Float call() throws Exception {
            if (checked) {
                return this.maxLevel;
            }

            this.maxLevel = GL11.glGetFloat(
                    EXTTextureFilterAnisotropic.GL_MAX_TEXTURE_MAX_ANISOTROPY_EXT);

            this.checked = true;
            return this.maxLevel;
        }

    }
}
