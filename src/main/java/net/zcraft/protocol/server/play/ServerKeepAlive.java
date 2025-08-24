package net.zcraft.protocol.server.play;

import lombok.AllArgsConstructor;
import lombok.Getter;
import net.zcraft.network.buffers.Types;
import net.zcraft.network.buffers.WriteBuffer;
import net.zcraft.protocol.IServerPacket;
import net.zcraft.protocol.PacketMode;

@Getter
@AllArgsConstructor
public class ServerKeepAlive implements IServerPacket
{
    private int id;
    @Override
    public void write(WriteBuffer buf)
    {
        buf.write(Types.VARINT, id);
    }

    @Override
    public int getPacketId(PacketMode mode)
    {
        return 0;
    }
}
