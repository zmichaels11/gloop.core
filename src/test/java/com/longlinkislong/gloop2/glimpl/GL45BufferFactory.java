/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.longlinkislong.gloop2.glimpl;

import com.longlinkislong.gloop2.AbstractBufferFactory;
import com.longlinkislong.gloop2.BufferAccessHint;
import com.longlinkislong.gloop2.BufferMapHint;
import com.longlinkislong.gloop2.BufferMapInvalidationHint;
import com.longlinkislong.gloop2.BufferMapSynchronizationHint;
import com.longlinkislong.gloop2.BufferStorageHint;
import com.longlinkislong.gloop2.BufferUnmapHint;
import org.lwjgl.opengl.GL15;
import org.lwjgl.opengl.GL30;
import org.lwjgl.opengl.GL44;
import org.lwjgl.opengl.GL45;

/**
 *
 * @author zmichaels
 */
public class GL45BufferFactory extends AbstractBufferFactory<GL45Buffer>{

    @Override
    protected GL45Buffer newBuffer() {
        final GL45Buffer out = new GL45Buffer();
        
        out.id = GL45.glCreateBuffers();
        
        return out;
    }
    
    @Override
    protected void doAllocate(
            final GL45Buffer buffer, 
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
        
        GL45.glNamedBufferStorage(buffer.id, size, flags);
    }
    
    @Override
    protected void doFree(GL45Buffer buffer) {
        GL15.glDeleteBuffers(buffer.id);        
    }

    @Override
    public boolean isValid(GL45Buffer buffer) {
        return buffer != null && buffer.id > 0;
    }    

    @Override
    protected void doDownload(GL45Buffer buffer, long offset, long size, long ptr) {
        GL45.nglGetNamedBufferSubData(buffer.id, offset, size, ptr);
    }

    @Override
    protected void doUpload(GL45Buffer buffer, long offset, long size, long ptr) {
        GL45.nglNamedBufferSubData(buffer.id, offset, size, ptr);
    }

    @Override
    protected long doMap(
            final GL45Buffer buffer, 
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
        
        return GL45.nglMapNamedBufferRange(buffer.id, offset, size, flags);
    }

    @Override
    protected void doUnmap(GL45Buffer buffer) {
        GL45.glUnmapNamedBuffer(buffer.id);
    }      
}
