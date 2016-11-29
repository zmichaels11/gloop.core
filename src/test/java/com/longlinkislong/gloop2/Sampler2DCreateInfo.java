/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.longlinkislong.gloop2;

import java.util.Objects;

/**
 *
 * @author zmichaels
 */
public final class Sampler2DCreateInfo {

    public static final SamplerMinFilter DEFAULT_MIN_FILTER = SamplerMinFilter.NEAREST_MIPMAP_LINEAR;
    public static final SamplerMagFilter DEFAULT_MAG_FILTER = SamplerMagFilter.LINEAR;
    public static final SamplerEdgeSampling DEFAULT_WRAP_S = SamplerEdgeSampling.REPEAT;
    public static final SamplerEdgeSampling DEFAULT_WRAP_T = SamplerEdgeSampling.REPEAT;
    public static final double DEFAULT_ANISOTROPIC_FILTERING = 1.0;
    public static final double DEFAULT_MIN_LOD = -1000.0;
    public static final double DEFAULT_MAX_LOD = 1000.0;
    public static final double DEFAULT_LOD_BIAS = 0.0;
    public static final double DEFAULT_BORDER_COLOR_R = 0.0;
    public static final double DEFAULT_BORDER_COLOR_G = 0.0;
    public static final double DEFAULT_BORDER_COLOR_B = 0.0;
    public static final double DEFAULT_BORDER_COLOR_A = 0.0;
    
    public final SamplerEdgeSampling edgeSamplingS;
    public final SamplerEdgeSampling edgeSamplingT;
    public final double anisotropicFilter;
    public final double minLOD;
    public final double maxLOD;
    public final double lodBias;
    public final double borderR;
    public final double borderG;
    public final double borderB;
    public final double borderA;
    public final SamplerMinFilter minFilter;
    public final SamplerMagFilter magFilter;

    public Sampler2DCreateInfo withBorderColor(final double red, final double green, final double blue, final double alpha) {
        return new Sampler2DCreateInfo(
                this.minFilter, this.magFilter,
                this.edgeSamplingS, this.edgeSamplingT,
                this.anisotropicFilter,
                this.minLOD, this.maxLOD, this.lodBias,
                red, green, blue, alpha);
    }

    public Sampler2DCreateInfo withFilter(final SamplerMinFilter min, final SamplerMagFilter mag) {
        return new Sampler2DCreateInfo(
                (min == SamplerMinFilter.DEFAULT) ? DEFAULT_MIN_FILTER : Objects.requireNonNull(min),
                (mag == SamplerMagFilter.DEFAULT) ? DEFAULT_MAG_FILTER : Objects.requireNonNull(mag),
                this.edgeSamplingS, this.edgeSamplingT,
                this.anisotropicFilter,
                this.minLOD, this.maxLOD, this.lodBias,
                this.borderR, this.borderG, this.borderB, this.borderA);
    }

    public Sampler2DCreateInfo withEdgeSampling(final SamplerEdgeSampling edgeS, final SamplerEdgeSampling edgeT) {
        return new Sampler2DCreateInfo(
                this.minFilter, this.magFilter,
                (edgeS == SamplerEdgeSampling.DEFAULT) ? DEFAULT_WRAP_S : edgeS,
                (edgeT == SamplerEdgeSampling.DEFAULT) ? DEFAULT_WRAP_T : edgeT,
                this.anisotropicFilter,
                this.minLOD, this.maxLOD, this.lodBias,
                this.borderR, this.borderG, this.borderB, this.borderA);
    }

    public Sampler2DCreateInfo withAnisotropic(final double level) {
        return new Sampler2DCreateInfo(
                this.minFilter, this.magFilter,
                this.edgeSamplingS, this.edgeSamplingT,
                level,
                this.minLOD, this.maxLOD, this.lodBias,
                this.borderR, this.borderG, this.borderB, this.borderA);
    }

    public Sampler2DCreateInfo withLOD(final double min, final double max, final double bias) {
        return new Sampler2DCreateInfo(
                this.minFilter, this.magFilter,
                this.edgeSamplingS, this.edgeSamplingT,
                this.anisotropicFilter,
                min, max, bias,
                this.borderR, this.borderG, this.borderB, this.borderA);
    }

    public Sampler2DCreateInfo() {
        this(
                DEFAULT_MIN_FILTER, DEFAULT_MAG_FILTER,
                DEFAULT_WRAP_S, DEFAULT_WRAP_T,
                DEFAULT_ANISOTROPIC_FILTERING,
                DEFAULT_MIN_LOD, DEFAULT_MAX_LOD, DEFAULT_LOD_BIAS,
                DEFAULT_BORDER_COLOR_R, DEFAULT_BORDER_COLOR_G, DEFAULT_BORDER_COLOR_B, DEFAULT_BORDER_COLOR_A);
    }

    public Sampler2DCreateInfo(
            final SamplerMinFilter minFilter, final SamplerMagFilter magFilter,
            final SamplerEdgeSampling wrapS, final SamplerEdgeSampling wrapT,
            final double aniso,
            final double minLOD, final double maxLOD, final double lodBias,
            final double borderR, final double borderG, final double borderB, final double borderA) {

        this.minFilter = (minFilter == SamplerMinFilter.DEFAULT) ? DEFAULT_MIN_FILTER : Objects.requireNonNull(minFilter);
        this.magFilter = (magFilter == SamplerMagFilter.DEFAULT) ? DEFAULT_MAG_FILTER : Objects.requireNonNull(magFilter);
        this.edgeSamplingS = (wrapS == SamplerEdgeSampling.DEFAULT) ? DEFAULT_WRAP_S : Objects.requireNonNull(wrapS);
        this.edgeSamplingT = (wrapT == SamplerEdgeSampling.DEFAULT) ? DEFAULT_WRAP_T : Objects.requireNonNull(wrapT);
        this.anisotropicFilter = aniso;
        this.minLOD = minLOD;
        this.maxLOD = maxLOD;
        this.lodBias = lodBias;
        this.borderR = borderR;
        this.borderG = borderG;
        this.borderB = borderB;
        this.borderA = borderA;
    }
    
    public Sampler2D allocate() {
        return GLObjectFactoryManager.getInstance().getSampler2DFactory().allocate(this);
    }
}
