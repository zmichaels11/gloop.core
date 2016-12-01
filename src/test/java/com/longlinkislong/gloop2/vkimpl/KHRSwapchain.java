/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.longlinkislong.gloop2.vkimpl;

import com.longlinkislong.gloop2.KHRSwapchainHelper;
import static com.longlinkislong.gloop2.KHRSwapchainHelper.createImageViews;
import static com.longlinkislong.gloop2.KHRSwapchainHelper.getSwapchainImages;
import static com.longlinkislong.gloop2.KHRSwapchainHelper.newSwapchain;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import static org.lwjgl.vulkan.KHRSwapchain.vkDestroySwapchainKHR;
import org.lwjgl.vulkan.VK10;
import static org.lwjgl.vulkan.VK10.VK_NULL_HANDLE;
import org.lwjgl.vulkan.VkDevice;
import org.lwjgl.vulkan.VkExtent2D;

/**
 *
 * @author zmichaels
 */
public final class KHRSwapchain {

    public long swapchain;
    public final VK10Texture2D[] framebuffers;

    public KHRSwapchain(final KHRSurface surface) {
        this(surface, null);
    }
    
    public KHRSwapchain(final KHRSurface surface, final KHRSwapchain oldSwapchain) {
        final VkDevice device = VKGlobalConstants.getInstance().selectedDevice.vkDevice;
        final KHRSurface.Format surfaceFormat = surface.supportedFormats.get(0);
        final int colorFormat = surfaceFormat.colorFormat;
        final int colorSpace = surfaceFormat.colorSpace;        

        VkExtent2D currentExtent = surface.capabilities.currentExtent();
        int currentWidth = currentExtent.width();
        int currentHeight = currentExtent.height();

        if (currentWidth != -1 && currentHeight != -1) {
            surface.width = currentWidth;
            surface.height = currentHeight;
        }

        final long hOldSwapChain = (oldSwapchain == null) ? VK10.VK_NULL_HANDLE : oldSwapchain.swapchain;
        
        this.swapchain = newSwapchain(surface, colorFormat, colorSpace, hOldSwapChain, device);
        
        final long[] images = getSwapchainImages(device, swapchain);
        final VK10Texture2D[] imageViews = createImageViews(device, images, colorFormat, surface.width, surface.height);
        
        this.framebuffers = imageViews;
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
