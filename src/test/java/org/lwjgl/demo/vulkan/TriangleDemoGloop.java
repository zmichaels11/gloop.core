/*
 * Copyright LWJGL. All rights reserved.
 * License terms: http://lwjgl.org/license.php
 */
package org.lwjgl.demo.vulkan;

import com.longlinkislong.gloop2.Buffer;
import com.longlinkislong.gloop2.BufferCreateInfo;
import com.longlinkislong.gloop2.ObjectFactoryManager;
import com.longlinkislong.gloop2.RasterPipeline;
import com.longlinkislong.gloop2.RasterPipelineCreateInfo;
import com.longlinkislong.gloop2.ShaderCreateInfo;
import com.longlinkislong.gloop2.ShaderType;
import com.longlinkislong.gloop2.VertexInputs;
import com.longlinkislong.gloop2.VertexAttribute;
import com.longlinkislong.gloop2.VertexAttributeFormat;
import com.longlinkislong.gloop2.vkimpl.CommandPool;
import com.longlinkislong.gloop2.vkimpl.VK10Buffer;
import com.longlinkislong.gloop2.vkimpl.VK10BufferFactory;
import com.longlinkislong.gloop2.vkimpl.VK10RasterPipeline;
import com.longlinkislong.gloop2.vkimpl.VK10RenderPass;
import com.longlinkislong.gloop2.vkimpl.VKGlobalConstants;
import com.longlinkislong.gloop2.vkimpl.VKThreadConstants;
import static org.lwjgl.system.MemoryUtil.*;
import static org.lwjgl.vulkan.KHRSwapchain.*;
import static org.lwjgl.vulkan.KHRSurface.*;
import static org.lwjgl.vulkan.VK10.*;
import static org.lwjgl.demo.vulkan.VKUtil.*;
import static org.lwjgl.glfw.GLFW.*;
import static org.lwjgl.glfw.GLFWVulkan.*;

import java.io.IOException;
import java.nio.FloatBuffer;
import java.nio.IntBuffer;
import java.nio.LongBuffer;
import org.junit.Test;

import org.lwjgl.PointerBuffer;
import org.lwjgl.glfw.GLFWKeyCallback;
import org.lwjgl.glfw.GLFWWindowSizeCallback;
import org.lwjgl.vulkan.VkClearValue;
import org.lwjgl.vulkan.VkCommandBuffer;
import org.lwjgl.vulkan.VkCommandBufferAllocateInfo;
import org.lwjgl.vulkan.VkCommandBufferBeginInfo;
import org.lwjgl.vulkan.VkCommandPoolCreateInfo;
import org.lwjgl.vulkan.VkDevice;
import org.lwjgl.vulkan.VkExtent2D;
import org.lwjgl.vulkan.VkFramebufferCreateInfo;
import org.lwjgl.vulkan.VkImageMemoryBarrier;
import org.lwjgl.vulkan.VkImageViewCreateInfo;
import org.lwjgl.vulkan.VkInstance;
import org.lwjgl.vulkan.VkPhysicalDevice;
import org.lwjgl.vulkan.VkPhysicalDeviceMemoryProperties;
import org.lwjgl.vulkan.VkPresentInfoKHR;
import org.lwjgl.vulkan.VkQueue;
import org.lwjgl.vulkan.VkQueueFamilyProperties;
import org.lwjgl.vulkan.VkRect2D;
import org.lwjgl.vulkan.VkRenderPassBeginInfo;
import org.lwjgl.vulkan.VkSemaphoreCreateInfo;
import org.lwjgl.vulkan.VkSubmitInfo;
import org.lwjgl.vulkan.VkSurfaceCapabilitiesKHR;
import org.lwjgl.vulkan.VkSurfaceFormatKHR;
import org.lwjgl.vulkan.VkSwapchainCreateInfoKHR;
import org.lwjgl.vulkan.VkViewport;

/**
 * Renders a simple triangle on a cornflower blue background on a GLFW window
 * with Vulkan.
 *
 * @author Kai Burjack
 */
public class TriangleDemoGloop {

    static {
        System.setProperty("org.slf4j.simpleLogger.defaultLogLevel", "debug");
    }
    
    /**
     * Remove if added to spec.
     */
    private static final int VK_FLAGS_NONE = 0;

    /**
     * This is just -1L, but it is nicer as a symbolic constant.
     */
    private static final long UINT64_MAX = 0xFFFFFFFFFFFFFFFFL;

    private static class ColorFormatAndSpace {

        int colorFormat;
        int colorSpace;
    }

