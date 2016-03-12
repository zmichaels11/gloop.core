/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.longlinkislong.gloop;

import com.longlinkislong.gloop.alspi.Device;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.CancellationException;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Future;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.Marker;
import org.slf4j.MarkerFactory;

/**
 *
 * @author zmichaels
 */
public class ALThread {
    private static final Marker SYS_MARKER = MarkerFactory.getMarker("SYSTEM");
    private static final Logger LOGGER = LoggerFactory.getLogger("ALThread");
    private static final transient Map<Thread, ALThread> THREAD_MAP = new HashMap<>();
    private transient Thread internalThread = null;
    private Device device;
    private boolean isKill = false;
    
    private transient final ExecutorService internalExecutor = new ThreadPoolExecutor(1, 1, 0L, TimeUnit.MILLISECONDS, new LinkedBlockingQueue<>()) {
        @Override
        protected void afterExecute(final Runnable task, final Throwable ex) {
            super.afterExecute(task, ex);
            
            if(task != null && task instanceof Future<?>) {
                try {
                    final Future<?> future = (Future<?>) task;
                    
                    if(future.isDone()) {
                        future.get();
                    }
                } catch(CancellationException ce) {
                    LOGGER.error(SYS_MARKER, "ALTask was canceled.");
                    LOGGER.debug(SYS_MARKER, ce.getMessage(), ce);
                } catch(ExecutionException ee) {
                    LOGGER.error(SYS_MARKER, "Error executing ALTask!");
                    LOGGER.debug(SYS_MARKER, ee.getMessage(), ee);
                } catch(InterruptedException ie) {
                    LOGGER.error(SYS_MARKER, "ALThread was interrupted! Resetting interrupt state.");
                    LOGGER.debug(SYS_MARKER, ie.getMessage(), ie);
                    Thread.currentThread().interrupt();
                }
            }
            
            if(ex != null) {
                LOGGER.error(SYS_MARKER, "Error executing ALTask!");
                LOGGER.debug(ex.getMessage(), ex);
            }
        }
    };        
    
    private ALThread() {
        this.internalExecutor.execute(this::init);
    }
    
    private void init() {
        internalThread = Thread.currentThread();
        device = ALTools.getDriverInstance().deviceCreate();
        THREAD_MAP.put(internalThread, this);
    }
    
    public void shutdown() {
        if(isKill) {
            throw new IllegalStateException("ALThread has already shutdown!");
        }
        
        try {
            internalExecutor.submit(() -> {                
                ALTools.getDriverInstance().deviceDelete(device);                
                device = null;
                THREAD_MAP.remove(internalThread);
                internalThread = null;
                isKill = true;                
            }).get();
        } catch (InterruptedException ex) {
            throw new RuntimeException("Synchronized task: [ALThread.destroy] was interrupted!", ex);
        } catch (ExecutionException ex) {
            throw new ALException("Unable to delete ALDevice!", ex);
        }
        
        internalExecutor.shutdown();
    }
    
    public void submitALTask(final ALTask task) {
        this.internalExecutor.execute(task);
    }
    
    public <ReturnType> ALFuture<ReturnType> submitALQuery(final ALQuery<ReturnType> query) {
        final Future<ReturnType> raw = this.internalExecutor.submit(query);
        
        return new ALFuture<>(raw);
    }
    
    public boolean isCurrent() {
        return Thread.currentThread() == this.internalThread;
    }
    
    private static final class DefaultHolder {
        private static final ALThread INSTANCE = new ALThread();
    }
    
    public static ALThread getDefaultInstance() {
        return DefaultHolder.INSTANCE;
    }
}
