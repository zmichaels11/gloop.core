/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.longlinkislong.gloop;

import com.longlinkislong.gloop.alspi.Source;
import com.longlinkislong.gloop.alspi.Driver;
import java.util.Objects;

/**
 *
 * @author zmichaels
 */
public class ALSource {

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

    public ALSource() {
        new InitTask().alRun();
    }

    public boolean isValid() {
        return source != null && source.isValid();
    }

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

    public void delete() {
        new DeleteTask().alRun();
    }

    public final class DeleteTask extends ALTask {

        @Override
        public void run() {
            if (isValid()) {
                ALTools.getDriverInstance().sourceDelete(source);
                source = null;
            }
        }
    }

    public void setBuffer(final ALBuffer buffer) {
        new SetBufferTask(buffer).alRun();
    }

    public final class SetBufferTask extends ALTask {

        private final ALBuffer buffer;

        public SetBufferTask(final ALBuffer buffer) {
            this.buffer = Objects.requireNonNull(buffer);
        }

        @Override
        public void run() {
            if (isValid()) {
                if(buffer.isValid()) {
                ALTools.getDriverInstance().sourceSetBuffer(source, buffer.buffer);
                } else {
                    throw new ALException("Invalid buffer!");
                }
            } else {
                throw new ALException("Invalid source!");
            }
        }
    }
    
    public float getPitch() {
        return ALSource.this.pitch;
    }

    public void setPitch(final float pitch) {
        new SetPitchTask(pitch).alRun();
    }

    public final class SetPitchTask extends ALTask {

        private final float pitch;

        public SetPitchTask(final float pitch) {
            this.pitch = pitch;
        }

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
    public float getGain() {
        return ALSource.this.gain;
    }

    public void setGain(final float gain) {
        new SetGainTask(gain).alRun();
    }

    public final class SetGainTask extends ALTask {

        private final float gain;

        public SetGainTask(final float gain) {
            this.gain = gain;
        }

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
    
    public GLVec3F getPosition() {
        return ALSource.this.position.copyTo();
    }

    public void setPosition(final float x, final float y, final float z) {
        new SetPositionTask(x, y, z).alRun();
    }

    public void setPosition(final GLVec3 pos) {
        new SetPositionTask(pos).alRun();
    }

    public final class SetPositionTask extends ALTask {

        private final float x;
        private final float y;
        private final float z;

        public SetPositionTask(final GLVec3 pos) {
            final GLVec3F vPos = pos.asGLVec3F();

            this.x = vPos.x();
            this.y = vPos.y();
            this.z = vPos.z();
        }

        public SetPositionTask(final float x, final float y, final float z) {
            this.x = x;
            this.y = y;
            this.z = z;
        }

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
    
    public GLVec3F getVelocity() {
        return ALSource.this.velocity.copyTo();
    }

    public void setVelocity(final float x, final float y, final float z) {
        new SetVelocityTask(x, y, z).alRun();
    }

    public void setVelocity(final GLVec3 vec) {
        new SetVelocityTask(vec).alRun();
    }

    public final class SetVelocityTask extends ALTask {

        private final float x;
        private final float y;
        private final float z;

        public SetVelocityTask(final GLVec3 vel) {
            final GLVec3F vVel = vel.asGLVec3F();

            this.x = vVel.x();
            this.y = vVel.y();
            this.z = vVel.z();
        }

        public SetVelocityTask(final float x, final float y, final float z) {
            this.x = x;
            this.y = y;
            this.z = z;
        }

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
    
    public boolean isLooping() {
        return ALSource.this.isLooping;
    }

    public void setLooping(final boolean shouldLoop) {
        new SetLoopingTask(shouldLoop).alRun();
    }

    public final class SetLoopingTask extends ALTask {

        private final boolean shouldLoop;

        public SetLoopingTask(final boolean shouldLoop) {
            this.shouldLoop = shouldLoop;
        }

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
    
    public boolean isPlaying() {
        return ALSource.this.isPlaying;
    }

    public void play() {
        new PlayTask().alRun();
    }

    public final class PlayTask extends ALTask {

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

    public ALBuffer[] dequeueBuffers() {
        return new DequeueBuffersQuery().alCall();
    }

    public final class DequeueBuffersQuery extends ALQuery<ALBuffer[]> {

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

    public void enqueueBuffers(final ALBuffer... buffers) {
        this.enqueueBuffers(buffers, 0, buffers.length);
    }
    
    public void enqueueBuffers(final ALBuffer[] buffers, final int offset, final int length) {
        new EnqueueBuffersTask(buffers, offset, length).alRun();
    }
    
    public final class EnqueueBuffersTask extends ALTask {

        private final ALBuffer[] buffers;

        public EnqueueBuffersTask(final ALBuffer[] buffers, final int offset, final int length) {
            this.buffers = new ALBuffer[length];
            System.arraycopy(buffers, offset, this.buffers, 0, length);            
        }

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
        if(other instanceof ALSource) {
            final ALSource oSource = (ALSource) other;
            
            if(this.isValid() && oSource.isValid()) {
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
        int hash = 5;
        hash = 79 * hash + Objects.hashCode(this.source);
        return hash;
    }
}
