/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.longlinkislong.gloop2;

import com.longlinkislong.gloop2.glimpl.GL33Sampler2DFactory;
import com.longlinkislong.gloop2.glimpl.GL45BufferFactory;
import com.longlinkislong.gloop2.glimpl.GL45Image2DFactory;
import com.longlinkislong.gloop2.glimpl.GL45Texture2DFactory;
import com.longlinkislong.gloop2.vkimpl.VK10BufferFactory;
import com.longlinkislong.gloop2.vkimpl.VK10RasterPipelineFactory;
import com.longlinkislong.gloop2.vkimpl.VK10ShaderFactory;

/**
 *
 * @author zmichaels
 */
public final class ObjectFactoryManager {
    private ObjectFactoryManager() {}
    
    private static final class Holder {
        private Holder() {}
        private static final ObjectFactoryManager INSTANCE = new ObjectFactoryManager();
    }
    
    public static ObjectFactoryManager getInstance() {
        return Holder.INSTANCE;
    }
    
    public void initGL45() {
        bufferFactory = new GL45BufferFactory();
        texture2DFactory = new GL45Texture2DFactory();
        sampler2DFactory = new GL33Sampler2DFactory();
        image2DFactory = new GL45Image2DFactory();        
    }
    
    public void initVK10() {
        bufferFactory = new VK10BufferFactory();
        shaderFactory = new VK10ShaderFactory();
        pipelineFactory = new VK10RasterPipelineFactory();
    }
    
    //TODO: do a selector
    private AbstractBufferFactory<?> bufferFactory;
    private AbstractTexture2DFactory<?> texture2DFactory;
    private AbstractSampler2DFactory<?> sampler2DFactory;
    private AbstractImage2DFactory<?> image2DFactory;    
    private AbstractShaderFactory<?> shaderFactory;
    private AbstractRasterPipelineFactory<?> pipelineFactory;
    private AbstractRasterCommandFactory<?> rasterCommandFactory;
    private AbstractFramebufferFactory<?> framebufferFactory;
    
    public AbstractRasterPipelineFactory<?> getRasterPipelineFactory() {
        return this.pipelineFactory;
    }
    
    public AbstractShaderFactory<?> getShaderFactory() {
        return this.shaderFactory;
    }
    
    public AbstractImage2DFactory<?> getImage2DFactory() {
        return this.image2DFactory;
    }
    
    public AbstractSampler2DFactory<?> getSampler2DFactory() {
        return this.sampler2DFactory;
    }
    
    public AbstractTexture2DFactory<?> getTexture2DFactory() {
        return this.texture2DFactory;
    }    
    
    public AbstractBufferFactory<?> getBufferFactory() {
        return this.bufferFactory;
    }
}
