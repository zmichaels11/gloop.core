/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.longlinkislong.gloop;

import com.longlinkislong.gloop.alspi.Listener;

/**
 *
 * @author zmichaels
 */
public final class ALListener {
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
    
    public GLVec3F getPosition() {
        return position.copyTo();
    }        
    
    public void setPosition(final GLVec3 pos) {
        new SetPositionTask(pos).alRun();
    }

    public void setPosition(final float x, final float y, final float z) {
        new SetPositionTask(x, y, z).alRun();
    }
    
    public final class SetPositionTask extends ALTask {

        private final float x;
        private final float y;
        private final float z;

        public SetPositionTask(final GLVec3 position) {
            final GLVec3F fPos = position.asGLVec3F();
            
            this.x = fPos.x();
            this.y = fPos.y();
            this.z = fPos.z();
        }
        
        public SetPositionTask(final float x, final float y, final float z) {
            this.x = x;
            this.y = y;
            this.z = z;
        }

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
    
    public GLVec3F getVelocity() {
        return this.velocity.copyTo();
    }
    
    public void setVelocity(final GLVec3 velocity) {
        new SetVelocityTask(velocity).alRun();
    }
    
    public void setVelocity(final float x, final float y, final float z) {
        new SetVelocityTask(x, y, z).alRun();
    }
    
    public final class SetVelocityTask extends ALTask {
        private final float x;
        private final float y;
        private final float z;
        
        public SetVelocityTask(final GLVec3 vel) {
            final GLVec3F fVel = vel.asGLVec3F();
            
            this.x = fVel.x();
            this.y = fVel.y();
            this.z = fVel.z();
        }
        
        public SetVelocityTask(final float x, final float y, final float z) {
            this.x = x;
            this.y = y;
            this.z = z;
        }
        
        @Override
        public void run() {
            if(listener != null && listener.isValid()) {
                velocity.set(x,  y, z);
                ALTools.getDriverInstance().listenerSetVelocity(listener, x, y, z);
            } else {
                throw new ALException("Invalid ALListener!");
            }
        }
    }
    
    public float getGain() {
        return this.gain;
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
            if(listener != null && listener.isValid()) {
                ALListener.this.gain = gain;
                ALTools.getDriverInstance().listenerSetGain(listener, gain);
            } else {
                throw new ALException("Invalid ALListener!");
            }
        }
    }
    
    public GLVec3F getOrientationAt() {
        return at.copyTo();
    }
    
    public GLVec3F getOrientationUp() {
        return up.copyTo();
    }
    
    public void setOrientation(final GLVec3 at, final GLVec3 up) {
        new SetOrientationTask(at, up).alRun();
    }
    
    public void setOrientation(final float atX, final float atY, final float atZ, final float upX, final float upY, final float upZ) {
        new SetOrientationTask(atX, atY, atZ, upX, upY, upZ).alRun();
    }
    
    public final class SetOrientationTask extends ALTask {
        private final float upX;
        private final float upY;
        private final float upZ;
        private final float atX;
        private final float atY;
        private final float atZ;
        
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
        
        public SetOrientationTask(final float atX, final float atY, final float atZ, final float upX, final float upY, final float upZ) {
            this.upX = upX;
            this.upY = upY;
            this.upZ = upZ;
            this.atX = atX;
            this.atY = atY;
            this.atZ = atZ;                        
        }
        
        @Override
        public void run() {
            if(listener != null && listener.isValid()) {
                at.set(atX, atY, atZ);
                up.set(upX, upY, upZ);
                
                ALTools.getDriverInstance().listenerSetOrientation(listener, atX, atY, atZ, upX, upY, upZ);
            }
        }
    }        
    
    private static final class Holder {

        private static final ALListener INSTANCE = new ALListener();
    }

    public static ALListener getInstance() {
        return Holder.INSTANCE;
    }
}
