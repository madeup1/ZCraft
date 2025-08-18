package net.zcraft.util;

import lombok.AllArgsConstructor;
import lombok.Getter;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;

@Getter
@AllArgsConstructor
public class CipherPair
{
    public CipherPair(byte[] secret)
    {
        try
        {
            this.cipherIn = CryptoUtils.generateAes(secret, Cipher.DECRYPT_MODE);
            this.cipherOut = CryptoUtils.generateAes(secret, Cipher.ENCRYPT_MODE);
        }
        catch (Exception e)
        {
            throw new RuntimeException(e);
        }
    }

    private Cipher cipherIn;
    private Cipher cipherOut;

    public byte[] encrypt(byte[] data)
    {
        return cipherIn.update(data);
    }

    public byte[] decrypt(byte[] data)
    {
        return cipherOut.update(data);
    }
}
