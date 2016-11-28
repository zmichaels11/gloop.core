/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.longlinkislong.gloop2.glimpl;

import com.longlinkislong.gloop2.glimpl.GL4XBuffer;
import com.longlinkislong.gloop2.AbstractBufferFactory;
import com.longlinkislong.gloop2.BufferAccessHint;
import com.longlinkislong.gloop2.BufferMapHint;
import com.longlinkislong.gloop2.BufferMapInvalidationHint;
import com.longlinkislong.gloop2.BufferMapSynchronizationHint;
import com.longlinkislong.gloop2.BufferStorageHint;
import com.longlinkislong.gloop2.BufferUnmapHint;
import org.lwjgl.opengl.ARBBufferStorage;
import org.lwjgl.opengl.GL;
import org.lwjgl.opengl.GL15;
import org.lwjgl.opengl.GL30;
import org.lwjgl.opengl.GL44;

/**
 *
 * @author zmichaels
 */
public class GL4XBufferFactory extends AbstractBufferFactory<GL4XBuffer> {

    @Override
    protected GL4XBuffer newBuffer() {
        final GL4XBuffer out = new GL4XBuffer();

        out.id = GL15.glGenBuffers();

        return out;
    }

    private void doImmutableAllocate(
            final GL4XBuffer buffer, 
            final long size, 
            final BufferAccessHint accessHint, 
            final BufferMapHint mapHint, 
            final BufferStorageHint storageHint) {
        
        int flags = 0;

        switch (accessHint) {
            case READ:
                flags |= GL30.GL_MAP_READ_BIT;
                break;
            case WRITE:
                flags |= GL30.GL_MAP_WRITE_BIT;
                break;
            default:
                flags |= (GL30.GL_MAP_READ_BIT | GL30.GL_MAP_WRITE_BIT);
                break;
        }

        switch (mapHint) {
            case PERSISTENT:
                flags |= GL44.GL_MAP_PERSISTENT_BIT;
                break;
            case COHERENT:
                flags |= (GL44.GL_MAP_PERSISTENT_BIT | GL44.GL_MAP_COHERENT_BIT);
                break;
            default:
            // do nothing
        }

        switch (storageHint) {
            case DYNAMIC:
                flags |= GL44.GL_DYNAMIC_STORAGE_BIT;
                break;
            default:
            // do nothing
        }

        GL15.glBindBuffer(GL15.GL_ARRAY_BUFFER, buffer.id);
        ARBBufferStorage.glBufferStorage(GL15.GL_ARRAY_BUFFER, size, flags);
    }

    private void doMutableAllocate(
            final GL4XBuffer buffer, 
            final long size, 
            final BufferAccessHint accessHint, 
            final BufferMapHint mapHint, 
            final BufferStorageHint storageHint) {
        
        final int usage;

        switch (storageHint) {
            case DYNAMIC:
                usage = GL15.GL_DYNAMIC_DRAW;
                break;
            default:
                usage = GL15.GL_STATIC_DRAW;
                break;
        }

        GL15.glBindBuffer(GL15.GL_ARRAY_BUFFER, buffer.id);
        GL15.glBufferData(GL15.GL_ARRAY_BUFFER, size, usage);
    }

    @Override
    protected void doAllocate(
            final GL4XBuffer buffer, 
            final long size, 
            final BufferAccessHint accessHint, 
            final BufferMapHint mapHint, 
            final BufferStorageHint storageHint) {
        
        if (GL.getCapabilities().GL_ARB_buffer_storage) {
            doImmutableAllocate(buffer, size, accessHint, mapHint, storageHint);
        } else {
            doMutableAllocate(buffer, size, accessHint, mapHint, storageHint);
        }
    }

    @Override
    protected void doFree(GL4XBuffer buffer) {
        GL15.glDeleteBuffers(buffer.id);
    }

    @Override
    protected void doDownload(GL4XBuffer buffer, long offset, long size, long ptr) {
        GL15.glBindBuffer(GL15.GL_ARRAY_BUFFER, buffer.id);
        GL15.nglGetBufferSubData(GL15.GL_ARRAY_BUFFER, offset, size, ptr);
    }

    @Override
    protected void doUpload(GL4XBuffer buffer, long offset, long size, long ptr) {
        GL15.glBindBuffer(GL15.GL_ARRAY_BUFFER, buffer.id);
        final int usage;

        switch (buffer.getStorageHint()) {
            case DYNAMIC:
                usage = GL15.GL_DYNAMIC_DRAW;
                break;
            default:
                usage = GL15.GL_STATIC_DRAW;
                break;
        }

        if (offset == 0) {
            GL15.nglBufferData(GL15.GL_ARRAY_BUFFER, size, ptr, usage);
        } else {
            GL15.nglBufferSubData(GL15.GL_ARRAY_BUFFER, offset, size, ptr);
        }
    }

    @Override
    protected long doMap(
            final GL4XBuffer buffer, 
            final long offset, 
            final long size, 
            final BufferMapHint mapHint, 
            final BufferMapInvalidationHint invalidHint, 
            final BufferMapSynchronizationHint syncHint, 
            final BufferUnmapHint unmapHint) {
        
        int flags = 0;

        switch (buffer.getAccessHint()) {
            case READ:
                flags |= GL30.GL_MAP_READ_BIT;
                break;
            case WRITE:
                flags |= GL30.GL_MAP_WRITE_BIT;
                break;
            default:
            case READ_WRITE:
                flags |= (GL30.GL_MAP_READ_BIT | GL30.GL_MAP_WRITE_BIT);
                break;
        }
        
        switch (syncHint) {
            case UNSYNCHRONIZED:
                flags |= GL30.GL_MAP_UNSYNCHRONIZED_BIT;
                break;
            default:
                // do nothing
        }
        
        switch (unmapHint) {
            case FLUSH_EXPLICIT:
                flags |= GL30.GL_MAP_FLUSH_EXPLICIT_BIT;
                break;
            default:
                // do nothing
        }
        
        switch (invalidHint) {
            case INVALIDATE_RANGE:
                flags |= GL30.GL_MAP_INVALIDATE_RANGE_BIT;
                break;
            case INVALIDATE_BUFFER:
                flags |= GL30.GL_MAP_INVALIDATE_BUFFER_BIT;
                break;
            default:
                // do nothing
        }
        
        if (GL.getCapabilities().GL_ARB_buffer_storage) {
            switch (mapHint) {
                case PERSISTENT:
                    flags |= ARBBufferStorage.GL_MAP_PERSISTENT_BIT;
                    break;
                case COHERENT:
                    flags |= (ARBBufferStorage.GL_MAP_PERSISTENT_BIT | ARBBufferStorage.GL_MAP_COHERENT_BIT);
                    break;
            }
        }
        
        GL15.glBindBuffer(GL15.GL_ARRAY_BUFFER, buffer.id);
        
        return GL30.nglMapBufferRange(GL15.GL_ARRAY_BUFFER, offset, size, flags);
    }

    @Override
    protected void doUnmap(GL4XBuffer buffer) {
        GL15.glBindBuffer(GL15.GL_ARRAY_BUFFER, buffer.id);
        GL15.glUnmapBuffer(GL15.GL_ARRAY_BUFFER);        
    }

    @Override
    public boolean isValid(GL4XBuffer buffer) {
        return buffer != null && buffer.id != 0;
    }

}
