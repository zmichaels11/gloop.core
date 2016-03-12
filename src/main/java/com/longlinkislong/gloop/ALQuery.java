/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.longlinkislong.gloop;

import java.util.concurrent.Callable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 * @author zmichaels
 * @param <ReturnType>
 */
public abstract class ALQuery<ReturnType> implements Callable<ReturnType> {
    private static final Logger LOGGER = LoggerFactory.getLogger("ALQuery");
    
    public final ReturnType alCall(final ALThread thread) {
        if(thread == null) {
            return this.alCall(ALThread.getDefaultInstance());
        }
        
        try {
            if(thread.isCurrent()) {
                return this.call();
            } else {
                return thread.submitALQuery(this).get();
            }
        } catch(InterruptedException ex) {
            LOGGER.error("ALQuery interrupted! Handling interruption and restoring interruption flag!");
            LOGGER.debug(ex.getMessage(), ex);
            
            Thread.currentThread().interrupt();
            return this.handleInterruption();
        } catch (Exception ex) {
            throw new ALException("Unable to call ALQuery!", ex);
        }
    }
    
    public final ReturnType alCall() {
        final ALThread thread = ALThread.getDefaultInstance();
        
        try {
            if(thread.isCurrent()) {
                return this.call();
            } else {
                return thread.submitALQuery(this).get();
            }
        } catch(InterruptedException ex) {
            LOGGER.error("ALQuery interrupted! Handling interruption and restoring interruption flag!");
            LOGGER.debug(ex.getMessage(), ex);
            return this.handleInterruption();
        } catch(Exception ex) {
            throw new ALException("Unable to call ALQuery!", ex);
        }
    }
    
    protected ReturnType handleInterruption() {
        return null;
    }
    
    
    public static <ReturnType> ALQuery<ReturnType> create(final Callable<ReturnType> query) {
        return new ALQuery<ReturnType>() {
            @Override
            public ReturnType call() throws Exception {
                return query.call();
            }
        };
    }
}
