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

import com.longlinkislong.gloop.alspi.AuxiliaryEffectSlot;
import java.util.Objects;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.Marker;
import org.slf4j.MarkerFactory;

/**
 *
 * @author zmichaels
 */
public class ALAuxiliaryEffectSlot extends ALObject {

    private static final Logger LOGGER = LoggerFactory.getLogger("ALAuxiliaryEffectSlot");
    private static final Marker GL_MARKER = MarkerFactory.getMarker("GLOOP");
    protected transient volatile AuxiliaryEffectSlot effectSlot;

    public ALAuxiliaryEffectSlot() {
        this.init();
    }

    public final void init() {
        new InitTask().alRun();
    }

    public boolean isValid() {
        return this.effectSlot != null && this.effectSlot.isValid();
    }

    public class InitTask extends ALTask {

        @Override
        public void run() {
            if (isValid()) {
                throw new ALException.InvalidStateException("ALEffectSlot is already initialized!");
            }

            ALAuxiliaryEffectSlot.this.effectSlot = ALTools.getDriverInstance().auxiliaryEffectSlotCreate();
        }
    }

    public void delete() {
        new DeleteTask().alRun();
    }

    public class DeleteTask extends ALTask {

        @Override
        public void run() {
            if (!isValid()) {
                LOGGER.warn(GL_MARKER, "Attempted to delete invalid ALAuxiliaryEffectSlot!");                
            } else {                
                ALTools.getDriverInstance().auxiliaryEffectSlotDelete(effectSlot);
                effectSlot = null;
            }
        }
    }

    public void attachEffect(final ALEffect effect) {
        new AttachEffectTask(effect).alRun();
    }

    public final class AttachEffectTask extends ALTask {

        private final ALEffect effect;

        public AttachEffectTask(final ALEffect effect) {
            this.effect = Objects.requireNonNull(effect);
        }

        @Override
        public void run() {
            if (!isValid()) {
                throw new ALException.InvalidStateException("ALEffectSlot is not valid!");
            } else if (!effect.isValid()) {
                throw new ALException.InvalidStateException("ALEffect is not valid!");
            } else {
                ALTools.getDriverInstance().auxiliaryEffectSlotAttachEffect(effectSlot, effect.effect);
            }
        }
    }
}
