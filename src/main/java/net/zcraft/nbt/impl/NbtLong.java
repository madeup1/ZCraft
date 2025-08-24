package net.zcraft.nbt.impl;

import net.zcraft.nbt.NbtTag;
import net.zcraft.nbt.NbtTagType;
import net.zcraft.network.buffers.ReadBuffer;
import net.zcraft.network.buffers.Types;
import net.zcraft.network.buffers.WriteBuffer;

public class NbtLong extends NbtTag<Long>
{
    public NbtLong(long value)
    {
        super(value);
    }

    @Override
    public void read(ReadBuffer buf)
    {
        this.setValue(buf.read(Types.LONG));
    }

    @Override
    public void write(WriteBuffer buf)
    {
        buf.write(Types.LONG, this.getValue());
    }

    @Override
    public NbtTagType type()
    {
        return NbtTagType.Long;
    }
}
