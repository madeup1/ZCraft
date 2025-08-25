package net.zcraft.protocol.server.play;

import lombok.AllArgsConstructor;
import lombok.Getter;
import net.zcraft.chat.ChatPosition;
import net.zcraft.chat.Component;
import net.zcraft.network.buffers.Types;
import net.zcraft.network.buffers.WriteBuffer;
import net.zcraft.protocol.IServerPacket;
import net.zcraft.protocol.PacketMode;

@AllArgsConstructor
@Getter
public class ServerChatMessage implements IServerPacket
{
    private Component component;
    private ChatPosition position;
    @Override
    public void write(WriteBuffer buf)
    {
        buf.write(Types.STRING, Component.toJson(component));
        buf.write(Types.BYTE, position.toByte());
    }

    @Override
    public int getPacketId(PacketMode mode)
    {
        return 0x02;
    }
}
