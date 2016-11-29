/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.longlinkislong.gloop2;

import java.util.Objects;

/**
 *
 * @author zmichaels
 */
public final class Image2DCreateInfo {

    public final int width;
    public final int height;
    public final ImageFormat format;  

    public Image2DCreateInfo(final int width, final int height,
            final ImageFormat iFormat) {

        this.width = width;
        this.height = height;
        this.format = Objects.requireNonNull(iFormat);        
    }
    
    public Image2DCreateInfo withSize(final int width, final int height) {
        return new Image2DCreateInfo(width, height, this.format);
    }
    
    public Image2DCreateInfo withFormat(final ImageFormat format) {
        return new Image2DCreateInfo(this.width, this.height, format);
    }
    
    public Image2DCreateInfo() {
        this(1, 1, ImageFormat.RGB);
    }
    
    public Image2D allocate() {
        return ObjectFactoryManager.getInstance().getImage2DFactory().allocate(this);
    }
}
