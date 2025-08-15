package net.zcraft.util.promises;

import java.util.function.Consumer;

public class Promise<T> implements Runnable
{
    private final IReturnable<T> returnable;
    private Consumer<Throwable> error;
    private Consumer<T> consumer;
    private Runnable finalize;
    public Promise(IReturnable<T> returnable)
    {
        this.returnable = returnable;
    }

    public Promise<T> onCatch(Consumer<Throwable> onCatch)
    {
        this.error = onCatch;

        return this;
    }

    public Promise<T> then(Consumer<T> consumer)
    {
        this.consumer = consumer;

        return this;
    }

    public Promise<T> finalize(Runnable finalize)
    {
        this.finalize = finalize;

        return this;
    }

    @Override
    public void run()
    {
        try
        {
            T value = returnable.process();

            if (consumer != null)
                consumer.accept(value);

            if (finalize != null)
                finalize.run();
        }
        catch (Exception e)
        {
            if (error != null)
                error.accept(e);
        }
    }
}
