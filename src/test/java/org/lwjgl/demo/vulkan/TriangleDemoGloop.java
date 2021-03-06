/*
 * Copyright LWJGL. All rights reserved.
 * License terms: http://lwjgl.org/license.php
 */
package org.lwjgl.demo.vulkan;

import com.longlinkislong.gloop2.Buffer;
import com.longlinkislong.gloop2.BufferCreateInfo;
import com.longlinkislong.gloop2.Framebuffer;
import com.longlinkislong.gloop2.ObjectFactoryManager;
import com.longlinkislong.gloop2.RasterPipeline;
import com.longlinkislong.gloop2.RasterPipelineCreateInfo;
import com.longlinkislong.gloop2.ShaderCreateInfo;
import com.longlinkislong.gloop2.ShaderType;
import com.longlinkislong.gloop2.VertexInputs;
import com.longlinkislong.gloop2.VertexAttribute;
import com.longlinkislong.gloop2.VertexAttributeFormat;
import com.longlinkislong.gloop2.vkimpl.CommandDependency;
import com.longlinkislong.gloop2.vkimpl.CommandPool;
import com.longlinkislong.gloop2.vkimpl.CommandQueue;
import com.longlinkislong.gloop2.vkimpl.KHRSurface;
import com.longlinkislong.gloop2.vkimpl.KSwapchain;
import com.longlinkislong.gloop2.vkimpl.VK10Buffer;
import com.longlinkislong.gloop2.vkimpl.VK10Framebuffer;
import com.longlinkislong.gloop2.vkimpl.VK10RasterPipeline;
import com.longlinkislong.gloop2.vkimpl.VK10RenderPass;
import com.longlinkislong.gloop2.vkimpl.VKGLFWWindow;
import com.longlinkislong.gloop2.vkimpl.VKGlobalConstants;

