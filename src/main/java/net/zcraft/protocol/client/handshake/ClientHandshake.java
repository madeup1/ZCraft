package net.zcraft.protocol.client.handshake;

import net.zcraft.chat.Component;
import net.zcraft.network.ZCraftConnection;
import net.zcraft.network.buffers.ReadBuffer;
import net.zcraft.protocol.IClientPacket;
import net.zcraft.protocol.PacketMode;
import net.zcraft.protocol.server.play.ServerDisconnect;
import org.tinylog.Logger;

import static net.zcraft.network.buffers.Types.*;

public class ClientHandshake implements IClientPacket
{
    public int protocolVersion;
    public String serverAddress;
    public short serverPort;
    public PacketMode state;

    @Override
    public void read(ReadBuffer b)
    {
        this.protocolVersion = b.read(VARINT);
        this.serverAddress = b.read(STRING);
        this.serverPort = b.read(SHORT);
        this.state = b.read(VARINT) == 1 ? PacketMode.Status : PacketMode.Login;
    }

    @Override
    public boolean processInstantly()
    {
        return true;
    }

    @Override
    public void process(ZCraftConnection connection)
    {
        connection.setPacketMode(this.state);
    }

    @Override
    public int getPacketId(PacketMode mode)
    {
        return 0x00;
    }
}
