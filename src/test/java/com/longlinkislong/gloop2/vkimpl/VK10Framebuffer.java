/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.longlinkislong.gloop2.vkimpl;

import com.longlinkislong.gloop2.AbstractFramebuffer;
import com.longlinkislong.gloop2.FramebufferCreateInfo;
import java.nio.LongBuffer;
import java.util.OptionalLong;
import org.lwjgl.system.MemoryStack;
import org.lwjgl.vulkan.VK10;

/**
 *
 * @author zmichaels
 */
public class VK10Framebuffer extends AbstractFramebuffer implements Resource {
    public long framebuffer;
    public long renderpass;
    private long semaphore;
        
    protected FramebufferCreateInfo getInfo() {
        return info;
    }        
    
    @Override
    public void lock() {
        if (this.semaphore != 0L) {
            throw new IllegalStateException("Resource is already locked!");
        }
        
        
    }

    @Override
    public void unlock() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public OptionalLong getSemaphore() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }
}
