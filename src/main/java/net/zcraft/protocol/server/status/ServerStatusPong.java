package net.zcraft.protocol.server.status;

import net.zcraft.network.buffers.Types;
import net.zcraft.network.buffers.WriteBuffer;
import net.zcraft.protocol.IServerPacket;
import net.zcraft.protocol.PacketMode;

public class ServerStatusPong implements IServerPacket
{
    private long time;

    public ServerStatusPong(long time)
    {
        this.time = time;
    }

    @Override
    public void write(WriteBuffer buf)
    {
        buf.write(Types.LONG, time);
    }

    @Override
    public int getPacketId(PacketMode mode)
    {
        return 0x01;
    }
}
