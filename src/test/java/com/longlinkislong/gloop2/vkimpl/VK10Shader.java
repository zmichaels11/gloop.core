/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.longlinkislong.gloop2.vkimpl;

import com.longlinkislong.gloop2.AbstractShader;

/**
 *
 * @author zmichaels
 */
public final class VK10Shader extends AbstractShader {
    public long module;    
    
    @Override
    protected void clear() {
        super.clear();
        
        this.module = 0L;
    }
}
