/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.longlinkislong.gloop2.vkimpl;

import org.lwjgl.vulkan.VkDevice;

/**
 *
 * @author zmichaels
 */
public class VKThreadContext {
    private static final ThreadLocal<VKThreadContext> INSTANCES = new ThreadLocal<>();
    
    public static void create(final VkDevice device) {
        INSTANCES.set(new VKThreadContext(device));
    }
    
    public VKThreadContext(final VkDevice device) {
        this.device = device;        
    }        
    
    private final VkDevice device;
        
    public VkDevice getDevice() {
        return this.device;
    }   
    
    public static VKThreadContext getCurrentContext() {
        return INSTANCES.get();
    }
}
