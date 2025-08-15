package net.zcraft.events;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.function.Consumer;

@SuppressWarnings("unchecked")
public class EventBus {
    // Fast map from event class to listeners list
    private final Map<Class<?>, CopyOnWriteArrayList<Consumer<?>>> listeners = new ConcurrentHashMap<>();

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
     * returns true if cancelled
     */
    public <T extends Event> boolean post(T event) {
        var list = listeners.get(event.getClass());
        if (list == null) return false;

        for (var listener : list)
        {
            ((Consumer<T>) listener).accept(event);
        }

        if (event instanceof CancellableEvent cEvent)
            return cEvent.isCancelled();
        return false;
    }
}
