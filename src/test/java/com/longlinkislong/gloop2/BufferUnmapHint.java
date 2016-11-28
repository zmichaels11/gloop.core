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
public enum BufferUnmapHint {
    DONT_CARE(0),
    FLUSH_ALL(0),
    FLUSH_EXPLICIT(1);
    
    final int value;
    
    private BufferUnmapHint(final int value) {
        this.value = value;
    }
    
    public static BufferUnmapHint sanitize(final BufferUnmapHint hint) {
        return (hint == null || hint == DONT_CARE) ? FLUSH_ALL : hint;
    }
    
    public static Optional<BufferUnmapHint> valueOf(final int value) {
        switch (value) {
            case 0:
                return Optional.of(FLUSH_ALL);
            case 1:
                return Optional.of(FLUSH_EXPLICIT);
            default:
                return Optional.empty();
                
        }
    }
}
