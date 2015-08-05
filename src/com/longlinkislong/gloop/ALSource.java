/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.longlinkislong.gloop;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;
import org.lwjgl.openal.AL10;

/**
 *
 * @author zmichaels
 */
public class ALSource extends ALObject {

    private static final int INVALID_SOURCE_ID = -1;
    private int sourceId = INVALID_SOURCE_ID;

    public ALSource() {
        super();
        this.init();
    }

    public ALSource(final ALThread thread) {
        super(thread);
        this.init();
    }

    public boolean isValid() {
        return this.sourceId != INVALID_SOURCE_ID;
    }

    public final void init() {
        new InitTask().alRun(this.getThread());
    }

    public class InitTask extends ALTask {

        @Override
        public void run() {
            if (!ALSource.this.isValid()) {
                throw new ALException("OpenAL source is invalid!");
            }

            ALSource.this.sourceId = AL10.alGenSources();
        }
    }

    public void delete() {
        new DeleteTask().alRun(this.getThread());
    }

    public class DeleteTask extends ALTask {

        @Override
        public void run() {
            if (!ALSource.this.isValid()) {
                throw new ALException("OpenAL source is invalid!");
            }

            AL10.alDeleteSources(ALSource.this.sourceId);
            ALSource.this.sourceId = INVALID_SOURCE_ID;
        }
    }

    public class SetVelocityTask extends ALTask {

        final float x;
        final float y;
        final float z;

        public SetVelocityTask(final GLVec3 vel) {
            final GLVec3F vf = vel.asGLVec3F();

            this.x = vf.x();
            this.y = vf.y();
            this.z = vf.z();
        }

        @Override
        public void run() {
            if (!ALSource.this.isValid()) {
                throw new ALException("Invalid OpenAL source!");
            }

            AL10.alSource3f(ALSource.this.sourceId, AL10.AL_VELOCITY, this.x, this.y, this.z);
        }
    }

    public void setPosition(final GLVec3 pos) {        
        new SetPositionTask(pos).alRun(this.getThread());
    }
    
    public class SetPositionTask extends ALTask {

        final float x;
        final float y;
        final float z;

        public SetPositionTask(final GLVec3 vel) {
            final GLVec3F vf = vel.asGLVec3F();

            this.x = vf.x();
            this.y = vf.y();
            this.z = vf.z();
        }

        @Override
        public void run() {
            if (!ALSource.this.isValid()) {
                throw new ALException("Invalid OpenAL source!");
            }

            AL10.alSource3f(ALSource.this.sourceId, AL10.AL_POSITION, this.x, this.y, this.z);
        }
    }
    
    public GLVec3F getPosition() {
        return new PositionQuery().alCall(this.getThread());
    }
    
    public class PositionQuery extends ALQuery<GLVec3F> {

        @Override
        public GLVec3F call() throws Exception {
            if(!ALSource.this.isValid()) {
                throw new ALException("ALSource is not valid!");
            }
            
            final FloatBuffer x = ByteBuffer.allocateDirect(Float.BYTES).order(ByteOrder.nativeOrder()).asFloatBuffer();
            final FloatBuffer y = ByteBuffer.allocateDirect(Float.BYTES).order(ByteOrder.nativeOrder()).asFloatBuffer();
            final FloatBuffer z = ByteBuffer.allocateDirect(Float.BYTES).order(ByteOrder.nativeOrder()).asFloatBuffer();
            
            AL10.alGetSource3f(ALSource.this.sourceId, AL10.AL_POSITION, x, y, z);
            
            return GLVec3F.create(x.get(), y.get(), z.get());
        }
        
    }
    
    public void setDirection(final GLVec3 dir) {
        new SetDirectionTask(dir).alRun(this.getThread());
    }

    public class SetDirectionTask extends ALTask {

        final float x;
        final float y;
        final float z;

        public SetDirectionTask(final GLVec3 vel) {
            final GLVec3F vf = vel.asGLVec3F();

            this.x = vf.x();
            this.y = vf.y();
            this.z = vf.z();
        }

        @Override
        public void run() {
            if (!ALSource.this.isValid()) {
                throw new ALException("Invalid OpenAL source!");
            }

            AL10.alSource3f(ALSource.this.sourceId, AL10.AL_VELOCITY, this.x, this.y, this.z);
        }
    }
    
    public GLVec3F getDirection() {
        return new DirectionQuery().alCall(this.getThread());
    }
    
    public class DirectionQuery extends ALQuery<GLVec3F> {

        @Override
        public GLVec3F call() throws Exception {
            if(!ALSource.this.isValid()) {
                throw new ALException("Invalid OpenAL source!");
            }
            
            final FloatBuffer x = ByteBuffer.allocateDirect(Float.BYTES).order(ByteOrder.nativeOrder()).asFloatBuffer();
            final FloatBuffer y = ByteBuffer.allocateDirect(Float.BYTES).order(ByteOrder.nativeOrder()).asFloatBuffer();
            final FloatBuffer z = ByteBuffer.allocateDirect(Float.BYTES).order(ByteOrder.nativeOrder()).asFloatBuffer();
            
            AL10.alGetSource3f(ALSource.this.sourceId, AL10.AL_DIRECTION, x, y, z);
            
            return GLVec3F.create(x.get(), y.get(), z.get());
        }
        
    }

    public void setGain(final float gain) {
        new SetGainTask(gain).alRun(this.getThread());
    }
    
    public class SetGainTask extends ALTask {

        final float gain;

        public SetGainTask(final float gain) {
            this.gain = gain;
        }

        @Override
        public void run() {
            if (!ALSource.this.isValid()) {
                throw new ALException("Invalid OpenAL source!");
            }

            AL10.alSourcef(ALSource.this.sourceId, AL10.AL_GAIN, this.gain);
        }
    }

    public float getGain() {
        return new GainQuery().alCall(this.getThread());
    }
    
    public class GainQuery extends ALQuery<Float> {

        @Override
        public Float call() throws Exception {
            if (!ALSource.this.isValid()) {
                throw new ALException("Invalid OpenAL source!");
            }

            return AL10.alGetSourcef(ALSource.this.sourceId, AL10.AL_GAIN);
        }
    }

    public void setPitch(final float value) {
        new SetPitchTask(value).alRun(this.getThread());
    }

    public class SetPitchTask extends ALTask {

        final float pitch;

        public SetPitchTask(final float pitch) {
            this.pitch = pitch;
        }

        @Override
        public void run() {
            if (!ALSource.this.isValid()) {
                throw new ALException("Invalid OpenAL source!");
            }

            AL10.alSourcef(ALSource.this.sourceId, AL10.AL_PITCH, this.pitch);
        }
    }

    public float getPitch() {
        return new PitchQuery().alCall(this.getThread());
    }

    public class PitchQuery extends ALQuery<Float> {

        @Override
        public Float call() throws Exception {
            if (!ALSource.this.isValid()) {
                throw new ALException("Invalod OpenAL source!");
            }

            return AL10.alGetSourcef(ALSource.this.sourceId, AL10.AL_PITCH);
        }

    }
}
