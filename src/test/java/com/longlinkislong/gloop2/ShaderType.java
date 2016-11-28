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
public enum ShaderType {
    UNKNOWN(0),
    VERTEX(1),
    TESS_CONTROL(2),
    TESS_EVALUATION(3),
    GEOMETRY(4),
    FRAGMENT(5),
    COMPUTE(6);
    
    final int value;
    
    private ShaderType(final int value) {
        this.value = value;
    }
    
    public static Optional<ShaderType> valueOf(final int value) {
        switch (value) {
            case 1:
                return Optional.of(VERTEX);
            case 2:
                return Optional.of(TESS_CONTROL);
            case 3:
                return Optional.of(TESS_EVALUATION);
            case 4:
                return Optional.of(GEOMETRY);
            case 5:
                return Optional.of(FRAGMENT);
            case 6: 
                return Optional.of(COMPUTE);
            default:
                return Optional.empty();
        }
    }
}
