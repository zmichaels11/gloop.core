/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.longlinkislong.gloop;

import org.lwjgl.openal.EXTEfx;

/**
 *
 * @author zmichaels
 */
public class ALEffectEcho extends ALEffect {

    public static final float DEFAULT_DELAY = 0.1f;
    public static final float DEFAULT_LRDELAY = 0.1f;
    public static final float DEFAULT_DAMPING = 0.5f;
    public static final float DEFAULT_FEEDBACK = 0.5f;
    public static final float DEFAULT_SPREAD = -1.0f;

    private float delay = DEFAULT_DELAY;
    private float lrDelay = DEFAULT_LRDELAY;
    private float damping = DEFAULT_DAMPING;
    private float feedback = DEFAULT_FEEDBACK;
    private float spread = DEFAULT_SPREAD;

    @Override
    protected void resetValues() {
        this.delay = DEFAULT_DELAY;
        this.lrDelay = DEFAULT_LRDELAY;
        this.damping = DEFAULT_DAMPING;
        this.feedback = DEFAULT_FEEDBACK;
        this.spread = DEFAULT_SPREAD;
    }
    
    public ALEffectEcho() {
        super(ALEffectType.AL_EFFECT_ECHO);
    }

    public float getDelay() {
        return this.delay;
    }

    public float getLRDelay() {
        return this.lrDelay;
    }

    public float getDamping() {
        return this.damping;
    }

    public float getFeedback() {
        return this.feedback;
    }

    public float getSpread() {
        return this.spread;
    }

    public ALEffectEcho setDelay(final float delay) {
        new SetDelayTask(delay).alRun();
        return this;
    }

    public ALEffectEcho setLRDelay(final float lrDelay) {
        new SetLRDelayTask(lrDelay).alRun();
        return this;
    }

    public ALEffectEcho setDamping(final float damping) {
        new SetDampingTask(damping).alRun();
        return this;
    }

    public ALEffectEcho setFeedback(final float feedback) {
        new SetFeedbackTask(feedback).alRun();
        return this;
    }

    public ALEffectEcho setSpread(final float spread) {
        new SetSpreadTask(spread).alRun();
        return this;
    }

    public final class SetDelayTask extends SetPropertyFTask {

        private final float delay;

        public SetDelayTask(final float delay) {
            super(EXTEfx.AL_ECHO_DELAY, delay);
            this.delay = delay;
        }

        @Override
        public void run() {
            super.run();
            ALEffectEcho.this.delay = this.delay;
        }
    }

    public final class SetLRDelayTask extends SetPropertyFTask {

        private final float lrDelay;

        public SetLRDelayTask(final float lrDelay) {
            super(EXTEfx.AL_ECHO_LRDELAY, lrDelay);
            this.lrDelay = lrDelay;
        }

        @Override
        public void run() {
            super.run();
            ALEffectEcho.this.lrDelay = this.lrDelay;
        }
    }
    
    public final class SetDampingTask extends SetPropertyFTask {
        private final float damping;
        
        public SetDampingTask(final float damping) {
            super(EXTEfx.AL_ECHO_DAMPING, damping);
            this.damping = damping;
        }
        
        @Override
        public void run() {
            super.run();
            ALEffectEcho.this.damping = this.damping;
        }
    }
    
    public final class SetFeedbackTask extends SetPropertyFTask {
        private final float feedback;
        
        public SetFeedbackTask(final float feedback) {
            super(EXTEfx.AL_ECHO_FEEDBACK, feedback);
            this.feedback = feedback;
        }
        
        @Override
        public void run() {
            super.run();
            ALEffectEcho.this.feedback = this.feedback;
        }
    }
    
    public final class SetSpreadTask extends SetPropertyFTask {
        private final float spread;
        
        public SetSpreadTask(final float spread) {
            super(EXTEfx.AL_ECHO_SPREAD, spread);
            this.spread = spread;
        }
        
        @Override
        public void run() {
            super.run();
            ALEffectEcho.this.spread = this.spread;
        }
    }
}
