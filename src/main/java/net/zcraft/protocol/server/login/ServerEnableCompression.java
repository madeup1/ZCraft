package net.zcraft.protocol.server.login;

import lombok.AllArgsConstructor;
import net.zcraft.network.buffers.Types;
import net.zcraft.network.buffers.WriteBuffer;
import net.zcraft.protocol.IServerPacket;
import net.zcraft.protocol.PacketMode;

@AllArgsConstructor
public class ServerEnableCompression implements IServerPacket
{
    private int threshold;


    @Override
    public void write(WriteBuffer buf)
    {
        buf.write(Types.VARINT, threshold);
    }

    @Override
    public int getPacketId(PacketMode mode)
    {
        return 0x03;
    }
}
