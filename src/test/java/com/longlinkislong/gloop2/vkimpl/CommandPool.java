/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.longlinkislong.gloop2.vkimpl;

import java.nio.LongBuffer;
import static org.lwjgl.demo.vulkan.VKUtil.translateVulkanResult;
import org.lwjgl.system.MemoryStack;
import org.lwjgl.vulkan.VK10;
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
}
