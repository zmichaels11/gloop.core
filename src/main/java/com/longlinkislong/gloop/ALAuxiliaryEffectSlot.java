/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
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
                throw new ALException("ALEffectSlot is already initialized!");
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
                throw new ALException("ALEffectSlot is not valid!");
            } else if (!effect.isValid()) {
                throw new ALException("ALEffect is not valid!");
            } else {
                ALTools.getDriverInstance().auxiliaryEffectSlotAttachEffect(effectSlot, effect.effect);
            }
        }
    }
}