    private static ColorFormatAndSpace getColorFormatAndSpace(VkPhysicalDevice physicalDevice, long surface) {
        IntBuffer pQueueFamilyPropertyCount = memAllocInt(1);
        vkGetPhysicalDeviceQueueFamilyProperties(physicalDevice, pQueueFamilyPropertyCount, null);
        int queueCount = pQueueFamilyPropertyCount.get(0);
        VkQueueFamilyProperties.Buffer queueProps = VkQueueFamilyProperties.calloc(queueCount);
        vkGetPhysicalDeviceQueueFamilyProperties(physicalDevice, pQueueFamilyPropertyCount, queueProps);
        memFree(pQueueFamilyPropertyCount);

        // Iterate over each queue to learn whether it supports presenting:
        IntBuffer supportsPresent = memAllocInt(queueCount);
        for (int i = 0; i < queueCount; i++) {
            supportsPresent.position(i);
            int err = vkGetPhysicalDeviceSurfaceSupportKHR(physicalDevice, i, surface, supportsPresent);
            if (err != VK_SUCCESS) {
                throw new AssertionError("Failed to physical device surface support: " + translateVulkanResult(err));
            }
        }

        // Search for a graphics and a present queue in the array of queue families, try to find one that supports both
        int graphicsQueueNodeIndex = Integer.MAX_VALUE;
        int presentQueueNodeIndex = Integer.MAX_VALUE;
        for (int i = 0; i < queueCount; i++) {
            if ((queueProps.get(i).queueFlags() & VK_QUEUE_GRAPHICS_BIT) != 0) {
                if (graphicsQueueNodeIndex == Integer.MAX_VALUE) {
                    graphicsQueueNodeIndex = i;
                }
                if (supportsPresent.get(i) == VK_TRUE) {
                    graphicsQueueNodeIndex = i;
                    presentQueueNodeIndex = i;
                    break;
                }
            }
        }
        queueProps.free();
        if (presentQueueNodeIndex == Integer.MAX_VALUE) {
            // If there's no queue that supports both present and graphics try to find a separate present queue
            for (int i = 0; i < queueCount; ++i) {
                if (supportsPresent.get(i) == VK_TRUE) {
                    presentQueueNodeIndex = i;
                    break;
                }
            }
        }
        memFree(supportsPresent);

        // Generate error if could not find both a graphics and a present queue
        if (graphicsQueueNodeIndex == Integer.MAX_VALUE) {
            throw new AssertionError("No graphics queue found");
        }
        if (presentQueueNodeIndex == Integer.MAX_VALUE) {
            throw new AssertionError("No presentation queue found");
        }
        if (graphicsQueueNodeIndex != presentQueueNodeIndex) {
            throw new AssertionError("Presentation queue != graphics queue");
        }

        // Get list of supported formats
        IntBuffer pFormatCount = memAllocInt(1);
        int err = vkGetPhysicalDeviceSurfaceFormatsKHR(physicalDevice, surface, pFormatCount, null);
        int formatCount = pFormatCount.get(0);
        if (err != VK_SUCCESS) {
            throw new AssertionError("Failed to query number of physical device surface formats: " + translateVulkanResult(err));
        }

        VkSurfaceFormatKHR.Buffer surfFormats = VkSurfaceFormatKHR.calloc(formatCount);
        err = vkGetPhysicalDeviceSurfaceFormatsKHR(physicalDevice, surface, pFormatCount, surfFormats);
        memFree(pFormatCount);
        if (err != VK_SUCCESS) {
            throw new AssertionError("Failed to query physical device surface formats: " + translateVulkanResult(err));
        }

        int colorFormat;
        if (formatCount == 1 && surfFormats.get(0).format() == VK_FORMAT_UNDEFINED) {
            colorFormat = VK_FORMAT_B8G8R8A8_UNORM;
        } else {
            colorFormat = surfFormats.get(0).format();
        }
        int colorSpace = surfFormats.get(0).colorSpace();
        surfFormats.free();

        ColorFormatAndSpace ret = new ColorFormatAndSpace();
        ret.colorFormat = colorFormat;
        ret.colorSpace = colorSpace;
        return ret;
    }

    private static long createCommandPool(VkDevice device, int queueNodeIndex) {
        VkCommandPoolCreateInfo cmdPoolInfo = VkCommandPoolCreateInfo.calloc()
                .sType(VK_STRUCTURE_TYPE_COMMAND_POOL_CREATE_INFO)
                .queueFamilyIndex(queueNodeIndex)
                .flags(VK_COMMAND_POOL_CREATE_RESET_COMMAND_BUFFER_BIT);
        LongBuffer pCmdPool = memAllocLong(1);
        int err = vkCreateCommandPool(device, cmdPoolInfo, null, pCmdPool);
        long commandPool = pCmdPool.get(0);
        cmdPoolInfo.free();
        memFree(pCmdPool);
        if (err != VK_SUCCESS) {
            throw new AssertionError("Failed to create command pool: " + translateVulkanResult(err));
        }
        return commandPool;
    }

    private static VkQueue createDeviceQueue(VkDevice device, int queueFamilyIndex) {
        PointerBuffer pQueue = memAllocPointer(1);
        vkGetDeviceQueue(device, queueFamilyIndex, 0, pQueue);
        long queue = pQueue.get(0);
        memFree(pQueue);
        return new VkQueue(queue, device);
    }

    private static void imageBarrier(VkCommandBuffer cmdbuffer, long image, int aspectMask, int oldImageLayout, int srcAccess, int newImageLayout, int dstAccess) {
        // Create an image barrier object
        VkImageMemoryBarrier.Buffer imageMemoryBarrier = VkImageMemoryBarrier.calloc(1)
                .sType(VK_STRUCTURE_TYPE_IMAGE_MEMORY_BARRIER)
                .pNext(NULL)
                .oldLayout(oldImageLayout)
                .srcAccessMask(srcAccess)
                .newLayout(newImageLayout)
                .dstAccessMask(dstAccess)
                .srcQueueFamilyIndex(VK_QUEUE_FAMILY_IGNORED)
                .dstQueueFamilyIndex(VK_QUEUE_FAMILY_IGNORED)
                .image(image);
        imageMemoryBarrier.subresourceRange()
                .aspectMask(aspectMask)
                .baseMipLevel(0)
                .levelCount(1)
                .layerCount(1);

        // Put barrier on top
        int srcStageFlags = VK_PIPELINE_STAGE_TOP_OF_PIPE_BIT;
        int destStageFlags = VK_PIPELINE_STAGE_TOP_OF_PIPE_BIT;

        // Put barrier inside setup command buffer
        vkCmdPipelineBarrier(cmdbuffer, srcStageFlags, destStageFlags, VK_FLAGS_NONE,
                null, // no memory barriers
                null, // no buffer memory barriers
                imageMemoryBarrier); // one image memory barrier
        imageMemoryBarrier.free();
    }

