/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.longlinkislong.gloop;

import static com.longlinkislong.gloop.GLAsserts.checkGLError;
import static com.longlinkislong.gloop.GLAsserts.glErrorMsg;
import java.nio.IntBuffer;
import org.lwjgl.opengl.ARBSync;
import org.lwjgl.opengl.ContextCapabilities;
import org.lwjgl.opengl.GL;
import org.lwjgl.opengl.GL32;

/**
 * GLSync object is an OpenGL object used for determining the execution status
 * of specific commands.
 *
 * @author zmichaels
 * @since 15.07.06
 */
public class GLSync extends GLObject {

    /**
     * The default timeout used by GLSync. By default, DEFAULT_TIMER has the
     * value of 1ms. This constant can be overwritten with the property
     * gloop.glsync.default_timeout.
     *
     * @since 15.07.06
     */
    public static final long DEFAULT_TIMEOUT;

    static {
        final String timeoutProp = "gloop.glsync.default_timeout";
        final String propVal = System.getProperty(timeoutProp, "1000000");

        DEFAULT_TIMEOUT = Math.min(1L, Long.parseLong(propVal));
    }

    private static final long INVALID_SYNC = -1;
    private volatile long sync = INVALID_SYNC;

    @FunctionalInterface
    private interface FenceSync {

        long get(int sync, int flags);
    }

    private static final FenceSync NULL_FENCE_SYNC = (sync, flags) -> {
        throw new IllegalStateException("glFenceSync was called before it was fetched! glFenceSync requires an instance of GLSync.InitTask to run prior to being called.");
    };
    private FenceSync glFenceSync = NULL_FENCE_SYNC;

    @FunctionalInterface
    private interface GetSynci {

        int get(long sync, int pName, IntBuffer buffer);
    }

    private static final GetSynci NULL_GET_SYNCI = (sync, pName, buffer) -> {
        throw new IllegalStateException("glGetSynci was called before it was fetched! glGetSynci requires an instance of GLSync.InitTask to run prior to being called.");
    };
    private GetSynci glGetSynci = NULL_GET_SYNCI;

    @FunctionalInterface
    private interface DeleteSync {

        void call(long sync);
    }

    private static final DeleteSync NULL_DELETE_SYNC = (sync) -> {
        throw new IllegalStateException("glDeleteSync was called before it was fetched! glDeleteSync requires an instance of GLSync.InitTask to run prior to being called.");
    };
    private DeleteSync glDeleteSync = NULL_DELETE_SYNC;

    private interface ClientWaitSync {

        int call(long sync, int waitbits, long timeout);
    }
    private static final ClientWaitSync NULL_CLIENT_WAIT_SYNC = (sync, bits, timeout) -> {
        throw new IllegalStateException("glClientWaitSync was called before it was fetched! glClientWaitSync requires an instance of GLSync.InitTask to run prior to being called.");
    };
    private ClientWaitSync glClientWaitSync = NULL_CLIENT_WAIT_SYNC;

    /**
     * Constructs a new GLSync object on the specified OpenGL thread.
     *
     * @param thread the OpenGL thread.
     * @since 15.07.06
     */
    public GLSync(final GLThread thread) {
        super(thread);
        this.init();
    }

    /**
     * Constructs a new GLSync object on the default OpenGL thread.
     *
     * @since 15.07.06
     */
    public GLSync() {
        this(GLThread.getDefaultInstance());
    }

    /**
     * Initializes the GLSync object.
     */
    public final void init() {
        new InitTask().glRun(this.getThread());
    }

    /**
     * A GLTask that initializes the GLSync object.
     *
     * @since 15.07.09
     */
    public class InitTask extends GLTask {

        @Override
        public void run() {
            final ContextCapabilities cap = GL.getCapabilities();

            if (cap.OpenGL32) {
                GLSync.this.glFenceSync = GL32::glFenceSync;
                GLSync.this.glGetSynci = GL32::glGetSynci;
                GLSync.this.glDeleteSync = GL32::glDeleteSync;
                GLSync.this.glClientWaitSync = GL32::glClientWaitSync;
            } else if (cap.GL_ARB_sync) {
                GLSync.this.glFenceSync = ARBSync::glFenceSync;
                GLSync.this.glGetSynci = ARBSync::glGetSynci;
                GLSync.this.glDeleteSync = ARBSync::glDeleteSync;
                GLSync.this.glClientWaitSync = ARBSync::glClientWaitSync;
            } else {
                GLSync.this.glFenceSync = (sync, flags) -> {
                    throw new UnsupportedOperationException("glFenceSync is not supported! glFenceSync requires either an OpenGL 3.2 context or ARB_sync.");
                };
                GLSync.this.glGetSynci = (sync, pName, buffer) -> {
                    throw new UnsupportedOperationException("glGetSynci is not supported! glSynci requires either an OpenGL 3.2 context or ARB_sync.");
                };
                GLSync.this.glDeleteSync = (sync) -> {
                    throw new UnsupportedOperationException("glDeleteSync is not supported! glDeleteSync requires either an OpenGL 3.2 context or ARB_sync.");
                };
                GLSync.this.glClientWaitSync = (sync, bits, timeout) -> {
                    throw new UnsupportedOperationException("glClientWaitSync is not supported! glClientWaitSync requires either an OpenGL 3.2 context or ARB_sync.");
                };
            }
        }
    }

