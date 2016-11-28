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
public enum BufferMapInvalidationHint {
    DONT_CARE(2),
    INVALIDATE_RANGE(1),
    INVALIDATE_BUFFER(2);
    
    public final int value;
    
    private BufferMapInvalidationHint(final int value) {
        this.value = value;
    }
    
    public static BufferMapInvalidationHint sanitize(final BufferMapInvalidationHint hint) {
        return (hint == null || hint == DONT_CARE) ? INVALIDATE_BUFFER : hint;        
    }
    
    public static Optional<BufferMapInvalidationHint> valueOf(final int value) {
        switch (value) {
            case 1:
                return Optional.of(INVALIDATE_RANGE);
            case 2:
                return Optional.of(INVALIDATE_BUFFER);
            default:
                return Optional.empty();
        }
    }
}
