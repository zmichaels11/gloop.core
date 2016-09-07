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
public class VkOutOfHostMemoryException extends VkException {

    public static final String GENERIC_MSG = "A host memory allocation has failed.";

    public VkOutOfHostMemoryException() {
        super(GENERIC_MSG);
    }

    public VkOutOfHostMemoryException(final Throwable cause) {
        super(GENERIC_MSG, cause);
    }

    public VkOutOfHostMemoryException(final String msg) {
        super(msg);
    }

    public VkOutOfHostMemoryException(final String msg, final Throwable cause) {
        super(msg, cause);
    }
}
