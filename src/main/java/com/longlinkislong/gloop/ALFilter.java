/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
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
public class ALFilter {

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
                throw new ALException("ALFilter is already initialized!");
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
                throw new ALException("ALFilter is not valid!");
            }

            ALTools.getDriverInstance().filterSetProperty(filter, name, value);
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
                throw new ALException("ALFilter is not valid!");
            }

            ALTools.getDriverInstance().filterSetProperty(filter, name, name);
        }
    }
}
