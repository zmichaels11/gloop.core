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
public abstract class AbstractTexture2D {
    protected int width;
    protected int height;
    protected AbstractImage2D[] levels;
    
    protected void clear() {
        this.width = 0;
        this.height = 0;
        this.levels = null;
    }
    
    public int getWidth() {
        if (this.isValid()) {
            return this.width;
        } else {
            throw new IllegalStateException("Invalid Texture!");
        }
    }
    
    public int getHeight() {
        if (this.isValid()) {
            return this.height;
        } else {
            throw new IllegalStateException("Invalid Texture!");
        }
    }
    
    public native void bind(int unit);
    
    public AbstractImage2D getImage(final int level) {
        if (this.isValid()) {
            return this.levels[level];
        } else {
            throw new IllegalArgumentException("Invalid mipmap level!");
        }
    }
    
    public native void generateMipmaps();
    
    public native boolean isValid(); //TODO: implement
}
