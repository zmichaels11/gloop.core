/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.longlinkislong.gloop2.vkimpl;

import com.longlinkislong.gloop2.AbstractPipeline;
import com.longlinkislong.gloop2.PipelineCreateInfo;
import com.longlinkislong.gloop2.VertexAttribute;
import java.util.List;

/**
 *
 * @author zmichaels
 */
public class VK10Pipeline extends AbstractPipeline {
    public long renderpass;
    public long pipeline;    
    
    @Override
    public boolean isValid() {
        return pipeline != 0L;
    }

    @Override
    public void free() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }
    
    public List<VertexAttribute> getAttributes() {
        return this.info.attributes.attributes;
    }
    
    public PipelineCreateInfo getInfo() {
        return this.info;
    }
}
