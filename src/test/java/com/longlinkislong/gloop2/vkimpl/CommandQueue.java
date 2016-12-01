/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.longlinkislong.gloop2.vkimpl;

import org.lwjgl.PointerBuffer;
import org.lwjgl.system.MemoryStack;
import org.lwjgl.vulkan.VK10;
import org.lwjgl.vulkan.VkDevice;
import org.lwjgl.vulkan.VkQueue;

/**
 *
 * @author zmichaels
 */
public class CommandQueue {
    public final VkQueue vkQueue;
    public CommandQueue(final QueueFamily family, final int id) {
        try (MemoryStack stack = MemoryStack.stackPush()) {
            final PointerBuffer pQueue = stack.callocPointer(1);
            final VkDevice device = VKGlobalConstants.getInstance().selectedDevice.vkDevice;

            VK10.vkGetDeviceQueue(device, family.queueFamilyIndex, id, pQueue);
            
            this.vkQueue = new VkQueue(pQueue.get(0), device);
        }
    }
    
    public void waitIdle() {
        VK10.vkQueueWaitIdle(vkQueue);
    }
}
