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
public abstract class AbstractTexture2D implements Texture2D, FramebufferAttachment {    
    protected int width;
    protected int height;
    protected Sampler2DCreateInfo sampler;
    protected int baseLevel;
    protected int maxLevel;
    protected int levels;
    protected TextureFormat format;
    
    @Override
    public final TextureFormat getFormat() {
        if (this.isValid()) {
            return this.format;
        } else {
            throw EX_INVALID_TEXTURE;
        }
    }
    
    @Override
    public final int getBaseMipmapLevel() {
        if (this.isValid()) {
            return this.baseLevel;
        } else {
            throw EX_INVALID_TEXTURE;
        }
    }
    
    @Override
    public final int getMaxMipmapLevel() {
        if (this.isValid()) {
            return this.maxLevel;
        } else {
            throw EX_INVALID_TEXTURE;
        }
    }
    
    @Override
    public final int getMipmapLevelCount() {
        if (this.isValid()) {
            return this.levels;
        } else {
            throw EX_INVALID_TEXTURE;
        }
    }

    @Override
    public final Sampler2DCreateInfo getSampler() {
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

    @Override
    public final int getWidth() {
        if (this.isValid()) {
            return this.width;
        } else {
            throw EX_INVALID_TEXTURE;
        }
    }

    @Override
    public final int getHeight() {
        if (this.isValid()) {
            return this.height;
        } else {
            throw EX_INVALID_TEXTURE;
        }
    }
    
    protected final AbstractTexture2DFactory getFactory() {
        return ObjectFactoryManager.getInstance().getTexture2DFactory();
    }

    @Override
    public final AbstractTexture2D bind(int unit) {
        getFactory().bind(this, unit);
        return this;
    }

    protected abstract Image2D doGetImage(final int level);

    @Override
    public final Image2D getImage(final int level) {
        if (this.isValid()) {
            return doGetImage(level);
        } else {
            throw EX_INVALID_TEXTURE;
        }
    }

    @Override
    public final AbstractTexture2D generateMipmaps() {
        getFactory().generateMipmaps(this);
        return this;
    }

    @Override
    public final boolean isValid() {
        return getFactory().isValid(this);
    }        

    @Override
    public final boolean isHandleResident() {
        return getFactory().isHandleResident(this);
    }

    @Override
    public final AbstractTexture2D setHandleResidency(final boolean residency) {
        if (residency) {
            getFactory().makeHandleResident(this);
        } else {
            getFactory().makeHandleNonResident(this);
        }
        
        return this;
    }

    @Override
    public abstract long getHandle();
    
    @Override
    public final void free() {
        getFactory().free(this);
    }
}
