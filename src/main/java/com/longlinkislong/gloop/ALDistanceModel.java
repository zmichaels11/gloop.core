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
import org.lwjgl.openal.AL11;

/**
 *
 * @author zmichaels
 */
public enum ALDistanceModel {
    AL_INVERSE_DISTANCE(AL10.AL_INVERSE_DISTANCE),
    AL_INVERSE_DISTANCE_CLAMPED(AL10.AL_INVERSE_DISTANCE_CLAMPED),
    AL_LINEAR_DISTANCE(AL11.AL_LINEAR_DISTANCE),
    AL_LINEAR_DISTANCE_CLAMPED(AL11.AL_LINEAR_DISTANCE_CLAMPED),
    AL_EXPONENTIAL_DISTANCE(AL11.AL_EXPONENT_DISTANCE),
    AL_EXPONENTIAL_DISTANCE_CLAMPED(AL11.AL_EXPONENT_DISTANCE_CLAMPED);
    
    private final int value;
    
    ALDistanceModel(int value) {
        this.value = value;
    }
    
    public void apply() {
        this.apply(ALThread.getDefaultInstance());
    }
    
    public void apply(final ALThread thread) {
        new ApplyTask().alRun(thread);
    }
    
    public class ApplyTask extends ALTask {

        @Override
        public void run() {
            ALTools.getDriverInstance().distanceModelApply(value);
        }        
    }
}
