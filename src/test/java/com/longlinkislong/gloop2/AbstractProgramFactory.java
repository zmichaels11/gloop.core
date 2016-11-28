/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.longlinkislong.gloop2;

import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 * @author zmichaels
 * @param <ProgramT>
 * @param <ShaderT>
 */
public abstract class AbstractProgramFactory<ProgramT extends AbstractProgram, ShaderT extends AbstractShader> {
    private static final Logger LOGGER = LoggerFactory.getLogger(AbstractProgramFactory.class);
    protected abstract ProgramT newProgram();
        
    public ProgramT allocate(ProgramCreateInfo<ShaderT> info) {
        final ProgramT out = newProgram();
        
        doLinkProgram(out, info.getShaders());        
        
        return out;
    }
    
    protected abstract void doLinkProgram(ProgramT program, List<ShaderT> shaders);
    
    public abstract boolean isValid(ProgramT program);
    
    protected abstract int getUniformLocation(ProgramT prg, String name);
    
    protected abstract void doSetUniformVec(ProgramT prg, int loc, int size, float[] data);
    
    public void setUniformVec(ProgramT prg, String name, int size, float... data) {
        final int loc = prg.uniforms.computeIfAbsent(name, n -> getUniformLocation(prg, n));
        
        doSetUniformVec(prg, loc, size, data);
    }
    
    protected abstract void doSetUniformMat(ProgramT prg, int loc, int size, float[] data);
    
    public void setUniformMat(ProgramT prg, String name, int size, float... data) {
        final int loc = prg.uniforms.computeIfAbsent(name, n -> getUniformLocation(prg, n));
        
        doSetUniformMat(prg, loc, size, data);
    }
    
    public abstract void doSetUniformVec(ProgramT prg, int loc,int size, int[] data);
    
    public void setUniformVec(ProgramT prg, String name, int count, int size, int... data) {
        final int loc = prg.uniforms.computeIfAbsent(name, n -> getUniformLocation(prg, n));
        
        doSetUniformVec(prg, loc, size, data);
    }
    
    protected abstract void bind(ProgramT prg);
    
    protected abstract void doFree(ProgramT prg);
    
    public void free(ProgramT prg) {
        if (isValid(prg)) {
            doFree(prg);
            prg.clear();
        } else {
            LOGGER.warn("Attempted to free unallocated program!");
        }
    }
}
