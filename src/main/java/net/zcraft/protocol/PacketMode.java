package net.zcraft.protocol;

import lombok.Getter;

@Getter
public enum PacketMode
{
    Handshake(1),
    Status(2),
    Login(2),
    Play(26);

    public static final int C2S_PACKET_COUNT = 1 + 2 + 2 + 26;

    private int packetCount;

    PacketMode(int count)
    {
        this.packetCount = count;
    }
}
