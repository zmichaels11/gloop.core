/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.longlinkislong.gloop2.vkimpl;

/**
 *
 * @author zmichaels
 */
public final class CommandDependency {
    public final long semaphore;
    public final int stage;     
    
    public CommandDependency(final int stage, final long semaphore) {
        this.stage = stage;
        this.semaphore = semaphore;
    }
}
