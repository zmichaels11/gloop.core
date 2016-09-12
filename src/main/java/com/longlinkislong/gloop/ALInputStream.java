/* 
 * Copyright (c) 2016, longlinkislong.com
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

import java.io.Closeable;
import java.io.EOFException;
import java.io.IOException;
import java.io.InputStream;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.ShortBuffer;
import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.UnsupportedAudioFileException;
import org.lwjgl.system.MemoryUtil;

/**
 * ALInputStream is an adapter for java InputStream objects to a form that uses
 * ALBuffers for reading in data.
 *
 * @author zmichaels
 * @since 16.03.21
 */
public class ALInputStream implements Closeable {

    private static final int DEFAULT_BUFFER_SIZE = Integer.getInteger("com.longlinkislong.gloop.alinputstream.buffer_size", 16 * 1024);
    private final AudioInputStream ain;
    public final ALSoundFormat format;
    private final boolean is16bit;
    public final int sampleRate;
    public final ByteOrder byteOrder;
    public final int bufferSize;

    /**
     * Constructs a new ALInputStream with the default buffer size.
     *
     * @param in the InputStream to wrap. Must support mark.
     * @throws UnsupportedAudioFileException if the audio format is not
     * supported.
     * @throws IOException if the InputStream cannot be opened.
     * @since 16.03.21
     */
    public ALInputStream(final InputStream in) throws UnsupportedAudioFileException, IOException {
        this(in, DEFAULT_BUFFER_SIZE);
    }

    /**
     * Constructs a new ALInputStream.
     *
     * @param in the InputStream to wrap. Must support mark.
     * @param bufferSize the buffer size to use.
     * @throws UnsupportedAudioFileException if the audio format is not
     * supported.
     * @throws IOException if the InputStream cannot be opened.
     * @since 16.03.21
     */
    public ALInputStream(final InputStream in, final int bufferSize) throws UnsupportedAudioFileException, IOException {
        this.bufferSize = bufferSize;
        this.ain = AudioSystem.getAudioInputStream(in);

        final AudioFormat fmt = this.ain.getFormat();
        final int channels = fmt.getChannels();
        final int sampleSize = fmt.getSampleSizeInBits();

        switch (channels) {
            case 1:
                switch (sampleSize) {
                    case 8:
                        this.format = ALSoundFormat.AL_FORMAT_MONO8;
                        this.is16bit = false;
                        break;
                    case 16:
                        this.format = ALSoundFormat.AL_FORMAT_MONO16;
                        this.is16bit = true;
                        break;
                    default:
                        throw new UnsupportedOperationException("Unsupported audio format: [MONO" + sampleSize + "]");
                }
                break;
            case 2:
                switch (sampleSize) {
                    case 8:
                        this.format = ALSoundFormat.AL_FORMAT_STEREO8;
                        this.is16bit = false;
                        break;
                    case 16:
                        this.format = ALSoundFormat.AL_FORMAT_STEREO16;
                        this.is16bit = true;
                        break;
                    default:
                        throw new UnsupportedOperationException("Unsupported audio format: [STEREO" + sampleSize + "]");
                }
                break;
            default:
                throw new UnsupportedOperationException("Unsupported channel count: " + channels);
        }

        this.byteOrder = fmt.isBigEndian() ? ByteOrder.BIG_ENDIAN : ByteOrder.LITTLE_ENDIAN;
        this.sampleRate = (int) fmt.getSampleRate();
    }

    @Override
    public void close() throws IOException {
        this.ain.close();
    }

    /**
     * Reads up to the buffer size from the internal InputStream into the
     * supplied ALBuffer.
     *
     * @param buffer the ALBuffer to write the data to.
     * @throws EOFException if end of stream has been reached.
     * @throws IOException if an error occurred while reading from the internal
     * InputStream.
     * @since 16.03.21
     */
    public void stream(final ALBuffer buffer) throws IOException {
        final ByteBuffer outBuffer = MemoryUtil.memAlloc(this.bufferSize).order(ByteOrder.nativeOrder());

        final byte[] inBuffer = new byte[bufferSize];
        final int readCount = this.ain.read(inBuffer, 0, this.bufferSize);

        if (readCount == -1) {
            throw new EOFException("End of stream reached!") {
                @Override
                public Throwable fillInStackTrace() {
                    return this;
                }
            };
        }

        final ByteBuffer readBuffer = ByteBuffer.wrap(inBuffer).order(this.byteOrder);

        if (this.is16bit) {
            final ShortBuffer src = readBuffer.asShortBuffer();
            final ShortBuffer dst = outBuffer.asShortBuffer();

            dst.put(src);
        } else {
            outBuffer.put(readBuffer);
        }

        outBuffer.position(0).limit(readCount);
        buffer.upload(this.format, outBuffer, this.sampleRate);
        ALTask.create(() -> MemoryUtil.memFree(outBuffer)).alRun(); // free on the OpenAL thread
    }
}
