/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.longlinkislong.gloop.nehe;

import com.longlinkislong.gloop.GLTask;
import com.longlinkislong.gloop.GLTexture;
import com.longlinkislong.gloop.GLTextureInternalFormat;
import com.longlinkislong.gloop.GLTools;
import com.longlinkislong.gloop.GLWindow;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL20;

/**
 *
 * @author zmichaels
 */
public class NeHe01 {

    static{        
        System.setProperty("gloop.opengl.version", "4.5");
        System.setProperty("gloop.gltools.dsa", "arbdsa");
        System.setProperty("debug", "true");
    }
    
    public NeHe01() {
        final GLWindow window = new GLWindow(640, 480, "NeHe01");

        window.getGLThread().submitGLTask(GLTask.create(()->{
            System.out.printf("OpenGL Version: %s\n", GL11.glGetString(GL11.GL_VERSION));
            System.out.printf("OpenGL Vendor: %s\n", GL11.glGetString(GL11.GL_VENDOR));
            System.out.printf("OpenGL Renderer: %s\n", GL11.glGetString(GL11.GL_RENDERER));
            System.out.printf("OpenGL GLSL Version: %s\n", GL11.glGetString(GL20.GL_SHADING_LANGUAGE_VERSION));            
            System.out.printf("Using DSA driver: %s\n", GLTools.getDSAImplement());
            System.out.printf("Preferred texture format: %s\n", GLTexture.getPreferredTextureFormat(GLTextureInternalFormat.GL_RGBA8));
        }));
        window.getGLThread().scheduleGLTask(window.new UpdateTask());
        
        window.setVisible(true);        
    }
    public static void main(String[] args) {
        NeHe01 test = new NeHe01();
    }
}
