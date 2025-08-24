package net.zcraft.materials;

import com.google.gson.*;
import it.unimi.dsi.fastutil.ints.Int2ObjectMap;
import it.unimi.dsi.fastutil.ints.Int2ObjectOpenHashMap;
import lombok.NonNull;
import lombok.SneakyThrows;
import net.zcraft.network.buffers.INetworkType;
import net.zcraft.network.buffers.ReadBuffer;
import net.zcraft.network.buffers.WriteBuffer;
import net.zcraft.registry.Registry;
import net.zcraft.registry.RegistryKey;
import org.jetbrains.annotations.Nullable;
import org.tinylog.Logger;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayDeque;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

import static net.zcraft.network.buffers.Types.*;
import static net.zcraft.network.buffers.Types.BOOLEAN;
import static net.zcraft.network.buffers.Types.FLOAT;
import static net.zcraft.network.buffers.Types.INT;
import static net.zcraft.network.buffers.Types.STRING;

public class MaterialRegistry implements Registry<String, Material>
{
    private static final File cacheFile = new File("materials.cache");
    private final Map<String, Material> keyToValue;
    private final Int2ObjectMap<Material> idMetaToValue = new Int2ObjectOpenHashMap<>();

    private static final INetworkType<Material> SERIALIZER = new INetworkType<Material>()
    {
        @Override
        public Material read(ReadBuffer buffer)
        {
            int id = buffer.read(INT);
            int metadata = buffer.read(INT);
            String namespace = buffer.read(STRING);
            String displayName = buffer.read(STRING);
            MaterialType type = MaterialType.values()[buffer.read(INT)];
            int stackSize = buffer.read(INT);

            Material material = new Material(id, metadata, namespace, displayName, type, stackSize);

            if (type == MaterialType.BLOCK)
            {
                material.setHardness(buffer.read(FLOAT));
                material.setDiggable(buffer.read(BOOLEAN));
                material.setTransparent(buffer.read(BOOLEAN));
            }

            return material;
        }

        @Override
        public void write(WriteBuffer buffer, Material value)
        {
            buffer.write(INT, value.getId());
            buffer.write(INT, value.getMetadata());
            buffer.write(STRING, value.getNamespace());
            buffer.write(STRING, value.getDisplayName());
            buffer.write(INT, value.getType().ordinal());
            buffer.write(INT, value.getStackSize());

            if (value.isBlock())
            {
                buffer.write(FLOAT, value.getHardness());
                buffer.write(BOOLEAN, value.isDiggable());
                buffer.write(BOOLEAN, value.isTransparent());
            }
        }
    };

    @SneakyThrows
    public MaterialRegistry(File file, Function<JsonObject, Material> consumer)
    {
        keyToValue = new HashMap<>();

        if (!cacheFile.exists() || cacheFile.lastModified() < file.lastModified())
        {
            Logger.debug("Creating cache.");

            JsonElement element = JsonParser.parseReader(new FileReader(file));

            if (!element.isJsonArray())
                throw new RuntimeException("not json array!");

            JsonArray array = element.getAsJsonArray();
            WriteBuffer buffer = new WriteBuffer();

            array.forEach((c) -> {
                Material material = consumer.apply((JsonObject) c);

                if (material != null)
                    this.add(material.getNamespace(), material);

                buffer.write(SERIALIZER, material);
            });

            Files.write(cacheFile.toPath(), buffer.getBytes());
        }
        else
        {
            ReadBuffer buffer = new ReadBuffer(Files.readAllBytes(cacheFile.toPath()));

            ArrayDeque<Material> deque = buffer.readAll(SERIALIZER);

            deque.forEach(c -> {
                this.add(c.getNamespace(), c);
            });
        }
    }

    @Override
    public @Nullable Collection<Material> values()
    {
        return keyToValue.values();
    }

    @Override
    public @Nullable Collection<RegistryKey<String, Material>> entries()
    {
        return keyToValue
                .entrySet()
                .stream()
                .map((c) -> new RegistryKey<String, Material>(c.getKey(), c.getValue()))
                .collect(Collectors.toSet());
    }

    @Override
    public void add(@NonNull String key, @NonNull Material value)
    {
        keyToValue.put(key, value);

        int idKey = (value.getId() << 16) | (value.getMetadata() & 0xFFFF);

        idMetaToValue.put(idKey, value);
    }

    @Override
    public void remove(@NonNull String key)
    {
        keyToValue.remove(key);
    }

    @Override
    public @Nullable Material get(@NonNull String key)
    {
        return keyToValue.get(key);
    }

    public @Nullable Material get(int id, int metadata)
    {
        return idMetaToValue.get((id << 16) | (metadata & 0xFFFF));
    }

    public @Nullable Material get(int id)
    {
        return get(id, 0);
    }

    @Override
    public void set(@NonNull String key, @Nullable Material value)
    {
        keyToValue.put(key, value);
    }
}
