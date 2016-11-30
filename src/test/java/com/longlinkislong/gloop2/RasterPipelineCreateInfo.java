/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.longlinkislong.gloop2;

import java.util.Collections;
import java.util.List;

/**
 *
 * @author zmichaels
 */
public final class RasterPipelineCreateInfo {

    public VertexArrayCreateInfo attributes;
    public List<Shader> shaderStages;
    public PrimitiveType primitiveType;
    public PolygonMode polygonMode;
    public CullMode cullMode;
    public VertexOrder frontFace;
    
    public ColorMask colorMask;
    public boolean blendEnabled;
    
    public boolean depthTest;
    public boolean stencilTest;
    
    public Viewport viewport;
    public Scissor scissor;
        
    //TODO: support attachment customization. Currently assuming only color attachment...
    //TODO: support multisample. Currently assuming only 1 sample...
    
    public RasterPipeline allocate() {
        return ObjectFactoryManager.getInstance().getPipelineFactory().allocate(this);
    }
}
