/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.longlinkislong.gloop;

import java.util.Optional;

/**
 *
 * @author zmichaels
 */
public enum ALEffectType {    
    AL_EFFECT_NULL(0x0000),
    AL_EFFECT_REVERB(0x0001),
    AL_EFFECT_CHORUS(0x0002),
    AL_EFFECT_DISTORTION(0x0003),
    AL_EFFECT_ECHO(0x0004),
    AL_EFFECT_FLANGER(0x0005),
    AL_EFFECT_FREQUENCY_SHIFTER(0x0006),
    AL_EFFECT_VOCAL_MORPHER(0x0007),
    AL_EFFECT_PITCH_SHIFTER(0x0008),
    AL_EFFECT_RING_MODULATOR(0x0009),
    AL_EFFECT_AUTOWAH(0x000A),
    AL_EFFECT_COMPRESSOR(0x000B),
    AL_EFFECT_EQUALIZER(0x000C);
        final int value;

    ALEffectType(final int value) {
        this.value = value;
    }

    public static Optional<ALEffectType> of(final int alEnum) {
        for (ALEffectType type : values()) {
            if (type.value == alEnum) {
                return Optional.of(type);
            }
        }

        return Optional.empty();
    }
}
