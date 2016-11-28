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
public abstract class AbstractShaderFactory <T extends AbstractShader> {
    private static final Logger LOGGER = LoggerFactory.getLogger(AbstractShaderFactory.class);
    
    protected abstract T newShader();
    
    public T allocate(ShaderCreateInfo info) {
        final T out = newShader();
        
        out.compiled = false;
        out.type = info.type;
        out.src = info.source;
        
        doAllocate(out, info.type, info.source);
        
        return out;
    }
    
    protected abstract void doAllocate(T shader, ShaderType type, String src);
    
    protected abstract void doFree(T shader);
    
    public void free(T shader) {
        if (isValid(shader)) {
            doFree(shader);
            shader.clear();
        } else {
            LOGGER.warn("Attempted to free unallocated shader!");
        }
    }
    
    public abstract boolean isValid(T shader);    
}
