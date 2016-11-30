/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.longlinkislong.gloop2.vkimpl;

import com.longlinkislong.gloop2.GLTerminatedException;
import java.nio.LongBuffer;
import java.util.ArrayList;
import java.util.List;
import org.lwjgl.PointerBuffer;
import static org.lwjgl.demo.vulkan.VKUtil.translateVulkanResult;
import org.lwjgl.glfw.GLFWVulkan;
import org.lwjgl.system.MemoryStack;
import static org.lwjgl.vulkan.EXTDebugReport.VK_DEBUG_REPORT_ERROR_BIT_EXT;
import static org.lwjgl.vulkan.EXTDebugReport.VK_DEBUG_REPORT_WARNING_BIT_EXT;
import static org.lwjgl.vulkan.EXTDebugReport.VK_EXT_DEBUG_REPORT_EXTENSION_NAME;
import static org.lwjgl.vulkan.EXTDebugReport.VK_STRUCTURE_TYPE_DEBUG_REPORT_CALLBACK_CREATE_INFO_EXT;
import static org.lwjgl.vulkan.EXTDebugReport.vkCreateDebugReportCallbackEXT;
import static org.lwjgl.vulkan.EXTDebugReport.vkDestroyDebugReportCallbackEXT;
import org.lwjgl.vulkan.VK10;
import static org.lwjgl.vulkan.VKUtil.VK_MAKE_VERSION;
import org.lwjgl.vulkan.VkApplicationInfo;
import org.lwjgl.vulkan.VkDebugReportCallbackCreateInfoEXT;
import org.lwjgl.vulkan.VkDebugReportCallbackEXT;
import org.lwjgl.vulkan.VkInstance;
import org.lwjgl.vulkan.VkInstanceCreateInfo;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 * @author zmichaels
 */
public final class VkGlobalConstants {
    public static final List<String> LAYERS = new ArrayList<>();
    private static final Logger LOGGER = LoggerFactory.getLogger(VkGlobalConstants.class);
    
    public final String appName = System.getProperty("com.longlinkislong.gloop2.app_name", "GLOOP_TEST");
    public final String engineName = System.getProperty("com.longlinkislong.gloop2.engine_name", "");
    
    private VkInstance createInstance() {
        final VkInstanceCreateInfo pCreateInfo = VkInstanceCreateInfo.calloc();
        
        try (MemoryStack stack = MemoryStack.stackPush()) {
            final VkApplicationInfo appInfo = VkApplicationInfo.callocStack(stack)
                    .sType(VK10.VK_STRUCTURE_TYPE_APPLICATION_INFO)
                    .pApplicationName(stack.UTF8(this.appName))
                    .pEngineName(stack.UTF8(this.engineName))
                    .apiVersion(VK_MAKE_VERSION(1, 0, 2));
            
            final PointerBuffer reqExt = GLFWVulkan.glfwGetRequiredInstanceExtensions();            
            final PointerBuffer ppEnabledExtensionNames = stack.callocPointer(reqExt.remaining() + 1);
            
            ppEnabledExtensionNames
                    .put(reqExt)
                    .put(stack.UTF8(VK_EXT_DEBUG_REPORT_EXTENSION_NAME))
                    .flip();
            
            final PointerBuffer ppEnabledLayerNames = stack.callocPointer(LAYERS.size());
            
            LAYERS.stream()
                    .map(stack::UTF8)
                    .forEach(ppEnabledLayerNames::put);
            
            ppEnabledLayerNames.flip();
            
            pCreateInfo.sType(VK10.VK_STRUCTURE_TYPE_INSTANCE_CREATE_INFO)
                    .pApplicationInfo(appInfo)
                    .ppEnabledExtensionNames(ppEnabledExtensionNames)
                    .ppEnabledLayerNames(ppEnabledLayerNames);
            
            final PointerBuffer pInstance = stack.callocPointer(1);
            final int err = VK10.vkCreateInstance(pCreateInfo, null, pInstance);
            
            if (err != VK10.VK_SUCCESS) {
                throw new AssertionError("Failed to create VkInstance: " + translateVulkanResult(err));
            }
            
            return new VkInstance(pInstance.get(0), pCreateInfo);
        }
    }
    
    private long setupDebug() {
        final VkDebugReportCallbackEXT debugCallback = new VkDebugReportCallbackEXT() {
            @Override
            public int invoke(int flags, int objectType, long object, long location, int messageCode, long pLayerPrefix, long pMessage, long pUserData) {
                LOGGER.error("VULKAN ERROR: " + getString(pMessage));
                return 0;
            }            
        };
        
        final int flags = VK_DEBUG_REPORT_ERROR_BIT_EXT | VK_DEBUG_REPORT_WARNING_BIT_EXT;
        
        try (MemoryStack stack = MemoryStack.stackPush()) {
            final VkDebugReportCallbackCreateInfoEXT dbgCreateInfo = VkDebugReportCallbackCreateInfoEXT.callocStack(stack)
                    .sType(VK_STRUCTURE_TYPE_DEBUG_REPORT_CALLBACK_CREATE_INFO_EXT)
                    .pfnCallback(debugCallback)
                    .flags(flags);
            
            final LongBuffer pCallback = stack.callocLong(1);
            final int err = vkCreateDebugReportCallbackEXT(instance, dbgCreateInfo, null, pCallback);
            
            if (err != VK10.VK_SUCCESS) {
                throw new AssertionError("Failed to create Vulkan debug report callback: " + translateVulkanResult(err));
            }
            
            return pCallback.get(0);
        }
    }
    
    private VkGlobalConstants() {
        this.instance = this.createInstance();
        this.debugCallbackHandle = this.setupDebug();
    }
    
    public void free() {
        vkDestroyDebugReportCallbackEXT(this.instance, this.debugCallbackHandle, null);
        VK10.vkDestroyInstance(instance, null);
        
        //TODO: maybe a thread interrupt would make more sense?
        throw new GLTerminatedException("Vulkan instance has ended!");
    }
    
    private static final class Holder {
        private static final VkGlobalConstants INSTANCE = new VkGlobalConstants();
    }
    
    public static VkGlobalConstants getInstance() {
        return Holder.INSTANCE;
    }
    
    private final long debugCallbackHandle;
    public final VkInstance instance;
}
