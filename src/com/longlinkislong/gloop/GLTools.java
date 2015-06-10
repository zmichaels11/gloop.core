/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.longlinkislong.gloop;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.util.List;
import java.util.ListIterator;

/**
 * A collection of functions that can aid in OpenGL programs.
 *
 * @author zmichaels
 * @since 15.05.27
 */
public class GLTools {

    public static void checkBuffer(final ByteBuffer data) {
        if (!data.isDirect()) {
            throw new GLException("ByteBuffer is not direct!");
        } else if (data.order() != ByteOrder.nativeOrder()) {
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

    /**
     * Wraps a list of GLVec2F as a ByteBuffer ready for OpenGL.
     *
     * @param data the data to wrap
     * @return the ByteBuffer
     * @since 15.05.27
     */
    public static ByteBuffer wrapVec2F(final List<GLVec2> data) {
        return wrapVec2F(data, 0, data.size());
    }

    /**
     * Wraps a list of GLVec2F as a ByteBuffer ready for OpenGL.
     *
     * @param <VecT> the type of vector.
     * @param data the data to wrap.
     * @param offset the offset to start reading from the list.
     * @param length the number of elements to read from the list.
     * @return the ByteBuffer
     * @since 15.05.27
     */
    public static <VecT extends GLVec2> ByteBuffer wrapVec2F(
            final List<VecT> data, final int offset, final int length) {

        final int vecSize = 8;
        final int neededSize = data.size() * vecSize;
        final ByteBuffer out = ByteBuffer.allocateDirect(neededSize)
                .order(ByteOrder.nativeOrder());
        final ListIterator<VecT> li = data.listIterator(offset);

        for (int i = 0; i < length; i++) {
            final GLVec2F vec = li.next().asGLVec2F();

            out.putFloat(vec.x());
            out.putFloat(vec.y());
        }

        out.flip();
        return out;
    }

    /**
     * Wraps a list of GLVec3F as a ByteBuffer ready for OpenGL.
     *
     * @param <VecT> the type of vector.
     * @param data the data to wrap.
     * @return the ByteBuffer.
     * @since 15.05.27
     */
    public static <VecT extends GLVec3> ByteBuffer wrapVec3F(
            final List<VecT> data) {
        
        return wrapVec3F(data, 0, data.size());
    }

    /**
     * Wraps a list of GLVec3F as a ByteBuffer ready for OpenGL.
     *
     * @param <VecT> the type of vector.
     * @param data the data to wrap.
     * @param offset the offset to start reading from the list.
     * @param length the number of elements to read from the list.
     * @return the ByteBuffer
     * @since 15.05.27
     */
    public static <VecT extends GLVec3> ByteBuffer wrapVec3F(
            final List<VecT> data, final int offset, final int length) {

        final int vecSize = 12;
        final int neededSize = data.size() * vecSize;
        final ByteBuffer out = ByteBuffer.allocateDirect(neededSize)
                .order(ByteOrder.nativeOrder());

        final ListIterator<VecT> li = data.listIterator(offset);

        for (int i = 0; i < length; i++) {
            final GLVec3F vec = li.next().asGLVec3F();

            out.putFloat(vec.x());
            out.putFloat(vec.y());
            out.putFloat(vec.z());
        }

        out.flip();
        return out;
    }

    /**
     * Wraps a list of GLVec4F as a ByteBuffer ready for OpenGL.
     *
     * @param <VecT> the type of vector.
     * @param data the data to wrap.
     * @return the ByteBuffer
     * @since 15.05.27
     */
    public static <VecT extends GLVec4> ByteBuffer wrapVec4F(final List<VecT> data) {
        return wrapVec4F(data, 0, data.size());
    }

    /**
     * Wraps a list of GLVec4F as a ByteBuffer ready for OpenGL.
     *
     * @param <VecT> the type of vector.
     * @param data the data to wrap.
     * @param offset the offset to start reading from the list.
     * @param length the number of elements to read from the list.
     *
     * @return the ByteBuffer
     * @since 15.05.27
     */
    public static <VecT extends GLVec4> ByteBuffer wrapVec4F(
            final List<VecT> data, final int offset, final int length) {

        final int vecSize = 16;
        final int neededSize = data.size() * vecSize;
        final ByteBuffer out = ByteBuffer.allocateDirect(neededSize)
                .order(ByteOrder.nativeOrder());
        final ListIterator<VecT> li = data.listIterator(offset);

        for (int i = 0; i < length; i++) {
            final GLVec4F vec = li.next().asGLVec4F();

            out.putFloat(vec.x());
            out.putFloat(vec.y());
            out.putFloat(vec.z());
            out.putFloat(vec.w());
        }

        out.flip();
        return out;
    }

    public static ByteBuffer wrapFloat(
            final List<Float> data, final int offset, final int length) {

        final int neededSize = data.size() << 2;
        final ByteBuffer out = ByteBuffer.allocateDirect(neededSize)
                .order(ByteOrder.nativeOrder());
        final ListIterator<Float> li = data.listIterator(offset);

        for (int i = 0; i < length; i++) {
            out.putFloat(li.next());
        }

        out.flip();

        return out;
    }

    public static ByteBuffer wrapFloat(final float... values) {
        return wrapFloat(values, 0, values.length);
    }

    public static ByteBuffer wrapFloat(
            final float[] data, final int offset, final int length) {

        final int neededSize = data.length << 2;
        final ByteBuffer out = ByteBuffer.allocateDirect(neededSize)
                .order(ByteOrder.nativeOrder());

        for (int i = 0; i < length; i++) {
            out.putFloat(data[offset + i]);
        }

        out.flip();
        return out;
    }

    public static ByteBuffer wrapInt(final int... values) {
        return wrapInt(values, 0, values.length);
    }

    public static ByteBuffer wrapInt(
            final List<Integer> data, final int offset, final int length) {

        final int neededSize = data.size() << 2;
        final ByteBuffer out = ByteBuffer.allocateDirect(neededSize)
                .order(ByteOrder.nativeOrder());
        final ListIterator<Integer> li = data.listIterator(offset);

        for (int i = 0; i < length; i++) {
            out.putInt(li.next());
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

    public static ByteBuffer wrapShort(final short... values) {
        return wrapShort(values, 0, values.length);
    }

    public static ByteBuffer wrapShort(
            final List<Short> data, final int offset, final int length) {

        final int neededSize = data.size() << 1;
        final ByteBuffer out = ByteBuffer.allocateDirect(neededSize)
                .order(ByteOrder.nativeOrder());
        final ListIterator<Short> li = data.listIterator(offset);

        for (int i = 0; i < length; i++) {
            out.putShort(li.next());
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

    public static ByteBuffer wrapByte(final byte... values) {
        return wrapByte(values, 0, values.length);
    }

    public static ByteBuffer wrapByte(
            final List<Byte> data, final int offset, final int length) {

        final ByteBuffer out = ByteBuffer.allocateDirect(data.size())
                .order(ByteOrder.nativeOrder());
        final ListIterator<Byte> li = data.listIterator(offset);

        for (int i = 0; i < length; i++) {
            out.put(li.next());
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

    public static String readAll(final InputStream src) throws IOException {
        final StringBuilder out = new StringBuilder();

        try (final InputStream lSrc = src;
                final InputStreamReader srcReader = new InputStreamReader(lSrc);
                final BufferedReader lineReader = new BufferedReader(srcReader);) {

            lineReader.lines().map(line -> line + '\n').forEach(out::append);
        }

        return out.toString();
    }

    /**
     * Retrieves the number of bytes that 'count' instances of 'type' would
     * consume.
     *
     * @param count the number of elements.
     * @param type the type of element.
     * @return the number of bytes.
     * @since 15.05.13
     */
    public static long sizeOf(final long count, final GLType type) {
        if (count < 0) {
            throw new GLException("Count should not be less than 0!");
        }

        switch (type) {
            case GL_BYTE:
                return count;
            case GL_UNSIGNED_BYTE:
                return count;
            case GL_SHORT:
                return count * 2L;
            case GL_UNSIGNED_SHORT:
                return count * 2L;
            case GL_INT:
                return count * 4L;
            case GL_UNSIGNED_INT:
                return count * 4L;
            case GL_FLOAT:
                return count * 4L;
            case GL_DOUBLE:
                return count * 8L;
            default:
                throw new GLException("Invalid type: " + type);
        }
    }        
}