import static org.lwjgl.system.MemoryUtil.*;
import static org.lwjgl.vulkan.KHRSwapchain.*;
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
import org.lwjgl.glfw.GLFWWindowSizeCallback;
import org.lwjgl.system.MemoryStack;
import org.lwjgl.vulkan.VK10;
import org.lwjgl.vulkan.VkClearValue;
import org.lwjgl.vulkan.VkCommandBuffer;
import org.lwjgl.vulkan.VkCommandBufferBeginInfo;
import org.lwjgl.vulkan.VkDevice;
import org.lwjgl.vulkan.VkImageMemoryBarrier;
import org.lwjgl.vulkan.VkPresentInfoKHR;
import org.lwjgl.vulkan.VkRect2D;
import org.lwjgl.vulkan.VkRenderPassBeginInfo;
import org.lwjgl.vulkan.VkSemaphoreCreateInfo;
import org.lwjgl.vulkan.VkSubmitInfo;
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
        ObjectFactoryManager.getInstance().initVK10();
    }

    /**
     * Remove if added to spec.
     */
    private static final int VK_FLAGS_NONE = 0;

    /**
     * This is just -1L, but it is nicer as a symbolic constant.
     */
    private static final long UINT64_MAX = 0xFFFFFFFFFFFFFFFFL;

    private static class Vertices {

        Buffer buffer;
        VertexInputs vertexArrayInput;
    }

    private static Vertices createVertices() {
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
                        .withStride(2 * 4)
                        .withFormat(VertexAttributeFormat.XY_32F)
                );
        ret.buffer = out;

        return ret;
    }

    private static VkCommandBuffer[] createRenderCommandBuffers(Framebuffer[] framebuffers, long renderPass, int width, int height,
            long pipeline, Buffer verticesBuf) {

        VkCommandBuffer[] renderCommandBuffers = VKGlobalConstants.getInstance().selectedDevice.getFirstGraphicsCommandPool().newCommandBuffers(framebuffers.length);
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
            renderPassBeginInfo.framebuffer(((VK10Framebuffer) framebuffers[i]).framebuffer);

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
            VkImageMemoryBarrier.Buffer prePresentBarrier = createPrePresentBarrier(swapchain.renderTextures[i].image);
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

    private static void submitPostPresentBarrier(long image, VkCommandBuffer commandBuffer, CommandQueue queue) {
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
        queue.submit(CommandQueue.NO_DEPENDENCIES, CommandQueue.NO_SIGNALS, commandBuffer);
    }

    /*
     * All resources that must be reallocated on window resize.
     */
    private static KSwapchain swapchain;
    //private static Framebuffer[] framebuffers;
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

        // Create the Vulkan instance        
        final VkDevice device = VKGlobalConstants.getInstance().selectedDevice.vkDevice;
        final VKGLFWWindow window = new VKGLFWWindow("Triangle Test", 640, 480);

        // Create static Vulkan resources
        final KHRSurface.Format colorFormatAndSpace = window.surface.supportedFormats.get(0);
        final CommandPool commandPool = VKGlobalConstants.getInstance().selectedDevice.getFirstGraphicsCommandPool();
        final VkCommandBuffer postPresentCommandBuffer = commandPool.newCommandBuffer();
        final CommandQueue queue = VKGlobalConstants.getInstance().selectedDevice.getFirstGraphicsFamily().getQueue();
        final Vertices vertices = createVertices();

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
                final VkDevice device = VKGlobalConstants.getInstance().selectedDevice.vkDevice;

                // Create the swapchain (this will also add a memory barrier to initialize the framebuffer images)
                swapchain = new KSwapchain(window.surface, swapchain);

                final VK10RenderPass renderPass = VKGlobalConstants.getInstance().getRenderPass(colorFormatAndSpace.colorFormat);

                // Create render command buffers
                if (renderCommandBuffers != null) {
                    vkResetCommandPool(device, commandPool.id, VK_FLAGS_NONE);
                }
                renderCommandBuffers = createRenderCommandBuffers(swapchain.framebuffers, renderPass.id, window.surface.width, window.surface.height, ((VK10RasterPipeline) pipeline).pipeline,
                        vertices.buffer);

                mustRecreate = false;
            }
        }
        final SwapchainRecreator swapchainRecreator = new SwapchainRecreator();

        // Handle canvas resize
        GLFWWindowSizeCallback windowSizeCallback = new GLFWWindowSizeCallback() {
            @Override
            public void invoke(long w, int width, int height) {
                if (width <= 0 || height <= 0) {
                    return;
                }

                //TODO: this is MESSY                
                window.surface.width = width;
                window.surface.height = height;
                swapchainRecreator.mustRecreate = true;
            }
        };
        glfwSetWindowSizeCallback(window.window, windowSizeCallback);
        window.setVisible(true);

        // Pre-allocate everything needed in the render loop
        PointerBuffer pCommandBuffers = memAllocPointer(1);

        final long imageAcquiredSemaphore;
        try (MemoryStack stack = MemoryStack.stackPush()) {
            final VkSemaphoreCreateInfo semaphoreCreateInfo = VkSemaphoreCreateInfo.callocStack(stack)
                    .sType(VK10.VK_STRUCTURE_TYPE_SEMAPHORE_CREATE_INFO);
            final LongBuffer pImageAcquiredSemaphore = stack.callocLong(1);
            final int err = VK10.vkCreateSemaphore(device, semaphoreCreateInfo, null, pImageAcquiredSemaphore);

            if (err != VK10.VK_SUCCESS) {
                throw new AssertionError("Failed to create image acquired semaphore: " + translateVulkanResult(err));
            }

            imageAcquiredSemaphore = pImageAcquiredSemaphore.get(0);
        }

        final long renderCompleteSemaphore;
        try (MemoryStack stack = MemoryStack.stackPush()) {
            final VkSemaphoreCreateInfo semaphoreCreateInfo = VkSemaphoreCreateInfo.callocStack(stack)
                    .sType(VK10.VK_STRUCTURE_TYPE_SEMAPHORE_CREATE_INFO);
            final LongBuffer pRenderCompleteSemaphore = stack.callocLong(1);
            final int err = VK10.vkCreateSemaphore(device, semaphoreCreateInfo, null, pRenderCompleteSemaphore);

            if (err != VK10.VK_SUCCESS) {
                throw new AssertionError("Failed to create render complete semaphore: " + translateVulkanResult(err));
            }

            renderCompleteSemaphore = pRenderCompleteSemaphore.get(0);
        }        

        // The render loop
        while (!glfwWindowShouldClose(window.window)) {
            // Handle window messages. Resize events happen exactly here.
            // So it is safe to use the new swapchain images and framebuffers afterwards.
            glfwPollEvents();
            if (swapchainRecreator.mustRecreate) {
                swapchainRecreator.recreate();
            }

            // Get next image from the swap chain (back/front buffer).
            // This will setup the imageAquiredSemaphore to be signalled when the operation is complete
            final int currentBuffer;
            try (MemoryStack stack = MemoryStack.stackPush()) {
                final IntBuffer pImageIndex = stack.callocInt(1);
                final int err = vkAcquireNextImageKHR(device, swapchain.swapchain, UINT64_MAX, imageAcquiredSemaphore, VK_NULL_HANDLE, pImageIndex);

                if (err != VK_SUCCESS) {
                    throw new AssertionError("Failed to acquire next swapchain image: " + translateVulkanResult(err));
                }
                
                currentBuffer = pImageIndex.get(0);
            }                                    

            // Select the command buffer for the current framebuffer image/attachment
            pCommandBuffers.put(0, renderCommandBuffers[currentBuffer]);

            // Submit to the graphics queue
            final CommandDependency[] deps = {new CommandDependency(VK10.VK_PIPELINE_STAGE_COLOR_ATTACHMENT_OUTPUT_BIT, imageAcquiredSemaphore)};
            final long[] signals = {renderCompleteSemaphore};

            queue.submit(deps, signals, renderCommandBuffers);

            // Present the current buffer to the swap chain
            // This will display the image
            try (MemoryStack stack = MemoryStack.stackPush()) {
                final LongBuffer pSwapchain = stack.longs(swapchain.swapchain);
                final LongBuffer pRenderCompleteSemaphore = stack.longs(renderCompleteSemaphore);
                final IntBuffer pImageIndex = stack.ints(currentBuffer);

                final VkPresentInfoKHR presentInfo = VkPresentInfoKHR.callocStack(stack)
                        .sType(VK_STRUCTURE_TYPE_PRESENT_INFO_KHR)
                        .pWaitSemaphores(pRenderCompleteSemaphore)
                        .swapchainCount(1)
                        .pSwapchains(pSwapchain)
                        .pImageIndices(pImageIndex)
                        .pResults(null);

                final int err = vkQueuePresentKHR(queue.vkQueue, presentInfo);
                if (err != VK_SUCCESS) {
                    throw new AssertionError("Failed to present the SwapChain image: " + translateVulkanResult(err));
                }
            }

            // Create and submit post present barrier
            queue.waitIdle();

            submitPostPresentBarrier(swapchain.renderTextures[currentBuffer].image, postPresentCommandBuffer, queue);
        }
                  
        memFree(pCommandBuffers);
        windowSizeCallback.free();

        // We don't bother disposing of all Vulkan resources.
        // Let the OS process manager take care of it.
    }

}
