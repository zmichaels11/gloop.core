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
public class VkLayerNotPresentException extends VkException {
    public static final String GENERIC_MESSAGE = "A requested layer is not present or could not be loaded.";

    public VkLayerNotPresentException() {
        super(GENERIC_MESSAGE);
    }

    public VkLayerNotPresentException(final Throwable cause) {
        super(GENERIC_MESSAGE, cause);
    }

    public VkLayerNotPresentException(final String msg) {
        super(msg);
    }

    public VkLayerNotPresentException(final String msg, final Throwable cause) {
        super(msg, cause);
    }
}
