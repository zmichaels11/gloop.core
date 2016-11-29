/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.longlinkislong.gloop2.glimpl;

import com.longlinkislong.gloop2.AbstractSampler2DFactory;
import static com.longlinkislong.gloop2.glimpl.GLTranslator.toGLenum;
import static org.lwjgl.opengl.EXTTextureFilterAnisotropic.GL_TEXTURE_MAX_ANISOTROPY_EXT;
import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.opengl.GL12.*;
import static org.lwjgl.opengl.GL14.*;
import static org.lwjgl.opengl.GL33.*;

/**
 *
 * @author zmichaels
 */
public final class GL33Sampler2DFactory extends AbstractSampler2DFactory<GL33Sampler2D> {

    @Override
    protected GL33Sampler2D newSampler2D() {
        return new GL33Sampler2D();
    }

    @Override
    protected void doAllocate(GL33Sampler2D sampler) {
        sampler.id = glGenSamplers();

        glSamplerParameteri(sampler.id, GL_TEXTURE_WRAP_S, toGLenum(sampler.getEdgeSamplingS()));
        glSamplerParameteri(sampler.id, GL_TEXTURE_WRAP_T, toGLenum(sampler.getEdgeSamplingT()));

        glSamplerParameterf(sampler.id, GL_TEXTURE_MAX_ANISOTROPY_EXT, (float) sampler.getAnisotropicFilter());

        glSamplerParameterf(sampler.id, GL_TEXTURE_MIN_LOD, (float) sampler.getMinLOD());
        glSamplerParameterf(sampler.id, GL_TEXTURE_MAX_LOD, (float) sampler.getMaxLOD());
        glSamplerParameterf(sampler.id, GL_TEXTURE_LOD_BIAS, (float) sampler.getLODBias());

        glSamplerParameterfv(sampler.id, GL_TEXTURE_BORDER_COLOR, new float[]{
            (float) sampler.getBorderColorRed(),
            (float) sampler.getBorderColorGreen(),
            (float) sampler.getBorderColorBlue(),
            (float) sampler.getBorderColorAlpha()});
        
        glSamplerParameteri(sampler.id, GL_TEXTURE_MIN_FILTER, toGLenum(sampler.getMinFilter()));
        glSamplerParameteri(sampler.id, GL_TEXTURE_MAG_FILTER, toGLenum(sampler.getMagFilter()));
    }

    @Override
    public boolean isValid(GL33Sampler2D sampler) {
        return sampler.id != 0;
    }

    @Override
    protected void doFree(GL33Sampler2D sampler) {
        glDeleteSamplers(sampler.id);
    }

    @Override
    protected void doBind(GL33Sampler2D sampler, int unit) {
        glBindSampler(unit, sampler.id);
    }
}
