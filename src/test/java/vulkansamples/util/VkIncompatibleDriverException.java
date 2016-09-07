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
public class VkIncompatibleDriverException extends VkException {
    public static final String GENERIC_MESSAGE = "The requested version of Vulkan is not supported by the driver or is otherwise incompatible for implementation-specific reasons.";

    public VkIncompatibleDriverException() {
        super(GENERIC_MESSAGE);
    }

    public VkIncompatibleDriverException(final Throwable cause) {
        super(GENERIC_MESSAGE, cause);
    }

    public VkIncompatibleDriverException(final String msg) {
        super(msg);
    }

    public VkIncompatibleDriverException(final String msg, final Throwable cause) {
        super(msg, cause);
    }
}
