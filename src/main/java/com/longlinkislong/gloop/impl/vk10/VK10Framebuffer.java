/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.longlinkislong.gloop.impl.vk10;

import com.longlinkislong.gloop.impl.Framebuffer;

/**
 *
 * @author zmichaels
 */
public final class VK10Framebuffer implements Framebuffer {
    long framebufferId = -1;
    
    @Override
    public boolean isValid() {
        return framebufferId != -1;
    }
}
