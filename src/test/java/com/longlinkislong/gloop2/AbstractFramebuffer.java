/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.longlinkislong.gloop2;

import java.util.HashMap;
import java.util.Map;

/**
 *
 * @author zmichaels
 */
public abstract class AbstractFramebuffer implements Framebuffer {
    protected final Map<String, AbstractFramebufferAttachment> attachments = new HashMap<>();
    
    protected void clear() {
        this.attachments.clear();
    }
    
    protected abstract int newAttachmentId();
    
    protected abstract void recycleAttachmentId(int attachmentId);
    
}