    private static class Swapchain {

        long swapchainHandle;
        long[] images;
        long[] imageViews;
    }

    private static Swapchain createSwapChain(VkDevice device, VkPhysicalDevice physicalDevice, long surface, long oldSwapChain, VkCommandBuffer commandBuffer, int newWidth,
            int newHeight, int colorFormat, int colorSpace) {
        int err;
        // Get physical device surface properties and formats
        VkSurfaceCapabilitiesKHR surfCaps = VkSurfaceCapabilitiesKHR.calloc();
        err = vkGetPhysicalDeviceSurfaceCapabilitiesKHR(physicalDevice, surface, surfCaps);
        if (err != VK_SUCCESS) {
            throw new AssertionError("Failed to get physical device surface capabilities: " + translateVulkanResult(err));
        }

        IntBuffer pPresentModeCount = memAllocInt(1);
        err = vkGetPhysicalDeviceSurfacePresentModesKHR(physicalDevice, surface, pPresentModeCount, null);
        int presentModeCount = pPresentModeCount.get(0);
        if (err != VK_SUCCESS) {
            throw new AssertionError("Failed to get number of physical device surface presentation modes: " + translateVulkanResult(err));
        }

        IntBuffer pPresentModes = memAllocInt(presentModeCount);
        err = vkGetPhysicalDeviceSurfacePresentModesKHR(physicalDevice, surface, pPresentModeCount, pPresentModes);
        memFree(pPresentModeCount);
        if (err != VK_SUCCESS) {
            throw new AssertionError("Failed to get physical device surface presentation modes: " + translateVulkanResult(err));
        }

        // Try to use mailbox mode. Low latency and non-tearing
        int swapchainPresentMode = VK_PRESENT_MODE_FIFO_KHR;
        for (int i = 0; i < presentModeCount; i++) {
            if (pPresentModes.get(i) == VK_PRESENT_MODE_MAILBOX_KHR) {
                swapchainPresentMode = VK_PRESENT_MODE_MAILBOX_KHR;
                break;
            }
            if ((swapchainPresentMode != VK_PRESENT_MODE_MAILBOX_KHR) && (pPresentModes.get(i) == VK_PRESENT_MODE_IMMEDIATE_KHR)) {
                swapchainPresentMode = VK_PRESENT_MODE_IMMEDIATE_KHR;
            }
        }
        memFree(pPresentModes);

        // Determine the number of images
        int desiredNumberOfSwapchainImages = surfCaps.minImageCount() + 1;
        if ((surfCaps.maxImageCount() > 0) && (desiredNumberOfSwapchainImages > surfCaps.maxImageCount())) {
            desiredNumberOfSwapchainImages = surfCaps.maxImageCount();
        }

        VkExtent2D currentExtent = surfCaps.currentExtent();
        int currentWidth = currentExtent.width();
        int currentHeight = currentExtent.height();
        if (currentWidth != -1 && currentHeight != -1) {
            width = currentWidth;
            height = currentHeight;
        } else {
            width = newWidth;
            height = newHeight;
        }

        int preTransform;
        if ((surfCaps.supportedTransforms() & VK_SURFACE_TRANSFORM_IDENTITY_BIT_KHR) != 0) {
            preTransform = VK_SURFACE_TRANSFORM_IDENTITY_BIT_KHR;
        } else {
            preTransform = surfCaps.currentTransform();
        }
        surfCaps.free();

        VkSwapchainCreateInfoKHR swapchainCI = VkSwapchainCreateInfoKHR.calloc()
                .sType(VK_STRUCTURE_TYPE_SWAPCHAIN_CREATE_INFO_KHR)
                .pNext(NULL)
                .surface(surface)
                .minImageCount(desiredNumberOfSwapchainImages)
                .imageFormat(colorFormat)
                .imageColorSpace(colorSpace)
                .imageUsage(VK_IMAGE_USAGE_COLOR_ATTACHMENT_BIT)
                .preTransform(preTransform)
                .imageArrayLayers(1)
                .imageSharingMode(VK_SHARING_MODE_EXCLUSIVE)
                .pQueueFamilyIndices(null)
                .presentMode(swapchainPresentMode)
                .oldSwapchain(oldSwapChain)
                .clipped(VK_TRUE)
                .compositeAlpha(VK_COMPOSITE_ALPHA_OPAQUE_BIT_KHR);
        swapchainCI.imageExtent()
                .width(width)
                .height(height);
        LongBuffer pSwapChain = memAllocLong(1);
        err = vkCreateSwapchainKHR(device, swapchainCI, null, pSwapChain);
        swapchainCI.free();
        long swapChain = pSwapChain.get(0);
        memFree(pSwapChain);
        if (err != VK_SUCCESS) {
            throw new AssertionError("Failed to create swap chain: " + translateVulkanResult(err));
        }

        // If we just re-created an existing swapchain, we should destroy the old swapchain at this point.
        // Note: destroying the swapchain also cleans up all its associated presentable images once the platform is done with them.
        if (oldSwapChain != VK_NULL_HANDLE) {
            vkDestroySwapchainKHR(device, oldSwapChain, null);
        }

        IntBuffer pImageCount = memAllocInt(1);
        err = vkGetSwapchainImagesKHR(device, swapChain, pImageCount, null);
        int imageCount = pImageCount.get(0);
        if (err != VK_SUCCESS) {
            throw new AssertionError("Failed to get number of swapchain images: " + translateVulkanResult(err));
        }

        LongBuffer pSwapchainImages = memAllocLong(imageCount);
        err = vkGetSwapchainImagesKHR(device, swapChain, pImageCount, pSwapchainImages);
        if (err != VK_SUCCESS) {
            throw new AssertionError("Failed to get swapchain images: " + translateVulkanResult(err));
        }
        memFree(pImageCount);

        long[] images = new long[imageCount];
        long[] imageViews = new long[imageCount];
        LongBuffer pBufferView = memAllocLong(1);
        VkImageViewCreateInfo colorAttachmentView = VkImageViewCreateInfo.calloc()
                .sType(VK_STRUCTURE_TYPE_IMAGE_VIEW_CREATE_INFO)
                .pNext(NULL)
                .format(colorFormat)
                .viewType(VK_IMAGE_VIEW_TYPE_2D)
                .flags(VK_FLAGS_NONE);
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
        for (int i = 0; i < imageCount; i++) {
            images[i] = pSwapchainImages.get(i);
            // Bring the image from an UNDEFINED state to the VK_ACCESS_COLOR_ATTACHMENT_WRITE_BIT state
            imageBarrier(commandBuffer, images[i], VK_IMAGE_ASPECT_COLOR_BIT,
                    VK_IMAGE_LAYOUT_UNDEFINED, 0,
                    VK_IMAGE_LAYOUT_COLOR_ATTACHMENT_OPTIMAL, VK_ACCESS_COLOR_ATTACHMENT_WRITE_BIT);
            colorAttachmentView.image(images[i]);
            err = vkCreateImageView(device, colorAttachmentView, null, pBufferView);
            imageViews[i] = pBufferView.get(0);
            if (err != VK_SUCCESS) {
                throw new AssertionError("Failed to create image view: " + translateVulkanResult(err));
            }
        }
        colorAttachmentView.free();
        memFree(pBufferView);
        memFree(pSwapchainImages);

        Swapchain ret = new Swapchain();
        ret.images = images;
        ret.imageViews = imageViews;
        ret.swapchainHandle = swapChain;
        return ret;
    }

