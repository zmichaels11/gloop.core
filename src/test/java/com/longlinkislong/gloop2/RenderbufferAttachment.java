/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.longlinkislong.gloop2;

import java.lang.ref.WeakReference;
import java.util.Optional;

/**
 *
 * @author zmichaels
 */
public final class RenderbufferAttachment extends AbstractFramebufferAttachment {    
    private final WeakReference<AbstractRenderbuffer> renderbuffer;
    
    public RenderbufferAttachment(final AbstractRenderbuffer renderbuffer) {
        super(0);
        this.renderbuffer = new WeakReference<>(renderbuffer);
    }
    
    private RenderbufferAttachment(final int id, final AbstractRenderbuffer renderbuffer) {
        super(id);
        this.renderbuffer = new WeakReference<>(renderbuffer);
    }        
    
    public Optional<AbstractRenderbuffer> getRenderbuffer() {
        return Optional.ofNullable(this.renderbuffer.get());
    }
    
    @Override
    RenderbufferAttachment withAttachmentId(final int newId) {
        return new RenderbufferAttachment(newId, this.renderbuffer.get());
    }
}
