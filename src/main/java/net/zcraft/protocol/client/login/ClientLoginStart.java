package net.zcraft.protocol.client.login;

import net.zcraft.crypto.Encryption;
import net.zcraft.entities.EntityPlayer;
import net.zcraft.network.ZCraftConnection;
import net.zcraft.network.buffers.ReadBuffer;
import net.zcraft.network.buffers.Types;
import net.zcraft.protocol.IClientPacket;
import net.zcraft.protocol.PacketMode;
import net.zcraft.protocol.server.login.ServerEncryptionRequest;
import net.zcraft.util.CryptoUtils;
import org.tinylog.Logger;

import java.util.Arrays;

public class ClientLoginStart implements IClientPacket
{
    public String name;
    @Override
    public void read(ReadBuffer buf)
    {
        this.name = buf.read(Types.STRING);
    }

    @Override
    public boolean processInstantly()
    {
        return true;
    }

    @Override
    public void process(ZCraftConnection connection)
    {
        Logger.debug("[+] {}", name);

        // i dont like offline mode i wont even add support atm
        String username = name.chars()   // IntStream of characters
                .filter(c -> Character.isLetter(c) || Character.isDigit(c) ||
                        (c >= 33 && c <= 47) || (c >= 58 && c <= 64) ||
                        (c >= 91 && c <= 96) || (c >= 123 && c <= 126)) // basic punctuation
                .collect(StringBuilder::new, StringBuilder::appendCodePoint, StringBuilder::append)
                .toString();

        connection.set("name", username);


        connection.setPacketMode(PacketMode.Login);

        byte[] token = CryptoUtils.getRandomToken();
        connection.set("verifyToken", token);
        connection.sendPacket(new ServerEncryptionRequest(Encryption.getPublicKey(), token));
    }

    @Override
    public int getPacketId(PacketMode mode)
    {
        return 0;
    }
}
