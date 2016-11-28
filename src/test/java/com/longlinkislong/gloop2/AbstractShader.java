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
public abstract class AbstractShader {
    protected String src;
    protected boolean compiled;
    protected ShaderType type;
    
    protected void clear() {
        this.src = "";
        this.compiled = false;
    }
    
    public String getSource() {
        return this.src;
    }
    
    public boolean isCompiled() {
        return this.compiled;
    }
    
    public ShaderType getShaderType() {
        return this.type;
    }        
}
