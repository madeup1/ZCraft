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
        buf.write(Types.INT, player.getEntityId());
        buf.write(Types.BYTE, player.getGamemode().toByte());
        buf.write(Types.BYTE, player.getInstance().dimension().toByte());
        buf.write(Types.BYTE, player.getInstance().difficulty().toByte());
        buf.write(Types.BYTE, (byte) 80);
        buf.write(Types.STRING, player.getInstance().levelType().name());
        buf.write(Types.BOOLEAN, false);
    }

    @Override
    public int getPacketId(PacketMode mode)
    {
        return 0x01;
    }
}
