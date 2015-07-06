/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.longlinkislong.gloop;

import org.lwjgl.opengl.GL11;
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

    /**
     * Constructs a new GLSync object on the specified OpenGL thread.
     *
     * @param thread the OpenGL thread.
     * @since 15.07.06
     */
    public GLSync(final GLThread thread) {
        super(thread);
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

            GLSync.this.sync = GL32.glFenceSync(GL32.GL_SYNC_GPU_COMMANDS_COMPLETE, 0);

            assert GL11.glGetError() == GL11.GL_NO_ERROR : String.format("glFenceSync(GL_SYNC_GPU_COMMANDS_COMPLETE, 0) = %d failed!", GLSync.this.sync);
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

            final int val = GL32.glGetSynci(GLSync.this.sync, GL32.GL_SYNC_STATUS, null);
            final GLSyncStatus status = GLSyncStatus.valueOf(val);

            assert status != null : "Unknown GLSyncStatus: " + val;
            assert GL11.glGetError() == GL11.GL_NO_ERROR : String.format("glGetSynci(%d, GL_SYNC_STATUS, NULL) failed!", GLSync.this.sync);

            return status;
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

            GL32.glDeleteSync(GLSync.this.sync);

            assert GL11.glGetError() == GL11.GL_NO_ERROR : String.format("glDeleteSync(%d) failed!", GLSync.this.sync);

            GLSync.this.sync = INVALID_SYNC;
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
            final int rVal = GL32.glClientWaitSync(GLSync.this.sync, GL32.GL_SYNC_FLUSH_COMMANDS_BIT, this.timeout);
            final GLWaitSyncStatus status = GLWaitSyncStatus.valueOf(rVal);

            assert status != null : "Unknown GLWaitSyncStatus: " + rVal;
            assert GL11.glGetError() == GL11.GL_NO_ERROR : String.format("glClientWaitSync(%d, GL_SYNC_FLUSH_COMMANDS_BIT, %d) failed!", GLSync.this.sync, this.timeout);

            return status;
        }
    }
}
