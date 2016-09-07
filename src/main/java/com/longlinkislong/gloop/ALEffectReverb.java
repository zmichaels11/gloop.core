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

import org.lwjgl.openal.AL10;
import org.lwjgl.openal.EXTEfx;

/**
 *
 * @author zmichaels
 */
public final class ALEffectReverb extends ALEffect {

    public static final float DEFAULT_DENSITY = 1.0f;
    private float density = DEFAULT_DENSITY;
    public static final float DEFAULT_DIFFUSION = 1.0f;
    private float diffusion = DEFAULT_DIFFUSION;
    public static final float DEFAULT_GAIN = 0.32f;
    private float gain = DEFAULT_GAIN;
    public static final float DEFAULT_GAINHF = 0.89f;
    private float gainHF = DEFAULT_GAINHF;
    public static final float DEFAULT_DECAY_TIME = 1.49f;    
    private float decayTime = DEFAULT_DECAY_TIME;
    public static final float DEFAULT_DECAY_HF_RATIO = 0.83f;
    private float decayHFRatio = DEFAULT_DECAY_HF_RATIO;
    public static final float DEFAULT_REFLECTIONS_GAIN = 0.05f;
    private float reflectionsGain = DEFAULT_REFLECTIONS_GAIN;
    public static final float DEFAULT_REFLECTIONS_DELAY = 0.007f;
    private float reflectionsDelay = DEFAULT_REFLECTIONS_DELAY;
    public static final float DEFAULT_LATE_REVERB_GAIN = 1.26f;
    private float lateReverbGain = DEFAULT_LATE_REVERB_GAIN;
    public static final float DEFAULT_LATE_REVERB_DELAY = 0.1f;
    private float lateReverbDelay = DEFAULT_LATE_REVERB_DELAY;
    public static final float DEFAULT_AIR_ABSORPTION_GAINHF = 0.994f;
    private float airAbsorptionGainHF = DEFAULT_AIR_ABSORPTION_GAINHF;
    public static final float DEFAULT_ROOM_ROLLOFF_FACTOR = 0.0f;
    private float roomRolloffFactor = DEFAULT_ROOM_ROLLOFF_FACTOR;
    public static final boolean DEFAULT_DECAYHF_LIMITED = true;
    private boolean decayHFLimit = DEFAULT_DECAYHF_LIMITED;

    @Override
    protected void resetValues() {
        super.resetValues();
        
        this.density = DEFAULT_DENSITY;
        this.diffusion = DEFAULT_DIFFUSION;
        this.gain = DEFAULT_GAIN;
        this.gainHF = DEFAULT_GAINHF;
        this.decayTime = DEFAULT_DECAY_TIME;
        this.decayHFRatio = DEFAULT_DECAY_HF_RATIO;
        this.reflectionsGain = DEFAULT_REFLECTIONS_GAIN;
        this.reflectionsDelay = DEFAULT_REFLECTIONS_DELAY;
        this.lateReverbGain = DEFAULT_LATE_REVERB_GAIN;
        this.lateReverbDelay = DEFAULT_LATE_REVERB_DELAY;
        this.airAbsorptionGainHF = DEFAULT_AIR_ABSORPTION_GAINHF;
        this.roomRolloffFactor = DEFAULT_ROOM_ROLLOFF_FACTOR;
        this.decayHFLimit = DEFAULT_DECAYHF_LIMITED;
    }
    
    public float getLateReverbDelay() {
        return this.lateReverbDelay;
    }

    public float getDensity() {
        return this.density;
    }

    public float getDiffusion() {
        return this.diffusion;
    }

    public float getGain() {
        return this.gain;
    }

    public float getGainHF() {
        return this.gainHF;
    }

    public float getDecayTime() {
        return this.decayTime;
    }

    public float getDecayHFRatio() {
        return this.decayHFRatio;
    }

    public float getReflectionsGain() {
        return this.reflectionsGain;
    }

    public float getLateReverbGain() {
        return this.lateReverbGain;
    }

    public float getReflectionsDelay() {
        return this.reflectionsDelay;
    }

    public float getAirAbsorptionGainHF() {
        return this.airAbsorptionGainHF;
    }

