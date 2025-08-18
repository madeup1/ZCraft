package net.zcraft.instance;

import lombok.Getter;
import lombok.Setter;

import java.util.LinkedList;
import java.util.concurrent.atomic.AtomicInteger;


public class InstanceManager
{
    @Getter private final LinkedList<Instance> instances = new LinkedList<>();
    @Getter private Instance defaultInstance;
    private final AtomicInteger entityId = new AtomicInteger(0);

    public int getEntityId()
    {
        return entityId.getAndIncrement();
    }

    public void setDefaultInstance(Instance instance)
    {
        if (!instances.contains(instance))
            instances.add(instance);
        defaultInstance = instance;
    }
}
