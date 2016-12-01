/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.longlinkislong.gloop2.vkimpl;

import java.nio.IntBuffer;
import java.nio.LongBuffer;
import org.lwjgl.PointerBuffer;
import static org.lwjgl.demo.vulkan.VKUtil.translateVulkanResult;
import org.lwjgl.system.MemoryStack;
import static org.lwjgl.system.MemoryUtil.NULL;
import org.lwjgl.vulkan.VK10;
import org.lwjgl.vulkan.VkCommandBuffer;
import org.lwjgl.vulkan.VkDevice;
import org.lwjgl.vulkan.VkQueue;
import org.lwjgl.vulkan.VkSubmitInfo;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 * @author zmichaels
 */
public class CommandQueue {
    private static final Logger LOGGER = LoggerFactory.getLogger(CommandQueue.class);
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
    
    public static final CommandDependency[] NO_DEPENDENCIES = null;
    public static final long[] NO_SIGNALS = null;       

    public void submit(final CommandDependency[] dependencies, final long[] signals, final VkCommandBuffer... bufs) {
        if (bufs == null || bufs.length == 0) {
            return;
        }
        
        try (MemoryStack stack = MemoryStack.stackPush()) {
            final VkSubmitInfo submitInfo = createSubmitInfo(stack, dependencies, signals);            
            final PointerBuffer pCommandBuffers = stack.callocPointer(bufs.length);

            for (VkCommandBuffer buf : bufs) {
                if (buf == null || buf.address() == NULL) {
                    LOGGER.warn("Ignoring null command buffer!");
                } else {
                    pCommandBuffers.put(buf);
                }
            }
            
            pCommandBuffers.flip();            
            submitInfo.pCommandBuffers(pCommandBuffers);
            
            final int err = VK10.vkQueueSubmit(this.vkQueue, submitInfo, VK10.VK_NULL_HANDLE);
            if (err != VK10.VK_SUCCESS) {
                throw new AssertionError("Failed to submit CommandBuffer(s): " + translateVulkanResult(err));
            }
        }
    }
    
    private static VkSubmitInfo createSubmitInfo(final MemoryStack stack, final CommandDependency[] deps, final long[] signals) {
        final VkSubmitInfo out = VkSubmitInfo.callocStack(stack)
                .sType(VK10.VK_STRUCTURE_TYPE_SUBMIT_INFO);
                
        if (deps != null && deps.length > 0) {
            final IntBuffer pStages = stack.callocInt(deps.length);
            final LongBuffer pSemaphores = stack.callocLong(deps.length);
                        
            for (CommandDependency dep : deps) {
                pStages.put(dep.stage);
                pSemaphores.put(dep.semaphore);
            }
            
            pStages.flip();
            pSemaphores.flip();
            
            out.pWaitDstStageMask(pStages);
            out.pWaitSemaphores(pSemaphores);
        }
        
        if (signals != null) {
            final LongBuffer pSemaphores = stack.callocLong(signals.length);
            
            for (long signal : signals) {
                pSemaphores.put(signal);
            }
            
            pSemaphores.flip();
            
            out.pSignalSemaphores(pSemaphores);
        }
        
        return out;
    }
}
