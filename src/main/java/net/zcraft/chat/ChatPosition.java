package net.zcraft.chat;

public enum ChatPosition
{
    Chat,
    ActionBar,
    System;

    public byte toByte()
    {
        return (byte) this.ordinal();
    }
}
