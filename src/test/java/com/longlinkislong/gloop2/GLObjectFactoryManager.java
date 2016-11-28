/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.longlinkislong.gloop2;

import com.longlinkislong.gloop2.glimpl.GL45BufferFactory;

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
    private final AbstractBufferFactory bufferFactory = new GL45BufferFactory();
    
    public AbstractBuffer allocate(BufferCreateInfo info) {
        return bufferFactory.allocate(info);
    }
    
    protected void free(AbstractBuffer buffer) {
        bufferFactory.free(buffer);
    }   
    
    protected boolean isValid(AbstractBuffer buffer) {
        return bufferFactory.isValid(buffer);
    }
    
    public AbstractBufferFactory getBufferFactory() {
        return this.bufferFactory;
    }
}
