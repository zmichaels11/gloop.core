package com.longlinkislong.gloop;

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
/**
 * A task that should run on a GLThread. Tasks do not return values.
 * @author zmichaels
 */
public abstract class GLTask implements Runnable {

    /**
     * Runs the GLTask now without checking for thread safety.
     * @since 15.05.27
     */
    @Override
    public abstract void run();

    /**
     * Runs the task on the supplied thread. If the thread supplied is the
     * current thread, the call will run instantly.
     *
     * @param thread the thread to run the call on. If null is supplied, the
     * default OpenGL thread will be used.
     * @since 15.05.12
     */
    public final void glRun(final GLThread thread) {
        if (thread == null) {
            this.glRun(GLThread.getDefaultInstance());
        } else {
            if (thread.isCurrent()) {
                this.run();
            } else {
                thread.submitGLTask(this);
            }
        }
    }

    /**
     * Runs the task on the default GLThread. If the current thread is the
     * default GLThread, the task will run instantly. Otherwise it will be
     * deferred.
     * 
     * @since 15.05.12
     */
    public final void glRun() {
        final GLThread thread = GLThread.getDefaultInstance();

        if (thread.isCurrent()) {
            this.run();
        } else {
            thread.submitGLTask(this);
        }
    }        

    /**
     * Combines this GLTask and another GLTask into a single GLTask. The order
     * of operation is first this task and then the other.
     *
     * @param next the task to run after this task.
     * @return the combined task
     * @since 15.05.12
     */
    public GLTask andThen(final GLTask next) {
        return new GLTask() {
            @Override
            public void run() {
                GLTask.this.run();
                next.run();
            }
        };
    }

    /**
     * Joins multiple tasks together into a single task.
     *
     * @param tasks the list of tasks.
     * @return the joined task
     * @since 15.05.15
     */
    public static GLTask join(final GLTask... tasks) {
        return join(tasks, 0, tasks.length);
    }

    /**
     * Runs a conditional GLTask based on the results of a GLQuery.
     *
     * @param <T> the result of the query.
     * @param query the query to run
     * @param expected the expected value of the query
     * @param pass the task to run if the actual equals the expected
     * @param fail the task to run if the actual does not equal the expected
     * @return the task
     * @since 15.05.12
     */
    public static <T> GLTask ifElse(
            final GLQuery<T> query, final T expected,
            final GLTask pass, final GLTask fail) {

        return new GLTask() {
            @Override
            public void run() {

                try {
                    final T actual = query.call();

                    if (actual.equals(expected)) {
                        pass.run();
                    } else {
                        fail.run();
                    }
                } catch (Exception ex) {
                    throw new GLException("Error executing ifElse task!", ex);
                }
            }
        };
    }

    /**
     * Joins multiple tasks from an array of tasks
     *
     * @param tasks the array of tasks
     * @param offset the offset of the first task
     * @param length the number of sequential tasks to join
     * @return the joined task.
     * @since 15.05.12
     */
    public static GLTask join(final GLTask[] tasks, final int offset, final int length) {
        return new GLTask() {
            @Override
            public void run() {
                for (int i = 0; i < length; i++) {
                    tasks[offset + i].run();
                }
            }
        };
    }

    /**
     * Creates a new GLTask from a runnable
     *
     * @param task the runnable to wrap
     * @return the task
     * @since 15.05.12
     */
    public static GLTask create(final Runnable task) {
        return new GLTask() {
            @Override
            public void run() {
                task.run();
            }
        };
    }

    /**
     * A task that does nothing
     *
     * @since 15.05.12
     */
    public static final GLTask NO_OP = new GLTask() {
        @Override
        public void run() {
        }
    };
}
