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
public final class ALFilterHighpass extends ALFilter {

    public static final float DEFAULT_GAIN = 1.0f;
    public static final float DEFAULT_GAINLF = 1.0f;

    private float gain = DEFAULT_GAIN;
    private float gainLF = DEFAULT_GAINLF;

    public ALFilterHighpass() {
        super(ALFilterType.AL_FILTER_HIGHPASS);

    }

    @Override
    protected void resetValues() {
        super.resetValues();
        this.gain = DEFAULT_GAIN;
        this.gainLF = DEFAULT_GAINLF;
    }

    public float getGain() {
        return this.gain;
    }

    public ALFilterHighpass setGain(final float gain) {
        new SetGainTask(gain).alRun();
        return this;
    }

    public final class SetGainTask extends SetPropertyFTask {

        private final float gain;

        public SetGainTask(final float gain) {
            super(EXTEfx.AL_HIGHPASS_GAIN, gain);
            this.gain = gain;
        }

        @Override
        public void run() {
            super.run();
            ALFilterHighpass.this.gain = this.gain;
        }
    }

    public float getGainLF() {
        return this.gainLF;
    }
    
    public ALFilterHighpass setGainLF(final float gainLF) {
        new SetGainLFTask(gainLF).alRun();
        return this;
    }
    
    public final class SetGainLFTask extends SetPropertyFTask {

        private final float gainLF;

        public SetGainLFTask(final float gainLF) {
            super(EXTEfx.AL_HIGHPASS_GAINLF, gainLF);
            this.gainLF = gainLF;
        }

        @Override
        public void run() {
            super.run();
            ALFilterHighpass.this.gainLF = this.gainLF;
        }
    }
}
