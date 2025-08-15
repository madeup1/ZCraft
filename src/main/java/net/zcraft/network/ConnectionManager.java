package net.zcraft.network;

import lombok.Getter;
import net.zcraft.util.threading.QueueLock;

import java.util.LinkedList;

public class ConnectionManager
{
    @Getter
    private final LinkedList<ZCraftConnection> connections = new LinkedList<>();
    @Getter
    private final QueueLock lock = new QueueLock();

    public ConnectionManager()
    {

    }

    public void add(ZCraftConnection connection)
    {
        lock.queue(() -> {
            if (!connections.contains(connection))
                connections.add(connection);
        });
    }

    public void remove(ZCraftConnection connection)
    {
        lock.queue(() -> {
            connections.remove(connection);
        });
    }
}
