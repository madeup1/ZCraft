package net.zcraft.registry;

import lombok.NonNull;
import net.zcraft.util.NullUtils;
import org.jetbrains.annotations.Nullable;

import java.util.Collection;
import java.util.function.Consumer;

public interface Registry<K, T>
{
    @Nullable Collection<T> values();
    @Nullable Collection<RegistryKey<K, T>> entries();
    void add(@NonNull K key, @NonNull T value);
    void remove(@NonNull K key);
    @Nullable T get(@NonNull K key);
    void set(@NonNull K key, @Nullable T value);


    default void forEach(Consumer<RegistryKey<K, T>> consumer)
    {
        NullUtils.nonNull(this.entries(), (values) -> {
            values.forEach(consumer);
        });
    }
}
