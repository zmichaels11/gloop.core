/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.longlinkislong.gloop2.vkimpl;

import com.longlinkislong.gloop2.AbstractRasterPipelineFactory;
import com.longlinkislong.gloop2.RasterPipelineCreateInfo;
import static com.longlinkislong.gloop2.vkimpl.VKTranslate.toVKenum;
import java.nio.IntBuffer;
import java.nio.LongBuffer;
import java.util.stream.Collectors;
import static org.lwjgl.demo.vulkan.VKUtil.translateVulkanResult;
import org.lwjgl.system.MemoryStack;
import org.lwjgl.system.MemoryUtil;
import static org.lwjgl.system.MemoryUtil.NULL;
import org.lwjgl.vulkan.VK10;
import org.lwjgl.vulkan.VkDevice;
import org.lwjgl.vulkan.VkGraphicsPipelineCreateInfo;
import org.lwjgl.vulkan.VkPipelineColorBlendAttachmentState;
import org.lwjgl.vulkan.VkPipelineColorBlendStateCreateInfo;
import org.lwjgl.vulkan.VkPipelineDepthStencilStateCreateInfo;
import org.lwjgl.vulkan.VkPipelineDynamicStateCreateInfo;
import org.lwjgl.vulkan.VkPipelineInputAssemblyStateCreateInfo;
import org.lwjgl.vulkan.VkPipelineLayoutCreateInfo;
import org.lwjgl.vulkan.VkPipelineMultisampleStateCreateInfo;
import org.lwjgl.vulkan.VkPipelineRasterizationStateCreateInfo;
import org.lwjgl.vulkan.VkPipelineShaderStageCreateInfo;
import org.lwjgl.vulkan.VkPipelineViewportStateCreateInfo;

/**
 *
 * @author zmichaels
 */
public class VK10PipelineFactory extends AbstractRasterPipelineFactory<VK10RasterPipeline> {
    
    public static int COLOR_FORMAT; //TODO: generate this!

    @Override
    protected VK10RasterPipeline newPipeline() {
        return new VK10RasterPipeline();
    }

    @Override
    protected void doAllocate(VK10RasterPipeline pipeline) {        
        createPipeline(pipeline);
    }    

