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
public class VkException extends RuntimeException {

    public VkException() {
        super();
    }

    public VkException(String msg) {
        super(msg);
    }

    public VkException(Throwable cause) {
        super(cause);
    }

    public VkException(String msg, Throwable cause) {
        super(msg, cause);
    }
}
