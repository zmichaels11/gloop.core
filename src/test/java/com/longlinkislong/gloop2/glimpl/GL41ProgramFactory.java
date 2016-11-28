/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.longlinkislong.gloop2.glimpl;

import com.longlinkislong.gloop2.AbstractProgramFactory;
import java.util.List;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL20;
import org.lwjgl.opengl.GL41;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 * @author zmichaels
 */
public final class GL41ProgramFactory extends AbstractProgramFactory<GL41Program, GL20Shader> {
    private static final Logger LOGGER = LoggerFactory.getLogger(AbstractProgramFactory.class);
    
    @Override
    protected GL41Program newProgram() {
        final GL41Program out = new GL41Program();
        
        out.id = GL20.glCreateProgram();
        
        return out;
    }   

    @Override
    public boolean isValid(GL41Program program) {
        return program != null && program.id != 0;
    }

    @Override
    protected int getUniformLocation(GL41Program prg, String name) {
        return GL20.glGetUniformLocation(prg.id, name);
    }

    @Override
    protected void bind(GL41Program prg) {
        GL20.glUseProgram(prg.id);
    }

    @Override
    protected void doLinkProgram(GL41Program program, List<GL20Shader> shaders) {
        shaders.stream()
                .mapToInt(s -> s.id)
                .forEach(shaderId -> GL20.glAttachShader(program.id, shaderId));
                
        GL20.glLinkProgram(program.id);
        
        final int status = GL20.glGetProgrami(program.id, GL20.GL_LINK_STATUS);
        
        if (status == GL11.GL_FALSE) {
            LOGGER.error("Unable to link program!");
            final String log = GL20.glGetProgramInfoLog(program.id);
            
            LOGGER.debug(log);
            free(program);
        }
    }

    @Override
    protected void doFree(GL41Program prg) {
        GL20.glDeleteProgram(prg.id);
    }

    @Override
    protected void doSetUniformVec(GL41Program prg, int loc, int size, float[] data) {
        switch(size) {
            case 1:
                GL41.glProgramUniform1fv(prg.id, loc, data);
                break;
            case 2:
                GL41.glProgramUniform2fv(prg.id, loc, data);
                break;
            case 3:
                GL41.glProgramUniform3fv(prg.id, loc, data);
                break;
            case 4:
                GL41.glProgramUniform4fv(prg.id, loc, data);
                break;
            default:
                throw new UnsupportedOperationException("Unsupported vector uniform size: " + size);
        }
    }

    @Override
    protected void doSetUniformMat(GL41Program prg, int loc, int size, float[] data) {
        switch (size) {
            case 2:
                GL41.glProgramUniformMatrix2fv(prg.id, loc, false, data);
                break;
            case 3:
                GL41.glProgramUniformMatrix3fv(prg.id, loc, false, data);
                break;
            case 4:
                GL41.glProgramUniformMatrix4fv(prg.id, loc, false, data);
                break;
            default:
                throw new UnsupportedOperationException("Unsupported matrix uniform size: " + size + "x" + size);
        }
    }

    @Override
    public void doSetUniformVec(GL41Program prg, int loc, int size, int[] data) {
        switch (size) {
            case 1:
                GL41.glProgramUniform1iv(prg.id, loc, data);
                break;
            case 2:
                GL41.glProgramUniform2iv(prg.id, loc, data);
                break;
            case 3:
                GL41.glProgramUniform3iv(prg.id, loc, data);
                break;
            case 4:
                GL41.glProgramUniform4iv(prg.id, loc, data);
                break;
            default:
                throw new UnsupportedOperationException("Unsupported vector uniform size: " + size);
        }
    }
}
