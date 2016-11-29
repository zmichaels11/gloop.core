/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.longlinkislong.gloop2;

/**
 *
 * @author zmichaels
 */
public abstract class AbstractSampler2D {
    protected static final IllegalStateException EX_INVALID_SAMPLER = new IllegalStateException("Invalid Sampler2D!");
    protected SamplerEdgeSampling edgeSamplingS;
    protected SamplerEdgeSampling edgeSamplingT;
    protected SamplerMinFilter minFilter;
    protected SamplerMagFilter magFilter;
    protected double anisotropicFilter;
    protected double minLOD;
    protected double maxLOD;
    protected double lodBias;
    protected double borderR;
    protected double borderG;
    protected double borderB;
    protected double borderA;
    
    public double getBorderColorRed() {
        if (this.isValid()) {
            return this.borderR;
        } else {
            throw EX_INVALID_SAMPLER;
        }
    }
    
    public double getBorderColorGreen() {
        if (this.isValid()) {
            return this.borderG;
        } else {
            throw EX_INVALID_SAMPLER;
        }
    }
    
    public double getBorderColorBlue() {
        if (this.isValid()) {
            return this.borderB;
        } else {
            throw EX_INVALID_SAMPLER;
        }
    }
    
    public double getBorderColorAlpha() {
        if (this.isValid()) {
            return this.borderA;
        } else {
            throw EX_INVALID_SAMPLER;
        }
    }
    
    public SamplerEdgeSampling getEdgeSamplingS() {
        if (isValid()) {
            return this.edgeSamplingS;
        } else {
            throw EX_INVALID_SAMPLER;
        }
    }
    
    public SamplerEdgeSampling getEdgeSamplingT() {
        if (isValid()) {
            return this.edgeSamplingT;
        } else {
            throw EX_INVALID_SAMPLER;
        }
    }
    
    public SamplerMinFilter getMinFilter() {
        if (isValid()) {
            return this.minFilter;
        } else {
            throw EX_INVALID_SAMPLER;
        }
    }
    
    public SamplerMagFilter getMagFilter() {
        if (isValid()) {
            return this.magFilter;
        } else {
            throw EX_INVALID_SAMPLER;
        }
    }
    
    public double getAnisotropicFilter() {
        if (isValid()) {
            return this.anisotropicFilter;
        } else {
            throw EX_INVALID_SAMPLER;
        }
    }
    
    public double getMinLOD() {
        if (isValid()) {
            return this.minLOD;
        } else {
            throw EX_INVALID_SAMPLER;
        }
    }
    
    public double getMaxLOD() {
        if (isValid()) {
            return this.maxLOD;
        } else {
            throw EX_INVALID_SAMPLER;
        }
    }
    
    public double getLODBias() {
        if(isValid()) {
            return this.lodBias;
        } else {
            throw EX_INVALID_SAMPLER;
        }
    }        
    
    protected void clear() {
        this.edgeSamplingS = null;
        this.edgeSamplingT = null;
        this.minFilter = null;
        this.magFilter = null;
        this.minLOD = 0;
        this.maxLOD = 0;
        this.lodBias = 0;
        this.borderR = 0;
        this.borderG = 0;
        this.borderB = 0;
        this.borderA = 0;
    }
    
    protected AbstractSampler2DFactory getFactory() {
        return GLObjectFactoryManager.getInstance().getSampler2DFactory();
    }
    
    public boolean isValid() {
        return getFactory().isValid(this);
    }
    
    public AbstractSampler2D bind(final int unit) {
        getFactory().bind(this, unit);
        return this;
    }
    
    public void free() {
        getFactory().free(this);
    }
}
