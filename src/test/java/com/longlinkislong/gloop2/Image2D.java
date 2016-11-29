/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.longlinkislong.gloop2;

import java.nio.ByteBuffer;

/**
 *
 * @author zmichaels
 */
public interface Image2D extends BaseObject {

    ImageFormat getFormat();

    boolean isLayered();

    int getLayer();

    int getWidth();

    int getHeight();

    Image2D write(int xOffset, int yOffset, int width, int height, DataFormat format, ByteBuffer data);
    
    default Image2D write(DataFormat format, ByteBuffer data) {
        return this.write(0, 0, this.getWidth(), this.getHeight(), format, data);
    }
    
    default Image2D write(int width, int height, DataFormat format, ByteBuffer data) {
        return this.write(0, 0, width, height, format, data);
    }
    
    Image2D read(int xOffset, int yOffset, int width, int height, DataFormat format, ByteBuffer data);
    
    default Image2D read(int width, int height, DataFormat format, ByteBuffer data) {
        return this.read(0, 0, width, height, format, data);
    }
    
    default Image2D read(DataFormat format, ByteBuffer data) {
        return this.read(0, 0, this.getWidth(), this.getHeight(), format, data);
    }
    
    Image2D bind(int unit, ImageAccess access);
    
    boolean isHandleResident();
    
    Image2D setHandleResidency(boolean residency, ImageAccess access);
}
