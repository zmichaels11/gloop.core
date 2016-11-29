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
public abstract class AbstractBuffer implements Buffer{
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
    
    @Override
    public boolean isMapped() {
        return this.mapPtr != 0L;
    }
    
    @Override
    public long getSize() {
        return this.size;
    }        
    
    @Override
    public ByteBuffer getMappedBuffer() {
        if (this.mapBuf == null && this.isMapped()) {
            //TODO: support LARGER buffers?
            this.mapBuf = MemoryUtil.memByteBuffer(this.mapPtr, (int) this.mapSize);
        }
        
        return mapBuf;
    }
    
    @Override
    public long getMapOffset() {
        return this.mapOffset;
    }
    
    @Override
    public long getMapSize() {
        return this.mapSize;
    }        
    
    @Override
    public BufferAccessHint getAccessHint() {
        return this.accessHint;
    }
    
    @Override
    public BufferMapHint getMapHint() {
        return this.mapHint;
    }
    
    @Override
    public BufferStorageHint getStorageHint() {
        return this.storageHint;
    }
    
    protected final AbstractBufferFactory getFactory() {
        return GLObjectFactoryManager.getInstance().getBufferFactory();
    }
    
    @Override
    public AbstractBuffer map(
            final long offset, final long size, 
            final BufferMapHint mapHint, 
            final BufferMapInvalidationHint invalidationHint, 
            final BufferMapSynchronizationHint syncHint, 
            final BufferUnmapHint unmapHint) {
        
        getFactory().map(this, offset, size, mapHint, invalidationHint, syncHint, unmapHint);        
        
        return this;
    }
    
    @Override
    public AbstractBuffer unmap() {                
        getFactory().doUnmap(this);
        
        // allow the byte buffer to become kill
        this.mapBuf = null;
        
        return this;
    }
    
    @Override
    public AbstractBuffer upload(long offset, long size, ByteBuffer data) {        
        getFactory().upload(this, offset, size, MemoryUtil.memAddress(data));
                
        return this;
    }
    
    @Override
    public AbstractBuffer download(final long offset, final long size, final ByteBuffer data) {
        getFactory().download(this, offset, size, MemoryUtil.memAddress(data));
        
        return this;
    }
    
    @Override
    public boolean isValid() {
        return getFactory().isValid(this);
    }
}
