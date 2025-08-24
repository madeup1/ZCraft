package net.zcraft.network.buffers;

import lombok.Getter;

import java.util.ArrayDeque;
import java.util.Queue;

public class WriteBuffer
{
    private final Queue<BufferSegment> segments;
    @Getter
    private int length;

    public WriteBuffer()
    {
        segments = new ArrayDeque<>();
    }

    public <T> void write(INetworkType<T> type, T value)
    {
        type.write(this, value);
    }

    @SafeVarargs
    public final <T> void write(INetworkType<T> type, T... values)
    {
        for (T value : values)
            type.write(this, value);
    }

    public void UNSAFE_write(byte[] data)
    {
        BufferSegment seg = new BufferSegment(data, data.length);

        segments.add(seg);
        length += seg.length;
    }

    public byte[] getBytes()
    {
        byte[] data = new byte[length];
        int offset = 0;

        for (BufferSegment segment : segments)
        {
            System.arraycopy(segment.data, 0, data, offset, segment.length);

            offset += segment.length;
        }

        return data;
    }

    public void clear()
    {
        segments.clear();
        length = 0;
    }

    public record BufferSegment(byte[] data, int length)
    {}
}
