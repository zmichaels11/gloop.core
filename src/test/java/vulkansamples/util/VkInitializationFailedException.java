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
public class VkInitializationFailedException extends VkException {

    public static final String GENERIC_MESSAGE = "Initialialization of an object could not be completed for implementation-specific reasons.";

    public VkInitializationFailedException() {
        super(GENERIC_MESSAGE);
    }

    public VkInitializationFailedException(final Throwable cause) {
        super(GENERIC_MESSAGE, cause);
    }

    public VkInitializationFailedException(final String msg) {
        super(msg);
    }

    public VkInitializationFailedException(final String msg, final Throwable cause) {
        super(msg, cause);
    }
}
