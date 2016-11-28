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
public abstract class AbstractRenderbuffer {
    protected int width;
    protected int height;
    protected RenderbufferFormat format;
    
    public int getWidth() {
        return this.width;
    }
    
    public int getHeight() {
        return this.height;
    }
    
    public RenderbufferFormat getFormat() {
        return this.format;
    }
    
    protected void clear() {
        this.width = 0;
        this.height = 0;
        this.format = null;
    }
}
