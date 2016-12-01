/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.longlinkislong.gloop2.vkimpl;

import com.longlinkislong.gloop2.TextureFormat;
import java.nio.IntBuffer;
import java.nio.LongBuffer;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import static org.lwjgl.demo.vulkan.VKUtil.translateVulkanResult;
import org.lwjgl.system.MemoryStack;
import static org.lwjgl.vulkan.KHRSurface.VK_COMPOSITE_ALPHA_OPAQUE_BIT_KHR;
import static org.lwjgl.vulkan.KHRSurface.VK_SURFACE_TRANSFORM_IDENTITY_BIT_KHR;
import static org.lwjgl.vulkan.KHRSwapchain.VK_STRUCTURE_TYPE_SWAPCHAIN_CREATE_INFO_KHR;
import static org.lwjgl.vulkan.KHRSwapchain.vkCreateSwapchainKHR;
import static org.lwjgl.vulkan.KHRSwapchain.vkDestroySwapchainKHR;
import static org.lwjgl.vulkan.KHRSwapchain.vkGetSwapchainImagesKHR;
import org.lwjgl.vulkan.VK10;
import static org.lwjgl.vulkan.VK10.VK_IMAGE_USAGE_COLOR_ATTACHMENT_BIT;
import static org.lwjgl.vulkan.VK10.VK_NULL_HANDLE;
import static org.lwjgl.vulkan.VK10.VK_SHARING_MODE_EXCLUSIVE;
import org.lwjgl.vulkan.VkCommandBuffer;
import org.lwjgl.vulkan.VkCommandBufferBeginInfo;
import org.lwjgl.vulkan.VkDevice;
import org.lwjgl.vulkan.VkExtent2D;
import org.lwjgl.vulkan.VkImageMemoryBarrier;
import org.lwjgl.vulkan.VkImageViewCreateInfo;
import org.lwjgl.vulkan.VkSwapchainCreateInfoKHR;

/**
 *
 * @author zmichaels
 */
public final class KHRSwapchain {

    public long swapchain;
    public final List<VK10Texture2D> renderSurfaces;

