package net.zcraft.network.buffers;

public interface INetworkType<T>
{
    T read(ReadBuffer buffer);
    void write(WriteBuffer buffer, T value);
}
