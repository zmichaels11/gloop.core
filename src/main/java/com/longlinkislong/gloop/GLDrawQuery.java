/* 
 * Copyright (c) 2015, longlinkislong.com
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 *
 * * Redistributions of source code must retain the above copyright notice, this
 *   list of conditions and the following disclaimer.
 * * Redistributions in binary form must reproduce the above copyright notice,
 *   this list of conditions and the following disclaimer in the documentation
 *   and/or other materials provided with the distribution.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS"
 * AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE
 * IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE
 * ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT HOLDER OR CONTRIBUTORS BE
 * LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR
 * CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF
 * SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS
 * INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN
 * CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE)
 * ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE
 * POSSIBILITY OF SUCH DAMAGE.
 */
package com.longlinkislong.gloop;

import com.longlinkislong.gloop.dsa.DSADriver;
import com.longlinkislong.gloop.spi.DrawQuery;
import com.longlinkislong.gloop.spi.Driver;
import java.util.Objects;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.Marker;
import org.slf4j.MarkerFactory;

/**
 * An OpenGL object that performs a conditional draw based on the outcome of a
 * test draw.
 *
 * @author zmichaels
 * @since 15.06.18
 */
public class GLDrawQuery extends GLObject {

    private static final Marker GLOOP_MARKER = MarkerFactory.getMarker("GLOOP");
    private static final Logger LOGGER = LoggerFactory.getLogger("GLDrawQuery");

    private transient volatile DrawQuery query;
    private transient volatile String name = "";

    /**
     * Assigns a human-readable name to the GLDrawQuery.
     *
     * @param newName the human-readable name.
     * @since 15.12.18
     */
    public final void setName(final CharSequence newName) {
        GLTask.create(() -> {
            LOGGER.trace(
                    GLOOP_MARKER,
                    "Renamed GLDrawQuery[{}] to GLDrawQuery[{}]!",
                    this.name,
                    newName);

            this.name = newName.toString();
        }).glRun(this.getThread());
    }

    /**
     * Retrieves the name of the GLDrawQuery
     *
     * @return the name.
     * @since 15.12.18
     */
    public final String getName() {
        return this.name;
    }

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
        return query.isValid();
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

    /**
     * Performs a draw query.
     *
     * @param testDraw the draw function to use as a test.
     * @param condition the conditional requirements to pass the test.
     * @since 15.06.18
     */
    public void drawQuery(final GLDrawTask testDraw, final GLDrawQueryCondition condition) {
        new DrawQueryTask(testDraw, condition).glRun(this.getThread());
    }

    /**
     * A GLTask that performs the draw query.
     *
     * @since 15.06.18
     */
    public class DrawQueryTask extends GLTask {

        final GLDrawTask testDraw;
        final GLDrawQueryCondition condition;

        /**
         * Constructs a new DrawQueryTask with the supplied task and passing
         * condition.
         *
         * @param task the test draw task.
         * @param condition the conditions for a pass.
         * @since 15.06.18
         */
        public DrawQueryTask(
                final GLDrawTask task,
                final GLDrawQueryCondition condition) {

            this.testDraw = Objects.requireNonNull(task);
            this.condition = Objects.requireNonNull(condition);
        }

        @Override
        public void run() {
            LOGGER.trace(GLOOP_MARKER, "############### Start DrawQueryTask ###############");
            LOGGER.trace(GLOOP_MARKER, "\tApplying GLDrawQuery[{}]", GLDrawQuery.this.getName());
            LOGGER.trace(GLOOP_MARKER, "\tConditional: {}", this.condition);
            LOGGER.trace(GLOOP_MARKER, "\tDraw Task: {}", this.testDraw);

            if (!GLDrawQuery.this.isValid()) {
                throw new GLException("Invalid GLDrawQuery!");
            }

            final Driver driver = GLTools.getDriverInstance();
            

            driver.drawQueryEnable(condition.value, query);            
            this.testDraw.run();
            driver.drawQueryDisable(condition.value);            
            LOGGER.trace(GLOOP_MARKER, "############### End DrawQueryTask ###############");
        }
    }

    /**
     * Draws a GLDrawTask if the draw query condition passes.
     *
     * @param mode the query mode.
     * @param fullDraw the draw task.
     * @since 15.12.18
     */
    public void conditionalDraw(
            final GLDrawQueryMode mode,
            final GLDrawTask fullDraw) {

        new ConditionalDrawTask(mode, fullDraw).glRun(this.getThread());
    }

    /**
     * A GLTask that draws if the condition passes.
     *
     * @since 15.12.18
     */
    public final class ConditionalDrawTask extends GLTask {

        private final GLDrawTask fullDraw;
        private final GLDrawQueryMode mode;

        /**
         * Constructs a new ConditionalDrawTask.
         *
         * @param mode the query mode.
         * @param task the task.
         * @since 15.12.18
         */
        public ConditionalDrawTask(
                final GLDrawQueryMode mode,
                final GLDrawTask task) {

            this.fullDraw = Objects.requireNonNull(task);
            this.mode = Objects.requireNonNull(mode);
        }

        @Override
        public void run() {
            LOGGER.trace(GLOOP_MARKER, "############### Start GLDrawQuery ConditionTask ###############");
            LOGGER.trace(GLOOP_MARKER, "\tApplying conditional GLDrawQuery[{}]", GLDrawQuery.this.getName());
            LOGGER.trace(GLOOP_MARKER, "\tConditional: {}", this.mode);
            LOGGER.trace(GLOOP_MARKER, "\tDraw task: {}", this.fullDraw);

            if (!GLDrawQuery.this.isValid()) {
                throw new GLException("Invalid GLDrawQuery!");
            }

            final Driver driver = GLTools.getDriverInstance();            

            driver.drawQueryBeginConditionalRender(query, mode.value);            
            this.fullDraw.run();
            driver.drawQueryEndConditionRender();            
            LOGGER.trace(GLOOP_MARKER, "############### End GLDrawQuery Condition Task ###############");
        }
    }

    public class InitTask extends GLTask {

        @Override
        public void run() {
            LOGGER.trace(GLOOP_MARKER, "############### Start GLDrawQuery Init Task ###############");

            if (!GLDrawQuery.this.isValid()) {
                throw new GLException("Invalid GLDrawQuery!");
            }

            query = GLTools.getDriverInstance().drawQueryCreate();            
            
            GLDrawQuery.this.name = "id=" + query.hashCode();

            LOGGER.trace(GLOOP_MARKER, "Initialized GLDrawQuery[{}]", GLDrawQuery.this.name);
            LOGGER.trace(GLOOP_MARKER, "############### End GLDrawQuery Init Task ###############");
        }
    }

    public class DeleteTask extends GLTask {

        @Override
        public void run() {
            LOGGER.trace(GLOOP_MARKER, "############### Start GLDrawQuery Delete Task ###############");
            LOGGER.trace(GLOOP_MARKER, "\tDeleting GLDrawQuery[{}]", GLDrawQuery.this.getName());

            if (!GLDrawQuery.this.isValid()) {
                throw new GLException("Invalid GLDrawQuery!");
            }

            GLTools.getDriverInstance().drawQueryDelete(query);
            query = null;
            LOGGER.trace(GLOOP_MARKER, "############### End GLDrawQuery Delete Task ###############");
        }
    }
}
