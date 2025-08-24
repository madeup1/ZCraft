package net.zcraft.nbt.impl;

import net.zcraft.nbt.NbtTag;
import net.zcraft.nbt.NbtTagType;
import net.zcraft.network.buffers.ReadBuffer;
import net.zcraft.network.buffers.Types;
import net.zcraft.network.buffers.WriteBuffer;

import java.nio.charset.StandardCharsets;

public class NbtString extends NbtTag<String>
{
    public NbtString(String value)
    {
        super(value);
    }

    @Override
    public void read(ReadBuffer buf)
    {
        int len = buf.read(Types.SHORT);

        this.setValue(new String(buf.UNSAFE_read(len), StandardCharsets.UTF_8));
    }

    @Override
    public void write(WriteBuffer buf)
    {
        byte[] data = this.getValue().getBytes(StandardCharsets.UTF_8);

        buf.write(Types.SHORT, (short) data.length);
        buf.UNSAFE_write(data);
    }

    @Override
    public NbtTagType type()
    {
        return NbtTagType.String;
    }
}
