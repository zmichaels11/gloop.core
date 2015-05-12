/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.longlinkislong.gloop;

import java.util.Deque;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;

/**
 *
 * @author zmichaels
 */
public class GLMultisampling {
    private static final Map<GLThread, GLMultisampling> INSTANCE_MAP = new HashMap<>();
    
    public static GLMultisampling getInstance(final GLThread thread) {
        if(INSTANCE_MAP.containsKey(thread)) {
            return INSTANCE_MAP.get(thread);
        } else {
            final GLMultisampling instance = new GLMultisampling(thread);
            
            INSTANCE_MAP.put(thread, instance);            
            return instance;
        }
    }
    
    private final GLThread thread;    
    private Deque<Frame> stack = new LinkedList<>();
    
    private GLMultisampling(final GLThread thread) {
        this.thread = thread;        
    }
    
    public void setRasterizerDiscard(final boolean value) {
        
    }
    
    public void push() {
        
    }
    
    private class Frame {
        boolean rasterizerDiscard;
        boolean multisampleShading;
        float minSampleShading;
        
        public Frame(
                final boolean rasterizerDiscard, 
                final boolean multisampleShading, 
                final float minSampleShading) {
            
            this.rasterizerDiscard = rasterizerDiscard;
            this.multisampleShading = multisampleShading;
            this.minSampleShading = minSampleShading;
        }
        
        public Frame(final Frame src) {
            this.rasterizerDiscard = src.rasterizerDiscard;
            this.multisampleShading = src.multisampleShading;
            this.minSampleShading = src.minSampleShading;
        }
    }
}
