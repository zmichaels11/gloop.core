/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.longlinkislong.gloop2.glimpl;

import com.longlinkislong.gloop2.AbstractBuffer;
import com.longlinkislong.gloop2.BufferAccessHint;
import com.longlinkislong.gloop2.BufferMapHint;
import com.longlinkislong.gloop2.BufferStorageHint;

/**
 *
 * @author zmichaels
 */
public class GL45Buffer extends AbstractBuffer {    
    int id;  
    
    void setSize(long size) {
        this.size = size;
    }
    
    void setAccessHint(final BufferAccessHint accessHint) {
        this.accessHint = accessHint;
    }
    
    void setMapHint(final BufferMapHint mapHint) {
        this.mapHint = mapHint;
    }
    
    void setStorageHint(final BufferStorageHint storageHint) {
        this.storageHint = storageHint;
    }        
    
    @Override
    protected void clear() {
        super.clear();
        
        this.id = 0;
    }
}
