/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.longlinkislong.gloop;

import org.lwjgl.opengl.GL11;

/**
 *
 * @author zmichaels
 */
public class GLClear extends GLObject {
    public final float red, green, blue, alpha;
    public final double depth;
    
    public GLClear() {
        this(0f, 0f, 0f, 0f, 1d);
    }
    
    public GLClear(final GLThread thread) {
        this(thread, 0f, 0f, 0f, 0f, 1d);
    }
    
    public GLClear(
            final float r, final float g, final float b, final float a, 
            final double depth) {
        
        super();
        
        this.red = r;
        this.green = g;
        this.blue = b;
        this.alpha = a;
        this.depth = depth;        
    }
    
    public GLClear(
            final GLThread thread,
            final float r, final float g, final float b, final float a, 
            final double depth) {
        
        super(thread);
        
        this.red = r;
        this.green = g;
        this.blue = b;
        this.alpha = a;
        this.depth = depth;                
    }
    
    private final ApplyClearTask init = new ApplyClearTask();
    
    public final void applyClear() {
        this.init.glRun(this.getThread());
    }
    
    public class ApplyClearTask extends GLTask {
        @Override
        public void run() {
            GL11.glClearColor(
                    GLClear.this.red,
                    GLClear.this.green,
                    GLClear.this.blue,
                    GLClear.this.alpha);
            GL11.glClearDepth(GLClear.this.depth);
        }
    }
    
    private ClearTask lastClearTask = null;
    
    public void clear(final GLClearBufferMode... bits) {
        this.clear(bits, 0, bits.length);
    }
    
    public void clear(
            final GLClearBufferMode[] bits, final int offset, final int length) {
        
        if(this.lastClearTask != null) {
            int bitValue = 0;
            
            for(GLClearBufferMode bit : bits) {
                bitValue |= bit.value;
            }
            
            if(this.lastClearTask.bits != bitValue) {
                this.lastClearTask = new ClearTask(bits, offset, length);
            }
        } else {
            this.lastClearTask = new ClearTask(bits, offset, length);
        }
        
        this.lastClearTask.glRun(this.getThread());
    }
    
    public class ClearTask extends GLTask {
        final int bits;
        
        public ClearTask(final GLClearBufferMode... bits) {
            this(bits, 0, bits.length);
        }
        
        public ClearTask(
                final GLClearBufferMode[] bits, 
                final int offset, 
                final int length) {
            
            int tBits = 0;
            
            for(GLClearBufferMode bit : bits) {
                tBits |= bit.value;
            }
            
            this.bits = tBits;
        }
        @Override
        public void run() {
            GL11.glClear(this.bits);
        }
    }
}
