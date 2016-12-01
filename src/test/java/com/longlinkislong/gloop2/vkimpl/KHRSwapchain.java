/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.longlinkislong.gloop2.vkimpl;

import static org.lwjgl.vulkan.KHRSwapchain.vkDestroySwapchainKHR;
import static org.lwjgl.vulkan.VK10.VK_NULL_HANDLE;
import org.lwjgl.vulkan.VkDevice;

/**
 *
 * @author zmichaels
 */
public final class KHRSwapchain {

    public long swapchain;

    public KHRSwapchain(final KHRSurface surface) {
        this(surface, null);
    }
    
    public KHRSwapchain(final KHRSurface surface, final KHRSwapchain oldSwapchain) {

    }

    public boolean isValid() {
        return this.swapchain != 0L;
    }

    public void free() {
        if (this.isValid()) {
            final VkDevice device = VKGlobalConstants.getInstance().selectedDevice.vkDevice;

            vkDestroySwapchainKHR(device, this.swapchain, null);
            this.swapchain = VK_NULL_HANDLE;
        }
    }

    
}
