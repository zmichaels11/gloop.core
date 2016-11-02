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

import com.longlinkislong.gloop.alspi.Filter;
import java.util.Objects;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.Marker;
import org.slf4j.MarkerFactory;

/**
 *
 * @author zmichaels
 */
public class ALFilter extends ALObject {

    private static final Logger LOGGER = LoggerFactory.getLogger("ALFilter");
    private static final Marker GL_MARKER = MarkerFactory.getMarker("GLOOP");

    protected volatile transient Filter filter;
    private final ALFilterType type;

    public ALFilter(final ALFilterType type) {
        this.type = Objects.requireNonNull(type);
        this.init();
    }

    public final boolean isValid() {
        return this.filter != null && this.filter.isValid();
    }

    protected void resetValues() {

    }

    public final void init() {
        new InitTask().alRun();
    }

    public final class InitTask extends ALTask {

        @Override
        public void run() {
            if (isValid()) {
                throw new ALException.InvalidStateException("ALFilter is already initialized!");
            }

            ALFilter.this.filter = ALTools.getDriverInstance().filterCreate(type.value);
        }
    }

    public final void delete() {
        new DeleteTask().alRun();
    }

    public final class DeleteTask extends ALTask {

        @Override
        public void run() {
            if (!isValid()) {
                LOGGER.warn(GL_MARKER, "Attempted to delete invalid ALFilter!");
            } else {
                ALTools.getDriverInstance().filterDelete(ALFilter.this.filter);
                ALFilter.this.filter = null;
                ALFilter.this.resetValues();
            }
        }
    }

    protected class SetPropertyITask extends ALTask {

        private final int name;
        private final int value;

        protected SetPropertyITask(final int name, final int value) {
            this.name = name;
            this.value = value;
        }

        @Override
        public void run() {
            if (!isValid()) {
                throw new ALException.InvalidStateException("ALFilter is not valid!");
            }

            ALTools.getDriverInstance().filterSetProperty(
                    ALFilter.this.filter, 
                    this.name, 
                    this.value);
        }
    }

    protected class SetPropertyFTask extends ALTask {

        private final int name;
        private final float value;

        protected SetPropertyFTask(final int name, final float value) {
            this.name = name;
            this.value = value;
        }

        @Override
        public void run() {
            if (!isValid()) {
                throw new ALException.InvalidStateException("ALFilter is not valid!");
            }

            ALTools.getDriverInstance().filterSetProperty(
                    ALFilter.this.filter, 
                    this.name, 
                    this.value);
        }
    }
}
