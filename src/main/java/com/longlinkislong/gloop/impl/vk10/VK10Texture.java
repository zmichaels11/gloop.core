/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.longlinkislong.gloop.impl.vk10;

import com.longlinkislong.gloop.impl.Texture;

/**
 *
 * @author zmichaels
 */
public final class VK10Texture implements Texture {
    long textureId = -1;
    
    @Override
    public boolean isValid() {
        return textureId != -1;
    }
    
}
