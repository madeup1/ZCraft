package net.zcraft.nbt.impl;

import net.zcraft.nbt.NbtTag;
import net.zcraft.nbt.NbtTagType;
import net.zcraft.network.buffers.ReadBuffer;
import net.zcraft.network.buffers.Types;
import net.zcraft.network.buffers.WriteBuffer;

public class NbtByte extends NbtTag<Byte>
{


    public NbtByte(byte value)
    {
        super(value);
    }

    @Override
    public void read(ReadBuffer buf)
    {
        this.setValue(buf.read(Types.BYTE));
    }

    @Override
    public void write(WriteBuffer buf)
    {
        buf.write(Types.BYTE, this.getValue());
    }

    @Override
    public NbtTagType type()
    {
        return NbtTagType.Byte;
    }

    @Override
    public String toString()
    {
        return this.getValue().toString();
    }
}
