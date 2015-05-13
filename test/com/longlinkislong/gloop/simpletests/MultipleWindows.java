/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.longlinkislong.gloop.simpletests;

import com.longlinkislong.gloop.GLTask;
import com.longlinkislong.gloop.GLThread;
import com.longlinkislong.gloop.GLWindow;
import org.lwjgl.opengl.GL11;

/**
 *
 * @author zmichaels
 */
public class MultipleWindows {

    public void doDemo() {
        GLWindow w0 = new GLWindow();
        GLWindow w1 = new GLWindow();

        init(w0);
        init(w1);
    }

    private void init(final GLWindow window) {
        GLThread thread = window.getThread();

        thread.scheduleGLTask(new Colorizer());
        thread.scheduleGLTask(window.new UpdateTask());
        thread.submitGLTask(window.new SetVisibleTask(true));
    }

    private class Colorizer extends GLTask {

        float t = 0;
        float r = (float) Math.random();
        float g = (float) Math.random();
        float b = (float) Math.random();
        float a = (float) Math.random();
        float rate = (float) (0.01 + 0.01 * (0.5 - Math.random()));

        @Override
        public void run() {
            this.a = (float) Math.abs(Math.sin(t + this.a));
            this.b = (float) Math.abs(Math.sin(t + this.b + 0.005 *  Math.random()));
            this.r = (float) Math.abs(Math.cos(t + this.r + 0.005 * Math.random()));
            this.g = (float) Math.abs(Math.cos(t + this.g + 0.005 * Math.random()));

            GL11.glClearColor(this.r, this.g, this.b, this.a);
            GL11.glClear(GL11.GL_COLOR_BUFFER_BIT);
            this.t += rate;
        }
    }

    public static void main(String[] args) {
        new MultipleWindows().doDemo();
    }
}