    public float getRoomRolloffFactor() {
        return this.roomRolloffFactor;
    }

    public boolean isDecayHFLimited() {
        return this.decayHFLimit;
    }

    public ALEffectReverb() {
        super(ALEffectType.AL_EFFECT_REVERB);
    }

    public ALEffectReverb setDensity(final float density) {
        new SetDensityTask(density).alRun();
        return this;
    }

    public final class SetDensityTask extends SetPropertyFTask {

        private final float density;

        public SetDensityTask(final float density) {
            super(EXTEfx.AL_REVERB_DENSITY, density);

            this.density = density;
        }

        @Override
        public void run() {
            super.run();

            ALEffectReverb.this.density = this.density;
        }
    }

    public ALEffectReverb setDiffusion(final float diffusion) {
        new SetDiffusionTask(diffusion).alRun();
        return this;
    }

    public final class SetDiffusionTask extends SetPropertyFTask {

        private final float diffusion;

        public SetDiffusionTask(final float diffusion) {
            super(EXTEfx.AL_REVERB_DIFFUSION, diffusion);

            this.diffusion = diffusion;
        }

        @Override
        public void run() {
            super.run();

            ALEffectReverb.this.diffusion = this.diffusion;
        }
    }

    public ALEffectReverb setGain(final float gain) {
        new SetGainTask(gain).alRun();
        return this;
    }

    public final class SetGainTask extends SetPropertyFTask {

        private final float gain;

        public SetGainTask(final float gain) {
            super(EXTEfx.AL_REVERB_GAIN, gain);

            this.gain = gain;
        }

        @Override
        public void run() {
            super.run();
            ALEffectReverb.this.gain = this.gain;
        }
    }

    public ALEffectReverb setGainHF(final float gainHF) {
        new SetGainHFTask(gainHF).alRun();
        return this;
    }

    public final class SetGainHFTask extends SetPropertyFTask {

        private final float gainHF;

        public SetGainHFTask(final float gainHF) {
            super(EXTEfx.AL_REVERB_GAINHF, gainHF);
            this.gainHF = gainHF;
        }

        @Override
        public void run() {
            super.run();
            ALEffectReverb.this.gainHF = this.gainHF;
        }
    }

    public ALEffectReverb setDecayTime(final float decayTime) {
        new SetDecayTimeTask(decayTime).alRun();
        return this;
    }

    public final class SetDecayTimeTask extends SetPropertyFTask {

        private final float decayTime;

        public SetDecayTimeTask(final float decayTime) {
            super(EXTEfx.AL_REVERB_DECAY_TIME, decayTime);
            this.decayTime = decayTime;
        }

        @Override
        public void run() {
            super.run();
            ALEffectReverb.this.decayTime = this.decayTime;
        }
    }

    public ALEffectReverb setDecayHFRatio(final float decayHFRatio) {
        new SetDecayHFRatioTask(decayHFRatio).alRun();
        return this;
    }

    public final class SetDecayHFRatioTask extends SetPropertyFTask {

        private final float decayHFRatio;

        public SetDecayHFRatioTask(final float decayHFRatio) {
            super(EXTEfx.AL_REVERB_DECAY_HFRATIO, decayHFRatio);
            this.decayHFRatio = decayHFRatio;
        }

        @Override
        public void run() {
            super.run();
            ALEffectReverb.this.decayHFRatio = this.decayHFRatio;
        }
    }

    public ALEffectReverb setReflectionsGain(final float reflectionsGain) {
        new SetReflectionsGainTask(reflectionsGain).alRun();
        return this;
    }

    public final class SetReflectionsGainTask extends SetPropertyFTask {

        private final float reflectionsGain;

        public SetReflectionsGainTask(final float reflectionsGain) {
            super(EXTEfx.AL_REVERB_REFLECTIONS_GAIN, reflectionsGain);
            this.reflectionsGain = reflectionsGain;
        }

        @Override
        public void run() {
            super.run();
            ALEffectReverb.this.reflectionsGain = this.reflectionsGain;
        }
    }

