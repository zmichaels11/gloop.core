/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.longlinkislong.gloop2.vkimpl;

import com.longlinkislong.gloop2.AbstractRasterCommandFactory;

/**
 *
 * @author zmichaels
 */
public class VK10RasterCommandFactory extends AbstractRasterCommandFactory<VK10RasterCommand> {

    @Override
    protected VK10RasterCommand newRasterCommand() {
        return new VK10RasterCommand();
    }

    @Override
    protected void doAllocate(VK10RasterCommand out) {
        
    }
    
}
