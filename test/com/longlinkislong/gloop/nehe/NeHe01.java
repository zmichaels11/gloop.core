/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.longlinkislong.gloop.nehe;

import com.longlinkislong.gloop.GLWindow;

/**
 *
 * @author zmichaels
 */
public class NeHe01 {

    public NeHe01() {        
        GLWindow window = new GLWindow();
        
        window.setVisible(true);
        window.getThread().scheduleGLTask(window.new UpdateTask());                        
    }

    public static void main(String[] args) {
        NeHe01 test = new NeHe01();
    }
}
