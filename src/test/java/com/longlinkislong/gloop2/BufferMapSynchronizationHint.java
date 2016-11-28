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
public enum BufferMapSynchronizationHint {
    DONT_CARE(1),
    SYNCHRONIZED(1),
    UNSYNCHRONIZED(2);
    
    public final int value;
    
    private BufferMapSynchronizationHint(final int value) {
        this.value = value;
    }
    
    public static BufferMapSynchronizationHint sanitize(final BufferMapSynchronizationHint hint) {
        return (hint == null || hint == DONT_CARE) ? SYNCHRONIZED : hint;        
    }
    
    public static Optional<BufferMapSynchronizationHint> valueOf(final int value) {
        switch (value) {
            case 1:
                return Optional.of(SYNCHRONIZED);
            case 2:
                return Optional.of(UNSYNCHRONIZED);
            default:
                return Optional.empty();
        }
    }
}
