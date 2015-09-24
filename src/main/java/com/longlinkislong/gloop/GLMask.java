/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.longlinkislong.gloop;

import static com.longlinkislong.gloop.GLAsserts.checkGLError;
import static com.longlinkislong.gloop.GLAsserts.glErrorMsg;
import org.lwjgl.opengl.GL11;

/**
 *
 * @author zmichaels
 */
public class GLMask extends GLObject {

    public static final boolean DEFAULT_RED_MASK = true;
    public static final boolean DEFAULT_GREEN_MASK = true;
    public static final boolean DEFAULT_BLUE_MASK = true;
    public static final boolean DEFAULT_ALPHA_MASK = true;
    public static final boolean DEFAULT_DEPTH_MASK = true;
    public static final int DEFAULT_STENCIL_MASK = 0xFFFFFFFF;

    public final boolean red, green, blue, alpha;
    public final boolean depth;
    public final int stencil;

    public GLMask() {
        this(
                GLThread.getDefaultInstance(),
                DEFAULT_RED_MASK, DEFAULT_GREEN_MASK, DEFAULT_BLUE_MASK, DEFAULT_ALPHA_MASK,
                DEFAULT_DEPTH_MASK,
                DEFAULT_STENCIL_MASK);
    }

    public GLMask(final GLThread thread) {
        this(
                thread,
                DEFAULT_RED_MASK, DEFAULT_GREEN_MASK, DEFAULT_BLUE_MASK, DEFAULT_ALPHA_MASK,
                DEFAULT_DEPTH_MASK,
                DEFAULT_STENCIL_MASK);
    }    

    public GLMask(
            final GLThread thread,
            final boolean red, final boolean green, final boolean blue, final boolean alpha,
            final boolean depth,
            final int stencil) {

        super(thread);
        this.red = red;
        this.green = green;
        this.blue = blue;
        this.alpha = alpha;
        this.depth = depth;
        this.stencil = stencil;
    }
    
    public GLMask withColorMask(final boolean red, final boolean green, final boolean blue, final boolean alpha) {
        return this.alpha == alpha && this.red == red && this.green == green & this.blue == blue
                ? this
                : new GLMask(this.getThread(), red, green, blue, alpha, this.depth, this.stencil);
    }
    
    public GLMask withDepthMask(final boolean depth) {
        return this.depth == depth
                ? this
                : new GLMask(this.getThread(), this.red, this.green, this.blue, this.alpha, depth, this.stencil);
    }
    
    public GLMask withStencilMask(final int stencilMask) {
        return this.stencil == stencilMask
                ? this
                : new GLMask(this.getThread(), this.red, this.green, this.blue, this.alpha, this.depth, stencilMask);
    }
    
    public GLMask withGLThread(final GLThread thread) {
        return this.getThread() == thread
                ? this
                : new GLMask(thread, this.red, this.green, this.blue, this.alpha, this.depth, this.stencil);
    }

    private ApplyTask apply = null;

    public void applyMask() {
        if(this.apply == null) {
            this.apply = new ApplyTask();
        }
        
        this.apply.glRun(this.getThread());
    }

    public class ApplyTask extends GLTask {

        @Override
        public void run() {
            final GLThread thread = GLThread.getCurrent().orElseThrow(GLException::new);
            
            thread.currentMask = GLMask.this.withGLThread(thread);
            
            GL11.glColorMask(GLMask.this.red, GLMask.this.green, GLMask.this.blue, GLMask.this.alpha);
            assert checkGLError() : glErrorMsg("glColorMask(BBBB)", GLMask.this.red, GLMask.this.green, GLMask.this.blue, GLMask.this.alpha);
            
            GL11.glDepthMask(GLMask.this.depth);
            assert checkGLError() : glErrorMsg("glDepthMask(D)", GLMask.this.depth);
            
            GL11.glStencilMask(GLMask.this.stencil);
            assert checkGLError() : glErrorMsg("glStencilMask(I)", GLMask.this.stencil);
        }
    }
}
