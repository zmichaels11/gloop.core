/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.longlinkislong.gloop2;

/**
 *
 * @author zmichaels
 */
public abstract class AbstractRenderbuffer implements Renderbuffer {
    protected int width;
    protected int height;
    protected RenderbufferFormat format;
    
    @Override
    public final int getWidth() {
        return this.width;
    }
    
    @Override
    public final int getHeight() {
        return this.height;
    }
    
    @Override
    public final RenderbufferFormat getFormat() {
        return this.format;
    }
    
    protected void clear() {
        this.width = 0;
        this.height = 0;
        this.format = null;
    }
}
