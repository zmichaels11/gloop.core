/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.longlinkislong.gloop;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import org.lwjgl.LWJGLException;
import org.lwjgl.opengl.Display;

/**
 *
 * @author zmichaels
 */
public class GLThread {

    private final ExecutorService internalExecutor = Executors.newSingleThreadExecutor();
    private Thread internalThread = null;

    public void submitGLTask(final GLTask task) {
        this.internalExecutor.execute(task);
    }

    public <ReturnType> GLFuture<ReturnType> submitGLQuery(
            final GLQuery<ReturnType> query) {

        final Future<ReturnType> raw = this.internalExecutor.submit(query);

        return new GLFuture<>(raw);

    }

    public final boolean isCurrent() {
        return Thread.currentThread() == this.internalThread;
    }

    protected final void setCurrent() {
        this.internalThread = Thread.currentThread();
    }

    private GLThread() {
    }

    private static final class Holder {

        private static final GLThread INSTANCE = new GLThread();
    }

    public static GLThread getDefaultInstance() {
        return Holder.INSTANCE;
    }

    public class GLOpenDisplayTask extends GLTask {

        public final int width;
        public final int height;
        public final String title;

        public GLOpenDisplayTask() {
            this("GLOOP App", 640, 480);
        }

        public GLOpenDisplayTask(
                final CharSequence title,
                final int width, final int height) {

            this.width = width;
            this.height = height;
            this.title = title.toString();
        }

        @Override
        public void run() {
            try {
                Display.create();
                GLThread.this.setCurrent();
            } catch (LWJGLException ex) {
                throw new GLException("Unable to create Display!", ex);
            }
        }

    }

    public class GLSyncedUpdateTask extends GLTask {

        public final int fps;

        public GLSyncedUpdateTask(final int fps) {
            this.fps = fps;
        }

        @Override
        public void run() {
            Display.sync(this.fps);
            Display.update();
            GLThread.this.setCurrent();
            GLThread.this.internalExecutor.execute(this);
        }

    }

    public class GLUpdateTask extends GLTask {

        @Override
        public void run() {
            Display.update();
            GLThread.this.setCurrent();
            GLThread.this.internalExecutor.execute(this);
        }

    }
}
