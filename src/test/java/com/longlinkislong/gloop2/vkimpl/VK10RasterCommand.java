/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.longlinkislong.gloop2.vkimpl;

import com.longlinkislong.gloop2.AbstractRasterCommand;
import com.longlinkislong.gloop2.RasterCommandCreateInfo;

/**
 *
 * @author zmichaels
 */
public class VK10RasterCommand extends AbstractRasterCommand {
    protected RasterCommandCreateInfo getInfo() {
        return this.info;
    }
    
    @Override
    public void draw() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }
    
}
