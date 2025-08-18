package net.zcraft.util;

public enum Gamemode
{
    Survival,
    Creative,
    Adventure,
    Spectator;

    public byte toByte()
    {
        return (byte) this.ordinal();
    }
}
