/* 
 * Copyright (c) 2016, longlinkislong.com
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

/**
 * A task that can be run on the OpenAL thread.
 *
 * @author zmichaels
 * @since 16.03.21
 */
public abstract class ALTask implements Runnable {

    /**
     * Delays the ALTask by at least the specified number of frames. The task is
     * ran on the default OpenAL thread.
     *
     * @param frames the minimum number of frames to delay the task by.
     * @since 16.03.21
     */
    public final void delay(final int frames) {
        this.delay(frames, ALThread.getDefaultInstance());
    }

    /**
     * Delays the ALTask by at least the specified number of frames.
     *
     * @param frames the minimum number of frames to delay the task by.
     * @param thread the OpenAL thread to execute the task on.
     * @since 16.03.21
     */
    public void delay(final int frames, final ALThread thread) {
        thread.submitALTask(new ALTask() {
            @Override
            public void run() {
                if (frames <= 0) {
                    ALTask.this.run();
                } else {
                    ALTask.this.delay(frames - 1, thread);
                }
            }
        });
    }

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
