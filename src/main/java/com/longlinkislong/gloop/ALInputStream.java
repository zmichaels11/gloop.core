/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.longlinkislong.gloop;

import java.io.Closeable;
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
 *
 * @author zmichaels
 */
public class ALInputStream implements Closeable {

    private final AudioInputStream ain;
    public final ALSoundFormat format;  
    private final boolean is16bit;
    public final int sampleRate;
    public final ByteOrder byteOrder;
    public final int bufferSize;

    public ALInputStream(final InputStream in) throws UnsupportedAudioFileException, IOException {
        this(in, 1024 * 16);
    }

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

    public void stream(final ALBuffer buffer) throws IOException {
        final ByteBuffer outBuffer = MemoryUtil.memAlloc(this.bufferSize).order(ByteOrder.nativeOrder());
        
        final byte[] inBuffer = new byte[bufferSize];
        final int readCount = this.ain.read(inBuffer, 0, this.bufferSize);
        
        if(readCount == -1) {
            throw new IOException("End of stream reached!");
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
        ALTask.create(() -> MemoryUtil.memFree(outBuffer)); // free on the OpenAL thread
    }
}
