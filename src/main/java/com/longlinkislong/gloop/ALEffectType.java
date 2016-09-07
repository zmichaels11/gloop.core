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
