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

import org.lwjgl.openal.EXTEfx;

/**
 *
 * @author zmichaels
 */
public final class ALEffectEqualizer extends ALEffect {

    public static final float DEFAULT_LOW_GAIN = 1.0f;
    public static final float DEFAULT_LOW_CUTOFF = 200.0f;
    public static final float DEFAULT_MID1_GAIN = 1.0f;
    public static final float DEFAULT_MID1_CENTER = 500.0f;
    public static final float DEFAULT_MID1_WIDTH = 1.0f;
    public static final float DEFAULT_MID2_GAIN = 1.0f;
    public static final float DEFAULT_MID2_CENTER = 3000.0f;
    public static final float DEFAULT_MID2_WIDTH = 1.0f;
    public static final float DEFAULT_HIGH_GAIN = 1.0f;
    public static final float DEFAULT_HIGH_CUTOFF = 6000.0f;

    private float lowGain;
    private float lowCutoff;
    private float mid1Gain;
    private float mid1Center;
    private float mid1Width;
    private float mid2Gain;
    private float mid2Center;
    private float mid2Width;
    private float highGain;
    private float highCutoff;

    public ALEffectEqualizer() {
        super(ALEffectType.AL_EFFECT_EQUALIZER);
    }

    @Override
    protected void resetValues() {
        this.lowGain = DEFAULT_LOW_GAIN;
        this.lowCutoff = DEFAULT_LOW_CUTOFF;
        this.mid1Gain = DEFAULT_MID1_GAIN;
        this.mid1Center = DEFAULT_MID1_CENTER;
        this.mid1Width = DEFAULT_MID1_WIDTH;
        this.mid2Gain = DEFAULT_MID2_GAIN;
        this.mid2Center = DEFAULT_MID2_CENTER;
        this.mid2Width = DEFAULT_MID2_WIDTH;
        this.highGain = DEFAULT_HIGH_GAIN;
        this.highCutoff = DEFAULT_HIGH_CUTOFF;
    }

    public float getLowGain() {
        return this.lowGain;
    }

    public float getLowCutoff() {
        return this.lowCutoff;
    }

    public float getMid1Gain() {
        return this.mid1Gain;
    }

    public float getMid1Center() {
        return this.mid1Center;
    }

    public float getMid1Width() {
        return this.mid1Width;
    }

    public float getMid2Gain() {
        return this.mid2Gain;
    }

    public float getMid2Center() {
        return this.mid2Center;
    }

    public float getMid2Width() {
        return this.mid2Width;
    }

    public float getHighCutoff() {
        return this.highCutoff;
    }

    public float getHighGain() {
        return this.highGain;
    }

    public ALEffectEqualizer setLowGain(final float gain) {
        new SetLowGainTask(gain).alRun();
        return this;
    }

    public ALEffectEqualizer setLowCutoff(final float cutoff) {
        new SetLowCutoffTask(cutoff).alRun();
        return this;
    }

    public ALEffectEqualizer setMid1Gain(final float gain) {
        new SetMid1GainTask(gain).alRun();
        return this;
    }

    public ALEffectEqualizer setMid1Center(final float center) {
        new SetMid1CenterTask(center).alRun();
        return this;
    }

    public ALEffectEqualizer setMid1Width(final float width) {
        new SetMid1WidthTask(width).alRun();
        return this;
    }

    public ALEffectEqualizer setMid2Gain(final float gain) {
        new SetMid2GainTask(gain).alRun();
        return this;
    }

    public ALEffectEqualizer setMid2Center(final float center) {
        new SetMid2CenterTask(center).alRun();
        return this;
    }

    public ALEffectEqualizer setMid2Width(final float width) {
        new SetMid2WidthTask(width).alRun();
        return this;
    }

    public ALEffectEqualizer setHighGain(final float gain) {
        new SetHighGainTask(gain).alRun();
        return this;
    }

