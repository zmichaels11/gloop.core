/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.longlinkislong.gloop;

import java.lang.ref.Reference;
import java.lang.ref.WeakReference;
import java.nio.ByteBuffer;
import java.nio.LongBuffer;
import org.lwjgl.system.MemoryUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Creates an OffHeapMapper object. This will move heap objects off heap.
 *
 * @author zmichaels
 * @since 15.10.01
 */
public final class OffHeapMapper implements ObjectMapper {
    private static final Logger LOGGER = LoggerFactory.getLogger(OffHeapMapper.class);
    private static final int MAX_REFS;    
    static {
        NativeTools.getInstance().loadNatives();
        MAX_REFS = Integer.getInteger("gloop.offheapmapper.max_refs", 20000);
        LOGGER.debug("Created reference pool of size: {}", MAX_REFS);
    }

    private final Reference<LongBuffer> sRefs;

    public OffHeapMapper() {        
        final CleanupTask cleanup = new CleanupTask();
        
        this.sRefs = new WeakReference<>(cleanup.refs);        
        Runtime.getRuntime().addShutdownHook(cleanup);
    }

    @Override
    public <T> T map(T t) {        
        final long addr = MemoryUtil.memNewGlobalRef(t);

        this.sRefs.get().put(addr);

        return MemoryUtil.memGlobalRefToObject(addr);
    }
    
    class CleanupTask extends Thread {
        final LongBuffer refs = ByteBuffer.allocateDirect(MAX_REFS * Long.BYTES).asLongBuffer();
        
        @Override
        public void run() {
            LOGGER.debug("Deleting cached references...");            
            
            int refCount = 0;
            refs.flip();
            while(refs.hasRemaining()) {
                MemoryUtil.memDeleteGlobalRef(refs.get());
                refCount++;
            }
            
            LOGGER.debug("Cleaned {} references.", refCount);            
        }
    }
}
