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
public class GLDepthTest extends GLObject {
    public static final GLDepthFunc DEFAULT_DEPTH_FUNC = GLDepthFunc.GL_LESS;
    public final boolean depthTestEnabled;
    public final GLDepthFunc depthFunc;
    
    public GLDepthTest() {
        this(GLThread.getDefaultInstance());
    }
    
    public GLDepthTest(final GLThread thread) {
        this(thread, false, DEFAULT_DEPTH_FUNC);
    }
    
    public GLDepthTest(final boolean enabled, final GLDepthFunc depthFunc) {
        this(GLThread.getDefaultInstance(), enabled, depthFunc);
    }
    
    public GLDepthTest(
            final GLThread thread,
            final boolean enabled, final GLDepthFunc depthFunc) {
        
        super(thread);
        
        this.depthTestEnabled = enabled;
        this.depthFunc = depthFunc;
    }
    
    private final GLTask applyTask = new ApplyDepthFuncTask();
    
    public void applyDepthFunc() {
        this.applyTask.glRun(this.getThread());
    }
    
    public class ApplyDepthFuncTask extends GLTask {
        @Override
        public void run() {
            if(GLDepthTest.this.depthTestEnabled) {
                GL11.glEnable(GL11.GL_DEPTH_TEST);
            } else {
                GL11.glDisable(GL11.GL_DEPTH_TEST);
            }
            
            GL11.glDepthFunc(GLDepthTest.this.depthFunc.value);
        }
    }
}
