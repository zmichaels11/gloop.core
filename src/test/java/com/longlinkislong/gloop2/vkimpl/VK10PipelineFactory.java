/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.longlinkislong.gloop2.vkimpl;

import com.longlinkislong.gloop2.AbstractPipelineFactory;
import com.longlinkislong.gloop2.PipelineCreateInfo;
import com.longlinkislong.gloop2.VertexAttribute;
import static com.longlinkislong.gloop2.vkimpl.VKTranslate.toVKenum;
import java.nio.IntBuffer;
import java.nio.LongBuffer;
import java.util.List;
import java.util.stream.Collectors;
import static org.lwjgl.demo.vulkan.VKUtil.translateVulkanResult;
import org.lwjgl.system.MemoryStack;
import org.lwjgl.system.MemoryUtil;
import static org.lwjgl.system.MemoryUtil.NULL;
import org.lwjgl.vulkan.VK10;
import org.lwjgl.vulkan.VkAttachmentDescription;
import org.lwjgl.vulkan.VkAttachmentReference;
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
import org.lwjgl.vulkan.VkPipelineVertexInputStateCreateInfo;
import org.lwjgl.vulkan.VkPipelineViewportStateCreateInfo;
import org.lwjgl.vulkan.VkRenderPassCreateInfo;
import org.lwjgl.vulkan.VkSubpassDescription;
import org.lwjgl.vulkan.VkVertexInputAttributeDescription;
import org.lwjgl.vulkan.VkVertexInputBindingDescription;

/**
 *
 * @author zmichaels
 */
public class VK10PipelineFactory extends AbstractPipelineFactory<VK10Pipeline> {

    public static VkDevice DEVICE;
    public static int colorFormat; //TODO: generate this!

    @Override
    protected VK10Pipeline newPipeline() {
        return new VK10Pipeline();
    }

    @Override
    protected void doAllocate(VK10Pipeline pipeline) {

    }

    private VkPipelineVertexInputStateCreateInfo createVao(VK10Pipeline pipeline) {
        final List<VertexAttribute> attribs = pipeline.getAttributes();

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

        final VkPipelineVertexInputStateCreateInfo out = VkPipelineVertexInputStateCreateInfo.calloc()
                .sType(VK10.VK_STRUCTURE_TYPE_PIPELINE_VERTEX_INPUT_STATE_CREATE_INFO)
                .pNext(NULL)
                .pVertexAttributeDescriptions(descriptions)
                .pVertexBindingDescriptions(bindings);

        return out;
    }

