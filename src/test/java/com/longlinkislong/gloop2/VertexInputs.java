/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.longlinkislong.gloop2;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 *
 * @author zmichaels
 */
public final class VertexInputs {
    public final List<VertexAttribute> attributes;
    
    public VertexInputs(final List<VertexAttribute> attribs) {
        final List<VertexAttribute> newAttribList = new ArrayList<>(attribs);
        
        this.attributes = Collections.unmodifiableList(newAttribList);
    }
    
    public VertexInputs() {
        this.attributes = Collections.emptyList();
    }
    
    public VertexInputs withAttribute(final VertexAttribute attrib) {
        final List<VertexAttribute> newAttribList = new ArrayList<>(this.attributes);
        
        newAttribList.add(attrib);
        
        return new VertexInputs(newAttribList);
    }        
}
