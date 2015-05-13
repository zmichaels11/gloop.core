/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.longlinkislong.gloop.nehe;

import com.longlinkislong.gloop.GLTask;
import com.longlinkislong.gloop.GLThread;

/**
 *
 * @author zmichaels
 */
public class NeHe01 {

    public NeHe01() {
        GLThread thread = GLThread.getDefaultInstance();
        GLTask dispCreateTask = thread.new OpenDisplayTask();
        
        thread.submitGLTask(dispCreateTask);                
    }

    public static void main(String[] args) {
        NeHe01 test = new NeHe01();
    }
}
