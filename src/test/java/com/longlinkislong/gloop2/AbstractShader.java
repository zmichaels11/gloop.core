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
public abstract class AbstractShader implements Shader {
    protected String src;
    protected boolean compiled;
    protected ShaderType type;
    
    protected void clear() {
        this.src = "";
        this.compiled = false;
    }
    
    @Override
    public final String getSource() {
        return this.src;
    }
    
    @Override
    public final boolean isCompiled() {
        return this.compiled;
    }
    
    @Override
    public final ShaderType getShaderType() {
        return this.type;
    }     
    
    @Override
    public final void free() {
        //TODO: implement
    }
    
    @Override
    public final boolean isValid() {        
        //TODO: implement
        return false;
    }
}
