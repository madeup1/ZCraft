package net.zcraft.network;

import lombok.Getter;
import lombok.Setter;
import lombok.SneakyThrows;
import net.zcraft.ZCraftServer;
import net.zcraft.init.ZCraftSettings;
import net.zcraft.network.buffers.ReadBuffer;
import net.zcraft.network.buffers.WriteBuffer;
import net.zcraft.protocol.IClientPacket;
import net.zcraft.protocol.IServerPacket;
import net.zcraft.protocol.PacketMode;
import net.zcraft.protocol.server.status.ServerStatusResponse;
import net.zcraft.util.ConnectionUtils;
import net.zcraft.util.Flags;
import org.tinylog.Logger;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;
import java.nio.charset.StandardCharsets;
import java.util.concurrent.Semaphore;

import static net.zcraft.network.buffers.Types.*;

@Getter
public class ZCraftConnection
{
    private final Socket socket;
    private final InputStream inputStream;
    private final OutputStream outputStream;
    @Setter private volatile int compressionThreshold = -1;
    @Setter private volatile PacketMode packetMode;

    private static final WriteBuffer REUSE_BUFFER = new WriteBuffer();

    @SneakyThrows
    public ZCraftConnection(Socket socket)
    {
        this.socket = socket;
        this.packetMode = PacketMode.Handshake;

        System.out.println("PacketMode: " + packetMode);

        try
        {
            this.inputStream = socket.getInputStream();
            this.outputStream = socket.getOutputStream();

            this.socket.setTcpNoDelay(true);
            this.socket.setReceiveBufferSize(4096);
            this.socket.setSendBufferSize(4096);
            this.socket.setSoTimeout(0);
        }
        catch (Exception e)
        {
            throw new RuntimeException(e);
        }

        ZCraftServer.getExecutor().runVirtual(() -> {
            try
            {
                this.listen();
            }
            catch (Exception e)
            {
                Logger.info("Disconnecting socket");

                ZCraftServer.getConnections().remove(this);
                try
                {
                    socket.close();
                }
                catch (IOException ex)
                {
                    throw new RuntimeException(ex);
                }

                e.printStackTrace();
            }
        });
    }

    private void sendPacket(IServerPacket packet, WriteBuffer buffer)
    {
        buffer.write(VARINT, packet.getPacketId(packetMode));
        packet.write(buffer);

        byte[] data = buffer.getBytes();

        buffer.clear();

        int dataLen = data.length;

        buffer.write(VARINT, dataLen);
        buffer.UNSAFE_write(data);

        try
        {
            this.outputStream.write(buffer.getBytes());
        }
        catch (Exception e)
        {
            // disconnect
            System.out.println("Failed to send packet");
        }
    }

    public void sendPacket(IServerPacket packet)
    {
        this.sendPacket(packet, REUSE_BUFFER);
    }

    public void sendPacketVirtual(IServerPacket packet)
    {
        ZCraftServer.getExecutor().runVirtual(() -> {
            this.sendPacket(packet, new WriteBuffer());
        });
    }

    private void listen() throws Exception
    {
        while (!socket.isClosed())
        {
            //if (socket.isClosed() || !socket.isConnected())
            //    break;

            int packetLen = ConnectionUtils.readVarInt(inputStream);

            if (packetLen == -1)
            {
                Logger.debug("No more data from client");

                throw new Exception("Socket has no more data");
            }

            int len = packetLen;
            boolean compressed = false;
            if (compressionThreshold != -1)
            {
                int dataLength = ConnectionUtils.readVarInt(inputStream);

                if (dataLength == 0)
                {
                    // Packet is uncompressed
                    len = packetLen - ConnectionUtils.getVarIntLength(dataLength);
                }
                else
                {
                        // Packet is compressed
                    len = dataLength;
                    compressed = true;
                }
            }
            // decrypt here
            // compression here

            byte[] data = inputStream.readNBytes(len);
            ReadBuffer buffer = new ReadBuffer(data);
            int packId = buffer.read(VARINT);

            Logger.debug("Found Packet ID: " + packId);
            Logger.debug("Packet Mode: " + packetMode);
            // match packet and read data
            IClientPacket packet = ZCraftServer
                    .getPacketManager()
                    .find(packetMode, packId);

            if (packet == null)
            {
                throw new IllegalArgumentException("Invalid packet. (" + packId + ")");
            }

            packet.read(buffer);

            if (packet.processInstantly())
            {
                packet.process(this);
            }
            else
            {

            }
        }
    }
}
