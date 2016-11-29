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
    protected static final IllegalStateException EX_INVALID_IMAGE = new IllegalStateException("Invalid Image2D!");    
        
    protected boolean layered;
    protected int layer;
    protected int width;
    protected int height;
    protected ImageFormat format;
    
    public ImageFormat getFormat() {
        if (this.isValid()) {
            return this.format;
        } else {
            throw EX_INVALID_IMAGE;
        }
    }
    
    protected void clear() {
        this.width = 0;
        this.height = 0;
        this.format = null;
    }
    
    public int getWidth() {
        if (this.isValid()) {
            return this.width;
        } else {
            throw EX_INVALID_IMAGE;
        }
    }
    
    public int getHeight() {
        if (this.isValid()) {
            return this.height;
        } else {
            throw EX_INVALID_IMAGE;
        }
    }
    
    protected final AbstractImage2DFactory getFactory() {
        return GLObjectFactoryManager.getInstance().getImage2DFactory();
    }
    
    public boolean isValid() {
        return getFactory().isValid(this);
    }
    
    public AbstractImage2D write(int xOffset, int yOffset, int width, int height, DataFormat dFmt, ByteBuffer data) {
        getFactory().write(this, xOffset, yOffset, width, height, dFmt, data);
        return this;
    }
    
    public AbstractImage2D read(int xOffset, int yOffset, int width, int height, DataFormat dFmt, ByteBuffer data) {
        getFactory().read(this, xOffset, yOffset, width, height, dFmt, data);
        return this;
    }
    
    public final AbstractImage2D write(final DataFormat dFmt, final ByteBuffer data) {
        return this.write(0, 0, this.width, this.height, dFmt, data);
    }
    
    public final AbstractImage2D read(final DataFormat dFmt, final ByteBuffer data) {
        return this.read(0, 0, this.width, this.height, dFmt, data);
    }
    
    public AbstractImage2D bind(int unit, ImageAccess access) {
        this.getFactory().bind(this, unit, access);
        return this;
    }
    
    public boolean isHandleResident() {
        return this.getFactory().isHandleResident(this);
    }        
    
    public AbstractImage2D setHandleResidency(final boolean residency, final ImageAccess access) {
        if (residency) {
            this.getFactory().makeHandleResident(this, access);
        } else {
            this.getFactory().makeHandleNonResident(this);
        }
        
        return this;
    }
}
