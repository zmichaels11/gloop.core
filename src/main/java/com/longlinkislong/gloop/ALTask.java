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
public abstract class ALTask implements Runnable {

    @Override
    public abstract void run();

    public final void alRun(final ALThread thread) {
        if (thread == null) {
            this.alRun(ALThread.getDefaultInstance());
        } else {
            if (thread.isCurrent()) {
                this.run();
            } else {
                thread.submitALTask(this);
            }
        }
    }

    public final void alRun() {
        final ALThread thread = ALThread.getDefaultInstance();

        if (thread.isCurrent()) {
            this.run();
        } else {
            thread.submitALTask(this);
        }
    }

    public final ALTask andThen(final ALTask next) {
        return new ALTask() {
            @Override
            public void run() {
                ALTask.this.run();
                next.run();
            }
        };
    }

    public static ALTask join(final ALTask[] tasks, final int offset, final int length) {
        return new ALTask() {
            @Override
            public void run() {
                for (int i = 0; i < length; i++) {
                    tasks[offset + i].run();
                }
            }
        };
    }

    public static ALTask join(final ALTask... tasks) {
        return join(tasks, 0, tasks.length);
    }

    public static ALTask create(final Runnable run) {
        return new ALTask() {
            @Override
            public void run() {
                run.run();
            }
        };
    }

    public static final ALTask NO_OP = new ALTask() {
        @Override
        public void run() {
        }
    };
}
