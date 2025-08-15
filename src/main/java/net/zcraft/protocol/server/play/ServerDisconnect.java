package net.zcraft.protocol.server.play;

import net.zcraft.chat.Component;
import net.zcraft.network.buffers.Types;
import net.zcraft.network.buffers.WriteBuffer;
import net.zcraft.protocol.IServerPacket;
import net.zcraft.protocol.PacketMode;

public class ServerDisconnect implements IServerPacket
{
    private Component component;

    public ServerDisconnect(Component component)
    {
        this.component = component;
    }

    public ServerDisconnect(String reason)
    {
        this(Component.text(reason));
    }

    @Override
    public void write(WriteBuffer buf)
    {
        buf.write(Types.STRING, Component.toJson(component));
    }

    @Override
    public int getPacketId(PacketMode mode)
    {
        return mode == PacketMode.Play ? 0x40 : 0x00;
    }
}
