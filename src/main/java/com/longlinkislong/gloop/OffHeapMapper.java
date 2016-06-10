/* 
 * Copyright (c) 2015, longlinkislong.com
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 *
 * * Redistributions of source code must retain the above copyright notice, this
 *   list of conditions and the following disclaimer.
 * * Redistributions in binary form must reproduce the above copyright notice,
 *   this list of conditions and the following disclaimer in the documentation
 *   and/or other materials provided with the distribution.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS"
 * AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE
 * IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE
 * ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT HOLDER OR CONTRIBUTORS BE
 * LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR
 * CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF
 * SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS
 * INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN
 * CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE)
 * ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE
 * POSSIBILITY OF SUCH DAMAGE.
 */
package com.longlinkislong.gloop;

import java.lang.ref.Reference;
import java.lang.ref.WeakReference;
import java.nio.ByteBuffer;
import java.nio.LongBuffer;
import org.lwjgl.system.MemoryUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.Marker;
import org.slf4j.MarkerFactory;

/**
 * Creates an OffHeapMapper object. This will move heap objects off heap.
 *
 * @author zmichaels
 * @since 15.10.01
 */
public final class OffHeapMapper implements ObjectMapper {

    private static final Marker LWJGL_MARKER = MarkerFactory.getMarker("LWJGL");
    private static final Logger LOGGER = LoggerFactory.getLogger("OffHeapMapper");
    private static final int MAX_REFS;

    static {
        NativeTools.getInstance().loadNatives();
        MAX_REFS = Integer.getInteger("gloop.offheapmapper.max_refs", 20000);
        LOGGER.debug("Created reference pool of size: {}", MAX_REFS);
    }

    private final Reference<LongBuffer> sRefs;

    /**
     * Constructs a new OffHeapMapper object.
     *
     * @since 15.10.01
     */
    public OffHeapMapper() {
        final CleanupTask cleanup = new CleanupTask();

        this.sRefs = new WeakReference<>(cleanup.refs);

        LOGGER.trace(LWJGL_MARKER, "Adding shutdown hook...");
        Runtime.getRuntime().addShutdownHook(cleanup);
    }

    @Override
    public <T> T map(final T t) {
        final long addr = MemoryUtil.memNewGlobalRef(t);

        this.sRefs.get().put(addr);

        LOGGER.trace(LWJGL_MARKER, "Creating global reference to object: [{}]", t);
        return MemoryUtil.memGlobalRefToObject(addr);
    }

    class CleanupTask extends Thread {

        final LongBuffer refs = ByteBuffer.allocateDirect(MAX_REFS * Long.BYTES).asLongBuffer();

        @Override
        public void run() {
            LOGGER.trace(LWJGL_MARKER, "Deleting cached references...");

            int refCount = 0;
            refs.flip();
            while (refs.hasRemaining()) {
                MemoryUtil.memDeleteGlobalRef(refs.get());
                refCount++;
            }

            LOGGER.trace(LWJGL_MARKER, "Cleaned {} references.", refCount);
        }
    }
}
