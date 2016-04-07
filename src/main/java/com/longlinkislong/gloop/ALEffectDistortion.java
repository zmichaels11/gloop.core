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
public final class ALEffectDistortion extends ALEffect {
    public static final float DEFAULT_EDGE = 0.2f;
    public static final float DEFAULT_GAIN = 0.05f;
    public static final float DEFAULT_LOWPASS_CUTOFF = 8000.0f;
    public static final float DEFAULT_EQCENTER = 3600.0f;
    public static final float DEFAULT_EQBANDWIDTH = 3600.0f;
    
    private float edge = DEFAULT_EDGE;
    private float gain = DEFAULT_GAIN;
    private float lowpassCutoff = DEFAULT_LOWPASS_CUTOFF;
    private float eqCenter = DEFAULT_EQCENTER;
    private float eqBandwidth = DEFAULT_EQBANDWIDTH;
    
    @Override
    protected void resetValues() {
        this.edge = DEFAULT_EDGE;
        this.gain = DEFAULT_GAIN;
        this.lowpassCutoff = DEFAULT_LOWPASS_CUTOFF;
        this.eqCenter = DEFAULT_EQCENTER;
        this.eqBandwidth = DEFAULT_EQBANDWIDTH;
    }
    
    public ALEffectDistortion() {
        super(ALEffectType.AL_EFFECT_DISTORTION);
    }
    
    public float getEdge() {
        return this.edge;
    }
    
    public float getGain() {
        return this.gain;
    }
    
    public float getLowpassCutoff() {
        return this.lowpassCutoff;
    }
    
    public float getEqCenter() {
        return this.eqCenter;
    }
    
    public float getEqBandwidth() {
        return this.eqBandwidth;
    }
    
    public ALEffectDistortion setEdge(final float edge) {
        new SetEdgeTask(edge).alRun();
        return this;
    }
    
    public ALEffectDistortion setGain(final float gain) {
        new SetGainTask(gain).alRun();
        return this;
    }
    
    public ALEffectDistortion setLowpassCutoff(final float lowpassCutoff) {
        new SetLowpassCutoffTask(lowpassCutoff).alRun();
        return this;
    }
    
    public ALEffectDistortion setEqCenter(final float eqCenter) {
        new SetEqCenterTask(eqCenter).alRun();
        return this;
    }       
    
    public ALEffectDistortion setEqBandwidth(final float eqBandwidth) {
        new SetEqBandwidthTask(eqBandwidth).alRun();
        return this;
    }
    
    public final class SetEdgeTask extends SetPropertyFTask {
        private final float edge;
        
        public SetEdgeTask(final float edge) {
            super(EXTEfx.AL_DISTORTION_EDGE, edge);
            this.edge = edge;
        }
        
        @Override
        public void run() {
            super.run();
            ALEffectDistortion.this.edge = this.edge;
        }
    }
    
    public final class SetGainTask extends SetPropertyFTask {
        private final float gain;
        
        public SetGainTask(final float gain) {
            super(EXTEfx.AL_DISTORTION_GAIN, gain);
            this.gain = gain;
        }
        
        @Override
        public void run() {
            super.run();
            ALEffectDistortion.this.gain = this.gain;
        }
    }
    
    public final class SetLowpassCutoffTask extends SetPropertyFTask {
        private final float lowpassCutoff;
        
        public SetLowpassCutoffTask(final float lowpassCutoff) {
            super(EXTEfx.AL_DISTORTION_LOWPASS_CUTOFF, lowpassCutoff);
            this.lowpassCutoff = lowpassCutoff;
        }
        
        @Override
        public void run() {
            super.run();
            ALEffectDistortion.this.lowpassCutoff = this.lowpassCutoff;
        }
    }
    
    public final class SetEqCenterTask extends SetPropertyFTask {
        private final float eqCenter;
        
        public SetEqCenterTask(final float eqCenter) {
            super(EXTEfx.AL_DISTORTION_EQCENTER, eqCenter);
            this.eqCenter = eqCenter;
        }
        
        @Override
        public void run() {
            super.run();
            ALEffectDistortion.this.eqCenter = this.eqCenter;
        }
    }
    
    public final class SetEqBandwidthTask extends SetPropertyFTask {
        private final float eqBandwidth;
        
        public SetEqBandwidthTask(final float eqBandwidth) {
            super(EXTEfx.AL_DISTORTION_EQBANDWIDTH, eqBandwidth);
            this.eqBandwidth = eqBandwidth;
        }
        
        @Override
        public void run() {
            super.run();
            ALEffectDistortion.this.eqBandwidth = this.eqBandwidth;
        }
    }
}
