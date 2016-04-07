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
public final class ALLowpassFilter extends ALFilter {

    public static final float DEFAULT_GAIN = 1.0f;
    public static final float DEFAULT_GAINHF = 1.0f;

    private float gain = DEFAULT_GAIN;
    private float gainHF = DEFAULT_GAINHF;

    @Override
    protected void resetValues() {
        super.resetValues();
        this.gain = DEFAULT_GAIN;
        this.gainHF = DEFAULT_GAINHF;
    }

    public ALLowpassFilter() {
        super(ALFilterType.AL_FILTER_LOWPASS);
    }

    public float getGain() {
        return this.gain;
    }

    public float getGainHF() {
        return this.gainHF;
    }

    public ALLowpassFilter setGain(final float gain) {
        new SetGainTask(gain).alRun();
        return this;
    }

    public final class SetGainTask extends SetPropertyFTask {

        private final float gain;

        public SetGainTask(final float gain) {
            super(EXTEfx.AL_LOWPASS_GAIN, gain);
            this.gain = gain;
        }

        @Override
        public void run() {
            super.run();
            ALLowpassFilter.this.gain = this.gain;
        }
    }

    public ALLowpassFilter setGainHF(final float gainHF) {
        new SetGainHFTask(gainHF).alRun();
        return this;
    }

    public final class SetGainHFTask extends SetPropertyFTask {

        private final float gainHF;

        public SetGainHFTask(final float gainHF) {
            super(EXTEfx.AL_LOWPASS_GAINHF, gainHF);
            this.gainHF = gainHF;
        }

        @Override
        public void run() {
            super.run();
            ALLowpassFilter.this.gainHF = this.gainHF;
        }
    }
}
