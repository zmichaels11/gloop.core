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
import org.lwjgl.glfw.Callbacks;
import org.lwjgl.glfw.GLFW;
import static org.lwjgl.glfw.GLFW.GLFW_ALPHA_BITS;
import static org.lwjgl.glfw.GLFW.GLFW_BLUE_BITS;
import static org.lwjgl.glfw.GLFW.GLFW_CONTEXT_VERSION_MAJOR;
import static org.lwjgl.glfw.GLFW.GLFW_CONTEXT_VERSION_MINOR;
import static org.lwjgl.glfw.GLFW.GLFW_DEPTH_BITS;
import static org.lwjgl.glfw.GLFW.GLFW_GREEN_BITS;
import static org.lwjgl.glfw.GLFW.GLFW_OPENGL_CORE_PROFILE;
import static org.lwjgl.glfw.GLFW.GLFW_OPENGL_FORWARD_COMPAT;
import static org.lwjgl.glfw.GLFW.GLFW_OPENGL_PROFILE;
import static org.lwjgl.glfw.GLFW.GLFW_RED_BITS;
import static org.lwjgl.glfw.GLFW.GLFW_REFRESH_RATE;
import static org.lwjgl.glfw.GLFW.GLFW_SAMPLES;
import static org.lwjgl.glfw.GLFW.GLFW_STENCIL_BITS;
import static org.lwjgl.glfw.GLFW.glfwWindowHint;
import org.lwjgl.glfw.GLFWCursorEnterCallback;
import org.lwjgl.glfw.GLFWCursorPosCallback;
import org.lwjgl.glfw.GLFWFramebufferSizeCallback;
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
 * A GLWindow represents a window that handles OpenGL drawing.
 *
 * @author zmichaels
 * @since 15.06.24
 */
public class GLWindow {

    public static int OPENGL_VERSION_MAJOR;
    public static int OPENGL_VERSION_MINOR;
    public static int OPENGL_SWAP_INTERVAL;
    public static int OPENGL_SAMPLES;
    public static final int OPENGL_RED_BITS;
    public static final int OPENGL_BLUE_BITS;
    public static final int OPENGL_GREEN_BITS;
    public static final int OPENGL_ALPHA_BITS;
    public static final int OPENGL_DEPTH_BITS;
    public static final int OPENGL_STENCIL_BITS;
    public static final int OPENGL_REFRESH_RATE;

    private static final long INVALID_WINDOW_ID = -1L;
    protected volatile long window = INVALID_WINDOW_ID;
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
    private Optional<GLFWFramebufferSizeCallback> resizeCallback = Optional.empty();
    private Optional<Runnable> onClose = Optional.empty();
    private final long monitor;
    private volatile boolean hasInitialized = false;
    private final List<Runnable> cleanupTasks = new ArrayList<>();

    protected static final Map<Long, GLWindow> WINDOWS = new TreeMap<>(Long::compareTo);
    private static final List<GLGamepad> GAMEPADS;

