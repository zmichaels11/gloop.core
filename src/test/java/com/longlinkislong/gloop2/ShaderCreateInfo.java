/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.longlinkislong.gloop2;

import java.util.Objects;

/**
 *
 * @author zmichaels
 */
public final class ShaderCreateInfo {
    public final ShaderType type;
    public final String source;
    
    public ShaderCreateInfo(final ShaderType type, final String source) {
        this.type = Objects.requireNonNull(type);
        this.source = Objects.requireNonNull(source);
    }
}
