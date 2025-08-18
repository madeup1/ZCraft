package net.zcraft.protocol.client.play;

import net.zcraft.network.ZCraftConnection;
import net.zcraft.network.buffers.ReadBuffer;
import net.zcraft.network.buffers.Types;
import net.zcraft.position.Vec3;
import net.zcraft.protocol.IClientPacket;
import net.zcraft.protocol.PacketMode;

public class ClientUseEntity implements IClientPacket
{
    private int target;
    private int type;
    private Vec3 position;
    @Override
    public void read(ReadBuffer buf)
    {
        this.target = buf.read(Types.VARINT);
        this.type = buf.read(Types.VARINT);

        if (this.type == 2)
        {
            this.position = buf.read(Types.VEC3);
        }
    }

    @Override
    public boolean processInstantly()
    {
        return false;
    }

    @Override
    public void process(ZCraftConnection connection)
    {

    }

    @Override
    public int getPacketId(PacketMode mode)
    {
        return 0x02;
    }
}
