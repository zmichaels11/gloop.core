/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.longlinkislong.gloop2;

/**
 *
 * @author zmichaels
 */
public final class FramebufferCreateInfo {
    public final int width;
    public final int height;
    
    public FramebufferCreateInfo(final int width, final int height) {
        this.width = width;
        this.height = height;
    }
    
    public FramebufferCreateInfo withSize(final int width, final int height) {
        return new FramebufferCreateInfo(width, height);
    }
}
