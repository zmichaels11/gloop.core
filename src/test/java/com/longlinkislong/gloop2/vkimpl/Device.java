/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.longlinkislong.gloop2.vkimpl;

import java.nio.FloatBuffer;
import java.nio.IntBuffer;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import org.lwjgl.PointerBuffer;
import static org.lwjgl.demo.vulkan.VKUtil.translateVulkanResult;
import org.lwjgl.system.MemoryStack;
import org.lwjgl.vulkan.VK10;
import org.lwjgl.vulkan.VkDevice;
import org.lwjgl.vulkan.VkDeviceCreateInfo;
import org.lwjgl.vulkan.VkDeviceQueueCreateInfo;
import org.lwjgl.vulkan.VkPhysicalDevice;
import org.lwjgl.vulkan.VkPhysicalDeviceMemoryProperties;
import org.lwjgl.vulkan.VkQueueFamilyProperties;

/**
 *
 * @author zmichaels
 */
public final class Device {
    
    public final VkDevice vkDevice;
    public final VkPhysicalDevice physicalDevice;
    public final List<QueueFamily> queueFamilies;
    public final VkPhysicalDeviceMemoryProperties memoryProperties;
    public final Map<QueueFamily, CommandPool> commandPools;

    private static final Map<VkPhysicalDevice, Device> DEVICE_MAP = new HashMap<>();
    
    public static Device getDevice(final VkPhysicalDevice device) {
        return DEVICE_MAP.computeIfAbsent(device, Device::new);
    }
    
    private Device(final VkPhysicalDevice physicalDevice) {
        this.physicalDevice = Objects.requireNonNull(physicalDevice);        
        this.queueFamilies = this.listQueues();
        this.vkDevice = createDevice(this.queueFamilies);
        this.memoryProperties = getMemoryProperties();
        this.commandPools = createCommandPools(this.queueFamilies);
    }
    
    public CommandPool getFirstGraphicsCommandPool() {
        return this.commandPools.get(this.getFirstGraphicsQueue());
    }
    
    public CommandPool getFirstComputeCommandPool() {
        return this.commandPools.get(this.getFirstComputeQueue());
    }
    
    private Map<QueueFamily, CommandPool> createCommandPools(final List<QueueFamily> queueFamilies) {
        return queueFamilies.stream()
                .collect(Collectors.toMap(family -> family, family -> new CommandPool(vkDevice, family)));
    }
    
    private VkPhysicalDeviceMemoryProperties getMemoryProperties() {
        final VkPhysicalDeviceMemoryProperties out = VkPhysicalDeviceMemoryProperties.calloc();
        
        VK10.vkGetPhysicalDeviceMemoryProperties(physicalDevice, out);
        
        return out;
    }

    private List<QueueFamily> listQueues() {
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
                            .mapToObj(index -> new QueueFamily(index, queueProps.get(index).queueFlags()))
                            .sorted()
                            .collect(Collectors.toList()));
        }
    }
    
    private VkDevice createDevice(final List<QueueFamily> families) {
        try (MemoryStack stack = MemoryStack.stackPush()) {
            final FloatBuffer pQueuePriorities = stack.floats(0.0F);
            final VkDeviceQueueCreateInfo.Buffer queueCreateInfo = VkDeviceQueueCreateInfo.callocStack(families.size(), stack);
            
            for (int i = 0; i < families.size(); i++) {
                queueCreateInfo.get(i)
                        .sType(VK10.VK_STRUCTURE_TYPE_DEVICE_QUEUE_CREATE_INFO)
                        .queueFamilyIndex(families.get(i).queueFamilyIndex)
                        .pQueuePriorities(pQueuePriorities);                        
            }
            
            final List<String> extensions = VKGlobalConstants.getInstance().extensions;
            final PointerBuffer ppExtensionNames = stack.callocPointer(extensions.size());
            
            extensions.stream()
                    .map(stack::UTF8)
                    .forEach(ppExtensionNames::put);
            
            ppExtensionNames.flip();
            
            final List<String> layers = VKGlobalConstants.getInstance().layers;
            final PointerBuffer ppEnabledLayerNames = stack.callocPointer(layers.size());
            
            layers.stream()
                    .map(stack::UTF8)
                    .forEach(ppEnabledLayerNames::put);
            
            ppEnabledLayerNames.flip();
            
            final VkDeviceCreateInfo deviceCreateInfo = VkDeviceCreateInfo.callocStack(stack)
                    .sType(VK10.VK_STRUCTURE_TYPE_DEVICE_CREATE_INFO)
                    .pQueueCreateInfos(queueCreateInfo)
                    .ppEnabledExtensionNames(ppExtensionNames)
                    .ppEnabledLayerNames(ppEnabledLayerNames);
            
            final PointerBuffer pDevice = stack.callocPointer(1);
            final int err = VK10.vkCreateDevice(physicalDevice, deviceCreateInfo, null, pDevice);
            
            if (err != VK10.VK_SUCCESS) {
                throw new AssertionError("Failed to create device: " + translateVulkanResult(err));
            }                          
                        
            return new VkDevice(pDevice.get(0), physicalDevice, deviceCreateInfo);            
        }
    }

    public final QueueFamily getFirstGraphicsQueue() {
        return this.queueFamilies.stream()
                .filter(queue -> queue.isGraphicsQueue)
                .findFirst()
                .orElseThrow(() -> new UnsupportedOperationException("The physical device does not have a Graphics Queue!"));
    }

    public final QueueFamily getFirstComputeQueue() {
        return this.queueFamilies.stream()
                .filter(queue -> queue.isComputeQueue)
                .findFirst()
                .orElseThrow(() -> new UnsupportedOperationException("The physical device does not have a Compute Queue!"));
    }

    
}
