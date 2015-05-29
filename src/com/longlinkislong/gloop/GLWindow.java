/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.longlinkislong.gloop;

import org.lwjgl.glfw.GLFW;
import static org.lwjgl.opengl.GL11.GL_FALSE;
import static org.lwjgl.opengl.GL11.GL_TRUE;
import org.lwjgl.opengl.GLContext;
import static org.lwjgl.system.MemoryUtil.NULL;

/**
 *
 * @author zmichaels
 */
public class GLWindow {

    private static final long INVALID_WINDOW_ID = -1L;
    private static boolean isGLFWInit = false;
    private long window = INVALID_WINDOW_ID;
    private final int width;
    private final int height;
    private final String title;
    private GLThread thread = null;
    private final GLWindow shared;

    public GLWindow() {
        this(640, 480, "GLOOP App", null);
    }

    public GLWindow(final int width, final int height) {
        this(width, height, "GLOOP App", null);
    }

    public GLWindow(
            final int width, final int height, 
            final CharSequence title) {
        
        this(width, height, title, null);
    }

    public GLWindow(
            final int width, final int height, 
            final CharSequence title, 
            final GLWindow shared) {
        
        this.width = width;
        this.height = height;
        this.title = title.toString();
        this.shared = shared;

        if (!isGLFWInit) {
            isGLFWInit = (GLFW.glfwInit() == GL_TRUE);
            
            if(!isGLFWInit) {
                throw new GLException("Could not initialize GLFW!");
            }
        }

        this.thread = GLThread.create();
        this.thread.submitGLTask(new InitTask()); 
        this.thread.pushViewport(new GLViewport(this.thread, 0, 0, this.width, this.height));
    }

    private class InitTask extends GLTask {

        @Override
        public void run() {
            GLFW.glfwDefaultWindowHints();
            GLFW.glfwWindowHint(GLFW.GLFW_VISIBLE, GL_FALSE);
            GLFW.glfwWindowHint(GLFW.GLFW_RESIZABLE, GL_TRUE);

            final long sharedContextHandle = shared != null ? shared.window : NULL;

            GLWindow.this.window = GLFW.glfwCreateWindow(
                    GLWindow.this.width, GLWindow.this.height,
                    GLWindow.this.title,
                    NULL, sharedContextHandle);

            if (GLWindow.this.window == NULL) {
                throw new GLException("Failed to create the GLFW window!");
            }

            GLFW.glfwMakeContextCurrent(GLWindow.this.window);
            GLFW.glfwSwapInterval(1);
            GLContext.createFromCurrent();                        
        }
    }

    public float getAspectRatio() {
        return (float) this.width / (float) this.height;
    }

    public void setVisible(final boolean isVisible) {
        new SetVisibleTask(isVisible).glRun(this.getThread());
    }
    
    public class SetVisibleTask extends GLTask {

        private final boolean visibility;

        public SetVisibleTask(final boolean visible) {
            this.visibility = visible;
        }

        @Override
        public void run() {
            if (this.visibility) {
                GLFW.glfwShowWindow(GLWindow.this.window);
            } else {
                GLFW.glfwHideWindow(GLWindow.this.window);
            }
        }
    }

    public GLThread getThread() {
        return this.thread;
    }

    private final UpdateTask updateTask = new UpdateTask();

    public void update() {
        this.updateTask.glRun(this.getThread());
    }

    public class UpdateTask extends GLTask {

        @Override
        public void run() {
            if (GLFW.glfwWindowShouldClose(GLWindow.this.window) == GL_TRUE) {
                GLFW.glfwDestroyWindow(GLWindow.this.window);
                GLWindow.this.thread.shutdown();
            } else {
                GLFW.glfwSwapBuffers(GLWindow.this.window);
                GLFW.glfwPollEvents();
            }
        }
    }

    public GLThread newWorkerThread() {
        final GLWindow dummy = new GLWindow(0, 0, "WORKER", this);

        return dummy.getThread();
    }
}
