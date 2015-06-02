/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.longlinkislong.gloop;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import org.lwjgl.glfw.GLFW;

/**
 *
 * @author zmichaels
 */
public class GLMouse implements GLMouseEnteredListener, GLMousePositionListener, GLMouseButtonListener {

    private final GLWindow window;
    private final List<GLMouseEnteredListener> mouseEnteredListeners = new ArrayList<>();
    private final List<GLMousePositionListener> mousePositionListeners = new ArrayList<>();
    private final List<GLMouseButtonListener> mouseButtonListeners = new ArrayList<>();

    protected GLMouse(final GLWindow window) {
        this.window = window;
    }

    public void setMousePosition(final double x, final double y) {
        if(!this.window.isValid()) {
            throw new GLException("Invalid window!");
        }
        
        GLFW.glfwSetCursorPos(this.window.window, x, y);
    }

    @Override
    public void mouseButtonActionPerformed(GLWindow window, int button, GLMouseButtonAction action, Set<GLKeyModifier> modifiers) {
        for (GLMouseButtonListener listener : this.mouseButtonListeners) {
            listener.mouseButtonActionPerformed(window, button, action, modifiers);
        }
    }

    private final ByteBuffer tmpXPos = ByteBuffer
            .allocateDirect(Double.BYTES)
            .order(ByteOrder.nativeOrder());
    private final ByteBuffer tmpYPos = ByteBuffer
            .allocateDirect(Double.BYTES)
            .order(ByteOrder.nativeOrder());

    public GLVec2D getMousePosition() {
        this.tmpXPos.clear();
        this.tmpYPos.clear();

        if (!this.window.isValid()) {
            throw new GLException("Invalid GLWindow!");
        }
        
        GLFW.glfwGetCursorPos(this.window.window, this.tmpXPos, this.tmpYPos);

        return GLVec2D.create(
                this.tmpXPos.getDouble(0),
                this.tmpYPos.getDouble(0));

    }

    public GLMouseButtonAction getMouseButton(final int button) {
        if(!this.window.isValid()) {
            throw new GLException("Invalid GLWindow!");
        }
        
        return GLMouseButtonAction.valueOf(
                GLFW.glfwGetMouseButton(this.window.window, button));
    }

    public boolean addListener(final GLMouseEnteredListener listener) {
        return this.mouseEnteredListeners.add(listener);
    }

    public boolean removeListener(final GLMouseEnteredListener listener) {
        return this.mouseEnteredListeners.remove(listener);
    }

    public void clearListeners() {
        this.mouseEnteredListeners.clear();
        this.mouseButtonListeners.clear();
        this.mousePositionListeners.clear();
    }

    public boolean addListener(final GLMousePositionListener listener) {
        return this.mousePositionListeners.add(listener);
    }

    public boolean removeListener(final GLMousePositionListener listener) {
        return this.mousePositionListeners.remove(listener);
    }

    public boolean addListener(final GLMouseButtonListener listener) {
        return this.mouseButtonListeners.add(listener);
    }

    public boolean removeListener(final GLMouseButtonListener listener) {
        return this.mouseButtonListeners.remove(listener);
    }

    @Override
    public void mouseEnteredActionPerformed(
            final GLWindow window, final GLMouseEnteredStatus status) {

        for (GLMouseEnteredListener listener : this.mouseEnteredListeners) {
            listener.mouseEnteredActionPerformed(window, status);
        }
    }

    @Override
    public void mousePositionActionPerformed(
            final GLWindow window, final double x, final double y) {

        for (GLMousePositionListener listener : this.mousePositionListeners) {
            listener.mousePositionActionPerformed(window, x, y);
        }
    }

}
