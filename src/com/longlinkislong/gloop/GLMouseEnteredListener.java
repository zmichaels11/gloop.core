/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.longlinkislong.gloop;

/**
 *
 * @author zmichaels
 */
public interface GLMouseEnteredListener {
    void mouseEnteredActionPerformed(final GLWindow window, final GLMouseEnteredStatus status);
    
    default void glfwCallback(long hwnd, int status) {
        final GLWindow window = GLWindow.WINDOWS.get(hwnd);
        
        this.mouseEnteredActionPerformed(window, GLMouseEnteredStatus.valueOf(status));
    }
}
