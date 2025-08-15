package net.zcraft.util.threading;

import lombok.Getter;

import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;

public class QueueLock
{
    @Getter
    private Queue<IQueueAction> actions = new ConcurrentLinkedQueue<>();

    @Getter
    private LockState lockState = LockState.UNLOCKED;


    public QueueLock()
    {

    }

    public void queue(IQueueAction action)
    {
        if (this.getLockState() == LockState.UNLOCKED)
        {
            this.lock();

            action.execute();

            this.unlock();
        }
        else
        {
            actions.add(action);
        }
    }

    public void lock()
    {
        lockState = LockState.LOCKED;
    }

    public void unlock()
    {
        actions.forEach(IQueueAction::execute);

        lockState = LockState.UNLOCKED;

        actions.clear();
    }

    public enum LockState
    {
        LOCKED,
        UNLOCKED
    }
}
