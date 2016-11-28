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
public enum BufferMapHint {
    DEFAULT(0),
    DONT_CARE(0),
    PERSISTENT(1),
    COHERENT(2);
    
    public final int value;
    
    BufferMapHint(final int value) {
        this.value = value;
    }
    
    public static BufferMapHint sanitize(final BufferMapHint hint) {      
        return (hint == null || hint == DONT_CARE) ? DEFAULT : hint;
    }
    
    public static Optional<BufferMapHint> valueOf(final int value) {
        switch (value) {
            case 0:
                return Optional.of(DEFAULT);
            case 1:
                return Optional.of(PERSISTENT);
            case 2:
                return Optional.of(COHERENT);
            default:
                return Optional.empty();
        }
    }
}
