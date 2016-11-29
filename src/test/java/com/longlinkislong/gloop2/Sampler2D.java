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
public interface Sampler2D extends BaseObject {
    double getBorderColorRed();
    
    double getBorderColorGreen();
    
    double getBorderColorBlue();
    
    double getBorderColorAlpha();
    
    SamplerEdgeSampling getEdgeSamplingS();
    
    SamplerEdgeSampling getEdgeSamplingT();
    
    SamplerMinFilter getMinFilter();
    
    SamplerMagFilter getMagFilter();
    
    double getAnisotropicFilter();
    
    double getMinLOD();
    
    double getMaxLOD();
    
    double getLODBias();
    
    Sampler2D bind(int unit);        
}
