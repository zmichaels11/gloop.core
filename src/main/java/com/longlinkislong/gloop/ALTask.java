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
        } else if (thread.isCurrent()) {
            this.run();
        } else {
            thread.submitALTask(this);
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

    public static ALTask create(final Runnable task) {
        return new ALTask() {
            @Override
            public void run() {
                task.run();
            }
        };
    }
}
