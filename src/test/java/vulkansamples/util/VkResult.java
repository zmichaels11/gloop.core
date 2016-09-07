/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package vulkansamples.util;

import java.util.Optional;
import org.lwjgl.vulkan.VK10;

/**
 *
 * @author zmichaels
 */
public enum VkResult {
    SUCCESS(VK10.VK_SUCCESS),
    NOT_READY(VK10.VK_NOT_READY),
    TIMEOUT(VK10.VK_TIMEOUT),
    EVENT_SET(VK10.VK_EVENT_SET),
    EVENT_RESET(VK10.VK_EVENT_RESET),
    INCOMPLETE(VK10.VK_INCOMPLETE),
    ERROR_OUT_OF_HOST_MEMORY(VK10.VK_ERROR_OUT_OF_HOST_MEMORY),
    ERROR_OUT_OF_DEVICE_MEMORY(VK10.VK_ERROR_OUT_OF_DEVICE_MEMORY),
    ERROR_INITIALIZATION_FAILED(VK10.VK_ERROR_INITIALIZATION_FAILED),
    ERROR_DEVICE_LOST(VK10.VK_ERROR_DEVICE_LOST),
    ERROR_LAYER_NOT_PRESENT(VK10.VK_ERROR_LAYER_NOT_PRESENT),
    ERROR_EXTENSION_NOT_PRESENT(VK10.VK_ERROR_EXTENSION_NOT_PRESENT),
    ERROR_FEATURE_NOT_PRESENT(VK10.VK_ERROR_FEATURE_NOT_PRESENT),
    ERROR_INCOMPATIBLE_DRIVER(VK10.VK_ERROR_INCOMPATIBLE_DRIVER),
    ERROR_TOO_MANY_OBJECTS(VK10.VK_ERROR_TOO_MANY_OBJECTS),
    ERROR_FORMAT_NOT_SUPPORTED(VK10.VK_ERROR_FORMAT_NOT_SUPPORTED),
    ERROR_FRAGMENTED_POOL(-12);
    final int value;

    public boolean isError() {
        return this.value < 0;
    }

    VkResult(final int value) {
        this.value = value;
    }

    public Optional<VkException> toException() {
        switch (this) {
            case ERROR_OUT_OF_HOST_MEMORY:
                return Optional.of(new VkOutOfHostMemoryException());
            case ERROR_OUT_OF_DEVICE_MEMORY:
                return Optional.of(new VkOutOfDeviceMemoryException());
            case ERROR_INITIALIZATION_FAILED:
                return Optional.of(new VkInitializationFailedException());
            case ERROR_DEVICE_LOST:
                return Optional.of(new VkDeviceLostException());
            case ERROR_LAYER_NOT_PRESENT:
                return Optional.of(new VkLayerNotPresentException());
            case ERROR_EXTENSION_NOT_PRESENT:
                return Optional.of(new VkExtensionNotPresentException());
            case ERROR_FEATURE_NOT_PRESENT:
                return Optional.of(new VkFeatureNotPresentException());
            case ERROR_INCOMPATIBLE_DRIVER:
                return Optional.of(new VkIncompatibleDriverException());
            case ERROR_TOO_MANY_OBJECTS:
                return Optional.of(new VkTooManyObjectsException());
            case ERROR_FORMAT_NOT_SUPPORTED:
                return Optional.of(new VkFormatNotSupportedException());
            case ERROR_FRAGMENTED_POOL:
                return Optional.of(new VkFragmentedPoolException());
            default:
                return Optional.empty();
        }
    }

    public static Optional<VkResult> of(final int vkEnum) {
        for (VkResult test : values()) {
            if (test.value == vkEnum) {
                return Optional.of(test);
            }
        }

        return Optional.empty();
    }
}
