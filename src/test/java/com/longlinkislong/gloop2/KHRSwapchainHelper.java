/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.longlinkislong.gloop2;

import com.longlinkislong.gloop2.vkimpl.CommandQueue;
import com.longlinkislong.gloop2.vkimpl.Device;
import com.longlinkislong.gloop2.vkimpl.KHRSurface;
import com.longlinkislong.gloop2.vkimpl.KSwapchain;
import com.longlinkislong.gloop2.vkimpl.VK10Texture2D;
import com.longlinkislong.gloop2.vkimpl.VKGlobalConstants;
import java.nio.IntBuffer;
import java.nio.LongBuffer;
import static org.lwjgl.demo.vulkan.VKUtil.translateVulkanResult;
import org.lwjgl.system.MemoryStack;
import static org.lwjgl.system.MemoryUtil.NULL;
import static org.lwjgl.vulkan.KHRSurface.VK_COMPOSITE_ALPHA_OPAQUE_BIT_KHR;
import static org.lwjgl.vulkan.KHRSurface.VK_SURFACE_TRANSFORM_IDENTITY_BIT_KHR;
import static org.lwjgl.vulkan.KHRSwapchain.VK_STRUCTURE_TYPE_SWAPCHAIN_CREATE_INFO_KHR;
import static org.lwjgl.vulkan.KHRSwapchain.vkCreateSwapchainKHR;
import static org.lwjgl.vulkan.KHRSwapchain.vkDestroySwapchainKHR;
import static org.lwjgl.vulkan.KHRSwapchain.vkGetSwapchainImagesKHR;
import org.lwjgl.vulkan.VK10;
import static org.lwjgl.vulkan.VK10.VK_ACCESS_COLOR_ATTACHMENT_WRITE_BIT;
import static org.lwjgl.vulkan.VK10.VK_COMPONENT_SWIZZLE_A;
import static org.lwjgl.vulkan.VK10.VK_COMPONENT_SWIZZLE_B;
import static org.lwjgl.vulkan.VK10.VK_COMPONENT_SWIZZLE_G;
import static org.lwjgl.vulkan.VK10.VK_COMPONENT_SWIZZLE_R;
import static org.lwjgl.vulkan.VK10.VK_IMAGE_ASPECT_COLOR_BIT;
import static org.lwjgl.vulkan.VK10.VK_IMAGE_LAYOUT_COLOR_ATTACHMENT_OPTIMAL;
import static org.lwjgl.vulkan.VK10.VK_IMAGE_LAYOUT_UNDEFINED;
import static org.lwjgl.vulkan.VK10.VK_IMAGE_USAGE_COLOR_ATTACHMENT_BIT;
import static org.lwjgl.vulkan.VK10.VK_IMAGE_VIEW_TYPE_2D;
import static org.lwjgl.vulkan.VK10.VK_PIPELINE_STAGE_TOP_OF_PIPE_BIT;
import static org.lwjgl.vulkan.VK10.VK_QUEUE_FAMILY_IGNORED;
import static org.lwjgl.vulkan.VK10.VK_SHARING_MODE_EXCLUSIVE;
import static org.lwjgl.vulkan.VK10.VK_STRUCTURE_TYPE_IMAGE_MEMORY_BARRIER;
import static org.lwjgl.vulkan.VK10.VK_STRUCTURE_TYPE_IMAGE_VIEW_CREATE_INFO;
import static org.lwjgl.vulkan.VK10.VK_SUCCESS;
import static org.lwjgl.vulkan.VK10.VK_TRUE;
import static org.lwjgl.vulkan.VK10.vkCmdPipelineBarrier;
import org.lwjgl.vulkan.VkCommandBuffer;
import org.lwjgl.vulkan.VkCommandBufferBeginInfo;
import org.lwjgl.vulkan.VkDevice;
import org.lwjgl.vulkan.VkImageMemoryBarrier;
import org.lwjgl.vulkan.VkImageViewCreateInfo;
import org.lwjgl.vulkan.VkSwapchainCreateInfoKHR;

/**
 *
 * @author zmichaels
 */
public class KHRSwapchainHelper {



