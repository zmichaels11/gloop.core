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
public final class Texture2DCreateInfo {

    public static final int DEFAULT_BASE_LEVEL = 0;
    public static final int DEFAULT_MAX_LEVEL = 1000;
    public final Sampler2DCreateInfo sampler;
    public final int width;
    public final int height;
    public final int levels;
    public final TextureFormat format;
    public final int baseLevel;
    public final int maxLevel;
    
    public Texture2DCreateInfo(
            final Sampler2DCreateInfo sampler,
            final int baseLevel, final int maxLevel,
            final int width, final int height,
            final int levels,
            final TextureFormat format) {

        this.sampler = Objects.requireNonNull(sampler);
        this.width = width;
        this.height = height;
        this.format = Objects.requireNonNull(format);
        this.baseLevel = baseLevel;
        this.maxLevel = maxLevel;
        this.levels = levels;
    }

    public Texture2DCreateInfo() {
        this(new Sampler2DCreateInfo(), DEFAULT_BASE_LEVEL, DEFAULT_MAX_LEVEL, 1, 1, 1, TextureFormat.RGBA8);
    }    
}
