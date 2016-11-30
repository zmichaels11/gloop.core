/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.longlinkislong.gloop2.vkimpl;

import static com.longlinkislong.gloop2.vkimpl.VKTranslate.toVKenum;
import java.nio.ByteBuffer;
import java.util.List;
import org.lwjgl.system.MemoryUtil;
import org.lwjgl.vulkan.VK10;
import org.lwjgl.vulkan.VkPipelineShaderStageCreateInfo;

/**
 *
 * @author zmichaels
 */
public class VK10Program {
    private static final ByteBuffer ENTRY_MAIN = MemoryUtil.memUTF8("main");
    
    public final VkPipelineShaderStageCreateInfo.Buffer createInfo;
    
    public VK10Program(final List<VK10Shader> shaders) {
        this.createInfo = VkPipelineShaderStageCreateInfo.calloc(shaders.size());
        
        for (int i = 0; i < shaders.size(); i++) {
            final VK10Shader shader = shaders.get(i);
            
            this.createInfo.get(i)
                    .sType(VK10.VK_STRUCTURE_TYPE_PIPELINE_SHADER_STAGE_CREATE_INFO)
                    .stage(toVKenum(shader.getShaderType()))
                    .module(shader.module)
                    .pName(ENTRY_MAIN);
        }
    }
    
    public void free() {
        this.createInfo.free();
    }
}
