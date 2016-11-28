/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.longlinkislong.gloop2;

import java.nio.ByteBuffer;

/**
 *
 * @author zmichaels
 */
public abstract class AbstractImage2D {
    protected int width;
    protected int height;
    protected ImageFormat format;
    
    protected void clear() {
        this.width = 0;
        this.height = 0;
        this.format = null;
    }
    
    public int getWidth() {
        return this.width;
    }
    
    public int getHeight() {
        return this.height;
    }
    
    public native boolean isValid(); //TODO: implement
    
    public native void write(int xOffset, int yOffset, int width, int height, DataFormat dFmt, ByteBuffer data);        
    
    public native void read(int xOffset, int yOffset, int width, int height, DataFormat dFmt, ByteBuffer data);
    
    public final void write(final DataFormat dFmt, final ByteBuffer data) {
        this.write(0, 0, this.width, this.height, dFmt, data);
    }
    
    public final void read(final DataFormat dFmt, final ByteBuffer data) {
        this.read(0, 0, this.width, this.height, dFmt, data);
    }
    
    public native void bind(boolean layered, int layer, ImageAccess access, ImageFormat fmt);
}
