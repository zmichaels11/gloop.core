/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.longlinkislong.gloop2;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 * @author zmichaels
 * @param <T>
 */
public abstract class AbstractFramebufferFactory <T extends AbstractFramebuffer> {
    private static final Logger LOGGER = LoggerFactory.getLogger(AbstractFramebufferFactory.class);
    
    protected abstract T newFramebuffer();
    
    protected abstract void doAttach(T fb, String alias, AbstractFramebufferAttachment attach);
    
    protected abstract void doRemove(T fb, String alias, AbstractFramebufferAttachment attach);
    
    public void attach(final T fb, final String alias, final AbstractFramebufferAttachment attach) {
        if (fb.attachments.containsKey(alias)) {
            doRemove(fb, alias, attach);
            fb.attachments.remove(alias);
        }
        final int attachId = fb.newAttachmentId();
        
        doAttach(fb, alias, attach.withAttachmentId(attachId));
    }
    
    public void remove(final T fb, final String alias) {
        if (fb.attachments.containsKey(alias)) {
            final AbstractFramebufferAttachment attach = fb.attachments.get(alias);
            
            doRemove(fb, alias, attach);
            fb.attachments.remove(alias);
            fb.recycleAttachmentId(attach.attachmentId);
        }
    }
    
    protected abstract void doBind(T fb, final AbstractFramebufferAttachment[] attachments);
    
    public void bind(T fb, String... attachments) {
        final int len = attachments.length;
        final AbstractFramebufferAttachment[] attach = new AbstractFramebufferAttachment[len];
        
        for (int i = 0; i < len; i++) {
            attach[i] = fb.attachments.get(attachments[i]);
        }
        
        doBind(fb, attach);
    }
    
    public abstract boolean isValid(T fb);
}
