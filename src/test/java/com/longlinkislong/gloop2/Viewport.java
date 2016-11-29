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
public final class Viewport {

    public final int x;
    public final int y;
    public final int width;
    public final int height;
    public final double minDepth;
    public final double maxDepth;

    public Viewport(final int x, final int y, final int width, final int height, final double minDepth, final double maxDepth) {
        this.x = x;
        this.y = y;
        this.width = width;
        this.height = height;
        this.minDepth = minDepth;
        this.maxDepth = maxDepth;
    }

    public Viewport() {
        this(0, 0, 0, 0, 0.0, 1.0);
    }
    
    public Viewport withPosition(final int x, final int y) {
        return new Viewport(x, y, this.width, this.height, this.minDepth, this.maxDepth);
    }
    
    public Viewport withSize(final int width, final int height) {
        return new Viewport(this.x, this.y, width, height, this.minDepth, this.maxDepth);
    }
    
    public Viewport withDepthBounds(final double min, final double max) {
        return new Viewport(this.x, this.y, this.width, this.height, min, max);
    }
}
