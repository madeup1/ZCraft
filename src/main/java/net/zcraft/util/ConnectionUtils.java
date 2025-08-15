package net.zcraft.util;

import java.io.EOFException;
import java.io.InputStream;

public class ConnectionUtils
{
    public static int getVarIntLength(int value)
    {
        int len = 1;
        while ((value & -128) != 0)
        {
            value >>= 7;
            len++;
        }

        return len;
    }

    public static int readVarInt(InputStream stream) throws Exception
    {
        int value = 0;
        int count = 0;

        while (true) {
            int b = stream.read();
            if (b == -1) return -1;

            value |= (b & 0x7F) << (7 * count);
            if ((b & 0x80) == 0) break;

            count++;
            if (count >= 5) throw new RuntimeException("Varint too long");
        }

        return value;

    }
}
