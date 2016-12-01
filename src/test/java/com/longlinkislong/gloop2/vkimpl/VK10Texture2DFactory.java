/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.longlinkislong.gloop2.vkimpl;

import com.longlinkislong.gloop2.AbstractTexture2DFactory;

/**
 *
 * @author zmichaels
 */
public class VK10Texture2DFactory extends AbstractTexture2DFactory<VK10Texture2D> {

    @Override
    protected VK10Texture2D newTexture2D() {
        return new VK10Texture2D();
    }

    @Override
    protected void doAllocate(VK10Texture2D texture) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public boolean isValid(VK10Texture2D texture) {
        return texture.view != 0L;
    }

    @Override
    protected void doFree(VK10Texture2D texture) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    protected void doBind(VK10Texture2D texture, int unit) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public boolean isHandleResident(VK10Texture2D texture) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    protected void doMakeHandleResident(VK10Texture2D tex) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    protected void doMakeHandleNonResident(VK10Texture2D tex) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    protected void doGenerateMipmaps(VK10Texture2D tex) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }
    
}
