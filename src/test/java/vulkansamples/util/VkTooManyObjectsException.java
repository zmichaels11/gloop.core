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
public class VkTooManyObjectsException extends VkException {

    public static final String GENERIC_MESSAGE = "Too many objects of the type have already been created.";

    public VkTooManyObjectsException() {
        super(GENERIC_MESSAGE);
    }

    public VkTooManyObjectsException(final Throwable cause) {
        super(GENERIC_MESSAGE, cause);
    }

    public VkTooManyObjectsException(final String msg) {
        super(msg);
    }

    public VkTooManyObjectsException(final String msg, final Throwable cause) {
        super(msg, cause);
    }
}
