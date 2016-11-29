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
public abstract class AbstractSampler2DFactory <T extends AbstractSampler2D> {
    private static final Logger LOGGER = LoggerFactory.getLogger(AbstractSampler2DFactory.class);
    
    protected abstract T newSampler2D();
    
    protected abstract void doAllocate(T sampler);
    
    public T allocate(Sampler2DCreateInfo info) {
        final T out = newSampler2D();
        
        out.anisotropicFilter = info.anisotropicFilter;
        out.borderA = info.borderA;
        out.borderB = info.borderB;
        out.borderG = info.borderG;
        out.borderR = info.borderR;        
        out.edgeSamplingS = info.edgeSamplingS;
        out.edgeSamplingT = info.edgeSamplingT;        
        out.lodBias = info.lodBias;        
        out.magFilter = info.magFilter;        
        out.maxLOD = info.maxLOD;        
        out.minFilter = info.minFilter;        
        out.minLOD = info.minLOD;
        
        return out;
    }
    
    public abstract boolean isValid(T sampler);
    
    protected abstract void doFree(T sampler);
    
    public void free(T sampler) {
        if (isValid(sampler)) {
            doFree(sampler);
            sampler.clear();
        } else {
            LOGGER.warn("Attempted to free unallocated sampler");
        }
    }
    
    protected abstract void doBind(T sampler, int unit);
    
    public void bind(final T sampler, final int unit) {
        if (isValid(sampler)) {
            doBind(sampler, unit);
        } else {
            throw AbstractSampler2D.EX_INVALID_SAMPLER;
        }
    }        
}
