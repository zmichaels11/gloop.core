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
public abstract class AbstractFramebufferFactory <T extends AbstractFramebuffer> {
    private static final Logger LOGGER = LoggerFactory.getLogger(AbstractFramebufferFactory.class);
    
    protected abstract T newFramebuffer();
    
    protected abstract void doAllocate(T fb);
    
    public T allocate(FramebufferCreateInfo info) {
        final T out = newFramebuffer();
        
        out.info = info;
        doAllocate(out);
        
        return out;
    }
    
    public abstract boolean isValid(T fb);
    
    protected abstract void doFree(T fb);
    
    public void free(T fb) {
        if (isValid(fb)) {
            doFree(fb);
            fb.clear();
        } else {
            LOGGER.warn("Attempted to free unallocated framebuffer!");
        }
    }
}