    private static long[] createFramebuffers(VkDevice device, Swapchain swapchain, long renderPass, int width, int height) {
        LongBuffer attachments = memAllocLong(1);
        VkFramebufferCreateInfo fci = VkFramebufferCreateInfo.calloc()
                .sType(VK_STRUCTURE_TYPE_FRAMEBUFFER_CREATE_INFO)
                .pAttachments(attachments)
                .flags(VK_FLAGS_NONE)
                .height(height)
                .width(width)
                .layers(1)
                .pNext(NULL)
                .renderPass(renderPass);
        // Create a framebuffer for each swapchain image
        long[] framebuffers = new long[swapchain.images.length];
        LongBuffer pFramebuffer = memAllocLong(1);
        for (int i = 0; i < swapchain.images.length; i++) {
            attachments.put(0, swapchain.imageViews[i]);
            int err = vkCreateFramebuffer(device, fci, null, pFramebuffer);
            long framebuffer = pFramebuffer.get(0);
            if (err != VK_SUCCESS) {
                throw new AssertionError("Failed to create framebuffer: " + translateVulkanResult(err));
            }
            framebuffers[i] = framebuffer;
        }
        memFree(attachments);
        memFree(pFramebuffer);
        fci.free();
        return framebuffers;
    }

    private static void submitCommandBuffer(VkQueue queue, VkCommandBuffer commandBuffer) {
        if (commandBuffer == null || commandBuffer.address() == NULL) {
            return;
        }
        VkSubmitInfo submitInfo = VkSubmitInfo.calloc()
                .sType(VK_STRUCTURE_TYPE_SUBMIT_INFO);
        PointerBuffer pCommandBuffers = memAllocPointer(1)
                .put(commandBuffer)
                .flip();
        submitInfo.pCommandBuffers(pCommandBuffers);
        int err = vkQueueSubmit(queue, submitInfo, VK_NULL_HANDLE);
        memFree(pCommandBuffers);
        submitInfo.free();
        if (err != VK_SUCCESS) {
            throw new AssertionError("Failed to submit command buffer: " + translateVulkanResult(err));
        }
    }

    private static class Vertices {

        Buffer buffer;
        VertexInputs vertexArrayInput;
    }

