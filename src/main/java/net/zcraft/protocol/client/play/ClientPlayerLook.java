package net.zcraft.protocol.client.play;

import lombok.Getter;
import net.zcraft.network.ZCraftConnection;
import net.zcraft.network.buffers.ReadBuffer;
import net.zcraft.network.buffers.Types;
import net.zcraft.protocol.IClientPacket;
import net.zcraft.protocol.PacketMode;

@Getter
public class ClientPlayerLook implements IClientPacket
{
    private float yaw;
    private float pitch;
    private boolean onGround;
    @Override
    public void read(ReadBuffer buf)
    {
        this.yaw = buf.read(Types.FLOAT);
        this.pitch = buf.read(Types.FLOAT);
        this.onGround = buf.read(Types.BOOLEAN);
    }

    @Override
    public boolean processInstantly()
    {
        return true;
    }

    @Override
    public void process(ZCraftConnection connection)
    {

    }

    @Override
    public int getPacketId(PacketMode mode)
    {
        return 0x05;
    }
}
