/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.longlinkislong.gloop;

import org.lwjgl.openal.EXTEfx;

/**
 *
 * @author zmichaels
 */
public final class ALEffectAutowah extends ALEffect {
    public static final float DEFAULT_ATTACK_TIME = 0.06f;
    public static final float DEFAULT_RELEASE_TIME = 0.06f;
    public static final float DEFAULT_RESONANCE = 1000.0f;
    public static final float DEFAULT_PEAK_GAIN = 11.22f;
    
    private float attackTime = DEFAULT_ATTACK_TIME;
    private float releaseTime = DEFAULT_RELEASE_TIME;
    private float resonance = DEFAULT_RESONANCE;
    private float peakGain = DEFAULT_PEAK_GAIN;
    
    public ALEffectAutowah() {
        super(ALEffectType.AL_EFFECT_AUTOWAH);
    }
    
    @Override
    protected void resetValues() {
        super.resetValues();
        this.attackTime = DEFAULT_ATTACK_TIME;
        this.releaseTime = DEFAULT_RELEASE_TIME;
        this.resonance = DEFAULT_RESONANCE;
        this.peakGain = DEFAULT_PEAK_GAIN;
    }
    
    public float getAttackTime() {
        return this.attackTime;
    }
    
    public float getReleaseTime() {
        return this.releaseTime;
    }
    
    public float getResonance() {
        return this.resonance;
    }
    
    public float getPeakGain() {
        return this.peakGain;
    }
    
    public ALEffectAutowah setAttackTime(final float seconds) {
        new SetAttackTimeTask(seconds).alRun();
        return this;
    }
    
    public ALEffectAutowah setReleaseTime(final float seconds) {
        new SetReleaseTimeTask(seconds).alRun();
        return this;
    }
    
    public ALEffectAutowah setResonance(final float resonance) {
        new SetResonanceTask(resonance).alRun();
        return this;
    }
    
    public ALEffectAutowah setPeakGain(final float gain) {
        new SetPeakGainTask(gain).alRun();
        return this;
    }
    
    public final class SetAttackTimeTask extends SetPropertyFTask {
        private final float time;
        
        public SetAttackTimeTask(final float time) {
            super(EXTEfx.AL_AUTOWAH_ATTACK_TIME, time);
            this.time = time;
        }
        
        @Override
        public void run() {
            super.run();
            ALEffectAutowah.this.attackTime = this.time;            
        }
    }
    
    public final class SetReleaseTimeTask extends SetPropertyFTask {
        private final float time;
        
        public SetReleaseTimeTask(final float time) {
            super(EXTEfx.AL_AUTOWAH_RELEASE_TIME, time);
            this.time = time;
        }
        
        @Override
        public void run() {
            super.run();
            ALEffectAutowah.this.releaseTime = this.time;
        }
    }
    
    public final class SetResonanceTask extends SetPropertyFTask {
        private final float resonance;
        
        public SetResonanceTask(final float resonance) {
            super(EXTEfx.AL_AUTOWAH_RESONANCE, resonance);
            this.resonance = resonance;
        }
        
        @Override
        public void run() {
            super.run();
            ALEffectAutowah.this.resonance = this.resonance;
        }
    }
    
    public final class SetPeakGainTask extends SetPropertyFTask {
        private final float peakGain;
        
        public SetPeakGainTask(final float peakGain) {
            super(EXTEfx.AL_AUTOWAH_PEAK_GAIN, peakGain);
            this.peakGain = peakGain;
        }
        
        @Override
        public void run() {
            super.run();
            ALEffectAutowah.this.peakGain = this.peakGain;
        }
    }
}
