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

import com.longlinkislong.gloop.alspi.Source;
import com.longlinkislong.gloop.alspi.Driver;
import java.util.Objects;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * An object that wraps the OpenAL source object. A source object represents
 * where a sound originates.
 *
 * @author zmichaels
 * @since 16.03.21
 */
public class ALSource {

    private static final Logger LOGGER = LoggerFactory.getLogger("ALSource");

    static {
        NativeTools.getInstance().loadNatives();
    }

    private Source source;
    private float gain = 1f;
    private float pitch = 1f;
    private final GLVec3F position = GLVec3F.create().asStaticVec();
    private final GLVec3F velocity = GLVec3F.create().asStaticVec();
    private boolean isLooping = false;
    private boolean isPlaying = false;
    private float referenceDistance = 1f;
    private float rolloffFactor = 1f;
    private float maxDistance = Float.MAX_VALUE;
    private float coneInnerAngle = 360f;
    private float coneOuterAngle = 360f;
    private float coneOuterGain = 0f;
    

    /**
     * Constructs a new ALSource object. This will initialize the OpenAL
     * handles.
     *
     * @since 16.03.21
     */
    public ALSource() {
        new InitTask().alRun();
    }

    /**
     * Checks if the ALSource object is valid. ALSource objects can only play
     * when the source handles report that it is valid.
     *
     * @return true if the ALSource object is ready to be used.
     * @since 16.03.21
     */
    public boolean isValid() {
        return source != null && source.isValid();
    }
    
    public void setDistance(final float referenceDistance, final float rolloffFactor, final float maxDistance) {
        new SetDistanceTask(referenceDistance, rolloffFactor, maxDistance).alRun();        
    }
    
    public final class SetDistanceTask extends ALTask {
        private final float referenceDistance;
        private final float maxDistance;
        private final float rolloffFactor;
        
        public SetDistanceTask(final float referenceDistance, final float rolloffFactor, final float maxDistance) {
            if(!Float.isFinite(referenceDistance)) {
                throw new ALException("Reference distance must be a number!");
            } else if(!Float.isFinite(rolloffFactor)) {
                throw new ALException("Rolloff factor must be a number!");
            } else if(!Float.isFinite(maxDistance)) {
                throw new ALException("Max distance must be a number!");
            } else if(referenceDistance < 0) {
                throw new ALException("Reference distance cannot be less than 0!");
            } else if(rolloffFactor < 0F) {
                throw new ALException("Rolloff factor cannot be less than 0!");
            } else if(maxDistance < 0F) {
                throw new ALException("Max distance cannot be less than 0!");
            } else {
                this.referenceDistance = referenceDistance;
                this.rolloffFactor = rolloffFactor;
                this.maxDistance = maxDistance;
            }
        }

        @Override
        public void run() {
            if(!isValid()) {
                throw new ALException("ALSource is not valid!");
            } else {                
                ALTools.getDriverInstance().sourceSetDistance(
                                source, 
                                this.referenceDistance, 
                                this.rolloffFactor, 
                                this.maxDistance);
                ALSource.this.referenceDistance = this.referenceDistance;
                ALSource.this.rolloffFactor = this.rolloffFactor;
                ALSource.this.maxDistance = this.maxDistance;
            }
        }
    }
    
    public float getReferenceDistance() {
        return this.referenceDistance;
    }
    
    public float getRolloffFactor() {
        return this.rolloffFactor;
    }
    
    public float getMaxDistance() {
        return this.maxDistance;
    }
    
    public float getInnerConeAngle() {
        return this.coneInnerAngle;
    }
    
    public float getOuterConeAngle() {
        return this.coneOuterAngle;
    }
    
    public float getOuterConeGain() {
        return this.coneOuterGain;
    }
    
    public void setCone(final float innerAngle, final float outerAngle, final float outerGain) {
        new SetConeTask(innerAngle, outerAngle, outerGain).alRun();        
    }
    
    public final class SetConeTask extends ALTask {
        private final float innerConeAngle;
        private final float outerConeAngle;
        private final float outerConeGain;
        
