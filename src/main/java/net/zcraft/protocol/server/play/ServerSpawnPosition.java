package net.zcraft.protocol.server.play;

import lombok.AllArgsConstructor;
import net.zcraft.network.buffers.Types;
import net.zcraft.network.buffers.WriteBuffer;
import net.zcraft.position.BlockPos;
import net.zcraft.position.Vec3;
import net.zcraft.protocol.IServerPacket;
import net.zcraft.protocol.PacketMode;

@AllArgsConstructor
public class ServerSpawnPosition implements IServerPacket
{
    private BlockPos position;

    @Override
    public void write(WriteBuffer buf)
    {
        buf.write(Types.BLOCKPOS, position);
    }

    @Override
    public int getPacketId(PacketMode mode)
    {
        return 0x05;
    }
}
