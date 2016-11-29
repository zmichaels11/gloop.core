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
public interface Texture2D extends BaseObject {
    public static final IllegalStateException EX_INVALID_TEXTURE = new IllegalStateException("Invalid Texture2D!");  
    
    int getWidth();
    
    int getHeight();
    
    Sampler2DCreateInfo getSampler();
    
    int getBaseMipmapLevel();
    
    int getMaxMipmapLevel();
    
    int getMipmapLevelCount();
    
    TextureFormat getFormat();
    
    Texture2D bind(int unit);
    
    Image2D getImage(int level);
    
    Texture2D generateMipmaps();
    
    boolean isHandleResident();
    
    Texture2D setHandleResidency(boolean residency);
    
    long getHandle();
}
