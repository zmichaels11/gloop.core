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
public abstract class AbstractPipelineFactory <T extends AbstractPipeline> {
    private static final Logger LOGGER = LoggerFactory.getLogger(AbstractPipelineFactory.class);
    
    protected abstract T newPipeline();
    
    protected abstract void doAllocate(T pipeline);
    
    public T allocate(final PipelineCreateInfo info) {
        final T out = newPipeline();
        
        out.info = info;
        
        doAllocate(out);
        
        return out;
    }
    
    public abstract boolean isValid(T pipeline);
    
    protected abstract void doFree(T pipeline);
    
    public void free(T pipeline) {
        if (isValid(pipeline)) {
            doFree(pipeline);
            pipeline.clear();            
        } else {
            LOGGER.error("Attempted to free unallocated pipeline!");
        }
    }
    
    protected abstract void doDraw(T pipeline, int offset, int start);
    
    public void draw(T pipeline, int offset, int start) {
        if (isValid(pipeline)) {
            doDraw(pipeline, offset, start);
        } else {
            throw new IllegalStateException("Invalid pipeline!");
        }
    }
}