    private void createPipeline(VK10Pipeline pipeline) {
        final PipelineCreateInfo info = pipeline.getInfo();

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

        final VkPipelineShaderStageCreateInfo.Buffer shaderStages = createShaderStages(pipeline);

        final VkPipelineLayoutCreateInfo pPipelineLayoutCreateInfo = VkPipelineLayoutCreateInfo.calloc()
                .sType(VK10.VK_STRUCTURE_TYPE_PIPELINE_LAYOUT_CREATE_INFO)
                .pNext(NULL)
                .pSetLayouts(null);

        final long layout;
        try (MemoryStack stack = MemoryStack.stackPush()) {
            final LongBuffer pPipelineLayout = stack.callocLong(1);
            final int err = VK10.vkCreatePipelineLayout(DEVICE, pPipelineLayoutCreateInfo, null, pPipelineLayout);

            if (err != VK10.VK_SUCCESS) {
                throw new AssertionError("Failed to create pipeline layout: " + translateVulkanResult(err));
            }

            layout = pPipelineLayout.get(0);
        } finally {
            pPipelineLayoutCreateInfo.free();
        }

        final VkGraphicsPipelineCreateInfo.Buffer pipelineCreateInfo = VkGraphicsPipelineCreateInfo.calloc(1)
                .sType(VK10.VK_STRUCTURE_TYPE_GRAPHICS_PIPELINE_CREATE_INFO)
                .layout(layout)
                .renderPass(pipeline.renderpass)
                .pVertexInputState(createVao(pipeline))
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
            final int err = VK10.vkCreateGraphicsPipelines(DEVICE, VK10.VK_NULL_HANDLE, pipelineCreateInfo, null, pPipelines);

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

    private VkPipelineShaderStageCreateInfo.Buffer createShaderStages(VK10Pipeline pipeline) {
        final List<VK10Shader> shaders = pipeline.getInfo().shaderStages.stream()
                .map(shader -> (VK10Shader) shader)
                .collect(Collectors.toList());

        final VkPipelineShaderStageCreateInfo.Buffer shaderStages = VkPipelineShaderStageCreateInfo.calloc(shaders.size());

        for (int i = 0; i < shaders.size(); i++) {
            final VK10Shader shader = shaders.get(i);

            shaderStages.get(i)
                    .sType(VK10.VK_STRUCTURE_TYPE_PIPELINE_SHADER_STAGE_CREATE_INFO)
                    .stage(toVKenum(shader.getShaderType()))
                    .module(shader.module)
                    .pName(MemoryUtil.memUTF8("main"));
        }

        return shaderStages;
    }

    private void createRenderPass(VK10Pipeline pipeline) {
        final VkAttachmentDescription.Buffer attachments = VkAttachmentDescription.calloc(1)
                .format(colorFormat)
                .samples(VK10.VK_SAMPLE_COUNT_1_BIT)
                .loadOp(VK10.VK_ATTACHMENT_LOAD_OP_CLEAR)
                .storeOp(VK10.VK_ATTACHMENT_STORE_OP_STORE)
                .stencilLoadOp(VK10.VK_ATTACHMENT_LOAD_OP_DONT_CARE)
                .stencilStoreOp(VK10.VK_ATTACHMENT_STORE_OP_DONT_CARE)
                .initialLayout(VK10.VK_IMAGE_LAYOUT_COLOR_ATTACHMENT_OPTIMAL)
                .finalLayout(VK10.VK_IMAGE_LAYOUT_COLOR_ATTACHMENT_OPTIMAL);

        final VkAttachmentReference.Buffer colorReference = VkAttachmentReference.calloc(1)
                .attachment(0)
                .layout(VK10.VK_IMAGE_LAYOUT_COLOR_ATTACHMENT_OPTIMAL);

        final VkSubpassDescription.Buffer subpass = VkSubpassDescription.calloc(1)
                .pipelineBindPoint(VK10.VK_PIPELINE_BIND_POINT_GRAPHICS)
                .pInputAttachments(null)
                .colorAttachmentCount(colorReference.remaining())
                .pColorAttachments(colorReference)
                .pResolveAttachments(null)
                .pDepthStencilAttachment(null)
                .pPreserveAttachments(null);

        final VkRenderPassCreateInfo renderPassInfo = VkRenderPassCreateInfo.calloc()
                .sType(VK10.VK_STRUCTURE_TYPE_RENDER_PASS_CREATE_INFO)
                .pNext(NULL)
                .pAttachments(attachments)
                .pSubpasses(subpass)
                .pDependencies(null);

        try (MemoryStack stack = MemoryStack.stackPush()) {
            final LongBuffer pRenderPass = stack.callocLong(1);
            final int err = VK10.vkCreateRenderPass(DEVICE, renderPassInfo, null, pRenderPass);

            if (err != VK10.VK_SUCCESS) {
                throw new AssertionError("Failed to create render pass: " + translateVulkanResult(err));
            }

            pipeline.renderpass = pRenderPass.get(0);
        } finally {
            renderPassInfo.free();
            subpass.free();
            colorReference.free();
            attachments.free();
        }
    }

    @Override
    public boolean isValid(VK10Pipeline pipeline) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    protected void doFree(VK10Pipeline pipeline) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    protected void doDraw(VK10Pipeline pipeline, int offset, int start) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

}
