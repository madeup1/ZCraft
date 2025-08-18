package net.zcraft.protocol.client.play;

import lombok.Getter;
import net.zcraft.network.ZCraftConnection;
import net.zcraft.network.buffers.ReadBuffer;
import net.zcraft.network.buffers.Types;
import net.zcraft.protocol.IClientPacket;
import net.zcraft.protocol.PacketMode;

@Getter
public class ClientSettings implements IClientPacket
{
    private String locale;
    private byte viewDistance;
    private byte chatMode;
    private boolean chatColors;
    private byte skinParts;
    @Override
    public void read(ReadBuffer buf)
    {
        this.locale = buf.read(Types.STRING);
        this.viewDistance = buf.read(Types.BYTE);
        this.chatMode = buf.read(Types.BYTE);
        this.chatColors = buf.read(Types.BOOLEAN);
        this.skinParts = buf.read(Types.BYTE);
    }

    @Override
    public boolean processInstantly()
    {
        return true;
    }

    @Override
    public void process(ZCraftConnection connection)
    {
        connection.getPlayer().setViewDistance(viewDistance);
        connection.set("locale", locale);
        connection.set("chatmode", chatMode);
        connection.set("chatcolors", chatColors);
        connection.set("skinparts", skinParts);
    }

    @Override
    public int getPacketId(PacketMode mode)
    {
        return 0x15;
    }
}
