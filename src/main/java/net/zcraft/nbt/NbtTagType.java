package net.zcraft.nbt;

import net.zcraft.network.buffers.ReadBuffer;
import net.zcraft.network.buffers.Types;

import java.util.function.Consumer;

public enum NbtTagType
{
    End,
    Byte,
    Short,
    Int,
    Long,
    Float,
    Double,
    ByteArray,
    String,
    List,
    Compound,
    IntArray;

    public byte toByte()
    {
        return (byte) this.ordinal();
    }

    public static NbtTagType read(ReadBuffer buffer)
    {
        return values()[buffer.read(Types.BYTE)];
    }
}
