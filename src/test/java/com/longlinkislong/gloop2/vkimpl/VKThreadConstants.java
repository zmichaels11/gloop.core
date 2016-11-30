/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.longlinkislong.gloop2.vkimpl;

import java.nio.IntBuffer;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import org.lwjgl.system.MemoryStack;
import org.lwjgl.vulkan.VK10;
import org.lwjgl.vulkan.VkPhysicalDevice;
import org.lwjgl.vulkan.VkQueueFamilyProperties;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 * @author zmichaels
 */
public final class VKThreadConstants {

    public static final AtomicInteger DEFAULT_PHYSICAL_DEVICE_ID
            = new AtomicInteger(Integer.getInteger("com.longlinkislong.gloop.default_physical_device", 0));

    private static final Logger LOGGER = LoggerFactory.getLogger(VKThreadConstants.class);

    private static final ThreadLocal<VKThreadConstants> THREAD_INSTANCE = new ThreadLocal<VKThreadConstants>() {
        @Override
        public VKThreadConstants initialValue() {
            // create the instance if it is not yet assigned...
            return new VKThreadConstants();
        }
    };

    private List<VKQueue> listQueues() {
        try (MemoryStack stack = MemoryStack.stackPush()) {
            final IntBuffer pQueueCount = stack.callocInt(1);

            VK10.vkGetPhysicalDeviceQueueFamilyProperties(physicalDevice, pQueueCount, null);

            final int queueCount = pQueueCount.get(0);
            final VkQueueFamilyProperties.Buffer queueProps = VkQueueFamilyProperties.callocStack(queueCount, stack);

            VK10.vkGetPhysicalDeviceQueueFamilyProperties(physicalDevice, pQueueCount, queueProps);

            if (queueCount == 0) {
                throw new UnsupportedOperationException("Unable to find any queues!");
            }

            return Collections.unmodifiableList(
                    IntStream.range(0, queueCount)
                            .mapToObj(index -> new VKQueue(index, queueProps.get(index).queueFlags()))
                            .collect(Collectors.toList()));
        }
    }

    private VKThreadConstants() {
        this.physicalDevice = VKGlobalConstants.getInstance().physicalDevices.get(DEFAULT_PHYSICAL_DEVICE_ID.get());
        this.queues = this.listQueues();
    }

    public static VKThreadConstants getInstance() {
        return THREAD_INSTANCE.get();
    }

    public final VKQueue getFirstGraphicsQueue() {
        return this.queues.stream()
                .filter(queue -> queue.isGraphicsQueue)
                .findFirst()
                .orElseThrow(() -> new UnsupportedOperationException("The physical device does not have a Graphics Queue!"));
    }
    
    public final VKQueue getFirstComputeQueue() {
        return this.queues.stream()
                .filter(queue -> queue.isComputeQueue)
                .findFirst()
                .orElseThrow(() -> new UnsupportedOperationException("The physical device does not have a Compute Queue!"));
    }
    
    public final List<VKQueue> queues;    
    public final VkPhysicalDevice physicalDevice;
}
