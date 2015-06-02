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
public interface GLMousePositionListener {
    void mousePositionActionPerformed(GLWindow window, double x, double y);
    
    default void glfwCallback(long hwnd, double x, double y) {
        final GLWindow window = GLWindow.WINDOWS.get(hwnd);
        
        this.mousePositionActionPerformed(window, x, y);
    }        
}
