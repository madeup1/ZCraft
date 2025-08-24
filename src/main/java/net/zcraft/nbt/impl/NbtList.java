package net.zcraft.nbt.impl;

import net.zcraft.nbt.Nbt;
import net.zcraft.nbt.NbtTag;
import net.zcraft.nbt.NbtTagType;
import net.zcraft.network.buffers.ReadBuffer;
import net.zcraft.network.buffers.Types;
import net.zcraft.network.buffers.WriteBuffer;

import java.util.ArrayList;
import java.util.List;

public class NbtList extends NbtTag<List<NbtTag<?>>>
{
    public NbtList(List<NbtTag<?>> list)
    {
        super(list);
    }

    @Override
    public void read(ReadBuffer buf)
    {
        NbtTagType type = NbtTagType.read(buf);
        int len = buf.read(Types.INT);

        this.setValue(new ArrayList<>(len));

        for (int i = 0; i < len; i++)
        {
            NbtTag<?> tag = Nbt.create(type.ordinal());
            tag.read(buf);

            this.getValue().add(tag);
        }
    }

    @Override
    public void write(WriteBuffer buf)
    {
        if (this.getValue().isEmpty())
            return;

        List<NbtTag<?>> tags = this.getValue();
        NbtTagType type = tags.getFirst().type();

        buf.write(Types.BYTE, type.toByte());
        buf.write(Types.INT, tags.size());

        tags.forEach(c -> {
            c.write(buf);
        });
    }

    @Override
    public NbtTagType type()
    {
        return NbtTagType.List;
    }
}
