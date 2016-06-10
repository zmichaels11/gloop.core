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

import java.util.ArrayList;
import java.util.List;

/**
 * Constructs a task from a series of smaller tasks.
 *
 * @author zmichaels
 * @since 15.05.27
 */
public class GLTaskBuilder {

    private final List<GLTask> tasks = new ArrayList<>(0);

    /**
     * Appends the GLTask to the end of the task buffer
     *
     * @param task the task to add
     * @return self reference
     * @since 15.05.13
     */
    public GLTaskBuilder append(final GLTask task) {
        this.tasks.add(task);

        return this;
    }
    
    /**
     * Appends a runnable to the task buffer.
     * @param task the runnable to add.
     * @return self reference
     * @since 15.05.27
     */
    public GLTaskBuilder append(final Runnable task) {
        this.tasks.add(GLTask.create(task));
        
        return this;
    }
    
    /**
     * Appends a query to the task buffer.
     * @param query the query to run
     * @return self reference
     * @since 15.05.27
     */
    public GLTaskBuilder append(final GLQuery<?> query) {
        this.tasks.add(GLTask.ifElse(query, null, GLTask.NO_OP, GLTask.NO_OP));
        
        return this;
    }

    /**
     * Joins all the GLTasks in the task buffer into a single GLTask.
     *
     * @return GLTask composed of multiple GLTasks.
     * @since 15.05.13
     */
    public GLTask toGLTask() {
        final GLTask[] arr = this.tasks.toArray(new GLTask[this.tasks.size()]);

        return GLTask.join(arr, 0, arr.length);
    }
}
