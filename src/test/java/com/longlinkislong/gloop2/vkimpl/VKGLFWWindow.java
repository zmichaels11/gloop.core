/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.longlinkislong.gloop2.vkimpl;

import java.nio.LongBuffer;
import static org.lwjgl.demo.vulkan.VKUtil.translateVulkanResult;
import org.lwjgl.glfw.GLFW;
import org.lwjgl.glfw.GLFWVulkan;
import org.lwjgl.system.MemoryStack;
import static org.lwjgl.system.MemoryUtil.NULL;
import org.lwjgl.vulkan.VK10;
import org.lwjgl.vulkan.VkInstance;

/**
 *
 * @author zmichaels
 */
//TODO: there should be a base class!
public class VKGLFWWindow {

    static {
        GLFW.glfwInit();
        Runtime.getRuntime().addShutdownHook(new Thread(GLFW::glfwTerminate));
    }

    public final String title;
    private int width;
    private int height;
    public long window;
    public final KHRSurface surface;

    public VKGLFWWindow(final String title, final int width, final int height) {
        this.title = title;
        this.width = width;
        this.height = height;

        GLFW.glfwDefaultWindowHints();
        GLFW.glfwWindowHint(GLFW.GLFW_CLIENT_API, GLFW.GLFW_NO_API);
        GLFW.glfwWindowHint(GLFW.GLFW_VISIBLE, GLFW.GLFW_FALSE);

        this.window = GLFW.glfwCreateWindow(width, height, title, NULL, NULL);

        //TODO: handle callbacks                
        try (MemoryStack stack = MemoryStack.stackPush()) {
            final VkInstance instance = VKGlobalConstants.getInstance().instance;
            final LongBuffer pSurface = stack.callocLong(1);
            final int err = GLFWVulkan.glfwCreateWindowSurface(instance, window, null, pSurface);

            if (err != VK10.VK_SUCCESS) {
                throw new AssertionError("Failed to create surface: " + translateVulkanResult(err));
            }

            this.surface = new KHRSurface(pSurface.get(0));
        }
    }

    public int getWidth() {
        return this.width;
    }

    public int getHeight() {
        return this.height;
    }

    private boolean isVisible = false;

    public boolean isVisible() {
        return this.isVisible;
    }

    public void setVisible(final boolean visibility) {
        if (this.isVisible == visibility) {
            // do nothing if the window state is the same.
            return;
        } else {
            this.isVisible = visibility;
        }

        if (visibility) {
            GLFW.glfwShowWindow(this.window);
        } else {
            GLFW.glfwHideWindow(this.window);
        }
    }
    
    public boolean isAlive() {
        return this.window != 0L;
    }
    
    public void free() {
        GLFW.glfwDestroyWindow(this.window);
        this.window = 0L;
    }        
}
