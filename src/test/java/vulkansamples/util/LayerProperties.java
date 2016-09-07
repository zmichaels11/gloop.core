/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package vulkansamples.util;

import java.util.Objects;
import org.lwjgl.vulkan.VkExtensionProperties;
import org.lwjgl.vulkan.VkLayerProperties;

/**
 *
 * @author zmichaels
 */
public final class LayerProperties {
    final VkLayerProperties properties;
    VkExtensionProperties.Buffer extensions = VkExtensionProperties.calloc(0);

    public LayerProperties(final VkLayerProperties props) {
        this.properties = Objects.requireNonNull(props);
    }
}
