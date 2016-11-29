/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.longlinkislong.gloop2.vkimpl;

import com.longlinkislong.gloop2.AbstractBuffer;

/**
 *
 * @author zmichaels
 */
public class VK10Buffer extends AbstractBuffer {
    public long id;
    public long memId;
    
    void setMapInfo(long ptr, long offset, long size) {
        this.mapPtr = ptr;
        this.mapOffset = offset;
        this.mapSize = size;
    }
}
