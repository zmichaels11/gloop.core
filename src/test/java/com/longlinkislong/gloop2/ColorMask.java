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
public final class ColorMask {

    public final boolean red;
    public final boolean green;
    public final boolean blue;
    public final boolean alpha;

    public ColorMask(final boolean red, final boolean green, final boolean blue, final boolean alpha) {
        this.red = red;
        this.green = green;
        this.blue = blue;
        this.alpha = alpha;
    }
    
    public ColorMask() {
        this(true, true, true, true);
    }
    
    public ColorMask withRGB(final boolean red, final boolean green, final boolean blue) {
        return new ColorMask(red, green, blue, this.alpha);
    }
    
    public ColorMask withAlpha(final boolean alpha) {
        return new ColorMask(red, green, blue, alpha);
    }
}