    public static long newSwapchain(KHRSurface surface, int colorFormat, int colorSpace, KSwapchain oldSwapChain, VkDevice device) {
        // Determine the number of images
        int desiredNumberOfSwapchainImages = surface.capabilities.minImageCount() + 1;
        if ((surface.capabilities.maxImageCount() > 0) && (desiredNumberOfSwapchainImages > surface.capabilities.maxImageCount())) {
            desiredNumberOfSwapchainImages = surface.capabilities.maxImageCount();
        }

        int preTransform;
        if ((surface.capabilities.supportedTransforms() & VK_SURFACE_TRANSFORM_IDENTITY_BIT_KHR) != 0) {
            preTransform = VK_SURFACE_TRANSFORM_IDENTITY_BIT_KHR;
        } else {
            preTransform = surface.capabilities.currentTransform();
        }

        final long swapChain;
        try (MemoryStack stack = MemoryStack.stackPush()) {
            final VkSwapchainCreateInfoKHR swapchainCI = VkSwapchainCreateInfoKHR.callocStack(stack)
                    .sType(VK_STRUCTURE_TYPE_SWAPCHAIN_CREATE_INFO_KHR)
                    .pNext(NULL)
                    .surface(surface.surface)
                    .minImageCount(desiredNumberOfSwapchainImages)
                    .imageFormat(colorFormat)
                    .imageColorSpace(colorSpace)
                    .imageUsage(VK_IMAGE_USAGE_COLOR_ATTACHMENT_BIT)
                    .preTransform(preTransform)
                    .imageArrayLayers(1)
                    .imageSharingMode(VK_SHARING_MODE_EXCLUSIVE)
                    .pQueueFamilyIndices(null)
                    .presentMode(surface.presentationMode)
                    .oldSwapchain(oldSwapChain == null ? VK10.VK_NULL_HANDLE : oldSwapChain.swapchain)
                    .clipped(VK_TRUE)
                    .compositeAlpha(VK_COMPOSITE_ALPHA_OPAQUE_BIT_KHR);

            swapchainCI.imageExtent()
                    .width(surface.width)
                    .height(surface.height);

            final LongBuffer pSwapChain = stack.callocLong(1);
            final int err = vkCreateSwapchainKHR(device, swapchainCI, null, pSwapChain);

            if (err != VK_SUCCESS) {
                throw new AssertionError("Failed to create swap chain: " + translateVulkanResult(err));
            }

            swapChain = pSwapChain.get(0);
        }

        // If we just re-created an existing swapchain, we should destroy the old swapchain at this point.
        // Note: destroying the swapchain also cleans up all its associated presentable images once the platform is done with them.
        if (oldSwapChain != null) {
            oldSwapChain.free();
        }

        return swapChain;
    }

    public static long newSwapchain(final VkDevice device, final KHRSurface surface, final int desiredNumberOfSwapchainImages, long oldSwapChain) {
        final KHRSurface.Format selectedFormat = surface.supportedFormats.get(0);
        final int colorFormat = selectedFormat.colorFormat;
        final int colorSpace = selectedFormat.colorSpace;

        final int preTransform;
        if ((surface.capabilities.supportedTransforms() & VK_SURFACE_TRANSFORM_IDENTITY_BIT_KHR) != 0) {
            preTransform = VK_SURFACE_TRANSFORM_IDENTITY_BIT_KHR;
        } else {
            preTransform = surface.capabilities.currentTransform();
        }

        try (MemoryStack stack = MemoryStack.stackPush()) {
            final VkSwapchainCreateInfoKHR swapchainCI = VkSwapchainCreateInfoKHR.callocStack(stack)
                    .sType(VK_STRUCTURE_TYPE_SWAPCHAIN_CREATE_INFO_KHR)
                    .pNext(NULL)
                    .surface(surface.surface)
                    .minImageCount(desiredNumberOfSwapchainImages)
                    .imageFormat(colorFormat)
                    .imageColorSpace(colorSpace)
                    .imageUsage(VK_IMAGE_USAGE_COLOR_ATTACHMENT_BIT)
                    .preTransform(preTransform)
                    .imageArrayLayers(1)
                    .imageSharingMode(VK_SHARING_MODE_EXCLUSIVE)
                    .pQueueFamilyIndices(null)
                    .presentMode(surface.presentationMode)
                    .oldSwapchain(oldSwapChain)
                    .clipped(VK_TRUE)
                    .compositeAlpha(VK_COMPOSITE_ALPHA_OPAQUE_BIT_KHR);

            final LongBuffer pSwapChain = stack.callocLong(1);
            final int err = vkCreateSwapchainKHR(device, swapchainCI, null, pSwapChain);

            if (err != VK10.VK_SUCCESS) {
                throw new AssertionError("Failed to create KHRSwapChain: " + translateVulkanResult(err));
            }

            if (oldSwapChain != VK10.VK_NULL_HANDLE) {
                vkDestroySwapchainKHR(device, oldSwapChain, null);
            }

            return pSwapChain.get(0);
        }
    }

    public static VK10Texture2D[] createImageViews(VkDevice device, long[] images, int colorFormat, int width, int height) {
        final VK10Texture2D[] imageViews;

        try (MemoryStack stack = MemoryStack.stackPush()) {
            final VkImageViewCreateInfo colorAttachmentView = VkImageViewCreateInfo.callocStack(stack)
                    .sType(VK_STRUCTURE_TYPE_IMAGE_VIEW_CREATE_INFO)
                    .pNext(NULL)
                    .format(colorFormat)
                    .viewType(VK_IMAGE_VIEW_TYPE_2D);

            colorAttachmentView.components()
                    .r(VK_COMPONENT_SWIZZLE_R)
                    .g(VK_COMPONENT_SWIZZLE_G)
                    .b(VK_COMPONENT_SWIZZLE_B)
                    .a(VK_COMPONENT_SWIZZLE_A);

            colorAttachmentView.subresourceRange()
                    .aspectMask(VK_IMAGE_ASPECT_COLOR_BIT)
                    .baseMipLevel(0)
                    .levelCount(1)
                    .baseArrayLayer(0)
                    .layerCount(1);

            imageViews = new VK10Texture2D[images.length];
            final LongBuffer pBufferView = stack.callocLong(1);

            for (int i = 0; i < images.length; i++) {
                colorAttachmentView.image(images[i]);
                final int err = VK10.vkCreateImageView(device, colorAttachmentView, null, pBufferView);

                if (err != VK10.VK_SUCCESS) {
                    throw new AssertionError("Failed to create image view: " + translateVulkanResult(err));
                }

                imageViews[i] = new VK10Texture2D(pBufferView.get(0), images[i], TextureFormat.RGBA8, width, height);
            }
        }

        waitOnImages(images);

        return imageViews;
    }

