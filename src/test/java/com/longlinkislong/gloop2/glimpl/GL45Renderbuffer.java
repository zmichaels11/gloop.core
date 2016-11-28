/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.longlinkislong.gloop2.glimpl;

import com.longlinkislong.gloop2.AbstractRenderbuffer;
import com.longlinkislong.gloop2.RenderbufferFormat;

/**
 *
 * @author zmichaels
 */
public class GL45Renderbuffer extends AbstractRenderbuffer{
    int id;
    
    void setWidth(final int width) {
        this.width = width;
    }
    
    void setHeight(final int height) {
        this.height = height;
    }
    
    void setFormat(final RenderbufferFormat fmt) {
        this.format = fmt;
    }
    
    @Override
    protected void clear() {
        super.clear();
        
        this.id = 0;
    }
}
