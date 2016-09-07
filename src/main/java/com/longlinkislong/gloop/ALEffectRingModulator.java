/*
 * Copyright (c) 2016, longlinkislong.com
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 *
 * * Redistributions of source code must retain the above copyright notice, this
 *   list of conditions and the following disclaimer.
 * * Redistributions in binary form must reproduce the above copyright notice,
 *   this list of conditions and the following disclaimer in the documentation
 *   and/or other materials provided with the distribution.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS"
 * AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE
 * IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE
 * ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT HOLDER OR CONTRIBUTORS BE
 * LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR
 * CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF
 * SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS
 * INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN
 * CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE)
 * ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE
 * POSSIBILITY OF SUCH DAMAGE.
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
