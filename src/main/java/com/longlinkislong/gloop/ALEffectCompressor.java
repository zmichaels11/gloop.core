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
