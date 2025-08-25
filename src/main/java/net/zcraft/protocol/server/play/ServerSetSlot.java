package net.zcraft.protocol.server.play;

import lombok.AllArgsConstructor;
import net.zcraft.items.ItemStack;
import net.zcraft.network.buffers.Types;
import net.zcraft.network.buffers.WriteBuffer;
import net.zcraft.protocol.IServerPacket;
import net.zcraft.protocol.PacketMode;
import org.tinylog.Logger;

@AllArgsConstructor
public class ServerSetSlot implements IServerPacket
{
    private ItemStack itemStack;
    private int windowId;
    private int slot;

    @Override
    public void write(WriteBuffer buf)
    {
        buf.write(Types.BYTE, (byte) windowId);
        buf.write(Types.SHORT, (short) slot);
        Logger.debug("ID is " + itemStack.getItemId());
        buf.write(Types.SHORT, (short) itemStack.getItemId());

        if (itemStack.getItemId() != -1)
        {
            buf.write(Types.BYTE, (byte) itemStack.getCount());
            buf.write(Types.SHORT, (short) itemStack.getMetadata());
            buf.write(Types.BYTE, (byte) 0);
        }

        byte[] data = buf.getBytes();

        StringBuilder sb = new StringBuilder();
        for (byte b : data) {
            sb.append(String.format("%02X ", b));
        }
        Logger.debug(sb.toString().trim());
    }

    @Override
    public int getPacketId(PacketMode mode)
    {
        return 0x2F;
    }
}
