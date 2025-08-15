package net.zcraft.registry;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@AllArgsConstructor
public class RegistryKey<K, V>
{
    K key;
    @Setter
    V value;
}
