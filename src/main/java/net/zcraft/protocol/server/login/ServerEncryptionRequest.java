package net.zcraft.protocol.server.login;

import lombok.AllArgsConstructor;
import net.zcraft.network.buffers.Types;
import net.zcraft.network.buffers.WriteBuffer;
import net.zcraft.protocol.IServerPacket;
import net.zcraft.protocol.PacketMode;

import java.security.PublicKey;

@AllArgsConstructor
public class ServerEncryptionRequest implements IServerPacket
{
    private PublicKey publicKey;
    private byte[] verifyToken;

    @Override
    public void write(WriteBuffer buf)
    {
        buf.write(Types.STRING, "");
        byte[] key = publicKey.getEncoded();
        buf.write(Types.VARINT, key.length);
        buf.UNSAFE_write(key);
        buf.write(Types.VARINT, verifyToken.length);
        buf.UNSAFE_write(verifyToken);
    }

    @Override
    public int getPacketId(PacketMode mode)
    {
        return 0x01;
    }
}
