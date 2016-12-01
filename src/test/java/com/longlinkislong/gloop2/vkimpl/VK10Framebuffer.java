/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.longlinkislong.gloop2.vkimpl;

import com.longlinkislong.gloop2.AbstractFramebuffer;
import com.longlinkislong.gloop2.FramebufferCreateInfo;

/**
 *
 * @author zmichaels
 */
public class VK10Framebuffer extends AbstractFramebuffer {
    public long framebuffer;
    public long renderpass;
        
    protected FramebufferCreateInfo getInfo() {
        return info;
    }
}
