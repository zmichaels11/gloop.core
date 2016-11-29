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
public abstract class AbstractTexture2D {

    protected static final IllegalStateException EX_INVALID_TEXTURE = new IllegalStateException("Invalid Texture2D!");    
    
    protected int width;
    protected int height;
    protected Sampler2DCreateInfo sampler;
    protected int baseLevel;
    protected int maxLevel;
    protected int levels;
    protected TextureFormat format;
    
    public TextureFormat getFormat() {
        if (this.isValid()) {
            return this.format;
        } else {
            throw EX_INVALID_TEXTURE;
        }
    }
    
    public int getBaseMipmapLevel() {
        if (this.isValid()) {
            return this.baseLevel;
        } else {
            throw EX_INVALID_TEXTURE;
        }
    }
    
    public int getMaxMipmapLevel() {
        if (this.isValid()) {
            return this.maxLevel;
        } else {
            throw EX_INVALID_TEXTURE;
        }
    }
    
    public int getMipmapLevelCount() {
        if (this.isValid()) {
            return this.levels;
        } else {
            throw EX_INVALID_TEXTURE;
        }
    }

    public Sampler2DCreateInfo getSampler() {
        if (this.isValid()) {
            return this.sampler;
        } else {
            throw EX_INVALID_TEXTURE;
        }
    }

    protected void clear() {
        this.width = 0;
        this.height = 0;
        this.sampler = null;
        this.baseLevel = 0;
        this.maxLevel = 0;
        this.levels = 0;
        this.format = null;        
    }

    public int getWidth() {
        if (this.isValid()) {
            return this.width;
        } else {
            throw EX_INVALID_TEXTURE;
        }
    }

    public int getHeight() {
        if (this.isValid()) {
            return this.height;
        } else {
            throw EX_INVALID_TEXTURE;
        }
    }
    
    protected final AbstractTexture2DFactory getFactory() {
        return GLObjectFactoryManager.getInstance().getTexture2DFactory();
    }

    public AbstractTexture2D bind(int unit) {
        getFactory().bind(this, unit);
        return this;
    }

    protected abstract AbstractImage2D doGetImage(final int level);

    public AbstractImage2D getImage(final int level) {
        if (this.isValid()) {
            return doGetImage(level);
        } else {
            throw EX_INVALID_TEXTURE;
        }
    }

    public AbstractTexture2D generateMipmaps() {
        getFactory().generateMipmaps(this);
        return this;
    }

    public boolean isValid() {
        return getFactory().isValid(this);
    }        

    public boolean isHandleResident() {
        return getFactory().isHandleResident(this);
    }

    public AbstractTexture2D setHandleResidency(final boolean residency) {
        if (residency) {
            getFactory().makeHandleResident(this);
        } else {
            getFactory().makeHandleNonResident(this);
        }
        
        return this;
    }

    public abstract long getHandle();
}