    private static Vertices createVertices(VkPhysicalDeviceMemoryProperties deviceMemoryProperties, VkDevice device) {
        //TODO: these should not be globals.        
        VK10BufferFactory.deviceMemoryProperties = deviceMemoryProperties;
        ObjectFactoryManager.getInstance().initVK10();

        final Buffer out = new BufferCreateInfo()
                .withSize(Float.BYTES * 6)
                .allocate();

        out.map();
        final FloatBuffer fb = out.getMappedBuffer().asFloatBuffer();

        // The triangle will showup upside-down, because Vulkan does not do proper viewport transformation to
        // account for inverted Y axis between the window coordinate system and clip space/NDC
        fb.put(-0.5f).put(-0.5f);
        fb.put(0.5f).put(-0.5f);
        fb.put(0.0f).put(0.5f);

        out.unmap();

        Vertices ret = new Vertices();
        ret.vertexArrayInput = new VertexInputs()
                .withAttribute(new VertexAttribute()
                        .withBuffer(out)
                        .withStride(2 * 4)
                        .withFormat(VertexAttributeFormat.XY_32F)
                );
        ret.buffer = out;

        return ret;
    }

    private static VkCommandBuffer[] createRenderCommandBuffers(VkDevice device, long commandPool, long[] framebuffers, long renderPass, int width, int height,
            long pipeline, Buffer verticesBuf) {
        
        VkCommandBuffer[] renderCommandBuffers = VKThreadConstants.getInstance().device.getFirstGraphicsCommandPool().newCommandBuffers(framebuffers.length);
        int err;

        // Create the command buffer begin structure
        VkCommandBufferBeginInfo cmdBufInfo = VkCommandBufferBeginInfo.calloc()
                .sType(VK_STRUCTURE_TYPE_COMMAND_BUFFER_BEGIN_INFO)
                .pNext(NULL);

        // Specify clear color (cornflower blue)
        VkClearValue.Buffer clearValues = VkClearValue.calloc(1);
        clearValues.color()
                .float32(0, 100 / 255.0f)
                .float32(1, 149 / 255.0f)
                .float32(2, 237 / 255.0f)
                .float32(3, 1.0f);

        // Specify everything to begin a render pass
        VkRenderPassBeginInfo renderPassBeginInfo = VkRenderPassBeginInfo.calloc()
                .sType(VK_STRUCTURE_TYPE_RENDER_PASS_BEGIN_INFO)
                .pNext(NULL)
                .renderPass(renderPass)
                .pClearValues(clearValues);
        VkRect2D renderArea = renderPassBeginInfo.renderArea();
        renderArea.offset().set(0, 0);
        renderArea.extent().set(width, height);

        for (int i = 0; i < renderCommandBuffers.length; ++i) {
            // Set target frame buffer
            renderPassBeginInfo.framebuffer(framebuffers[i]);

            err = vkBeginCommandBuffer(renderCommandBuffers[i], cmdBufInfo);
            if (err != VK_SUCCESS) {
                throw new AssertionError("Failed to begin render command buffer: " + translateVulkanResult(err));
            }

            vkCmdBeginRenderPass(renderCommandBuffers[i], renderPassBeginInfo, VK_SUBPASS_CONTENTS_INLINE);

            // Update dynamic viewport state
            VkViewport.Buffer viewport = VkViewport.calloc(1)
                    .height(height)
                    .width(width)
                    .minDepth(0.0f)
                    .maxDepth(1.0f);
            vkCmdSetViewport(renderCommandBuffers[i], 0, viewport);
            viewport.free();

            // Update dynamic scissor state
            VkRect2D.Buffer scissor = VkRect2D.calloc(1);
            scissor.extent().set(width, height);
            scissor.offset().set(0, 0);
            vkCmdSetScissor(renderCommandBuffers[i], 0, scissor);
            scissor.free();

            // Bind the rendering pipeline (including the shaders)
            vkCmdBindPipeline(renderCommandBuffers[i], VK_PIPELINE_BIND_POINT_GRAPHICS, pipeline);

            // Bind triangle vertices
            LongBuffer offsets = memAllocLong(1);
            offsets.put(0, 0L);
            LongBuffer pBuffers = memAllocLong(1);
            pBuffers.put(0, ((VK10Buffer) verticesBuf).id);
            vkCmdBindVertexBuffers(renderCommandBuffers[i], 0, pBuffers, offsets);
            memFree(pBuffers);
            memFree(offsets);

            // Draw triangle
            vkCmdDraw(renderCommandBuffers[i], 3, 1, 0, 0);

            vkCmdEndRenderPass(renderCommandBuffers[i]);

            // Add a present memory barrier to the end of the command buffer
            // This will transform the frame buffer color attachment to a
            // new layout for presenting it to the windowing system integration 
            VkImageMemoryBarrier.Buffer prePresentBarrier = createPrePresentBarrier(swapchain.images[i]);
            vkCmdPipelineBarrier(renderCommandBuffers[i],
                    VK_PIPELINE_STAGE_COLOR_ATTACHMENT_OUTPUT_BIT,
                    VK_PIPELINE_STAGE_TOP_OF_PIPE_BIT,
                    VK_FLAGS_NONE,
                    null, // No memory barriers
                    null, // No buffer memory barriers
                    prePresentBarrier); // One image memory barrier
            prePresentBarrier.free();

            err = vkEndCommandBuffer(renderCommandBuffers[i]);
            if (err != VK_SUCCESS) {
                throw new AssertionError("Failed to begin render command buffer: " + translateVulkanResult(err));
            }
        }
        renderPassBeginInfo.free();
        clearValues.free();
        cmdBufInfo.free();
        return renderCommandBuffers;
    }

