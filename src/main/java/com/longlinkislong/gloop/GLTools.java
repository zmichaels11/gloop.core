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

import com.longlinkislong.gloop.dsa.ARBDSA;
import com.longlinkislong.gloop.dsa.DSADriver;
import com.longlinkislong.gloop.dsa.EXTDSA;
import com.longlinkislong.gloop.dsa.FakeDSA;
import com.longlinkislong.gloop.dsa.GL45DSA;
import com.longlinkislong.gloop.dsa.NoDSA;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;
import java.nio.IntBuffer;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.ListIterator;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.Marker;
import org.slf4j.MarkerFactory;

/**
 * A collection of functions that can aid in OpenGL programs.
 *
 * @author zmichaels
 * @since 15.05.27
 */
public class GLTools {

    private static final Logger LOGGER = LoggerFactory.getLogger("GLTools");
    private static final Marker GLOOP_MARKER = MarkerFactory.getMarker("GLOOP");
    private static final Marker SYS_MARKER = MarkerFactory.getMarker("SYSTEM");

    /**
     * Red component offset for vectors
     *
     * @since 15.06.13
     */
    public static final int R = 0;
    /**
     * Green component offset for vectors
     *
     * @since 15.06.13
     */
    public static final int G = 1;
    /**
     * Blue component offset for vectors
     *
     * @since 15.06.13
     */
    public static final int B = 2;
    /**
     * Alpha component offset for vectors
     *
     * @since 15.06.13
     */
    public static final int A = 3;
    /**
     * X component offset for vectors
     *
     * @since 15.06.13
     */
    public static final int X = 0;
    /**
     * Y component offset for vectors
     *
     * @since 15.06.13
     */
    public static final int Y = 1;
    /**
     * Z component offset for vectors
     *
     * @since 15.06.13
     */
    public static final int Z = 2;
    /**
     * W component offset for vectors
     *
     * @since 15.06.13
     */
    public static final int W = 3;
    /**
     * Width offset for vectors
     *
     * @since 15.06.13
     */
    public static final int WIDTH = 0;
    /**
     * Height offset for vectors
     *
     * @since 15.06.13
     */
    public static final int HEIGHT = 1;
    /**
     * Depth offset for vectors
     *
     * @since 15.06.13
     */
    public static final int DEPTH = 2;

    /**
     * Machine epsilon equivalent to 'mediump' from GLSL.
     *
     * @since 15.06.13
     */
    public static final double MEDIUMP = 9.77e-04;
    /**
     * Machine epsilon equivalent to 'highp' from GLSL.
     *
     * @since 15.06.13
     */
    public static final double HIGHP = 1.19e-07;
    /**
     * Machine epsilon supported by 64bit floats
     *
     * @since 15.06.13
     */
    public static final double SUPERP = 2.22e-16;

    /**
     * Machine epsilon supported by some FPUs
     *
     * @since 15.06.13
     */
    public static final double ULTRAP = 1.08e-19;

    /**
     * Constant used to convert radians to degrees
     *
     * @since 15.06.13
     */
    public static final double RADIANS_TO_DEGREES = 180.0 / Math.PI;
    /**
     * Constant used to convert degrees to radians
     *
     * @since 15.06.13
     */
    public static final double DEGREES_TO_RADIANS = Math.PI / 180.0;

    public static double getTime() {
        return GLTools.getTime();
    }

    /**
     * Returns the value clamped to the range [min, max].
     *
     * @param value the value to clamp.
     * @param min the minimum value.
     * @param max the maximum value.
     * @return the clamped value.
     * @since 15.07.22
     */
    public static int clamp(int value, int min, int max) {
        return Math.min(Math.max(value, min), max);
    }

    /**
     * Returns the value clamped to the range [min, max].
     *
     * @param value the value to clamp.
     * @param min the minimum value.
     * @param max the maximum value.
     * @return the clamped value.
     * @since 15.07.22
     */
    public static long clamp(long value, long min, long max) {
        return Math.min(Math.max(value, min), max);
    }

    /**
     * Returns the value clamped to the range [min, max].
     *
     * @param value the value to clamp.
     * @param min the minimum value.
     * @param max the maximum value.
     * @return the clamped value.
     * @since 15.07.22
     */
    public static float clamp(float value, float min, float max) {
        return Math.min(Math.max(value, min), max);
    }

    /**
     * Returns the value clamped to the range [min, max].
     *
     * @param value the value to clamp.
     * @param min the minimum value.
     * @param max the maximum value.
     * @return the clamped value.
     * @since 15.07.22
     */
    public static double clamp(double value, double min, double max) {
        return Math.min(Math.max(value, min), max);
    }

