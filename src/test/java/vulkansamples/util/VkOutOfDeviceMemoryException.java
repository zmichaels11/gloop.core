/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package vulkansamples.util;

/**
 *
 * @author zmichaels
 */
public class VkOutOfDeviceMemoryException extends VkException {
    public static final String GENERIC_MESSAGE = "A device memory allocation has failed.";

    public VkOutOfDeviceMemoryException() {
        super(GENERIC_MESSAGE);
    }

    public VkOutOfDeviceMemoryException(final Throwable cause) {
        super(GENERIC_MESSAGE, cause);
    }

    public VkOutOfDeviceMemoryException(final String msg) {
        super(msg);
    }

    public VkOutOfDeviceMemoryException(final String msg, final Throwable cause) {
        super(msg, cause);
    }
}
