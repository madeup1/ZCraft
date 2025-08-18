package net.zcraft.util;

import java.math.BigInteger;
import java.security.MessageDigest;
import java.security.PublicKey;

public class AuthUtils
{
    public static String getServerHash(String serverId, byte[] sharedSecret, PublicKey key)
    {
        try {
            MessageDigest sha1 = MessageDigest.getInstance("SHA-1");

            // Update with serverId
            sha1.update(serverId.getBytes("ISO_8859_1"));

            // Update with sharedSecret
            sha1.update(sharedSecret);

            // Update with publicKey (encoded form)
            sha1.update(key.getEncoded());

            byte[] digest = sha1.digest();

            // Convert to signed hexadecimal string
            return new BigInteger(digest).toString(16);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
}
