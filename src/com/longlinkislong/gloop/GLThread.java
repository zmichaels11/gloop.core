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
import org.lwjgl.opengl.DisplayMode;

/**
 *
 * @author zmichaels
 */
public class GLThread {

    private final ExecutorService internalExecutor = Executors.newSingleThreadExecutor();
    private Thread internalThread = null;

    public void shutdown() {
        this.internalExecutor.shutdown();
    }

    protected boolean isCurrent() {
        return (Thread.currentThread() == this.internalThread);
    }

    public void submitGLTask(final GLTask task) {
        this.internalExecutor.execute(task);
    }

    public void scheduleGLTask(final GLTask task) {
        this.internalExecutor.execute(new GLTask() {

            @Override
            public void run() {
                task.run();
                internalExecutor.execute(this);
            }
        });
    }

    public <ReturnType> GLFuture<ReturnType> submitGLQuery(
            final GLQuery<ReturnType> query) {

        final Future<ReturnType> raw = this.internalExecutor.submit(query);

        return new GLFuture<>(raw);

    }

    private GLThread() {
    }

    private static final class Holder {

        private static final GLThread INSTANCE = new GLThread();
    }

    public static GLThread getDefaultInstance() {
        return Holder.INSTANCE;
    }

    public class OpenDisplayTask extends GLTask {

        public final int width;
        public final int height;
        public final String title;

        public OpenDisplayTask() {
            this("GLOOP App", 640, 480);
        }

        public OpenDisplayTask(
                final CharSequence title,
                final int width, final int height) {

            this.width = width;
            this.height = height;
            this.title = title.toString();
        }

        @Override
        public void run() {
            try {
                Display.setDisplayMode(new DisplayMode(this.width, this.height));
                Display.setTitle(this.title);
                Display.create();
                GLThread.this.internalThread = Thread.currentThread();
            } catch (LWJGLException ex) {
                throw new GLException("Unable to create Display!", ex);
            }
        }

    }

    public class SyncedUpdateTask extends GLTask {

        public final int fps;

        public SyncedUpdateTask(final int fps) {
            this.fps = fps;
        }

        @Override
        public void run() {
            Display.sync(this.fps);
            Display.update();
            if (Display.isCloseRequested()) {
                Display.destroy();
            }
        }

    }

    public class UpdateTask extends GLTask {

        @Override
        public void run() {
            Display.update();

            if (Display.isCloseRequested()) {
                Display.destroy();
            }
        }

    }
}
