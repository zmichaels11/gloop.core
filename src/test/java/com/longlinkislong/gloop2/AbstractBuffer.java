/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.longlinkislong.gloop2;

import java.nio.ByteBuffer;
import org.lwjgl.system.MemoryUtil;

/**
 *
 * @author zmichaels
 */
public abstract class AbstractBuffer {
    protected long size;
    protected long mapPtr;
    protected ByteBuffer mapBuf;
    protected long mapOffset;
    protected long mapSize;
    protected BufferAccessHint accessHint;
    protected BufferMapHint mapHint;
    protected BufferStorageHint storageHint;
    
    protected void clear() {
        this.size = 0L;
        this.mapPtr = 0L;
        this.mapBuf = null;
        this.mapOffset = 0L;
        this.mapSize = 0L;
        this.accessHint = null;
        this.mapHint = null;
        this.storageHint = null;
    }
    
    public boolean isMapped() {
        return this.mapPtr != 0L;
    }
    
    public long getSize() {
        return this.size;
    }        
    
    public ByteBuffer getMappedBuffer() {
        if (this.mapBuf == null && this.isMapped()) {
            //TODO: support LARGER buffers?
            this.mapBuf = MemoryUtil.memByteBuffer(this.mapPtr, (int) this.mapSize);
        }
        
        return mapBuf;
    }
    
    public long getMapOffset() {
        return this.mapOffset;
    }
    
    public long getMapSize() {
        return this.mapSize;
    }        
    
    public BufferAccessHint getAccessHint() {
        return this.accessHint;
    }
    
    public BufferMapHint getMapHint() {
        return this.mapHint;
    }
    
    public BufferStorageHint getStorageHint() {
        return this.storageHint;
    }
    
    public AbstractBuffer map(
            final long offset, final long size, 
            final BufferMapHint mapHint, 
            final BufferMapInvalidationHint invalidationHint, 
            final BufferMapSynchronizationHint syncHint, 
            final BufferUnmapHint unmapHint) {
        
        GLObjectFactoryManager.getInstance()
                .getBufferFactory()
                .map(this, offset, size, mapHint, invalidationHint, syncHint, unmapHint);        
        
        return this;
    }
    
    public AbstractBuffer unmap() {                
        GLObjectFactoryManager.getInstance()
                .getBufferFactory()
                .doUnmap(this);
        
        // allow the byte buffer to become kill
        this.mapBuf = null;
        
        return this;
    }
    
    public AbstractBuffer upload(long offset, long size, ByteBuffer data) {        
        GLObjectFactoryManager.getInstance()
                .getBufferFactory()
                .upload(this, offset, size, MemoryUtil.memAddress(data));
                
        return this;
    }
    
    public AbstractBuffer download(final long offset, final long size, final ByteBuffer data) {
        GLObjectFactoryManager.getInstance()
                .getBufferFactory()
                .download(this, offset, size, MemoryUtil.memAddress(data));
        
        return this;
    }
    
    public boolean isValid() {
        return GLObjectFactoryManager.getInstance().isValid(this);
    }
}