    private static VkImageMemoryBarrier.Buffer createPrePresentBarrier(long presentImage) {
        VkImageMemoryBarrier.Buffer imageMemoryBarrier = VkImageMemoryBarrier.calloc(1)
                .sType(VK_STRUCTURE_TYPE_IMAGE_MEMORY_BARRIER)
                .pNext(NULL)
                .srcAccessMask(VK_ACCESS_COLOR_ATTACHMENT_WRITE_BIT)
                .dstAccessMask(0)
                .oldLayout(VK_IMAGE_LAYOUT_COLOR_ATTACHMENT_OPTIMAL)
                .newLayout(VK_IMAGE_LAYOUT_PRESENT_SRC_KHR)
                .srcQueueFamilyIndex(VK_QUEUE_FAMILY_IGNORED)
                .dstQueueFamilyIndex(VK_QUEUE_FAMILY_IGNORED);
        imageMemoryBarrier.subresourceRange()
                .aspectMask(VK_IMAGE_ASPECT_COLOR_BIT)
                .baseMipLevel(0)
                .levelCount(1)
                .baseArrayLayer(0)
                .layerCount(1);
        imageMemoryBarrier.image(presentImage);
        return imageMemoryBarrier;
    }

    private static VkImageMemoryBarrier.Buffer createPostPresentBarrier(long presentImage) {
        VkImageMemoryBarrier.Buffer imageMemoryBarrier = VkImageMemoryBarrier.calloc(1)
                .sType(VK_STRUCTURE_TYPE_IMAGE_MEMORY_BARRIER)
                .pNext(NULL)
                .srcAccessMask(0)
                .dstAccessMask(VK_ACCESS_COLOR_ATTACHMENT_WRITE_BIT)
                .oldLayout(VK_IMAGE_LAYOUT_PRESENT_SRC_KHR)
                .newLayout(VK_IMAGE_LAYOUT_COLOR_ATTACHMENT_OPTIMAL)
                .srcQueueFamilyIndex(VK_QUEUE_FAMILY_IGNORED)
                .dstQueueFamilyIndex(VK_QUEUE_FAMILY_IGNORED);
        imageMemoryBarrier.subresourceRange()
                .aspectMask(VK_IMAGE_ASPECT_COLOR_BIT)
                .baseMipLevel(0)
                .levelCount(1)
                .baseArrayLayer(0)
                .layerCount(1);
        imageMemoryBarrier.image(presentImage);
        return imageMemoryBarrier;
    }

    private static void submitPostPresentBarrier(long image, VkCommandBuffer commandBuffer, VkQueue queue) {
        VkCommandBufferBeginInfo cmdBufInfo = VkCommandBufferBeginInfo.calloc()
                .sType(VK_STRUCTURE_TYPE_COMMAND_BUFFER_BEGIN_INFO)
                .pNext(NULL);
        int err = vkBeginCommandBuffer(commandBuffer, cmdBufInfo);
        cmdBufInfo.free();
        if (err != VK_SUCCESS) {
            throw new AssertionError("Failed to begin command buffer: " + translateVulkanResult(err));
        }

        VkImageMemoryBarrier.Buffer postPresentBarrier = createPostPresentBarrier(image);
        vkCmdPipelineBarrier(
                commandBuffer,
                VK_PIPELINE_STAGE_ALL_COMMANDS_BIT,
                VK_PIPELINE_STAGE_TOP_OF_PIPE_BIT,
                VK_FLAGS_NONE,
                null, // No memory barriers,
                null, // No buffer barriers,
                postPresentBarrier); // one image barrier
        postPresentBarrier.free();

        err = vkEndCommandBuffer(commandBuffer);
        if (err != VK_SUCCESS) {
            throw new AssertionError("Failed to wait for idle queue: " + translateVulkanResult(err));
        }

        // Submit the command buffer
        submitCommandBuffer(queue, commandBuffer);
    }

    /*
     * All resources that must be reallocated on window resize.
     */
    private static Swapchain swapchain;
    private static long[] framebuffers;
    private static int width, height;
    private static VkCommandBuffer[] renderCommandBuffers;

    @Test
    public void testTriangleDemo() {
        final String[] args = {};

        try {
            main(args);
        } catch (IOException ex) {
            throw new RuntimeException(ex);
        }
    }

