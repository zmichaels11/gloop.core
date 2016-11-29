/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.longlinkislong.gloop2;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 * @author zmichaels
 * @param <T>
 */
public abstract class AbstractTexture2DFactory <T extends AbstractTexture2D> {        
    private static final Logger LOGGER = LoggerFactory.getLogger(AbstractTexture2DFactory.class);
    
    protected abstract T newTexture2D();                
    
    protected abstract void doAllocate(T texture);
    
    public T allocate(Texture2DCreateInfo info) {
        final T out = newTexture2D();
        
        out.baseLevel = info.baseLevel;
        out.format = info.format;
        out.height = info.height;
        out.width = info.width;
        out.levels = info.levels;
        out.maxLevel = info.maxLevel;
        out.sampler = info.sampler;
        out.width = info.width;
        
        doAllocate(out);
        
        return out;
    }      
    
    public abstract boolean isValid(T texture);            
    
    protected abstract void doFree(T texture); 
    
    public void free(T texture) {
        if (isValid(texture)) {
            doFree(texture);
            texture.clear();
        } else {
            LOGGER.warn("Attempted to free unallocated texture!");
        }
    }      
    
    protected abstract void doBind(T texture, int unit);
    
    public void bind(T texture, int unit) {
        if (isValid(texture)) {
            doBind(texture, unit);
        } else {
            throw T.EX_INVALID_TEXTURE;
        }
    }    
                
    public abstract boolean isHandleResident(T texture);        
    
    protected abstract void doMakeHandleResident(T tex);        
    
    protected abstract void doMakeHandleNonResident(T tex);        
    
    public void makeHandleResident(T tex) {
        if (isValid(tex) == false) {
            throw T.EX_INVALID_TEXTURE;
        } else if (isHandleResident(tex)) {
            LOGGER.warn("Texture2D handle is already resident! State change ignored.");
        } else {
            doMakeHandleResident(tex);
        }
    }        
    
    public void makeHandleNonResident(T tex) {
        if (isValid(tex) == false) {
            throw T.EX_INVALID_TEXTURE;
        } else if (isHandleResident(tex)) {
            doMakeHandleNonResident(tex);
        } else {
            LOGGER.warn("Texture2D handle is already non-resident! State change ignored.");
        }
    }        
    
    protected abstract void doGenerateMipmaps(T tex);
    
    public void generateMipmaps(T texture) {
        if (isValid(texture)) {
            doGenerateMipmaps(texture);
        } else {
            throw T.EX_INVALID_TEXTURE;
        }
    }
          
}
