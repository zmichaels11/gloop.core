/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.longlinkislong.gloop;

import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author zmichaels
 */
public class GLTaskBuilder {
    private final List<GLTask> tasks = new ArrayList<>();
    
    /**
     * Appends the GLTask to the end of the task buffer
     * @param task the task to add
     * @return self reference
     * @since 15.05.13
     */
    public GLTaskBuilder append(final GLTask task) {
        this.tasks.add(task);
        
        return this;
    }        
    
    /**
     * Joins all the GLTasks in the task buffer into a single GLTask.
     * @return GLTask composed of multiple GLTasks.
     * @since 15.05.13
     */
    public GLTask toGLTask() {
        final GLTask[] arr = this.tasks.toArray(new GLTask[this.tasks.size()]);
        
        return GLTask.join(arr, 0, arr.length);
    }
}
