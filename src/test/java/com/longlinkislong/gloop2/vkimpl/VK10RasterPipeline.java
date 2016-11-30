/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.longlinkislong.gloop2.vkimpl;

import com.longlinkislong.gloop2.AbstractRasterPipeline;
import com.longlinkislong.gloop2.RasterPipelineCreateInfo;

/**
 *
 * @author zmichaels
 */
public class VK10RasterPipeline extends AbstractRasterPipeline {    
    public long pipeline;    
    
    @Override
    public boolean isValid() {
        return pipeline != 0L;
    }   
    
    public RasterPipelineCreateInfo getInfo() {
        return this.info;
    }
}
