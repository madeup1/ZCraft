package net.zcraft.protocol.client.status;

import net.zcraft.ZCraftServer;
import net.zcraft.network.ZCraftConnection;
import net.zcraft.network.buffers.ReadBuffer;
import net.zcraft.protocol.IClientPacket;
import net.zcraft.protocol.PacketMode;
import net.zcraft.protocol.server.status.ServerStatusResponse;

public class ClientStatusRequest implements IClientPacket
{
    @Override
    public void read(ReadBuffer buf)
    {

    }

    @Override
    public boolean processInstantly()
    {
        return true;
    }

    @Override
    public void process(ZCraftConnection connection)
    {
        connection.sendPacket(new ServerStatusResponse(ZCraftServer.getMotdHandler().apply(connection)));
    }

    @Override
    public int getPacketId(PacketMode mode)
    {
        return 0x00;
    }
}
