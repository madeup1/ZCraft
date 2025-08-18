package net.zcraft.protocol.server.play;

import lombok.AllArgsConstructor;
import net.zcraft.entities.EntityPlayer;
import net.zcraft.network.buffers.Types;
import net.zcraft.network.buffers.WriteBuffer;
import net.zcraft.protocol.IServerPacket;
import net.zcraft.protocol.PacketMode;

@AllArgsConstructor
public class ServerJoinGame implements IServerPacket
{
    private EntityPlayer player;
    @Override
    public void write(WriteBuffer buf)
    {
        buf.write(Types.VARINT, player.getEntityId());

    }

    @Override
    public int getPacketId(PacketMode mode)
    {
        return 0x01;
    }
}
