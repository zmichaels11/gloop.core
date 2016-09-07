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
public class VkFormatNotSupportedException extends VkException {
    public static final String GENERIC_MESSAGE = "A requested format is not supported on this device.";

    public VkFormatNotSupportedException() {
        super(GENERIC_MESSAGE);
    }

    public VkFormatNotSupportedException(final Throwable cause) {
        super(GENERIC_MESSAGE);
    }

    public VkFormatNotSupportedException(final String msg) {
        super(msg);
    }

    public VkFormatNotSupportedException(final String msg, final Throwable cause) {
        super(msg, cause);
    }
}