    private void createPipeline(VK10RasterPipeline pipeline) {
        final RasterPipelineCreateInfo info = pipeline.getInfo();

        final VkPipelineInputAssemblyStateCreateInfo inputAssemblyState = VkPipelineInputAssemblyStateCreateInfo.calloc()
                .sType(VK10.VK_STRUCTURE_TYPE_PIPELINE_INPUT_ASSEMBLY_STATE_CREATE_INFO)
                .topology(toVKenum(info.primitiveType));

        final VkPipelineRasterizationStateCreateInfo rasterizationState = VkPipelineRasterizationStateCreateInfo.calloc()
                .sType(VK10.VK_STRUCTURE_TYPE_PIPELINE_RASTERIZATION_STATE_CREATE_INFO)
                .polygonMode(toVKenum(info.polygonMode))
                .cullMode(toVKenum(info.cullMode))
                .frontFace(toVKenum(info.frontFace))
                .depthClampEnable(VK10.VK_FALSE)
                .rasterizerDiscardEnable(VK10.VK_FALSE)
                .depthBiasEnable(VK10.VK_FALSE);

        final VkPipelineColorBlendAttachmentState.Buffer colorWriteMask = VkPipelineColorBlendAttachmentState.calloc(1)
                .blendEnable(VK10.VK_FALSE) //TODO: support blending
                .colorWriteMask(0xF);       //TODO: get this value from the Pipeline info

        final VkPipelineColorBlendStateCreateInfo colorBlendState = VkPipelineColorBlendStateCreateInfo.calloc()
                .sType(VK10.VK_STRUCTURE_TYPE_PIPELINE_COLOR_BLEND_STATE_CREATE_INFO)
                .pAttachments(colorWriteMask);

        final VkPipelineViewportStateCreateInfo viewportState = VkPipelineViewportStateCreateInfo.calloc()
                .sType(VK10.VK_STRUCTURE_TYPE_PIPELINE_VIEWPORT_STATE_CREATE_INFO)
                .viewportCount(1)
                .scissorCount(1);

        final IntBuffer pDynamicStates = MemoryUtil.memAllocInt(2);

        pDynamicStates.put(VK10.VK_DYNAMIC_STATE_VIEWPORT).put(VK10.VK_DYNAMIC_STATE_SCISSOR).flip();

        final VkPipelineDynamicStateCreateInfo dynamicState = VkPipelineDynamicStateCreateInfo.calloc()
                .sType(VK10.VK_STRUCTURE_TYPE_PIPELINE_DYNAMIC_STATE_CREATE_INFO)
                .pDynamicStates(pDynamicStates);

        final VkPipelineDepthStencilStateCreateInfo depthStencilState = VkPipelineDepthStencilStateCreateInfo.calloc()
                .sType(VK10.VK_STRUCTURE_TYPE_PIPELINE_DEPTH_STENCIL_STATE_CREATE_INFO)
                .depthTestEnable(VK10.VK_FALSE)
                .depthWriteEnable(VK10.VK_FALSE)
                .depthCompareOp(VK10.VK_COMPARE_OP_ALWAYS)
                .depthBoundsTestEnable(VK10.VK_FALSE)
                .stencilTestEnable(VK10.VK_FALSE);

        depthStencilState.back()
                .failOp(VK10.VK_STENCIL_OP_KEEP)
                .passOp(VK10.VK_STENCIL_OP_KEEP)
                .compareOp(VK10.VK_COMPARE_OP_ALWAYS);

        depthStencilState.front(depthStencilState.back());

        final VkPipelineMultisampleStateCreateInfo multisampleState = VkPipelineMultisampleStateCreateInfo.calloc()
                .sType(VK10.VK_STRUCTURE_TYPE_PIPELINE_MULTISAMPLE_STATE_CREATE_INFO)
                .pSampleMask(null)
                .rasterizationSamples(VK10.VK_SAMPLE_COUNT_1_BIT);

        final VkPipelineShaderStageCreateInfo.Buffer shaderStages = new VK10Program(pipeline.getInfo().shaderStages.stream().map(shader -> (VK10Shader) shader).collect(Collectors.toList())).createInfo;

        final VkPipelineLayoutCreateInfo pPipelineLayoutCreateInfo = VkPipelineLayoutCreateInfo.calloc()
                .sType(VK10.VK_STRUCTURE_TYPE_PIPELINE_LAYOUT_CREATE_INFO)
                .pNext(NULL)
                .pSetLayouts(null);
        
        final VkDevice device = VKThreadConstants.getInstance().physicalDevice.device;
        
        final long layout;
        try (MemoryStack stack = MemoryStack.stackPush()) {
            final LongBuffer pPipelineLayout = stack.callocLong(1);
            final int err = VK10.vkCreatePipelineLayout(device, pPipelineLayoutCreateInfo, null, pPipelineLayout);

            if (err != VK10.VK_SUCCESS) {
                throw new AssertionError("Failed to create pipeline layout: " + translateVulkanResult(err));
            }

            layout = pPipelineLayout.get(0);
        } finally {
            pPipelineLayoutCreateInfo.free();
        }
        
        final VKVertexInput vertexInput = new VKVertexInput(pipeline.getInfo().vertexInputs);

        final VkGraphicsPipelineCreateInfo.Buffer pipelineCreateInfo = VkGraphicsPipelineCreateInfo.calloc(1)
                .sType(VK10.VK_STRUCTURE_TYPE_GRAPHICS_PIPELINE_CREATE_INFO)
                .layout(layout)
                .renderPass(VKGlobalConstants.getInstance().getRenderPass(COLOR_FORMAT).id)
                .pVertexInputState(vertexInput.info)                
                .pInputAssemblyState(inputAssemblyState)
                .pRasterizationState(rasterizationState)
                .pColorBlendState(colorBlendState)
                .pMultisampleState(multisampleState)
                .pViewportState(viewportState)
                .pDepthStencilState(depthStencilState)
                .pStages(shaderStages)
                .pDynamicState(dynamicState);

        try (MemoryStack stack = MemoryStack.stackPush()) {
            final LongBuffer pPipelines = stack.callocLong(1);
            final int err = VK10.vkCreateGraphicsPipelines(device, VK10.VK_NULL_HANDLE, pipelineCreateInfo, null, pPipelines);

            if (err != VK10.VK_SUCCESS) {
                throw new AssertionError("Failed to create pipeline: " + translateVulkanResult(err));
            }
            
            pipeline.pipeline = pPipelines.get(0);
        } finally {
            shaderStages.free();
            multisampleState.free();
            depthStencilState.free();
            dynamicState.free();
            MemoryUtil.memFree(pDynamicStates);
            viewportState.free();
            colorBlendState.free();
            colorWriteMask.free();
            rasterizationState.free();
            inputAssemblyState.free();            
        }
    }      

    @Override
    public boolean isValid(VK10RasterPipeline pipeline) {
        return pipeline.pipeline != 0L;
    }

    @Override
    protected void doFree(VK10RasterPipeline pipeline) {
        final VkDevice device = VKThreadConstants.getInstance().physicalDevice.device;
        
        VK10.vkDestroyPipeline(device, pipeline.pipeline, null);        
    }

    @Override
    protected void doDraw(VK10RasterPipeline pipeline, int offset, int start) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

}
