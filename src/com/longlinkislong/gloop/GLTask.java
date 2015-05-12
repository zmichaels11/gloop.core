package com.longlinkislong.gloop;

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

/**
 *
 * @author zmichaels
 */
public abstract class GLTask implements Runnable {            
    public final void glRun() {
        this.run();
    }
    
    public GLTask andThen(final GLTask next) {
        return new GLTask() {
            @Override
            public void run() {
                GLTask.this.glRun();
                next.glRun();
            }
        };
    }        
    
    public static GLTask create(final Runnable task) {
        return new GLTask() {
            @Override
            public void run() {
                task.run();
            }
        };
    }
}
