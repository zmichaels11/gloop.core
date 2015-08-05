/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.longlinkislong.gloop;

import org.lwjgl.openal.AL10;

/**
 *
 * @author zmichaels
 */
public enum ALFormatType {

    AL_FORMAT_MONO8(AL10.AL_FORMAT_MONO8),
    AL_FORMAT_MONO16(AL10.AL_FORMAT_MONO16),
    AL_FORMAT_STEREO8(AL10.AL_FORMAT_STEREO8),
    AL_FORMAT_STEREO16(AL10.AL_FORMAT_STEREO16);
    final int value;

    ALFormatType(final int value) {

        this.value = value;
    }
}
