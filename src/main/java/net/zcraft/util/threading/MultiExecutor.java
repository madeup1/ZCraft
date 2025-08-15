package net.zcraft.util.threading;

import java.util.concurrent.*;

public class MultiExecutor
{
    private final ExecutorService service;

    public MultiExecutor(int threads)
    {
        service = Executors.newFixedThreadPool(threads);
    }

    public void submit(Runnable runnable)
    {
        service.submit(runnable);
    }

    public void shutdown()
    {
        service.shutdown();
    }

    public <T> Future<T> submit(Callable<T> task)
    {
        return service.submit(task);
    }

    public void runVirtual(Runnable runnable)
    {
        Thread.ofVirtual().start(runnable);
    }

    public CompletableFuture<Void> runAsync(Runnable runnable)
    {
        return CompletableFuture.runAsync(runnable);
    }
}
