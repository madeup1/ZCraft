package net.zcraft.protocol.server.status;

import net.zcraft.network.buffers.Types;
import net.zcraft.network.buffers.WriteBuffer;
import net.zcraft.protocol.IServerPacket;
import net.zcraft.protocol.PacketMode;
import net.zcraft.util.status.ServerStatus;
import org.tinylog.Logger;

public class ServerStatusResponse implements IServerPacket
{
    private ServerStatus status;

    public ServerStatusResponse(ServerStatus status)
    {
        this.status = status;
    }

    @Override
    public void write(WriteBuffer buf)
    {
        buf.write(Types.STRING, status.toJson());
    }

    @Override
    public int getPacketId(PacketMode mode)
    {
        return 0x00;
    }
}
