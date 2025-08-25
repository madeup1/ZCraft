package net.zcraft.nbt.impl;

import net.zcraft.nbt.NbtTag;
import net.zcraft.nbt.NbtTagType;
import net.zcraft.network.buffers.ReadBuffer;
import net.zcraft.network.buffers.Types;
import net.zcraft.network.buffers.WriteBuffer;

public class NbtInt extends NbtTag<Integer>
{
    public NbtInt(int value)
    {
        super(value);
    }

    @Override
    public void read(ReadBuffer buf)
    {
        this.setValue(buf.read(Types.INT));
    }

    @Override
    public void write(WriteBuffer buf)
    {
        buf.write(Types.INT, this.getValue());
    }

    @Override
    public NbtTagType type()
    {
        return NbtTagType.Int;
    }

    @Override
    public String toString()
    {
        return getValue().toString();
    }
}
