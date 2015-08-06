/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.longlinkislong.gloop;

import java.nio.IntBuffer;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.concurrent.Callable;
import java.util.concurrent.CancellationException;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Future;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;
import java.util.stream.Collectors;
import org.lwjgl.BufferUtils;
import org.lwjgl.PointerBuffer;
import org.lwjgl.opencl.CL10;
import org.lwjgl.opencl.CLCreateContextCallback;
import org.lwjgl.opencl.CLDevice;
import org.lwjgl.opencl.CLPlatform;
import static org.lwjgl.system.MemoryUtil.NULL;
import static org.lwjgl.system.MemoryUtil.memDecodeUTF8;

/**
 *
 * @author zmichaels
 */
public final class CLThread implements ExecutorService {

    private static final Map<Thread, CLThread> THREAD_MAP = new HashMap<>();
    private static final boolean DEBUG;
    long device;
    long context;
    final IntBuffer errcode_ret = BufferUtils.createIntBuffer(1);
    long commandQueue;

    static {
        DEBUG = Boolean.getBoolean("debug") && !System.getProperty("debug.exclude", "").contains("clthread");
    }

    private static final class Holder {

        private static final CLThread INSTANCE = new CLThread();
    }

    public static CLThread getDefaultInstance() {
        return Holder.INSTANCE;
    }

    public static Optional<CLThread> getCurrent() {
        return Optional.ofNullable(THREAD_MAP.get(Thread.currentThread()));
    }

    public static CLThread getAny() {
        return CLThread.getCurrent().orElseGet(CLThread::getDefaultInstance);
    }

    private final ExecutorService internalExecutor;
    private boolean shouldHaltScheduledTasks = false;
    private Thread internalThread = null;

    public CLThread() {
        this(new ThreadPoolExecutor(
                1, 1, 0L, TimeUnit.MILLISECONDS, new LinkedBlockingQueue<>()) {
                    @Override
                    protected void afterExecute(final Runnable task, Throwable ex) {
                        super.afterExecute(task, ex);

                        if (task != null && task instanceof Future<?>) {
                            try {
                                final Future<?> future = (Future<?>) task;

                                if (future.isDone()) {
                                    future.get();
                                }
                            } catch (CancellationException ce) {
                                ex = ce;
                            } catch (ExecutionException ee) {
                                ex = ee.getCause();
                            } catch (InterruptedException ie) {
                                Thread.currentThread().interrupt();
                            }
                        }

                        if (ex != null) {
                            ex.printStackTrace();
                        }
                    }
                });
    }

    public CLThread(final ExecutorService service) {
        this.internalExecutor = Objects.requireNonNull(service);
        this.submitCLTask(new InitTask());
        
    }

    private class InitTask extends CLTask {

        @Override
        public void run() {
            CLThread.this.internalThread = Thread.currentThread();

            final String oldName = CLThread.this.internalThread.getName();

            if (oldName.contains("OpenGL")) {
                CLThread.this.internalThread.setName(oldName.replace("OpenGL", "OpenGL+OpenCL"));
            } else if (oldName.contains("OpenAL")) {
                CLThread.this.internalThread.setName(oldName.replace("OpenAL", "OpenAL+OpenCL"));
            } else {
                CLThread.this.internalThread.setName("OpenCL Thread: " + CLThread.this.internalThread.getId());
            }

            final CLPlatform platform = CLPlatform.getPlatforms().get(0);
            final PointerBuffer ctxProps = BufferUtils.createPointerBuffer(3);
            final List<CLDevice> devices = platform.getDevices(CL10.CL_DEVICE_TYPE_DEFAULT);

            ctxProps.put(CL10.CL_CONTEXT_PLATFORM).put(platform).put(0).flip();

            final CLCreateContextCallback callback = new CLCreateContextCallback() {

                @Override
                public void invoke(long errinfo, long private_info, long cb, long user_data) {
                    System.err.println("[LWJGL] cl_create_context_callback");
                    System.err.println("\tInfo: " + memDecodeUTF8(errinfo));
                }
            };

            final IntBuffer errcode_ret = CLThread.this.errcode_ret;
            final long device = devices.get(0).getPointer();
            final long context = CL10.clCreateContext(ctxProps, device, callback, NULL, errcode_ret);

            CLThread.this.device = device;
            CLThread.this.context = context;
            CLThread.this.commandQueue = CL10.clCreateCommandQueue(context, device, CL10.CL_QUEUE_PROFILING_ENABLE, errcode_ret);

            THREAD_MAP.put(CLThread.this.internalThread, CLThread.this);
        }
    }

