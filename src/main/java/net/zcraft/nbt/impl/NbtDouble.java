package net.zcraft.nbt.impl;

import net.zcraft.nbt.NbtTag;
import net.zcraft.nbt.NbtTagType;
import net.zcraft.network.buffers.ReadBuffer;
import net.zcraft.network.buffers.Types;
import net.zcraft.network.buffers.WriteBuffer;

public class NbtDouble extends NbtTag<Double>
{
    public NbtDouble(double value)
    {
        super(value);
    }

    @Override
    public void read(ReadBuffer buf)
    {
        this.setValue(buf.read(Types.DOUBLE));
    }

    @Override
    public void write(WriteBuffer buf)
    {
        buf.write(Types.DOUBLE, this.getValue());
    }

    @Override
    public NbtTagType type()
    {
        return NbtTagType.Double;
    }

    @Override
    public String toString()
    {
        return getValue() + "D";
    }
}
