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
public enum RenderbufferFormat {    
    RGB8(1),
    RGBA8(2);

    final int value;

    RenderbufferFormat(final int value) {
        this.value = value;
    }
    
    public static Optional<RenderbufferFormat> valueOf(final int value) {
        switch (value) {
            case 1:
                return Optional.of(RGB8);                
            case 2:
                return Optional.of(RGBA8);                
            default:
                return Optional.empty();
        }
    }
}
