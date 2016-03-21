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

import com.longlinkislong.gloop.alspi.Buffer;
import java.nio.ByteBuffer;
import java.util.Objects;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * ALBuffer represents data that can be sent to an OpenAL device.
 *
 * @author zmichaels
 * @since 16.03.21
 */
public class ALBuffer {

    private static final Logger LOGGER = LoggerFactory.getLogger("ALBuffer");

    static {
        NativeTools.getInstance().loadNatives();
    }

    Buffer buffer;

    /**
     * Constructs a new ALBuffer. This will also initialize the handles.
     *
     * @since 16.03.21
     */
    public ALBuffer() {
        new InitTask().alRun();
    }

    /**
     * Constructs a new ALBuffer with the supplied buffer handle.
     *
     * @param buffer the buffer handle.
     * @since 16.03.21
     */
    protected ALBuffer(final Buffer buffer) {
        this.buffer = buffer;
    }

    /**
     * Uploads the ALBuffer to the OpenAL device.
     *
     * @param format the format to upload the data as.
     * @param data the data.
     * @param frequency the frequency of the data.
     * @since 16.03.21
     */
    public void upload(final ALSoundFormat format, final ByteBuffer data, final int frequency) {
        new UploadTask(format, data, frequency).alRun();
    }

    /**
     * Deletes the ALBuffer. This will also mark the ALBuffer as invalid
     *
     * @since 16.03.21
     */
    public void delete() {
        new DeleteTask().alRun();
    }

    /**
     * Checks if the ALBuffer is valid.
     *
     * @return true if the ALBuffer is valid.
     * @since 16.03.21
     */
    public boolean isValid() {
        return buffer != null && buffer.isValid();
    }

    /**
     * An ALTask that uploads the ALBuffer.
     *
     * @since 16.03.21
     */
    public final class UploadTask extends ALTask {

        private final ByteBuffer dataBuffer;
        private final int format;
        private final int frequency;

        /**
         * Constructs a new UploadTask.
         *
         * @param format the format of the data.
         * @param data the data.
         * @param frequency the frequency of the data.
         * @since 16.03.21
         */
        public UploadTask(final ALSoundFormat format, final ByteBuffer data, final int frequency) {
            this.dataBuffer = Objects.requireNonNull(data);
            this.format = format.value;
            this.frequency = frequency;
        }

        @SuppressWarnings("unchecked")
        @Override
        public void run() {
            if (isValid()) {
                ALTools.getDriverInstance().bufferSetData(buffer, format, dataBuffer, frequency);
            } else {
                throw new ALException("Invalid ALBuffer!");
            }
        }
    }

    /**
     * An ALTask that initializes the ALBuffer and allocates needed handles.
     *
     * @since 16.03.21
     */
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

    /**
     * An ALTask that deletes the backing handles and marks the ALBuffer as
     * invalid.
     *
     * @since 16.03.21
     */
    public final class DeleteTask extends ALTask {

        @SuppressWarnings("unchecked")
        @Override
        public void run() {
            if (isValid()) {
                ALTools.getDriverInstance().bufferDelete(buffer);
                buffer = null;
            } else {
                LOGGER.warn("Attempted to delete invalid ALBuffer!");
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