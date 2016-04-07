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
public enum ALFilterType {
    AL_FILTER_NULL(0x0000),
    AL_FILTER_LOWPASS(0x0001),
    AL_FILTER_HIGHPASS(0x0002),
    AL_FILTER_BANDPASS(0x0003);

    final int value;

    ALFilterType(final int value) {
        this.value = value;
    }

    public static Optional<ALFilterType> of(final int alEnum) {
        for (ALFilterType type : values()) {
            if (type.value == alEnum) {
                return Optional.of(type);
            }
        }

        return Optional.empty();
    }
}
