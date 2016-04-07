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
public final class ALEffectCompressor extends ALEffect {
    public static final boolean DEFAULT_COMPRESSOR_STATE = true;
    private boolean compressorEnabled = DEFAULT_COMPRESSOR_STATE;
    
    @Override
    protected void resetValues() {
        this.compressorEnabled = DEFAULT_COMPRESSOR_STATE;
    }
    
    public ALEffectCompressor() {
        super(ALEffectType.AL_EFFECT_COMPRESSOR);
    }
    
    public boolean isCompressorEnabled() {
        return this.compressorEnabled;
    }
    
    public ALEffectCompressor setCompressorEnabled(final boolean state) {
        new SetCompressorEnabledTask(state).alRun();
        return this;
    }
    
    public final class SetCompressorEnabledTask extends SetPropertyITask {
        private final boolean enabled;
        
        public SetCompressorEnabledTask(final boolean state) {
            super(EXTEfx.AL_COMPRESSOR_ONOFF, state ? 1 : 0);
            this.enabled = state;
        }
        
        @Override
        public void run() {
            super.run();
            ALEffectCompressor.this.compressorEnabled = this.enabled;
        }
    }
}
