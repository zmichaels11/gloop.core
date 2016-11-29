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
public abstract class AbstractImage2D implements Image2D {
    protected static final IllegalStateException EX_INVALID_IMAGE = new IllegalStateException("Invalid Image2D!");    
            
    protected int width;
    protected int height;
    protected ImageFormat format;
    
    @Override
    public final boolean isLayered() {
        throw new UnsupportedOperationException("Layered Image2D objects are currently not supported!");
    }
    
    @Override
    public final int getLayer() {
        throw new UnsupportedOperationException("Layered Image2D objects are currently not supported!");
    }
    
    @Override
    public final ImageFormat getFormat() {
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
    
    @Override
    public final int getWidth() {
        if (this.isValid()) {
            return this.width;
        } else {
            throw EX_INVALID_IMAGE;
        }
    }
    
    @Override
    public final int getHeight() {
        if (this.isValid()) {
            return this.height;
        } else {
            throw EX_INVALID_IMAGE;
        }
    }
    
    protected final AbstractImage2DFactory getFactory() {
        return ObjectFactoryManager.getInstance().getImage2DFactory();
    }
    
    @Override
    public final boolean isValid() {
        return getFactory().isValid(this);
    }
    
    @Override
    public final AbstractImage2D write(int xOffset, int yOffset, int width, int height, DataFormat dFmt, ByteBuffer data) {
        getFactory().write(this, xOffset, yOffset, width, height, dFmt, data);
        return this;
    }
    
    @Override
    public final AbstractImage2D read(int xOffset, int yOffset, int width, int height, DataFormat dFmt, ByteBuffer data) {
        getFactory().read(this, xOffset, yOffset, width, height, dFmt, data);
        return this;
    }        
        
    @Override
    public final AbstractImage2D bind(int unit, ImageAccess access) {
        this.getFactory().bind(this, unit, access);
        return this;
    }
    
    @Override
    public final boolean isHandleResident() {
        return this.getFactory().isHandleResident(this);
    }        
    
    @Override
    public final AbstractImage2D setHandleResidency(final boolean residency, final ImageAccess access) {
        if (residency) {
            this.getFactory().makeHandleResident(this, access);
        } else {
            this.getFactory().makeHandleNonResident(this);
        }
        
        return this;
    }
    
    @Override
    public final void free() {
        getFactory().free(this);
    }
}
