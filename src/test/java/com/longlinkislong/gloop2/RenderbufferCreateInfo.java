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
public final class RenderbufferCreateInfo {
    public final int width;
    public final int height;
    public final RenderbufferFormat format;
    
    public RenderbufferCreateInfo(final RenderbufferFormat fmt, final int width, final int height) {
        this.width = width;
        this.height = height;
        this.format = Objects.requireNonNull(fmt);
    }        
    
    public RenderbufferCreateInfo() {
        this(RenderbufferFormat.RGBA8, 1, 1);
    }
    
    public RenderbufferCreateInfo withRenderbufferFormat(final RenderbufferFormat fmt) {
        return new RenderbufferCreateInfo(fmt, this.width, this.height);
    }
    
    public RenderbufferCreateInfo withWidth(final int width) {
        return new RenderbufferCreateInfo(this.format, width, this.height);
    }
    
    public RenderbufferCreateInfo withHeight(final int height) {
        return new RenderbufferCreateInfo(this.format, this.width, height);
    }
}