    /**
     * Checks if the GLSync object has been fenced.
     *
     * @return true if the GLSync object has been fenced and not deleted.
     * @since 15.07.06
     */
    public boolean isFenced() {
        return this.sync != INVALID_SYNC;
    }

    /**
     * Fences the GLSync object.
     *
     * @since 15.07.06
     */
    public void fence() {
        new FenceTask().glRun(this.getThread());
    }

    /**
     * Sends a GLSync object to the OpenGL task queue.
     *
     * @since 15.07.06
     */
    public class FenceTask extends GLTask {

        @Override
        public void run() {
            if (GLSync.this.isFenced()) {
                throw new GLException("GLSync object is already fenced!");
            }

            GLSync.this.sync = GLSync.this.glFenceSync.get(GL32.GL_SYNC_GPU_COMMANDS_COMPLETE, 0);
            assert checkGLError() : glErrorMsg("glFenceSync(II)", "GL_SYNC_GPU_COMMANDS_COMPLETE", 0);
        }
    }

    /**
     * Retrieves the current status of the sync object.
     *
     * @return the sync object.
     * @since 15.07.06
     */
    public GLSyncStatus getSyncStatus() {
        return new SyncStatusQuery().glCall(this.getThread());
    }

    /**
     * Retrieves the status for the GLSync object.
     *
     * @since 15.07.06
     */
    public class SyncStatusQuery extends GLQuery<GLSyncStatus> {

        @Override
        public GLSyncStatus call() throws Exception {
            if (!GLSync.this.isFenced()) {
                throw new GLException("GLSync object needs to be fenced prior to requesting a sync status!");
            }
            final int val = GLSync.this.glGetSynci.get(GLSync.this.sync, GL32.GL_SYNC_STATUS, null);
            assert checkGLError() : glErrorMsg("glGetSynci(II*)", GLSync.this.sync, "GL_SYNC_STATUS", 0);

            return GLSyncStatus.of(val).orElseThrow(GLException::new);
        }
    }

    /**
     * Requests that the GLSync object is deleted.
     *
     * @since 15.07.06
     */
    public void delete() {
        new DeleteTask().glRun(this.getThread());
    }

    /**
     * A GLTask that clears the GLSync object.
     *
     * @since 15.07.06
     */
    public class DeleteTask extends GLTask {

        @Override
        public void run() {
            if (!GLSync.this.isFenced()) {
                throw new GLException("GLSync object needs to be fenced prior to sending a DeleteSynctask!");
            }

            GLSync.this.glDeleteSync.call(GLSync.this.sync);
            assert checkGLError() : glErrorMsg("glDeleteSync(I)", GLSync.this.sync);

            GLSync.this.sync = INVALID_SYNC;
            GLSync.this.glClientWaitSync = NULL_CLIENT_WAIT_SYNC;
            GLSync.this.glDeleteSync = NULL_DELETE_SYNC;
            GLSync.this.glFenceSync = NULL_FENCE_SYNC;
            GLSync.this.glGetSynci = NULL_GET_SYNCI;
        }
    }

    /**
     * Executes a GLQuery requesting to wait for completion or until the default
     * timeout expires.
     *
     * @return the result of the wait sync.
     * @since 15.07.06
     */
    public GLWaitSyncStatus waitSync() {
        return new WaitSyncQuery().glCall(this.getThread());
    }

    /**
     * Executes a GLQuery requesting to wait for completion or until the timeout
     * expires.
     *
     * @param timeout the length of time in nanoseconds to wait before
     * returning.
     * @return the result of the wait sync.
     * @since 15.07.06
     */
    public GLWaitSyncStatus waitSync(final long timeout) {
        return new WaitSyncQuery(timeout).glCall(this.getThread());
    }

    /**
     * A GLQuery that waits up to a specified timeout value.
     *
     * @since 15.07.06
     */
    public class WaitSyncQuery extends GLQuery<GLWaitSyncStatus> {

        final long timeout;

        /**
         * Constructs a WaitSyncQuery using the default timeout value.
         *
         * @since 15.07.06
         */
        public WaitSyncQuery() {
            this(GLSync.DEFAULT_TIMEOUT);
        }

        /**
         * Constructs a WaitSyncQuery with the specified timeout in nanoseconds.
         *
         * @param timeout the length of nanoseconds to wait before returning the
         * GL_TIMEOUT_EXPIRED.
         * @since 15.07.06
         */
        public WaitSyncQuery(final long timeout) {
            this.timeout = timeout;
        }

        @Override
        public GLWaitSyncStatus call() throws Exception {
            final int rVal = GLSync.this.glClientWaitSync.call(GLSync.this.sync, GL32.GL_SYNC_FLUSH_COMMANDS_BIT, this.timeout);
            assert checkGLError() : glErrorMsg("glClientWaitSync(IIL)", GLSync.this.sync, "GL_SYNC_FLUSH_COMMANDS_BIT", this.timeout);
            
            return GLWaitSyncStatus.of(rVal).orElseThrow(GLException::new);
        }
    }
}
