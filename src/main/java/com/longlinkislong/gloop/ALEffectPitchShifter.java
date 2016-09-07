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
public final class ALEffectPitchShifter extends ALEffect {
    public static final int DEFAULT_COARSE_TUNE = 12;
    public static final int DEFAULT_FINE_TUNE = 0;
    
    private int coarseTune = DEFAULT_COARSE_TUNE;
    private int fineTune = DEFAULT_FINE_TUNE;
    
    public ALEffectPitchShifter() {
        super(ALEffectType.AL_EFFECT_PITCH_SHIFTER);
    }
    
    public int getCoarseTune() {
        return this.coarseTune;
    }
    
    public int getFineTune() {
        return this.fineTune;
    }
    
    public ALEffectPitchShifter setCoarseTune(final int coarse) {
        new SetCoarseTuneTask(coarse).alRun();
        return this;
    }
    
    public ALEffectPitchShifter setFineTune(final int fine) {
        new SetFineTuneTask(fine).alRun();
        return this;
    }
    
    public final class SetCoarseTuneTask extends SetPropertyITask {
        private final int coarseTune;
        
        public SetCoarseTuneTask(final int coarse) {
            super(EXTEfx.AL_PITCH_SHIFTER_COARSE_TUNE, coarse);
            this.coarseTune = coarse;
        }
        
        @Override
        public void run() {
            super.run();            
            ALEffectPitchShifter.this.coarseTune = this.coarseTune;
        }
    }
    
    public final class SetFineTuneTask extends SetPropertyITask {
        private final int fineTune;
        
        public SetFineTuneTask(final int fine) {
            super(EXTEfx.AL_PITCH_SHIFTER_FINE_TUNE, fine);
            this.fineTune = fine;
        }
        
        @Override
        public void run() {
            super.run();
            ALEffectPitchShifter.this.fineTune = this.fineTune;
        }
    }
    
    @Override
    protected void resetValues() {
        super.resetValues();
        
        this.coarseTune = DEFAULT_COARSE_TUNE;
        this.fineTune = DEFAULT_FINE_TUNE;
    }
}
