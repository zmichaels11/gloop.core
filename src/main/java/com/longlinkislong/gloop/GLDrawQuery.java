/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.longlinkislong.gloop;

import static com.longlinkislong.gloop.GLAsserts.checkGLError;
import static com.longlinkislong.gloop.GLAsserts.glErrorMsg;
import java.util.Objects;
import org.lwjgl.opengl.GL15;
import org.lwjgl.opengl.GL30;

/**
 * An OpenGL object that performs a conditional draw based on the outcome of a
 * test draw.
 *
 * @author zmichaels
 * @since 15.06.18
 */
public class GLDrawQuery extends GLObject {

    private static final int INVALID_QUERY_ID = -1;
    private int queryId = INVALID_QUERY_ID;

    /**
     * Constructs a new GLDrawQuery object on the default OpenGL thread.
     *
     * @since 15.06.18
     */
    public GLDrawQuery() {
        super(GLThread.getDefaultInstance());

        this.init();
    }

    /**
     * Constructs a new GLDrawQuery object on the specified OpenGL thread.
     *
     * @param thread the OpenGL thread to create the object on.
     * @since 15.06.18
     */
    public GLDrawQuery(final GLThread thread) {
        super(thread);

        this.init();
    }

    /**
     * Checks if the GLDrawQuery object has been initialized.
     *
     * @return true if the GLDrawQuery object has been initialized and has not
     * yet been deleted.
     * @since 15.06.18
     */
    public boolean isValid() {
        return this.queryId != INVALID_QUERY_ID;
    }

    /**
     * Initializes the GLDrawQuery object on the OpenGL thread associated with
     * the GLDrawQuery object.
     *
     * @since 15.06.18
     */
    public final void init() {
        new InitTask().glRun(this.getThread());
    }

    private DrawQueryTask lastDrawQuery = null;

    /**
     * Performs a draw query.
     * @param testDraw the draw function to use as a test.
     * @param condition the conditional requirements to pass the test.
     * @since 15.06.18
     */
    public void drawQuery(final GLDrawTask testDraw, final GLDrawQueryCondition condition) {
        if (this.lastDrawQuery == null
                || this.lastDrawQuery.testDraw != testDraw
                || this.lastDrawQuery.condition != condition) {

            this.lastDrawQuery = new DrawQueryTask(testDraw, condition);
        }

        this.lastDrawQuery.glRun(this.getThread());
    }

    /**
     * A GLTask that performs the draw query.
     * @since 15.06.18
     */
    public class DrawQueryTask extends GLTask {

        final GLDrawTask testDraw;
        final GLDrawQueryCondition condition;

        /**
         * Constructs a new DrawQueryTask with the supplied task and passing condition.
         * @param task the test draw task.
         * @param condition the conditions for a pass.
         * @since 15.06.18
         */
        public DrawQueryTask(
                final GLDrawTask task,
                final GLDrawQueryCondition condition) {

            Objects.requireNonNull(this.testDraw = task);
            Objects.requireNonNull(this.condition = condition);
        }

        @Override
        public void run() {
            if (!GLDrawQuery.this.isValid()) {
                throw new GLException("Invalid GLDrawQuery!");
            }

            GL15.glBeginQuery(this.condition.value, GLDrawQuery.this.queryId);
            assert checkGLError() : glErrorMsg("glBeginQuery(II)", this.condition, GLDrawQuery.this.queryId);
            
            this.testDraw.run();
            GL15.glEndQuery(this.condition.value);
            assert checkGLError() : glErrorMsg("glEndQuery(I)", this.condition);
        }
    }

    private ConditionalDrawTask lastConditionalDraw = null;

    public void conditionalDraw(final GLDrawQueryMode mode, final GLDrawTask fullDraw) {
        if (this.lastConditionalDraw == null
                || this.lastConditionalDraw.mode != mode
                || this.lastConditionalDraw.fullDraw != fullDraw) {

            this.lastConditionalDraw = new ConditionalDrawTask(mode, fullDraw);
        }

        this.lastConditionalDraw.glRun(this.getThread());
    }

    public class ConditionalDrawTask extends GLTask {

        final GLDrawTask fullDraw;
        final GLDrawQueryMode mode;

        public ConditionalDrawTask(
                final GLDrawQueryMode mode,
                final GLDrawTask task) {

            Objects.requireNonNull(this.fullDraw = task);
            Objects.requireNonNull(this.mode = mode);
        }

        @Override
        public void run() {
            if (!GLDrawQuery.this.isValid()) {
                throw new GLException("Invalid GLDrawQuery!");
            }

            GL30.glBeginConditionalRender(GLDrawQuery.this.queryId, this.mode.value);
            assert checkGLError() : glErrorMsg("glBeginConditionalRender(II)", GLDrawQuery.this.queryId, this.mode);
            
            this.fullDraw.run();
            GL30.glEndConditionalRender();
            assert checkGLError() : glErrorMsg("glEndConditionalRender(void)");
        }
    }

    public class InitTask extends GLTask {

        @Override
        public void run() {
            if (!GLDrawQuery.this.isValid()) {
                throw new GLException("Invalid GLDrawQuery!");
            }

            GLDrawQuery.this.queryId = GL15.glGenQueries();
        }
    }

    public class DeleteTask extends GLTask {

        @Override
        public void run() {
            if (!GLDrawQuery.this.isValid()) {
                throw new GLException("Invalid GLDrawQuery!");
            }

            GL15.glDeleteQueries(GLDrawQuery.this.queryId);
            assert checkGLError() : glErrorMsg("glDeleteQueries(I)", GLDrawQuery.this.queryId);
        }
    }
}