    /**
     * Checks if the buffer can be used for OpenGL calls. OpenGL requires that
     * the buffer is direct and native order.
     *
     * @param data the buffer to check
     * @return the ByteBuffer
     * @throws GLException if buffer is not direct.
     * @throws GLException if buffer is not in native byte order.
     * @since 15.06.13
     */
    public static ByteBuffer checkBuffer(final ByteBuffer data) {
        if (!data.isDirect()) {
            throw new GLException("ByteBuffer is not direct!");
        } else if (data.order() != ByteOrder.nativeOrder()) {
            throw new GLException("ByteBuffer is not in native order!");
        }

        return data;
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
     * @param <VecT> the vector type
     * @param data the data to wrap
     * @return the ByteBuffer
     * @since 15.05.27
     */
    public static <VecT extends GLVec2> ByteBuffer wrapVec2F(final List<VecT> data) {
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

        final int neededSize = data.size() * GLVec2F.VECTOR_WIDTH;
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

        final int neededSize = data.size() * GLVec3F.VECTOR_WIDTH;
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

    /**
     * Wraps a segment of an array of floats as a ByteBuffer ready for OpenGL.
     *
     * @param data the data to wrap.
     * @param offset the offset to start reading the data from the array.
     * @param length the number of floats to read from the array.
     * @return the ByteBuffer.
     * @since 15.06.13
     */
    public static ByteBuffer wrapFloat(
            final List<Float> data, final int offset, final int length) {

        final int neededSize = data.size() * Float.BYTES;
        final ByteBuffer out = ByteBuffer.allocateDirect(neededSize)
                .order(ByteOrder.nativeOrder());
        final ListIterator<Float> li = data.listIterator(offset);

        for (int i = 0; i < length; i++) {
            out.putFloat(li.next());
        }

        out.flip();

        return out;
    }

    /**
     * Wraps a series of vectors as a ByteBuffer ready for OpenGL.
     *
     * @param <VecT> the type of vector.
     * @param vecs the vectors
     * @return the ByteBuffer
     * @since 15.06.13
     */
    @SuppressWarnings("unchecked")
    public static <VecT extends GLVec2> ByteBuffer wrapVec2F(final VecT... vecs) {
        return wrapVec2F(vecs, 0, vecs.length);
    }

    /**
     * Wraps a segment of an array of vectors as a ByteBuffer ready for OpenGL.
     *
     * @param <VecT> the type of vector.
     * @param data the array of vectors
     * @param offset the offset to start reading from the array
     * @param length the number of vectors to read
     * @return the ByteBuffer
     * @since 15.06.13
     */
    public static <VecT extends GLVec2> ByteBuffer wrapVec2F(
            final VecT[] data, final int offset, final int length) {

        final int neededSize = data.length * GLVec2F.VECTOR_WIDTH;
        final ByteBuffer out = ByteBuffer.allocateDirect(neededSize)
                .order(ByteOrder.nativeOrder());

        for (int i = 0; i < length; i++) {
            final GLVec2F vec = data[offset + i].asGLVec2F();

            out.putFloat(vec.x());
            out.putFloat(vec.y());
        }

        out.flip();
        return out;
    }

    /**
     * Wraps a series of vectors as a ByteBuffer ready for OpenGL.
     *
     * @param <VecT> the type of vector
     * @param vecs the series of vectors
     * @return the ByteBuffer
     * @since 15.06.13
     */
    @SuppressWarnings("unchecked")
    public static <VecT extends GLVec3> ByteBuffer wrapVec3F(final VecT... vecs) {
        return wrapVec3F(vecs, 0, vecs.length);
    }

    /**
     * Wraps a segment of an array of vectors to a ByteBuffer ready for OpenGL.
     *
     * @param <VecT> the type of vector
     * @param data the vector array
     * @param offset the offset to start reading from the array
     * @param length the number of elements to read from the array
     * @return the bytebuffer
     * @since 15.06.13
     */
    public static <VecT extends GLVec3> ByteBuffer wrapVec3F(
            final VecT[] data, final int offset, final int length) {

        final int neededSize = data.length * GLVec3F.VECTOR_WIDTH;
        final ByteBuffer out = ByteBuffer.allocateDirect(neededSize)
                .order(ByteOrder.nativeOrder());

        for (int i = 0; i < length; i++) {
            final GLVec3F vec = data[offset + i].asGLVec3F();

            out.putFloat(vec.x());
            out.putFloat(vec.y());
            out.putFloat(vec.z());
        }

        out.flip();

        return out;
    }

    /**
     * Wraps a series of vectors as a ByteBuffer ready for OpenGL.
     *
     * @param <VecT> the type of vector to wrap.
     * @param vecs the vectors to wrap.
     * @return the ByteBuffer
     * @since 15.06.13
     */
    @SuppressWarnings("unchecked")
    public static <VecT extends GLVec4> ByteBuffer wrapVec4F(final VecT... vecs) {
        return wrapVec4F(vecs, 0, vecs.length);
    }

    /**
     * Wraps a segment of an array of vectors to a ByteBuffer ready for OpenGL.
     *
     * @param <VecT> the type of vector to wrap
     * @param data the data
     * @param offset the offset to start reading from the array
     * @param length the number of elements to read
     * @return the ByteBuffer.
     * @since 15.06.13
     */
    public static <VecT extends GLVec4> ByteBuffer wrapVec4F(
            final VecT[] data, final int offset, final int length) {

        final int neededSize = data.length * GLVec4F.VECTOR_WIDTH;
        final ByteBuffer out = ByteBuffer.allocateDirect(neededSize)
                .order(ByteOrder.nativeOrder());

        for (int i = 0; i < length; i++) {
            final GLVec4F vec = data[offset + i].asGLVec4F();

            out.putFloat(vec.x());
            out.putFloat(vec.y());
            out.putFloat(vec.z());
            out.putFloat(vec.w());
        }

        out.flip();

        return out;
    }

    /**
     * Wraps a series of floats
     *
     * @param values the floats
     * @return the ByteBuffer
     * @since 15.06.13
     */
    public static ByteBuffer wrapFloat(final float... values) {
        return wrapFloat(values, 0, values.length);
    }

    /**
     * Wraps a segment of an array of floats as a ByteBuffer ready for OpenGL.
     *
     * @param data the array
     * @param offset the offset to start reading from the array
     * @param length the number of floats to read
     * @return the ByteBuffer
     * @since 15.06.13
     */
    public static ByteBuffer wrapFloat(
            final float[] data, final int offset, final int length) {

        final int neededSize = data.length * Float.BYTES;
        final ByteBuffer out = ByteBuffer.allocateDirect(neededSize)
                .order(ByteOrder.nativeOrder());

        for (int i = 0; i < length; i++) {
            out.putFloat(data[offset + i]);
        }

        out.flip();
        return out;
    }

    /**
     * Wraps a series of integers as a ByteBuffer ready for OpenGL.
     *
     * @param values the integers
     * @return the ByteBuffer
     * @since 15.06.13
     */
    public static ByteBuffer wrapInt(final int... values) {
        return wrapInt(values, 0, values.length);
    }

    /**
     * Wraps a list of Integers as a ByteBuffer ready for OpenGL.
     *
     * @param data the list of integers
     * @return the ByteBuffer
     * @since 15.06.13
     */
    public static ByteBuffer wrapInt(final List<Integer> data) {
        return wrapInt(data, 0, data.size());
    }

    /**
     * Wraps a segment of a list of integers to a ByteBuffer ready for OpenGL.
     *
     * @param data the list of integers
     * @param offset the offset to start reading from the list of integers.
     * @param length the number of integers to read.
     *
     * @return the ByteBuffer
     * @since 15.06.13
     */
    public static ByteBuffer wrapInt(
            final List<Integer> data, final int offset, final int length) {

        final int neededSize = data.size() * Integer.BYTES;
        final ByteBuffer out = ByteBuffer.allocateDirect(neededSize)
                .order(ByteOrder.nativeOrder());
        final ListIterator<Integer> li = data.listIterator(offset);

        for (int i = 0; i < length; i++) {
            out.putInt(li.next());
        }

        out.flip();

        return out;
    }

    /**
     * Wraps a segment of an array of integers to a ByteBuffer ready for OpenGL.
     *
     * @param data the array of integers
     * @param offset the offset to start reading from the array
     * @param length the number of integers to read.
     * @return the ByteBuffer
     * @since 15.06.13
     */
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

    /**
     * Wraps a series of shorts to a ByteBuffer ready for OpenGL.
     *
     * @param values the series of shorts
     * @return the ByteBuffer
     * @since 15.06.13
     */
    public static ByteBuffer wrapShort(final short... values) {
        return wrapShort(values, 0, values.length);
    }

    /**
     * Wraps a list of shorts as a ByteBuffer ready for OpenGL.
     *
     * @param data the list of shorts
     * @return the ByteBuffer
     * @since 15.06.13
     */
    public static ByteBuffer wrapShort(final List<Short> data) {
        return wrapShort(data, 0, data.size());
    }

    /**
     * Wraps a segment of a list of shorts as a ByteBuffer ready for OpenGL.
     *
     * @param data the list of shorts
     * @param offset the offset to start reading from the list of shorts
     * @param length the number of shorts to read
     * @return the ByteBuffer
     * @since 15.06.13
     */
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

    /**
     * Wraps a segment of an array of shorts as a ByteBuffer ready for OpenGL.
     *
     * @param data the array of shorts
     * @param offset the offset to start reading from the array
     * @param length the number of shorts to read
     * @return the ByteBuffer
     * @since 15.06.13
     */
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

    /**
     * Wraps a series of bytes as a ByteBuffer ready for OpenGL.
     *
     * @param values the shorts
     * @return the ByteBuffer
     * @since 15.06.13
     */
    public static ByteBuffer wrapByte(final byte... values) {
        return wrapByte(values, 0, values.length);
    }

    /**
     * Wraps a list of bytes as a ByteBuffer ready for OpenGL.
     *
     * @param data the list of bytes
     * @return the ByteBuffer
     * @since 15.06.13
     */
    public static ByteBuffer wrapByte(final List<Byte> data) {
        return wrapByte(data, 0, data.size());
    }

    /**
     * Wraps a segment of a list of bytes as a ByteBuffer ready for OpenGL.
     *
     * @param data the list of bytes
     * @param offset the offset to start reading from the list
     * @param length the number of elements to read from the list
     * @return the ByteBuffer
     * @since 15.06.13
     */
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

    /**
     * WRaps a segment of an array of bytes as a ByteBuffer ready for OpenGL.
     *
     * @param data the array of bytes
     * @param offset the offset to start reading
     * @param length the number of bytes to read.
     * @return the ByteBuffer
     */
    public static ByteBuffer wrapByte(
            final byte[] data, final int offset, final int length) {

        final ByteBuffer out = ByteBuffer.allocateDirect(length)
                .order(ByteOrder.nativeOrder());

        out.put(data, offset, length);

        out.flip();
        return out;
    }

    public static ByteBuffer writeByte(final ByteBuffer dst, final List<Byte> data) {
        return writeByte(dst, data, 0, data.size());
    }

    public static ByteBuffer writeByte(
            final ByteBuffer dst,
            final List<Byte> data, final int offset, final int length) {

        final ByteBuffer out = dst == null || dst.capacity() < length
                ? ByteBuffer.allocateDirect(length).order(ByteOrder.nativeOrder())
                : dst;
        final ListIterator<Byte> it = data.listIterator(offset);

        out.clear();
        for (int i = 0; i < length; i++) {
            out.put(it.next());
        }
        out.flip();

        return out;
    }

    public static ByteBuffer writeByte(final ByteBuffer dst, final byte... values) {
        return writeByte(dst, values, 0, values.length);
    }

    public static ByteBuffer writeByte(
            final ByteBuffer dst,
            final byte[] data, final int offset, final int length) {

        final ByteBuffer out = dst == null || dst.capacity() < length
                ? ByteBuffer.allocateDirect(length).order(ByteOrder.nativeOrder())
                : dst;

        out.clear();
        out.put(data, offset, length);
        out.flip();

        return out;
    }

    public static ByteBuffer writeShort(final ByteBuffer dst, final List<Short> data) {
        return writeShort(dst, data, 0, data.size());
    }

    public static ByteBuffer writeShort(
            final ByteBuffer dst,
            final List<Short> data, final int offset, final int length) {

        final int bytes = length * Short.BYTES;
        final ByteBuffer out = dst == null || dst.capacity() < bytes
                ? ByteBuffer.allocateDirect(bytes).order(ByteOrder.nativeOrder())
                : dst;
        final ListIterator<Short> it = data.listIterator(offset);

        out.clear();
        for (int i = 0; i < length; i++) {
            out.putShort(it.next());
        }
        out.flip();

        return out;
    }

    public static ByteBuffer writeShort(final ByteBuffer dst, final short... values) {
        return writeShort(dst, values, 0, values.length);
    }

    public static ByteBuffer writeShort(
            final ByteBuffer dst,
            final short[] data, final int offset, final int length) {

        final int bytes = length * Short.BYTES;
        final ByteBuffer out = dst == null || dst.capacity() < bytes
                ? ByteBuffer.allocateDirect(bytes).order(ByteOrder.nativeOrder())
                : dst;

        out.clear();
        for (int i = 0; i < length; i++) {
            out.putShort(data[offset + i]);
        }
        out.flip();

        return out;
    }

    public static ByteBuffer writeInt(final ByteBuffer dst, final int... values) {
        return writeInt(dst, values, 0, values.length);
    }

    public static ByteBuffer writeInt(
            final ByteBuffer dst,
            final int[] data, final int offset, final int length) {

        final int bytes = length * Integer.BYTES;
        final ByteBuffer out = dst == null || dst.capacity() < bytes
                ? ByteBuffer.allocateDirect(bytes).order(ByteOrder.nativeOrder())
                : dst;

        out.clear();
        for (int i = 0; i < length; i++) {
            out.putInt(data[offset + i]);
        }
        out.flip();

        return out;
    }

    public static ByteBuffer writeFloat(final ByteBuffer dst, final List<Float> data) {
        return writeFloat(dst, data, 0, data.size());
    }

    public static ByteBuffer writeFloat(
            final ByteBuffer dst,
            final List<Float> data, final int offset, final int length) {

        final int bytes = length * Integer.BYTES;
        final ByteBuffer out = dst == null || dst.capacity() < bytes
                ? ByteBuffer.allocateDirect(bytes).order(ByteOrder.nativeOrder())
                : dst;
        final ListIterator<Float> it = data.listIterator(offset);

        out.clear();
        for (int i = 0; i < length; i++) {
            out.putFloat(it.next());
        }
        out.flip();
        return out;
    }

    public static ByteBuffer writeFloat(final ByteBuffer dst, final float... values) {
        return writeFloat(dst, values, 0, values.length);
    }

    public static ByteBuffer writeFloat(
            final ByteBuffer dst,
            final float[] data, final int offset, final int length) {

        final int bytes = length * Float.BYTES;
        final ByteBuffer out = dst == null || dst.capacity() < bytes
                ? ByteBuffer.allocateDirect(bytes).order(ByteOrder.nativeOrder())
                : dst;

        out.clear();
        for (int i = 0; i < length; i++) {
            out.putFloat(data[offset + i]);
        }
        out.flip();

        return out;
    }

    public static ByteBuffer writeVec2F(final ByteBuffer dst, final List<GLVec2> data) {
        return writeVec2F(dst, data, 0, data.size());
    }

    public static ByteBuffer writeVec2F(
            final ByteBuffer dst,
            final List<GLVec2> data, final int offset, final int length) {

        final int bytes = length * GLVec2F.VECTOR_WIDTH;
        final ByteBuffer out = dst == null || dst.capacity() < bytes
                ? ByteBuffer.allocateDirect(bytes).order(ByteOrder.nativeOrder())
                : dst;
        final ListIterator<GLVec2> it = data.listIterator(offset);

        out.clear();
        for (int i = 0; i < length; i++) {
            final GLVec2F vec = it.next().asGLVec2F();

            out.putFloat(vec.x());
            out.putFloat(vec.y());
        }
        out.flip();
        return out;
    }

    public static ByteBuffer writeVec2F(final ByteBuffer dst, final GLVec2... values) {
        return writeVec2F(dst, values, 0, values.length);
    }

    public static ByteBuffer writeVec2F(
            final ByteBuffer dst,
            final GLVec2[] data, final int offset, final int length) {

        final int bytes = length * GLVec2F.VECTOR_WIDTH;
        final ByteBuffer out = dst == null || dst.capacity() < bytes
                ? ByteBuffer.allocateDirect(bytes).order(ByteOrder.nativeOrder())
                : dst;

        out.clear();
        for (int i = 0; i < length; i++) {
            final GLVec2F vec = data[offset + i].asGLVec2F();

            out.putFloat(vec.x());
            out.putFloat(vec.y());
        }
        out.flip();

        return out;
    }

    public static ByteBuffer writeVec3F(final ByteBuffer dst, final List<GLVec3> data) {
        return writeVec3F(dst, data, 0, data.size());
    }

    public static ByteBuffer writeVec3F(
            final ByteBuffer dst,
            final List<GLVec3> data, final int offset, final int length) {

        final int bytes = length * GLVec3F.VECTOR_WIDTH;
        final ByteBuffer out = dst == null || dst.capacity() < bytes
                ? ByteBuffer.allocateDirect(bytes).order(ByteOrder.nativeOrder())
                : dst;
        final ListIterator<GLVec3> it = data.listIterator(offset);

        out.clear();
        for (int i = 0; i < length; i++) {
            final GLVec3F vec = it.next().asGLVec3F();

            out.putFloat(vec.x());
            out.putFloat(vec.y());
            out.putFloat(vec.z());
        }
        out.flip();

        return out;
    }

    public static ByteBuffer writeVec3F(final ByteBuffer dst, final GLVec3... values) {
        return writeVec3F(dst, values, 0, values.length);
    }

    public static ByteBuffer writeVec3F(
            final ByteBuffer dst,
            final GLVec3[] data, final int offset, final int length) {

        final int bytes = length * GLVec3F.VECTOR_WIDTH;
        final ByteBuffer out = dst == null || dst.capacity() < bytes
                ? ByteBuffer.allocateDirect(bytes).order(ByteOrder.nativeOrder())
                : dst;

        out.clear();
        for (int i = 0; i < length; i++) {
            final GLVec3F vec = data[offset + i].asGLVec3F();

            out.putFloat(vec.x());
            out.putFloat(vec.y());
            out.putFloat(vec.z());
        }
        out.flip();

        return out;
    }

    public static ByteBuffer writeVec4F(final ByteBuffer dst, final List<GLVec4> data) {
        return writeVec4F(dst, data, 0, data.size());
    }

    public static ByteBuffer writeVec4F(
            final ByteBuffer dst,
            final List<GLVec4> data, final int offset, final int length) {

        final int bytes = length * GLVec4F.VECTOR_WIDTH;
        final ByteBuffer out = dst == null || dst.capacity() < bytes
                ? ByteBuffer.allocateDirect(bytes).order(ByteOrder.nativeOrder())
                : dst;
        final ListIterator<GLVec4> it = data.listIterator(offset);

        for (int i = 0; i < length; i++) {
            final GLVec4F vec = it.next().asGLVec4F();

            out.putFloat(vec.x());
            out.putFloat(vec.y());
            out.putFloat(vec.z());
            out.putFloat(vec.w());
        }
        out.flip();

        return out;
    }

    public static ByteBuffer writeVec4F(final ByteBuffer dst, final GLVec4... values) {
        return writeVec4F(dst, values, 0, values.length);
    }

    public static ByteBuffer writeVec4F(
            final ByteBuffer dst,
            final GLVec4[] data, final int offset, final int length) {

        final int bytes = length * GLVec4F.VECTOR_WIDTH;
        final ByteBuffer out = dst == null || dst.capacity() < bytes
                ? ByteBuffer.allocateDirect(bytes).order(ByteOrder.nativeOrder())
                : dst;

        out.clear();
        for (int i = 0; i < length; i++) {
            final GLVec4F vec = data[offset + i].asGLVec4F();

            out.putFloat(vec.x());
            out.putFloat(vec.y());
            out.putFloat(vec.z());
            out.putFloat(vec.w());
        }
        out.flip();

        return out;
    }

    /**
     * Reads from an InputStream line by line and returns the entire source.
     *
     * @param src the InputStream to read from.
     * @return the text read from the InputStream.
     * @throws IOException if the source cannot be read.
     * @since 15.07.08
     */
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

    /**
     * Checks if two floats are less than delta apart.
     *
     * @param a the first float
     * @param b the second float
     * @param delta the error for being considered equal.
     * @return true if the floats have a difference less than delta.
     * @since 15.06.13
     */
    public static boolean compare(final float a, final float b, final float delta) {
        return Math.abs(a - b) < delta;
    }

    /**
     * Checks if two doubles are less than delta apart.
     *
     * @param a the first double
     * @param b the second double
     * @param delta the error for being considered equal.
     * @return true if the doubles have a difference less than delta.
     * @since 15.06.13
     */
    public static boolean compare(final double a, final double b, final double delta) {
        return Math.abs(a - b) < delta;
    }

    /**
     * Converts a FloatBuffer into a verbose String.
     *
     * @param buffer the buffer to convert.
     * @return the String describing the FloatBuffer
     * @since 15.07.06
     */
    public static String FloatBufferToString(final FloatBuffer buffer) {
        final StringBuilder out = new StringBuilder();

        if (buffer == null) {
            out.append("FloatBuffer: [null]");
            return out.toString();
        }

        out.append("FloatBuffer: [pos: ");
        out.append(buffer.position());
        out.append(" limit: ");
        out.append(buffer.limit());
        out.append(" capacity: ");
        out.append(buffer.capacity());
        out.append(" order: ");
        out.append(buffer.order());
        out.append(" direct: ");
        out.append(buffer.isDirect());
        out.append(" data: {");

        for (int i = buffer.position(); i < buffer.limit(); i++) {
            out.append(buffer.get(i));

            if (i < buffer.limit() - 1) {
                out.append(", ");
            }
        }

        out.append("}]");
        return out.toString();
    }

    /**
     * Converts an IntBuffer into a verbose String.
     *
     * @param buffer the buffer to convert.
     * @return the String describing the IntBuffer.
     * @since 15.07.06
     */
    public static String IntBufferToString(final IntBuffer buffer) {
        final StringBuilder out = new StringBuilder();

        if (buffer == null) {
            out.append("IntBuffer: [null]");
            return out.toString();
        }

        out.append("IntBuffer: [pos: ");
        out.append(buffer.position());
        out.append(" limit: ");
        out.append(buffer.limit());
        out.append(" capacity: ");
        out.append(buffer.capacity());
        out.append(" order: ");
        out.append(buffer.order());
        out.append(" direct: ");
        out.append(buffer.isDirect());
        out.append(" data: {");

        for (int i = buffer.position(); i < buffer.limit(); i++) {
            out.append(buffer.get(i));

            if (i < buffer.limit() - 1) {
                out.append(", ");
            }
        }

        out.append("}]");

        return out.toString();
    }

    /**
     * Converts a 32bit fixed-point to a 16bit fixed-point.
     *
     * @param value the 32bit.
     * @return the 16bit.
     * @since 15.07.09
     */
    public static int fx32fx16(int value) {
        final int uint16 = value & 0xFFFF;
        final double scale = uint16 / (double) 0xFFFFFFFF;
        return (int) (0xFFFF * scale) & 0xFFFF;
    }

    /**
     * Converts a 16bit fixed-point to a 12bit fixed-point.
     *
     * @param value the 16bit.
     * @return the 12bit.
     * @since 15.07.09
     */
    public static int fx16fx12(int value) {
        final int uint16 = value & 0xFFFF;
        final double scale = uint16 / (double) 0xFFFF;
        return (int) (0xFFF * scale) & 0xFFF;
    }

    /**
     * Converts a 16bit fixed-point to an 11bit fixed-point.
     *
     * @param value the 16bit.
     * @return the 10bit.
     * @since 15.07.09
     */
    public static int fx16fx11(int value) {
        final int uint16 = value & 0xFFFF;
        final double scale = uint16 / (double) 0xFFFF;
        return (int) (0x7FF * scale) & 0x7FF;
    }

    /**
     * Converts a 16bit fixed-point to a 10bit fixed-point.
     *
     * @param value the 16bit.
     * @return the 10bit.
     * @since 15.07.09
     */
    public static int fx16fx10(int value) {
        final int uint16 = value & 0xFFFF;
        final double scale = uint16 / (double) 0xFFFF;
        return (int) (0x3FF * scale) & 0x3FF;
    }

    /**
     * Converts a 16bit fixed-point to a 9bit fixed-point.
     *
     * @param value the 16bit.
     * @return the 9bit.
     * @since 15.07.09
     */
    public static int fx16fx9(int value) {
        final int uint16 = value & 0xFFFF;
        final double scale = uint16 / (double) 0xFFFF;
        return (int) (0x1FF * scale) & 0x1FF;
    }

    /**
     * Converts a 16bit fixed-point to an 8bit fixed-point.
     *
     * @param value the 16bit.
     * @return the 8bit.
     * @since 15.07.09
     */
    public static int fx16fx8(int value) {
        final int uint16 = value & 0xFFFF;
        final double scale = uint16 / (double) 0xFFFF;
        return (int) (0xFF * scale) & 0xFF;
    }

    /**
     * Converts an 8bit fixed-point to a 6bit fixed-point.
     *
     * @param value the 8bit.
     * @return the 6bit.
     * @since 15.07.09
     */
    public static int fx8fx6(int value) {
        final int uint8 = value & 0xFF;
        final double scale = uint8 / (double) 0xFF;
        return (int) (0x3F * scale) & 0x3F;
    }

    /**
     * Converts an 8bit fixed-point to a 5bit fixed-point.
     *
     * @param value the 8bit.
     * @return the 5bit.
     * @since 15.07.09
     */
    public static int fx8fx5(int value) {
        final int uint8 = value & 0xFF;
        final double scale = uint8 / (double) 0xFF;
        return (int) (0x1F * scale) & 0x1F;
    }

    /**
     * Converts an 8bit fixed-point to a 4bit fixed-point.
     *
     * @param value the 8bit
     * @return the 4bit.
     * @since 15.07.09
     */
    public static int fx8fx4(int value) {
        final int uint8 = value & 0xFF;
        final double scale = uint8 / (double) 0xFF;
        return (int) (0xF * scale) & 0xF;
    }

    /**
     * Converts an 8bit fixed-point to a 3bit fixed-point.
     *
     * @param value the 8bit.
     * @return the 3bit.
     * @since 15.07.09
     */
    public static int fx8fx3(int value) {
        final int uint8 = value & 0xFF;
        final double scale = uint8 / (double) 0xFF;
        return (int) (0x7 * scale) & 0x7;
    }

    /**
     * Converts an 8bit fixed-point to a 2bit fixed-point.
     *
     * @param value the 8bit.
     * @return the 2bit.
     */
    public static int fx8fx2(int value) {
        final int uint8 = value & 0xFF;
        final double scale = uint8 / (double) 0xFF;
        return (int) (0x3 * scale) & 0x3;
    }

    private static final VendorQuery VENDOR_QUERY = new VendorQuery();
    /**
     * Constant for describing an AMD GPU.
     *
     * @since 15.07.09
     */
    public static final String GPU_AMD = "AMD";
    /**
     * Constant for describing an NVidia GPU.
     *
     * @since 15.07.09
     */
    public static final String GPU_NVIDIA = "NVIDIA";

    /**
     * Constant for describing an Intel GPU.
     *
     * @since 15.07.09
     */
    public static final String GPU_INTEL = "INTEL";

    /**
     * Checks if the specified OpenGL version is supported on the context of the
     * specified OpenGL thread.
     *
     * @param version the version to check.
     * @param thread the OpenGL to get the context from.
     * @return true if the context supports the specified version.
     * @since 15.07.09
     */
    public static boolean hasOpenGLVersion(final int version, GLThread thread) {
        return newOpenGLVersionQuery(version).glCall(thread);
    }

    /**
     * Checks if the specified OpenGL version is supported.
     *
     * @param version the version to check.
     * @return true if it is supported.
     * @since 15.07.09
     */
    public static boolean hasOpenGLVersion(final int version) {
        return newOpenGLVersionQuery(version).glCall();
    }

    /**
     * Constructs a new GLQuery that checks if the requested version of OpenGL
     * is supported.
     *
     * @param version the version to check.
     * @return true if the version is supported.
     * @since 15.07.09
     */
    public static GLQuery<Boolean> newOpenGLVersionQuery(final int version) {
        return GLQuery.create(GLTools::_hasOpenGLVersion, version);
    }

    private static boolean _hasOpenGLVersion(final int version) {
        return GLTools.getDSAInstance().getOpenGLVersion() >= version;
    }

    /**
     * Checks if the vendor is AMD.
     *
     * @return true if the GPU is an AMD card.
     * @since 15.07.09
     */
    public static boolean isGPUAmd() {
        return getVendor().equals(GPU_AMD);
    }

    /**
     * Checks if the vendor is NVidia.
     *
     * @return true if the GPU is an NVidia card.
     * @since 15.07.09
     */
    public static boolean isNVidia() {
        return getVendor().equals(GPU_NVIDIA);
    }

    /**
     * Checks if the vendor is Intel.
     *
     * @return true if the GPU is an Intel card.
     * @since 15.07.09
     */
    public static boolean isIntel() {
        return getVendor().equals(GPU_INTEL);
    }

    /**
     * Retrieves the name of the GPU vendor.
     *
     * @return the vendor name.
     * @since 15.07.09
     */
    public static String getVendor() {
        if (VendorQuery.isSet()) {
            return VendorQuery.VENDOR;
        } else {
            return VENDOR_QUERY.glCall(GLThread.getDefaultInstance());
        }
    }

    /**
     * A GLTask that checks the GPU vendor.
     *
     * @since 15.07.09
     */
    public static class VendorQuery extends GLQuery<String> {

        private static String VENDOR = null;

        private static boolean isSet() {
            return VENDOR != null;
        }

        @Override
        public String call() throws Exception {
            LOGGER.trace(GLOOP_MARKER, "Querying OpenGL vendor...");

            if (isSet()) {
                final String rawVendor = GLTools.getDSAInstance()
                        .glGetString(7936 /* GL11.GL_VENDOR */)
                        .toLowerCase();

                if (rawVendor.contains("amd")) {
                    VENDOR = GPU_AMD;
                } else if (rawVendor.contains("intel")) {
                    VENDOR = GPU_INTEL;
                } else if (rawVendor.contains("nvidia")) {
                    VENDOR = GPU_NVIDIA;
                } else {
                    VENDOR = rawVendor;
                }
            }

            return VENDOR;
        }

    }

    /**
     * Checks if the given value is a power of 2.
     *
     * @param value the value to test.
     * @return true if it is roughly a power of 2.
     * @since 15.07.07
     */
    public static boolean isPowerOf2(double value) {
        final double val = Math.log(value) / Math.log(2);

        return GLTools.compare(val, Math.floor(val), GLTools.HIGHP);
    }

    /**
     * Estimates the number of mipmaps that should be generated for the
     * specified texture.
     *
     * @param width the width of the image
     * @param height the height of the image
     * @return the recommended number of mipmaps.
     * @since 15.07.07
     */
    public static int recommendedMipmaps(int width, int height) {
        final int sz = Math.min(width, height);

        if (isPowerOf2(sz)) {
            final int recommended = (int) (Math.log(sz) / Math.log(2)) - 1;

            return Math.max(recommended, 1);
        } else {
            return 1;
        }
    }

    private static DSADriver DSA = null;
    private static final DSADriver[] DSA_IMPLEMENTATIONS;

    /**
     * Retrieves the current DSADriver.
     *
     * @return the DSADriver.
     * @since 15.07.13
     */
    public static DSADriver getDSAInstance() {
        if (DSA == null) {
            LOGGER.trace(SYS_MARKER, "Scanning for DSA implementations...");
            for (DSADriver dsaImp : DSA_IMPLEMENTATIONS) {
                if (dsaImp.isSupported()) {
                    DSA = dsaImp;
                    break;
                }
            }

            LOGGER.debug(SYS_MARKER, "Using DSADriver: {}", getDSAImplement());
            if(DSA == NoDSA.getInstance()) {
                LOGGER.warn(SYS_MARKER, "Using NoDSA implementation is potentially unsafe!");
            }
        }

        return DSA;
    }

    /**
     * Calculates an estimate on how many bytes are needed for a volume of
     * pixels.
     *
     * @param type the data type. Expects [UNSIGNED]_[BYTE | SHORT | INT] |
     * FLOAT
     * @param format the color format. Expects type that matches GLTextureFormat
     * @param width the width of the pixel volume. Must be at least 1.
     * @param height the height of the pixel volume. Must be at least 1.
     * @param depth the depth of the pixel volume. Must be at least 1.
     * @return the estimated size of the volume. -1 will be returned on
     * calculation error.
     * @since 15.09.23
     */
    public static int pixelSize(int width, int height, int depth, int format, int type) {
        if (width < 1 || height < 1 || depth < 1) {
            return -1;
        }

        final Optional<GLType> glType = GLType.of(type);
        final int tSize;

        if (glType.isPresent()) {
            switch (glType.get()) {
                case GL_UNSIGNED_BYTE_3_3_2:
                case GL_UNSIGNED_BYTE_2_3_3_REV:
                    return width * height * depth * Byte.BYTES;
                case GL_UNSIGNED_SHORT_5_6_5:
                case GL_UNSIGNED_SHORT_5_6_5_REV:
                case GL_UNSIGNED_SHORT_4_4_4_4:
                case GL_UNSIGNED_SHORT_4_4_4_4_REV:
                case GL_UNSIGNED_SHORT_5_5_5_1:
                case GL_UNSIGNED_SHORT_1_5_5_5_REV:
                    return width * height * depth * Short.BYTES;
                case GL_UNSIGNED_INT_8_8_8_8:
                case GL_UNSIGNED_INT_8_8_8_8_REV:
                case GL_UNSIGNED_INT_10_10_10_2:
                case GL_UNSIGNED_INT_2_10_10_10_REV:
                    return width * height * depth * Integer.BYTES;
                default:
                    tSize = glType.get().width;
            }
        } else {
            return -1;
        }

        final Optional<GLTextureFormat> glFormat = GLTextureFormat.of(format);
        final int fSize;

        if (glFormat.isPresent()) {
            fSize = glFormat.get().size;
        } else {
            return -1;
        }

        return (width * height * depth) * fSize * tSize;
    }

    /**
     * Retrieves the name of the implementation for Direct State Access that is
     * currently being used.
     *
     * @return the DSA name.
     * @since 15.07.09
     */
    public static String getDSAImplement() {
        return getDSAInstance().toString();
    }

    static {
        NativeTools.getInstance().autoLoad();

        LOGGER.trace(SYS_MARKER, "Checking for DSA override...");
        // check for driver override.
        final String dsa = System.getProperty("gloop.gltools.dsa", "");
        switch (dsa.toLowerCase()) {
            case "fakedsa":
            case "fake":
                LOGGER.info(SYS_MARKER, "Forcing DSA driver: FakeDSA");
                DSA = FakeDSA.getInstance();
                break;
            case "extdsa":
            case "ext":
                LOGGER.info(SYS_MARKER, "Forcing DSA driver: EXTDSA");
                DSA = EXTDSA.getInstance();
                break;
            case "gl45dsa":
            case "gl45":
                LOGGER.info(SYS_MARKER, "Forcing DSA driver: GL45DSA");
                DSA = GL45DSA.getInstance();
                break;
            case "arbdsa":
            case "arb":
                LOGGER.info(SYS_MARKER, "Forcing DSA driver: ARBDSA");
                DSA = ARBDSA.getInstance();
                break;
            case "nodsa":
            case "no":
                LOGGER.info(SYS_MARKER, "Forcing DSA driver: NoDSA");
                DSA = NoDSA.getInstance();
                break;
            case "":
                break;
            default:
                LOGGER.warn(SYS_MARKER, "Unknown DSA driver: {}!", dsa);
        }

        LOGGER.trace(SYS_MARKER, "Scanning for DSA driver plugins...");
        // check for all plugins
        final List<Class<? extends DSADriver>> dsaDrivers = new ArrayList<>();
        final String dsaPlugins = System.getProperty("gloop.gltools.dsa.plugins", "");
        final List<String> plugins = new ArrayList<>();

        // populate the known plugins with the default plugins.
        Stream
                .of(GL45DSA.class, ARBDSA.class, FakeDSA.class)
                .map(Class::getName)
                .forEach(plugins::add);

        // detect user-specified plugins
        Arrays
                .stream(dsaPlugins.split(","))
                .map(String::trim)
                .filter(String::isEmpty)
                .forEach(plugins::add);

        // find the implementations.
        for (String plugin : plugins) {
            if (plugin == null || plugin.isEmpty()) {
                continue;
            }

            LOGGER.trace(SYS_MARKER, "Adding DSADriver: {}", plugin);

            try {
                final Class<?> dsaPlugin = Class.forName(plugin);

                dsaDrivers.add(dsaPlugin.asSubclass(DSADriver.class));
            } catch (ClassNotFoundException ex) {
                throw new RuntimeException("Could not find DSA driver: " + plugin, ex);
            }
        }

        LOGGER.trace(SYS_MARKER, "Removing blacklisted DSA drivers...");
        // remove all plugins that are blacklisted.
        final String dsaBlacklist = System.getProperty("gloop.gltools.dsa.blacklist", "");
        final String[] blacklisted = dsaBlacklist.split(",");

        for (String blacklistedDSA : blacklisted) {
            if (blacklistedDSA.trim().isEmpty()) {
                continue;
            }

            LOGGER.trace(SYS_MARKER, "Removing DSADriver: {}", blacklistedDSA);

            Optional<Class<?>> blacklistedDSADriver = Optional.empty();

            for (Class<?> driver : dsaDrivers) {
                if (driver.getName().toLowerCase().endsWith(blacklistedDSA.toLowerCase())) {
                    blacklistedDSADriver = Optional.of(driver);
                    break;
                }
            }

            blacklistedDSADriver.ifPresent(dsaDrivers::remove);
        }

        // construct the array of available DSADrivers from the list of known plugins
        DSA_IMPLEMENTATIONS = dsaDrivers.stream().map(def -> {
            try {
                LOGGER.trace(SYS_MARKER, "Loading driver: {}", def.getName());

                final Method singletonGetter = def.getMethod("getInstance");

                return singletonGetter.invoke(null);
            } catch (IllegalAccessException | IllegalArgumentException | InvocationTargetException | NoSuchMethodException ex) {
                throw new RuntimeException("Unable to initialize DSA Driver: " + def.getName(), ex);
            }
        }).collect(Collectors.toList()).toArray(new DSADriver[dsaDrivers.size()]);

        try {
            assert false;
            LOGGER.debug(SYS_MARKER, "OpenGL assertions are disabled.");
        } catch (Throwable ex) {
            LOGGER.info(SYS_MARKER, "OpenGL assertions are enabled!");
        }
    }
}