    public static long[] getSwapchainImages(VkDevice device, long swapchain) {
        final int imageCount;
        try (MemoryStack stack = MemoryStack.stackPush()) {
            final IntBuffer pImageCount = stack.callocInt(1);
            final int err = vkGetSwapchainImagesKHR(device, swapchain, pImageCount, null);

            if (err != VK10.VK_SUCCESS) {
                throw new AssertionError("Failed to retrieve number of KHRSwapchain images: " + translateVulkanResult(err));
            }

            imageCount = pImageCount.get(0);
        }

        final long[] images = new long[imageCount];
        try (MemoryStack stack = MemoryStack.stackPush()) {
            final IntBuffer pImageCount = stack.ints(imageCount);
            final LongBuffer pSwapchainImages = stack.callocLong(imageCount);
            final int err = vkGetSwapchainImagesKHR(device, swapchain, pImageCount, pSwapchainImages);

            if (err != VK_SUCCESS) {
                throw new AssertionError("Failed to get KHRSwapchain images: " + translateVulkanResult(err));
            }

            pSwapchainImages.get(images).position(0);
        }

        return images;
    }

    private static void waitOnImages(long[] images) {
        final Device device = VKGlobalConstants.getInstance().selectedDevice;
        final VkCommandBuffer cmdBuffer = device.getFirstGraphicsCommandPool().newCommandBuffer();

        try (MemoryStack stack = MemoryStack.stackPush()) {
            final VkImageMemoryBarrier.Buffer imageMemoryBarrier = VkImageMemoryBarrier.callocStack(images.length, stack)
                    .sType(VK_STRUCTURE_TYPE_IMAGE_MEMORY_BARRIER)
                    .pNext(NULL)
                    .oldLayout(VK_IMAGE_LAYOUT_UNDEFINED)
                    .srcAccessMask(0)
                    .newLayout(VK_IMAGE_LAYOUT_COLOR_ATTACHMENT_OPTIMAL)
                    .dstAccessMask(VK_ACCESS_COLOR_ATTACHMENT_WRITE_BIT)
                    .srcQueueFamilyIndex(VK_QUEUE_FAMILY_IGNORED)
                    .dstQueueFamilyIndex(VK_QUEUE_FAMILY_IGNORED);

            imageMemoryBarrier.subresourceRange()
                    .aspectMask(VK_IMAGE_ASPECT_COLOR_BIT)
                    .baseMipLevel(0)
                    .levelCount(1)
                    .layerCount(1);

            for (int i = 0; i < images.length; i++) {
                imageMemoryBarrier.get(i).image(images[i]);
            }

            final int srcStageFlags = VK_PIPELINE_STAGE_TOP_OF_PIPE_BIT;
            final int destStageFlags = VK_PIPELINE_STAGE_TOP_OF_PIPE_BIT;
            final VkCommandBufferBeginInfo cmdBufInfo = VkCommandBufferBeginInfo.callocStack(stack)
                    .sType(VK10.VK_STRUCTURE_TYPE_COMMAND_BUFFER_BEGIN_INFO);

            final int err = VK10.vkBeginCommandBuffer(cmdBuffer, cmdBufInfo);

            if (err != VK10.VK_SUCCESS) {
                throw new AssertionError("Failed to begin KHRSwapchain image creation CommandBuffer!" + translateVulkanResult(err));
            }

            // Put barrier inside setup command buffer
            vkCmdPipelineBarrier(cmdBuffer, srcStageFlags, destStageFlags, 0,
                    null, // no memory barriers
                    null, // no buffer memory barriers
                    imageMemoryBarrier); // one image memory barrier                        
        }

        final int err = VK10.vkEndCommandBuffer(cmdBuffer);
        if (err != VK10.VK_SUCCESS) {
            throw new AssertionError("Failed to end KHRSwapchain image creation CommandBuffer!" + translateVulkanResult(err));
        }

        final CommandQueue queue = device.getFirstGraphicsFamily().getQueue();

        queue.submit(CommandQueue.NO_DEPENDENCIES, CommandQueue.NO_SIGNALS, cmdBuffer);
        queue.waitIdle();
    }
}
