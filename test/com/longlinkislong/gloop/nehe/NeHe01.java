/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.longlinkislong.gloop.nehe;

import com.longlinkislong.gloop.GLTask;
import com.longlinkislong.gloop.GLWindow;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL20;

/**
 *
 * @author zmichaels
 */
public class NeHe01 {

    public NeHe01() {
        final GLWindow window = new GLWindow();

        window.getGLThread().submitGLTask(GLTask.create(()->{
            System.out.printf("OpenGL Version: %s\n", GL11.glGetString(GL11.GL_VERSION));
            System.out.printf("OpenGL Vendor: %s\n", GL11.glGetString(GL11.GL_VENDOR));
            System.out.printf("OpenGL Renderer: %s\n", GL11.glGetString(GL11.GL_RENDERER));
            System.out.printf("OpenGL GLSL Version: %s\n", GL11.glGetString(GL20.GL_SHADING_LANGUAGE_VERSION));
        }));
        window.getGLThread().scheduleGLTask(window.new UpdateTask());
        
        window.setVisible(true);        
    }
    public static void main(String[] args) {
        NeHe01 test = new NeHe01();
    }
}
