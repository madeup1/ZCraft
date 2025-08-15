import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import net.zcraft.ZCraftServer;
import net.zcraft.materials.Material;
import net.zcraft.materials.MaterialType;
import net.zcraft.network.buffers.INetworkType;
import net.zcraft.network.buffers.ReadBuffer;
import net.zcraft.network.buffers.WriteBuffer;
import net.zcraft.util.NanoTimer;
import org.junit.Test;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayDeque;
import java.util.HashMap;

import static net.zcraft.network.buffers.Types.*;

public class BufferLoadTest
{
    private static INetworkType<Material> SERIALIZER = new INetworkType<Material>()
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

    private static final File file = new File("materials.json");

    @Test
    public void writeData() throws IOException
    {
        NanoTimer timer = new NanoTimer();

        JsonElement element = JsonParser.parseReader(new FileReader(file));

        if (!element.isJsonArray())
            throw new RuntimeException("not json array!");

        JsonArray array = element.getAsJsonArray();

        WriteBuffer buffer = new WriteBuffer();

        array.forEach((c) -> {
            Material material = new Material(c.getAsJsonObject());

            this.add(material.getNamespace(), material);

            buffer.write(SERIALIZER, material);
        });

        Files.write(Path.of("materials.cache"), buffer.getBytes());

        System.out.println("Creating cache took " + timer.elapsedMs() + "ms");
    }

    private final HashMap<String, Material> materials = new HashMap<>();

    public void add(String name, Material mat)
    {
        materials.put(name, mat);
    }

    // ok so this is SIGNIFICANTLY FASTER

    @Test
    public void read() throws IOException
    {
        // System.out.println("read too");

        materials.clear();

        ReadBuffer buffer = new ReadBuffer(Files.readAllBytes(Path.of("materials.cache")));

        NanoTimer timer = new NanoTimer();

        buffer.until((b) -> b.remaining() > 0, () -> {
            Material material = buffer.read(SERIALIZER);

            this.add(material.getNamespace(), material);
        });

        System.out.println("Load (until) took " + timer.elapsedMs() + "ms");

        buffer.setPointer(0);
        timer.reset();

        ArrayDeque<Material> deque = buffer.readAll(SERIALIZER);

        deque.forEach(c -> {
            this.add(c.getNamespace(), c);
        });

        System.out.println("Load (deque) took " + timer.elapsedMs() + "ms");
    }
}
