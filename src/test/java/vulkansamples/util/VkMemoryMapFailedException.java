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
public class VkMemoryMapFailedException extends VkException {
    public static final String GENERIC_MESSAGE = "Mapping of a memory object has failed.";

    public VkMemoryMapFailedException() {
        super(GENERIC_MESSAGE);
    }

    public VkMemoryMapFailedException(final Throwable cause) {
        super(GENERIC_MESSAGE, cause);
    }

    public VkMemoryMapFailedException(final String msg) {
        super(msg);
    }

    public VkMemoryMapFailedException(final String msg, final Throwable cause) {
        super(msg, cause);
    }
}
