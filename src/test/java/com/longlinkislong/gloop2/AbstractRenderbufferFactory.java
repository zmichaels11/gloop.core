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
public abstract class AbstractRenderbufferFactory<T extends AbstractRenderbuffer> {
    private static final Logger LOGGER = LoggerFactory.getLogger(AbstractRenderbufferFactory.class);
    
    protected abstract T newRenderbuffer();
    
    public T allocate(RenderbufferCreateInfo info) {
        final T out = newRenderbuffer();
        
        out.format = info.format;
        out.width = info.width;
        out.height = info.height;
        
        doAllocate(out, info.width, info.height, info.format);
        
        return out;
    }
    
    protected abstract void doAllocate(T rb, int width, int height, RenderbufferFormat fmt);
    
    public void free(T rb) {
        if (isValid(rb)) {
            doFree(rb);
            rb.clear();
        } else {
            LOGGER.warn("Attempted to free unallocated renderbuffer!");
        }
    }
    
    protected abstract void doFree(T rb);
    
    public abstract boolean isValid(T rb);
}
