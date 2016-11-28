/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.longlinkislong.gloop2.glimpl;

import com.longlinkislong.gloop2.AbstractShaderFactory;
import com.longlinkislong.gloop2.ShaderType;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL20;
import org.lwjgl.opengl.GL32;
import org.lwjgl.opengl.GL40;
import org.lwjgl.opengl.GL43;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 * @author zmichaels
 */
public final class GL20ShaderFactory extends AbstractShaderFactory<GL20Shader> {
    private static final Logger LOGGER = LoggerFactory.getLogger(GL20ShaderFactory.class);
    
    @Override
    protected GL20Shader newShader() {
        return new GL20Shader();
    }

    @Override
    protected void doAllocate(GL20Shader shader, ShaderType type, String src) {
        switch (type) {
            case VERTEX:
                shader.id = GL20.glCreateShader(GL20.GL_VERTEX_SHADER);
                break;
            case TESS_EVALUATION:
                shader.id = GL20.glCreateShader(GL40.GL_TESS_EVALUATION_SHADER);
                break;
            case TESS_CONTROL:
                shader.id = GL20.glCreateShader(GL40.GL_TESS_CONTROL_SHADER);
                break;
            case GEOMETRY:
                shader.id = GL20.glCreateShader(GL32.GL_GEOMETRY_SHADER);
                break;
            case FRAGMENT:
                shader.id = GL20.glCreateShader(GL20.GL_FRAGMENT_SHADER);
                break;
            case COMPUTE:
                shader.id = GL20.glCreateShader(GL43.GL_COMPUTE_SHADER);
                break;
            default:
                throw new UnsupportedOperationException("Invalid shader type!");            
        }
        
        GL20.glShaderSource(shader.id, src);
        GL20.glCompileShader(shader.id);
        
        final int status = GL20.glGetShaderi(shader.id, GL20.GL_COMPILE_STATUS);
        
        if (status == GL11.GL_FALSE) {
            LOGGER.error("Error compiling {} shader!", type);
            LOGGER.debug(GL20.glGetShaderInfoLog(shader.id));
            
            GL20.glDeleteShader(shader.id);
        }
    }

    @Override
    protected void doFree(GL20Shader shader) {
        GL20.glDeleteShader(shader.id);
    }

    @Override
    public boolean isValid(GL20Shader shader) {
        return shader != null && shader.id != 0;
    }       
}