    static {
        final String glVersion = System.getProperty("gloop.opengl.version", "3.2");
        OPENGL_VERSION_MAJOR = Integer.parseInt(glVersion.substring(0, glVersion.indexOf(".")));
        OPENGL_VERSION_MINOR = Integer.parseInt(glVersion.substring(glVersion.indexOf(".") + 1));
        OPENGL_REFRESH_RATE = Integer.parseInt(System.getProperty("gloop.opengl.refresh_rate", "-1"));
        OPENGL_SWAP_INTERVAL = Integer.parseInt(System.getProperty("gloop.opengl.swap_interval", "1"));
        OPENGL_SAMPLES = Integer.parseInt(System.getProperty("gloop.opengl.msaa", "-1"));
        OPENGL_RED_BITS = Integer.parseInt(System.getProperty("gloop.opengl.red_bits", "8"));
        OPENGL_GREEN_BITS = Integer.parseInt(System.getProperty("gloop.opengl.green_bits", "8"));
        OPENGL_BLUE_BITS = Integer.parseInt(System.getProperty("gloop.opengl.blue_bits", "8"));
        OPENGL_ALPHA_BITS = Integer.parseInt(System.getProperty("gloop.opengl.alpha_bits", "8"));
        OPENGL_DEPTH_BITS = Integer.parseInt(System.getProperty("gloop.opengl.depth_bits", "24"));
        OPENGL_STENCIL_BITS = Integer.parseInt(System.getProperty("gloop.opengl.stencil_bits", "8"));

        if (GLFW.glfwInit() != GL_TRUE) {
            throw new GLException("Could not initialize GLFW!");
        }

        GLFW.glfwSetErrorCallback(Callbacks.errorCallbackThrow());

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

    /**
     * Registers a callback to run when the window is closed.
     *
     * @param onCloseCallback the callback to run
     * @since 15.06.24
     */
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
        return (this.hasInitialized && this.window != INVALID_WINDOW_ID);
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

    /**
     * Retrieves the mouse object associated with the window. A GLWindow object
     * may own up to one mouse object.
     *
     * @return the mouse object.
     * @throws GLException if the window is not initialized.
     * @see
     * <a href="http://www.glfw.org/docs/latest/input.html#input_mouse">GLFW
     * Mouse Input</a>
     * @since 15.06.24
     */
    public GLMouse getMouse() throws GLException {
        return new MouseQuery().glCall(this.getGLThread());
    }

    /**
     * A GLQuery that requests for the Mouse object.
     *
     * @since 15.06.30
     */
    public class MouseQuery extends GLQuery<GLMouse> {

        @Override
        public GLMouse call() throws Exception {
            if (!GLWindow.this.isValid()) {
                throw new GLException("GLWindow is not valid!");
            }

            return GLWindow.this.mouse.orElseGet(GLWindow.this::newMouse);
        }
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
     * @see
     * <a href="http://www.glfw.org/docs/latest/input.html#input_keyboard">GLFW
     * Keyboard Input</a>
     * @since 15.06.07
     */
    public GLKeyboard getKeyboard() throws GLException {
        return new KeyboardQuery().glCall(this.getGLThread());
    }

    /**
     * A GLQuery that requests for the GLKeyboard object.
     *
     * @since 15.06.30
     */
    public class KeyboardQuery extends GLQuery<GLKeyboard> {

        @Override
        public GLKeyboard call() throws Exception {
            if (!GLWindow.this.isValid()) {
                throw new GLException("Invalid GLWindow!");
            }

            return GLWindow.this.keyboard.orElseGet(GLWindow.this::newKeyboard);
        }

    }

    /**
     * Sets the clipboard string.
     *
     * @param seq the string to set
     * @throws GLException if the window is not initialized.
     * @see <a href="http://www.glfw.org/docs/latest/input.html#clipboard">GLFW
     * Clipboard Input and Output</a>
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
     * @see <a href="http://www.glfw.org/docs/latest/input.html#clipboard">GLFW
     * Clipboard Input and Output</a>
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
     * @see <a href="http://www.glfw.org/docs/latest/input.html#time">GLFW Time
     * Input</a>
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

    private void setFramebufferResizeCallback(final GLFramebufferResizeListener listener) {
        final GLFWFramebufferSizeCallback callback
                = GLFW.GLFWFramebufferSizeCallback(listener::glfwFramebufferResizeCallback);

        GLFW.glfwSetFramebufferSizeCallback(this.window, callback);

        this.resizeCallback = Optional.of(callback);
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
        return new DPIQuery().glCall(this.getGLThread());
    }

    public class DPIQuery extends GLQuery<Double> {

        @Override
        public Double call() throws Exception {
            if(!GLWindow.this.isValid()) {
                throw new GLException("GLWindow is not valid!");
            }
            
            final long mHandle = GLFW.glfwGetWindowMonitor(GLWindow.this.monitor);

            final ByteBuffer mode = GLFW.glfwGetVideoMode(mHandle);

            final ByteBuffer widthMM = ByteBuffer
                    .allocateDirect(Integer.BYTES)
                    .order(ByteOrder.nativeOrder());

            GLFW.glfwGetMonitorPhysicalSize(mHandle, widthMM, null);

            final int vWidth = GLFWvidmode.width(mode);

            return (vWidth / (widthMM.getInt() / 25.4));
        }
    }

    private class InitTask extends GLTask {

        @Override
        public void run() {
            glfwWindowHint(GLFW.GLFW_VISIBLE, GL_FALSE);
            glfwWindowHint(GLFW.GLFW_RESIZABLE, GL_TRUE);

            glfwWindowHint(GLFW_CONTEXT_VERSION_MAJOR, OPENGL_VERSION_MAJOR);
            glfwWindowHint(GLFW_CONTEXT_VERSION_MINOR, OPENGL_VERSION_MINOR);
            glfwWindowHint(GLFW_OPENGL_FORWARD_COMPAT, GL_TRUE);
            glfwWindowHint(GLFW_OPENGL_PROFILE, GLFW_OPENGL_CORE_PROFILE);
            glfwWindowHint(GLFW_SAMPLES, OPENGL_SAMPLES);
            glfwWindowHint(GLFW_RED_BITS, OPENGL_RED_BITS);
            glfwWindowHint(GLFW_BLUE_BITS, OPENGL_BLUE_BITS);
            glfwWindowHint(GLFW_GREEN_BITS, OPENGL_GREEN_BITS);
            glfwWindowHint(GLFW_ALPHA_BITS, OPENGL_ALPHA_BITS);
            glfwWindowHint(GLFW_DEPTH_BITS, OPENGL_DEPTH_BITS);
            glfwWindowHint(GLFW_STENCIL_BITS, OPENGL_STENCIL_BITS);
            glfwWindowHint(GLFW_REFRESH_RATE, OPENGL_REFRESH_RATE);

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
            GLFW.glfwSwapInterval(OPENGL_SWAP_INTERVAL);
            GLContext.createFromCurrent();

            final ByteBuffer fbWidth = ByteBuffer
                    .allocateDirect(Integer.BYTES)
                    .order(ByteOrder.nativeOrder());
            final ByteBuffer fbHeight = ByteBuffer
                    .allocateDirect(Integer.BYTES)
                    .order(ByteOrder.nativeOrder());

            GLFW.glfwGetFramebufferSize(GLWindow.this.window, fbWidth, fbHeight);
            GLWindow.this.thread.currentViewport = new GLViewport(0, 0, fbWidth.getInt(), fbHeight.getInt());

            GLWindow.this.handler.register();

            WINDOWS.put(GLWindow.this.window, GLWindow.this);
            GLWindow.this.hasInitialized = true;
        }
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

    /**
     * Sets the visibility of the window.
     *
     * @param isVisible the visibility flag.
     * @since 15.06.24
     */
    public void setVisible(final boolean isVisible) {
        new SetWindowVisibilityTask(isVisible).glRun(this.getGLThread());
    }

    public class SetWindowVisibilityTask extends GLTask {

        final boolean visibility;

        public SetWindowVisibilityTask(final boolean isVisible) {
            this.visibility = isVisible;
        }

        @Override
        public void run() {
            if (!GLWindow.this.isValid()) {
                throw new GLException("GLWindow is not valid!");
            }

            if (this.visibility) {
                GLFW.glfwShowWindow(GLWindow.this.window);
            } else {
                GLFW.glfwHideWindow(GLWindow.this.window);
            }
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
        new SetWindowSizeTask(width, height).glRun(this.getGLThread());
    }

    public class SetWindowSizeTask extends GLTask {

        final int width;
        final int height;

        public SetWindowSizeTask(final int width, final int height) {
            if ((this.width = width) < 0) {
                throw new GLException("Cannot set window width to less than 0!");
            }

            if ((this.height = height) < 0) {
                throw new GLException("Cannot set window height to less than 0!");
            }
        }

        @Override
        public void run() {
            if (!GLWindow.this.isValid()) {
                throw new GLException("GLWindow is not valid!");
            }

            GLFW.glfwSetWindowSize(GLWindow.this.window, this.width, this.height);
        }

    }

    /**
     * Retrieves the width of the back buffer.
     *
     * @return the back buffer width.
     * @throws GLException if the window is not initialized.
     * @since 15.06.07
     */
    public final int getFramebufferWidth() throws GLException {
        final GLQuery<int[]> sizeQuery = new FramebufferSizeQuery();

        return sizeQuery.glCall(this.getGLThread())[GLTools.WIDTH];
    }

    /**
     * Retrieves the height of the back buffer.
     *
     * @return the back buffer height
     * @throws GLException if the window is not initialized.
     * @since 15.06.07
     */
    public final int getFramebufferHeight() throws GLException {
        final GLQuery<int[]> sizeQuery = new FramebufferSizeQuery();

        return sizeQuery.glCall(this.getGLThread())[GLTools.HEIGHT];
    }

    public class FramebufferSizeQuery extends GLQuery<int[]> {

        @Override
        public int[] call() throws Exception {
            if (!GLWindow.this.isValid()) {
                throw new GLException("GLWindow is not valid!");
            }

            final ByteBuffer width = ByteBuffer
                    .allocateDirect(Integer.BYTES)
                    .order(ByteOrder.nativeOrder());
            final ByteBuffer height = ByteBuffer
                    .allocateDirect(Integer.BYTES)
                    .order(ByteOrder.nativeOrder());

            GLFW.glfwGetFramebufferSize(GLWindow.this.window, width, height);

            return new int[]{width.getInt(), height.getInt()};
        }

    }

    /**
     * Retrieves the x-position of the top-left of the window.
     *
     * @return the top-left x coordinate in screen space.
     * @throws GLException if the the window is not initialized.
     * @since 15.06.07
     */
    public int getX() throws GLException {
        final GLQuery<int[]> posQuery = this.new WindowPositionQuery();

        return posQuery.glCall(this.getGLThread())[GLTools.X];
    }

    /**
     * Retrieves the y-position of the top-left of the window.
     *
     * @return the top-left y coordinate in screen space.
     * @throws GLException if the window has not been initialized.
     * @since 15.06.07
     */
    public int getY() throws GLException {
        final GLQuery<int[]> posQuery = this.new WindowPositionQuery();

        return posQuery.glCall(this.getGLThread())[GLTools.Y];
    }

    /**
     * A GLQuery that requests the position of the window.
     *
     * @since 15.06.30
     */
    public class WindowPositionQuery extends GLQuery<int[]> {

        @Override
        public int[] call() throws Exception {
            if (!GLWindow.this.isValid()) {
                throw new GLException("GLWindow is not valid!");
            }

            final ByteBuffer x = ByteBuffer
                    .allocateDirect(Integer.BYTES)
                    .order(ByteOrder.nativeOrder());
            final ByteBuffer y = ByteBuffer
                    .allocateDirect(Integer.BYTES)
                    .order(ByteOrder.nativeOrder());

            GLFW.glfwGetWindowPos(GLWindow.this.window, x, y);

            return new int[]{x.getInt(), y.getInt()};
        }

    }

    /**
     * Retrieves the width of the window.
     *
     * @return the window width
     * @throws GLException if the window has not been initialized.
     * @since 15.06.05
     */
    public int getWidth() {
        final GLQuery<int[]> sizeQuery = this.new WindowSizeQuery();

        return sizeQuery.glCall(this.getGLThread())[GLTools.WIDTH];
    }

    /**
     * Retrieves the height of the window.
     *
     * @return the height of the window.
     * @throws GLException if the window has not been initialized.
     * @since 15.06.05
     */
    public int getHeight() throws GLException {
        final GLQuery<int[]> sizeQuery = this.new WindowSizeQuery();

        return sizeQuery.glCall(this.getGLThread())[GLTools.HEIGHT];
    }

    /**
     * A GLTask that requests the size of the window.
     *
     * @since 15.06.30
     */
    public class WindowSizeQuery extends GLQuery<int[]> {

        @Override
        public int[] call() throws Exception {
            if (!GLWindow.this.isValid()) {
                throw new GLException("GLWindow is not valid!");
            }

            final ByteBuffer width = ByteBuffer
                    .allocateDirect(Integer.BYTES)
                    .order(ByteOrder.nativeOrder());
            final ByteBuffer height = ByteBuffer
                    .allocateDirect(Integer.BYTES)
                    .order(ByteOrder.nativeOrder());

            GLFW.glfwGetWindowSize(GLWindow.this.window, width, height);

            return new int[]{width.getInt(), height.getInt()};
        }

    }

    /**
     * Retrieves the thread owned by the GLWindow.
     *
     * @return the thread owned by the window.
     * @since 15.06.05
     */
    public GLThread getGLThread() {
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
        this.updateTask.glRun(this.getGLThread());
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

    /**
     * Closes the window.
     *
     * @since 15.07.01
     */
    public void close() {
        new CloseTask().glRun(this.getGLThread());
    }

    /**
     * A GLTask that closes the GLWindow.
     *
     * @since 15.07.01
     */
    public class CloseTask extends GLTask {

        @Override
        public void run() {
            if (!GLWindow.this.isValid()) {
                throw new GLException("GLWindow is not valid!");
            }

            GLFW.glfwSetWindowShouldClose(GLWindow.this.window, GL_TRUE);
        }
    }

    private void cleanup() {
        this.cleanupTasks.forEach(Runnable::run);
        this.cleanupTasks.clear();
        this.workerThreads.forEach(GLWindow::stop);
        GLFW.glfwDestroyWindow(this.window);
        WINDOWS.remove(this.window);
        this.window = GLWindow.INVALID_WINDOW_ID;
        this.cursorEnterCallback.ifPresent(GLFWCursorEnterCallback::release);
        this.cursorPosCallback.ifPresent(GLFWCursorPosCallback::release);
        this.keyCallback.ifPresent(GLFWKeyCallback::release);
        this.mouseButtonCallback.ifPresent(GLFWMouseButtonCallback::release);
        this.scrollCallback.ifPresent(GLFWScrollCallback::release);
        this.resizeCallback.ifPresent(GLFWFramebufferSizeCallback::release);
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

        return dummy.getGLThread();
    }

    @Override
    public String toString() {
        return "GLWindow: " + this.window;
    }

    /**
     * Attempts to append a task to the end of the cleanup queue.
     *
     * @param task the task to append.
     * @since 15.06.30
     */
    public void appendToCleanup(final Runnable task) {
        this.cleanupTasks.add(task);
    }

    /**
     * Attempts to remove a task from the cleanup queue.
     *
     * @param task the task to remove.
     * @return true if the task was removed.
     * @since 15.06.30
     */
    public boolean removeFromCleanup(final Runnable task) {
        return this.cleanupTasks.remove(task);
    }

    /**
     * Removes all tasks from the cleanup task queue.
     *
     * @since 15.06.30
     */
    public void clearCleanup() {
        this.cleanupTasks.clear();
    }

    private WindowHandler handler = new WindowHandler();

    /**
     * Adds a listener for when the window resizes.
     *
     * @param listener the resize listener.
     * @return true if the listener was registered.
     * @since 15.06.24
     */
    public boolean addWindowResizeListener(final GLFramebufferResizeListener listener) {
        return this.handler.resizeListeners.add(listener);
    }

    /**
     * Attempts to remove a window resize listener.
     *
     * @param listener the listener to remove.
     * @return true if the listener was removed.
     * @since 15.06.24
     */
    public boolean removeWindowResizeListener(final GLFramebufferResizeListener listener) {
        return this.handler.resizeListeners.remove(listener);
    }

    /**
     * Removes all window listeners from the window.
     *
     * @since 15.06.24
     */
    public void clearWindowListeners() {
        this.handler.resizeListeners.clear();
    }

    private class WindowHandler implements GLFramebufferResizeListener {

        final List<GLFramebufferResizeListener> resizeListeners = new ArrayList<>();

        @Override
        public void framebufferResizedActionPerformed(GLWindow window, GLViewport view) {
            if (!window.getGLThread().viewportStack.isEmpty()) {
                throw new GLException("Viewport stack is not empty on Window Resize event!");
            }

            view.applyViewport();

            this.resizeListeners.forEach((listener) -> {
                listener.framebufferResizedActionPerformed(window, view);
            });
        }

        void register() {
            GLWindow.this.setFramebufferResizeCallback(this);
        }
    }
}
