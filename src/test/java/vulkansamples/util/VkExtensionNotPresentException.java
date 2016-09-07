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
public class VkExtensionNotPresentException extends VkException {
    public static final String GENERIC_MESSAGE = "A requested extension is not supported.";

    public VkExtensionNotPresentException() {
        super(GENERIC_MESSAGE);
    }

    public VkExtensionNotPresentException(final Throwable cause) {
        super(GENERIC_MESSAGE, cause);
    }

    public VkExtensionNotPresentException(final String msg) {
        super(msg);
    }

    public VkExtensionNotPresentException(final String msg, final Throwable cause) {
        super(msg, cause);
    }
}
