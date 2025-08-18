package net.zcraft.util;

public enum Dimension
{
    Nether,
    Overworld,
    End;

    public byte toByte()
    {
        return (byte) ((byte) this.ordinal() - 1);
    }
}
