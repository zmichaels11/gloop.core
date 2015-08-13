/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.longlinkislong.gloop;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Set;
import org.lwjgl.glfw.GLFW;

/**
 *
 * @author zmichaels
 */
public class GLKeyboard implements GLKeyListener {

    private final List<GLKeyListener> listeners = new ArrayList<>();
    private final GLWindow window;

    protected GLKeyboard(final GLWindow window) {        
        this.window = window;
    }

    public void addKeyListener(final GLKeyListener listener) {
        this.listeners.add(listener);
    }

    public boolean removeKeyListener(final GLKeyListener listener) {
        return this.listeners.remove(listener);
    }

    public void addAllKeyListeners(final Collection<? extends GLKeyListener> listeners) {
        this.listeners.addAll(listeners);
    }

    public void removeAllKeyListeners() {
        this.listeners.clear();       
    }

    public List<GLKeyListener> getKeyListeners() {
        return Collections.unmodifiableList(this.listeners);
    }   

    public GLKeyAction getKey(final int keyId) {
        if(!this.window.isValid()) {
            throw new GLException("Invalid GLWindow!");
        }
        return GLKeyAction.valueOf(GLFW.glfwGetKey(this.window.window, keyId));
    }    

    @Override
    public void keyActionPerformed(GLWindow window, int key, int scancode, GLKeyAction action, Set<GLKeyModifier> mods) {
        for (GLKeyListener listener : listeners) {
            listener.keyActionPerformed(window, key, scancode, action, mods);
        }
    }

}
