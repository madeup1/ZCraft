package net.zcraft.crypto;

import lombok.Getter;
import net.zcraft.util.CipherPair;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.PrivateKey;
import java.security.PublicKey;

public class Encryption
{
    @Getter
    private static KeyPair keyPair;
    private static Cipher cipher;

    public static void init()
    {
        try
        {
            // Create a keypair generator for RSA
            KeyPairGenerator keyGen = KeyPairGenerator.getInstance("RSA");
            keyGen.initialize(1024); // Minecraft 1.8 uses 1024-bit keys

            // Generate the pair
            keyPair = keyGen.generateKeyPair();

            cipher = Cipher.getInstance("RSA/ECB/PKCS1Padding");
            cipher.init(Cipher.DECRYPT_MODE, getPrivateKey());
        }
        catch (Exception e)
        {
            throw new RuntimeException(e);
        }
    }

    public static byte[] decrypt(byte[] data)
    {
        try
        {
            return cipher.doFinal(data);
        }
        catch (IllegalBlockSizeException | BadPaddingException e)
        {
            throw new RuntimeException(e);
        }
    }

    public static PublicKey getPublicKey()
    {
        return keyPair.getPublic();
    }

    public static PrivateKey getPrivateKey()
    {
        return keyPair.getPrivate();
    }
}
