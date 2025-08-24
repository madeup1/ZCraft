package net.zcraft.nbt.impl;

import net.zcraft.nbt.NbtTag;
import net.zcraft.nbt.NbtTagType;
import net.zcraft.network.buffers.ReadBuffer;
import net.zcraft.network.buffers.Types;
import net.zcraft.network.buffers.WriteBuffer;

public class NbtIntArray extends NbtTag<int[]>
{
    public NbtIntArray(int[] values)
    {
        super(values);
    }

    @Override
    public void read(ReadBuffer buf)
    {
        int len = buf.read(Types.INT);
        int[] data = new int[len];

        for (int i = 0; i < len; i++)
            data[i] = buf.read(Types.INT);

        this.setValue(data);
    }

    @Override
    public void write(WriteBuffer buf)
    {
        int[] data = this.getValue();
        buf.write(Types.INT, data.length);

        for (int i = 0; i < data.length; i++)
            buf.write(Types.INT, data[i]);
    }

    @Override
    public NbtTagType type()
    {
        return NbtTagType.IntArray;
    }
}
