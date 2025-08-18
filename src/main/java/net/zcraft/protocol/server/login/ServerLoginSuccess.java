package net.zcraft.protocol.server.login;

import lombok.AllArgsConstructor;
import net.zcraft.network.buffers.Types;
import net.zcraft.network.buffers.WriteBuffer;
import net.zcraft.protocol.IServerPacket;
import net.zcraft.protocol.PacketMode;

@AllArgsConstructor
public class ServerLoginSuccess implements IServerPacket
{
    private String username;
    private String uuid;
    @Override
    public void write(WriteBuffer buf)
    {
        buf.write(Types.STRING, uuid);
        buf.write(Types.STRING, username);
    }

    @Override
    public int getPacketId(PacketMode mode)
    {
        return 0x02;
    }
}
