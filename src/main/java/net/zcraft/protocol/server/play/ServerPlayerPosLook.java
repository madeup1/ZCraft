package net.zcraft.protocol.server.play;

import lombok.AllArgsConstructor;
import lombok.Getter;
import net.zcraft.network.buffers.Types;
import net.zcraft.network.buffers.WriteBuffer;
import net.zcraft.protocol.IServerPacket;
import net.zcraft.protocol.PacketMode;

@AllArgsConstructor
@Getter
public class ServerPlayerPosLook implements IServerPacket
{
    private double x;
    private double y;
    private double z;
    private float yaw;
    private float pitch;
    private byte flags;

    @Override
    public void write(WriteBuffer buf)
    {
        buf.write(Types.DOUBLE, x, y, z);
        buf.write(Types.FLOAT, yaw, pitch);
        buf.write(Types.BYTE, flags);
    }

    @Override
    public int getPacketId(PacketMode mode)
    {
        return 0x08;
    }
}
