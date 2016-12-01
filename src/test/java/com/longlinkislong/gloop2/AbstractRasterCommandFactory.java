/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.longlinkislong.gloop2;

/**
 *
 * @author zmichaels
 * @param <T>
 */
public abstract class AbstractRasterCommandFactory <T extends AbstractRasterCommand> {
    protected abstract T newRasterCommand();
    
    protected abstract void doAllocate(T out);
    
    public T allocate(RasterCommandCreateInfo info) {
        final T out = newRasterCommand();
        
        out.info = info;
        doAllocate(out);
        
        return out;
    }
}
