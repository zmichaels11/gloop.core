/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.longlinkislong.gloop.impl.gl45;

import com.longlinkislong.gloop.impl.Program;
import java.util.Map;
import java.util.WeakHashMap;

/**
 *
 * @author zmichaels
 */
public final class GL45Program implements Program {
    static final Map<Thread, Integer> CURRENT_PROGRAM = new WeakHashMap<>();
    
    int programId = -1;
}
