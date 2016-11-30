/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.longlinkislong.gloop2.vkimpl;

import java.util.concurrent.atomic.AtomicInteger;
import org.lwjgl.vulkan.VkPhysicalDevice;

/**
 *
 * @author zmichaels
 */
public final class VKThreadConstants {
    
    public static final AtomicInteger DEFAULT_PHYSICAL_DEVICE_ID
            = new AtomicInteger(Integer.getInteger("com.longlinkislong.gloop.default_physical_device", 0));
    

    private static final ThreadLocal<VKThreadConstants> THREAD_INSTANCE = new ThreadLocal<VKThreadConstants>() {
        @Override
        public VKThreadConstants initialValue() {
            // create the instance if it is not yet assigned...
            return new VKThreadConstants();
        }
    };

           

    private VKThreadConstants() {
        final int vkPhysicalDeviceId = DEFAULT_PHYSICAL_DEVICE_ID.get();
        final VkPhysicalDevice vkPhysicalDevice = VKGlobalConstants.getInstance().physicalDevices.get(vkPhysicalDeviceId);
        
        this.physicalDevice = Device.getDevice(vkPhysicalDevice);
    }

    public static VKThreadConstants getInstance() {
        return THREAD_INSTANCE.get();
    }
    
    

              
        
    public final Device physicalDevice;
}
