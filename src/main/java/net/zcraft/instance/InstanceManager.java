package net.zcraft.instance;

import java.util.concurrent.atomic.AtomicInteger;

public class InstanceManager
{
    private final AtomicInteger entityId = new AtomicInteger(0);

    public int getEntityId()
    {
        return entityId.getAndIncrement();
    }
}
