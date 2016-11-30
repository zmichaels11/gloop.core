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
public class QueueFamily implements Comparable<QueueFamily>{
    public final boolean isGraphicsQueue;
    public final boolean isComputeQueue;
    public final boolean isTransferQueue;
    public final boolean hasSparseBinding;
    public final int queueFamilyIndex;
    
    public QueueFamily(final int queueIndex, final int flags) {
        this.queueFamilyIndex = queueIndex;
        this.isComputeQueue = (flags & VK10.VK_QUEUE_COMPUTE_BIT) != 0;
        this.isGraphicsQueue = (flags & VK10.VK_QUEUE_GRAPHICS_BIT) != 0;
        this.isTransferQueue = (flags & VK10.VK_QUEUE_TRANSFER_BIT) != 0;
        this.hasSparseBinding = (flags & VK10.VK_QUEUE_SPARSE_BINDING_BIT) != 0;
    }

    @Override
    public String toString() {
        final StringBuilder out = new StringBuilder(1024);
        
        out.append("Queue: [");
        out.append(queueFamilyIndex);
        out.append("]: ");
        out.append((this.isGraphicsQueue) ? "G" : " ");
        out.append((this.isComputeQueue) ? "C" : " ");
        out.append((this.isTransferQueue) ? "T" : " ");
        out.append((this.hasSparseBinding) ? "S" : " ");
        
        return out.toString();
    }
    
    private int getFeatureCount() {
        int features = 0;
        
        if (this.isGraphicsQueue) {
            features++;
        }
        
        if (this.isComputeQueue) {
            features++;
        }
        
        if (this.isTransferQueue) {
            features++;
        }
        
        return features;
    }
    
    // sorts the queues such that the queue supporting the most features appears first.
    @Override
    public int compareTo(QueueFamily o) {
        return (o.getFeatureCount() - this.getFeatureCount());
    }
}
