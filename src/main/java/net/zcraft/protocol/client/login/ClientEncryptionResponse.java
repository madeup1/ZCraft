package net.zcraft.protocol.client.login;

import net.zcraft.crypto.Encryption;
import net.zcraft.entities.EntityPlayer;
import net.zcraft.network.ZCraftConnection;
import net.zcraft.network.buffers.ReadBuffer;
import net.zcraft.network.buffers.Types;
import net.zcraft.protocol.IClientPacket;
import net.zcraft.protocol.PacketMode;
import net.zcraft.protocol.server.login.ServerLoginSuccess;
import net.zcraft.protocol.server.play.ServerDisconnect;
import net.zcraft.protocol.server.play.ServerJoinGame;
import net.zcraft.util.CipherPair;
import net.zcraft.util.ConnectionUtils;
import net.zcraft.util.CryptoUtils;
import org.tinylog.Logger;

import javax.crypto.Cipher;
import java.util.Arrays;

public class ClientEncryptionResponse implements IClientPacket
{
    private CipherPair cipherPair;
    private byte[] verifyToken;
    @Override
    public void read(ReadBuffer buf)
    {
        byte[] shared = buf.UNSAFE_read(buf.read(Types.VARINT));
        verifyToken = buf.UNSAFE_read(buf.read(Types.VARINT));

        shared = Encryption.decrypt(shared);
        verifyToken = Encryption.decrypt(verifyToken);

        cipherPair = new CipherPair(shared);
    }

    @Override
    public boolean processInstantly()
    {
        return true;
    }

    @Override
    public void process(ZCraftConnection connection)
    {
        Logger.debug("Got Encryption response");

        if (!Arrays.equals(verifyToken, connection.get("verifyToken")))
        {
            Logger.debug("Invalid verify token!");

            return;
        }
        connection.setCipher(cipherPair);

        EntityPlayer player = new EntityPlayer(connection.get("name"), connection);

        player.setUuid(ConnectionUtils.getUuid(player.getName()));

        if (!player.authenticate())
        {
            connection.sendPacket(new ServerDisconnect("Invalid session! Maybe restart your game?"));

            return;
        }

        connection.sendPacket(new ServerLoginSuccess(player.getName(), player.getUuid().toString()));
        Logger.debug("Name: {}, UUID: {}", player.getName(), player.getUuid());
        connection.setPacketMode(PacketMode.Play);

        connection.sendPacket(new ServerJoinGame(player));
    }

    @Override
    public int getPacketId(PacketMode mode)
    {
        return 0x01;
    }
}
