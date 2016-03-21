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

import com.longlinkislong.gloop.alspi.Listener;
import java.util.Objects;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * ALListener represents a listener device. OpenAL has a single listener device
 * that represents the user.
 *
 * @author zmichaels
 * @since 16.03.21
 */
public final class ALListener {

    private static final Logger LOGGER = LoggerFactory.getLogger("ALListener");

    static {
        NativeTools.getInstance().loadNatives();
    }

    private final GLVec3F at = GLVec3F.create(0f, 0f, -1f).asStaticVec();
    private final GLVec3F up = GLVec3F.create(0f, 1f, 0f).asStaticVec();
    private final GLVec3F position = GLVec3F.create(0f, 0f, 0f).asStaticVec();
    private final GLVec3F velocity = GLVec3F.create(0f, 0f, 0f).asStaticVec();
    private float gain = 1f;

    private Listener listener;

    private ALListener() {
        ALTask.create(() -> listener = ALTools.getDriverInstance().listenerGetInstance()).alRun();
    }

    /**
     * Retrieves the position of the ALListener device. The return value may be
     * obsolete due to lack of synchronization.
     *
     * @return the position of the listener.
     * @since 16.03.21
     */
    public GLVec3F getPosition() {
        return position.copyTo();
    }

    /**
     * Sets the position of the listener.
     *
     * @param pos the position vector.
     * @since 16.03.21
     */
    public void setPosition(final GLVec3 pos) {
        new SetPositionTask(Objects.requireNonNull(pos)).alRun();
    }

    /**
     * Sets the position of the listener.
     *
     * @param x the x-position.
     * @param y the y-position.
     * @param z the z-position.
     * @since 16.03.21
     */
    public void setPosition(final float x, final float y, final float z) {
        new SetPositionTask(x, y, z).alRun();
    }