    public ALEffectEqualizer setHighCutoff(final float cutoff) {
        new SetHighCutoffTask(cutoff).alRun();
        return this;
    }

    public final class SetLowGainTask extends SetPropertyFTask {

        private final float gain;

        public SetLowGainTask(final float gain) {
            super(EXTEfx.AL_EQUALIZER_LOW_GAIN, gain);
            this.gain = gain;
        }

        @Override
        public void run() {
            super.run();
            ALEffectEqualizer.this.lowGain = this.gain;
        }
    }

    public final class SetLowCutoffTask extends SetPropertyFTask {

        private final float cutoff;

        public SetLowCutoffTask(final float cutoff) {
            super(EXTEfx.AL_EQUALIZER_LOW_CUTOFF, cutoff);
            this.cutoff = cutoff;
        }

        @Override
        public void run() {
            super.run();
            ALEffectEqualizer.this.lowCutoff = this.cutoff;
        }
    }

    public final class SetMid1GainTask extends SetPropertyFTask {

        private final float gain;

        public SetMid1GainTask(final float gain) {
            super(EXTEfx.AL_EQUALIZER_MID1_GAIN, gain);
            this.gain = gain;
        }

        @Override
        public void run() {
            super.run();
            ALEffectEqualizer.this.mid1Gain = this.gain;
        }
    }

    public final class SetMid1CenterTask extends SetPropertyFTask {

        private final float center;

        public SetMid1CenterTask(final float center) {
            super(EXTEfx.AL_EQUALIZER_MID1_CENTER, center);
            this.center = center;
        }

        @Override
        public void run() {
            super.run();
            ALEffectEqualizer.this.mid1Center = this.center;
        }
    }

    public final class SetMid1WidthTask extends SetPropertyFTask {

        private final float width;

        public SetMid1WidthTask(final float width) {
            super(EXTEfx.AL_EQUALIZER_MID1_WIDTH, width);
            this.width = width;
        }

        @Override
        public void run() {
            super.run();
            ALEffectEqualizer.this.mid1Width = this.width;
        }
    }

    public final class SetMid2GainTask extends SetPropertyFTask {

        private final float gain;

        public SetMid2GainTask(final float gain) {
            super(EXTEfx.AL_EQUALIZER_MID2_GAIN, gain);
            this.gain = gain;
        }

        @Override
        public void run() {
            super.run();
            ALEffectEqualizer.this.mid2Gain = this.gain;
        }
    }

    public final class SetMid2CenterTask extends SetPropertyFTask {

        private final float center;

        public SetMid2CenterTask(final float center) {
            super(EXTEfx.AL_EQUALIZER_MID2_CENTER, center);
            this.center = center;
        }

        @Override
        public void run() {
            super.run();
            ALEffectEqualizer.this.mid2Center = this.center;
        }
    }

    public final class SetMid2WidthTask extends SetPropertyFTask {

        private final float width;

        public SetMid2WidthTask(final float width) {
            super(EXTEfx.AL_EQUALIZER_MID2_WIDTH, width);
            this.width = width;
        }

        @Override
        public void run() {
            super.run();
            ALEffectEqualizer.this.mid2Width = this.width;
        }
    }

    public final class SetHighGainTask extends SetPropertyFTask {

        private final float gain;

        public SetHighGainTask(final float gain) {
            super(EXTEfx.AL_EQUALIZER_HIGH_GAIN, gain);
            this.gain = gain;
        }

        @Override
        public void run() {
            super.run();
            ALEffectEqualizer.this.highGain = this.gain;
        }
    }

    public final class SetHighCutoffTask extends SetPropertyFTask {

        private final float cutoff;

        public SetHighCutoffTask(final float cutoff) {
            super(EXTEfx.AL_EQUALIZER_HIGH_CUTOFF, cutoff);
            this.cutoff = cutoff;
        }

        @Override
        public void run() {
            super.run();
            ALEffectEqualizer.this.highCutoff = cutoff;
        }
    }
}