        public SetConeTask(final float innerAngle, final float outerAngle, final float outerGain) {
            if(!Float.isNaN(innerAngle)) {
                throw new ALException("Inner angle cannot be NaN!");
            } else if(!Float.isNaN(outerAngle)) {
                throw new ALException("Outer angle cannot be NaN!");
            } else if(outerGain < 0F) {
                throw new ALException("Outer gain cannot be less than 0.0!");
            } else if(outerGain > 1F) {
                throw new ALException("Outer gain cannot be greater than 1.0!");
            } else {
                this.innerConeAngle = innerAngle;
                this.outerConeAngle = outerAngle;
                this.outerConeGain = outerGain;
            }
        }

        @Override
        public void run() {
            if(!isValid()) {
                throw new ALException("ALSource is not valid!");
            }
            
            ALTools.getDriverInstance().sourceSetCone(source, innerConeAngle, outerConeAngle, outerConeGain);
            ALSource.this.coneInnerAngle = this.innerConeAngle;
            ALSource.this.coneOuterAngle = this.outerConeAngle;
            ALSource.this.coneOuterGain = this.outerConeGain;
        }
    }

    /**
     * An OpenAL task that initializes the ALSource object. It is recommended
     * not to call this method more than the original implicit call. However, it
     * is possible to recycle an ALSource object by calling this task after
     * deletion.
     *
     * @since 16.03.21
     */
    public final class InitTask extends ALTask {

        @Override
        public void run() {
            if (isValid()) {
                throw new ALException("ALSource is already initialized!");
            } else {
                source = ALTools.getDriverInstance().sourceCreate();
            }
        }
    }

    /**
     * Deletes the ALSource object. This method will do nothing if the OpenAL
     * source handle is not valid. The ALSource object will be considered
     * invalid after the task is executed.
     *
     * @since 16.03.21
     */
    public void delete() {
        new DeleteTask().alRun();
    }

    /**
     * An OpenAL task that deletes the resources allocated by the ALSource
     * object. The ALSource object will be considered invalid after execution of
     * this task. This task will log a warning if called multiple times.
     *
     * @since 16.03.21
     */
    public final class DeleteTask extends ALTask {

        @SuppressWarnings("unchecked")
        @Override
        public void run() {
            if (isValid()) {
                ALTools.getDriverInstance().sourceDelete(source);
                source = null;
            } else {
                LOGGER.warn("ALSource.DeleteTask called when ALSource object is not valid!");
            }
        }
    }

    /**
     * Sets the current buffer for the ALSource object.
     *
     * @param buffer the buffer object.
     * @since 16.03.21
     */
    public void setBuffer(final ALBuffer buffer) {
        new SetBufferTask(buffer).alRun();
    }

    /**
     * An ALTask that assigns an ALBuffer to the ALSource object.
     *
     * @since 16.03.21
     */
    public final class SetBufferTask extends ALTask {

        private final ALBuffer buffer;

        /**
         * Constructs a new SetBufferTask.
         *
         * @param buffer the ALBuffer to assign.
         * @since 16.03.21
         */
        public SetBufferTask(final ALBuffer buffer) {
            this.buffer = Objects.requireNonNull(buffer);
        }

        @SuppressWarnings("unchecked")
        @Override
        public void run() {
            if (isValid()) {
                if (buffer.isValid()) {
                    ALTools.getDriverInstance().sourceSetBuffer(source, buffer.buffer);
                } else {
                    throw new ALException("Invalid buffer!");
                }
            } else {
                throw new ALException("Invalid source!");
            }
        }
    }

    /**
     * Retrieves the pitch of the ALSource object. The returned value may
     * contain an obsolete value since no synchronization is used.
     *
     * @return the pitch.
     * @since 16.03.21
     */
    public float getPitch() {
        return ALSource.this.pitch;
    }

