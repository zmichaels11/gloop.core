/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.longlinkislong.gloop2.vkimpl;

import com.longlinkislong.gloop2.AbstractFramebufferFactory;
import com.longlinkislong.gloop2.FramebufferAttachment;
import com.longlinkislong.gloop2.FramebufferCreateInfo;
import static com.longlinkislong.gloop2.vkimpl.VKTranslate.toVKenum;
import java.nio.LongBuffer;
import java.util.Map;
import java.util.Objects;
import static org.lwjgl.demo.vulkan.VKUtil.translateVulkanResult;
import org.lwjgl.system.MemoryStack;
import org.lwjgl.vulkan.VK10;
import static org.lwjgl.vulkan.VK10.VK_ATTACHMENT_LOAD_OP_DONT_CARE;
import org.lwjgl.vulkan.VkAttachmentDescription;
import org.lwjgl.vulkan.VkAttachmentReference;
import org.lwjgl.vulkan.VkDevice;
import org.lwjgl.vulkan.VkFramebufferCreateInfo;
import org.lwjgl.vulkan.VkRenderPassCreateInfo;
import org.lwjgl.vulkan.VkSubpassDescription;

/**
 *
 * @author zmichaels
 */
public class VK10FramebufferFactory extends AbstractFramebufferFactory<VK10Framebuffer> {

    @Override
    protected VK10Framebuffer newFramebuffer() {
        return new VK10Framebuffer();
    }

    @Override
    protected void doAllocate(VK10Framebuffer fb) {
        createRenderPass(fb);
        createFramebuffer(fb);
    }

    private void createFramebuffer(VK10Framebuffer fb) {
        final FramebufferCreateInfo info = fb.getInfo();
        final Map<Integer, FramebufferAttachment> attachments = info.attachments;
        final int attachmentCount = attachments.size();

        try (MemoryStack stack = MemoryStack.stackPush()) {
            final LongBuffer pAttachments = stack.callocLong(attachmentCount);

            for (int i = 0; i < attachmentCount; i++) {
                pAttachments.put(i, ((VK10Texture2D) attachments.get(i)).id);
            }

            final VkFramebufferCreateInfo fbCreateInfo = VkFramebufferCreateInfo.calloc()
                    .sType(VK10.VK_STRUCTURE_TYPE_FRAMEBUFFER_CREATE_INFO)
                    .pAttachments(pAttachments)
                    .width(info.width)
                    .height(info.height)
                    .layers(1)
                    .renderPass(fb.renderpass);

            final VkDevice device = VKGlobalConstants.getInstance().selectedDevice.vkDevice;
            final LongBuffer pFramebuffer = stack.callocLong(1);
            final int err = VK10.vkCreateFramebuffer(device, fbCreateInfo, null, pFramebuffer);

            if (err != VK10.VK_SUCCESS) {
                throw new AssertionError("Failed to create framebuffer: " + translateVulkanResult(err));
            }
            
            fb.framebuffer = pFramebuffer.get(0);
        }
    }

    private void createRenderPass(VK10Framebuffer fb) {
        final int attachmentCount = fb.getInfo().attachments.size();

        try (MemoryStack stack = MemoryStack.stackPush()) {
            final VkAttachmentDescription.Buffer attachments = VkAttachmentDescription.callocStack(attachmentCount, stack);
            final VkAttachmentReference.Buffer colorReferences = VkAttachmentReference.callocStack(attachmentCount, stack);

            for (int i = 0; i < attachmentCount; i++) {
                final FramebufferAttachment attachment = fb.getInfo().attachments.get(i);

                Objects.requireNonNull(attachment, "Skipping attachment numbers is not supported!");

                //TODO: support depth attachments
                attachments.get(i)
                        .format(toVKenum(attachment.getFormat()))
                        .samples(VK10.VK_SAMPLE_COUNT_1_BIT)
                        .loadOp(VK10.VK_ATTACHMENT_LOAD_OP_LOAD)
                        .storeOp(VK10.VK_ATTACHMENT_STORE_OP_STORE).stencilLoadOp(VK_ATTACHMENT_LOAD_OP_DONT_CARE)
                        .stencilStoreOp(VK10.VK_ATTACHMENT_STORE_OP_DONT_CARE)
                        .initialLayout(VK10.VK_IMAGE_LAYOUT_COLOR_ATTACHMENT_OPTIMAL)
                        .finalLayout(VK10.VK_IMAGE_LAYOUT_COLOR_ATTACHMENT_OPTIMAL);

                colorReferences.get(i)
                        .attachment(i)
                        .layout(VK10.VK_IMAGE_LAYOUT_COLOR_ATTACHMENT_OPTIMAL);
            }

            final VkSubpassDescription.Buffer subpass = VkSubpassDescription.callocStack(1, stack)
                    .pipelineBindPoint(VK10.VK_PIPELINE_BIND_POINT_GRAPHICS)
                    .colorAttachmentCount(attachmentCount)
                    .pColorAttachments(colorReferences)
                    .pDepthStencilAttachment(null)
                    .pInputAttachments(null)
                    .pPreserveAttachments(null)
                    .pResolveAttachments(null);

            final VkRenderPassCreateInfo renderPassCreateInfo = VkRenderPassCreateInfo.callocStack(stack)
                    .sType(VK10.VK_STRUCTURE_TYPE_RENDER_PASS_CREATE_INFO)
                    .pAttachments(attachments)
                    .pSubpasses(subpass);

            final VkDevice device = VKGlobalConstants.getInstance().selectedDevice.vkDevice;
            final LongBuffer pRenderPass = stack.callocLong(1);
            final int err = VK10.vkCreateRenderPass(device, renderPassCreateInfo, null, pRenderPass);

            if (err != VK10.VK_SUCCESS) {
                throw new AssertionError("Failed to create render pass: " + translateVulkanResult(err));
            }

            fb.renderpass = pRenderPass.get(0);
        }
    }

    @Override
    public boolean isValid(VK10Framebuffer fb) {
        return fb.framebuffer != 0L;
    }

    @Override
    protected void doFree(VK10Framebuffer fb) {
        final VkDevice device = VKGlobalConstants.getInstance().selectedDevice.vkDevice;
        
        VK10.vkDestroyFramebuffer(device, fb.framebuffer, null);
        VK10.vkDestroyRenderPass(device, fb.renderpass, null);
    }

}
