/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.longlinkislong.gloop2.glimpl;

import com.longlinkislong.gloop2.AbstractImage2D;
import com.longlinkislong.gloop2.AbstractTexture2D;

/**
 *
 * @author zmichaels
 */
public final class GL45Texture2D extends AbstractTexture2D{
    long handle = 0l;
    int id = 0;
    
    GL45Image2D[] mipmaps;
    
    @Override
    protected AbstractImage2D doGetImage(int level) {
        return mipmaps[level];
    }

    @Override
    public long getHandle() {
        return this.handle;
    }
    
}