    public void submitCLTask(final CLTask task) {
        this.internalExecutor.submit(task);
    }

    public void scheduleCLTask(final CLTask task) {
        this.internalExecutor.execute(new CLTask() {
            @Override
            public void run() {
                task.run();

                if (!CLThread.this.shouldHaltScheduledTasks) {
                    CLThread.this.internalExecutor.execute(this);
                }
            }
        });
    }

    public void submitCLTask(final CLTask task, final long delay) {
        this.internalExecutor.execute(new CLTask() {
            long count = delay;

            @Override
            public void run() {
                if (count <= 0) {
                    task.run();
                } else {
                    count--;

                    if (!CLThread.this.shouldHaltScheduledTasks) {
                        CLThread.this.internalExecutor.execute(this);
                    }
                }
            }
        });
    }

    public <ReturnType> CLFuture<ReturnType> submitCLQuery(final CLQuery<ReturnType> query) {
        return new CLFuture<>(this.internalExecutor.submit(query));
    }

    public boolean isCurrent() {
        return Thread.currentThread() == this.internalThread;
    }

    @Override
    public void shutdown() {
        this.shouldHaltScheduledTasks = true;
        this.internalExecutor.shutdown();
    }

    @Override
    public List<Runnable> shutdownNow() {
        return this.internalExecutor.shutdownNow();
    }

    @Override
    public boolean isShutdown() {
        return this.internalExecutor.isShutdown();
    }

    @Override
    public boolean isTerminated() {
        return this.internalExecutor.isTerminated();
    }

    @Override
    public boolean awaitTermination(long timeout, TimeUnit unit) throws InterruptedException {
        return this.internalExecutor.awaitTermination(timeout, unit);
    }

    @Override
    public <T> Future<T> submit(Callable<T> task) {
        return this.submitCLQuery(CLQuery.create(task));
    }

    @Override
    public <T> Future<T> submit(Runnable task, T result) {
        return this.submitCLQuery(CLQuery.create(task, () -> {
            return result;
        }));
    }

    @Override
    public Future<?> submit(Runnable task) {
        return this.submitCLQuery(CLQuery.create(task, () -> {
            return Boolean.TRUE;
        }));
    }

    @Override
    public <T> List<Future<T>> invokeAll(Collection<? extends Callable<T>> tasks) throws InterruptedException {
        return tasks.stream().map(this::submit).collect(Collectors.toList());
    }

    @Override
    public <T> List<Future<T>> invokeAll(Collection<? extends Callable<T>> tasks, long timeout, TimeUnit unit) throws InterruptedException {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public <T> T invokeAny(Collection<? extends Callable<T>> tasks) throws InterruptedException, ExecutionException {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public <T> T invokeAny(Collection<? extends Callable<T>> tasks, long timeout, TimeUnit unit) throws InterruptedException, ExecutionException, TimeoutException {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public void execute(Runnable command) {
        this.submitCLTask(CLTask.create(command));
    }

    public void finish() {
        new FinishTask().clRun(this);
    }

    public class FinishTask extends CLTask {

        @Override
        public void run() {
            CL10.clFinish(CLThread.this.commandQueue);
        }
    }
    
    public class FlushTask extends CLTask {
        @Override
        public void run() {
            CL10.clFlush(CLThread.this.commandQueue);
        }
    }
}
