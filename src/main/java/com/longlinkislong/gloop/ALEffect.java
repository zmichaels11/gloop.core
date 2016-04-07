/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.longlinkislong.gloop;

import com.longlinkislong.gloop.alspi.Effect;
import java.util.Objects;

/**
 *
 * @author zmichaels
 */
public abstract class ALEffect {

    private final ALEffectType type;
    protected Effect effect;

    public ALEffect(final ALEffectType type) {
        this.type = Objects.requireNonNull(type);
        this.init();
    }

    public final boolean isValid() {
        return this.effect != null && this.effect.isValid();
    }
    
    public final ALEffect init() {
        new InitTask().alRun();
        return this;
    }

    public final class InitTask extends ALTask {

        @Override
        public void run() {
            if (isValid()) {
                throw new ALException("ALEffect is already initialized!");
            }

            ALEffect.this.effect = ALTools.getDriverInstance().effectCreate(ALEffect.this.type.value);
        }
    }
    
    public final ALEffect delete() {
        new DeleteTask().alRun();
        return this;
    }

    protected void resetValues() {
        
    }
    
    public final class DeleteTask extends ALTask {

        @Override
        public void run() {
            if (!isValid()) {
                throw new ALException("ALEffect is not valid!");
            }

            ALTools.getDriverInstance().effectDelete(effect);
            ALEffect.this.effect = null;
            ALEffect.this.resetValues();
        }
    }

    protected class SetPropertyITask extends ALTask {

        final int name;
        final int value;

        protected SetPropertyITask(final int name, final int value) {
            this.name = name;
            this.value = value;
        }

        @Override
        public void run() {
            if (!isValid()) {
                throw new ALException("ALEffect is not valid!");
            }

            ALTools.getDriverInstance().effectSetProperty(
                    ALEffect.this.effect,
                    this.name,
                    this.value);
        }
    }

    protected class SetPropertyFTask extends ALTask {

        final int name;
        final float value;

        protected SetPropertyFTask(final int name, final float value) {
            this.name = name;
            this.value = value;
        }

        @Override
        public void run() {
            if (!isValid()) {
                throw new ALException("ALEffect is not valid!");
            }

            ALTools.getDriverInstance().effectSetProperty(
                    ALEffect.this.effect,
                    this.name,
                    this.value);
        }
    }
}
