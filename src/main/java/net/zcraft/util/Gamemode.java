package net.zcraft.util;

public enum Gamemode
{
    Survival,
    Creative,
    Adventure,
    Spectator;

    private byte toByte()
    {
        return (byte) this.ordinal();
    }
}
