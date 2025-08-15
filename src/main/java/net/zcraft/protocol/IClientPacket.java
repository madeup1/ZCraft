package net.zcraft.protocol;

import net.zcraft.network.ZCraftConnection;
import net.zcraft.network.buffers.ReadBuffer;

public interface IClientPacket extends IPacket
{
    void read(ReadBuffer buf);
    boolean processInstantly();
    void process(ZCraftConnection connection);
}
