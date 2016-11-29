/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.longlinkislong.gloop2.glimpl;

import com.longlinkislong.gloop2.AbstractImage2DFactory;
import com.longlinkislong.gloop2.DataFormat;
import com.longlinkislong.gloop2.ImageAccess;
import static com.longlinkislong.gloop2.glimpl.GLTranslator.toGLenum;
import java.nio.ByteBuffer;
import org.lwjgl.opengl.ARBBindlessTexture;
import org.lwjgl.opengl.GL42;
import org.lwjgl.opengl.GL45;

/**
 *
 * @author zmichaels
 */
public class GL45Image2DFactory extends AbstractImage2DFactory<GL45Image2D> {

    private static final RuntimeException EX_UNSUPPORTED = new UnsupportedOperationException("Standalone Image2D objects are not supported by this spec!");

    @Override
    protected GL45Image2D newImage2D() {
        throw EX_UNSUPPORTED;
    }

    @Override
    protected void doAllocate(GL45Image2D image) {
        throw EX_UNSUPPORTED;
    }

    @Override
    public boolean isValid(GL45Image2D image) {
        return true;
    }

    @Override
    protected void doFree(GL45Image2D image) {
        throw EX_UNSUPPORTED;
    }

    @Override
    protected void doBind(GL45Image2D image, int unit, ImageAccess access) {
        final int glAccess = toGLenum(access);
        final int glImageFmt = toGLenum(image.getFormat());
        
        GL42.glBindImageTexture(unit, image.parentId, image.level, false, 0, glAccess, glImageFmt);
    }

    @Override
    protected void doWrite(GL45Image2D image, int xOffset, int yOffset, int width, int height, DataFormat fmt, ByteBuffer data) {
        final int glImageFmt = toGLenum(image.getFormat());
        final int glDataFmt = toGLenum(fmt);
        
        GL45.glTextureSubImage2D(image.parentId, image.level, xOffset, yOffset, width, height, glImageFmt, glDataFmt, data);
    }

    @Override
    protected void doRead(GL45Image2D image, int xOffset, int yOffset, int width, int height, DataFormat fmt, ByteBuffer data) {
        final int glImageFmt = toGLenum(image.getFormat());
        final int glDataFmt = toGLenum(fmt);
        
        GL45.glGetTextureSubImage(image.parentId, image.level, xOffset, yOffset, 0, width, height, 1, glImageFmt, glDataFmt, data);
    }

    @Override
    public boolean isHandleResident(GL45Image2D image) {
        return image.handle != 0L;
    }

    @Override
    protected void doMakeHandleResident(GL45Image2D image, ImageAccess access) {
        final int glImageFmt = toGLenum(image.getFormat());
        final int glAccess = toGLenum(access);
        
        image.handle = ARBBindlessTexture.glGetImageHandleARB(image.parentId, image.level, false, 0, glImageFmt);
        ARBBindlessTexture.glMakeImageHandleResidentARB(image.handle, glAccess);
    }

    @Override
    protected void doMakeHandleNonResident(GL45Image2D image) {
        ARBBindlessTexture.glMakeImageHandleNonResidentARB(image.handle);
        image.handle = 0L;
    }

}
