package net.zcraft.loop;

import lombok.Getter;
import net.zcraft.ZCraftServer;
import net.zcraft.util.loop.ILoopHook;
import net.zcraft.util.threading.QueueLock;

import java.util.LinkedList;

public class LoopManager
{
    @Getter
    private LinkedList<ILoopHook> hooks = new LinkedList<>();

    @Getter
    private QueueLock lock = new QueueLock();

    public LoopManager()
    {

    }

    public void run()
    {
        if (hooks.isEmpty())
            return;

        lock.queue(() -> {
            hooks.forEach(ILoopHook::execute);
        });
    }

    public void addHook(ILoopHook hook)
    {
        lock.queue(() -> {
            if (!hooks.contains(hook))
                hooks.add(hook);
        });
    }

    public void removeHook(ILoopHook hook)
    {
        lock.queue(() -> {
            hooks.remove(hook);
        });
    }
}
