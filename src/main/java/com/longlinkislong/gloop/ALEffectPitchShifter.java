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
