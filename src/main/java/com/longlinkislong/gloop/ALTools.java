/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.longlinkislong.gloop;

import com.longlinkislong.gloop.alspi.Driver;
import com.longlinkislong.gloop.alspi.DriverManager;
import java.util.Optional;

/**
 *
 * @author zmichaels
 */
public final class ALTools {
    private ALTools() {}        
    
    private static final class DriverHolder {
        @SuppressWarnings("rawtypes")
        private static final Driver DRIVER_INSTANCE;
                
        static {
            final DriverManager driverManager = new DriverManager();
            final String preferredDriverName = System.getProperty("com.longlinkislong.gloop.aldriver");
            @SuppressWarnings("rawtypes")
            final Optional<Driver> preferredDriver = driverManager.selectDriverByName(preferredDriverName);
            
            if(preferredDriver.isPresent()) {
                DRIVER_INSTANCE = preferredDriver.get();
            } else {
                DRIVER_INSTANCE = driverManager.selectBestDriver().orElseThrow(() -> new RuntimeException("No supported drivers found!"));
            }
        }
    }
    
    @SuppressWarnings("rawtypes")
    public static final Driver getDriverInstance() {
        return DriverHolder.DRIVER_INSTANCE;
    }
}
