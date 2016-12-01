/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.longlinkislong.gloop2.vkimpl;

import com.longlinkislong.gloop2.AbstractTexture2D;
import com.longlinkislong.gloop2.Image2D;
import com.longlinkislong.gloop2.TextureFormat;

/**
 *
 * @author zmichaels
 */
public class VK10Texture2D extends AbstractTexture2D {

    public long id;

    public VK10Texture2D() {

    }

    public VK10Texture2D(long id, TextureFormat format, final int width, final int height) {
        this.id = id;
        this.format = format;
        this.width = width;
        this.height = height;
    }

    @Override
    protected Image2D doGetImage(int level) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public long getHandle() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

}
