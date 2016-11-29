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
public final class GLObjectFactoryManager {
    private GLObjectFactoryManager() {}
    
    private static final class Holder {
        private Holder() {}
        private static final GLObjectFactoryManager INSTANCE = new GLObjectFactoryManager();
    }
    
    public static GLObjectFactoryManager getInstance() {
        return Holder.INSTANCE;
    }
    
    //TODO: do a selector
    private AbstractBufferFactory<?> bufferFactory;
    private AbstractTexture2DFactory<?> textureFactory;
    private AbstractSampler2DFactory<?> sampler2DFactory;
    private AbstractImage2DFactory<?> image2DFactory;
    
    public AbstractImage2DFactory<?> getImage2DFactory() {
        return this.image2DFactory;
    }
    
    public AbstractSampler2DFactory<?> getSampler2DFactory() {
        return this.sampler2DFactory;
    }
    
    public AbstractTexture2DFactory<?> getTexture2DFactory() {
        return this.textureFactory;
    }    
    
    public AbstractBufferFactory getBufferFactory() {
        return this.bufferFactory;
    }
}
