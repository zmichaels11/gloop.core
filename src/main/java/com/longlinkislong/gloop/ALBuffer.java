/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.longlinkislong.gloop;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.util.Objects;
import org.lwjgl.openal.AL10; 

/**
 *
 * @author zmichaels
 */
public class ALBuffer extends ALObject {

    private static final int INVALID_BUFFER_ID = -1;
    int bufferId = INVALID_BUFFER_ID;

    public ALBuffer() {
        super();

        this.init();
    }

    public ALBuffer(final ALThread thread) {
        super(thread);

        this.init();
    }

    ALBuffer(final ALThread thread, final int id) {
        super(thread);

        this.bufferId = id;
    }

    public boolean isValid() {
        return this.bufferId != INVALID_BUFFER_ID;
    }

    public final void init() {
        new InitTask().alRun(this.getThread());
    }

    public class InitTask extends ALTask {

        @Override
        public void run() {
            if (!ALBuffer.this.isValid()) {
                ALBuffer.this.bufferId = AL10.alGenBuffers();
            } else {
                throw new ALException("ALBuffer is already initialized!");
            }
        }
    }

    public void delete() {
        new DeleteTask().alRun(this.getThread());
    }

    public class DeleteTask extends ALTask {

        @Override
        public void run() {
            if (ALBuffer.this.isValid()) {
                AL10.alDeleteBuffers(ALBuffer.this.bufferId);

                ALBuffer.this.bufferId = INVALID_BUFFER_ID;
            }
        }
    }

    public class UploadTask extends ALTask {

        final ALFormatType format;
        final ByteBuffer data;
        final int rate;

        public UploadTask(final ALFormatType formatType, final ByteBuffer data, final int rate) {
            if (!data.isDirect()) {
                throw new ALException("Backing data buffer is not direct!");
            } else if (data.order() != ByteOrder.nativeOrder()) {
                throw new ALException("Data is not in native order!");
            }

            this.format = Objects.requireNonNull(formatType);
            this.data = Objects.requireNonNull(data);
            this.rate = rate;
        }

        @Override
        public void run() {
            if (!ALBuffer.this.isValid()) {
                throw new ALException("Invalid OpenAL buffer!");
            }
            AL10.alBufferData(ALBuffer.this.bufferId, format.value, data, rate);
        }

    }
}
