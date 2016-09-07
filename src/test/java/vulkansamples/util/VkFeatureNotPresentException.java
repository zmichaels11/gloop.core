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
public class VkFeatureNotPresentException extends VkException {
    public static final String GENERIC_MESSAGE = "The requested feature is not supported.";

    public VkFeatureNotPresentException() {
        super(GENERIC_MESSAGE);
    }

    public VkFeatureNotPresentException(final Throwable cause) {
        super(GENERIC_MESSAGE, cause);
    }

    public VkFeatureNotPresentException(final String msg) {
        super(msg);
    }

    public VkFeatureNotPresentException(final String msg, final Throwable cause) {
        super(msg, cause);
    }
}
