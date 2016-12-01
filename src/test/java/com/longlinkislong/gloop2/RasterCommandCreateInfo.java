/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.longlinkislong.gloop2;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

/**
 *
 * @author zmichaels
 */
public final class RasterCommandCreateInfo {
    public final Map<Integer, Buffer> inputs;
    public final RasterPipeline pipeline;
    public final Framebuffer output;
    
    public RasterCommandCreateInfo(
            final Map<Integer, Buffer> inputs,
            final RasterPipeline pipeline,
            final Framebuffer outputs) {
        
        this.inputs = Collections.unmodifiableMap(new HashMap<>(inputs));
        this.pipeline = pipeline;
        this.output = outputs;
    }
    
    public RasterCommandCreateInfo() {
        this(Collections.emptyMap(), null, null);
    }
    
    public RasterCommandCreateInfo withInput(final int location, final Buffer vertexData) {
        final Map<Integer, Buffer> newMap = new HashMap<>(this.inputs);
        
        newMap.put(location, vertexData);
        
        return new RasterCommandCreateInfo(newMap, pipeline, output);
    }
    
    public RasterCommandCreateInfo withOutput(final Framebuffer output) {
        return new RasterCommandCreateInfo(this.inputs, this.pipeline, output);
    }
    
    public RasterCommandCreateInfo withPipeline(final RasterPipeline pipeline) {
        return new RasterCommandCreateInfo(inputs, pipeline, output);
    }
}
