package net.zcraft.util;

import java.util.concurrent.atomic.AtomicInteger;

public class WindowUtils
{
    private static AtomicInteger ID_COUNTER = new AtomicInteger();

    public static byte nextWindowId()
    {
        return (byte) ID_COUNTER.updateAndGet(c -> c + 1 >= 128 ? 1 : c + 1);
    }
}
