package net.zcraft.nbt.impl;

import net.zcraft.nbt.NbtTag;
import net.zcraft.nbt.NbtTagType;
import net.zcraft.network.buffers.ReadBuffer;
import net.zcraft.network.buffers.Types;
import net.zcraft.network.buffers.WriteBuffer;

public class NbtByteArray extends NbtTag<byte[]>
{
    public NbtByteArray(byte[] values)
    {
        super(values);
    }

    @Override
    public void read(ReadBuffer buf)
    {
        int len = buf.read(Types.INT);

        byte[] data = buf.UNSAFE_read(len);

        this.setValue(data);
    }

    @Override
    public void write(WriteBuffer buf)
    {
        byte[] data = this.getValue();

        buf.write(Types.INT, data.length);
        buf.UNSAFE_write(data);
    }

    @Override
    public NbtTagType type()
    {
        return NbtTagType.ByteArray;
    }

    @Override
    public String toString()
    {
        StringBuilder builder = new StringBuilder("[");

        for (byte b : this.getValue())
            builder.append(b).append(",");

        return builder.append("]").toString();
    }
}
