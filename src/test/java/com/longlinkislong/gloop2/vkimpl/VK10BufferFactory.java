/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.longlinkislong.gloop2.vkimpl;

import com.longlinkislong.gloop2.AbstractBufferFactory;
import com.longlinkislong.gloop2.BufferAccessHint;
import com.longlinkislong.gloop2.BufferMapHint;
import com.longlinkislong.gloop2.BufferMapInvalidationHint;
import com.longlinkislong.gloop2.BufferMapSynchronizationHint;
import com.longlinkislong.gloop2.BufferStorageHint;
import com.longlinkislong.gloop2.BufferUnmapHint;
import java.nio.IntBuffer;
import java.nio.LongBuffer;
import org.lwjgl.PointerBuffer;
import static org.lwjgl.demo.vulkan.VKUtil.translateVulkanResult;
import org.lwjgl.system.MemoryStack;
import static org.lwjgl.system.MemoryUtil.NULL;
import org.lwjgl.vulkan.VK10;
import org.lwjgl.vulkan.VkBufferCreateInfo;
import org.lwjgl.vulkan.VkDevice;
import org.lwjgl.vulkan.VkMemoryAllocateInfo;
import org.lwjgl.vulkan.VkMemoryRequirements;
import org.lwjgl.vulkan.VkPhysicalDeviceMemoryProperties;

/**
 *
 * @author zmichaels
 */
public class VK10BufferFactory extends AbstractBufferFactory<VK10Buffer> {
    private static final int GENERIC_BUFFER_HINT =            
            VK10.VK_BUFFER_USAGE_VERTEX_BUFFER_BIT;
    public static VkDevice DEVICE = null;
    public static VkPhysicalDeviceMemoryProperties deviceMemoryProperties;
    
    @Override
    protected VK10Buffer newBuffer() {
        return new VK10Buffer();
    }

    @Override
    protected void doAllocate(VK10Buffer buffer, long size, BufferAccessHint accessHint, BufferMapHint mapHint, BufferStorageHint storageHint) {
        
        final VkBufferCreateInfo bufInfo = VkBufferCreateInfo.calloc()
                .sType(VK10.VK_STRUCTURE_TYPE_BUFFER_CREATE_INFO)
                .pNext(NULL)
                .size(size)
                .usage(GENERIC_BUFFER_HINT)
                .flags(0);
                
        try (MemoryStack stack = MemoryStack.stackPush()) {
            final LongBuffer pBuffer = stack.callocLong(1);
            final int err = VK10.vkCreateBuffer(DEVICE, bufInfo, null, pBuffer);            
            
            buffer.id = pBuffer.get(0);                        
            
            if (err != VK10.VK_SUCCESS) {
                throw new AssertionError("Failed to create vertex buffer: " + translateVulkanResult(err));
            }
        } finally {
            bufInfo.free();
        }
        
        final VkMemoryRequirements memReqs = VkMemoryRequirements.calloc();
        final VkMemoryAllocateInfo memAlloc = VkMemoryAllocateInfo.calloc()
                .sType(VK10.VK_STRUCTURE_TYPE_MEMORY_ALLOCATE_INFO)
                .pNext(NULL);
        
        VK10.vkGetBufferMemoryRequirements(DEVICE, buffer.id, memReqs);
        
        memAlloc.allocationSize(memReqs.size());
        
        try (MemoryStack stack = MemoryStack.stackPush()) {
            final IntBuffer memoryTypeIndex = stack.callocInt(1);
            
            getMemoryType(deviceMemoryProperties, memReqs.memoryTypeBits(), VK10.VK_MEMORY_PROPERTY_HOST_VISIBLE_BIT, memoryTypeIndex);
            
            memAlloc.memoryTypeIndex(memoryTypeIndex.get(0));            
        } finally {
            memReqs.free();
        }
        
        try (MemoryStack stack = MemoryStack.stackPush()) {
            final LongBuffer pMemory = stack.callocLong(1);
            final int err = VK10.vkAllocateMemory(DEVICE, memAlloc, null, pMemory);
            
            buffer.memId = pMemory.get(0);
            
            if (err != VK10.VK_SUCCESS) {
                throw new AssertionError("Failed to allocate vertex memory: " + translateVulkanResult(err));
            }                        
        } finally {
            memAlloc.free();
        }
        
        {
            final int err = VK10.vkBindBufferMemory(DEVICE, buffer.id, buffer.memId, 0L);
            
            if (err != VK10.VK_SUCCESS) {
                throw new AssertionError("Failed to bind memory to vertex buffer: " + translateVulkanResult(err));
            }
        }
    }
    
    private static boolean getMemoryType(VkPhysicalDeviceMemoryProperties deviceMemoryProperties, int typeBits, int properties, IntBuffer typeIndex) {
        int bits = typeBits;
        for (int i = 0; i < 32; i++) {
            if ((bits & 1) == 1) {
                if ((deviceMemoryProperties.memoryTypes(i).propertyFlags() & properties) == properties) {
                    typeIndex.put(0, i);
                    return true;
                }
            }
            bits >>= 1;
        }
        return false;
    }

    @Override
    protected void doFree(VK10Buffer buffer) {
        VK10.vkDestroyBuffer(DEVICE, buffer.id, null);
    }

    @Override
    protected void doDownload(VK10Buffer buffer, long offset, long size, long ptr) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    protected void doUpload(VK10Buffer buffer, long offset, long size, long ptr) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    protected long doMap(VK10Buffer buffer, long offset, long size, BufferMapHint mapHint, BufferMapInvalidationHint invalidHint, BufferMapSynchronizationHint syncHint, BufferUnmapHint unmapHint) {                
        try (MemoryStack stack = MemoryStack.stackPush()) {
            final PointerBuffer pData = stack.callocPointer(1);
            final int err = VK10.vkMapMemory(DEVICE, buffer.memId, offset, size, 0, pData);
                        
            final long data = pData.get(0);
            
            if (err != VK10.VK_SUCCESS) {
                throw new AssertionError("Failed to map vertex memory: " + translateVulkanResult(err));
            }
            
            buffer.setMapInfo(data, offset, size);
            
            return data;
        }                
    }

    @Override
    protected void doUnmap(VK10Buffer buffer) {
        VK10.vkUnmapMemory(DEVICE, buffer.memId);                
        buffer.setMapInfo(0L, 0L, 0L);
    }

    @Override
    public boolean isValid(VK10Buffer buffer) {
        return buffer.id != 0L;
    }
    
}
