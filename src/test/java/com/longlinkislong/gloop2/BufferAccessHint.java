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
public enum BufferAccessHint {
    DONT_CARE(3),
    READ(1),
    WRITE(2),
    READ_WRITE(3);
    
    final int value;
    
    BufferAccessHint(final int value) {
        this.value = value;
    }
    
    public static Optional<BufferAccessHint> valueOf(final int value) {
        switch(value) {
            case 1:
                return Optional.of(READ);
            case 2:
                return Optional.of(WRITE);
            case 3:
                return Optional.of(READ_WRITE);
            default:
                return Optional.empty();
        }
    }
}