    /**
     * An ALTask that assigns the position of the listener.
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
         * @param position the position vector.
         * @since 16.03.21
         */
        public SetPositionTask(final GLVec3 position) {
            final GLVec3F fPos = position.asGLVec3F();

            this.x = fPos.x();
            this.y = fPos.y();
            this.z = fPos.z();
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
            if (listener.isValid()) {
                position.set(x, y, z);
                ALTools.getDriverInstance().listenerSetPosition(listener, x, y, z);
            } else {
                throw new ALException("Invalid ALListener!");
            }
        }
    }

    /**
     * Retrieves the velocity of the ALListener. The return value may be
     * obsolete due to lack of synchronization.
     *
     * @return the velocity of the ALListener.
     * @since 16.03.21
     */
    public GLVec3F getVelocity() {
        return this.velocity.copyTo();
    }

    /**
     * Sets the velocity of the ALListener.
     *
     * @param velocity the velocity vector.
     * @since 16.03.21
     */
    public void setVelocity(final GLVec3 velocity) {
        new SetVelocityTask(velocity).alRun();
    }

    /**
     * Sets the velocity of the vector.
     *
     * @param x the x-component of the velocity.
     * @param y the y-component of the velocity.
     * @param z the z-component of the velocity.
     * @since 16.03.21
     */
    public void setVelocity(final float x, final float y, final float z) {
        new SetVelocityTask(x, y, z).alRun();
    }

    /**
     * An ALTask that sets the velocity of the ALListener.
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
            final GLVec3F fVel = vel.asGLVec3F();

            this.x = fVel.x();
            this.y = fVel.y();
            this.z = fVel.z();
        }

        /**
         * Constructs a new SetVelocityTask
         *
         * @param x the x-component of the velocity.
         * @param y the y-component of the velocity.
         * @param z the z-component of the velocity.
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
            if (listener != null && listener.isValid()) {
                velocity.set(x, y, z);
                ALTools.getDriverInstance().listenerSetVelocity(listener, x, y, z);
            } else {
                throw new ALException("Invalid ALListener!");
            }
        }
    }

    /**
     * Retrieves the gain of the ALListener. This value may be obsolete due to
     * lack of synchronization.
     *
     * @return the gain.
     * @since 16.03.21
     */
    public float getGain() {
        return this.gain;
    }

    /**
     * Sets the gain of the ALListener.
     *
     * @param gain the gain.
     * @since 16.03.21
     */
    public void setGain(final float gain) {
        new SetGainTask(gain).alRun();
    }

    public final class SetGainTask extends ALTask {

        private final float gain;

        public SetGainTask(final float gain) {
            if (!Float.isFinite(gain)) {
                throw new ALException("Gain must be finite on range [0.0, 1.0]!");
            } else if (gain < 0.0F) {
                LOGGER.warn("Attempted to set gain less than 0.0!");
                this.gain = 0.0F;
            } else if (gain > 1.0F) {
                LOGGER.warn("Attempted to set gain above 1.0!");
                this.gain = 1.0F;
            } else {
                this.gain = gain;
            }
        }

        @SuppressWarnings("unchecked")
        @Override
        public void run() {
            if (listener != null && listener.isValid()) {
                ALListener.this.gain = gain;
                ALTools.getDriverInstance().listenerSetGain(listener, gain);
            } else {
                throw new ALException("Invalid ALListener!");
            }
        }
    }

    /**
     * Retrieves the at-orientation of the listener.
     *
     * @return the at-orientation.
     * @since 16.03.21
     */
    public GLVec3F getOrientationAt() {
        return at.copyTo();
    }

    /**
     * Retrieves the up-orientation of the listener.
     *
     * @return the up-orientation.
     * @since 16.03.21
     */
    public GLVec3F getOrientationUp() {
        return up.copyTo();
    }

    /**
     * Sets the orientation of the listener.
     *
     * @param at the at-vector.
     * @param up the up-vector.
     */
    public void setOrientation(final GLVec3 at, final GLVec3 up) {
        new SetOrientationTask(at, up).alRun();
    }

    /**
     * Sets the orientation of the listener.
     *
     * @param atX the x-component of the at-vector.
     * @param atY the y-component of the at-vector.
     * @param atZ the z-component of the at-vector.
     * @param upX the x-component of the up-vector.
     * @param upY the y-component of the up-vector.
     * @param upZ the z-component of the up-vector.
     * @since 16.03.21
     */
    public void setOrientation(final float atX, final float atY, final float atZ, final float upX, final float upY, final float upZ) {
        new SetOrientationTask(atX, atY, atZ, upX, upY, upZ).alRun();
    }

    /**
     * An ALTask that sets the orientation of the listener.
     *
     * @since 16.03.21
     */
    public final class SetOrientationTask extends ALTask {

        private final float upX;
        private final float upY;
        private final float upZ;
        private final float atX;
        private final float atY;
        private final float atZ;

        /**
         * Constructs a new SetOrientationTask.
         *
         * @param at the at-vector.
         * @param up the up-vector.
         * @since 16.03.21
         */
        public SetOrientationTask(final GLVec3 at, final GLVec3 up) {
            final GLVec3F fAt = at.asGLVec3F();
            final GLVec3F fUp = up.asGLVec3F();

            this.atX = fAt.x();
            this.atY = fAt.y();
            this.atZ = fAt.z();

            this.upX = fUp.x();
            this.upY = fUp.y();
            this.upZ = fUp.z();
        }

        /**
         * Constructs a new SetOrientationTask.
         *
         * @param atX the x-component of the at-vector.
         * @param atY the y-component of the at-vector.
         * @param atZ the z-component of the at-vector.
         * @param upX the x-component of the up-vector.
         * @param upY the y-component of the up-vector.
         * @param upZ the z-component of the up-vector.
         * @since 16.03.21
         */
        public SetOrientationTask(
                final float atX, final float atY, final float atZ,
                final float upX, final float upY, final float upZ) {

            this.upX = upX;
            this.upY = upY;
            this.upZ = upZ;
            this.atX = atX;
            this.atY = atY;
            this.atZ = atZ;
        }

        @SuppressWarnings("unchecked")
        @Override
        public void run() {
            if (listener != null && listener.isValid()) {
                at.set(atX, atY, atZ);
                up.set(upX, upY, upZ);

                ALTools.getDriverInstance().listenerSetOrientation(listener, atX, atY, atZ, upX, upY, upZ);
            }
        }
    }

    private static final class Holder {

        private static final ALListener INSTANCE = new ALListener();
    }

    /**
     * Retrieves the ALListener object.
     *
     * @return the ALListener object.
     * @since 16.03.21
     */
    public static ALListener getInstance() {
        return Holder.INSTANCE;
    }
}
