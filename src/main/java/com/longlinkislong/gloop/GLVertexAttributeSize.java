/* 
 * Copyright (c) 2015, longlinkislong.com
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

/**
 * The number of elements each attribute type occupies.
 * @author zmichaels
 * @since 15.06.24
 */
public enum GLVertexAttributeSize {

    /**
     * 1 byte signed or unsigned.
     * @since 15.06.24
     */
    BYTE(1),
    /**
     * 2 bytes signed or unsigned.
     * @since 15.06.24
     */
    SHORT(1),
    /**
     * 4 bytes signed or unsigned.
     * @since 15.06.24
     */
    INT(1),
    /**
     * Float or double.
     * @since 15.06.24
     */
    FLOAT(1),
    /**
     * 2-element vector.
     * @since 15.06.24
     */
    VEC2(2),
    /**
     * 3-element vector.
     * @since 15.06.24
     */
    VEC3(3),
    /**
     * 4-element vector.
     * @since 15.06.24
     */
    VEC4(4);
    
    final int value;

    GLVertexAttributeSize(final int value) {
        this.value = value;
    }
}
