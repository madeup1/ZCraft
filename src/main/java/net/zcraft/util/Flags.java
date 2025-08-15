package net.zcraft.util;

import java.nio.ByteOrder;

public class Flags
{
    public static boolean ENDIAN_CONVERSION = ByteOrder.nativeOrder() != ByteOrder.LITTLE_ENDIAN;
}
