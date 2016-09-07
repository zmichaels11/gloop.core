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
public final class ALEffectChorus extends ALEffect {

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
    public static final int DEFAULT_PHASE = 90;
    public static final float DEFAULT_RATE = 1.1f;
    public static final float DEFAULT_DEPTH = 0.1f;
    public static final float DEFAULT_FEEDBACK = 0.25f;
    public static final float DEFAULT_DELAY = 0.016f;

    private Waveform waveform = DEFAULT_WAVEFORM;
    private int phase = DEFAULT_PHASE;
    private float rate = DEFAULT_RATE;
    private float depth = DEFAULT_DEPTH;
    private float feedback = DEFAULT_FEEDBACK;
    private float delay = DEFAULT_DELAY;

    public ALEffectChorus() {
        super(ALEffectType.AL_EFFECT_CHORUS);
    }

    @Override
    protected void resetValues() {
        this.waveform = DEFAULT_WAVEFORM;
        this.phase = DEFAULT_PHASE;
        this.rate = DEFAULT_RATE;
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

    public float getRate() {
        return this.rate;
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

    public ALEffectChorus setWaveform(final Waveform waveform) {
        new SetWaveformTask(waveform).alRun();
        return this;
    }

    public final class SetWaveformTask extends SetPropertyITask {

        final Waveform waveform;

        public SetWaveformTask(final Waveform waveform) {
            super(EXTEfx.AL_CHORUS_WAVEFORM, waveform.value);
            this.waveform = waveform;
        }

        @Override
        public void run() {
            super.run();
            ALEffectChorus.this.waveform = this.waveform;
        }
    }

    public ALEffectChorus setDelay(final float delay) {
        new SetDelayTask(delay).alRun();
        return this;
    }

    public final class SetDelayTask extends SetPropertyFTask {

        private final float delay;

        public SetDelayTask(final float delay) {
            super(EXTEfx.AL_CHORUS_DELAY, delay);
            this.delay = delay;
        }

        @Override
        public void run() {
            super.run();
            ALEffectChorus.this.delay = this.delay;
        }
    }

    public ALEffectChorus setDepth(final float depth) {
        new SetDepthTask(depth).alRun();
        return this;
    }

    public final class SetDepthTask extends SetPropertyFTask {

        private final float depth;

        public SetDepthTask(final float depth) {
            super(EXTEfx.AL_CHORUS_DEPTH, depth);
            this.depth = depth;
        }

        @Override
        public void run() {
            super.run();
            ALEffectChorus.this.depth = this.depth;
        }
    }

    public ALEffectChorus setFeedback(final float feedback) {
        new SetFeedbackTask(feedback).alRun();
        return this;
    }

    public final class SetFeedbackTask extends SetPropertyFTask {

        private final float feedback;

        public SetFeedbackTask(final float feedback) {
            super(EXTEfx.AL_CHORUS_FEEDBACK, feedback);
            this.feedback = feedback;
        }

        @Override
        public void run() {
            super.run();
            ALEffectChorus.this.feedback = this.feedback;
        }
    }

    public ALEffectChorus setPhase(final int phase) {
        new SetPhaseTask(phase).alRun();
        return this;
    }

    public final class SetPhaseTask extends SetPropertyITask {

        private final int phase;

        public SetPhaseTask(final int phase) {
            super(EXTEfx.AL_CHORUS_PHASE, phase);
            this.phase = phase;
        }

        @Override
        public void run() {
            super.run();
            ALEffectChorus.this.phase = this.phase;
        }
    }

    public ALEffectChorus setRate(final float rate) {
        new SetRateTask(rate).alRun();
        return this;
    }

    public final class SetRateTask extends SetPropertyFTask {

        private final float rate;

        public SetRateTask(final float rate) {
            super(EXTEfx.AL_CHORUS_RATE, rate);
            this.rate = rate;
        }

        @Override
        public void run() {
            super.run();
            ALEffectChorus.this.rate = this.rate;
        }
    }
}
