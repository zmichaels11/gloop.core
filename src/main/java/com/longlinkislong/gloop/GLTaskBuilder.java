/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
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

    private final List<GLTask> tasks = new ArrayList<>();

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
