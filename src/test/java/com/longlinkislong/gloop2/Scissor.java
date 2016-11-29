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
public final class Scissor {
    public final int x;
    public final int y;
    public final int width;
    public final int height;
    
    public Scissor(final int x, final int y, final int width, final int height) {
        this.x = x;
        this.y = y;
        this.width = width;
        this.height = height;
    }
    
    public Scissor() {
        this(0, 0, 0, 0);
    }
    
    public Scissor withPosition(final int x, final int y) {
        return new Scissor(x, y, this.width, this.height);
    }
    
    public Scissor withSize(final int width, final int height) {        
        return new Scissor(this.x, this.y, width, height);
    }
}
