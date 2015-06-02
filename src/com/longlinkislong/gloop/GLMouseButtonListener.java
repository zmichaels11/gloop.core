/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.longlinkislong.gloop;

import java.util.Set;

/**
 *
 * @author zmichaels
 */
public interface GLMouseButtonListener {
    void mouseButtonActionPerformed(
            GLWindow window, 
            int button, 
            GLMouseButtonAction action, 
            Set<GLKeyModifier> modifiers);
    
    default void glfwCallback(long hwnd, int button, int action, int mods) {
        final GLWindow window = GLWindow.WINDOWS.get(hwnd);
        
        this.mouseButtonActionPerformed(
                window, 
                button, 
                GLMouseButtonAction.valueOf(action), 
                GLKeyModifier.parseModifiers(mods));
    }
}
