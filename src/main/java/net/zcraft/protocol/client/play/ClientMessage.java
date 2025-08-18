package net.zcraft.protocol.client.play;

import lombok.Getter;
import net.zcraft.network.ZCraftConnection;
import net.zcraft.network.buffers.ReadBuffer;
import net.zcraft.network.buffers.Types;
import net.zcraft.protocol.IClientPacket;
import net.zcraft.protocol.PacketMode;

@Getter
public class ClientMessage implements IClientPacket
{
    private String message;
    @Override
    public void read(ReadBuffer buf)
    {
        this.message = buf.read(Types.STRING);
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
        return 0x01;
    }
}
