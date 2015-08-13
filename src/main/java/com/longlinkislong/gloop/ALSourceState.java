/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.longlinkislong.gloop;

import java.util.Optional;
import org.lwjgl.openal.AL10;

/**
 *
 * @author zmichaels
 */
public enum ALSourceState {
    AL_PLAYING(AL10.AL_PLAYING),
    AL_PAUSED(AL10.AL_PAUSED),
    AL_STOPPED(AL10.AL_STOPPED),
    AL_INITIAL(AL10.AL_INITIAL);
    
        final int value;

    ALSourceState(final int value) {
        
        this.value = value;
    }
    
    public static Optional<ALSourceState> valueOf(final int value) {
        for(ALSourceState state : values()) {
            if(state.value == value) {
                return Optional.of(state);
            }
        }
        
        return Optional.empty();
    }
}
