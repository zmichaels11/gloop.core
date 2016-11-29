/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.longlinkislong.gloop2.glimpl;

import com.longlinkislong.gloop2.AbstractSampler2D;

/**
 *
 * @author zmichaels
 */
public class GL33Sampler2D extends AbstractSampler2D{
    int id;
    
    @Override
    protected void clear() {
        super.clear();
        this.id = 0;
    }
}
