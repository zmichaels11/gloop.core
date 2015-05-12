/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.longlinkislong.gloop;

import java.util.concurrent.Callable;

/**
 *
 * @author zmichaels
 * @param <ReturnType>
 */
public abstract class GLQuery<ReturnType> implements Callable<ReturnType>{    
    public GLFuture<ReturnType> submit(final GLThread thread) {
        return thread.submitGLQuery(this);
    }
    
    public final ReturnType glCall() {
        try {
            return this.call();
        } catch(final Exception ex) {
            throw new GLException(ex);
        }
    }
    
    public static <ReturnType> GLQuery<ReturnType> create(
            final Callable<ReturnType> query) {
        
        return new GLQuery<ReturnType>() {

            @Override
            public ReturnType call() throws Exception {
                return query.call();
            }
            
        };
    }
}
