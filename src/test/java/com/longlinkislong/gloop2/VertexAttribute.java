/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.longlinkislong.gloop2;

import java.util.Objects;

/**
 *
 * @author zmichaels
 */
public final class VertexAttribute {
    
    public final int location;
    public final VertexAttributeFormat format;
    public final int offset;
    public final int stride;
    public final long divisor;

    public VertexAttribute(int location, VertexAttributeFormat format, int stride, int offset, long divisor) {        
        this.location = location;
        this.format = format;
        this.stride = stride;
        this.offset = offset;
        this.divisor = divisor;
    }

    public VertexAttribute() {
        this(0, null, 0, 0, 0);
    }


    public VertexAttribute withLocation(final int location) {
        return new VertexAttribute(location, this.format, this.stride, this.offset, this.divisor);
    }

    public VertexAttribute withFormat(final VertexAttributeFormat format) {
        return new VertexAttribute(this.location, Objects.requireNonNull(format), this.stride, this.offset, this.divisor);
    }

    public VertexAttribute withStride(final int stride) {
        return new VertexAttribute(this.location, this.format, stride, this.offset, this.divisor);
    }

    public VertexAttribute withOffset(final int offset) {
        return new VertexAttribute(this.location, this.format, this.stride, offset, this.divisor);
    }

    public VertexAttribute withDivisor(final long divisor) {
        return new VertexAttribute(this.location, this.format, this.stride, this.offset, divisor);
    }
}
