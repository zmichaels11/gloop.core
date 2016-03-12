/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.longlinkislong.gloop;

import com.longlinkislong.gloop.alspi.Buffer;
import java.nio.ByteBuffer;
import java.util.Objects;

/**
 *
 * @author zmichaels
 */
public class ALBuffer {
    
    static {
        NativeTools.getInstance().loadNatives();
    }

    Buffer buffer;

    public ALBuffer() {
        new InitTask().alRun();
    }

    protected ALBuffer(final Buffer buffer) {
        this.buffer = buffer;
    }

    public void upload(final ALSoundFormat format, final ByteBuffer data, final int frequency) {
        new UploadTask(format, data, frequency).alRun();
    }

    public void delete() {
        new DeleteTask().alRun();
    }

    public boolean isValid() {
        return buffer != null && buffer.isValid();
    }

    public final class UploadTask extends ALTask {

        private final ByteBuffer dataBuffer;
        private final int format;
        private final int frequency;

        public UploadTask(final ALSoundFormat format, final ByteBuffer data, final int frequency) {
            this.dataBuffer = Objects.requireNonNull(data);
            this.format = format.value;
            this.frequency = frequency;
        }

        @Override
        public void run() {
            if (isValid()) {
                ALTools.getDriverInstance().bufferSetData(buffer, format, dataBuffer, frequency);
            } else {
                throw new ALException("Invalid ALBuffer!");
            }
        }
    }

    public final class InitTask extends ALTask {

        @Override
        public void run() {
            if (isValid()) {
                throw new ALException("ALBuffer is already initialized!");
            } else {
                buffer = ALTools.getDriverInstance().bufferCreate();
            }
        }
    }

    public final class DeleteTask extends ALTask {

        @Override
        public void run() {
            if (isValid()) {
                ALTools.getDriverInstance().bufferDelete(buffer);
                buffer = null;
            }
        }
    }

    @Override
    public boolean equals(final Object other) {
        if (other instanceof ALBuffer) {
            final ALBuffer bOther = (ALBuffer) other;

            if (buffer == null || bOther.buffer == null) {
                return false;
            } else {
                return buffer.equals(bOther.buffer);
            }
        } else {
            return false;
        }
    }

    @Override
    public int hashCode() {
        if (this.buffer == null) {
            return 0;
        } else {
            int hash = 7;
            hash = 83 * hash + Objects.hashCode(this.buffer);
            return hash;
        }
    }
}