    /**
     * Sets the pitch for the ALSource object.
     *
     * @param pitch the pitch value.
     * @since 16.03.21
     */
    public void setPitch(final float pitch) {
        new SetPitchTask(pitch).alRun();
    }

    /**
     * An OpenAL task that sets the pitch of the ALSource object.
     *
     * @since 16.03.21
     */
    public final class SetPitchTask extends ALTask {

        private final float pitch;

        /**
         * Constructs a new SetPitchTask.
         *
         * @param pitch the pitch to set.
         * @since 16.03.21
         */
        public SetPitchTask(final float pitch) {
            if (!Float.isFinite(pitch)) {
                throw new ALException("Pitch must be a finite value on the range [0.0, \u221E)!");
            } else if (pitch <= 0.0F) {
                LOGGER.warn("Attempted to set pitch less than or equal to 0.0!");
                this.pitch = 0.0F;
            } else {
                this.pitch = pitch;
            }
        }

        @SuppressWarnings("unchecked")
        @Override
        public void run() {
            if (isValid()) {
                ALSource.this.pitch = pitch;
                ALTools.getDriverInstance().sourceSetPitch(source, pitch);
            } else {
                throw new ALException("Invalid pitch!");
            }
        }
    }

    /**
     * Retrieves the gain for the ALSource. This may return an obsolete value
     * due to not using synchronization.
     *
     * @return the gain.
     * @since 16.03.21
     */
    public float getGain() {
        return ALSource.this.gain;
    }

    /**
     * Sets the gain for the ALSource object.
     *
     * @param gain the gain.
     * @since 16.03.21
     */
    public void setGain(final float gain) {
        new SetGainTask(gain).alRun();
    }

    /**
     * An ALTask that sets the gain for the ALSource.
     *
     * @since 16.03.21
     */
    public final class SetGainTask extends ALTask {

        private final float gain;

        public SetGainTask(final float gain) {
            if (!Float.isFinite(gain)) {
                throw new ALException("Gain must be finite on the range [0.0, \u221E)!");
            } else if (gain < 0.0F) {
                LOGGER.warn("Attempted to set gain below 0.0!");
                this.gain = 0.0F;
            } else {
                this.gain = gain;
            }
        }

        @SuppressWarnings("unchecked")
        @Override
        public void run() {
            if (isValid()) {
                ALSource.this.gain = gain;
                ALTools.getDriverInstance().sourceSetGain(source, gain);
            } else {
                throw new ALException("Invalid source!");
            }
        }
    }

    /**
     * Retrieves the position of the ALSource object. The returned value may be
     * obsolete due to lack of synchronization.
     *
     * @return the position of the ALSource.
     * @since 16.03.21
     */
    public GLVec3F getPosition() {
        return ALSource.this.position.copyTo();
    }

    /**
     * Sets the position of the ALSource object.
     *
     * @param x the x-position of the source.
     * @param y the y-position of the source.
     * @param z the z-position of the source.
     * @since 16.03.21
     */
    public void setPosition(final float x, final float y, final float z) {
        new SetPositionTask(x, y, z).alRun();
    }

    /**
     * Sets the position of the ALSource object.
     *
     * @param pos the vector position of the source.
     * @since 16.03.21
     */
    public void setPosition(final GLVec3 pos) {
        new SetPositionTask(Objects.requireNonNull(pos)).alRun();
    }

    /**
     * An ALTask that sets the position of the ALSource object.
     *
     * @since 16.03.21
     */
    public final class SetPositionTask extends ALTask {

        private final float x;
        private final float y;
        private final float z;

        /**
         * Constructs a new SetPositionTask.
         *
         * @param pos the position to set.
         * @since 16.03.21
         */
        public SetPositionTask(final GLVec3 pos) {
            final GLVec3F vPos = pos.asGLVec3F();

            this.x = vPos.x();
            this.y = vPos.y();
            this.z = vPos.z();
        }

        /**
         * Constructs a new SetPositionTask.
         *
         * @param x the x-position.
         * @param y the y-position.
         * @param z the z-position.
         * @since 16.03.21
         */
        public SetPositionTask(final float x, final float y, final float z) {
            this.x = x;
            this.y = y;
            this.z = z;
        }

