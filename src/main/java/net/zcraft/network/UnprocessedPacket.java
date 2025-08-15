package net.zcraft.network;

import net.zcraft.protocol.IClientPacket;
import net.zcraft.protocol.IPacket;

public record UnprocessedPacket(ZCraftConnection conn, IClientPacket packet)
{
    public void process()
    {
        packet.process(conn);
    }
}
