package net.zcraft.util;

import javax.crypto.Cipher;
import javax.crypto.SecretKey;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import java.security.PublicKey;
import java.security.interfaces.RSAPublicKey;
import java.util.Random;

public class CryptoUtils
{
    public static byte[] publicKeyToAsn1(RSAPublicKey key)
    {
        return key.getEncoded();
    }

    public static byte[] getRandomToken()
    {
        byte[] token = new byte[4];

        Random random = new Random();

        random.nextBytes(token);

        return token;
    }

    public static Cipher generateAes(byte[] key, int mode) throws Exception {
        SecretKey secretKey = new SecretKeySpec(key, "AES");
        IvParameterSpec iv = new IvParameterSpec(key); // IV = key clone

        Cipher cipher = Cipher.getInstance("AES/CFB8/NoPadding");
        cipher.init(mode, secretKey, iv);
        return cipher;
    }
}
