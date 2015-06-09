/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.longlinkislong.gloop;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.TreeMap;
import org.lwjgl.glfw.GLFW;
import org.lwjgl.glfw.GLFWCursorEnterCallback;
import org.lwjgl.glfw.GLFWCursorPosCallback;
import org.lwjgl.glfw.GLFWKeyCallback;
import org.lwjgl.glfw.GLFWMouseButtonCallback;
import org.lwjgl.glfw.GLFWScrollCallback;
import org.lwjgl.glfw.GLFWvidmode;
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
    protected long window = INVALID_WINDOW_ID;
    private final int width;
    private final int height;
    private final String title;
    private GLThread thread = null;
    private final GLWindow shared;
    private Optional<GLFWKeyCallback> keyCallback = Optional.empty();
    private Optional<GLFWMouseButtonCallback> mouseButtonCallback = Optional.empty();
    private Optional<GLFWCursorPosCallback> cursorPosCallback = Optional.empty();
    private Optional<GLFWCursorEnterCallback> cursorEnterCallback = Optional.empty();
    private Optional<GLFWScrollCallback> scrollCallback = Optional.empty();
    private Optional<Runnable> onClose = Optional.empty();
    private final long monitor;

    protected static final Map<Long, GLWindow> WINDOWS = new TreeMap<>(Long::compareTo);
    private static final List<GLGamepad> GAMEPADS;

    static {
        if (GLFW.glfwInit() != GL_TRUE) {
            throw new GLException("Could not initialize GLFW!");
        }

        final List<GLGamepad> gamepads = new ArrayList<>();
        for (int i = 0; i < GLFW.GLFW_JOYSTICK_LAST; i++) {
            if (GLFW.glfwJoystickPresent(i) == GL11.GL_TRUE) {
                gamepads.add(new GLGamepad(i));
            }
        }

        GAMEPADS = Collections.unmodifiableList(gamepads);
    }

    /**
     * Retrieves the list of gamepads
     *
     * @return the list of gamepads
     * @since 15.06.07
     */
    public static List<GLGamepad> listGamepads() {
        return GAMEPADS;
    }

    /**
     * Returns a list of active GLWindow objects.
     *
     * @return the list of windows.
     * @since 15.06.07
     */
    public static List<GLWindow> listActiveWindows() {
        final List<GLWindow> windows = new ArrayList<>();

        windows.addAll(WINDOWS.values());

        return Collections.unmodifiableList(windows);
    }

    public void setOnClose(final Runnable onCloseCallback) {
        this.onClose = Optional.ofNullable(onCloseCallback);
    }
    
    /**
     * Constructs a new GLWindow with all default parameters.
     *
     * @since 15.06.07
     */
    public GLWindow() {
        this(640, 480, "GLOOP App", null);
    }

    /**
     * Constructs a new GLWindow with the supplied size, default title, and no
     * shared context.
     *
     * @param width the width of the window
     * @param height the height of the window
     * @since 15.06.07
     */
    public GLWindow(final int width, final int height) {
        this(width, height, "GLOOP App", null);
    }

    /**
     * Constructs a new GLWindow with the supplied size, title, and no shared
     * context.
     *
     * @param width the width of the window
     * @param height the height of the window
     * @param title the title for the window
     * @since 15.06.07
     */
    public GLWindow(
            final int width, final int height,
            final CharSequence title) {

        this(width, height, title, null);
    }

    /**
     * Constructs a new GLWindow with the supplied size, title, and shared
     * context.
     *
     * @param width the width of the window
     * @param height the height of the window
     * @param title the title of the window
     * @param shared the window to retrieve a context from for sharing. Null
     * specifies no shared context.
     * @since 15.06.07
     */
    public GLWindow(
            final int width, final int height,
            final CharSequence title,
            final GLWindow shared) {

        this.width = width;
        this.height = height;
        this.title = title.toString();
        this.shared = shared;

        this.thread = GLThread.create();
        this.thread.submitGLTask(new InitTask());
        this.monitor = NULL;
    }

    /**
     * Checks if the GLWindow is valid. A window is determined to be valid if it
     * has been initialized and not closed.
     *
     * @return true if the window is initialized.
     * @since 15.06.07
     */
    public boolean isValid() {
        return this.window != INVALID_WINDOW_ID;
    }

    private Optional<GLMouse> mouse = Optional.empty();

    private GLMouse newMouse() {
        final GLMouse ms = new GLMouse(this);

        this.setMouseButtonCallback(ms);
        this.setMouseEnteredCallback(ms);
        this.setMousePositionCallback(ms);
        this.setMouseScrollCallback(ms);

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

    /**
     * Retrieves the keyboard object associated with the window.
     *
     * @return the keyboard object
     * @throws GLException if the window is not initialized.
     * @since 15.06.07
     */
    public GLKeyboard getKeyboard() throws GLException {
        if (!this.isValid()) {
            throw new GLException("Invalid GLWindow!");
        }

        return this.keyboard.orElseGet(this::newKeyboard);
    }

    /**
     * Sets the clipboard string.
     *
     * @param seq the string to set
     * @throws GLException if the window is not initialized.
     * @since 15.06.07
     */
    public void setClipboardString(final CharSequence seq) throws GLException {
        if (!this.isValid()) {
            throw new GLException("Invalid GLWindow!");
        }
        GLFW.glfwSetClipboardString(this.window, seq);
    }

    /**
     * Retrieves the clipboard string
     *
     * @return the clipboard string
     * @throws GLException if the window is not initialized.
     * @since 15.06.07
     */
    public String getClipboardString() throws GLException {
        if (!this.isValid()) {
            throw new GLException("Invalid GLWindow!");
        }

        return GLFW.glfwGetClipboardString(this.window);
    }

    /**
     * Retrieves the time in seconds since the start of the application.
     *
     * @return the current time
     * @since 15.06.07
     */
    public static double getTime() {
        return GLFW.glfwGetTime();
    }

    private void setMouseScrollCallback(final GLMouseScrollListener listener) {
        final GLFWScrollCallback callback
                = GLFW.GLFWScrollCallback(listener::glfwScrollCallback);
        
        GLFW.glfwSetScrollCallback(this.window, callback);
        
        this.scrollCallback = Optional.of(callback);
    }
    
    private void setMousePositionCallback(final GLMousePositionListener listener) {
        final GLFWCursorPosCallback callback
                = GLFW.GLFWCursorPosCallback(listener::glfwCursorPosCallback);
        

        GLFW.glfwSetCursorPosCallback(this.window, callback);

        this.cursorPosCallback = Optional.of(callback);
    }

    private void setMouseButtonCallback(final GLMouseButtonListener listener) {
        final GLFWMouseButtonCallback callback
                = GLFW.GLFWMouseButtonCallback(listener::glfwMouseButtonCallback);

        GLFW.glfwSetMouseButtonCallback(this.window, callback);

        this.mouseButtonCallback = Optional.of(callback);
    }

    private void setMouseEnteredCallback(final GLMouseEnteredListener listener) {
        final GLFWCursorEnterCallback callback
                = GLFW.GLFWCursorEnterCallback(listener::glfwCursorEnteredCallback);
        
        GLFW.glfwSetCursorEnterCallback(this.window, callback);

        this.cursorEnterCallback = Optional.of(callback);
    }

    private void setKeyCallback(final GLKeyListener listener) {
        final GLFWKeyCallback callback
                = GLFW.GLFWKeyCallback(listener::glfwCallback);

        GLFW.glfwSetKeyCallback(this.window, callback);

        this.keyCallback = Optional.of(callback);
    }

    /**
     * Retrieves the DPI of the monitor displaying the window.
     *
     * @return the DPI
     * @throws GLException if the window has not been initialized.
     * @since 15.06.07
     */
    public double getDPI() throws GLException {
        if (!this.isValid()) {
            throw new GLException("Invalid GLWindow!");
        }

        final long mHandle = GLFW.glfwGetWindowMonitor(this.monitor);

        final ByteBuffer mode = GLFW.glfwGetVideoMode(mHandle);

        final ByteBuffer widthMM = ByteBuffer
                .allocateDirect(Integer.BYTES)
                .order(ByteOrder.nativeOrder());

        GLFW.glfwGetMonitorPhysicalSize(mHandle, widthMM, null);

        final int vWidth = GLFWvidmode.width(mode);

        return (vWidth / (widthMM.getInt() / 25.4));
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
                    GLWindow.this.monitor,
                    sharedContextHandle);

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
     * Waits for the window and OpenGL context to be fully initialized before
     * executing subsequent functions.
     *
     * @return self reference after the window has been initialized.
     * @throws GLException if the OpenGL thread could not be synchronized.
     * @since 15.06.07
     */
    public GLWindow waitForInit() throws GLException {
        if (!this.isValid()) {
            this.thread.insertBarrier();
        }

        return this;
    }

    /**
     * Retrieves the aspect ratio for the window. This number is the width
     * divided by the height.
     *
     * @return the aspect ratio.
     * @since 15.06.07
     */
    public double getAspectRatio() {
        return (double) this.width / (double) this.height;
    }

    public void setVisible(final boolean isVisible) {
        if (isVisible) {
            GLFW.glfwShowWindow(this.window);
        } else {
            GLFW.glfwHideWindow(this.window);
        }
    }

    /**
     * Sets the size of the window.
     *
     * @param width the width of the window
     * @param height the height of the window
     * @throws GLException if the window is invalid or an invalid width or
     * height was provided.
     * @since 15.06.07
     */
    public void setSize(final int width, final int height) throws GLException {
        if (!this.isValid()) {
            throw new GLException("Invalid GLWindow!");
        }

        GLFW.glfwSetWindowSize(this.window, width, height);
    }

    private final ByteBuffer tmpBuffer = ByteBuffer
            .allocateDirect(Integer.BYTES)
            .order(ByteOrder.nativeOrder());

    /**
     * Retrieves the width of the back buffer.
     *
     * @return the back buffer width.
     * @throws GLException if the window is not initialized.
     * @since 15.06.07
     */
    public final int getFramebufferWidth() throws GLException {
        if (!this.isValid()) {
            throw new GLException("Invalid GLWindow!");
        }

        final ByteBuffer temp = ByteBuffer
                .allocateDirect(Integer.BYTES)
                .order(ByteOrder.nativeOrder());

        GLFW.glfwGetFramebufferSize(this.window, temp, null);

        return temp.getInt();
    }

    /**
     * Retrieves the height of the back buffer.
     *
     * @return the back buffer height
     * @throws GLException if the window is not initialized.
     * @since 15.06.07
     */
    public final int getFramebufferHeight() throws GLException {
        if (!this.isValid()) {
            throw new GLException("Invalid GLWindow!");
        }

        final ByteBuffer temp = ByteBuffer
                .allocateDirect(Integer.BYTES)
                .order(ByteOrder.nativeOrder());

        GLFW.glfwGetFramebufferSize(this.window, null, temp);

        return temp.getInt();
    }

    /**
     * Retrieves the x-position of the top-left of the window.
     *
     * @return the top-left x coordinate in screen space.
     * @throws GLException if the the window is not initialized.
     * @since 15.06.07
     */
    public int getX() throws GLException {
        if (!this.isValid()) {
            throw new GLException("Invalid GLWindow!");
        }

        final ByteBuffer temp = ByteBuffer
                .allocateDirect(Integer.BYTES)
                .order(ByteOrder.nativeOrder());

        GLFW.glfwGetWindowPos(this.window, temp, null);

        return temp.getInt();
    }

    /**
     * Retrieves the y-position of the top-left of the window.
     *
     * @return the top-left y coordinate in screen space.
     * @throws GLException if the window has not been initialized.
     * @since 15.06.07
     */
    public int getY() throws GLException {
        if (!this.isValid()) {
            throw new GLException("Invalid GLWindow!");
        }

        final ByteBuffer temp = ByteBuffer
                .allocateDirect(Integer.BYTES)
                .order(ByteOrder.nativeOrder());

        GLFW.glfwGetWindowPos(this.window, null, temp);

        return temp.getInt();
    }

    /**
     * Retrieves the width of the window.
     *
     * @return the window width
     * @throws GLException if the window has not been initialized.
     * @since 15.06.05
     */
    public int getWidth() {
        if (!this.isValid()) {
            throw new GLException("Invalid GLWindow!");
        }

        final ByteBuffer temp = ByteBuffer
                .allocateDirect(Integer.BYTES)
                .order(ByteOrder.nativeOrder());

        GLFW.glfwGetWindowSize(this.window, temp, null);
        return temp.getInt();
    }

    /**
     * Retrieves the height of the window.
     *
     * @return the height of the window.
     * @throws GLException if the window has not been initialized.
     * @since 15.06.05
     */
    public int getHeight() throws GLException {
        if (!this.isValid()) {
            throw new GLException("Invalid GLWindow!");
        }

        final ByteBuffer temp = ByteBuffer
                .allocateDirect(Integer.BYTES)
                .order(ByteOrder.nativeOrder());

        GLFW.glfwGetWindowSize(this.window, null, temp);
        return temp.getInt();
    }

    /**
     * Retrieves the thread owned by the GLWindow.
     *
     * @return the thread owned by the window.
     * @since 15.06.05
     */
    public GLThread getThread() {
        return this.thread;
    }

    private final UpdateTask updateTask = new UpdateTask();

    /**
     * Executes an update task on the default thread.
     *
     * @throws GLException if the window is invalid.
     * @since 15.06.05
     */
    public void update() throws GLException {
        if (!this.isValid()) {
            throw new GLException("Invalid GLWindow!");
        }
        this.updateTask.glRun(this.getThread());
    }

    /**
     * A task that updates the window and checks for input.
     *
     * @since 15.06.05
     */
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
        this.cursorEnterCallback.ifPresent(GLFWCursorEnterCallback::release);
        this.cursorPosCallback.ifPresent(GLFWCursorPosCallback::release);
        this.keyCallback.ifPresent(GLFWKeyCallback::release);
        this.mouseButtonCallback.ifPresent(GLFWMouseButtonCallback::release);
        this.onClose.ifPresent(Runnable::run);
        this.thread.shutdown();
    }

    /**
     * Signals that the GLWindow and underlying GLThread should stop.
     *
     * @since 15.06.05
     */
    public void stop() {
        GLFW.glfwSetWindowShouldClose(this.window, GL11.GL_TRUE);
    }

    private final List<GLWindow> workerThreads = new ArrayList<>();

    /**
     * Constructs a new OpenGL worker thread. The worker thread will have a
     * shared context with the window.
     *
     * @return the new worker thread.
     * @throws GLException if the window is invalid.
     * @since 15.06.05
     */
    public GLThread newWorkerThread() throws GLException {
        if (!this.isValid()) {
            throw new GLException("Invalid GLWindow!");
        }
        final GLWindow dummy = new GLWindow(0, 0, "WORKER", this);

        this.workerThreads.add(dummy);

        return dummy.getThread();
    }

    @Override
    public String toString() {
        return "GLWindow: " + this.window;
    }
}
