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
import org.lwjgl.openal.AL10;

/**
 * Supported sound formats for ALBuffer objects.
 *
 * @author zmichaels
 * @since 16.03.21
 */
public enum ALSoundFormat {
    /**
     * Single source 8bit sound.
     *
     * @since 16.03.21
     */
    AL_FORMAT_MONO8(AL10.AL_FORMAT_MONO8),
    /**
     * Single source 16bit sound.
     *
     * @since 16.03.21
     */
    AL_FORMAT_MONO16(AL10.AL_FORMAT_MONO16),
    /**
     * Stereo source 8bit sound.
     *
     * @since 16.03.21
     */
    AL_FORMAT_STEREO8(AL10.AL_FORMAT_STEREO8),
    /**
     * Stereo source 16bit sound.
     *
     * @since 16.03.21
     */
    AL_FORMAT_STEREO16(AL10.AL_FORMAT_STEREO16);

    public final int value;

    ALSoundFormat(final int value) {
        this.value = value;
    }

    /**
     * Converts an ALenum value to the corresponding ALSoundFormat.
     *
     * @param alEnum the ALenum.
     * @return The ALSoundFormat wrapped in an Optional. Will return an empty
     * Optional if the ALenum has no supported corresponding ALSoundFormat.
     * @since 16.03.21
     */
    public static Optional<ALSoundFormat> of(final int alEnum) {
        for (ALSoundFormat testFormat : values()) {
            if (testFormat.value == alEnum) {
                return Optional.of(testFormat);
            }
        }

        return Optional.empty();
    }
}
