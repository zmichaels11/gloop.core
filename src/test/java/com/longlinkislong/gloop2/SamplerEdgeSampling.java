/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.longlinkislong.gloop2;

import java.util.Optional;

/**
 *
 * @author zmichaels
 */
public enum SamplerEdgeSampling {
    REPEAT(1),
    MIRRORED_REPEAT(2),
    CLAMP_TO_EDGE(3),
    CLAMP_TO_BORDER(4),
    MIRROR_CLAMP_TO_EDGE(5),
    DEFAULT(0);
    
    public final int value;
    
    private SamplerEdgeSampling(final int value) {
        this.value = value;
    }
    
    public static Optional<SamplerEdgeSampling> valueOf(final int value) {
        switch (value) {
            case 0:
                return Optional.of(DEFAULT);
            case 1:
                return Optional.of(REPEAT);
            case 2:
                return Optional.of(MIRRORED_REPEAT);
            case 3:
                return Optional.of(CLAMP_TO_EDGE);
            case 4:
                return Optional.of(CLAMP_TO_BORDER);
            case 5:
                return Optional.of(MIRROR_CLAMP_TO_EDGE);
            default:
                return Optional.empty();
        }
    }
}
