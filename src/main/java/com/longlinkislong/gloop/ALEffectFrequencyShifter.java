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

import java.util.Optional;
import org.lwjgl.openal.EXTEfx;

/**
 *
 * @author zmichaels
 */
public final class ALEffectFrequencyShifter extends ALEffect {
    public enum Direction {
        DOWN(0),
        UP(1),
        OFF(2);
        final int value;
        Direction(final int value) {
            this.value = value;
        }
        
        public static Optional<Direction> of(final int alEnum) {
            for(Direction dir : values()) {
                if(dir.value == alEnum) {
                    return Optional.of(dir);
                }
            }
                        
            return Optional.empty();
        }
    }
    
    public static final float DEFAULT_FREQUENCY = 0.0f;
    public static final Direction DEFAULT_LEFT_DIRECTION = Direction.DOWN;
    public static final Direction DEFAULT_RIGHT_DIRECTION = Direction.DOWN;
    
    private float frequency = DEFAULT_FREQUENCY;
    private Direction leftDirection = DEFAULT_LEFT_DIRECTION;
    private Direction rightDirection = DEFAULT_RIGHT_DIRECTION;
    
    @Override
    protected void resetValues() {
        this.frequency = DEFAULT_FREQUENCY;
        this.leftDirection = DEFAULT_LEFT_DIRECTION;
        this.rightDirection = DEFAULT_RIGHT_DIRECTION;
    }
    
    public ALEffectFrequencyShifter() {
        super(ALEffectType.AL_EFFECT_FREQUENCY_SHIFTER);
    }
    
    public float getFrequency() {
        return this.frequency;
    }
    
    public Direction getLeftDirection() {
        return this.leftDirection;
    }
    
    public Direction getRightDirection() {
        return this.rightDirection;
    }
    
    public ALEffectFrequencyShifter setFrequency(final float freq) {
        new SetFrequencyTask(freq).alRun();
        return this;
    }
    
    public ALEffectFrequencyShifter setLeftDirection(final Direction left) {
        new SetLeftDirectionTask(left).alRun();
        return this;
    }
    
    public ALEffectFrequencyShifter setRightDirection(final Direction right) {
        new SetRightDirectionTask(right).alRun();
        return this;
    }
    
    public final class SetFrequencyTask extends SetPropertyFTask {
        private final float frequency;
        
        public SetFrequencyTask(final float frequency) {
            super(EXTEfx.AL_FREQUENCY_SHIFTER_FREQUENCY, frequency);
            this.frequency = frequency;
        }
        
        @Override
        public void run() {
            super.run();
            ALEffectFrequencyShifter.this.frequency = this.frequency;
        }
    }
    
    public final class SetLeftDirectionTask extends SetPropertyITask {
        private final Direction direction;
        
        public SetLeftDirectionTask(final Direction left) {
            super(EXTEfx.AL_FREQUENCY_SHIFTER_LEFT_DIRECTION, left.value);
            this.direction = left;
        }
        
        @Override
        public void run() {
            super.run();
            ALEffectFrequencyShifter.this.leftDirection = this.direction;
        }
    }
    
    public final class SetRightDirectionTask extends SetPropertyITask {
        private final Direction direction;
        
        public SetRightDirectionTask(final Direction right) {
            super(EXTEfx.AL_FREQUENCY_SHIFTER_RIGHT_DIRECTION, right.value);
            this.direction = right;
        }
        
        @Override
        public void run() {
            super.run();
            ALEffectFrequencyShifter.this.rightDirection = this.direction;
        }
    }
}
