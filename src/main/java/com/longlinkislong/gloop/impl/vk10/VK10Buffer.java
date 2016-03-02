/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.longlinkislong.gloop.impl.vk10;

import com.longlinkislong.gloop.impl.Buffer;

/**
 *
 * @author zmichaels
 */
public final class VK10Buffer implements Buffer {
    long pBuffer = -1;
    long pMemory = -1;
    long size = -1;
    
    State state = State.INITIAL;
    
    @Override
    public boolean isValid() {
        return state == State.VALID;
    }
    
    
    enum State {
        INITIAL,
        CONFIGURED,
        VALID,
        DEAD
    }
}
