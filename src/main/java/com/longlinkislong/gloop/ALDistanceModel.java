/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
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
