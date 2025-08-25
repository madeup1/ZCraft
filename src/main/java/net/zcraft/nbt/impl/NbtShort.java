package net.zcraft.nbt.impl;

import net.zcraft.nbt.NbtTag;
import net.zcraft.nbt.NbtTagType;
import net.zcraft.network.buffers.ReadBuffer;
import net.zcraft.network.buffers.Types;
import net.zcraft.network.buffers.WriteBuffer;

public class NbtShort extends NbtTag<Short>
{
    public NbtShort(short value)
    {
        super(value);
    }

    @Override
    public void read(ReadBuffer buf)
    {
        this.setValue(buf.read(Types.SHORT));
    }

    @Override
    public void write(WriteBuffer buf)
    {
        buf.write(Types.SHORT, this.getValue());
    }

    @Override
    public NbtTagType type()
    {
        return NbtTagType.Short;
    }

    @Override
    public String toString()
    {
        return this.getValue() + "S";
    }
}
