package net.zcraft.protocol;

import net.zcraft.network.buffers.WriteBuffer;

public interface IServerPacket extends IPacket
{
    void write(WriteBuffer buf);
}
