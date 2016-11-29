/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.longlinkislong.gloop2;

/**
 *
 * @author zmichaels
 */
public abstract class AbstractPipeline implements Pipeline {
    protected PipelineCreateInfo info;
    
    protected void clear() {
        this.info = null;
    }
}
