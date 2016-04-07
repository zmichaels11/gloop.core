/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.longlinkislong.gloop;

import java.util.Optional;
import org.lwjgl.openal.EXTEfx;

/**
 *
 * @author zmichaels
 */
public final class ALEffectFlanger extends ALEffect {

    public enum Waveform {
        SINUSOID(0),
        TRIANGLE(1);
        final int value;

        Waveform(final int value) {
            this.value = value;
        }

        public static Optional<Waveform> of(final int alEnum) {
            for (Waveform wave : values()) {
                if (wave.value == alEnum) {
                    return Optional.of(wave);
                }
            }

            return Optional.empty();
        }
    }
    
    public static final Waveform DEFAULT_WAVEFORM = Waveform.TRIANGLE;
    public static final int DEFAULT_PHASE = 0;
    public static final float DEFAULT_RATE = 0.27f;
    public static final float DEFAULT_DEPTH = 1.0f;
    public static final float DEFAULT_FEEDBACK = -0.5f;
    public static final float DEFAULT_DELAY = 0.002f;

    private Waveform waveform = DEFAULT_WAVEFORM;
    private int phase = DEFAULT_PHASE;
    private float rate = DEFAULT_RATE;
    private float depth = DEFAULT_DEPTH;
    private float feedback = DEFAULT_FEEDBACK;
    private float delay = DEFAULT_DELAY;
    
    public ALEffectFlanger() {
        super(ALEffectType.AL_EFFECT_FLANGER);
    }
    
    @Override
    protected void resetValues() {
        this.waveform = DEFAULT_WAVEFORM;
        this.phase = DEFAULT_PHASE;
        this.depth = DEFAULT_DEPTH;
        this.feedback = DEFAULT_FEEDBACK;
        this.delay = DEFAULT_DELAY;
    }
    
    public Waveform getWaveform() {
        return this.waveform;
    }
    
    public int getPhase() {
        return this.phase;
    }
    
    public float getDepth() {
        return this.depth;
    }
    
    public float getFeedback() {
        return this.feedback;
    }
    
    public float getDelay() {
        return this.delay;
    }
    
    public float getRate() {
        return this.rate;
    }
    
    public ALEffectFlanger setWaveform(final Waveform waveform) {
        new SetWaveformTask(waveform).alRun();
        return this;
    }
    
    public ALEffectFlanger setPhase(final int phase) {
        new SetPhaseTask(phase).alRun();
        return this;
    }
    
    public ALEffectFlanger setRate(final float rate) {
        new SetRateTask(rate).alRun();
        return this;
    }
    
    public ALEffectFlanger setDepth(final float depth) {
        new SetDepthTask(depth).alRun();
        return this;
    }
    
    public ALEffectFlanger setFeedback(final float feedback) {
        new SetFeedbackTask(feedback).alRun();
        return this;
    }
    
    public ALEffectFlanger setDelay(final float delay) {
        new SetDelayTask(delay).alRun();
        return this;
    }
    
    public final class SetWaveformTask extends SetPropertyITask {
        private final Waveform waveform;
        
        public SetWaveformTask(final Waveform waveform) {
            super(EXTEfx.AL_FLANGER_WAVEFORM, waveform.value);
            this.waveform = waveform;
        }
        
        @Override
        public void run() {
            super.run();
            ALEffectFlanger.this.waveform = this.waveform;
        }
    }
    
    public final class SetPhaseTask extends SetPropertyITask {
        private final int phase;
        
        public SetPhaseTask(final int phase) {
            super(EXTEfx.AL_FLANGER_PHASE, phase);
            this.phase = phase;
        }
        
        @Override
        public void run() {
            super.run();
            ALEffectFlanger.this.phase = this.phase;
        }
    }
    
    public final class SetRateTask extends SetPropertyFTask {
        private final float rate;
        
        public SetRateTask(final float rate) {
            super(EXTEfx.AL_FLANGER_RATE, rate);
            this.rate = rate;
        }
        
        @Override
        public void run() {
            super.run();
            ALEffectFlanger.this.rate = this.rate;
        }
    }
    
    public final class SetDepthTask extends SetPropertyFTask {
        private final float depth;
        
        public SetDepthTask(final float depth) {
            super(EXTEfx.AL_FLANGER_DEPTH, depth);
            this.depth = depth;
        }
        
        @Override
        public void run() {
            super.run();
            ALEffectFlanger.this.depth = this.depth;
        }
    }
    
    public final class SetFeedbackTask extends SetPropertyFTask {
        private final float feedback;
        
        public SetFeedbackTask(final float feedback) {
            super(EXTEfx.AL_FLANGER_FEEDBACK, feedback);
            this.feedback = feedback;
        }
        
        @Override
        public void run() {
            super.run();
            ALEffectFlanger.this.feedback = this.feedback;
        }        
    }
    
    public final class SetDelayTask extends SetPropertyFTask {
        private final float delay;
        
        public SetDelayTask(final float delay) {
            super(EXTEfx.AL_FLANGER_DELAY, delay);
            this.delay = delay;
        }
        
        @Override
        public void run() {
            super.run();
            ALEffectFlanger.this.delay = this.delay;
        }
    }
}
