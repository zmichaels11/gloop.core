/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.longlinkislong.gloop2;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

/**
 *
 * @author zmichaels
 */
public final class VertexArrayCreateInfo {
    public final List<VertexAttribute> attributes;
    
    public VertexArrayCreateInfo(final List<VertexAttribute> attribs) {
        final List<VertexAttribute> newAttribList = new ArrayList<>(attribs);
        
        this.attributes = Collections.unmodifiableList(newAttribList);
    }
    
    public VertexArrayCreateInfo() {
        this.attributes = Collections.emptyList();
    }
    
    public VertexArrayCreateInfo withAttribute(final VertexAttribute attrib) {
        final List<VertexAttribute> newAttribList = new ArrayList<>(this.attributes);
        
        newAttribList.add(attrib);
        
        return new VertexArrayCreateInfo(newAttribList);
    }        
}
