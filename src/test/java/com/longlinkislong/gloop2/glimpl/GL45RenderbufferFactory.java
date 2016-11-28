/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.longlinkislong.gloop2.glimpl;

import com.longlinkislong.gloop2.AbstractRenderbufferFactory;
import com.longlinkislong.gloop2.RenderbufferFormat;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL30;
import org.lwjgl.opengl.GL45;

/**
 *
 * @author zmichaels
 */
public class GL45RenderbufferFactory extends AbstractRenderbufferFactory<GL45Renderbuffer>{

    @Override
    protected GL45Renderbuffer newRenderbuffer() {
        final GL45Renderbuffer out = new GL45Renderbuffer();
        
        out.id = GL45.glCreateRenderbuffers();
        
        return out;
    }

    @Override
    protected void doAllocate(GL45Renderbuffer rb, int width, int height, RenderbufferFormat fmt) {
        final int format;
        
        switch (fmt) {
            case RGB8:
                format = GL11.GL_RGB8;
                break;
            default:
                format = GL11.GL_RGBA8;
                break;
        }
        
        GL45.glNamedRenderbufferStorage(rb.id, format, width, height);
    }

    @Override
    protected void doFree(GL45Renderbuffer rb) {
        GL30.glDeleteRenderbuffers(rb.id);
    }

    @Override
    public boolean isValid(GL45Renderbuffer rb) {
        return rb != null && rb.id != 0;
    }
    
}
