/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.longlinkislong.gloop;

import java.nio.ByteBuffer;
import java.nio.FloatBuffer;
import org.lwjgl.glfw.GLFW;

/**
 *
 * @author zmichaels
 */
public class GLGamepad extends GLObject {

    private final String name;
    private final int id;
    private float[] axes;
    private final GLGamepadState buttons[];
    private final int buttonCount;
    private final int axesCount;

    protected GLGamepad(final int id) {
        this.name = GLFW.glfwGetJoystickName(this.id = id);
        final FloatBuffer axesData = GLFW.glfwGetJoystickAxes(this.id);
        final ByteBuffer buttonData = GLFW.glfwGetJoystickButtons(this.id);        

        axesData.get(this.axes = new float[axesData.limit()]);
        this.buttonCount = buttonData.limit();        
        this.axesCount = this.axes.length;
        
        this.buttons = new GLGamepadState[this.buttonCount];
        
        for(int i = 0; i < this.buttonCount; i++) {
            this.buttons[i] = GLGamepadState.valueOf(buttonData.get(i));
        }
    }
    
    public int getButtonCount() {
        return this.buttonCount;
    }
    
    public int getAxesCount() {
        return this.axesCount;
    }
    
    public GLGamepadState getButtonState(final int buttonId) {
        return this.buttons[buttonId];
    }
    
    public float getAxesState(final int axes) {
        return this.axes[axes];
    }

    public final void update() {
        final FloatBuffer axesData = GLFW.glfwGetJoystickAxes(this.id);
        final ByteBuffer buttonData = GLFW.glfwGetJoystickButtons(this.id);        
                
        axesData.get(this.axes);
        for(int i = 0; i < this.buttonCount; i++) {
            this.buttons[i] = GLGamepadState.valueOf(buttonData.get(i));
        }        
    }
}
