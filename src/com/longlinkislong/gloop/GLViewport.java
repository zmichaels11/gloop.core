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
public class GLViewport extends GLObject {

    public final int x;
    public final int y;
    public final int width;
    public final int height;

    public GLViewport(final int x, final int y, final int w, final int h) {
        super();
        this.x = x;
        this.y = y;
        this.width = w;
        this.height = h;
    }

    public GLViewport(
            final GLThread thread,
            final int x, final int y, final int w, final int h) {

        super(thread);
        this.x = x;
        this.y = y;
        this.width = w;
        this.height = h;
    }

    private final ApplyViewportTask applyTask = new ApplyViewportTask();

    public void applyViewport() {
        this.applyTask.glRun(this.getThread());
    }

    public class ApplyViewportTask extends GLTask {

        @Override
        public void run() {
            GL11.glViewport(
                    GLViewport.this.x, GLViewport.this.y,
                    GLViewport.this.width, GLViewport.this.height);
        }
    }
}
