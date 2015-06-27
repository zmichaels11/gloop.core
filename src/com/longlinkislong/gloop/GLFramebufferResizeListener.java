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
public interface GLFramebufferResizeListener {
    void framebufferResizedActionPerformed(GLWindow window, int newWidth, int newHeight);
    default void glfwFramebufferResizeCallback(final long hwnd, final int width, final int height) {
        final GLWindow window = GLWindow.WINDOWS.get(hwnd);
        
        this.framebufferResizedActionPerformed(window, width, height);
    }
}