        @SuppressWarnings("unchecked")
        @Override
        public void run() {
            if (isValid()) {
                ALSource.this.position.set(this.x, this.y, this.z);
                ALTools.getDriverInstance().sourceSetPosition(source, x, y, z);
            } else {
                throw new ALException("Invalid position!");
            }
        }
    }

    /**
     * Retrieves the velocity of the ALSource. The value returned may be
     * obsolete due to lack of synchronization.
     *
     * @return the velocity.
     * @since 16.03.21
     */
    public GLVec3F getVelocity() {
        return ALSource.this.velocity.copyTo();
    }

    /**
     * Sets the velocity of the ALSource.
     *
     * @param x the x-position of the velocity.
     * @param y the y-position of the velocity.
     * @param z the z-position of the velocity.
     * @since 16.03.21
     */
    public void setVelocity(final float x, final float y, final float z) {
        new SetVelocityTask(x, y, z).alRun();
    }

    /**
     * Sets the velocity of the ALSource object.
     *
     * @param vec the velocity vector.
     * @since 16.03.21
     */
    public void setVelocity(final GLVec3 vec) {
        new SetVelocityTask(Objects.requireNonNull(vec)).alRun();
    }

    /**
     * An ALTask that sets the velocity of the ALSource object.
     *
     * @since 16.03.21
     */
    public final class SetVelocityTask extends ALTask {

        private final float x;
        private final float y;
        private final float z;

        /**
         * Constructs a new SetVelocityTask.
         *
         * @param vel the velocity vector.
         * @since 16.03.21
         */
        public SetVelocityTask(final GLVec3 vel) {
            final GLVec3F vVel = vel.asGLVec3F();

            this.x = vVel.x();
            this.y = vVel.y();
            this.z = vVel.z();
        }

        /**
         * Constructs a new SetVelocityTask.
         *
         * @param x the x-position.
         * @param y the y-position.
         * @param z the z-position.
         * @since 16.03.21
         */
        public SetVelocityTask(final float x, final float y, final float z) {
            this.x = x;
            this.y = y;
            this.z = z;
        }

        @SuppressWarnings("unchecked")
        @Override
        public void run() {
            if (isValid()) {
                ALSource.this.velocity.set(this.x, this.y, this.z);
                ALTools.getDriverInstance().sourceSetVelocity(source, x, y, z);
            } else {
                throw new ALException("Invalid source!");
            }
        }
    }

    /**
     * Checks if the ALSource is looping its current buffer.
     *
     * @return true if the ALSource is looping.
     * @since 16.03.21
     */
    public boolean isLooping() {
        return ALSource.this.isLooping;
    }

    /**
     * Sets the ALSource to loop the ALBuffer object assigned to it.
     *
     * @param shouldLoop true if the ALSource should loop.
     * @since 16.03.21
     */
    public void setLooping(final boolean shouldLoop) {
        new SetLoopingTask(shouldLoop).alRun();
    }

    /**
     * An ALTask that sets the looping status for the ALSource.
     *
     * @since 16.03.21
     */
    public final class SetLoopingTask extends ALTask {

        private final boolean shouldLoop;

        /**
         * Constructs a new SetLoopingTask.
         *
         * @param shouldLoop true if the ALSource should loop.
         * @since 16.03.21
         */
        public SetLoopingTask(final boolean shouldLoop) {
            this.shouldLoop = shouldLoop;
        }

        @SuppressWarnings("unchecked")
        @Override
        public void run() {
            if (isValid()) {
                ALSource.this.isLooping = this.shouldLoop;
                ALTools.getDriverInstance().sourceSetLooping(source, shouldLoop);
            } else {
                throw new ALException("Invalid source!");
            }
        }
    }

    /**
     * Checks if the ALSource object is playing.
     *
     * @return true if the ALSource is playing.
     * @since 16.03.21
     */
    public boolean isPlaying() {
        return ALSource.this.isPlaying;
    }