    public static void main(String[] args) throws IOException {
        if (!glfwInit()) {
            throw new RuntimeException("Failed to initialize GLFW");
        }
        if (!glfwVulkanSupported()) {
            throw new AssertionError("GLFW failed to find the Vulkan loader");
        }

        /* Look for instance extensions */
        PointerBuffer requiredExtensions = glfwGetRequiredInstanceExtensions();
        if (requiredExtensions == null) {
            throw new AssertionError("Failed to find list of required Vulkan extensions");
        }

        // Create the Vulkan instance
        final VkInstance instance = VKGlobalConstants.getInstance().instance;
        final VkPhysicalDevice physicalDevice = VKThreadConstants.getInstance().device.physicalDevice;        
        //final int graphicsQueueIndex = VKThreadConstants.getInstance().physicalDevice.getFirstGraphicsQueue().queueFamilyIndex;
        //final VKThreadConstantsOld.DeviceInfo deviceAndGraphicsQueueFamily = VKThreadConstantsOld.createDevice(physicalDevice, graphicsQueueIndex, Arrays.asList(VK_KHR_SWAPCHAIN_EXTENSION_NAME));
        final VkDevice device = VKThreadConstants.getInstance().device.vkDevice;

        // bind the device context to the current thread...
        //VKThreadConstantsOld.create(device);

        final int queueFamilyIndex = VKThreadConstants.getInstance().device.getFirstComputeQueue().queueFamilyIndex;
        final VkPhysicalDeviceMemoryProperties memoryProperties = VKThreadConstants.getInstance().device.memoryProperties;

        // Create GLFW window
        glfwDefaultWindowHints();
        glfwWindowHint(GLFW_CLIENT_API, GLFW_NO_API);
        glfwWindowHint(GLFW_VISIBLE, GLFW_FALSE);
        long window = glfwCreateWindow(800, 600, "GLFW Vulkan Demo", NULL, NULL);
        GLFWKeyCallback keyCallback;
        glfwSetKeyCallback(window, keyCallback = new GLFWKeyCallback() {
            public void invoke(long window, int key, int scancode, int action, int mods) {
                if (action != GLFW_RELEASE) {
                    return;
                }
                if (key == GLFW_KEY_ESCAPE) {
                    glfwSetWindowShouldClose(window, true);
                }
            }
        });
        LongBuffer pSurface = memAllocLong(1);
        int err = glfwCreateWindowSurface(instance, window, null, pSurface);
        final long surface = pSurface.get(0);
        if (err != VK_SUCCESS) {
            throw new AssertionError("Failed to create surface: " + translateVulkanResult(err));
        }

        // Create static Vulkan resources
        final ColorFormatAndSpace colorFormatAndSpace = getColorFormatAndSpace(physicalDevice, surface);
        final CommandPool commandPool = VKThreadConstants.getInstance().device.getFirstGraphicsCommandPool();
        final VkCommandBuffer setupCommandBuffer = commandPool.newCommandBuffer();
        final VkCommandBuffer postPresentCommandBuffer = commandPool.newCommandBuffer();
        final VkQueue queue = createDeviceQueue(device, queueFamilyIndex);        
        final Vertices vertices = createVertices(memoryProperties, device);

        final RasterPipeline pipeline = new RasterPipelineCreateInfo()
                .withVertexInputs(vertices.vertexArrayInput)
                .withShaderStage(new ShaderCreateInfo()
                        .withType(ShaderType.VERTEX)
                        .withSource("org/lwjgl/demo/vulkan/triangle.vert.spv")
                        .allocate())
                .withShaderStage(new ShaderCreateInfo()
                        .withType(ShaderType.FRAGMENT)
                        .withSource("org/lwjgl/demo/vulkan/triangle.frag.spv")
                        .allocate())
                .allocate();

        final class SwapchainRecreator {

            boolean mustRecreate = true;

            void recreate() {
                final VkDevice device = VKThreadConstants.getInstance().device.vkDevice;

                // Begin the setup command buffer (the one we will use for swapchain/framebuffer creation)
                VkCommandBufferBeginInfo cmdBufInfo = VkCommandBufferBeginInfo.calloc()
                        .sType(VK_STRUCTURE_TYPE_COMMAND_BUFFER_BEGIN_INFO)
                        .pNext(NULL);
                int err = vkBeginCommandBuffer(setupCommandBuffer, cmdBufInfo);
                cmdBufInfo.free();
                if (err != VK_SUCCESS) {
                    throw new AssertionError("Failed to begin setup command buffer: " + translateVulkanResult(err));
                }
                long oldChain = swapchain != null ? swapchain.swapchainHandle : VK_NULL_HANDLE;
                // Create the swapchain (this will also add a memory barrier to initialize the framebuffer images)
                swapchain = createSwapChain(device, physicalDevice, surface, oldChain, setupCommandBuffer,
                        width, height, colorFormatAndSpace.colorFormat, colorFormatAndSpace.colorSpace);
                err = vkEndCommandBuffer(setupCommandBuffer);
                if (err != VK_SUCCESS) {
                    throw new AssertionError("Failed to end setup command buffer: " + translateVulkanResult(err));
                }
                submitCommandBuffer(queue, setupCommandBuffer);
                vkQueueWaitIdle(queue);

                if (framebuffers != null) {
                    for (int i = 0; i < framebuffers.length; i++) {
                        vkDestroyFramebuffer(device, framebuffers[i], null);
                    }
                }
                
                final VK10RenderPass renderPass = VKGlobalConstants.getInstance().getRenderPass(colorFormatAndSpace.colorFormat);
                
                framebuffers = createFramebuffers(device, swapchain, renderPass.id, width, height);
                // Create render command buffers
                if (renderCommandBuffers != null) {
                    vkResetCommandPool(device, commandPool.id, VK_FLAGS_NONE);
                }
                renderCommandBuffers = createRenderCommandBuffers(device, commandPool.id, framebuffers, renderPass.id, width, height, ((VK10RasterPipeline) pipeline).pipeline,
                        vertices.buffer);

                mustRecreate = false;
            }
        }
        final SwapchainRecreator swapchainRecreator = new SwapchainRecreator();

        // Handle canvas resize
        GLFWWindowSizeCallback windowSizeCallback = new GLFWWindowSizeCallback() {
            public void invoke(long window, int width, int height) {
                if (width <= 0 || height <= 0) {
                    return;
                }
                TriangleDemoGloop.width = width;
                TriangleDemoGloop.height = height;
                swapchainRecreator.mustRecreate = true;
            }
        };
        glfwSetWindowSizeCallback(window, windowSizeCallback);
        glfwShowWindow(window);

        // Pre-allocate everything needed in the render loop
        IntBuffer pImageIndex = memAllocInt(1);
        int currentBuffer = 0;
        PointerBuffer pCommandBuffers = memAllocPointer(1);
        LongBuffer pSwapchains = memAllocLong(1);
        LongBuffer pImageAcquiredSemaphore = memAllocLong(1);
        LongBuffer pRenderCompleteSemaphore = memAllocLong(1);

        // Info struct to create a semaphore
        VkSemaphoreCreateInfo semaphoreCreateInfo = VkSemaphoreCreateInfo.calloc()
                .sType(VK_STRUCTURE_TYPE_SEMAPHORE_CREATE_INFO)
                .pNext(NULL)
                .flags(VK_FLAGS_NONE);

        // Info struct to submit a command buffer which will wait on the semaphore
        IntBuffer pWaitDstStageMask = memAllocInt(1);
        pWaitDstStageMask.put(0, VK_PIPELINE_STAGE_COLOR_ATTACHMENT_OUTPUT_BIT);
        VkSubmitInfo submitInfo = VkSubmitInfo.calloc()
                .sType(VK_STRUCTURE_TYPE_SUBMIT_INFO)
                .pNext(NULL)
                .waitSemaphoreCount(pImageAcquiredSemaphore.remaining())
                .pWaitSemaphores(pImageAcquiredSemaphore)
                .pWaitDstStageMask(pWaitDstStageMask)
                .pCommandBuffers(pCommandBuffers)
                .pSignalSemaphores(pRenderCompleteSemaphore);

        // Info struct to present the current swapchain image to the display
        VkPresentInfoKHR presentInfo = VkPresentInfoKHR.calloc()
                .sType(VK_STRUCTURE_TYPE_PRESENT_INFO_KHR)
                .pNext(NULL)
                .pWaitSemaphores(pRenderCompleteSemaphore)
                .swapchainCount(pSwapchains.remaining())
                .pSwapchains(pSwapchains)
                .pImageIndices(pImageIndex)
                .pResults(null);

        // The render loop
        while (!glfwWindowShouldClose(window)) {
            // Handle window messages. Resize events happen exactly here.
            // So it is safe to use the new swapchain images and framebuffers afterwards.
            glfwPollEvents();
            if (swapchainRecreator.mustRecreate) {
                swapchainRecreator.recreate();
            }

            // Create a semaphore to wait for the swapchain to acquire the next image
            err = vkCreateSemaphore(device, semaphoreCreateInfo, null, pImageAcquiredSemaphore);
            if (err != VK_SUCCESS) {
                throw new AssertionError("Failed to create image acquired semaphore: " + translateVulkanResult(err));
            }

            // Create a semaphore to wait for the render to complete, before presenting
            err = vkCreateSemaphore(device, semaphoreCreateInfo, null, pRenderCompleteSemaphore);
            if (err != VK_SUCCESS) {
                throw new AssertionError("Failed to create render complete semaphore: " + translateVulkanResult(err));
            }

            // Get next image from the swap chain (back/front buffer).
            // This will setup the imageAquiredSemaphore to be signalled when the operation is complete
            err = vkAcquireNextImageKHR(device, swapchain.swapchainHandle, UINT64_MAX, pImageAcquiredSemaphore.get(0), VK_NULL_HANDLE, pImageIndex);
            currentBuffer = pImageIndex.get(0);
            if (err != VK_SUCCESS) {
                throw new AssertionError("Failed to acquire next swapchain image: " + translateVulkanResult(err));
            }

            // Select the command buffer for the current framebuffer image/attachment
            pCommandBuffers.put(0, renderCommandBuffers[currentBuffer]);

            // Submit to the graphics queue
            err = vkQueueSubmit(queue, submitInfo, VK_NULL_HANDLE);
            if (err != VK_SUCCESS) {
                throw new AssertionError("Failed to submit render queue: " + translateVulkanResult(err));
            }

            // Present the current buffer to the swap chain
            // This will display the image
            pSwapchains.put(0, swapchain.swapchainHandle);
            err = vkQueuePresentKHR(queue, presentInfo);
            if (err != VK_SUCCESS) {
                throw new AssertionError("Failed to present the swapchain image: " + translateVulkanResult(err));
            }
            // Create and submit post present barrier
            vkQueueWaitIdle(queue);

            // Destroy this semaphore (we will create a new one in the next frame)
            vkDestroySemaphore(device, pImageAcquiredSemaphore.get(0), null);
            vkDestroySemaphore(device, pRenderCompleteSemaphore.get(0), null);
            submitPostPresentBarrier(swapchain.images[currentBuffer], postPresentCommandBuffer, queue);
        }
        presentInfo.free();
        memFree(pWaitDstStageMask);
        submitInfo.free();
        memFree(pImageAcquiredSemaphore);
        memFree(pRenderCompleteSemaphore);
        semaphoreCreateInfo.free();
        memFree(pSwapchains);
        memFree(pCommandBuffers);

        windowSizeCallback.free();
        keyCallback.free();
        glfwDestroyWindow(window);
        glfwTerminate();

        // We don't bother disposing of all Vulkan resources.
        // Let the OS process manager take care of it.
    }

}
