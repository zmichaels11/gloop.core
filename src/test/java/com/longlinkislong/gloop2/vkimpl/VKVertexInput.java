/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.longlinkislong.gloop2.vkimpl;

import com.longlinkislong.gloop2.VertexArrayCreateInfo;
import com.longlinkislong.gloop2.VertexAttribute;
import static com.longlinkislong.gloop2.vkimpl.VKTranslate.toVKenum;
import java.util.List;
import static org.lwjgl.system.MemoryUtil.NULL;
import org.lwjgl.vulkan.VK10;
import org.lwjgl.vulkan.VkPipelineVertexInputStateCreateInfo;
import org.lwjgl.vulkan.VkVertexInputAttributeDescription;
import org.lwjgl.vulkan.VkVertexInputBindingDescription;

/**
 *
 * @author zmichaels
 */
public class VKVertexInput {
    public final VkPipelineVertexInputStateCreateInfo info;
    
    public VKVertexInput(VertexArrayCreateInfo info) {
        final List<VertexAttribute> attribs = info.attributes;
        
        final VkVertexInputBindingDescription.Buffer bindings = VkVertexInputBindingDescription.calloc(attribs.size());
        final VkVertexInputAttributeDescription.Buffer descriptions = VkVertexInputAttributeDescription.calloc(attribs.size());

        for (int i = 0; i < attribs.size(); i++) {
            final VertexAttribute attrib = attribs.get(i);
            final int format = toVKenum(attrib.format);

            bindings.get(i)
                    .binding(i)
                    .stride(attrib.stride)
                    .inputRate(VK10.VK_VERTEX_INPUT_RATE_VERTEX);

            descriptions.get(i)
                    .binding(i)
                    .location(attrib.location)
                    .format(format)
                    .offset(attrib.offset);
        }

        this.info = VkPipelineVertexInputStateCreateInfo.calloc()
                .sType(VK10.VK_STRUCTURE_TYPE_PIPELINE_VERTEX_INPUT_STATE_CREATE_INFO)
                .pNext(NULL)
                .pVertexAttributeDescriptions(descriptions)
                .pVertexBindingDescriptions(bindings);                
    }
    
    public void free() {
        info.free();
    }
}
