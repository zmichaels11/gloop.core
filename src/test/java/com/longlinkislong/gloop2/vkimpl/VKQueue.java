/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.longlinkislong.gloop2.vkimpl;

import org.lwjgl.vulkan.VK10;

/**
 *
 * @author zmichaels
 */
public class VKQueue {
    public final boolean isGraphicsQueue;
    public final boolean isComputeQueue;
    public final boolean isTransferQueue;
    public final boolean hasSparseBinding;
    public final int queueIndex;
    
    public VKQueue(final int queueIndex, final int flags) {
        this.queueIndex = queueIndex;
        this.isComputeQueue = (flags & VK10.VK_QUEUE_COMPUTE_BIT) != 0;
        this.isGraphicsQueue = (flags & VK10.VK_QUEUE_GRAPHICS_BIT) != 0;
        this.isTransferQueue = (flags & VK10.VK_QUEUE_TRANSFER_BIT) != 0;
        this.hasSparseBinding = (flags & VK10.VK_QUEUE_SPARSE_BINDING_BIT) != 0;
    }
}
