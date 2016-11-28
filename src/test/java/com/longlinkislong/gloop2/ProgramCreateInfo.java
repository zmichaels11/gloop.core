/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.longlinkislong.gloop2;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 *
 * @author zmichaels
 * @param <T>
 */
public abstract class ProgramCreateInfo<T extends AbstractShader> {

    public final Map<String, Integer> attributes;        
    
    abstract List<T> getShaders();
    
    public ProgramCreateInfo(final Map<String, Integer> attribs) {
        this.attributes = Collections.unmodifiableMap(new HashMap<>(attribs));                
    }
}
