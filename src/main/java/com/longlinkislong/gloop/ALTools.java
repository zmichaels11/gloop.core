/*
 * Copyright (c) 2016, longlinkislong.com
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 *
 * * Redistributions of source code must retain the above copyright notice, this
 *   list of conditions and the following disclaimer.
 * * Redistributions in binary form must reproduce the above copyright notice,
 *   this list of conditions and the following disclaimer in the documentation
 *   and/or other materials provided with the distribution.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS"
 * AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE
 * IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE
 * ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT HOLDER OR CONTRIBUTORS BE
 * LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR
 * CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF
 * SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS
 * INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN
 * CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE)
 * ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE
 * POSSIBILITY OF SUCH DAMAGE.
 */
package com.longlinkislong.gloop;

import com.longlinkislong.gloop.alspi.Driver;
import com.longlinkislong.gloop.alspi.DriverManager;
import java.util.Optional;

/**
 * The utility class for OpenAL.
 *
 * @author zmichaels
 * @since 16.06.10
 */
public final class ALTools {

    private ALTools() {
    }

    private static final class DriverHolder {

        private DriverHolder() {
        }

        @SuppressWarnings("rawtypes")
        private static final Driver DRIVER_INSTANCE;

        static {
            final DriverManager driverManager = new DriverManager();
            final String preferredDriverName = System.getProperty("com.longlinkislong.gloop.aldriver");
            @SuppressWarnings("rawtypes")
            final Optional<Driver> preferredDriver = driverManager.selectDriverByName(preferredDriverName);

            if (preferredDriver.isPresent()) {
                DRIVER_INSTANCE = preferredDriver.get();
            } else {
                DRIVER_INSTANCE = driverManager.selectBestDriver().orElseThrow(() -> new RuntimeException("No supported drivers found!"));
            }
        }
    }

    /**
     * Retrieves the ALTools object.
     *
     * @return the ALTools.
     * @since 16.06.10
     */
    @SuppressWarnings("rawtypes")
    public static Driver getDriverInstance() {
        return DriverHolder.DRIVER_INSTANCE;
    }
}
