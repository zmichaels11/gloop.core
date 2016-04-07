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
public final class ALEffectRingModulator extends ALEffect {

    public enum Waveform {
        SIN(0),
        SAW(1),
        SQUARE(2);

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
    public static final float DEFAULT_FREQUENCY = 440.0f;
    public static final float DEFAULT_HIGHPASS_CUTOFF = 800.0f;
    public static final Waveform DEFAULT_WAVEFORM = Waveform.SIN;

    private float frequency = DEFAULT_FREQUENCY;
    private float highpassCutoff = DEFAULT_HIGHPASS_CUTOFF;
    private Waveform waveform = DEFAULT_WAVEFORM;

    public ALEffectRingModulator() {
        super(ALEffectType.AL_EFFECT_RING_MODULATOR);
    }

    public float getFrequency() {
        return this.frequency;
    }

    public float getHighpassCutoff() {
        return this.highpassCutoff;
    }

    public Waveform getWaveform() {
        return this.waveform;
    }

    public ALEffectRingModulator setFrequency(final float frequency) {
        new SetFrequencyTask(frequency).alRun();
        return this;
    }

    public ALEffectRingModulator setHighpassCutoff(final float highpassCutoff) {
        new SetHighpassCutoffTask(highpassCutoff).alRun();
        return this;
    }

    public ALEffectRingModulator setWaveform(final Waveform waveform) {
        new SetWaveformTask(waveform).alRun();
        return this;
    }

    public final class SetFrequencyTask extends SetPropertyFTask {

        private final float frequency;

        public SetFrequencyTask(final float frequency) {
            super(EXTEfx.AL_RING_MODULATOR_FREQUENCY, frequency);
            this.frequency = frequency;
        }

        @Override
        public void run() {
            super.run();
            ALEffectRingModulator.this.frequency = this.frequency;
        }
    }

    public final class SetHighpassCutoffTask extends SetPropertyFTask {

        private final float highpassCutoff;

        public SetHighpassCutoffTask(final float highpassCutoff) {
            super(EXTEfx.AL_RING_MODULATOR_HIGHPASS_CUTOFF, highpassCutoff);
            this.highpassCutoff = highpassCutoff;
        }

        @Override
        public void run() {
            super.run();
            ALEffectRingModulator.this.highpassCutoff = this.highpassCutoff;
        }
    }

    public final class SetWaveformTask extends SetPropertyITask {

        private final Waveform waveform;

        public SetWaveformTask(final Waveform waveform) {
            super(EXTEfx.AL_RING_MODULATOR_WAVEFORM, waveform.value);
            this.waveform = waveform;
        }

        @Override
        public void run() {
            super.run();
            ALEffectRingModulator.this.waveform = this.waveform;
        }
    }
}
