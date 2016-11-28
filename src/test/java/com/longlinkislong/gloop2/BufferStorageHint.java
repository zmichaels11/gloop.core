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
public enum BufferStorageHint {
    DONT_CARE(2),
    DYNAMIC(1),
    STATIC(2);
    
    final int value;
    
    BufferStorageHint(final int value) {
        this.value = value;
    }
    
    public static Optional<BufferStorageHint> valueOf(final int value) {
        switch (value) {
            case 1:
                return Optional.of(DYNAMIC);
            case 2:
                return Optional.of(STATIC);
            default:
                return Optional.empty();
                
        }
    }
}
