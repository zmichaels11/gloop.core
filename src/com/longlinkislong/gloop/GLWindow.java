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
import java.util.Map;
import java.util.Optional;
import java.util.TreeMap;
import org.lwjgl.glfw.GLFW;
import org.lwjgl.glfw.GLFWCursorEnterCallback;
import org.lwjgl.glfw.GLFWCursorPosCallback;
import org.lwjgl.glfw.GLFWKeyCallback;
import org.lwjgl.glfw.GLFWMouseButtonCallback;
import org.lwjgl.opengl.GL11;
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
    protected long window = INVALID_WINDOW_ID;
    private final int width;
    private final int height;
    private final String title;
    private GLThread thread = null;
    private final GLWindow shared;
    private Optional<GLFWKeyCallback> keyCallback = Optional.empty();
    private Optional<GLFWMouseButtonCallback> mouseButtonCallback = Optional.empty();
    private Optional<GLFWCursorPosCallback> cursorPosCallback = Optional.empty();
    private Optional<GLFWCursorEnterCallback> cursorEntorCallback = Optional.empty();
    private float windowScale;
    private boolean useWindowScale;

    protected static final Map<Long, GLWindow> WINDOWS = new TreeMap<>(Long::compareTo);

    public GLWindow() {
        this(640, 480, "GLOOP App", null);
    }

    public GLWindow(final int width, final int height) {
        this(width, height, "GLOOP App", null);
    }

    public boolean isValid() {
        return this.window != INVALID_WINDOW_ID;
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

            if (!isGLFWInit) {
                throw new GLException("Could not initialize GLFW!");
            }
        }

        this.thread = GLThread.create();
        this.thread.submitGLTask(new InitTask());         
    }       

    private Optional<GLMouse> mouse = Optional.empty();

    private GLMouse newMouse() {
        final GLMouse ms = new GLMouse(this);

        this.setMouseButtonCallback(ms);
        this.setMouseEnteredCallback(ms);
        this.setMousePositionCallback(ms);

        this.mouse = Optional.of(ms);

        return ms;
    }

    public GLMouse getMouse() {
        return this.mouse.orElseGet(this::newMouse);
    }

    private Optional<GLKeyboard> keyboard = Optional.empty();

    private GLKeyboard newKeyboard() {
        final GLKeyboard kb = new GLKeyboard(this);

        this.keyboard = Optional.of(kb);
        this.setKeyCallback(kb);

        return kb;
    }

    public GLKeyboard getKeyboard() {
        return this.keyboard.orElseGet(this::newKeyboard);
    }

    public void setClipboardString(final CharSequence seq) {
        GLFW.glfwSetClipboardString(this.window, seq);
    }

    public String getClipboardString() {
        return GLFW.glfwGetClipboardString(this.window);
    }

    public static double getTime() {
        return GLFW.glfwGetTime();
    }

    public void setMousePositionCallback(final GLMousePositionListener listener) {
        final GLFWCursorPosCallback callback
                = GLFW.GLFWCursorPosCallback(listener::glfwCallback);

        GLFW.glfwSetCursorPosCallback(this.window, callback);

        this.cursorPosCallback = Optional.of(callback);
    }

    public void setMouseButtonCallback(final GLMouseButtonListener listener) {
        final GLFWMouseButtonCallback callback
                = GLFW.GLFWMouseButtonCallback(listener::glfwCallback);

        GLFW.glfwSetMouseButtonCallback(this.window, callback);

        this.mouseButtonCallback = Optional.of(callback);
    }

    public void setMouseEnteredCallback(final GLMouseEnteredListener listener) {
        final GLFWCursorEnterCallback callback
                = GLFW.GLFWCursorEnterCallback(listener::glfwCallback);

        GLFW.glfwSetCursorEnterCallback(this.window, callback);

        this.cursorEntorCallback = Optional.of(callback);
    }

    public void setKeyCallback(final GLKeyListener listener) {
        final GLFWKeyCallback callback
                = GLFW.GLFWKeyCallback(listener::glfwCallback);

        GLFW.glfwSetKeyCallback(this.window, callback);

        this.keyCallback = Optional.of(callback);
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

            final ByteBuffer fbWidth = ByteBuffer
                    .allocateDirect(Integer.BYTES)
                    .order(ByteOrder.nativeOrder());
            final ByteBuffer fbHeight = ByteBuffer
                    .allocateDirect(Integer.BYTES)
                    .order(ByteOrder.nativeOrder());
            
            GLFW.glfwGetFramebufferSize(GLWindow.this.window, fbWidth, fbHeight);            
            GLWindow.this.thread.pushViewport(new GLViewport(0, 0, fbWidth.getInt(0), fbHeight.getInt(0)));
            
            WINDOWS.put(GLWindow.this.window, GLWindow.this);
        }
    }
    
    /**
     * Returns the instance of the window after it is initialized.
     * @return the window post initialization.
     * @since 15.06.01
     */
    public GLWindow waitForInit() {
        if(!this.isValid()){
            this.thread.insertBarrier();
        }
        
        return this;
    }

    public float getAspectRatio() {
        return (float) this.width / (float) this.height;
    }

    public void setVisible(final boolean isVisible) {
        if (isVisible) {
            GLFW.glfwShowWindow(this.window);
        } else {
            GLFW.glfwHideWindow(this.window);
        }
    }

    public void setSize(final int width, final int height) {
        if (!this.isValid()) {
            throw new GLException("Invalid GLWindow!");
        }

        GLFW.glfwSetWindowSize(this.window, width, height);
    }

    private final ByteBuffer tmpBuffer = ByteBuffer
            .allocateDirect(Integer.BYTES)
            .order(ByteOrder.nativeOrder());
   
    public final int getFramebufferWidth() {
        if (!this.isValid()) {
            throw new GLException("Invalid GLWindow!");
        }

        this.tmpBuffer.clear();
        GLFW.glfwGetFramebufferSize(this.window, this.tmpBuffer, null);
        return this.tmpBuffer.getInt(0);
    }
    
    public final int getFramebufferHeight() {
        if(!this.isValid()) {
            throw new GLException("Invalid GLWindow!");
        }
        
        this.tmpBuffer.clear();
        GLFW.glfwGetFramebufferSize(this.window, null, this.tmpBuffer);
        return this.tmpBuffer.getInt(0);
    }

    public int getX() {
        if (!this.isValid()) {
            throw new GLException("Invalid GLWindow!");
        }

        this.tmpBuffer.clear();
        GLFW.glfwGetWindowPos(this.window, this.tmpBuffer, null);
        return this.tmpBuffer.getInt(0);
    }

    public int getY() {
        if (!this.isValid()) {
            throw new GLException("Invalid GLWindow!");
        }

        this.tmpBuffer.clear();
        GLFW.glfwGetWindowPos(this.window, null, this.tmpBuffer);
        return this.tmpBuffer.getInt(0);
    }

    public int getWidth() {
        if (!this.isValid()) {
            throw new GLException("Invalid GLWindow!");
        }

        this.tmpBuffer.clear();
        GLFW.glfwGetWindowSize(this.window, this.tmpBuffer, null);
        return this.tmpBuffer.getInt(0);
    }

    public int getHeight() {
        if (!this.isValid()) {
            throw new GLException("Invalid GLWindow!");
        }

        this.tmpBuffer.clear();
        GLFW.glfwGetWindowSize(this.window, null, this.tmpBuffer);
        return this.tmpBuffer.getInt(0);
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
                GLWindow.this.cleanup();
            } else {
                GLFW.glfwSwapBuffers(GLWindow.this.window);
                GLFW.glfwPollEvents();
            }
        }
    }

    private void cleanup() {
        this.workerThreads.forEach(GLWindow::stop);
        GLFW.glfwDestroyWindow(this.window);
        WINDOWS.remove(this.window);
        this.window = GLWindow.INVALID_WINDOW_ID;
        this.cursorEntorCallback.ifPresent(GLFWCursorEnterCallback::release);
        this.cursorPosCallback.ifPresent(GLFWCursorPosCallback::release);
        this.keyCallback.ifPresent(GLFWKeyCallback::release);
        this.mouseButtonCallback.ifPresent(GLFWMouseButtonCallback::release);
        this.thread.shutdown();
    }

    protected void stop() {
        GLFW.glfwSetWindowShouldClose(this.window, GL11.GL_TRUE);
    }

    private final List<GLWindow> workerThreads = new ArrayList<>();

    public GLThread newWorkerThread() {
        final GLWindow dummy = new GLWindow(0, 0, "WORKER", this);

        this.workerThreads.add(dummy);

        return dummy.getThread();
    }

    public long getHandle() {
        return this.window;
    }

    @Override
    public String toString() {
        return "GLWindow: " + this.window;
    }

    public static class PollInputs extends GLTask {

        @Override
        public void run() {
            GLFW.glfwPollEvents();
        }
    }
}
