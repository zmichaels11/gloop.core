/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.longlinkislong.gloop;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;

/**
 * NativeTools is a container for native data structures.
 *
 * @author zmichaels
 * @since 15.08.05
 */
public final class NativeTools {

    private static final class Holder {

        private static final NativeTools INSTANCE = new NativeTools();
    }

    /**
     * Retrieves the instance of the NativeTools structure.
     *
     * @return the NativeTools.
     * @since 15.08.05
     */
    public static NativeTools getInstance() {
        return Holder.INSTANCE;
    }

    private final ByteBuffer[] wordPool = new ByteBuffer[256];  // 4 bytes    
    private final ByteBuffer[] dwordPool = new ByteBuffer[128];  // 8 bytes    
    private final ByteBuffer[] qwordPool = new ByteBuffer[64];  // 16 bytes
    private final ByteBuffer[] owordPool = new ByteBuffer[32];  // 32 bytes    
    private final ByteBuffer[] qvwordPool = new ByteBuffer[16];  // 64 bytes
    private final ByteBuffer[] ovwordPool = new ByteBuffer[8];  // 128 bytes

    private int wId;
    private int dwId;
    private int qwId;
    private int owId;
    private int qvwId;
    private int ovwId;

    private NativeTools() {
        ByteBuffer data = ByteBuffer.allocateDirect(1024);

        for (int i = 0; i < wordPool.length; i++) {
            data.position(i * 4);
            wordPool[i] = data.slice().order(ByteOrder.nativeOrder());
        }

        data = ByteBuffer.allocateDirect(1024);

        for (int i = 0; i < dwordPool.length; i++) {
            data.position(i * 8);
            dwordPool[i] = data.slice().order(ByteOrder.nativeOrder());
        }

        data = ByteBuffer.allocateDirect(1024);

        for (int i = 0; i < qwordPool.length; i++) {
            data.position(i * 16);
            qwordPool[i] = data.slice().order(ByteOrder.nativeOrder());
        }

        data = ByteBuffer.allocateDirect(1024);

        for (int i = 0; i < owordPool.length; i++) {
            data.position(i * 32);
            owordPool[i] = data.slice().order(ByteOrder.nativeOrder());
        }

        data = ByteBuffer.allocateDirect(1024);

        for (int i = 0; i < qvwordPool.length; i++) {
            data.position(i * 64);
            qvwordPool[i] = data.slice().order(ByteOrder.nativeOrder());
        }

        data = ByteBuffer.allocateDirect(1024);

        for (int i = 0; i < ovwordPool.length; i++) {
            data.position(i * 128);
            ovwordPool[i] = data.slice().order(ByteOrder.nativeOrder());
        }
    }

    private synchronized int nextWId() {
        final int id = this.wId;

        this.wId = (id + 1) % this.wordPool.length;

        return id;
    }

    private synchronized int nextDWId() {
        final int id = this.dwId;

        this.dwId = (id + 1) % this.dwordPool.length;

        return id;
    }

    private synchronized int nextQWId() {
        final int id = this.qwId;

        this.qwId = (id + 1) % this.qwordPool.length;

        return id;
    }

    private synchronized int nextOWId() {
        final int id = this.owId;

        this.owId = (id + 1) % this.owordPool.length;

        return id;
    }

    private synchronized int nextQVWId() {
        final int id = this.qvwId;

        this.qvwId = (id + 1) % this.qvwordPool.length;

        return id;
    }

    private synchronized int nextOVWId() {
        final int id = this.ovwId;

        this.ovwId = (id + 1) % this.ovwordPool.length;

        return id;
    }

    /**
     * Retrieves the next 32bit word from the object pool.
     *
     * @return the 32bit word.
     * @since 15.08.05
     */
    public ByteBuffer nextWord() {
        final int id = this.nextWId();
        final ByteBuffer out = this.wordPool[id];

        out.clear();

        return out;
    }

    /**
     * Retrieves the next 64bit word from the object pool.
     *
     * @return the 64bit word.
     * @since 15.08.05
     */
    public ByteBuffer nextDWord() {
        final int id = this.nextDWId();
        final ByteBuffer out = this.dwordPool[id];

        out.clear();

        return out;
    }

    /**
     * Retrieves the next 128bit word from the object pool.
     *
     * @return the 128bit word.
     * @since 15.08.05
     */
    public ByteBuffer nextQWord() {
        final int id = this.nextQWId();
        final ByteBuffer out = this.qwordPool[id];

        out.clear();

        return out;
    }

    /**
     * Retrieves the next 256bit word from the object pool.
     *
     * @return the 256bit word.
     * @since 15.08.05
     */
    public ByteBuffer nextOWord() {
        final int id = this.nextOWId();
        final ByteBuffer out = this.owordPool[id];

        out.clear();

        return out;
    }

    /**
     * Retrieves the next 512bit vector word from the object pool.
     *
     * @return the 512bit word.
     * @since 15.08.05
     */
    public ByteBuffer nextQVWord() {
        final int id = this.nextQVWId();
        final ByteBuffer out = this.qvwordPool[id];

        out.clear();

        return out;
    }

    /**
     * Retrieves the next 1024bit vector word from the object pool.
     *
     * @return the 1028bit word.
     * @since 15.08.05
     */
    public ByteBuffer nextOVWord() {
        final int id = this.nextOVWId();
        final ByteBuffer out = this.ovwordPool[id];

        out.clear();

        return out;
    }

}
