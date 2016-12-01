/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.longlinkislong.gloop2;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

/**
 *
 * @author zmichaels
 */
public final class FramebufferCreateInfo {
    public final int width;
    public final int height;
    public final Map<Integer, FramebufferAttachment> attachments;
    
    public FramebufferCreateInfo(final int width, final int height, final Map<Integer, FramebufferAttachment> attachments) {
        this.attachments = Collections.unmodifiableMap(new HashMap<>(attachments));
        this.width = width;
        this.height = height;
    }
    
    public FramebufferCreateInfo() {
        this(0, 0, Collections.emptyMap());
    }
    
    public FramebufferCreateInfo withSize(final int width, final int height) {
        return new FramebufferCreateInfo(width, height, attachments);
    }
    
    public FramebufferCreateInfo withAttachment(final int id, final FramebufferAttachment attachment) {
        final Map<Integer, FramebufferAttachment> newAttachments = new HashMap<>(this.attachments);
        
        newAttachments.put(id, attachment);
        
        return new FramebufferCreateInfo(width, height, newAttachments);
    }
    
    public Framebuffer allocate() {
        return ObjectFactoryManager.getInstance().getFramebufferFactory().allocate(this);
    }
}
