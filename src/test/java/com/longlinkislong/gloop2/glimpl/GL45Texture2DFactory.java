/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.longlinkislong.gloop2.glimpl;

import com.longlinkislong.gloop2.AbstractTexture2DFactory;
import com.longlinkislong.gloop2.Sampler2DCreateInfo;
import static com.longlinkislong.gloop2.glimpl.GLTranslator.toGLenum;
import static org.lwjgl.opengl.ARBBindlessTexture.*;
import static org.lwjgl.opengl.EXTTextureFilterAnisotropic.*;
import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.opengl.GL12.*;
import static org.lwjgl.opengl.GL14.GL_TEXTURE_LOD_BIAS;
import org.lwjgl.opengl.GL45;
import static org.lwjgl.opengl.GL45.*;

/**
 *
 * @author zmichaels
 */
public final class GL45Texture2DFactory extends AbstractTexture2DFactory<GL45Texture2D> {

    @Override
    protected GL45Texture2D newTexture2D() {
        return new GL45Texture2D();
    }

    @Override
    protected void doAllocate(GL45Texture2D texture) {
        final int fmt = toGLenum(texture.getFormat());

        texture.id = glCreateTextures(GL_TEXTURE_2D);

        glTextureParameteri(texture.id, GL_TEXTURE_BASE_LEVEL, texture.getBaseMipmapLevel());
        glTextureParameteri(texture.id, GL_TEXTURE_MAX_LEVEL, texture.getMaxMipmapLevel());

        final Sampler2DCreateInfo sampler = texture.getSampler();

        glTextureParameteri(texture.id, GL_TEXTURE_WRAP_S, toGLenum(sampler.edgeSamplingS));
        glTextureParameteri(texture.id, GL_TEXTURE_WRAP_T, toGLenum(sampler.edgeSamplingT));

        glTextureParameterf(texture.id, GL_TEXTURE_MAX_ANISOTROPY_EXT, (float) sampler.anisotropicFilter);

        glTextureParameterf(texture.id, GL_TEXTURE_MIN_LOD, (float) sampler.minLOD);
        glTextureParameterf(texture.id, GL_TEXTURE_MAX_LOD, (float) sampler.maxLOD);
        glTextureParameterf(texture.id, GL_TEXTURE_LOD_BIAS, (float) sampler.lodBias);

        glTextureParameterfv(texture.id, GL_TEXTURE_BORDER_COLOR, new float[]{(float) sampler.borderR, (float) sampler.borderG, (float) sampler.borderB, (float) sampler.borderA});
        
        glTextureParameteri(texture.id, GL_TEXTURE_MIN_FILTER, toGLenum(sampler.minFilter));
        glTextureParameteri(texture.id, GL_TEXTURE_MAG_FILTER, toGLenum(sampler.magFilter));

        GL45.glTextureStorage2D(texture.id, texture.getMipmapLevelCount(), fmt, texture.getWidth(), texture.getHeight());
        texture.mipmaps = new GL45Image2D[texture.getMipmapLevelCount()];
    }

    @Override
    public boolean isValid(GL45Texture2D texture) {
        return texture.id != 0;
    }

    @Override
    protected void doFree(GL45Texture2D texture) {
        glDeleteTextures(texture.id);
    }

    @Override
    protected void doBind(GL45Texture2D texture, int unit) {
        GL45.glBindTextureUnit(unit, texture.id);
    }

    @Override
    public boolean isHandleResident(GL45Texture2D texture) {
        return texture.handle != 0L;
    }

    @Override
    protected void doMakeHandleResident(GL45Texture2D tex) {
        tex.handle = glGetTextureHandleARB(tex.id);
        glMakeTextureHandleResidentARB(tex.handle);
    }

    @Override
    protected void doMakeHandleNonResident(GL45Texture2D tex) {
        glMakeTextureHandleNonResidentARB(tex.handle);
        tex.handle = 0L;
    }

    @Override
    protected void doGenerateMipmaps(GL45Texture2D tex) {
        glGenerateTextureMipmap(tex.id);
    }

}
