/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.longlinkislong.gloop2;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 * @author zmichaels
 * @param <T>
 */
public abstract class AbstractBufferFactory<T extends AbstractBuffer> {

    private static final Logger LOGGER = LoggerFactory.getLogger(AbstractBufferFactory.class);

    protected abstract T newBuffer();

    public T allocate(BufferCreateInfo info) {
        final T out = newBuffer();

        out.accessHint = info.access;
        out.size = info.size;
        out.mapHint = info.mapped;
        out.storageHint = info.storage;

        doAllocate(out, info.size, info.access, info.mapped, info.storage);

        return out;
    }

    protected abstract void doAllocate(T buffer, long size, BufferAccessHint accessHint, BufferMapHint mapHint, BufferStorageHint storageHint);

    public void free(T buffer) {
        if (isValid(buffer)) {
            doFree(buffer);
            buffer.clear();
        } else {
            LOGGER.warn("Attempted to free unallocated buffer!");
        }
    }

    protected abstract void doFree(T buffer);

    public void download(T buffer, long offset, long size, long ptr) {
        if (ptr == 0) {
            LOGGER.warn("Attempted to download to nullptr!");
        } else if (!isValid(buffer)) {
            LOGGER.warn("Attempted to download data from invalid buffer!");
        } else {
            doDownload(buffer, offset, size, ptr);
        }
    }
    
    protected abstract void doDownload(T buffer, long offset, long size, long ptr);

    public void upload(T buffer, long offset, long size, long ptr) {
        if (offset > 0 && ptr == 0) {
            LOGGER.warn("Cannot allocate space if offset is not 0!");
        } else if (!isValid(buffer)) {
            LOGGER.warn("Attempted to upload data to invalid buffer!");
        } else {
            doUpload(buffer, offset, size, ptr);
        }
    }
    
    protected abstract void doUpload(T buffer, long offset, long size, long ptr);

    protected abstract long doMap(T buffer, long offset, long size, BufferMapHint mapHint, BufferMapInvalidationHint invalidHint, BufferMapSynchronizationHint syncHint, BufferUnmapHint unmapHint);

    protected abstract void doUnmap(T buffer);
    
    public void unmap(T buffer) {
        if (isValid(buffer)) {
            doUnmap(buffer);
        } else {
            LOGGER.warn("Attempted to unmap invalid buffer!");
        }
    }

    public long map(
            final T buffer, final long offset, final long size, 
            final BufferMapHint mapHint, 
            final BufferMapInvalidationHint invalidHint, 
            final BufferMapSynchronizationHint syncHint, 
            final BufferUnmapHint unmapHint) {
        
        if (buffer.isMapped()) {
            LOGGER.warn("Attempted to map buffer (already mapped)");
            return buffer.mapPtr;
        } else {
            
            
            return doMap(
                    buffer,
                    offset,
                    size,
                    BufferMapHint.sanitize(mapHint),
                    BufferMapInvalidationHint.sanitize(invalidHint),
                    BufferMapSynchronizationHint.sanitize(syncHint),
                    BufferUnmapHint.sanitize(unmapHint));
        }
    }
    
    public abstract boolean isValid(T buffer);
}
