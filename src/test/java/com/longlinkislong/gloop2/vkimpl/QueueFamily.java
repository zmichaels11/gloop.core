/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.longlinkislong.gloop2.vkimpl;

import java.nio.IntBuffer;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;
import org.lwjgl.PointerBuffer;
import static org.lwjgl.demo.vulkan.VKUtil.translateVulkanResult;
import org.lwjgl.system.MemoryStack;
import static org.lwjgl.vulkan.KHRSurface.vkGetPhysicalDeviceSurfaceSupportKHR;
import org.lwjgl.vulkan.VK10;
import org.lwjgl.vulkan.VkDevice;
import org.lwjgl.vulkan.VkPhysicalDevice;
import org.lwjgl.vulkan.VkQueue;

/**
 *
 * @author zmichaels
 */
public class QueueFamily implements Comparable<QueueFamily> {

    public final boolean isGraphicsQueue;
    public final boolean isComputeQueue;
    public final boolean isTransferQueue;
    public final boolean hasSparseBinding;
    public final int queueFamilyIndex;
    public final int queueCount;    

    public QueueFamily(final int queueIndex, final int flags, final int count) {
        this.queueFamilyIndex = queueIndex;
        this.isComputeQueue = (flags & VK10.VK_QUEUE_COMPUTE_BIT) != 0;
        this.isGraphicsQueue = (flags & VK10.VK_QUEUE_GRAPHICS_BIT) != 0;
        this.isTransferQueue = (flags & VK10.VK_QUEUE_TRANSFER_BIT) != 0;
        this.hasSparseBinding = (flags & VK10.VK_QUEUE_SPARSE_BINDING_BIT) != 0;
        this.queueCount = count;
    }
    
    public boolean canPresent(final Surface surface) {
        try (MemoryStack stack = MemoryStack.stackPush()) {
            final VkPhysicalDevice physicalDevice = VKGlobalConstants.getInstance().selectedDevice.physicalDevice;
            final IntBuffer pSupportsPresent = stack.callocInt(1);
            final int err = vkGetPhysicalDeviceSurfaceSupportKHR(physicalDevice, queueFamilyIndex, surface.surface, pSupportsPresent);
            
            if (err != VK10.VK_SUCCESS) {
                throw new AssertionError("Failed to check if queue supports present: " + translateVulkanResult(err));
            }
            
            return pSupportsPresent.get(0) == VK10.VK_TRUE;
        }                
    }

    private final Map<Integer, VkQueue> queues = new HashMap<>();

    private VkQueue newQueue(int id) {
        try (MemoryStack stack = MemoryStack.stackPush()) {
            final PointerBuffer pQueue = stack.callocPointer(1);
            final VkDevice device = VKGlobalConstants.getInstance().selectedDevice.vkDevice;

            VK10.vkGetDeviceQueue(device, queueFamilyIndex, id, pQueue);

            return new VkQueue(pQueue.get(0), device);
        }
    }

    private final AtomicInteger queueIncrementor = new AtomicInteger(0);
    private final ThreadLocal<Integer> queueSelector = new ThreadLocal<Integer>() {
        @Override
        protected Integer initialValue() {
            return queueIncrementor.getAndIncrement() % queueCount;
        }
    };
    
    public VkQueue getQueue() {
        return getQueue(queueSelector.get());
    }

    public VkQueue getQueue(int id) {
        return queues.computeIfAbsent(id, this::newQueue);
    }

    @Override
    public String toString() {
        final StringBuilder out = new StringBuilder(1024);

        out.append("QueueFamily ");
        out.append(queueFamilyIndex);
        out.append(": ");
        out.append((this.isGraphicsQueue) ? "G" : "");
        out.append((this.isComputeQueue) ? "C" : "");
        out.append((this.isTransferQueue) ? "T" : "");
        out.append((this.hasSparseBinding) ? "S" : "");
        out.append(" x");
        out.append(this.queueCount);

        return out.toString();
    }

    private int getFeatureCount() {
        int features = 0;

        if (this.isGraphicsQueue) {
            features++;
        }

        if (this.isComputeQueue) {
            features++;
        }

        if (this.isTransferQueue) {
            features++;
        }

        return features;
    }

    // sorts the queues such that the queue supporting the most features appears first.
    @Override
    public int compareTo(QueueFamily o) {
        return (o.getFeatureCount() - this.getFeatureCount());
    }
}
