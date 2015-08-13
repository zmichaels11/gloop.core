/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.longlinkislong.gloop;

/**
 *
 * @author zmichaels
 */
public abstract class CLTask implements Runnable {

    @Override
    public abstract void run();

    public final void clRun(final CLThread thread) {
        if (thread == null) {
            this.clRun(CLThread.getDefaultInstance());
        } else {
            if (thread.isCurrent()) {
                this.run();
            } else {
                thread.submitCLTask(this);
            }
        }
    }

    public final void clRun() {
        final CLThread thread = CLThread.getDefaultInstance();

        if (thread.isCurrent()) {
            this.run();
        } else {
            thread.submitCLTask(this);
        }
    }

    public final CLTask andThen(final CLTask next) {
        return new CLTask() {
            @Override
            public void run() {
                CLTask.this.run();
                next.run();
            }
        };
    }

    public static CLTask join(final CLTask[] tasks, final int offset, final int length) {
        return new CLTask() {
            @Override
            public void run() {
                for (int i = 0; i < length; i++) {
                    tasks[offset + i].run();
                }
            }
        };
    }

    public static CLTask join(final CLTask... tasks) {
        return join(tasks, 0, tasks.length);
    }

    public static CLTask create(final Runnable run) {
        return new CLTask() {
            @Override
            public void run() {
                run.run();
            }
        };
    }

    public static final CLTask NO_OP = new CLTask() {
        @Override
        public void run() {
        }
    };
}
