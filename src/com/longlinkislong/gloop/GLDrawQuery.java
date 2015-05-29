/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.longlinkislong.gloop;

import java.util.Objects;
import org.lwjgl.opengl.GL15;
import org.lwjgl.opengl.GL30;

/**
 *
 * @author zmichaels
 */
public class GLDrawQuery extends GLObject {

    private static final int INVALID_QUERY_ID = -1;
    private int queryId = INVALID_QUERY_ID;

    public GLDrawQuery() {
        super();
    }

    public GLDrawQuery(final GLThread thread) {
        super(thread);
    }

    public boolean isValid() {
        return this.queryId != INVALID_QUERY_ID;
    }

    public final void init() {
        new InitTask().glRun(this.getThread());
    }

    private DrawQueryTask lastDrawQuery = null;

    public void drawQuery(final GLDrawTask testDraw, final GLDrawQueryCondition condition) {
        if (this.lastDrawQuery == null
                || this.lastDrawQuery.testDraw != testDraw
                || this.lastDrawQuery.condition != condition) {

            this.lastDrawQuery = new DrawQueryTask(testDraw, condition);
        }

        this.lastDrawQuery.glRun(this.getThread());
    }

    public class DrawQueryTask extends GLTask {

        final GLDrawTask testDraw;
        final GLDrawQueryCondition condition;

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
            this.testDraw.run();
            GL15.glEndQuery(this.condition.value);
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
            this.fullDraw.run();
            GL30.glEndConditionalRender();
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
        }
    }
}