    public ALEffectReverb setReflectionsDelay(final float reflectionsDelay) {
        new SetReflectionsDelayTask(reflectionsDelay).alRun();
        return this;
    }

    public final class SetReflectionsDelayTask extends SetPropertyFTask {

        private final float reflectionsDelay;

        public SetReflectionsDelayTask(final float reflectionsDelay) {
            super(EXTEfx.AL_REVERB_REFLECTIONS_DELAY, reflectionsDelay);
            this.reflectionsDelay = reflectionsDelay;
        }

        @Override
        public void run() {
            super.run();
            ALEffectReverb.this.reflectionsDelay = reflectionsDelay;
        }
    }

    public ALEffectReverb setLateReverbDelay(final float lateReverbDelay) {
        new SetLateReverbDelayTask(lateReverbDelay).alRun();
        return this;
    }

    public final class SetLateReverbDelayTask extends SetPropertyFTask {

        private final float lateReverbDelay;

        public SetLateReverbDelayTask(final float lateReverbDelay) {
            super(EXTEfx.AL_REVERB_LATE_REVERB_DELAY, lateReverbDelay);
            this.lateReverbDelay = lateReverbDelay;
        }

        @Override
        public void run() {
            super.run();
            ALEffectReverb.this.lateReverbDelay = this.lateReverbDelay;
        }
    }

    public ALEffectReverb setLateReverbGain(final float reverbGain) {
        new SetLateReverbGainTask(reverbGain).alRun();
        return this;
    }

    public final class SetLateReverbGainTask extends SetPropertyFTask {

        private final float lateReverbGain;

        public SetLateReverbGainTask(final float lateReverbGain) {
            super(EXTEfx.AL_REVERB_LATE_REVERB_GAIN, lateReverbGain);
            this.lateReverbGain = lateReverbGain;
        }

        @Override
        public void run() {
            super.run();
            ALEffectReverb.this.lateReverbGain = this.lateReverbGain;
        }
    }

    public ALEffectReverb setAirAbsorptionGainHF(final float airAbsorptionGainHF) {
        new SetAirAbsorptionGainHFTask(airAbsorptionGainHF).alRun();
        return this;
    }

    public final class SetAirAbsorptionGainHFTask extends SetPropertyFTask {

        private final float airAbsorptionGainHF;

        public SetAirAbsorptionGainHFTask(final float airAbsorptionGainHF) {
            super(EXTEfx.AL_REVERB_AIR_ABSORPTION_GAINHF, airAbsorptionGainHF);
            this.airAbsorptionGainHF = airAbsorptionGainHF;
        }

        @Override
        public void run() {
            super.run();
            ALEffectReverb.this.airAbsorptionGainHF = this.airAbsorptionGainHF;
        }
    }

    public ALEffectReverb setRoomRoolloffFactor(final float roomRolloffFactor) {
        new SetRoomRolloffFactorTask(roomRolloffFactor).alRun();
        return this;
    }

    public final class SetRoomRolloffFactorTask extends SetPropertyFTask {

        private final float roomRolloffFactor;

        public SetRoomRolloffFactorTask(final float roomRolloffFactor) {
            super(EXTEfx.AL_REVERB_ROOM_ROLLOFF_FACTOR, roomRolloffFactor);
            this.roomRolloffFactor = roomRolloffFactor;
        }

        @Override
        public void run() {
            super.run();
            ALEffectReverb.this.roomRolloffFactor = this.roomRolloffFactor;
        }
    }
    
    public ALEffectReverb setDecayHFLimited(final boolean isDecayHFLimited) {
        new SetDecayHFLimitedTask(isDecayHFLimited).alRun();
        return this;
    }

    public final class SetDecayHFLimitedTask extends SetPropertyITask {

        private final boolean decayHFLimited;

        public SetDecayHFLimitedTask(final boolean isLimited) {
            super(EXTEfx.AL_REVERB_DECAY_HFLIMIT, isLimited ? AL10.AL_TRUE : AL10.AL_FALSE);
            this.decayHFLimited = isLimited;
        }

        @Override
        public void run() {
            super.run();
            ALEffectReverb.this.decayHFLimit = this.decayHFLimited;
        }
    }
}
