/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.longlinkislong.gloop;

import com.longlinkislong.gloop.alspi.AuxiliaryEffectSlot;
import java.util.Objects;

/**
 *
 * @author zmichaels
 */
public class ALAuxiliaryEffectSlot {

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
                throw new ALException("ALEffectSlot is not valid!");
            }

            ALTools.getDriverInstance().auxiliaryEffectSlotDelete(effectSlot);
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
