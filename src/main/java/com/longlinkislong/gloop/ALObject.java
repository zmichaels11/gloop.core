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
public abstract class ALObject {
    static {
        NativeTools.getInstance().autoLoad();
    }
    
    private final ALThread thread;
    
    public ALObject() {
        this.thread = ALThread.getDefaultInstance();
    }
    
    public ALObject(final ALThread thread) {
        this.thread = Objects.requireNonNull(thread);
    }
    
    public ALThread getThread() {
        return this.thread;
    }
}