    public KHRSwapchain(final KHRSurface surface, final int newWidth, final int newHeight, final KHRSwapchain oldSwapchain) {
        final VkDevice device = VKGlobalConstants.getInstance().selectedDevice.vkDevice;
        final KHRSurface.Format selectedFormat = surface.supportedFormats.get(0);

        int numberOfImages = surface.capabilities.minImageCount() + 1;

        if ((surface.capabilities.maxImageCount() > 0) && (numberOfImages > surface.capabilities.maxImageCount())) {
            //TODO: this is just a convoluted implementation of max
            numberOfImages = surface.capabilities.maxImageCount();
        }

        final VkExtent2D currentExtent = surface.capabilities.currentExtent();
        int currentWidth = currentExtent.width();
        int currentHeight = currentExtent.height();

        if (currentWidth != -1 && currentHeight != -1) {
            surface.width = currentWidth;
            surface.height = currentHeight;
        } else {
            surface.width = newWidth;
            surface.height = newHeight;
        }

        int preTransform;

        if ((surface.capabilities.supportedTransforms() & VK_SURFACE_TRANSFORM_IDENTITY_BIT_KHR) != 0) {
            preTransform = VK_SURFACE_TRANSFORM_IDENTITY_BIT_KHR;
        } else {
            preTransform = surface.capabilities.currentTransform();
        }

        try (MemoryStack stack = MemoryStack.stackPush()) {
            final VkSwapchainCreateInfoKHR swapchainCreateInfo = VkSwapchainCreateInfoKHR.callocStack(stack)
                    .sType(VK_STRUCTURE_TYPE_SWAPCHAIN_CREATE_INFO_KHR)
                    .surface(surface.surface)
                    .minImageCount(numberOfImages)
                    .imageFormat(selectedFormat.colorFormat)
                    .imageColorSpace(selectedFormat.colorSpace)
                    .imageUsage(VK_IMAGE_USAGE_COLOR_ATTACHMENT_BIT)
                    .preTransform(preTransform)
                    .imageArrayLayers(1)
                    .imageSharingMode(VK_SHARING_MODE_EXCLUSIVE)
                    .presentMode(surface.presentationMode)
                    .oldSwapchain((oldSwapchain != null) ? oldSwapchain.swapchain : VK_NULL_HANDLE)
                    .clipped(VK10.VK_TRUE)
                    .compositeAlpha(VK_COMPOSITE_ALPHA_OPAQUE_BIT_KHR);

            swapchainCreateInfo.imageExtent()
                    .width(surface.width)
                    .height(surface.height);

            final LongBuffer pSwapChain = stack.callocLong(1);
            final int err = vkCreateSwapchainKHR(device, swapchainCreateInfo, null, pSwapChain);

            if (err != VK10.VK_SUCCESS) {
                throw new AssertionError("Failed to create SwapChain: " + translateVulkanResult(err));
            }

            this.swapchain = pSwapChain.get(0);
        }

        final int imageCount;
        try (MemoryStack stack = MemoryStack.stackPush()) {
            final IntBuffer pImageCount = stack.callocInt(1);
            final int err = vkGetSwapchainImagesKHR(device, this.swapchain, pImageCount, null);

            if (err != VK10.VK_SUCCESS) {
                throw new AssertionError("Failed to get number of SwapChain images: " + translateVulkanResult(err));
            }

            imageCount = pImageCount.get(0);
        }

        final long[] images = new long[imageCount];
        try (MemoryStack stack = MemoryStack.stackPush()) {
            final IntBuffer pImageCount = stack.ints(imageCount);
            final LongBuffer pSwapchainImages = stack.callocLong(imageCount);
            final int err = vkGetSwapchainImagesKHR(device, this.swapchain, pImageCount, pSwapchainImages);

            if (err != VK10.VK_SUCCESS) {
                throw new AssertionError("Failed to get swapchain images: " + translateVulkanResult(err));
            }

            pSwapchainImages.position(0); // might not be needed
            pSwapchainImages.get(images).position(0);
        }

        final VkCommandBuffer setupCmdBuf = VKGlobalConstants.getInstance().selectedDevice.getFirstGraphicsCommandPool().newCommandBuffer();
        try (MemoryStack stack = MemoryStack.stackPush()) {
            final VkCommandBufferBeginInfo cmdBufBeginInfo = VkCommandBufferBeginInfo.callocStack(stack)
                    .sType(VK10.VK_STRUCTURE_TYPE_COMMAND_BUFFER_BEGIN_INFO);
            final int err = VK10.vkBeginCommandBuffer(setupCmdBuf, cmdBufBeginInfo);

            if (err != VK10.VK_SUCCESS) {
                throw new AssertionError("Failed to begin setup CommandBuffer: " + translateVulkanResult(err));
            }
        }
        //todo: end command buffer later

        final VK10Texture2D[] imageViews = new VK10Texture2D[imageCount];
        try (MemoryStack stack = MemoryStack.stackPush()) {
            final LongBuffer pBufferView = stack.callocLong(1);
            final VkImageViewCreateInfo colorAttachmentView = VkImageViewCreateInfo.callocStack(stack)
                    .sType(VK10.VK_STRUCTURE_TYPE_IMAGE_CREATE_INFO)
                    .format(selectedFormat.colorFormat)
                    .viewType(VK10.VK_IMAGE_VIEW_TYPE_2D);

            colorAttachmentView.components()
                    .r(VK10.VK_COMPONENT_SWIZZLE_R)
                    .g(VK10.VK_COMPONENT_SWIZZLE_G)
                    .b(VK10.VK_COMPONENT_SWIZZLE_B)
                    .a(VK10.VK_COMPONENT_SWIZZLE_A);

            colorAttachmentView.subresourceRange()
                    .aspectMask(VK10.VK_IMAGE_ASPECT_COLOR_BIT)
                    .baseMipLevel(0)
                    .levelCount(1)
                    .baseArrayLayer(0)
                    .layerCount(1);

            final VkImageMemoryBarrier.Buffer imageMemoryBarrier = VkImageMemoryBarrier.callocStack(imageCount, stack);

            for (int i = 0; i < imageCount; i++) {
                imageMemoryBarrier.get(i)
                        .sType(VK10.VK_STRUCTURE_TYPE_IMAGE_MEMORY_BARRIER)
                        .oldLayout(VK10.VK_IMAGE_LAYOUT_UNDEFINED)
                        .srcAccessMask(0)
                        .newLayout(VK10.VK_IMAGE_LAYOUT_COLOR_ATTACHMENT_OPTIMAL)
                        .dstAccessMask(VK10.VK_ACCESS_COLOR_ATTACHMENT_WRITE_BIT)
                        .srcQueueFamilyIndex(VK10.VK_QUEUE_FAMILY_IGNORED)
                        .dstQueueFamilyIndex(VK10.VK_QUEUE_FAMILY_IGNORED)
                        .image(images[i]);

                imageMemoryBarrier.subresourceRange()
                        .aspectMask(VK10.VK_IMAGE_ASPECT_COLOR_BIT)
                        .levelCount(1)
                        .layerCount(1);
            }
            
            for (int i = 0; i < imageCount; i++) {
                colorAttachmentView.image(images[i]);
                final int err = VK10.vkCreateImageView(device, colorAttachmentView, null, pBufferView);

                if (err != VK10.VK_SUCCESS) {
                    throw new AssertionError("Failed to create ImageView: " + translateVulkanResult(err));
                }

                imageViews[i] = new VK10Texture2D(
                        pBufferView.get(0),
                        images[i],
                        TextureFormat.RGBA8,
                        surface.width, surface.height);
            }

            final int srcStageFlags = VK10.VK_PIPELINE_STAGE_TOP_OF_PIPE_BIT;
            final int dstStageFlags = VK10.VK_PIPELINE_STAGE_TOP_OF_PIPE_BIT;

            VK10.vkCmdPipelineBarrier(setupCmdBuf, srcStageFlags, dstStageFlags, 0, null, null, imageMemoryBarrier);            
        }
        
        final int err = VK10.vkEndCommandBuffer(setupCmdBuf);
        if (err != VK10.VK_SUCCESS) {
            throw new AssertionError("Failed to end KHRSwapchain Setup command buffer: " + translateVulkanResult(err));
        }
        
        final CommandQueue queue = VKGlobalConstants.getInstance().selectedDevice.getFirstGraphicsFamily().getQueue();
        
        queue.submit(setupCmdBuf);
        queue.waitIdle();
        
        
        this.renderSurfaces = Collections.unmodifiableList(Arrays.asList(imageViews));
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
