package net.zcraft.protocol.client.play;

import lombok.Getter;
import net.zcraft.network.ZCraftConnection;
import net.zcraft.network.buffers.ReadBuffer;
import net.zcraft.network.buffers.Types;
import net.zcraft.protocol.IClientPacket;
import net.zcraft.protocol.PacketMode;
import org.tinylog.Logger;

@Getter
public class ClientPluginMessage implements IClientPacket
{
    private String channel;
    private byte[] data;
    @Override
    public void read(ReadBuffer buf)
    {
        this.channel = buf.read(Types.STRING);
        this.data = buf.UNSAFE_read(buf.remaining());
    }

    @Override
    public boolean processInstantly()
    {
        return false;
    }

    @Override
    public void process(ZCraftConnection connection)
    {
        Logger.debug(channel + " | message received");
    }

    @Override
    public int getPacketId(PacketMode mode)
    {
        return 0x17;
    }
}
