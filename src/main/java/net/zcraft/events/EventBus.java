package net.zcraft.events;

import org.tinylog.Logger;

import java.util.ArrayDeque;
import java.util.Map;
import java.util.concurrent.*;
import java.util.function.Consumer;

@SuppressWarnings("unchecked")
public class EventBus {
    // Fast map from event class to listeners list
    private final Map<Class<?>, CopyOnWriteArrayList<Consumer<?>>> listeners = new ConcurrentHashMap<>();
    private final ArrayDeque<Event> toProcess = new ArrayDeque<>();

    private final ExecutorService executor = Executors.newSingleThreadExecutor(r -> {
        Thread t = new Thread(r, "Event Thread");
        t.setDaemon(false); // ensure it keeps running
        return t;
    });

    /**
     * Register a listener for an event type
     */
    public <T extends Event> void register(Class<T> type, Consumer<T> listener) {
        listeners.computeIfAbsent(type, __ -> new CopyOnWriteArrayList<>()).add(listener);
    }

    /**
     * Unregister a listener
     */
    public <T extends Event> void unregister(Class<T> type, Consumer<T> listener) {
        var list = listeners.get(type);
        if (list != null) list.remove(listener);
    }

    /**
     * Dispatch an event to all registered listeners
     * doesnt cancel well
     */
    public <T extends Event> void post(T event) {
        executor.submit(() -> {
            var list = listeners.get(event.getClass());
            if (list == null) return;

            for (var listener : list)
            {
                ((Consumer<T>) listener).accept(event);
            }
        });
    }

    /**
     * Dispatch an event to all registered listeners
     * returns true if cancelled
     */
    public <T extends Event> boolean postCancellable(T event) {
        Future<Boolean> future = executor.submit(() -> {
            var list = listeners.get(event.getClass());
            if (list == null) return false;

            for (var listener : list)
            {
                ((Consumer<T>) listener).accept(event);
            }

            if (event instanceof CancellableEvent cEvent)
                return cEvent.isCancelled();
            return false;
        });

        try
        {
            return future.get();
        }
        catch (Exception e)
        {
            Logger.error(e);
        }

        return false;
    }
}
