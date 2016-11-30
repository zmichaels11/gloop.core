/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.longlinkislong.gloop2.vkimpl;

import com.longlinkislong.gloop2.AbstractShaderFactory;
import com.longlinkislong.gloop2.ShaderType;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.LongBuffer;

import static org.lwjgl.demo.opengl.util.DemoUtils.ioResourceToByteBuffer;
import static org.lwjgl.demo.vulkan.VKUtil.translateVulkanResult;
import org.lwjgl.system.MemoryStack;
import static org.lwjgl.system.MemoryUtil.NULL;
import org.lwjgl.vulkan.VK10;
import org.lwjgl.vulkan.VkDevice;
import org.lwjgl.vulkan.VkShaderModuleCreateInfo;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 * @author zmichaels 
 */
public final class VK10ShaderFactory extends AbstractShaderFactory<VK10Shader> {
    private static final Logger LOGGER = LoggerFactory.getLogger(VK10ShaderFactory.class);    
    
    @Override
    protected VK10Shader newShader() {
        return new VK10Shader();
    }

    @Override
    protected void doAllocate(VK10Shader shader, ShaderType type, String src) {
        try {
            shader.module = loadShader(src);
        } catch (IOException ex) {
            shader.module = 0L;
            LOGGER.error("Unable to load shader from location: {}", src);
            LOGGER.debug(ex.getMessage(), ex);
        }
    }
    
    private static long loadShader(String classPath) throws IOException {
        final VkDevice device = VKThreadConstants.getInstance().getDevice();
        final ByteBuffer shaderCode = ioResourceToByteBuffer(classPath, 8096);
                
        final VkShaderModuleCreateInfo moduleCreateInfo = VkShaderModuleCreateInfo.calloc()
                .sType(VK10.VK_STRUCTURE_TYPE_SHADER_MODULE_CREATE_INFO)
                .pNext(NULL)
                .pCode(shaderCode)
                .flags(0);
        
        try (MemoryStack stack = MemoryStack.stackPush()) {
            final LongBuffer pShaderModule = stack.callocLong(1);            
            final int err = VK10.vkCreateShaderModule(device, moduleCreateInfo, null, pShaderModule);
            
            if (err != VK10.VK_SUCCESS) {
                throw new AssertionError("Failed to create shader module: " + translateVulkanResult(err));
            }
            
            return pShaderModule.get(0);
        } finally {
            moduleCreateInfo.free();            
        }
    }

    @Override
    protected void doFree(VK10Shader shader) {
        final VkDevice device = VKThreadConstants.getInstance().getDevice();
        
        VK10.vkDestroyShaderModule(device, shader.module, null);
    }

    @Override
    public boolean isValid(VK10Shader shader) {
        return shader.module != 0L;
    }        
}
