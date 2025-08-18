package net.zcraft.util;

public enum Difficulty
{
    Peaceful,
    Easy,
    Normal,
    Hard;

    public byte toByte()
    {
        return (byte) this.ordinal();
    }
}
