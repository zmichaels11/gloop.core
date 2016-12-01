/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.longlinkislong.gloop2.vkimpl;

import java.nio.IntBuffer;
import java.util.ArrayDeque;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Queue;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import static org.lwjgl.demo.vulkan.VKUtil.translateVulkanResult;
import org.lwjgl.system.MemoryStack;
import static org.lwjgl.vulkan.KHRSurface.VK_PRESENT_MODE_FIFO_KHR;
import static org.lwjgl.vulkan.KHRSurface.VK_PRESENT_MODE_IMMEDIATE_KHR;
import static org.lwjgl.vulkan.KHRSurface.VK_PRESENT_MODE_MAILBOX_KHR;
import static org.lwjgl.vulkan.KHRSurface.vkGetPhysicalDeviceSurfaceCapabilitiesKHR;
import static org.lwjgl.vulkan.KHRSurface.vkGetPhysicalDeviceSurfaceFormatsKHR;
import static org.lwjgl.vulkan.KHRSurface.vkGetPhysicalDeviceSurfacePresentModesKHR;
import org.lwjgl.vulkan.VK10;
import org.lwjgl.vulkan.VkPhysicalDevice;
import org.lwjgl.vulkan.VkSurfaceCapabilitiesKHR;
import org.lwjgl.vulkan.VkSurfaceFormatKHR;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 * @author zmichaels
 */
public final class KHRSurface {

    public static final List<Integer> PREFERRED_PRESENTATION_MODE = Arrays.asList(VK_PRESENT_MODE_MAILBOX_KHR, VK_PRESENT_MODE_IMMEDIATE_KHR, VK_PRESENT_MODE_FIFO_KHR);

    private static final Logger LOGGER = LoggerFactory.getLogger(KHRSurface.class);

    public final long surface;
    //TODO: this needs to be refreshed every time the swapchain is lost? (or is it the surface that is lost)
    public final VkSurfaceCapabilitiesKHR capabilities;
    public final int presentationMode;
    public final List<Format> supportedFormats;
    public int width;
    public int height;

    public KHRSurface(final long surface) {
        this.surface = surface;
        this.capabilities = this.getCapabilities();
        this.presentationMode = this.selectPresentationMode();
        this.supportedFormats = this.listSurfaceFormats();
    }

    private List<Format> listSurfaceFormats() {
        final VkPhysicalDevice physicalDevice = VKGlobalConstants.getInstance().selectedDevice.physicalDevice;
        final int formatCount;

        try (MemoryStack stack = MemoryStack.stackPush()) {
            final IntBuffer pFormatCount = stack.callocInt(1);
            final int err = vkGetPhysicalDeviceSurfaceFormatsKHR(physicalDevice, surface, pFormatCount, null);

            if (err != VK10.VK_SUCCESS) {
                throw new AssertionError("Failed to query number of physical device surface format count: " + translateVulkanResult(err));
            }

            formatCount = pFormatCount.get(0);
        }

        try (MemoryStack stack = MemoryStack.stackPush()) {
            final VkSurfaceFormatKHR.Buffer formats = VkSurfaceFormatKHR.callocStack(formatCount, stack);
            final IntBuffer pFormatCount = stack.ints(formatCount);
            final int err = vkGetPhysicalDeviceSurfaceFormatsKHR(physicalDevice, surface, pFormatCount, formats);

            if (err != VK10.VK_SUCCESS) {
                throw new AssertionError("Failed to query physical device surface formats: " + translateVulkanResult(err));
            }

            return Collections.unmodifiableList(
                    IntStream.range(0, formatCount)
                            .mapToObj(formats::get)
                            .map(fmt -> new Format(fmt.format(), fmt.colorSpace()))
                            .collect(Collectors.toList()));
        }
    }

    private int selectPresentationMode() {
        final VkPhysicalDevice physicalDevice = VKGlobalConstants.getInstance().selectedDevice.physicalDevice;
        final int presentModeCount;

        try (MemoryStack stack = MemoryStack.stackPush()) {
            final IntBuffer pPresentModeCount = stack.callocInt(1);
            final int err = vkGetPhysicalDeviceSurfacePresentModesKHR(physicalDevice, surface, pPresentModeCount, null);

            if (err != VK10.VK_SUCCESS) {
                throw new AssertionError("Failed to get number of physical device surface presentation modes: " + translateVulkanResult(err));
            }

            presentModeCount = pPresentModeCount.get(0);
        }

        final Set<Integer> presentationModes;

        try (MemoryStack stack = MemoryStack.stackPush()) {
            final IntBuffer pPresentModes = stack.callocInt(presentModeCount);
            final IntBuffer pPresentModeCount = stack.ints(presentModeCount);
            final int err = vkGetPhysicalDeviceSurfacePresentModesKHR(physicalDevice, surface, pPresentModeCount, pPresentModes);

            if (err != VK10.VK_SUCCESS) {
                throw new AssertionError("Failed to get physical device presentation modes: " + translateVulkanResult(err));
            }

            presentationModes = IntStream.range(0, presentModeCount)
                    .map(pPresentModes::get)
                    .boxed()
                    .collect(Collectors.toSet());
        }

        final Queue<Integer> preferredPresentationModes = new ArrayDeque<>(PREFERRED_PRESENTATION_MODE);

        while (!preferredPresentationModes.isEmpty()) {
            final Integer testPresentationMode = preferredPresentationModes.poll();

            if (presentationModes.contains(testPresentationMode)) {
                return testPresentationMode;
            }
        }

        // uh fallback on this one?
        LOGGER.warn("None of the preferred presentation modes were supported!");
        return VK_PRESENT_MODE_FIFO_KHR;
    }

    private VkSurfaceCapabilitiesKHR getCapabilities() {
        final VkSurfaceCapabilitiesKHR caps = VkSurfaceCapabilitiesKHR.calloc();
        final VkPhysicalDevice physicalDevice = VKGlobalConstants.getInstance().selectedDevice.physicalDevice;
        final int err = vkGetPhysicalDeviceSurfaceCapabilitiesKHR(physicalDevice, surface, caps);

        if (err != VK10.VK_SUCCESS) {
            throw new AssertionError("Failed to get physical device surface capabilities: " + translateVulkanResult(err));
        }

        return caps;
    }

    public static final class Format {

        public final int colorFormat;
        public final int colorSpace;

        private Format(final int colorFormat, final int colorSpace) {
            this.colorFormat = colorFormat;
            this.colorSpace = colorSpace;
        }
    }
}