    /**
     * Plays the ALSource.
     *
     * @since 16.03.21
     */
    public void play() {
        new PlayTask().alRun();
    }

    /**
     * An ALTask that plays the ALSource object.
     *
     * @since 16.03.21
     */
    public final class PlayTask extends ALTask {

        @SuppressWarnings("unchecked")
        @Override
        public void run() {
            if (isValid()) {
                ALSource.this.isPlaying = true;
                ALTools.getDriverInstance().sourcePlay(source);
            } else {
                throw new ALException("Invalid source!");
            }
        }
    }

    /**
     * Retrieves all ALBuffers that have finished processing.
     *
     * @return the array of ALBuffers.
     * @since 16.03.21
     */
    public ALBuffer[] dequeueBuffers() {
        return new DequeueBuffersQuery().alCall();
    }

    /**
     * An ALQuery that retrieves all ALBuffers that have finished being
     * processed.
     *
     * @since 16.03.21
     */
    public final class DequeueBuffersQuery extends ALQuery<ALBuffer[]> {

        @SuppressWarnings({"unchecked", "rawtypes"})
        @Override
        public ALBuffer[] call() throws Exception {
            if (isValid()) {
                final Driver driver = ALTools.getDriverInstance();
                final int processed = driver.sourceGetBuffersProcessed(source);
                final ALBuffer[] buffers = new ALBuffer[processed];

                for (int i = 0; i < processed; i++) {
                    buffers[i] = new ALBuffer(driver.sourceDequeueBuffer(source));
                }

                return buffers;
            } else {
                throw new ALException("ALSource is not valid!");
            }
        }
    }

    /**
     * Queues a set of buffers for processing.
     *
     * @param buffers the buffers to process.
     * @since 16.03.21
     */
    public void enqueueBuffers(final ALBuffer... buffers) {
        this.enqueueBuffers(buffers, 0, buffers.length);
    }

    /**
     * Queues a range of buffers for processing.
     *
     * @param buffers the array of buffers to read the buffers from.
     * @param offset the offset to begin reading from the array.
     * @param length the number of buffers to read.
     * @since 16.03.21
     */
    public void enqueueBuffers(final ALBuffer[] buffers, final int offset, final int length) {
        new EnqueueBuffersTask(buffers, offset, length).alRun();
    }

    /**
     * An ALTask that enqueues a range of buffers for processing.
     *
     * @since 16.03.21
     */
    public final class EnqueueBuffersTask extends ALTask {

        private final ALBuffer[] buffers;

        /**
         * Constructs a new EnqueueBuffersTask.
         *
         * @param buffers the array of buffers to read from.
         * @param offset the offset to begin reading from the array of buffers.
         * @param length the number of buffers to read from the array.
         * @since 16.03.21
         */
        public EnqueueBuffersTask(final ALBuffer[] buffers, final int offset, final int length) {
            this.buffers = new ALBuffer[length];
            System.arraycopy(buffers, offset, this.buffers, 0, length);
        }

        @SuppressWarnings("unchecked")
        @Override
        public void run() {
            if (isValid()) {
                for (ALBuffer buffer : buffers) {
                    if (buffer.isValid()) {
                        ALTools.getDriverInstance().sourceEnqueueBuffer(source, buffer.buffer);
                    } else {
                        throw new ALException("Invalid ALBuffer!");
                    }
                }
            } else {
                throw new ALException("Invalid ALSource!");
            }
        }
    }

    @Override
    public boolean equals(Object other) {
        if (other instanceof ALSource) {
            final ALSource oSource = (ALSource) other;

            if (this.isValid() && oSource.isValid()) {
                return oSource.source.equals(oSource.source);
            } else {
                return false;
            }
        } else {
            return false;
        }
    }

    @Override
    public int hashCode() {
        if (this.source == null) {
            return 0;
        } else {
            int hash = 5;
            hash = 79 * hash + Objects.hashCode(this.source);
            return hash;
        }
    }
}
