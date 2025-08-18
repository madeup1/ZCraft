package net.zcraft.protocol.client.play;

import net.zcraft.network.ZCraftConnection;
import net.zcraft.network.buffers.ReadBuffer;
import net.zcraft.network.buffers.Types;
import net.zcraft.protocol.IClientPacket;
import net.zcraft.protocol.PacketMode;

public class ClientPlayer implements IClientPacket
{
    private boolean onGround;
    @Override
    public void read(ReadBuffer buf)
    {
        this.onGround = buf.read(Types.BOOLEAN);
    }

    @Override
    public boolean processInstantly()
    {
        return true;
    }

    @Override
    public void process(ZCraftConnection connection)
    {

    }

    @Override
    public int getPacketId(PacketMode mode)
    {
        return 0x03;
    }
}
