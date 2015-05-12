/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.longlinkislong.gloop;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.util.Iterator;
import java.util.List;
import java.util.RandomAccess;

/**
 *
 * @author zmichaels
 */
public class GLTools {

    public static void checkBuffer(final ByteBuffer data) {
        if(!data.isDirect()) {
            throw new GLException("ByteBuffer is not direct!");
        } else if(data.order() != ByteOrder.nativeOrder()) {
            throw new GLException("ByteBuffer is not in native order!");
        }
    }
    
    /**
     * Calculates the best-fit power of 2 for the specified number
     *
     * @param n the number to fit
     * @return the best-fit power of 2
     * @since 14.09.25
     */
    public static int getNearestPowerOf2(int n) {
        int val = 1;
        while (val < n) {
            val <<= 1;
        }
        return val;
    }

    public ByteBuffer wrapFloat(
            final List<Float> data, final int offset, final int length) {
        
        final int neededSize = data.size() << 2;
        final ByteBuffer out = ByteBuffer.allocateDirect(neededSize)
                .order(ByteOrder.nativeOrder());

        if(data instanceof RandomAccess) {
            for(int i = 0; i < length; i++) {
                out.putFloat(data.get(offset + i));
            }
        } else {
            final Iterator<Float> it = data.iterator();
            
            for(int i = 0; i < offset; i++) {
                it.next();
            }
            
            for(int i = 0; i < length; i++) {
                out.putFloat(it.next());
            }
        }
        
        
        out.flip();

        return out;
    }

    public static ByteBuffer wrapFloat(
            final float[] data, final int offset, final int length) {
        
        final int neededSize = data.length << 2;
        final ByteBuffer out = ByteBuffer.allocateDirect(neededSize)
                .order(ByteOrder.nativeOrder());

        for(int i = 0; i < length; i++) {
            out.putFloat(data[offset + i]);
        }

        out.flip();
        return out;
    }

    public static ByteBuffer wrapInt(
            final List<Integer> data, final int offset, final int length) {

        final int neededSize = data.size() << 2;
        final ByteBuffer out = ByteBuffer.allocateDirect(neededSize)
                .order(ByteOrder.nativeOrder());

        if (data instanceof RandomAccess) {
            for (int i = 0; i < length; i++) {
                out.putInt(data.get(offset + i));
            }
        } else {
            final Iterator<Integer> it = data.iterator();

            for (int i = 0; i < offset; i++) {
                it.next();
            }
            for (int i = 0; i < length; i++) {
                out.putInt(it.next());
            }
        }

        out.flip();

        return out;
    }

    public static ByteBuffer wrapInt(
            final int[] data, final int offset, final int length) {

        final int neededSize = data.length << 2;
        final ByteBuffer out = ByteBuffer.allocateDirect(neededSize)
                .order(ByteOrder.nativeOrder());

        for (int i = 0; i < length; i++) {
            out.putInt(data[offset + i]);
        }

        out.flip();
        return out;
    }

    public static ByteBuffer wrapShort(
            final List<Short> data, final int offset, final int length) {

        final int neededSize = data.size() << 1;
        final ByteBuffer out = ByteBuffer.allocateDirect(neededSize)
                .order(ByteOrder.nativeOrder());

        if (data instanceof RandomAccess) {
            for (int i = 0; i < length; i++) {
                out.putShort(data.get(offset + i));
            }
        } else {
            final Iterator<Short> it = data.iterator();

            for (int i = 0; i < offset; i++) {
                it.next();
            }

            for (int i = 0; i < length; i++) {
                out.putShort(it.next());
            }
        }

        out.flip();
        return out;
    }

    public static ByteBuffer wrapShort(
            final short[] data, final int offset, final int length) {

        final int neededSize = data.length << 1;
        final ByteBuffer out = ByteBuffer.allocateDirect(neededSize)
                .order(ByteOrder.nativeOrder());

        for (int i = 0; i < length; i++) {
            out.putShort(data[offset + i]);
        }

        out.flip();
        return out;
    }

    public static ByteBuffer wrapByte(
            final List<Byte> data, final int offset, final int length) {

        final ByteBuffer out = ByteBuffer.allocateDirect(data.size())
                .order(ByteOrder.nativeOrder());

        if (data instanceof RandomAccess) {
            for (int i = 0; i < length; i++) {
                out.put(data.get(i + offset));
            }
        } else {
            final Iterator<Byte> it = data.iterator();

            // skip over the offset amount
            for (int i = 0; i < offset; i++) {
                it.next();
            }
            for (int i = 0; i < length; i++) {
                out.put(it.next());
            }
        }

        out.flip();
        return out;
    }

    public static ByteBuffer wrapByte(
            final byte[] data, final int offset, final int length) {

        final ByteBuffer out = ByteBuffer.allocateDirect(length)
                .order(ByteOrder.nativeOrder());

        for (int i = 0; i < length; i++) {
            out.put(data[offset + i]);
        }

        out.flip();
        return out;
    }
}
