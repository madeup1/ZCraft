package net.zcraft.nbt.impl;

import net.zcraft.nbt.NbtTag;
import net.zcraft.nbt.NbtTagType;
import net.zcraft.network.buffers.ReadBuffer;
import net.zcraft.network.buffers.Types;
import net.zcraft.network.buffers.WriteBuffer;

public class NbtFloat extends NbtTag<Float>
{
    public NbtFloat(float value)
    {
        super(value);
    }

    @Override
    public void read(ReadBuffer buf)
    {
        this.setValue(buf.read(Types.FLOAT));
    }

    @Override
    public void write(WriteBuffer buf)
    {
        buf.write(Types.FLOAT, this.getValue());
    }

    @Override
    public NbtTagType type()
    {
        return NbtTagType.Float;
    }
}
