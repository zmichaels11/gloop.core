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
public class VkFragmentedPoolException extends VkException {
    public static final String GENERIC_MESSAGE = "A requested pool allocation has failed due to fragmentation of the pool's memory.";

    public VkFragmentedPoolException() {
        super (GENERIC_MESSAGE);
    }

    public VkFragmentedPoolException(final String msg) {
        super(msg);
    }

    public VkFragmentedPoolException(final Throwable cause) {
        super(GENERIC_MESSAGE, cause);
    }

    public VkFragmentedPoolException(final String msg, final Throwable cause) {
        super(msg, cause);
    }
}
