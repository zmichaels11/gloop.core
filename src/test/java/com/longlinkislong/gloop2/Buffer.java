/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.longlinkislong.gloop2;

import java.nio.ByteBuffer;

/**
 *
 * @author zmichaels
 */
public interface Buffer {
    boolean isMapped();
    
    long getSize();
    
    ByteBuffer getMappedBuffer();
    
    long getMapOffset();
    
    long getMapSize();
    
    BufferAccessHint getAccessHint();
    
    BufferMapHint getMapHint();
    
    BufferStorageHint getStorageHint();
    
    Buffer map(long offset, long size, BufferMapHint mapHint, BufferMapInvalidationHint invalidationHint, BufferMapSynchronizationHint syncHint, BufferUnmapHint unmapHint);
    
    Buffer unmap();
    
    Buffer upload(long offset, long size, ByteBuffer data);
    
    default Buffer upload(ByteBuffer data) {
        return upload(0, data.capacity(), data);
    }
    
    Buffer download(long offset, long size, ByteBuffer data);
    
    default Buffer download(ByteBuffer data) {
        final int size = Math.min((int) this.getSize(), data.capacity());
        
        return download(0, size, data);
    }
    
    boolean isValid();
}
