/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.longlinkislong.gloop;

import java.nio.LongBuffer;
import org.lwjgl.system.MemoryUtil;

/**
 * Creates an OffHeapMapper object. This will move heap objects off heap.
 * @author zmichaels
 * @since 15.10.01
 */
public final class OffHeapMapper implements ObjectMapper {
    static {
        NativeTools.getInstance().autoLoad();
    }
    
    
    private static final int MAX_REFS = 10000;
    private final LongBuffer refs = LongBuffer.allocate(MAX_REFS);

    @Override
    public <T> T map(T t) {
        final long addr = MemoryUtil.memNewGlobalRef(t);

        refs.put(addr);

        return MemoryUtil.memGlobalRefToObject(addr);
    }

    @Override
    protected void finalize() throws Throwable {
        try {
            refs.flip();
            while (refs.hasRemaining()) {
                MemoryUtil.memDeleteGlobalRef(refs.get());
            }
        } finally {
            super.finalize();
        }
    }
}
