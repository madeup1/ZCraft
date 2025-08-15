package net.zcraft.protocol;

import lombok.Getter;
import net.zcraft.ZCraftServer;
import net.zcraft.network.UnprocessedPacket;
import net.zcraft.protocol.client.handshake.ClientHandshake;
import net.zcraft.protocol.client.status.ClientStatusPing;
import net.zcraft.protocol.client.status.ClientStatusRequest;

import java.util.ArrayDeque;
import java.util.Iterator;
import java.util.concurrent.Semaphore;

import static net.zcraft.protocol.PacketMode.*;

public class PacketManager
{
    private final IPacketFactory[] packets = new IPacketFactory[PacketMode.C2S_PACKET_COUNT];
    private Semaphore semaphore = new Semaphore(0);
    private ArrayDeque<UnprocessedPacket> unprocessedPackets = new ArrayDeque<>();

    public PacketManager()
    {
        register(Handshake, 0, ClientHandshake::new);

        register(Status, 0, ClientStatusRequest::new);
        register(Status, 1, ClientStatusPing::new);

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
