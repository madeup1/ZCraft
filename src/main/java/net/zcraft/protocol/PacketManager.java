package net.zcraft.protocol;

import lombok.Getter;
import net.zcraft.ZCraftServer;
import net.zcraft.network.UnprocessedPacket;
import net.zcraft.protocol.client.handshake.ClientHandshake;
import net.zcraft.protocol.client.login.ClientEncryptionResponse;
import net.zcraft.protocol.client.login.ClientLoginStart;
import net.zcraft.protocol.client.play.*;
import net.zcraft.protocol.client.status.ClientStatusPing;
import net.zcraft.protocol.client.status.ClientStatusRequest;

import java.util.ArrayDeque;
import java.util.Iterator;
import java.util.concurrent.Semaphore;

import static net.zcraft.protocol.PacketMode.*;

public class PacketManager
{
    private final IPacketFactory[] packets = new IPacketFactory[PacketMode.C2S_PACKET_COUNT];
    private final Semaphore semaphore = new Semaphore(0);
    private final ArrayDeque<UnprocessedPacket> unprocessedPackets = new ArrayDeque<>();

    public PacketManager()
    {
        register(Handshake, 0, ClientHandshake::new);

        register(Status, 0, ClientStatusRequest::new);
        register(Status, 1, ClientStatusPing::new);

        register(Login, 0, ClientLoginStart::new);
        register(Login, 1, ClientEncryptionResponse::new);

        register(Play, 0x00, ClientKeepAlive::new);
        register(Play, 0x01, ClientMessage::new);
        register(Play, 0x02, ClientUseEntity::new);
        register(Play, 0x03, ClientPlayer::new);
        register(Play, 0x04, ClientPlayerPos::new);
        register(Play, 0x05, ClientPlayerLook::new);
        register(Play, 0x06, ClientPlayerPosLook::new);
        register(Play, 0x15, ClientSettings::new);
        register(Play, 0x17, ClientPluginMessage::new);

        if (ZCraftServer.getSettings().isPacketThread())
        {
            new Thread(() -> {
                try
                {
                    while (true)
                    {
                        semaphore.acquire();

                        processAll();
                    }
                }
                catch (Exception e)
                {
                    throw new RuntimeException(e);
                }
            }).start();
        }
    }

    public void queue(UnprocessedPacket packet)
    {
        this.unprocessedPackets.add(packet);
    }

    public void processAll()
    {
        UnprocessedPacket packet;
        while ((packet = unprocessedPackets.poll()) != null)
        {
            packet.process();
        }
    }

    public void process()
    {
        if (ZCraftServer.getSettings().isPacketThread())
        {
            semaphore.release();
        }
        else
        {
            processAll();
        }
    }

    public IClientPacket find(PacketMode mode, int id)
    {
        if (id >= packets.length)
            return null;
        IPacketFactory factory = packets[this.getOffset(mode) + id];

        if (factory == null)
            return null;
        return factory.make();
    }

    private void register(PacketMode mode, int id, IPacketFactory packet)
    {
        packets[this.getOffset(mode) + id] = packet;
    }

    private int getOffset(PacketMode mode)
    {
        return switch (mode)
        {
            case Handshake -> 0;
            case Status -> 1;
            case Login -> 3;
            case Play -> 5;
        };
    }
}
