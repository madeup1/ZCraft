package net.zcraft.protocol.client.status;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import net.zcraft.network.ZCraftConnection;
import net.zcraft.network.buffers.ReadBuffer;
import net.zcraft.network.buffers.Types;
import net.zcraft.protocol.IClientPacket;
import net.zcraft.protocol.PacketMode;
import net.zcraft.protocol.server.status.ServerStatusPong;
import org.tinylog.Logger;

public class ClientStatusPing implements IClientPacket
{
    private long time;

    @Override
    public void read(ReadBuffer buf)
    {
        this.time = buf.read(Types.LONG);
    }

    @Override
    public boolean processInstantly()
    {
        return true;
    }

    @Override
    public void process(ZCraftConnection connection)
    {
        connection.sendPacket(new ServerStatusPong(time));
    }

    @Override
    public int getPacketId(PacketMode mode)
    {
        return 0x01;
    }
}
