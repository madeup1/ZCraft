package net.zcraft.nbt;

import it.unimi.dsi.fastutil.ints.Int2ObjectMap;
import it.unimi.dsi.fastutil.ints.Int2ObjectOpenHashMap;
import net.zcraft.nbt.impl.*;
import net.zcraft.network.buffers.INetworkType;
import net.zcraft.network.buffers.ReadBuffer;
import net.zcraft.network.buffers.Types;
import net.zcraft.network.buffers.WriteBuffer;

import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.HashMap;

public class Nbt
{
    public static final INetworkType<NbtCompound> SERIALIZER = new INetworkType<NbtCompound>()
    {
        @Override
        public NbtCompound read(ReadBuffer buffer)
        {
            return null;
        }

        @Override
        public void write(WriteBuffer buffer, NbtCompound value)
        {
            NbtCompound compound = Nbt.create(10);
        }
    };

    public static String readString(ReadBuffer buf)
    {
        int len = buf.read(Types.SHORT);
        byte[] data = buf.UNSAFE_read(len);

        return new String(data, StandardCharsets.UTF_8);
    }

    public static void writeString(WriteBuffer buf, String value)
    {
        byte[] data = value.getBytes(StandardCharsets.UTF_8);
        buf.write(Types.SHORT, (short) data.length);
        buf.UNSAFE_write(data);
    }

    private static final Int2ObjectMap<INbtCreator<?>> creators = new Int2ObjectOpenHashMap<>();

    static
    {
        register(1, () -> new NbtByte((byte) 0));
        register(2, () -> new NbtShort((short) 0));
        register(3, () -> new NbtInt(0));
        register(4, () -> new NbtLong(0));
        register(5, () -> new NbtFloat(0));
        register(6, () -> new NbtDouble(0));
        register(7, () -> new NbtByteArray(new byte[0]));
        register(8, () -> new NbtString(""));
        register(9, () -> new NbtList(new ArrayList<>()));
        register(10, () -> new NbtCompound(new HashMap<>()));
        register(11, () -> new NbtIntArray(new int[0]));
    }

    private static <T> void register(int id, INbtCreator<T> creator) {
        creators.put(id, creator);
    }

    @SuppressWarnings("unchecked")
    public static <T> T create(int id)
    {
        return (T) creators.get(id).create();
    }
}
