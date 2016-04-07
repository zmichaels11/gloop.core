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
public class ALFilterBandpass extends ALFilter {

    public static final float DEFAULT_GAIN = 1.0f;
    public static final float DEFAULT_GAINLF = 1.0f;
    public static final float DEFAULT_GAINHF = 1.0f;

    private float gain = DEFAULT_GAIN;
    private float gainLF = DEFAULT_GAINLF;
    private float gainHF = DEFAULT_GAINHF;

    public ALFilterBandpass() {
        super(ALFilterType.AL_FILTER_BANDPASS);
    }

    @Override
    protected void resetValues() {
        this.gain = DEFAULT_GAIN;
        this.gainLF = DEFAULT_GAINLF;
        this.gainHF = DEFAULT_GAINHF;
    }

    public float getGain() {
        return this.gain;
    }

    public float getGainLF() {
        return this.gainLF;
    }

    public float getGainHF() {
        return this.gainHF;
    }

    public ALFilterBandpass setGain(final float gain) {
        new SetGainTask(gain).alRun();
        return this;
    }
    
    public final class SetGainTask extends SetPropertyFTask {

        private final float gain;

        public SetGainTask(final float gain) {
            super(EXTEfx.AL_BANDPASS_GAIN, gain);
            this.gain = gain;
        }

        @Override
        public void run() {
            super.run();
            ALFilterBandpass.this.gain = this.gain;
        }
    }
    
    public ALFilterBandpass setGainLF(final float gainLF) {
        new SetGainLFTask(gainLF).alRun();
        return this;
    }

    public final class SetGainLFTask extends SetPropertyFTask {

        private final float gainLF;

        public SetGainLFTask(final float gainLF) {
            super(EXTEfx.AL_BANDPASS_GAINLF, gainLF);
            this.gainLF = gainLF;
        }

        @Override
        public void run() {
            super.run();
            ALFilterBandpass.this.gainLF = this.gainLF;
        }
    }

    public ALFilterBandpass setGainHF(final float gainHF) {
        new SetGainHFTask(gainHF).alRun();
        return this;
    }
    
    public final class SetGainHFTask extends SetPropertyFTask {

        private final float gainHF;

        public SetGainHFTask(final float gainHF) {
            super(EXTEfx.AL_BANDPASS_GAINHF, gainHF);
            this.gainHF = gainHF;
        }

        @Override
        public void run() {
            super.run();
            ALFilterBandpass.this.gainHF = this.gainHF;
        }
    }
}
