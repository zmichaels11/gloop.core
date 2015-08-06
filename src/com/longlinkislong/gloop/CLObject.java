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
public abstract class CLObject {
    private final CLThread thread;
    
    public CLObject() {
        this.thread = CLThread.getDefaultInstance();
    }
    
    public CLObject(final CLThread thread) {
        this.thread = Objects.requireNonNull(thread);
    }
    
    public CLThread getThread() {
        return this.thread;
    }
}
