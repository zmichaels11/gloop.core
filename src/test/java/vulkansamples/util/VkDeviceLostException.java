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
public class VkDeviceLostException extends VkException {
    public static final String GENERIC_MESSAGE = "The logical or physical device has been lost.";

    public VkDeviceLostException() {
        super(GENERIC_MESSAGE);
    }

    public VkDeviceLostException(final Throwable cause) {
        super(GENERIC_MESSAGE, cause);
    }

    public VkDeviceLostException(final String msg) {
        super(msg);
    }

    public VkDeviceLostException(final String msg, final Throwable cause) {
        super(msg, cause);
    }
}
