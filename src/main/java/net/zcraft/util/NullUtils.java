package net.zcraft.util;

import lombok.NonNull;

import java.util.function.Consumer;

public class NullUtils
{
    public static <T> void ifNull(T o, Runnable runnable)
    {
        if (o == null)
            runnable.run();
    }

    public static <T> void nonNull(T o, Consumer<@NonNull T> consumer)
    {
        if (!(o == null))
            consumer.accept(o);
    }
}
