/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.longlinkislong.gloop;

import java.util.Objects;

/**
 *
 * @author zmichaels
 */
public abstract class GLObject {
    private final GLThread associatedThread;
    
    public GLObject() {
        this(GLThread.getDefaultInstance());
    }
    
    public GLObject(final GLThread thread) {
        Objects.requireNonNull(thread, "OpenGL thread cannot be null!");
        this.associatedThread = thread;
    }
    
    public final GLThread getGLThread() {
        return this.associatedThread;
    }        
    
    public class SyncQuery extends GLQuery<Boolean> {
        @Override
        public Boolean call() throws Exception {
            return true;
        }        
    }
}
