package net.zcraft.network.buffers;

import lombok.Getter;
import lombok.Setter;

import java.lang.invoke.MethodHandles;
import java.lang.invoke.VarHandle;
import java.nio.ByteOrder;
import java.util.ArrayDeque;
import java.util.Optional;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Predicate;

public class ReadBuffer
{
    private byte[] data;
    @Getter @Setter
    private int pointer = 0;

    public ReadBuffer(byte[] data)
    {
        this.data = data;
    }

    public void reset()
    {
        this.pointer = 0;
    }

    public byte[] UNSAFE_read(int count)
    {
        if (this.ensure(count))
        {
            byte[] dat = new byte[count];

            System.arraycopy(data, pointer, dat, 0, count);
            pointer += count;

            return dat;
        }

        return null;
    }

    public boolean ensure(int count)
    {
        return pointer + count <= data.length;
    }

    public <T> T read(INetworkType<T> type)
    {
        return type.read(this);
    }

    public void until(Predicate<ReadBuffer> predicate, Runnable runnable)
    {
        while (predicate.test(this))
            runnable.run();
    }

    public <T> ArrayDeque<T> readAll(INetworkType<T> type)
    {
        ArrayDeque<T> deque = new ArrayDeque<>();

        while (this.remaining() > 0)
            deque.add(type.read(this));

        return deque;
    }

    public int remaining()
    {
        return data.length - pointer;
    }
}
