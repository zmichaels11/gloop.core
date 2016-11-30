/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.longlinkislong.gloop2.vkimpl;

import java.nio.LongBuffer;
import org.lwjgl.PointerBuffer;
import static org.lwjgl.demo.vulkan.VKUtil.translateVulkanResult;
import org.lwjgl.system.MemoryStack;
import org.lwjgl.vulkan.VK10;
import org.lwjgl.vulkan.VkCommandBuffer;
import org.lwjgl.vulkan.VkCommandBufferAllocateInfo;
import org.lwjgl.vulkan.VkCommandPoolCreateInfo;
import org.lwjgl.vulkan.VkDevice;

/**
 *
 * @author zmichaels
 */
public final class CommandPool {
    public final long id;
    
    CommandPool(final VkDevice device, final QueueFamily family) {
        try (MemoryStack stack = MemoryStack.stackPush()) {
            final VkCommandPoolCreateInfo cmdPoolInfo = VkCommandPoolCreateInfo.callocStack(stack)
                    .sType(VK10.VK_STRUCTURE_TYPE_COMMAND_POOL_CREATE_INFO)
                    .queueFamilyIndex(family.queueFamilyIndex)
                    .flags(VK10.VK_COMMAND_POOL_CREATE_RESET_COMMAND_BUFFER_BIT);
                        
            final LongBuffer pCmdPool = stack.callocLong(1);
            final int err = VK10.vkCreateCommandPool(device, cmdPoolInfo, null, pCmdPool);
            
            if (err != VK10.VK_SUCCESS) {
                throw new AssertionError("Failed to create command pool: " + translateVulkanResult(err));
            }
            
            this.id = pCmdPool.get(0);
        }
    }
    
    public VkCommandBuffer newCommandBuffer() {
        try (MemoryStack stack = MemoryStack.stackPush()) {
            final VkCommandBufferAllocateInfo cmdBufAllocateInfo = VkCommandBufferAllocateInfo.callocStack(stack)
                    .sType(VK10.VK_STRUCTURE_TYPE_COMMAND_BUFFER_ALLOCATE_INFO)
                    .commandPool(id)
                    .level(VK10.VK_COMMAND_BUFFER_LEVEL_PRIMARY)
                    .commandBufferCount(1);
            
            final VkDevice device = VKGlobalConstants.getInstance().selectedDevice.vkDevice;
            final PointerBuffer pCommandBuffer = stack.callocPointer(1);
            final int err = VK10.vkAllocateCommandBuffers(device, cmdBufAllocateInfo, pCommandBuffer);
            
            if (err != VK10.VK_SUCCESS) {
                throw new AssertionError("Failed to allocate command buffer: " + translateVulkanResult(err));
            }
            
            return new VkCommandBuffer(pCommandBuffer.get(0), device);
        }
    }
    
    public VkCommandBuffer[] newCommandBuffers(final int count) {
        try (MemoryStack stack = MemoryStack.stackPush()) {
            final VkCommandBufferAllocateInfo cmdBufAllocateInfo = VkCommandBufferAllocateInfo.callocStack(stack)
                    .sType(VK10.VK_STRUCTURE_TYPE_COMMAND_BUFFER_ALLOCATE_INFO)
                    .commandPool(id)
                    .level(VK10.VK_COMMAND_BUFFER_LEVEL_PRIMARY)
                    .commandBufferCount(count);
            
            final VkDevice device = VKGlobalConstants.getInstance().selectedDevice.vkDevice;
            final PointerBuffer pCommandBuffer = stack.callocPointer(count);
            final int err = VK10.vkAllocateCommandBuffers(device, cmdBufAllocateInfo, pCommandBuffer);
            
            if (err != VK10.VK_SUCCESS) {
                throw new AssertionError("Failed to allocate command buffers: " + translateVulkanResult(err));
            }
            
            final VkCommandBuffer[] out = new VkCommandBuffer[count];
            
            for (int i = 0; i < count; i++) {
                out[i] = new VkCommandBuffer(pCommandBuffer.get(i), device);
            }
            
            return out;
        }
    }
}
