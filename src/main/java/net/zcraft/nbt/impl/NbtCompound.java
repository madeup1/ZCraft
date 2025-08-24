package net.zcraft.nbt.impl;

import net.zcraft.nbt.Nbt;
import net.zcraft.nbt.NbtTag;
import net.zcraft.nbt.NbtTagType;
import net.zcraft.network.buffers.ReadBuffer;
import net.zcraft.network.buffers.Types;
import net.zcraft.network.buffers.WriteBuffer;

import java.util.HashMap;
import java.util.function.Consumer;

public class NbtCompound extends NbtTag<HashMap<String, NbtTag<?>>>
{
    public NbtCompound(HashMap<String, NbtTag<?>> map)
    {
        super(map);
    }

    public void put(String key, NbtTag<?> value)
    {
        this.getValue().put(key, value);
    }

    @SuppressWarnings("unchecked")
    public <T extends NbtTag<?>> T get(String key)
    {
        return (T) this.getValue().get(key);
    }

    public void ifPresent(String key, Consumer<NbtTag<?>> consumer)
    {
        if (this.getValue().containsKey(key))
            consumer.accept(this.get(key));
    }

    public void ifAbsent(String key, Consumer<NbtCompound> consumer)
    {
        if (!this.getValue().containsKey(key))
            consumer.accept(this);
    }

    public boolean containsKey(String key)
    {
        return this.getValue().containsKey(key);
    }

    @Override
    public void read(ReadBuffer buf)
    {
        while (buf.remaining() > 0)
        {
            NbtTagType type = NbtTagType.read(buf);

            if (type == NbtTagType.End)
                break;

            NbtTag<?> tag = Nbt.create(type.ordinal());
            String namespace = Nbt.readString(buf);

            tag.read(buf);

            this.put(namespace, tag);
        }
    }

    @Override
    public void write(WriteBuffer buf)
    {
        this.getValue().forEach((namespace, tag) -> {
            buf.write(Types.BYTE, tag.type().toByte());
            Nbt.writeString(buf, namespace);
            tag.write(buf);
        });

        buf.write(Types.BYTE, NbtTagType.End.toByte());
    }

    @Override
    public NbtTagType type()
    {
        return NbtTagType.Compound;
    }
}
