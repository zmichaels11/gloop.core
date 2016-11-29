/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.longlinkislong.gloop2;

import java.nio.ByteBuffer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 * @author zmichaels
 * @param <T>
 */
public abstract class AbstractImage2DFactory <T extends AbstractImage2D>{
    private static final Logger LOGGER = LoggerFactory.getLogger(AbstractImage2DFactory.class);
    
    protected abstract T newImage2D();
    
    protected abstract void doAllocate(T image);
    
    public T allocate(Image2DCreateInfo info) {
        final T out = newImage2D();
        
        out.format = info.format;
        out.height = info.height;
        out.width = info.width;
        
        doAllocate(out);
        
        return out;
    }
    
    public abstract boolean isValid(T image);
    
    protected abstract void doFree(T image);
    
    public void free(T image) {
        if (isValid(image)) {
            doFree(image);
            image.clear();
        } else {
            LOGGER.warn("Attempted to free unallocated image!");
        }
    }
    
    protected abstract void doBind(T image, int unit, ImageAccess access);
    
    public void bind(final T image, final int unit, final ImageAccess access) {
        if (isValid(image)) {
            doBind(image, unit, access);
        } else {
            throw AbstractImage2D.EX_INVALID_IMAGE;
        }
    }
    
    protected abstract void doWrite(T image, int xOffset, int yOffset, int width, int height, DataFormat fmt, ByteBuffer data);
    
    protected abstract void doRead(T image, int xOffset, int yOffset, int width, int height, DataFormat fmt, ByteBuffer data);
    
    public void write(final T image, final int xOffset, final int yOffset, final int width, final int height, final DataFormat fmt, final ByteBuffer data) {
        if (isValid(image)) {
            doWrite(image, xOffset, yOffset, width, height, fmt, data);
        } else {
            throw AbstractImage2D.EX_INVALID_IMAGE;
        }
    }
    
    public void read(final T image, final int xOffset, final int yOffset, final int width, final int height, final DataFormat fmt, final ByteBuffer data) {
        if (isValid(image)) {
            doRead(image, xOffset, yOffset, width, height, fmt, data);
        } else {
            throw AbstractImage2D.EX_INVALID_IMAGE;
        }
    }
    
    public abstract boolean isHandleResident(T image);
    
    protected abstract void doMakeHandleResident(T image, ImageAccess access);
    
    protected abstract void doMakeHandleNonResident(T image);
    
    public void makeHandleResident(final T image, final ImageAccess access) {
        if (isValid(image) == false) {
            throw AbstractImage2D.EX_INVALID_IMAGE;
        } else if (isHandleResident(image)) {
            LOGGER.warn("Image2D handle is already resident! State change ignored.");
        } else {
            doMakeHandleResident(image, access);
        }
    }
    
    public void makeHandleNonResident(T image) {
        if (isValid(image) == false) {
            throw AbstractImage2D.EX_INVALID_IMAGE;
        } else if (isHandleResident(image)) {
            doMakeHandleNonResident(image);
        } else {
            LOGGER.warn("Image2D handle is already non-resident! State change ignored.");
        }
    }
}
