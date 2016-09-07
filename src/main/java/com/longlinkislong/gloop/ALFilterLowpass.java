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
public final class ALFilterLowpass extends ALFilter {

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

    public ALFilterLowpass() {
        super(ALFilterType.AL_FILTER_LOWPASS);
    }

    public float getGain() {
        return this.gain;
    }

    public float getGainHF() {
        return this.gainHF;
    }

    public ALFilterLowpass setGain(final float gain) {
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
            ALFilterLowpass.this.gain = this.gain;
        }
    }

    public ALFilterLowpass setGainHF(final float gainHF) {
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
            ALFilterLowpass.this.gainHF = this.gainHF;
        }
    }
}
