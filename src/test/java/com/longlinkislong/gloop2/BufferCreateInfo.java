/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.longlinkislong.gloop2;

import java.util.Objects;

/**
 *
 * @author zmichaels
 */
public final class BufferCreateInfo {

    public final long size;
    public final BufferStorageHint storage;
    public final BufferAccessHint access;
    public final BufferMapHint mapped;
    
    public BufferCreateInfo(
            final long size, 
            final BufferStorageHint storage, 
            final BufferAccessHint access, 
            final BufferMapHint mapped) {
        
        this.size = size;
        this.storage = Objects.requireNonNull(storage);
        this.access = Objects.requireNonNull(access);
        this.mapped = Objects.requireNonNull(mapped);
    }
    
    public BufferCreateInfo() {
        this(0L, BufferStorageHint.DONT_CARE, BufferAccessHint.DONT_CARE, BufferMapHint.DONT_CARE);
    }
    
    public BufferCreateInfo withSize(final long newSize) {
        return new BufferCreateInfo(newSize, this.storage, this.access, this.mapped);
    }
    
    public BufferCreateInfo withStorageHints(final BufferStorageHint hints) {
        return new BufferCreateInfo(this.size, hints, this.access, this.mapped);
    }
    
    public BufferCreateInfo withAccessHints(final BufferAccessHint hints) {
        return new BufferCreateInfo(this.size, this.storage, hints, this.mapped);
    }
    
    public BufferCreateInfo withMappedHints(final BufferMapHint hints) {
        return new BufferCreateInfo(this.size, this.storage, this.access, hints);
    }
    
    public Buffer allocate() {
        return ObjectFactoryManager.getInstance().getBufferFactory().allocate(this);
    }
}
