/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.longlinkislong.gloop2.vkimpl;

import java.nio.ByteBuffer;
import java.nio.FloatBuffer;
import java.nio.IntBuffer;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.lwjgl.PointerBuffer;
import static org.lwjgl.demo.vulkan.VKUtil.translateVulkanResult;
import org.lwjgl.system.MemoryStack;
import static org.lwjgl.vulkan.EXTDebugReport.VK_EXT_DEBUG_REPORT_EXTENSION_NAME;
import org.lwjgl.vulkan.VK10;
import static org.lwjgl.vulkan.VKUtil.VK_MAKE_VERSION;
import org.lwjgl.vulkan.VkApplicationInfo;
import org.lwjgl.vulkan.VkDevice;
import org.lwjgl.vulkan.VkDeviceCreateInfo;
import org.lwjgl.vulkan.VkDeviceQueueCreateInfo;
import org.lwjgl.vulkan.VkInstance;
import org.lwjgl.vulkan.VkInstanceCreateInfo;
import org.lwjgl.vulkan.VkPhysicalDevice;
import org.lwjgl.vulkan.VkPhysicalDeviceMemoryProperties;
import org.lwjgl.vulkan.VkQueueFamilyProperties;

/**
 *
 * @author zmichaels
 */
public class VKThreadConstants {    
    public static final boolean VALIDATION = Boolean.getBoolean("vulkan.validation");
    public static final List<String> LAYERS = new ArrayList<>();
    public static final String APP_NAME = System.getProperty("com.longlinkislong.gloop2.app_name", "GLOOP TEST");
    public static final String ENGINE_NAME = System.getProperty("com.longlinkislong.gloop2.engine_name", "GLOOP2.VULKAN10");
    
    static {
        if (VALIDATION) {
            LAYERS.add("VK_LAYER_LUNARG_standard_validation");
        }
    }
    
    private static final ThreadLocal<VKThreadConstants> INSTANCES = new ThreadLocal<>();

    private final Map<Integer, VK10RenderPass> renderPassDefinitions = new HashMap<>();

    public static void create(final VkDevice device) {
        INSTANCES.set(new VKThreadConstants(device));
    }
    
    public static class DeviceInfo {
        public VkDevice device;
        public int queueFamilyIndex;
        public VkPhysicalDeviceMemoryProperties memoryProperties;
    }

    public static DeviceInfo createDevice(final VkPhysicalDevice physicalDevice, final int queueFamilyIndex, final List<String> extensions) {        
        try (MemoryStack stack = MemoryStack.stackPush()) {
            final FloatBuffer pQueuePriorities = stack.floats(0.0F);
            final VkDeviceQueueCreateInfo.Buffer queueCreateInfo = VkDeviceQueueCreateInfo.callocStack(1, stack)
                    .sType(VK10.VK_STRUCTURE_TYPE_DEVICE_QUEUE_CREATE_INFO)
                    .queueFamilyIndex(queueFamilyIndex)
                    .pQueuePriorities(pQueuePriorities);
            
            final PointerBuffer ppExtensionNames = stack.callocPointer(extensions.size());
            
            extensions.stream()
                    .map(stack::UTF8)
                    .forEach(ppExtensionNames::put);
            
            ppExtensionNames.flip();
            
            final PointerBuffer ppEnabledLayerNames = stack.callocPointer(LAYERS.size());
            
            LAYERS.stream()
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
            
            final DeviceInfo out = new DeviceInfo();
            
            out.device = new VkDevice(pDevice.get(0), physicalDevice, deviceCreateInfo);
            out.memoryProperties = VkPhysicalDeviceMemoryProperties.calloc();
            out.queueFamilyIndex = queueFamilyIndex;
            
            VK10.vkGetPhysicalDeviceMemoryProperties(physicalDevice, out.memoryProperties);
            
            return out;
        }
    }          
    
    public static int getGraphicsQueueIndex(final VkPhysicalDevice physicalDevice) {
        try (MemoryStack stack = MemoryStack.stackPush()) {
            final IntBuffer pQueueCount = stack.callocInt(1);
            
            VK10.vkGetPhysicalDeviceQueueFamilyProperties(physicalDevice, pQueueCount, null);
            
            final int queueCount = pQueueCount.get(0);
            final VkQueueFamilyProperties.Buffer queueProps = VkQueueFamilyProperties.callocStack(queueCount);
            
            VK10.vkGetPhysicalDeviceQueueFamilyProperties(physicalDevice, pQueueCount, queueProps);
            
            for (int i = 0; i < queueCount; i++) {
                if ((queueProps.get(i).queueFlags() & VK10.VK_QUEUE_GRAPHICS_BIT) != 0) {
                    return i;
                }
            }
            
            throw new UnsupportedOperationException("No graphics queue was found!");
        }
    }
        
    
    public static int getPhysicalDeviceCount(final VkInstance instance) {
        try (MemoryStack stack = MemoryStack.stackPush()) {
            final IntBuffer pDeviceCount = stack.callocInt(1);
            final int err = VK10.vkEnumeratePhysicalDevices(instance, pDeviceCount, null);
            
            if (err != VK10.VK_SUCCESS) {
                throw new AssertionError("Failed to get number of physical devices: " + translateVulkanResult(err));
            }

            return pDeviceCount.get(0);
        }
    }
    
    public static VkPhysicalDevice getPhysicalDevice(final VkInstance instance, final int deviceId) {        
        final int physicalDeviceCount = getPhysicalDeviceCount(instance);
        
        try (MemoryStack stack = MemoryStack.stackPush()) {
            final PointerBuffer pPhysicalDevices = stack.callocPointer(physicalDeviceCount);
            final IntBuffer pPhysicalDeviceCount = stack.ints(physicalDeviceCount);            
            final int err = VK10.vkEnumeratePhysicalDevices(instance, pPhysicalDeviceCount, pPhysicalDevices);
            
            if (err != VK10.VK_SUCCESS) {
                throw new AssertionError("Failed to get physical devices: " + translateVulkanResult(err));
            }
            
            return new VkPhysicalDevice(pPhysicalDevices.get(deviceId), instance);
        }
    }

    public VKThreadConstants(final VkDevice device) {
        this.device = device;
    }
    
    private final VkDevice device;

    public VkDevice getDevice() {
        return this.device;
    }

    public static VKThreadConstants getInstance() {
        return INSTANCES.get();
    }

    public VK10RenderPass getRenderPass(final int colorFormat) {
        return renderPassDefinitions.computeIfAbsent(colorFormat, VK10RenderPass::new);
    }
}
